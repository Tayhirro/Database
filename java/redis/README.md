# Redis 操作指南（Java 版）

> Redis 是一个高性能的键值对存储系统，常用于缓存、消息队列、分布式锁等场景。

---

## 1. 基础配置

### 1.1 Maven 依赖

```xml
<dependencies>
    <!-- Spring Data Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    
    <!-- 连接池 -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-pool2</artifactId>
    </dependency>
    
    <!-- JSON 序列化 -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
</dependencies>
```

### 1.2 配置文件

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: 1000ms
    timeout: 5000ms
```

---

## 2. 序列化配置（重要！）

### 2.1 自定义 RedisTemplate（推荐）

```java
@Configuration
public class RedisConfig {
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 使用 Jackson2JsonRedisSerializer 序列化 Java 对象
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = 
            new Jackson2JsonRedisSerializer<>(Object.class);
        
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, 
            ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        
        // Key 使用 String 序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        
        // Value 使用 JSON 序列化
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        
        template.afterPropertiesSet();
        return template;
    }
}
```

### 2.2 StringRedisTemplate（字符串专用）

```java
@Service
public class RedisService {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
}
```

**区别：**
- `StringRedisTemplate`：键值都是 String，用于简单字符串存储
- `RedisTemplate<String, Object>`：值可以是任意 Java 对象（需配置序列化）

---

## 3. 基本数据类型操作

### 3.1 String（字符串）

```java
@Service
public class StringOps {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    // 设置值
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
    
    // 设置值 + 过期时间
    public void setWithExpire(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }
    
    // 获取值
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    // 如果不存在则设置（分布式锁的基础）
    public Boolean setIfAbsent(String key, String value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }
    
    // 原子递增（计数器）
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }
    
    // 删除
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }
}
```

### 3.2 Hash（哈希 - 类似 Java Map）

```java
@Service
public class HashOps {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // 存储对象（将 User 对象的字段映射到 Hash）
    public void putUser(String key, User user) {
        redisTemplate.opsForHash().put(key, "id", user.getId());
        redisTemplate.opsForHash().put(key, "name", user.getName());
        redisTemplate.opsForHash().put(key, "age", user.getAge());
    }
    
    // 存储整个 Map
    public void putAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }
    
    // 获取单个字段
    public Object getField(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }
    
    // 获取整个对象（Map）
    public Map<Object, Object> getAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }
    
    // 删除字段
    public Long deleteField(String key, Object... fields) {
        return redisTemplate.opsForHash().delete(key, fields);
    }
    
    // 判断字段是否存在
    public Boolean hasField(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }
}

// 使用示例
User user = new User(1L, "张三", 25);
hashOps.putUser("user:1", user);

// 或者更简洁的方式：直接用 Map
Map<String, Object> userMap = new HashMap<>();
userMap.put("id", 1L);
userMap.put("name", "张三");
userMap.put("age", 25);
hashOps.putAll("user:1", userMap);
```

### 3.3 List（列表 - 类似 Java List）

```java
@Service
public class ListOps {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    // 左插入（队列头部）
    public Long leftPush(String key, String value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }
    
    // 右插入（队列尾部）
    public Long rightPush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }
    
    // 左弹出（从头部取出）
    public String leftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }
    
    // 右弹出（从尾部取出）
    public String rightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }
    
    // 范围查询（分页）
    public List<String> range(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }
    
    // 获取列表长度
    public Long size(String key) {
        return redisTemplate.opsForList().size(key);
    }
    
    // 应用场景：消息队列
    public void sendMessage(String queue, String message) {
        // 生产者：右插入
        redisTemplate.opsForList().rightPush(queue, message);
    }
    
    public String receiveMessage(String queue) {
        // 消费者：左弹出（阻塞式）
        return redisTemplate.opsForList().leftPop(queue, 10, TimeUnit.SECONDS);
    }
}
```

### 3.4 Set（集合 - 无序唯一）

```java
@Service
public class SetOps {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    // 添加元素
    public Long add(String key, String... members) {
        return redisTemplate.opsForSet().add(key, members);
    }
    
