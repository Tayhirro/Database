package com.hmdp.service.strategy.recall.impl.blog;

/*
 * 现实业务背景：用户打开为你推荐时，既需要常互动作者的内容，也需要圈外发现内容，避免推荐完全等同关注流。
 * 实际触发：for_you 模式调用本召回通道，根据点赞作者交互生成熟悉作者和发现候选，并把交互信号交给排序。
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.config.RecommendProperties;
import com.hmdp.dto.AuthorInteractionDTO;
import com.hmdp.entity.Blog;
import com.hmdp.mapper.BlogLikeMapper;
import com.hmdp.mapper.BlogMapper;
import com.hmdp.service.strategy.recall.RecallContext;
import com.hmdp.service.strategy.recall.RecallStrategy;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 为 For You 推荐先挑出一批可能感兴趣的博客 ID，后续排序器再决定最终顺序。
 * 这个“先挑候选”的步骤称为召回。
 *     1. 从点赞历史判断作者偏好：用户给某位作者的博客点过越多次赞，
 *     就优先取一部分该作者的新内容；排序阶段只需要次数汇总，不需要每条历史点赞明细。
 *     2. 同时加入陌生作者：候选结果一部分来自偏好作者，另一部分来自用户没怎么互动过的作者，
 *     防止推荐页永远只有熟悉账号，让用户仍能发现新内容。
 *     3. 这是人工规则，不是机器学习概率：点赞次数和热度只是可解释的排序信号，
 *     分数表示规则下的相对先后，不能解释成“用户有 80% 概率喜欢”。
 *
 * 具体怎么分配名额（以 {@link RecallContext}（召回上下文：用户、时间边界、候选上限、共享信号）
 * 的 limit = 200 为例）：
 *     4. 先查偏好作者：调 {@code blogLikeMapper.selectAuthorInteractions} 按作者聚合当前用户的
 *     点赞记录，得到"作者 ID -> 点赞次数"列表，同时把它写进 ctx.extra 的 "authorInteractions"
 *     键，排序阶段（BlogFeedService 算作者亲和度）直接复用，不再查库。
 *     5. 熟悉作者路：名额 personalizedLimit = min(limit/2, 偏好作者数 * 10)。limit=200 时即
 *     min(100, 偏好作者数*10)：3 个偏好作者取 30 条，偏好作者达到 10 个及以上就取满 100 条；
 *     条件是 user_id IN (偏好作者) 且排除自己，按 liked 降序、create_time 降序、id 降序取前 N 条 ID。
 *     6. 发现路：用剩余名额（limit - 已取条数，如上例 200 - 30 = 170）补齐，条件是
 *     user_id NOT IN (偏好作者) 且排除自己，排序方式相同，保证推荐不只有熟人内容。
 *     7. 用户没有任何点赞历史时，偏好作者列表为空、熟悉路跳过，全部名额走发现路，
 *     退化为"全站热门+最新"。
 *     8. 两路结果放进 LinkedHashSet 去重合并；同时叠加与 FollowFeedRecall 相同的翻页边界：
 *     create_time 早于 maxTime，或等于 maxTime 且 id 小于 extra 里的 lastId。
 *     9. 通道注册名为 "for-you"。
 *
 */
@Component
public class ForYouRecall implements RecallStrategy {

    public static final String AUTHOR_INTERACTIONS = "authorInteractions";

    private final BlogMapper blogMapper;
    private final BlogLikeMapper blogLikeMapper;
    private final RecommendProperties recommendProperties;

    public ForYouRecall(BlogMapper blogMapper,
                        BlogLikeMapper blogLikeMapper,
                        RecommendProperties recommendProperties) {
        this.blogMapper = blogMapper;
        this.blogLikeMapper = blogLikeMapper;
        this.recommendProperties = recommendProperties;
    }

    @Override
    public List<Long> recall(RecallContext ctx) {
        List<AuthorInteractionDTO> interactions = blogLikeMapper.selectAuthorInteractions(ctx.getUserId());
        if (interactions == null) {
            interactions = Collections.emptyList();
        }
        if (ctx.getExtra() != null) {
            ctx.getExtra().put(AUTHOR_INTERACTIONS, interactions);
        }

        List<Long> preferredAuthors = interactions.stream()
                .map(AuthorInteractionDTO::getAuthorId)
                .collect(Collectors.toList());
        // 多通道改造后本通道只拿候选池的一部分名额（RecommendProperties 的 quota.for-you，默认 80），
        // 剩余名额让给 interest/cf/hot 通道；ctx.getLimit() 仍是整池大小（200），不能再用它当本通道上限。
        int poolQuota = recommendProperties.getQuotaForYou();
        int personalizedLimit = Math.min(poolQuota / 2, preferredAuthors.size() * 10);
        LinkedHashSet<Long> result = new LinkedHashSet<>();

        // 两路召回分别保证“熟悉作者相关性”和“圈外内容发现”，再去重合并。
        if (!preferredAuthors.isEmpty() && personalizedLimit > 0) {
            result.addAll(queryCandidates(ctx, preferredAuthors, false, personalizedLimit));
        }
        result.addAll(queryCandidates(ctx, preferredAuthors, true, poolQuota - result.size()));
        return new ArrayList<>(result);
    }

    private List<Long> queryCandidates(
            RecallContext ctx,
            List<Long> preferredAuthors,
            boolean discovery,
            int limit
    ) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<Blog>()
                .select(Blog::getId)
                .ne(Blog::getUserId, ctx.getUserId());
        if (!preferredAuthors.isEmpty()) {
            if (discovery) {
                wrapper.notIn(Blog::getUserId, preferredAuthors);
            } else {
                wrapper.in(Blog::getUserId, preferredAuthors);
            }
        }
        applyBoundary(wrapper, ctx);
        wrapper.orderByDesc(Blog::getLiked, Blog::getCreateTime, Blog::getId)
                .last("LIMIT " + limit);
        return blogMapper.selectList(wrapper).stream()
                .map(Blog::getId)
                .collect(Collectors.toList());
    }

    private void applyBoundary(LambdaQueryWrapper<Blog> wrapper, RecallContext ctx) {
        if (ctx.getMaxTime() == null || ctx.getMaxTime() <= 0) {
            return;
        }
        LocalDateTime time = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(ctx.getMaxTime()), ZoneOffset.UTC);
        Long lastId = getLastId(ctx.getExtra());
        wrapper.and(query -> query.lt(Blog::getCreateTime, time)
                .or(lastId != null, nested -> nested.eq(Blog::getCreateTime, time)
                        .lt(Blog::getId, lastId)));
    }

    private Long getLastId(Map<String, Object> extra) {
        if (extra == null || !(extra.get("lastId") instanceof Number)) {
            return null;
        }
        return ((Number) extra.get("lastId")).longValue();
    }

    @Override
    public String getStrategyName() {
        return "for-you";
    }
}
