# Redis 数据结构与 StringRedisTemplate（速查）

> **类型**：机制（Mechanism）

## 一句话
`StringRedisTemplate` 按 Redis 数据结构提供 `opsForXxx()`，不同入口对应不同的“存储形态/命令族/取值方式”。

## 严格定义
- Redis 的 key 对应的 value 有类型：String / Hash / List / Set / ZSet。
- `StringRedisTemplate` 是 Spring Data Redis 的字符串模板：key/value 通常以 `String` 读写；Hash 的 field/value 也通常以 `String` 表达。
- `opsForValue()/opsForHash()/opsForList()/opsForSet()/opsForZSet()` 分别封装了对应类型的常用命令。

## 常用操作（命令 → Java）

### 1) String（`opsForValue()`）
- 适合：验证码、计数器、简单标记（锁/开关）、Bitmap（签到）
- 命令：`SET key value` / `GET key` / `INCR key` / `DEL key`
- Java：
  - `stringRedisTemplate.opsForValue().set(key, value, ttl, TimeUnit.MINUTES);`
  - `stringRedisTemplate.opsForValue().get(key);`
  - `stringRedisTemplate.opsForValue().increment(key);`

#### Bitmap（本质还是 String）
Redis 的 String 是“二进制安全”的字节序列，所以它除了能 `SET/GET` 一整个字符串以外，还能对同一个 value 按位操作：
- 命令：`SETBIT key offset 0|1` / `GETBIT key offset` / `BITCOUNT key` / `BITFIELD key ...`
- Spring：这些位操作在 `StringRedisTemplate.opsForValue()` 下面提供：
  - `stringRedisTemplate.opsForValue().setBit(key, offset, true);`
  - `stringRedisTemplate.opsForValue().getBit(key, offset);`
  - `stringRedisTemplate.opsForValue().bitField(key, subCommands);`

示例（签到）：
- key：`sign:{userId}:{yyyyMM}`（例如 `sign:5:202602`）
- offset：`dayOfMonth - 1`（1 号写第 0 位，2 号写第 1 位…）
  - `SETBIT sign:5:202602 0 1` 表示 2 月 1 号已签到
  - `SETBIT sign:5:202602 1 1` 表示 2 月 2 号已签到

### 2) Hash（`opsForHash()`）
- 适合：对象按字段拆分存（一个 key 下多个 field-value）
- 命令：`HSET key field value` / `HGET key field` / `HGETALL key`
- Java：
  - 写单个字段：`stringRedisTemplate.opsForHash().put(key, field, value);`
  - 写多个字段：`stringRedisTemplate.opsForHash().putAll(key, map);`
  - 取全部字段：`stringRedisTemplate.opsForHash().entries(key);`

### 3) List（`opsForList()`）
- 适合：简单队列、时间线（简化版）
- 命令：`LPUSH/RPUSH` / `LPOP/RPOP` / `LRANGE`
- Java：
  - `stringRedisTemplate.opsForList().leftPush(key, value);`
  - `stringRedisTemplate.opsForList().rightPop(key);`
  - `stringRedisTemplate.opsForList().range(key, 0, 9);`

### 4) Set（`opsForSet()`）
- 适合：去重集合（关注/粉丝、标签、是否点赞）
- 命令：`SADD` / `SREM` / `SMEMBERS` / `SISMEMBER`
- Java：
  - `stringRedisTemplate.opsForSet().add(key, value);`
  - `stringRedisTemplate.opsForSet().remove(key, value);`
  - `stringRedisTemplate.opsForSet().isMember(key, value);`

### 5) ZSet（`opsForZSet()`）
- 适合：排行榜、按时间排序（score=分数/时间戳）
- 命令：`ZADD` / `ZRANGE` / `ZREVRANGE` / `ZSCORE` / `ZREM`
- Java：
  - `stringRedisTemplate.opsForZSet().add(key, member, score);`
  - `stringRedisTemplate.opsForZSet().reverseRange(key, 0, 9);`
  - `stringRedisTemplate.opsForZSet().score(key, member);`

## TTL（过期/续期）
- 统一过期：`stringRedisTemplate.expire(key, ttl, TimeUnit.SECONDS);`
- 查看 TTL：`stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);`

## 一个对象怎么存进 Hash（例子）
如果你要把 `UserDTO(id=1,nickName="tom",icon="/a.png")` 存到 Redis Hash，常见做法是：
- key：业务主键（比如 token 对应的 `login:token:{token}`）
- value：Hash（field 是属性名，value 是属性值字符串）

写入通常是“先对象转 Map，再 `putAll`”：
- `Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(), copyOptions);`
- `stringRedisTemplate.opsForHash().putAll(key, userMap);`

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → ops → mechanism → Redis 数据结构与 StringRedisTemplate。
