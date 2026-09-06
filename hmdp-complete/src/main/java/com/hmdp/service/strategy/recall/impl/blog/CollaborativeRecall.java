package com.hmdp.service.strategy.recall.impl.blog;

/*
 * 现实业务背景：作者偏好召回只能覆盖"和你点赞习惯相似度最高"的场景，缺少"看过 X 的人也看 Y"这类
 * 群体行为信号；协同过滤用共同点赞关系把长尾内容带出来。
 * 实际触发：for_you 模式的多路召回调用本通道，以用户最近点赞为种子做 item-based 协同过滤。
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.config.RecommendProperties;
import com.hmdp.entity.BlogLike;
import com.hmdp.mapper.BlogLikeMapper;
import com.hmdp.mapper.RecommendQueryMapper;
import com.hmdp.service.strategy.recall.RecallContext;
import com.hmdp.service.strategy.recall.RecallStrategy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 协同过滤召回通道（注册名 "cf"，item-based CF）。
 *
 * 两步召回，共 2 条 SQL（{@link BlogLikeMapper}（点赞关系 Mapper）+ {@link RecommendQueryMapper}
 * （推荐召回专用查询 Mapper））：
 * 1. 取种子：当前用户最近点赞的 cf-recent-likes（默认 10）篇博客 ID；
 * 2. 共同点赞统计：一条 SQL 找"也点赞过这些种子的用户"还点赞过什么，
 *    按 COUNT(DISTINCT 共同点赞人数) DESC, blog_id DESC 取前 quota-cf（默认 20）条，
 *    并排除用户已点赞的博客。
 *
 * 具体例子：用户最近点赞了博客 101、102；查得用户 7、9、11 也点过 101，其中 7 和 9 还点过 300、
 * 11 还点过 301——候选按共同人数排序是 300（2 人）在前、301（1 人）在后。
 *
 * 边界条件：本通道不使用 maxTime/lastId 翻页边界。共同点赞人数是全局统计值，
 * 与发布时间边界语义不匹配；候选不足时由 hot 通道兜底，跳过是刻意设计。
 *
 * 冷启动：没有点赞历史就没有种子，返回空列表，候选池由其他通道填充。
 */
@Component
public class CollaborativeRecall implements RecallStrategy {

    private final BlogLikeMapper blogLikeMapper;
    private final RecommendQueryMapper recommendQueryMapper;
    private final RecommendProperties recommendProperties;

    public CollaborativeRecall(BlogLikeMapper blogLikeMapper,
                               RecommendQueryMapper recommendQueryMapper,
                               RecommendProperties recommendProperties) {
        this.blogLikeMapper = blogLikeMapper;
        this.recommendQueryMapper = recommendQueryMapper;
        this.recommendProperties = recommendProperties;
    }

    @Override
    public List<Long> recall(RecallContext ctx) {
        if (ctx.getUserId() == null) {
            return Collections.emptyList();
        }
        List<Long> seeds = blogLikeMapper.selectList(new LambdaQueryWrapper<BlogLike>()
                        .select(BlogLike::getBlogId)
                        .eq(BlogLike::getUserId, ctx.getUserId())
                        .orderByDesc(BlogLike::getId)
                        .last("LIMIT " + recommendProperties.getCfRecentLikes()))
                .stream()
                .map(BlogLike::getBlogId)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
        if (seeds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> ids = recommendQueryMapper.selectCollaborativeBlogIds(
                ctx.getUserId(), seeds, recommendProperties.getQuotaCf());
        return ids == null ? Collections.emptyList() : new ArrayList<>(ids);
    }

    @Override
    public String getStrategyName() {
        return "cf";
    }
}
