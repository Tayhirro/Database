-- 秒杀资格 Lua 脚本：把"查库存、判重、扣库存、记资格"合并成一个原子操作。
--
-- KEYS[1] = seckill:stock:{voucherId}   预热进 Redis 的剩余库存字符串
-- KEYS[2] = seckill:ordered:{voucherId} 已抢到资格的 userId 集合（Set）
-- ARGV[1] = userId
--
-- 返回值约定（Java 端按码转成提示语）：
--   0 = 资格获取成功，库存已扣减 1，用户已记入资格集合
--   1 = 库存 key 不存在（活动未预热或已清理）
--   2 = 库存不足（售罄）
--   3 = 该用户已经抢到过（一人一单，取消订单后资格保留，也不允许再抢）
--
-- 原子性说明：整个脚本执行期间 Redis 不会插入其他命令，
-- 因此"GET 库存 -> 判断 > 0 -> DECR"之间不存在并发窗口，天然防超卖；
-- 数据库层的 stock > 0 条件更新与 UNIQUE(user_id, voucher_id) 只是最终兜底。
if redis.call('EXISTS', KEYS[1]) == 0 then
    return 1
end
if tonumber(redis.call('GET', KEYS[1])) <= 0 then
    return 2
end
if redis.call('SISMEMBER', KEYS[2], ARGV[1]) == 1 then
    return 3
end
redis.call('DECR', KEYS[1])
redis.call('SADD', KEYS[2], ARGV[1])
return 0