    // 获取所有成员
    public Set<String> members(String key) {
        return redisTemplate.opsForSet().members(key);
    }
    
    // 判断是否包含
    public Boolean isMember(String key, String member) {
        return redisTemplate.opsForSet().isMember(key, member);
    }
    
    // 移除元素
    public Long remove(String key, Object... members) {
        return redisTemplate.opsForSet().remove(key, members);
    }
    
    // 交集（共同好友）
    public Set<String> intersect(String key1, String key2) {
        return redisTemplate.opsForSet().intersect(key1, key2);
    }
    
    // 并集
    public Set<String> union(String key1, String key2) {
        return redisTemplate.opsForSet().union(key1, key2);
    }
    
    // 差集
    public Set<String> difference(String key1, String key2) {
        return redisTemplate.opsForSet().difference(key1, key2);
    }
}
```

### 3.5 ZSet（有序集合 - 带分数）

```java
@Service
public class ZSetOps {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    // 添加元素（带分数）
    public Boolean add(String key, String member, double score) {
        return redisTemplate.opsForZSet().add(key, member, score);
    }
    
    // 按分数范围查询（排行榜）
    public Set<String> rangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }
    
    // 倒序查询（Top N）
    public Set<String> reverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }
    
    // 获取排名（第几名）
    public Long rank(String key, String member) {
        return redisTemplate.opsForZSet().rank(key, member);
    }
    
    // 增加分数（如点赞数）
    public Double incrementScore(String key, String member, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, member, delta);
    }
    
    // 应用场景：排行榜
    public void updateScore(String game, String player, int score) {
        redisTemplate.opsForZSet().add("rank:" + game, player, score);
    }
    
    public Set<String> getTop10(String game) {
        // 获取前10名
        return redisTemplate.opsForZSet()
            .reverseRange("rank:" + game, 0, 9);
    }
}
```

---

## 4. 过期时间设置（TTL）

```java
@Service
public class ExpireOps {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    // 方式1：设置时指定过期时间
    public void setWithExpire(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }
    
