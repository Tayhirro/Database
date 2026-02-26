---
title: Redis 数据结构与 StringRedisTemplate（速查）
date: "2026-02-03"
categories:
  - springboot
description: 类型：机制（Mechanism）
---
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

#### 基础操作
| 操作 | Redis 命令 | Java 方法 |
|------|-----------|----------|
| 设置值 | `SET key value` | `opsForValue().set(key, value)` |
| 设置值+过期 | `SET key value EX 60` | `opsForValue().set(key, value, 60, TimeUnit.SECONDS)` |
| 获取值 | `GET key` | `opsForValue().get(key)` |
| 获取并设置新值 | `GETSET key newVal` | `opsForValue().getAndSet(key, newVal)` |
| 不存在才设置 | `SETNX key value` | `opsForValue().setIfAbsent(key, value)` |
| 不存在才设置+过期 | `SET key value NX EX 60` | `opsForValue().setIfAbsent(key, value, 60, TimeUnit.SECONDS)` |
| 存在才设置 | `SET key value XX` | `opsForValue().setIfPresent(key, value)` |
| 批量设置 | `MSET k1 v1 k2 v2` | `opsForValue().multiSet(map)` |
| 批量获取 | `MGET k1 k2 k3` | `opsForValue().multiGet(keys)` |
| 追加字符串 | `APPEND key value` | `opsForValue().append(key, value)` |
| 获取长度 | `STRLEN key` | `opsForValue().size(key)` |

#### 数值操作（计数器）
| 操作 | Redis 命令 | Java 方法 |
|------|-----------|----------|
| 自增 1 | `INCR key` | `opsForValue().increment(key)` |
| 自增 N | `INCRBY key N` | `opsForValue().increment(key, N)` |
| 自减 1 | `DECR key` | `opsForValue().decrement(key)` |
| 自减 N | `DECRBY key N` | `opsForValue().decrement(key, N)` |
| 浮点数增加 | `INCRBYFLOAT key 0.5` | `opsForValue().increment(key, 0.5)` |

#### 代码示例
```java
// 验证码：5 分钟过期
opsForValue().set("code:13800138000", "123456", 5, TimeUnit.MINUTES);

// 分布式锁：不存在才设置 + 过期时间
Boolean locked = opsForValue().setIfAbsent("lock:order:123", "uuid", 30, TimeUnit.SECONDS);

// 计数器：文章浏览量 +1
Long views = opsForValue().increment("article:100:views");

// 批量获取
List<String> keys = Arrays.asList("user:1:name", "user:2:name", "user:3:name");
List<String> values = opsForValue().multiGet(keys);

// 批量设置
Map<String, String> map = new HashMap<>();
map.put("k1", "v1");
map.put("k2", "v2");
opsForValue().multiSet(map);
```

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

#### BITFIELD（一次取多天的 bit）
在“统计连续签到天数”时，关键点是：**一次把本月从 1 号到今天的所有 bit 取出来**，在 JVM 里用位运算快速统计。

你在 HMDP 里看到的用法：
```java
List<Long> result = stringRedisTemplate.opsForValue().bitField(
    key,
    BitFieldSubCommands.create()
        .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth))
        .valueAt(0)
);
```

它对应的 Redis 命令语义（近似）是：
- `BITFIELD {key} GET u{dayOfMonth} 0`

解释每一段：
- `BitFieldSubCommands.create()`：**创建一个“命令构造器/描述对象”**，并不会真的发 Redis 命令。
- `.get(...)`：向这个构造器里追加一个 GET 子命令（`BITFIELD` 可以一次带多个子命令）。
- `BitFieldType.unsigned(dayOfMonth)`：表示读取一个“无符号整数”，长度是 `dayOfMonth` 个 bit（今天是 3 号就读 3 个 bit）。
- `.valueAt(0)`：从 bit offset=0 开始读。

返回为什么是 `List<Long>`：
- 因为一个 `BITFIELD` 请求里可以有多个 GET/SET/INCR 子命令，所以 Spring 用 `List<Long>` 按顺序返回每个子命令的结果。
- 你这里只有一个 GET，所以 `result.get(0)` 就是那段 bit 解释成的无符号整数。

为啥它能用 `num & 1` 从“今天开始往前数”：
- 你写签到是 `SETBIT key (dayOfMonth - 1) 1`（越靠近今天 offset 越大）。
- `BITFIELD GET uN 0` 读的是从 offset 0 开始的 N 个 bit，其中 **最后一个 bit（offset=N-1，也就是今天）会落在结果整数的最低位（LSB）**。
- 所以 `num & 1` 检查的是“今天是否签到”，然后 `num >>>= 1` 就把“今天”抛掉，继续检查“昨天”…直到遇到 0 停止。

