# StringRedisTemplate - Redis 操作官方 API

## 简介

`StringRedisTemplate` 是 Spring Data Redis 官方提供的 Redis 操作模板类，专门用于操作字符串类型的键值对。

**官方文档**: [Spring Data Redis](https://spring.io/projects/spring-data-redis)

---

## 类层级结构

```
RedisAccessor (抽象类)
    │
    │  - 持有 RedisConnectionFactory（连接工厂）
    │  - 提供连接管理的基础能力
    │
    ▼
RedisTemplate<K, V> (核心模板类)
    │
    │  - 提供所有 Redis 操作的通用实现
    │  - 支持多种数据类型：String、Hash、List、Set、ZSet
    │  - 提供 execute() 方法执行底层命令
    │  - 支持事务、Pipeline、Lua 脚本
    │  - 默认使用 JDK 序列化（存储为字节码，不可读）
    │
    ▼
StringRedisTemplate (字符串专用)
    │
    │  - 继承 RedisTemplate<String, String>
    │  - Key 和 Value 都使用 StringRedisSerializer
    │  - 存储内容可读（推荐用于大多数场景）
    │
    ▼
你也可以自定义 RedisTemplate<String, Object>（存储对象）
```

### 源码定义

```java
// RedisAccessor - 最顶层抽象类
public abstract class RedisAccessor implements InitializingBean {
    private RedisConnectionFactory connectionFactory;
    // 管理 Redis 连接
}

// RedisTemplate - 核心模板类
public class RedisTemplate<K, V> extends RedisAccessor 
        implements RedisOperations<K, V>, BeanClassLoaderAware {
    
    // 序列化器
    private RedisSerializer<?> keySerializer;
    private RedisSerializer<?> valueSerializer;
    private RedisSerializer<?> hashKeySerializer;
    private RedisSerializer<?> hashValueSerializer;
    
    // 各数据类型操作
    private ValueOperations<K, V> valueOps;
    private HashOperations<K, ?, ?> hashOps;
    private ListOperations<K, V> listOps;
    private SetOperations<K, V> setOps;
    private ZSetOperations<K, V> zSetOps;
    
    // 核心执行方法
    public <T> T execute(RedisCallback<T> action) { ... }
    public <T> T execute(SessionCallback<T> session) { ... }
}

// StringRedisTemplate - 字符串专用
public class StringRedisTemplate extends RedisTemplate<String, String> {
    
    public StringRedisTemplate() {
        // 默认使用 StringRedisSerializer
        setKeySerializer(RedisSerializer.string());
        setValueSerializer(RedisSerializer.string());
        setHashKeySerializer(RedisSerializer.string());
        setHashValueSerializer(RedisSerializer.string());
    }
}
```

### StringRedisTemplate vs RedisTemplate 对比

| 特性 | StringRedisTemplate | RedisTemplate<K,V> |
|------|---------------------|-------------------|
| Key 类型 | String | 泛型 K |
| Value 类型 | String | 泛型 V |
| 默认序列化 | StringRedisSerializer | JdkSerializationRedisSerializer |
| 存储可读性 | ✅ 可读（纯文本） | ❌ 不可读（字节码） |
| 存储对象 | 需手动 JSON 转换 | 可直接存对象（需配置序列化） |
| 使用场景 | 缓存字符串、JSON | 缓存复杂对象 |

---

## 依赖配置

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

## 导入路径

```java
import org.springframework.data.redis.core.StringRedisTemplate;
```

## 基本使用

```java
@Service
public class UserService {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;  // Spring 官方提供
    
    // 使用示例在下方
}
```

---

## 核心 API 方法

### 1. opsForValue() - String 操作

#### 基本方法

| 方法 | 说明 | 示例 |
|------|------|------|
| `set(K key, V value)` | 设置值 | `stringRedisTemplate.opsForValue().set("key", "value")` |
| `set(K key, V value, long timeout, TimeUnit unit)` | 设置值+过期时间 | `set("key", "value", 30, TimeUnit.MINUTES)` |
| `get(K key)` | 获取值 | `String value = stringRedisTemplate.opsForValue().get("key")` |
| `setIfAbsent(K key, V value)` | 不存在才设置 | `Boolean success = setIfAbsent("lock", "1")` |
| `setIfAbsent(K key, V value, long timeout, TimeUnit unit)` | 不存在才设置+过期 | `setIfAbsent("lock", "1", 10, TimeUnit.SECONDS)` |
| `increment(K key)` | 原子递增 | `Long count = stringRedisTemplate.opsForValue().increment("counter")` |
| `increment(K key, long delta)` | 原子增加指定值 | `increment("counter", 5)` |
| `decrement(K key)` | 原子递减 | `Long count = decrement("counter")` |

#### 使用示例

```java
@Service
public class CacheService {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    // 设置缓存
    public void setCache(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }
    
    // 设置缓存+过期时间
    public void setCacheWithExpire(String key, String value, long minutes) {
        stringRedisTemplate.opsForValue().set(key, value, minutes, TimeUnit.MINUTES);
    }
    
    // 获取缓存
    public String getCache(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
    
    // 计数器
    public Long incrementCounter(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }
}
```

---

### 2. opsForHash() - Hash 操作

#### 基本方法

| 方法 | 说明 | 示例 |
|------|------|------|
| `put(H key, HK hashKey, HV value)` | 存储 Hash 字段 | `opsForHash().put("user:1", "name", "张三")` |
| `putAll(H key, Map<HK, HV> m)` | 批量存储 | `putAll("user:1", map)` |
| `get(H key, Object hashKey)` | 获取字段 | `Object name = opsForHash().get("user:1", "name")` |
| `entries(H key)` | 获取所有字段 | `Map<Object, Object> map = entries("user:1")` |
| `delete(H key, Object... hashKeys)` | 删除字段 | `delete("user:1", "password")` |
| `hasKey(H key, Object hashKey)` | 判断字段是否存在 | `Boolean exists = hasKey("user:1", "name")` |

#### 使用示例

```java
@Service
public class UserCacheService {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    // 存储用户信息
    public void saveUser(Long id, Map<String, String> userMap) {
        String key = "user:" + id;
        stringRedisTemplate.opsForHash().putAll(key, userMap);
    }
    
    // 获取用户字段
    public String getUserName(Long id) {
        String key = "user:" + id;
        return (String) stringRedisTemplate.opsForHash().get(key, "name");
    }
    
    // 获取整个用户对象
    public Map<Object, Object> getUser(Long id) {
        String key = "user:" + id;
        return stringRedisTemplate.opsForHash().entries(key);
    }
}
```

---

### 3. opsForList() - List 操作

#### 基本方法

| 方法 | 说明 | 示例 |
|------|------|------|
| `leftPush(K key, V value)` | 左插入 | `opsForList().leftPush("queue", "msg1")` |
| `rightPush(K key, V value)` | 右插入 | `rightPush("queue", "msg1")` |
| `leftPop(K key)` | 左弹出 | `String msg = leftPop("queue")` |
| `rightPop(K key)` | 右弹出 | `String msg = rightPop("queue")` |
| `leftPop(K key, long timeout, TimeUnit unit)` | 阻塞左弹出 | `leftPop("queue", 10, TimeUnit.SECONDS)` |
| `range(K key, long start, long end)` | 范围查询 | `List<String> list = range("list", 0, 9)` |
| `size(K key)` | 获取长度 | `Long size = size("list")` |

#### 使用示例

```java
@Service
public class MessageQueueService {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    // 生产消息
    public void sendMessage(String queue, String message) {
        stringRedisTemplate.opsForList().rightPush(queue, message);
    }
    
    // 消费消息（阻塞）
    public String receiveMessage(String queue) {
        return stringRedisTemplate.opsForList().leftPop(queue, 10, TimeUnit.SECONDS);
    }
    
    // 获取队列前10条
    public List<String> getMessages(String queue) {
        return stringRedisTemplate.opsForList().range(queue, 0, 9);
    }
}
```

---

### 4. opsForSet() - Set 操作

#### 基本方法

| 方法 | 说明 | 示例 |
|------|------|------|
| `add(K key, V... values)` | 添加元素 | `opsForSet().add("tags", "java", "redis")` |
| `members(K key)` | 获取所有成员 | `Set<String> tags = members("tags")` |
| `isMember(K key, Object o)` | 判断是否包含 | `Boolean exists = isMember("tags", "java")` |
| `remove(K key, Object... values)` | 移除元素 | `remove("tags", "java")` |
| `intersect(K key, K otherKey)` | 交集 | `Set<String> common = intersect("set1", "set2")` |
| `union(K key, K otherKey)` | 并集 | `Set<String> all = union("set1", "set2")` |
| `difference(K key, K otherKey)` | 差集 | `Set<String> diff = difference("set1", "set2")` |

#### 使用示例

```java
@Service
public class TagService {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    // 添加标签
    public void addTags(String userId, String... tags) {
        stringRedisTemplate.opsForSet().add("user:tags:" + userId, tags);
    }
    
    // 获取共同标签（交集）
    public Set<String> getCommonTags(String userId1, String userId2) {
        return stringRedisTemplate.opsForSet().intersect(
            "user:tags:" + userId1, 
            "user:tags:" + userId2
        );
    }
}
```

---

### 5. opsForZSet() - ZSet 有序集合操作

#### 基本方法

| 方法 | 说明 | 示例 |
|------|------|------|
| `add(K key, V value, double score)` | 添加元素+分数 | `opsForZSet().add("rank", "player1", 100)` |
| `range(K key, long start, long end)` | 按索引范围查询 | `Set<String> top10 = range("rank", 0, 9)` |
| `reverseRange(K key, long start, long end)` | 倒序查询 | `reverseRange("rank", 0, 9)` |
| `rangeByScore(K key, double min, double max)` | 按分数范围查询 | `rangeByScore("rank", 90, 100)` |
| `rank(K key, Object o)` | 获取排名 | `Long rank = rank("rank", "player1")` |
| `reverseRank(K key, Object o)` | 倒序排名 | `reverseRank("rank", "player1")` |
| `score(K key, Object o)` | 获取分数 | `Double score = score("rank", "player1")` |
| `incrementScore(K key, V value, double delta)` | 增加分数 | `incrementScore("rank", "player1", 10)` |

#### 使用示例

```java
@Service
public class RankService {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    // 更新分数
    public void updateScore(String game, String player, int score) {
        stringRedisTemplate.opsForZSet().add("rank:" + game, player, score);
    }
    
    // 获取 Top 10
    public Set<String> getTop10(String game) {
        return stringRedisTemplate.opsForZSet().reverseRange("rank:" + game, 0, 9);
    }
    
    // 获取玩家排名（第几名）
    public Long getPlayerRank(String game, String player) {
        Long rank = stringRedisTemplate.opsForZSet().reverseRank("rank:" + game, player);
        return rank != null ? rank + 1 : null;  // 排名从1开始
    }
    
    // 增加分数
    public Double addScore(String game, String player, int delta) {
        return stringRedisTemplate.opsForZSet().incrementScore("rank:" + game, player, delta);
    }
}
```

---

### 6. 通用方法（直接在 StringRedisTemplate 上调用）

| 方法 | 说明 | 示例 |
|------|------|------|
| `delete(K key)` | 删除 key | `stringRedisTemplate.delete("key")` |
| `hasKey(K key)` | 判断 key 是否存在 | `Boolean exists = hasKey("key")` |
| `expire(K key, long timeout, TimeUnit unit)` | 设置过期时间 | `expire("key", 30, TimeUnit.MINUTES)` |
| `getExpire(K key)` | 获取剩余过期时间（秒） | `Long ttl = getExpire("key")` |
| `getExpire(K key, TimeUnit unit)` | 获取剩余过期时间（指定单位） | `getExpire("key", TimeUnit.MINUTES)` |
| `persist(K key)` | 移除过期时间 | `persist("key")` |

#### 使用示例

```java
@Service
public class CommonService {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    // 删除缓存
    public void deleteCache(String key) {
        stringRedisTemplate.delete(key);
    }
    
    // 设置过期时间
    public void setExpire(String key, long minutes) {
        stringRedisTemplate.expire(key, minutes, TimeUnit.MINUTES);
    }
    
    // 获取剩余过期时间
    public Long getTTL(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}
```

---

### 7. execute() - 底层执行方法

`execute()` 是 RedisTemplate 的**核心方法**，所有 `opsForXxx()` 方法底层都调用它。当内置方法无法满足需求时，可以直接使用 `execute()` 执行底层 Redis 命令。

#### 方法签名

```java
// 1. RedisCallback - 直接操作 RedisConnection（最底层）
<T> T execute(RedisCallback<T> action);
<T> T execute(RedisCallback<T> action, boolean exposeConnection);
<T> T execute(RedisCallback<T> action, boolean exposeConnection, boolean pipeline);

// 2. SessionCallback - 支持事务和 Pipeline
<T> T execute(SessionCallback<T> session);

// 3. RedisScript - 执行 Lua 脚本
<T> T execute(RedisScript<T> script, List<K> keys, Object... args);
<T> T execute(RedisScript<T> script, RedisSerializer<?> argsSerializer, 
              RedisSerializer<T> resultSerializer, List<K> keys, Object... args);

// 4. 在指定连接上执行多条命令
List<Object> executePipelined(RedisCallback<?> action);
List<Object> executePipelined(SessionCallback<?> session);
```

#### 7.1 RedisCallback - 执行底层命令

```java
// 执行原生 Redis 命令
String result = stringRedisTemplate.execute((RedisCallback<String>) connection -> {
    // connection 是 RedisConnection，可执行任何底层命令
    byte[] value = connection.stringCommands().get("mykey".getBytes());
    return value != null ? new String(value) : null;
});

// 获取 Redis 服务器信息
Properties info = stringRedisTemplate.execute((RedisCallback<Properties>) connection -> {
    return connection.serverCommands().info();
});

// 获取所有匹配的 key（KEYS 命令，生产慎用）
Set<byte[]> keys = stringRedisTemplate.execute((RedisCallback<Set<byte[]>>) connection -> {
    return connection.keyCommands().keys("user:*".getBytes());
});

// 执行 SETNX + EXPIRE 原子操作（分布式锁）
Boolean locked = stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> {
    byte[] key = "lock:order:123".getBytes();
    byte[] value = "1".getBytes();
    
    // SET key value NX PX 10000（原子操作）
    Boolean result = connection.stringCommands().set(
        key, value,
        Expiration.milliseconds(10000),
        RedisStringCommands.SetOption.SET_IF_ABSENT
    );
    return Boolean.TRUE.equals(result);
});
```

#### 7.2 SessionCallback - 事务操作

```java
// 事务操作：MULTI ... EXEC
List<Object> results = stringRedisTemplate.execute(new SessionCallback<List<Object>>() {
    @Override
    public List<Object> execute(RedisOperations operations) throws DataAccessException {
        operations.multi();  // 开启事务
        
        operations.opsForValue().set("key1", "value1");
        operations.opsForValue().set("key2", "value2");
        operations.opsForValue().increment("counter");
        
        return operations.exec();  // 执行事务
    }
});
// results = [true, true, 1]（每条命令的返回值）

// Lambda 写法
List<Object> results = stringRedisTemplate.execute(new SessionCallback<>() {
    @Override
    public List<Object> execute(RedisOperations ops) {
        ops.multi();
        ops.opsForValue().set("a", "1");
        ops.opsForValue().set("b", "2");
        return ops.exec();
    }
});

// 带 WATCH 的乐观锁事务
stringRedisTemplate.execute(new SessionCallback<>() {
    @Override
    public Object execute(RedisOperations ops) {
        ops.watch("balance");  // 监视 key
        
        String balance = (String) ops.opsForValue().get("balance");
        int newBalance = Integer.parseInt(balance) - 100;
        
        if (newBalance < 0) {
            ops.unwatch();  // 取消监视
            throw new RuntimeException("余额不足");
        }
        
        ops.multi();
        ops.opsForValue().set("balance", String.valueOf(newBalance));
        List<Object> result = ops.exec();  // 如果 balance 被修改，返回 null
        
        if (result == null) {
            throw new RuntimeException("并发冲突，请重试");
        }
        return result;
    }
});
```

#### 7.3 Pipeline - 批量命令

```java
// 批量执行命令（减少网络往返，提高性能）
List<Object> results = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
    StringRedisConnection stringConn = (StringRedisConnection) connection;
    
    for (int i = 0; i < 1000; i++) {
        stringConn.set("key:" + i, "value:" + i);
    }
    
    return null;  // Pipeline 必须返回 null
});

// 使用 SessionCallback 的 Pipeline
List<Object> results = stringRedisTemplate.executePipelined(new SessionCallback<>() {
    @Override
    public Object execute(RedisOperations ops) {
        for (int i = 0; i < 100; i++) {
            ops.opsForValue().set("batch:" + i, String.valueOf(i));
        }
        return null;  // 必须返回 null
    }
});
```

#### 7.4 Lua 脚本执行

```java
// 定义 Lua 脚本
String luaScript = """
    local current = redis.call('GET', KEYS[1])
    if current == false then
        current = 0
    end
    current = tonumber(current) + tonumber(ARGV[1])
    redis.call('SET', KEYS[1], current)
    return current
    """;

// 创建 RedisScript 对象
RedisScript<Long> script = RedisScript.of(luaScript, Long.class);

// 执行脚本
Long result = stringRedisTemplate.execute(
    script,
    Arrays.asList("counter"),  // KEYS
    "10"                        // ARGV
);
// 相当于：counter = counter + 10

// 分布式锁释放（原子操作：只有持有锁的人才能释放）
String unlockScript = """
    if redis.call('GET', KEYS[1]) == ARGV[1] then
        return redis.call('DEL', KEYS[1])
    else
        return 0
    end
    """;

RedisScript<Long> unlockRedisScript = RedisScript.of(unlockScript, Long.class);

Long unlocked = stringRedisTemplate.execute(
    unlockRedisScript,
    Arrays.asList("lock:order:123"),  // 锁的 key
    "unique-request-id"               // 锁的持有者标识
);
// unlocked = 1 表示释放成功，0 表示不是锁的持有者
```

#### 7.5 execute 使用场景总结

| 场景 | 使用方式 | 说明 |
|------|----------|------|
| 执行原生命令 | `execute(RedisCallback)` | 当内置方法不够用时 |
| 事务操作 | `execute(SessionCallback)` + `multi()/exec()` | MULTI-EXEC 事务 |
| 乐观锁 | `execute(SessionCallback)` + `watch()` | WATCH-MULTI-EXEC |
| 批量操作 | `executePipelined()` | 减少网络往返 |
| 原子操作 | `execute(RedisScript)` | Lua 脚本保证原子性 |
| 分布式锁 | Lua 脚本 | 加锁/释放锁的原子操作 |

---

## 完整使用示例

### 示例：用户缓存

```java
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @Autowired
    private UserMapper userMapper;
    
    public User getUser(Long id) {
        String key = "user:" + id;
        
        // 1. 查缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json != null) {
            return JSON.parseObject(json, User.class);
        }
        
        // 2. 查数据库
        User user = userMapper.selectById(id);
        
        // 3. 存缓存
        if (user != null) {
            stringRedisTemplate.opsForValue().set(
                key, 
                JSON.toJSONString(user), 
                30, 
                TimeUnit.MINUTES
            );
        }
        
        return user;
    }
}
```

---

## 注意事项

1. **StringRedisTemplate vs RedisTemplate**
   - `StringRedisTemplate`：键值都是 String，用于字符串存储
   - `RedisTemplate<String, Object>`：值可以是对象，需要配置序列化

2. **过期时间单位**
   - `TimeUnit.SECONDS` - 秒
   - `TimeUnit.MINUTES` - 分钟
   - `TimeUnit.HOURS` - 小时
   - `TimeUnit.DAYS` - 天

3. **返回值注意**
   - 很多方法返回 `Boolean`，需要判空：`Boolean.TRUE.equals(result)`

---

**官方文档**: https://docs.spring.io/spring-data/redis/docs/current/api/