    // 方式2：对已存在的 key 设置过期时间
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }
    
    // 方式3：指定时间点过期
    public Boolean expireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }
    
    // 获取剩余过期时间
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
    
    // 取消过期时间（设为永久）
    public Boolean persist(String key) {
        return redisTemplate.persist(key);
    }
    
    // 实用场景：验证码 5 分钟过期
    public void setCode(String phone, String code) {
        redisTemplate.opsForValue()
            .set("code:" + phone, code, 5, TimeUnit.MINUTES);
    }
    
    // 实用场景：Token 7 天过期
    public void setToken(String userId, String token) {
        redisTemplate.opsForValue()
            .set("token:" + userId, token, 7, TimeUnit.DAYS);
    }
}
```

---

## 5. 对象序列化存储（重点）

### 5.1 使用 Jackson 序列化（推荐）

```java
@Service
public class ObjectRedisService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // 存储 Java 对象（自动转 JSON）
    public void setObject(String key, Object obj) {
        redisTemplate.opsForValue().set(key, obj);
    }
    
    // 存储对象 + 过期时间
    public void setObject(String key, Object obj, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, obj, timeout, unit);
    }
    
    // 读取并反序列化
    @SuppressWarnings("unchecked")
    public <T> T getObject(String key, Class<T> clazz) {
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null) {
            return null;
        }
        // 如果配置了 Jackson，获取到的就是 clazz 类型的对象
        return (T) obj;
    }
    
    // 存储 List
    public void setList(String key, List<?> list) {
        redisTemplate.opsForValue().set(key, list);
    }
    
    // 读取 List
    public <T> List<T> getList(String key, Class<T> clazz) {
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj instanceof List) {
            return (List<T>) obj;
        }
        return null;
    }
}
```

### 5.2 使用 Hash 存储对象（更节省空间）

```java
@Service
public class ObjectHashService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // 将对象转为 Map 存入 Hash（推荐！）
    public void setUser(String key, User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("name", user.getName());
        map.put("age", user.getAge());
        map.put("createTime", user.getCreateTime());
        
        redisTemplate.opsForHash().putAll(key, map);
        // 设置过期时间
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
    }
    
    // 从 Hash 读取并组装对象
    public User getUser(String key) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return null;
        }
        
        User user = new User();
        user.setId((Long) entries.get("id"));
        user.setName((String) entries.get("name"));
        user.setAge((Integer) entries.get("age"));
        return user;
    }
    
    // 更优雅的方式：使用 BeanUtils
    public void setUserBean(String key, User user) {
        Map<String, Object> map = BeanUtil.beanToMap(user);
        redisTemplate.opsForHash().putAll(key, map);
    }
    
    public User getUserBean(String key) {
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        return BeanUtil.mapToBean(map, User.class, false);
    }
}
```

### 5.3 存储 JSON 字符串（最灵活）

```java
@Service
public class JsonRedisService {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    // 对象转 JSON 存储
    public void setJson(String key, Object obj) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(obj);
        stringRedisTemplate.opsForValue().set(key, json);
    }
    
    // JSON 转对象
    public <T> T getJson(String key, Class<T> clazz) throws JsonProcessingException {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        return objectMapper.readValue(json, clazz);
    }
    
    // 存储复杂对象（如 List<User>）
    public void setJsonList(String key, List<?> list) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(list);
        stringRedisTemplate.opsForValue().set(key, json, 10, TimeUnit.MINUTES);
    }
    
    public <T> List<T> getJsonList(String key, Class<T> clazz) throws JsonProcessingException {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        // 使用 TypeReference 处理泛型
        return objectMapper.readValue(json, new TypeReference<List<T>>() {});
    }
}
```

---

## 6. 分布式锁

```java
@Component
public class RedisLock {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    /**
     * 尝试获取锁
     * @param lockKey 锁的key
     * @param requestId 请求标识（UUID），用于解锁时验证
     * @param expireTime 过期时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId, int expireTime) {
        // setIfAbsent = SETNX 命令
        Boolean result = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, requestId, expireTime, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(result);
    }
    
    /**
     * 释放锁（使用 Lua 脚本保证原子性）
     */
    public void unlock(String lockKey, String requestId) {
        // Lua 脚本：只有 value 匹配时才删除（防止误删别人的锁）
        String script = 
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "return redis.call('del', KEYS[1]) " +
            "else return 0 end";
        
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);
        
        redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId);
    }
    
    // 使用示例：防止超卖
    public void deductStock(String productId) {
        String lockKey = "lock:stock:" + productId;
        String requestId = UUID.randomUUID().toString();
        
        try {
            boolean locked = tryLock(lockKey, requestId, 10);
            if (!locked) {
                throw new RuntimeException("获取锁失败，请重试");
            }
            
            // 扣减库存业务逻辑
            // ...
            
        } finally {
            unlock(lockKey, requestId);
        }
    }
}
```

---

## 7. 管道操作（Pipeline - 批量执行）

```java
@Service
public class PipelineOps {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    /**
     * 使用管道批量插入（提高性能）
     */
    public void batchInsert(List<User> users) {
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                for (User user : users) {
                    String key = "user:" + user.getId();
                    String json = JSON.toJSONString(user);
                    connection.set(key.getBytes(), json.getBytes());
                }
                return null;
            }
        });
    }
    
    /**
     * 管道批量查询
     */
    public List<User> batchGet(List<Long> userIds) {
        List<Object> results = redisTemplate.executePipelined(
            (RedisCallback<Object>) connection -> {
                for (Long id : userIds) {
                    connection.get(("user:" + id).getBytes());
                }
                return null;
            },
            new StringRedisSerializer()
        );
        
        // 解析结果
        List<User> users = new ArrayList<>();
        for (Object result : results) {
            if (result != null) {
                users.add(JSON.parseObject((String) result, User.class));
            }
        }
        return users;
    }
}
```

---

## 8. 缓存问题解决方案

### 8.1 缓存穿透（查不到，一直查数据库）

```java
@Service
public class CachePenetration {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private UserMapper userMapper;
    