### 2) Hash（`opsForHash()`）
- 适合：对象按字段拆分存（一个 key 下多个 field-value）
- 命令：`HSET key field value` / `HGET key field` / `HGETALL key`

#### 基础操作
| 操作 | Redis 命令 | Java 方法 |
|------|-----------|----------|
| 写单个字段 | `HSET key field value` | `opsForHash().put(key, field, value)` |
| 写多个字段 | `HMSET key f1 v1 f2 v2` | `opsForHash().putAll(key, map)` |
| 不存在才写 | `HSETNX key field value` | `opsForHash().putIfAbsent(key, field, value)` |
| 取单个字段 | `HGET key field` | `opsForHash().get(key, field)` |
| 取多个字段 | `HMGET key f1 f2 f3` | `opsForHash().multiGet(key, fields)` |
| 取全部字段 | `HGETALL key` | `opsForHash().entries(key)` |
| 只取所有 field | `HKEYS key` | `opsForHash().keys(key)` |
| 只取所有 value | `HVALS key` | `opsForHash().values(key)` |
| 删除字段 | `HDEL key f1 f2` | `opsForHash().delete(key, f1, f2)` |
| 判断字段存在 | `HEXISTS key field` | `opsForHash().hasKey(key, field)` |
| 字段数量 | `HLEN key` | `opsForHash().size(key)` |

#### 数值操作（计数器）
| 操作 | Redis 命令 | Java 方法 |
|------|-----------|----------|
| 整数加 N | `HINCRBY key field N` | `opsForHash().increment(key, field, N)` |
| 整数加 1 | `HINCRBY key field 1` | `opsForHash().increment(key, field, 1)` |
| 整数减 1 | `HINCRBY key field -1` | `opsForHash().increment(key, field, -1)` |
| 浮点数加 | `HINCRBYFLOAT key field 0.5` | `opsForHash().increment(key, field, 0.5)` |

#### 代码示例
```java
// 写单个字段
opsForHash().put("user:1", "name", "张三");
opsForHash().put("user:1", "age", "25");

// 写多个字段（一次性）
Map<String, String> map = new HashMap<>();
map.put("name", "张三");
map.put("age", "25");
opsForHash().putAll("user:1", map);

// 取多个字段（批量获取）
List<Object> fields = Arrays.asList("name", "age", "city");
List<Object> values = opsForHash().multiGet("user:1", fields);
// values = ["张三", "25", null]  ← city 不存在返回 null

// 计数器：点赞数 +1
opsForHash().increment("post:100", "likes", 1);

// 计数器：库存 -1
opsForHash().increment("product:1", "stock", -1);

// 判断字段是否存在
Boolean exists = opsForHash().hasKey("user:1", "name");  // true

// 只取所有字段名
Set<Object> keys = opsForHash().keys("user:1");  // ["name", "age"]

// 只取所有值
List<Object> vals = opsForHash().values("user:1");  // ["张三", "25"]
```

#### 游标扫描（大 Hash 分批遍历）
```java
// HSCAN：避免 HGETALL 一次性加载过多数据
Cursor<Map.Entry<Object, Object>> cursor = opsForHash().scan("bigHash", 
    ScanOptions.scanOptions().match("user:*").count(100).build());

while (cursor.hasNext()) {
    Map.Entry<Object, Object> entry = cursor.next();
    // 处理 entry.getKey(), entry.getValue()
}
cursor.close();
```

### 3) List（`opsForList()`）
- 适合：简单队列、时间线（简化版）
- 命令：`LPUSH/RPUSH` / `LPOP/RPOP` / `LRANGE`

#### 基础操作
| 操作 | Redis 命令 | Java 方法 |
|------|-----------|----------|
| 左插入（头部） | `LPUSH key v1 v2` | `opsForList().leftPush(key, value)` |
| 左批量插入 | `LPUSH key v1 v2 v3` | `opsForList().leftPushAll(key, v1, v2, v3)` |
| 右插入（尾部） | `RPUSH key v1 v2` | `opsForList().rightPush(key, value)` |
| 右批量插入 | `RPUSH key v1 v2 v3` | `opsForList().rightPushAll(key, v1, v2, v3)` |
| 左弹出 | `LPOP key` | `opsForList().leftPop(key)` |
| 右弹出 | `RPOP key` | `opsForList().rightPop(key)` |
| 阻塞左弹出 | `BLPOP key timeout` | `opsForList().leftPop(key, timeout, TimeUnit)` |
| 阻塞右弹出 | `BRPOP key timeout` | `opsForList().rightPop(key, timeout, TimeUnit)` |
| 范围查询 | `LRANGE key 0 9` | `opsForList().range(key, 0, 9)` |
| 按索引取 | `LINDEX key 0` | `opsForList().index(key, 0)` |
| 按索引改 | `LSET key 0 newVal` | `opsForList().set(key, 0, newVal)` |
| 列表长度 | `LLEN key` | `opsForList().size(key)` |
| 删除元素 | `LREM key count value` | `opsForList().remove(key, count, value)` |
| 保留范围 | `LTRIM key 0 99` | `opsForList().trim(key, 0, 99)` |

