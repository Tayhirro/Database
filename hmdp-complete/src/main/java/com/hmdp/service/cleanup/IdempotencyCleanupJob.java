package com.hmdp.service.cleanup;

/*
 * 现实业务背景：发布防重记录只需保留一段时间，长期不清理会让幂等表持续增长。
 * 实际触发：Spring 定时调度自动触发，分批删除已经过期的发布幂等记录。
 *
 * 背景：用户双击发布或成功响应丢失后重试时，tb_idempotency_record 表用
 * "用户 ID + clientRequestId" 唯一约束保证同一请求只创建一篇博客。每次发布都会写一条记录，
 * 记录创建时设了 expire_time = 创建时间 + 30 天（BlogIdempotencyService 的 RETENTION_DAYS），
 * 过了保留期就不再有防重意义，可以删掉。
 *
 * 调度节奏与删除方式：@Scheduled fixedDelay，默认 86400000 毫秒 = 每 24 小时跑一轮
 * （本轮跑完再计时下一轮），可用配置项 hmdp.idempotency.cleanup-interval-ms 覆盖。
 * 每轮调用 idempotencyService.cleanupExpired(500)，对应 SQL 为
 * DELETE FROM tb_idempotency_record WHERE expire_time <= 当前时间 LIMIT 500：
 * 即单轮最多删 500 条，防止一次删太多行锁表；如果某天过期记录超过 500 条，
 * 余下的留给下一轮（24 小时后）继续删。有清理动作时打一条 INFO 日志。
 *
 * 多实例互斥与失败观测：每轮开始先用 RedisLockClient.tryLock 抢锁 key "lock:cleanup:idempotency"，
 * 抢不到说明其他实例正在跑，直接跳过本轮；任务体整体 try/catch，失败时累加类内连续失败计数并打
 * ERROR 日志（成功后清零），finally 里释放锁。注意锁 TTL 沿用 RedisLockClient 的 10 秒固定值，
 * 属于尽力而为的互斥：单轮超过 10 秒时锁会自然过期，但 DELETE 本身幂等，重复执行不会产生副作用。
 */

import com.hmdp.service.blog.BlogIdempotencyService;
import com.hmdp.utils.RedisLockClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class IdempotencyCleanupJob {

    /** 本任务的 Redis 互斥锁 key：抢不到说明其他实例正在执行本轮清理。 */
    private static final String LOCK_KEY = "lock:cleanup:idempotency";

    private final BlogIdempotencyService idempotencyService;
    private final RedisLockClient redisLockClient;

    /** 连续失败轮数：成功一轮清零；调度线程单线程执行，AtomicLong 只是防御性选择。 */
    private final AtomicLong consecutiveFailures = new AtomicLong();

    public IdempotencyCleanupJob(BlogIdempotencyService idempotencyService, RedisLockClient redisLockClient) {
        this.idempotencyService = idempotencyService;
        this.redisLockClient = redisLockClient;
    }

    @Scheduled(fixedDelayString = "${hmdp.idempotency.cleanup-interval-ms:86400000}")
    public void cleanupExpiredRecords() {
        if (!redisLockClient.tryLock(LOCK_KEY)) {
            log.info("其他实例正在执行幂等记录清理，本轮跳过");
            return;
        }
        try {
            int cleaned = idempotencyService.cleanupExpired(500);
            consecutiveFailures.set(0);
            if (cleaned > 0) {
                log.info("已清理过期幂等记录，count={}", cleaned);
            }
        } catch (RuntimeException e) {
            long failures = consecutiveFailures.incrementAndGet();
            log.error("幂等记录清理失败，已连续失败 {} 轮", failures, e);
        } finally {
            redisLockClient.unlock(LOCK_KEY);
        }
    }
}
