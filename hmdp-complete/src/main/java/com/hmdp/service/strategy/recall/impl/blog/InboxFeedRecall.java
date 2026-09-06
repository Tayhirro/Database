package com.hmdp.service.strategy.recall.impl.blog;

/*
 * 现实业务背景：关注流（following 模式）除了拉模式按关注作者实时查博客（follow 通道），
 * 还有一条推模式通道——发布博客时，作者的新内容已被 FeedPushService 预写进每个粉丝的
 * 收件箱（tb_feed_inbox），读取时直接按收件人取数，不再按关注列表圈定作者范围；
 * 两条通道的结果在召回编排器里合并互补，收件箱缺失的内容由拉模式兜底。
 * 实际触发：BlogFeedService 的 following 模式召回通道列表是 ["inbox", "follow"]，
 * {@link com.hmdp.service.strategy.recall.RecallOrchestrator}（召回编排器：按通道名调用各策略
 * 并用 LinkedHashSet 合并候选）按本类注册名 "inbox" 找到这里。
 */

import com.hmdp.entity.Blog;
import com.hmdp.entity.Follow;
import com.hmdp.mapper.BlogMapper;
import com.hmdp.mapper.FeedInboxMapper;
import com.hmdp.service.IFollowService;
import com.hmdp.service.feedcache.FollowCacheService;
import com.hmdp.service.strategy.recall.RecallContext;
import com.hmdp.service.strategy.recall.RecallStrategy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 推模式收件箱召回通道（注册名精确为 "inbox"）。
 *
 * 数据来源：tb_feed_inbox，博客发布事务提交后由 FeedPushService 批量写入——
 * recipient_id=粉丝用户 ID、blog_id=新博客 ID、score=发布时间的 UTC epoch 毫秒；
 * 表上有 UNIQUE(recipient_id, blog_id) 和 (recipient_id, score) 索引，本通道的查询正好走后者。
 *
 * 本通道一次召回最多 2 条 SQL：
 * 1. FeedInboxMapper.selectInboxBlogIds（收件箱取 blog_id）：
 *    SELECT blog_id FROM tb_feed_inbox WHERE recipient_id = 当前用户，
 *    按顺序叠加翻页边界后 ORDER BY score DESC, blog_id DESC LIMIT 200
 *    （limit 来自 RecallContext，BlogFeedService 固定传 200）。
 * 2. BlogMapper.selectBatchIds（按这批 blog_id 批量查博客拿作者 ID，用于取关过滤；
 *    已被删除的博客查不到，自然被丢弃）。
 *
 * 翻页边界 =（上一页最后一条的 score 时间戳毫秒，blog_id）：
 * - maxTime 为空（首页请求）：不加边界，直接取最新的 limit 条；
 * - 有 maxTime 无 lastId：只取 score 早于 maxTime 的记录；
 * - 两者都有：取 score 早于 maxTime，或 score 等于 maxTime 且 blog_id 小于 lastId。
 * 排序固定 ORDER BY score DESC, blog_id DESC：score 只精确到毫秒，同一毫秒内发布的多篇博客
 * 靠 blog_id 分出先后，保证翻页不重复、不漏内容。
 * score 存的本来就是 UTC epoch 毫秒、RecallContext.maxTime 也是同一口径的毫秒值，
 * 所以直接按数值比较即可，无需像 follow 通道那样把毫秒转成 UTC LocalDateTime 再比 DATETIME 列。
 *
 * 已取关过滤：收件箱是发布时刻的快照，作者可能已被用户取关。召回后先走
 * {@link FollowCacheService}（关注列表的 Caffeine 本地缓存，5 分钟过期、关注变更时主动失效）
 * 拿当前关注列表，把作者不在列表里的候选剔除，保证 following 模式只出现"现在仍关注"的作者内容。
 * 边界说明：过滤发生在 LIMIT 之后，若本页候选里有已取关作者的博客，本页返回条数会少于 limit；
 * 下游的合并、排序和快照都按实际条数工作，不受影响。
 */
@Component
public class InboxFeedRecall implements RecallStrategy {

    @Resource
    private FeedInboxMapper feedInboxMapper;

    @Resource
    private BlogMapper blogMapper;

    @Resource
    private FollowCacheService followCacheService;

    @Resource
    private IFollowService followService;

    @Override
    public List<Long> recall(RecallContext ctx) {
        if (ctx.getUserId() == null) {
            return Collections.emptyList();
        }
        // 当前关注列表（与 follow 通道同源：tb_follow 里 user_id = 我的 follow_user_id 集合）。
        List<Long> followedIds = followCacheService.getFollowedIds(ctx.getUserId(), id -> {
            List<Follow> follows = followService.query().eq("user_id", id).list();
            return follows.stream().map(Follow::getFollowUserId).collect(Collectors.toList());
        });
        if (followedIds == null || followedIds.isEmpty()) {
            // 一个关注都没有：收件箱里的每条记录都来自"发布时关注过"的作者，
            // 现在全部视为已取关，无需查询直接返回空。
            return Collections.emptyList();
        }

        Long lastId = LastIdSupport.lastIdOf(ctx);
        List<Long> blogIds = feedInboxMapper.selectInboxBlogIds(
                ctx.getUserId(), ctx.getMaxTime(), lastId, ctx.getLimit());
        if (blogIds == null || blogIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量查一次博客拿作者（1 条 SQL），剔除"推送时关注、现在已取关"的作者候选。
        Set<Long> followedSet = new HashSet<>(followedIds);
        Map<Long, Blog> blogsById = blogMapper.selectBatchIds(blogIds).stream()
                .collect(Collectors.toMap(Blog::getId, blog -> blog, (left, right) -> left));
        List<Long> result = new ArrayList<>(blogIds.size());
        for (Long blogId : blogIds) {
            Blog blog = blogsById.get(blogId);
            if (blog != null && blog.getUserId() != null && followedSet.contains(blog.getUserId())) {
                result.add(blogId);
            }
        }
        return result;
    }

    @Override
    public String getStrategyName() {
        return "inbox";
    }
}
