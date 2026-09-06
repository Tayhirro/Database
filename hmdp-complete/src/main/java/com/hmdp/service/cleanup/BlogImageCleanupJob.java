package com.hmdp.service.cleanup;

/*
 * 现实业务背景：用户上传图片后可能关闭页面，或博客删除后的文件第一次清理失败，服务器会留下待回收资产。
 * 实际触发：Spring 定时调度自动触发，不由用户直接调用；它清理过期 TEMP 并重试 DELETING 图片。
 *
 * 调度节奏：@Scheduled fixedDelay，默认 3600000 毫秒 = 每 1 小时跑一轮（本轮跑完再计时下一轮），
 * 可用配置项 hmdp.upload.cleanup-interval-ms 覆盖。每轮做两件事（实现在 BlogImageServiceImpl）：
 * 1. cleanupExpiredTemporaryImages：清理过期"临时图"（TEMP 状态，即已上传但从未被博客绑定）。
 *    保留 24 小时（配置 hmdp.upload.temp-retention-hours，默认 24），按上传时间从旧到新扫描，
 *    每轮最多处理 100 条（hmdp.upload.cleanup-batch-size）。例：图片 A 昨天上传、今天已过 24 小时
 *    仍未发布，本轮会被删掉文件和记录；刚上传 10 分钟的图片 B 不会被碰。
 *    文件删除失败会把状态还原回 TEMP 供重试；文件已删除但数据库记录删除失败则保持 DELETING，
 *    由下面第 2 步的补偿任务继续重试（重试对"文件不存在"幂等），两种失败都不会中断整批。
 * 2. cleanupDeletingImages：重试"待删除"图片（DELETING 状态，即删除中途失败留下的记录）。
 *    扫描 nextRetryTime 已到期（含为空）的记录，同样每轮最多 100 条；删除成功就清掉文件和记录，
 *    失败则把下次重试时间推后 5 分钟（hmdp.upload.deleting-retry-delay-minutes），等下一轮再试。
 * 两项清理分别返回各自处理成功的条数，有任何清理动作时打一条 INFO 日志，便于排查堆积。
 *
 * 多实例互斥与失败观测：每轮开始先用 RedisLockClient.tryLock 抢锁 key "lock:cleanup:blog-image"，
 * 抢不到说明其他实例正在跑，直接跳过本轮；任务体整体 try/catch，失败时累加类内连续失败计数并打
 * ERROR 日志（成功后清零），finally 里释放锁。注意锁 TTL 沿用 RedisLockClient 的 10 秒固定值，
 * 属于尽力而为的互斥：单轮超过 10 秒时锁会自然过期，但清理逻辑本身幂等，重复执行不会产生副作用。
 */

import com.hmdp.service.IBlogImageService;
import com.hmdp.utils.RedisLockClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class BlogImageCleanupJob {

    /** 本任务的 Redis 互斥锁 key：抢不到说明其他实例正在执行本轮清理。 */
    private static final String LOCK_KEY = "lock:cleanup:blog-image";

    private final IBlogImageService blogImageService;
    private final RedisLockClient redisLockClient;

    /** 连续失败轮数：成功一轮清零；调度线程单线程执行，AtomicLong 只是防御性选择。 */
    private final AtomicLong consecutiveFailures = new AtomicLong();

    public BlogImageCleanupJob(IBlogImageService blogImageService, RedisLockClient redisLockClient) {
        this.blogImageService = blogImageService;
        this.redisLockClient = redisLockClient;
    }

    @Scheduled(fixedDelayString = "${hmdp.upload.cleanup-interval-ms:3600000}")
    public void cleanupExpiredTemporaryImages() {
        if (!redisLockClient.tryLock(LOCK_KEY)) {
            log.info("其他实例正在执行博客图片清理，本轮跳过");
            return;
        }
        try {
            int temporaryCleaned = blogImageService.cleanupExpiredTemporaryImages();
            int deletingCleaned = blogImageService.cleanupDeletingImages();
            consecutiveFailures.set(0);
            if (temporaryCleaned > 0 || deletingCleaned > 0) {
                log.info("博客图片补偿清理完成，temporary={}, deleting={}",
                        temporaryCleaned, deletingCleaned);
            }
        } catch (RuntimeException e) {
            long failures = consecutiveFailures.incrementAndGet();
            log.error("博客图片补偿清理失败，已连续失败 {} 轮", failures, e);
        } finally {
            redisLockClient.unlock(LOCK_KEY);
        }
    }
}
