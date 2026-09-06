package com.hmdp.service.cleanup;

/*
 * 现实业务背景：tb_blog.liked 是 tb_blog_like 关系表的冗余计数，正常由点赞/取消点赞在同一事务里增减，
 * 但历史故障、手工改库或旧版本缺陷可能让两边漂移（例如关系删了计数没减）。
 * 实际触发：Spring 定时调度自动触发，不属于任何用户请求链路；发现不一致就按关系表真实条数修正。
 *
 * 调度节奏：@Scheduled fixedDelay，默认 86400000 毫秒 = 每 24 小时跑一轮（本轮跑完再计时下一轮）。
 * 间隔可通过配置项 hmdp.reconcile.like-interval-ms 覆盖；application.yaml 未配置该键时取默认值 86400000。
 *
 * 每轮对账的三个步骤（SQL 实现在 BlogMapper）：
 * 1. selectRecentlyLikedBlogIds：SELECT DISTINCT blog_id FROM tb_blog_like
 *    WHERE create_time > NOW() - INTERVAL 48 HOUR LIMIT 500，即只对最近 48 小时有点赞活动的博客对账，
 *    最多 500 篇，避免一张老博客也没有的大表被整表扫描。
 * 2. 逐篇用 countLikesByBlogId（COUNT tb_blog_like）与 tb_blog.liked 比较；
 * 3. 不一致时用 resetLikedToCount（UPDATE tb_blog SET liked = 实际条数）修正，
 *    并逐条打 WARN 日志（发现 1 条记 1 条，同时记录计数侧与关系侧两个数值），例如：
 *    "博客点赞计数不一致已修正，blogId=100, tb_blog.liked=12, tb_blog_like 实际=11"。
 * 修正走覆盖式 UPDATE 而不是增减，保证结果恒等于关系表；日志同时承担告警和审计线索。
 *
 * 多实例互斥与失败观测：与 BlogImageCleanupJob 同风格——每轮开始用 RedisLockClient.tryLock
 * 抢锁 key "lock:cleanup:like-reconcile"，抢不到说明其他实例在跑，直接跳过本轮；
 * 任务体整体 try/catch，失败时累加类内连续失败计数并打 ERROR 日志（成功后清零），finally 里释放锁。
 * 注意锁 TTL 沿用 RedisLockClient 的 10 秒固定值，属于尽力而为的互斥；
 * 修正 SQL 本身是按真实条数覆盖的幂等 UPDATE，重复执行不会产生副作用。
 */

import com.hmdp.entity.Blog;
import com.hmdp.mapper.BlogMapper;
import com.hmdp.utils.RedisLockClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class LikeCountReconciliationJob {

    /** 本任务的 Redis 互斥锁 key：抢不到说明其他实例正在执行本轮对账。 */
    private static final String LOCK_KEY = "lock:cleanup:like-reconcile";

    private final BlogMapper blogMapper;
    private final RedisLockClient redisLockClient;

    /** 连续失败轮数：成功一轮清零；调度线程单线程执行，AtomicLong 只是防御性选择。 */
    private final AtomicLong consecutiveFailures = new AtomicLong();

    public LikeCountReconciliationJob(BlogMapper blogMapper, RedisLockClient redisLockClient) {
        this.blogMapper = blogMapper;
        this.redisLockClient = redisLockClient;
    }

    /**
     * 对账入口：默认 24 小时一轮（配置项 hmdp.reconcile.like-interval-ms 可覆盖）。
     * 每轮只处理最近 48 小时有点赞活动的前 500 篇博客；计数一致时不打日志，避免噪音。
     */
    @Scheduled(fixedDelayString = "${hmdp.reconcile.like-interval-ms:86400000}")
    public void reconcileLikeCounts() {
        if (!redisLockClient.tryLock(LOCK_KEY)) {
            log.info("其他实例正在执行点赞对账，本轮跳过");
            return;
        }
        try {
            List<Long> blogIds = blogMapper.selectRecentlyLikedBlogIds();
            if (blogIds == null || blogIds.isEmpty()) {
                consecutiveFailures.set(0);
                return;
            }
            int fixed = 0;
            for (Long blogId : blogIds) {
                Blog blog = blogMapper.selectById(blogId);
                if (blog == null) {
                    // 博客本体已删除（点赞关系行可能残留），没有 tb_blog.liked 可修正，跳过。
                    continue;
                }
                long recorded = blog.getLiked() == null ? 0L : blog.getLiked();
                long actual = blogMapper.countLikesByBlogId(blogId);
                if (recorded != actual) {
                    blogMapper.resetLikedToCount(blogId, actual);
                    log.warn("博客点赞计数不一致已修正，blogId={}, tb_blog.liked={}, tb_blog_like 实际={}",
                            blogId, recorded, actual);
                    fixed++;
                }
            }
            consecutiveFailures.set(0);
            if (fixed > 0) {
                log.info("点赞计数对账完成，检查 {} 篇，修正 {} 篇", blogIds.size(), fixed);
            }
        } catch (RuntimeException e) {
            long failures = consecutiveFailures.incrementAndGet();
            log.error("点赞计数对账失败，已连续失败 {} 轮", failures, e);
        } finally {
            redisLockClient.unlock(LOCK_KEY);
        }
    }
}
