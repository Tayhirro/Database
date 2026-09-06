package com.hmdp.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 全局唯一 ID 生成器，用于秒杀订单号等需要跨表唯一、又不能依赖数据库自增的场景。
 *
 * 为什么不用数据库自增：订单量一大，单表自增 ID 容易被外部猜到成交量，
 * 分库分表后自增也会冲突；这里用 Redis INCR 保证同一秒内的序列号全局唯一。
 *
 * ID 的 64 位结构（从高位到低位）：
 * 1. 第 63 位：符号位，恒为 0，保证 ID 永远是正数；
 * 2. 第 31~62 位（共 31 位）：以 2021-01-01 00:00:00 UTC 为起点的秒级时间戳，
 *    31 位能用到约 2091 年；
 * 3. 第 0~31 位（共 32 位）：同一秒内的 Redis 自增序列，按业务前缀隔离，
 *    例如前缀 "order" 的 key 是 id:worker:order，单秒可发约 42.9 亿个号。
 *
 * 用法：nextId("order") 一次 Redis INCR 返回一个全局唯一递增趋势的长整型。
 * Redis 不可用时本类直接抛异常：订单号是交易关键依赖，不允许降级生成重复号。
 */
@Component
public class RedisIdWorker {

    /** 起始纪元：2021-01-01 00:00:00 UTC，早于项目所有业务数据。 */
    private static final long EPOCH = LocalDateTime.of(2021, 1, 1, 0, 0, 0)
            .toInstant(ZoneOffset.UTC).getEpochSecond();

    /** 时间戳部分左移位数，低 32 位留给序列号。 */
    private static final int TIMESTAMP_BITS = 32;

    private final StringRedisTemplate stringRedisTemplate;

    public RedisIdWorker(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 生成下一个全局唯一 ID。
     *
     * @param businessPrefix 业务前缀，隔离不同业务的序列计数（如 "order"）
     * @return 64 位正整数 ID，整体随时间递增，同秒内严格递增
     */
    public long nextId(String businessPrefix) {
        long timestampSeconds = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) - EPOCH;
        long count = stringRedisTemplate.opsForValue().increment(RedisConstants.ID_WORKER_KEY + businessPrefix);
        return (timestampSeconds << TIMESTAMP_BITS) | (count & 0xFFFFFFFFL);
    }
}