#### 代码示例
```java
// 消息队列：生产者（右插入）
opsForList().rightPush("queue:orders", orderId);

// 消息队列：消费者（左弹出，阻塞 10 秒）
String msg = opsForList().leftPop("queue:orders", 10, TimeUnit.SECONDS);

// 分页查询：取第 1-10 条
List<String> list = opsForList().range("timeline:user1", 0, 9);

// 限制列表长度（只保留最近 100 条）
opsForList().rightPush("recent:views", itemId);
opsForList().trim("recent:views", -100, -1);  // 保留最后 100 个

// 删除指定元素（count=0 删除所有匹配项）
opsForList().remove("mylist", 0, "valueToRemove");
```

### 4) Set（`opsForSet()`）
- 适合：去重集合（关注/粉丝、标签、是否点赞）
- 命令：`SADD` / `SREM` / `SMEMBERS` / `SISMEMBER`

#### 基础操作
| 操作 | Redis 命令 | Java 方法 |
|------|-----------|----------|
| 添加成员 | `SADD key m1 m2` | `opsForSet().add(key, m1, m2)` |
| 移除成员 | `SREM key m1 m2` | `opsForSet().remove(key, m1, m2)` |
| 判断是否存在 | `SISMEMBER key m` | `opsForSet().isMember(key, m)` |
| 获取所有成员 | `SMEMBERS key` | `opsForSet().members(key)` |
| 成员数量 | `SCARD key` | `opsForSet().size(key)` |
| 随机取 N 个 | `SRANDMEMBER key N` | `opsForSet().randomMembers(key, N)` |
| 随机弹出 | `SPOP key` | `opsForSet().pop(key)` |
| 移动成员 | `SMOVE src dst m` | `opsForSet().move(src, m, dst)` |

#### 集合运算（社交场景常用）
| 操作 | Redis 命令 | Java 方法 |
|------|-----------|----------|
| 交集（共同关注） | `SINTER k1 k2` | `opsForSet().intersect(k1, k2)` |
| 并集 | `SUNION k1 k2` | `opsForSet().union(k1, k2)` |
| 差集（我关注他没关注） | `SDIFF k1 k2` | `opsForSet().difference(k1, k2)` |
| 交集存到新 key | `SINTERSTORE dst k1 k2` | `opsForSet().intersectAndStore(k1, k2, dst)` |
| 并集存到新 key | `SUNIONSTORE dst k1 k2` | `opsForSet().unionAndStore(k1, k2, dst)` |
| 差集存到新 key | `SDIFFSTORE dst k1 k2` | `opsForSet().differenceAndStore(k1, k2, dst)` |

#### 代码示例
```java
// 用户点赞
opsForSet().add("post:100:likes", "user:1");

// 取消点赞
opsForSet().remove("post:100:likes", "user:1");

// 判断是否点赞
Boolean liked = opsForSet().isMember("post:100:likes", "user:1");

// 点赞数
Long count = opsForSet().size("post:100:likes");

// 共同关注（交集）
Set<String> common = opsForSet().intersect("follow:user1", "follow:user2");

// 我关注的人里，他没关注的（推荐关注）
Set<String> recommend = opsForSet().difference("follow:user1", "follow:user2");

// 随机抽奖：抽 3 个中奖用户（不重复）
List<String> winners = opsForSet().randomMembers("lottery:2024", 3);

// 随机抽奖：抽 1 个并移除（不能重复中奖）
String winner = opsForSet().pop("lottery:2024");
```

#### 游标扫描（大 Set 分批遍历）
```java
Cursor<String> cursor = opsForSet().scan("bigSet",
    ScanOptions.scanOptions().match("*").count(100).build());
    
while (cursor.hasNext()) {
    String member = cursor.next();
}
cursor.close();
```

### 5) ZSet（`opsForZSet()`）
- 适合：排行榜、按时间排序（score=分数/时间戳）
- 命令：`ZADD` / `ZRANGE` / `ZREVRANGE` / `ZSCORE` / `ZREM`