    public User getUser(Long id) {
        String key = "user:" + id;
        String json = redisTemplate.opsForValue().get(key);
        
        if (json != null) {
            // 缓存命中
            return JSON.parseObject(json, User.class);
        }
        
        // 查询数据库
        User user = userMapper.selectById(id);
        
        if (user != null) {
            // 存入缓存
            redisTemplate.opsForValue().set(key, JSON.toJSONString(user), 30, TimeUnit.MINUTES);
        } else {
            // 解决方案：缓存空值（布隆过滤器更优）
            redisTemplate.opsForValue().set(key, "", 5, TimeUnit.MINUTES);
        }
        
        return user;
    }
}
```

### 8.2 缓存击穿（热点 key 过期，瞬间大量请求）

```java
@Service
public class CacheBreakdown {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private RedisLock redisLock;
    
    public User getHotUser(Long id) {
        String key = "user:" + id;
        String json = redisTemplate.opsForValue().get(key);
        
        if (json != null) {
            return JSON.parseObject(json, User.class);
        }
        
        // 加锁重建缓存（互斥锁）
        String lockKey = "lock:user:" + id;
        String requestId = UUID.randomUUID().toString();
        
        try {
            boolean locked = redisLock.tryLock(lockKey, requestId, 10);
            
            if (locked) {
                // 双重检查
                json = redisTemplate.opsForValue().get(key);
                if (json != null) {
                    return JSON.parseObject(json, User.class);
                }
                
                // 查询数据库并重建缓存
                User user = userMapper.selectById(id);
                redisTemplate.opsForValue().set(key, 
                    JSON.toJSONString(user), 30, TimeUnit.MINUTES);
                return user;
            } else {
                // 没抢到锁，稍等再查
                Thread.sleep(100);
                return getHotUser(id);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            redisLock.unlock(lockKey, requestId);
        }
    }
}
```

### 8.3 缓存雪崩（大量 key 同时过期）

```java
@Service
public class CacheAvalanche {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    // 解决方案1：随机过期时间
    public void setWithRandomExpire(String key, Object value) {
        // 基础30分钟 + 0-10分钟随机
        long expire = 30 + new Random().nextInt(10);
        redisTemplate.opsForValue().set(key, JSON.toJSONString(value), 
            expire, TimeUnit.MINUTES);
    }
    
    // 解决方案2：多级缓存（Redis + 本地缓存 Caffeine）
    @Cacheable(value = "user", key = "#id")
    public User getUserWithMultiLevel(Long id) {
        // 先查 Redis
        String key = "user:" + id;
        String json = redisTemplate.opsForValue().get(key);
        
        if (json != null) {
            return JSON.parseObject(json, User.class);
        }
        
        // 再查数据库
        User user = userMapper.selectById(id);
        if (user != null) {
            redisTemplate.opsForValue().set(key, 
                JSON.toJSONString(user), 30, TimeUnit.MINUTES);
        }
        return user;
    }
}
```

---

## 9. 总结

| 数据类型 | Java 对应 | 使用场景 |
|---------|----------|---------|
| String | String | 简单 KV、计数器、分布式锁 |
| Hash | Map | 存储对象、购物车 |
| List | List | 消息队列、时间线 |
| Set | Set | 标签、共同好友、去重 |
| ZSet | SortedSet | 排行榜、延迟队列 |

**核心要点：**
1. **序列化**：配置 Jackson 序列化器存储 Java 对象
2. **过期时间**：验证码/Token 场景必设过期时间
3. **存储方式**：简单对象用 String + JSON，复杂对象用 Hash
4. **缓存问题**：穿透（空值/布隆）、击穿（互斥锁）、雪崩（随机过期）
5. **性能优化**：Pipeline 批量操作、连接池配置

---

**挂载路径：**
[[Java]] → [[中间件]] → [[Redis]] → 具体操作
