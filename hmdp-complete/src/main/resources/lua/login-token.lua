-- 登录 Token 原子写入脚本：把“写 Hash 字段”和“设置过期时间”合并成一个原子操作。
--
-- 背景：Java 端先 HMSET 再 EXPIRE 是两条独立命令，两条命令之间连接中断或进程崩溃时，
-- EXPIRE 没有执行就会留下一个没有 TTL 的永久 Token，既占用内存也无法自动清理。
--
-- KEYS[1] = login:token:{token}   登录 Token 的 Hash key（前缀由常量 LOGIN_USER_KEY 决定）
-- ARGV[1] = 过期秒数（当前传入 36000，即 10 小时，由常量 LOGIN_USER_TTL 决定）
-- ARGV[2..] = Hash 字段与值交替排列：如 id、"1"、nickName、"user_xxx"、icon、"..."（来自 UserDTO 字段）
--
-- 返回值：1 = 写入成功。
-- 原子性说明：整个脚本执行期间 Redis 不会插入其他命令，
-- 因此不存在“Hash 已写入但 TTL 未设置”的中间状态；脚本失败则一个字段都不会写入，
-- Java 端捕获异常后会清理可能的残留并让本次登录失败。
for i = 2, #ARGV, 2 do
    redis.call('HMSET', KEYS[1], ARGV[i], ARGV[i + 1])
end
redis.call('EXPIRE', KEYS[1], tonumber(ARGV[1]))
return 1