#### 基础操作
| 操作 | Redis 命令 | Java 方法 |
|------|-----------|----------|
| 添加成员 | `ZADD key score member` | `opsForZSet().add(key, member, score)` |
| 批量添加 | `ZADD key s1 m1 s2 m2` | `opsForZSet().add(key, Set<TypedTuple>)` |
| 移除成员 | `ZREM key m1 m2` | `opsForZSet().remove(key, m1, m2)` |
| 获取分数 | `ZSCORE key member` | `opsForZSet().score(key, member)` |
| 增加分数 | `ZINCRBY key delta member` | `opsForZSet().incrementScore(key, member, delta)` |
| 成员数量 | `ZCARD key` | `opsForZSet().size(key)` |
| 分数范围内数量 | `ZCOUNT key min max` | `opsForZSet().count(key, min, max)` |

#### 排名查询
| 操作 | Redis 命令 | Java 方法 |
|------|-----------|----------|
| 正序排名（0开始） | `ZRANK key member` | `opsForZSet().rank(key, member)` |
| 倒序排名（0开始） | `ZREVRANK key member` | `opsForZSet().reverseRank(key, member)` |
| 正序范围（分数低→高） | `ZRANGE key 0 9` | `opsForZSet().range(key, 0, 9)` |
| 倒序范围（分数高→低） | `ZREVRANGE key 0 9` | `opsForZSet().reverseRange(key, 0, 9)` |
| 带分数的正序范围 | `ZRANGE key 0 9 WITHSCORES` | `opsForZSet().rangeWithScores(key, 0, 9)` |
| 带分数的倒序范围 | `ZREVRANGE key 0 9 WITHSCORES` | `opsForZSet().reverseRangeWithScores(key, 0, 9)` |

#### 按分数范围查询
| 操作 | Redis 命令 | Java 方法 |
|------|-----------|----------|
| 分数范围内成员 | `ZRANGEBYSCORE key min max` | `opsForZSet().rangeByScore(key, min, max)` |
| 倒序分数范围 | `ZREVRANGEBYSCORE key max min` | `opsForZSet().reverseRangeByScore(key, min, max)` |
| 按分数范围删除 | `ZREMRANGEBYSCORE key min max` | `opsForZSet().removeRangeByScore(key, min, max)` |
| 按排名范围删除 | `ZREMRANGEBYRANK key 0 9` | `opsForZSet().removeRange(key, 0, 9)` |

#### 代码示例
```java
// 排行榜：添加/更新分数
opsForZSet().add("rank:game", "player1", 1000);
opsForZSet().add("rank:game", "player2", 2000);

// 增加分数（玩家得分 +100）
opsForZSet().incrementScore("rank:game", "player1", 100);

// 获取 Top 10（分数从高到低）
Set<String> top10 = opsForZSet().reverseRange("rank:game", 0, 9);

// 获取 Top 10 带分数
Set<ZSetOperations.TypedTuple<String>> top10WithScores = 
    opsForZSet().reverseRangeWithScores("rank:game", 0, 9);
    
for (ZSetOperations.TypedTuple<String> tuple : top10WithScores) {
    String player = tuple.getValue();
    Double score = tuple.getScore();
}

// 查询玩家排名（从 0 开始，所以 +1 才是第几名）
Long rank = opsForZSet().reverseRank("rank:game", "player1");
// rank = 0 表示第 1 名

// 查询某分数段的玩家
Set<String> players = opsForZSet().rangeByScore("rank:game", 1000, 2000);

// 延迟队列：按时间戳排序，取到期的任务
long now = System.currentTimeMillis();
Set<String> expiredTasks = opsForZSet().rangeByScore("delay:queue", 0, now);
// 处理后删除
opsForZSet().removeRangeByScore("delay:queue", 0, now);

// 滑动窗口限流：保留最近 1 分钟的请求记录
long windowStart = System.currentTimeMillis() - 60000;
opsForZSet().removeRangeByScore("rate:user1", 0, windowStart);
Long count = opsForZSet().size("rate:user1");
if (count < 100) {  // 限制 100 次/分钟
    opsForZSet().add("rate:user1", UUID.randomUUID().toString(), System.currentTimeMillis());
}
```

#### 游标扫描
```java
Cursor<ZSetOperations.TypedTuple<String>> cursor = opsForZSet().scan("bigZSet",
    ScanOptions.scanOptions().count(100).build());
    
while (cursor.hasNext()) {
    ZSetOperations.TypedTuple<String> tuple = cursor.next();
    String member = tuple.getValue();
    Double score = tuple.getScore();
}
cursor.close();
```

