package com.hmdp.utils;

public class RedisConstants {
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 2L;
    public static final String LOGIN_CODE_SEND_LOCK_KEY = "login:code:send:lock:";
    public static final Long LOGIN_CODE_SEND_LOCK_TTL = 60L;
    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 36000L;

    public static final Long CACHE_NULL_TTL = 2L;

    public static final Long CACHE_SHOP_TTL = 30L;
    public static final String CACHE_SHOP_KEY = "cache:shop:";

    public static final String LOCK_SHOP_KEY = "lock:shop:";
    public static final Long LOCK_SHOP_TTL = 10L;

    public static final String SECKILL_STOCK_KEY = "seckill:stock:";
    // 秒杀资格集合：SECKILL_ORDERED_KEY + voucherId -> Set，成员是已抢到资格的 userId，Lua 判重用
    public static final String SECKILL_ORDERED_KEY = "seckill:ordered:";
    // 秒杀订单流：Lua 校验通过后 XADD 一条订单消息，消费端异步写库
    public static final String SECKILL_STREAM_KEY = "seckill:stream:orders";
    // 秒杀订单流消费组名
    public static final String SECKILL_STREAM_GROUP = "order-writer";
    // 消费超过最大重试次数的消息转入的死信 List
    public static final String SECKILL_STREAM_DEAD_KEY = "seckill:stream:orders:dead";
    // 售罄订阅集合：SECKILL_SUBSCRIBE_KEY + voucherId -> Set，成员是订阅到货提醒的 userId
    public static final String SECKILL_SUBSCRIBE_KEY = "seckill:subscribe:";
    // 到货/开始提醒的发送标记：SECKILL_REMINDED_KEY + voucherId -> "1"，保证同一活动只提醒一次
    public static final String SECKILL_REMINDED_KEY = "seckill:reminded:";
    // 全局唯一 ID 发生器：ID_WORKER_KEY + 业务前缀 -> INCR 序列
    public static final String ID_WORKER_KEY = "id:worker:";
    // 搜索热词 ZSet：成员是搜索词，分数是搜索次数
    public static final String SEARCH_HOT_KEY = "search:hot:";
    public static final String FEED_KEY = "feed:";
    public static final String SHOP_GEO_KEY = "shop:geo:";
    public static final String USER_SIGN_KEY = "sign:";

    public static final String FOLLOW_KEY = "follow:"; // 关注
    public static final String FEED_CACHE_KEY = "feed:cache:";
    public static final String FEED_EXPOSURE_KEY = "feed:exposure:";
    public static final String BLOG_RATE_LIMIT_KEY = "rate:blog:";
}