## TTL（过期/续期）
- 统一过期：`stringRedisTemplate.expire(key, ttl, TimeUnit.SECONDS);`
- 查看 TTL：`stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);`

## 通用 Key 操作（直接用 `redisTemplate`）

| 操作 | Redis 命令 | Java 方法 |
|------|-----------|----------|
| 删除 key | `DEL key` | `redisTemplate.delete(key)` |
| 批量删除 | `DEL k1 k2 k3` | `redisTemplate.delete(keys)` |
| 判断存在 | `EXISTS key` | `redisTemplate.hasKey(key)` |
| 设置过期 | `EXPIRE key 60` | `redisTemplate.expire(key, 60, TimeUnit.SECONDS)` |
| 设置过期时间点 | `EXPIREAT key timestamp` | `redisTemplate.expireAt(key, date)` |
| 取消过期 | `PERSIST key` | `redisTemplate.persist(key)` |
| 查看 TTL | `TTL key` | `redisTemplate.getExpire(key, TimeUnit.SECONDS)` |
| 重命名 | `RENAME key newkey` | `redisTemplate.rename(key, newkey)` |
| 查看类型 | `TYPE key` | `redisTemplate.type(key)` |
| 模糊查询 key | `KEYS pattern` | `redisTemplate.keys(pattern)` （生产慎用！）|
| 游标扫描 | `SCAN cursor MATCH pattern` | `redisTemplate.scan(options)` |

#### 代码示例
```java
// 删除 key
redisTemplate.delete("user:1");

// 批量删除
Set<String> keys = redisTemplate.keys("temp:*");
redisTemplate.delete(keys);

// 判断存在
Boolean exists = redisTemplate.hasKey("user:1");

// 设置过期时间（已存在的 key）
redisTemplate.expire("user:1", 30, TimeUnit.MINUTES);

// 查看剩余时间
Long ttl = redisTemplate.getExpire("user:1", TimeUnit.SECONDS);
// -1 = 永不过期，-2 = key 不存在

// 安全的模糊删除（用 SCAN 代替 KEYS）
ScanOptions options = ScanOptions.scanOptions().match("temp:*").count(100).build();
Cursor<String> cursor = redisTemplate.scan(options);
List<String> keysToDelete = new ArrayList<>();
while (cursor.hasNext()) {
    keysToDelete.add(cursor.next());
}
cursor.close();
if (!keysToDelete.isEmpty()) {
    redisTemplate.delete(keysToDelete);
}
```

## 事务与管道

#### 管道（Pipeline）- 批量操作提升性能
```java
// 一次网络往返执行多个命令
List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
    for (int i = 0; i < 1000; i++) {
        connection.stringCommands().set(
            ("key:" + i).getBytes(), 
            ("value:" + i).getBytes()
        );
    }
    return null;
});
```

#### 事务（Multi/Exec）
```java
// 开启事务
redisTemplate.setEnableTransactionSupport(true);

List<Object> results = redisTemplate.execute(new SessionCallback<List<Object>>() {
    @Override
    public List<Object> execute(RedisOperations operations) {
        operations.multi();  // 开始事务
        
        operations.opsForValue().set("k1", "v1");
        operations.opsForValue().set("k2", "v2");
        operations.opsForValue().increment("counter");
        
        return operations.exec();  // 提交事务
    }
});
```

#### Watch（乐观锁）
```java
redisTemplate.execute(new SessionCallback<Object>() {
    @Override
    public Object execute(RedisOperations operations) {
        operations.watch("balance");  // 监视 key
        
        String balance = (String) operations.opsForValue().get("balance");
        int current = Integer.parseInt(balance);
        
        operations.multi();
        operations.opsForValue().set("balance", String.valueOf(current - 100));
        
        List<Object> results = operations.exec();  // 如果 balance 被改过，返回 null
        if (results == null) {
            // 被其他线程修改了，重试
        }
        return results;
    }
});
```

## 一个对象怎么存进 Hash（例子）
如果你要把 `UserDTO(id=1,nickName="tom",icon="/a.png")` 存到 Redis Hash，常见做法是：
- key：业务主键（比如 token 对应的 `login:token:{token}`）
- value：Hash（field 是属性名，value 是属性值字符串）

写入通常是“先对象转 Map，再 `putAll`”：
- `Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(), copyOptions);`
- `stringRedisTemplate.opsForHash().putAll(key, userMap);`

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → ops → mechanism → Redis 数据结构与 StringRedisTemplate。
