# Redis 缓存问题解决方案

## 概述

使用 Redis 作为缓存时，需要防范三种常见问题：**缓存穿透**、**缓存击穿**、**缓存雪崩**。

---

## 1. 缓存穿透（Cache Penetration）

### 问题描述
查询一个**不存在的数据**，缓存中没有，数据库也没有，导致每次请求都打到数据库。

**场景**：恶意攻击者不断查询不存在的 ID

### 解决方案

#### 方案 1：缓存空值（简单有效）

```java
@Service
public class UserService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private UserMapper userMapper;
    
    public User getUser(Long id) {
        String key = "user:" + id;
        String json = redisTemplate.opsForValue().get(key);
        
        // 1. 缓存命中（包括空值）
        if (json != null) {
            // 空值标记
            if ("".equals(json)) {
                return null;
            }
            return JSON.parseObject(json, User.class);
        }
        
        // 2. 查询数据库
        User user = userMapper.selectById(id);
        
        if (user != null) {
            // 3. 缓存数据（30分钟）
            redisTemplate.opsForValue().set(key, JSON.toJSONString(user), 30, TimeUnit.MINUTES);
        } else {
            // 4. 缓存空值（5分钟，较短）
            redisTemplate.opsForValue().set(key, "", 5, TimeUnit.MINUTES);
        }
        
        return user;
    }
}
```

**优缺点**：
- ✅ 简单易实现
- ❌ 占用内存（存储大量空值）
- ❌ 有效期短，频繁重建

#### 方案 2：布隆过滤器（推荐生产环境）

```java
@Component
public class BloomFilter {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private static final String BLOOM_KEY = "bloom:user";
    
    // 添加元素到布隆过滤器
    public void add(Long id) {
        redisTemplate.opsForValue().setBit(BLOOM_KEY, id, true);
    }
    
    // 判断元素可能存在或一定不存在
    public boolean mightContain(Long id) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().getBit(BLOOM_KEY, id));
    }
}

@Service
public class UserService {
    
    @Autowired
    private BloomFilter bloomFilter;
    
    public User getUser(Long id) {
        // 1. 布隆过滤器检查
        if (!bloomFilter.mightContain(id)) {
            // 一定不存在，直接返回
            return null;
        }
        
        // 2. 查缓存...
        // 3. 查数据库...
    }
}
```

**优缺点**：
- ✅ 不占用额外缓存空间
- ✅ 内存效率高
- ❌ 有一定误判率（可接受范围内）
- ❌ 不能删除元素（需要重建）

---

## 2. 缓存击穿（Cache Breakdown）

### 问题描述
**热点 key 过期**的瞬间，大量请求同时打到数据库。

**场景**：微博热点新闻缓存过期

### 解决方案

#### 方案 1：互斥锁（分布式锁）

```java
@Service
public class HotDataService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private RedisLock redisLock;
    
    public User getHotUser(Long id) {
        String key = "user:" + id;
        String json = redisTemplate.opsForValue().get(key);
        
        // 1. 缓存命中
        if (json != null) {
            return JSON.parseObject(json, User.class);
        }
        
        // 2. 缓存失效，加锁重建
        String lockKey = "lock:user:" + id;
        String requestId = UUID.randomUUID().toString();
        
        try {
            boolean locked = redisLock.tryLock(lockKey, requestId, 10);
            
            if (locked) {
                // 3. 双重检查（防止多线程重复查询）
                json = redisTemplate.opsForValue().get(key);
                if (json != null) {
                    return JSON.parseObject(json, User.class);
                }
                
                // 4. 查询数据库
                User user = userMapper.selectById(id);
                
                // 5. 重建缓存
                redisTemplate.opsForValue().set(key, 
                    JSON.toJSONString(user), 30, TimeUnit.MINUTES);
                
                return user;
            } else {
                // 6. 没抢到锁，等待后重试
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

#### 方案 2：逻辑过期（永不过期）

```java
@Data
public class RedisData {
    private Object data;        // 实际数据
    private LocalDateTime expireTime;  // 逻辑过期时间
}

@Service
public class LogicalExpireService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private RedisLock redisLock;
    
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = 
        Executors.newFixedThreadPool(10);
    
    public User getUserWithLogicalExpire(Long id) {
        String key = "user:" + id;
        String json = redisTemplate.opsForValue().get(key);
        
        if (json == null) {
            return null;  // 真的不存在
        }
        
        // 1. 反序列化
        RedisData redisData = JSON.parseObject(json, RedisData.class);
        User user = JSON.parseObject((String) redisData.getData(), User.class);
        LocalDateTime expireTime = redisData.getExpireTime();
        
        // 2. 判断是否逻辑过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 未过期，直接返回
            return user;
        }
        
        // 3. 已过期，尝试获取锁重建
        String lockKey = "lock:user:" + id;
        String requestId = UUID.randomUUID().toString();
        
        boolean locked = redisLock.tryLock(lockKey, requestId, 10);
        
        if (locked) {
            // 4. 开启独立线程重建缓存
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    // 查询数据库
                    User newUser = userMapper.selectById(id);
                    // 重建缓存（设置新的逻辑过期时间）
                    saveUserWithLogicalExpire(id, newUser, 30L);
                } finally {
                    redisLock.unlock(lockKey, requestId);
                }
            });
        }
        
        // 5. 返回过期数据（保证可用性）
        return user;
    }
    
    private void saveUserWithLogicalExpire(Long id, User user, Long expireMinutes) {
        RedisData redisData = new RedisData();
        redisData.setData(JSON.toJSONString(user));
        redisData.setExpireTime(LocalDateTime.now().plusMinutes(expireMinutes));
        
        // 写入 Redis（不设 TTL，永久有效）
        redisTemplate.opsForValue().set("user:" + id, JSON.toJSONString(redisData));
    }
}
```

---

## 3. 缓存雪崩（Cache Avalanche）

### 问题描述
**大量 key 同时过期**，或 Redis 宕机，导致所有请求打到数据库。

**场景**：缓存服务器重启、批量导入数据后同时过期

### 解决方案

#### 方案 1：随机过期时间（最简单）

```java
@Service
public class RandomExpireService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private Random random = new Random();
    
    public void setWithRandomExpire(String key, Object value) {
        // 基础30分钟 + 0-10分钟随机
        long expire = 30 + random.nextInt(10);
        redisTemplate.opsForValue().set(key, 
            JSON.toJSONString(value), expire, TimeUnit.MINUTES);
    }
}
```

#### 方案 2：多级缓存

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    // Caffeine 本地缓存配置
    @Bean
    public Cache<String, Object> localCache() {
        return Caffeine.newBuilder()
            .maximumSize(1000)          // 最大1000条
            .expireAfterWrite(10, TimeUnit.MINUTES)  // 10分钟过期
            .build();
    }
}

@Service
public class MultiLevelCacheService {
    
    @Autowired
    private Cache<String, Object> localCache;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    public User getUser(Long id) {
        String key = "user:" + id;
        
        // 1. 查本地缓存（Caffeine）
        User user = (User) localCache.getIfPresent(key);
        if (user != null) {
            return user;
        }
        
        // 2. 查 Redis
        String json = redisTemplate.opsForValue().get(key);
        if (json != null) {
            user = JSON.parseObject(json, User.class);
            localCache.put(key, user);  // 写入本地缓存
            return user;
        }
        
        // 3. 查数据库
        user = userMapper.selectById(id);
        if (user != null) {
            // 写入多级缓存
            localCache.put(key, user);
            redisTemplate.opsForValue().set(key, 
                JSON.toJSONString(user), 30, TimeUnit.MINUTES);
        }
        
        return user;
    }
}
```

#### 方案 3：Redis 高可用

```yaml
# 哨兵模式
spring:
  redis:
    sentinel:
      master: mymaster
      nodes:
        - 192.168.1.10:26379
        - 192.168.1.11:26379
        - 192.168.1.12:26379

# 或集群模式
spring:
  redis:
    cluster:
      nodes:
        - 192.168.1.10:6379
        - 192.168.1.11:6379
        - 192.168.1.12:6379
```

---

## 4. 总结对比

| 问题 | 原因 | 解决方案 | 复杂度 |
|------|------|----------|--------|
| **缓存穿透** | 查询不存在的数据 | 1. 缓存空值<br>2. 布隆过滤器 | 低/中 |
| **缓存击穿** | 热点 key 过期 | 1. 互斥锁<br>2. 逻辑过期 | 中/高 |
| **缓存雪崩** | 大量 key 同时过期 | 1. 随机过期<br>2. 多级缓存<br>3. Redis 高可用 | 低/中 |

## 5. 实际项目建议

1. **基础防护**：所有缓存都加随机过期时间（防雪崩）
2. **热点数据**：使用逻辑过期 + 异步重建（防击穿）
3. **防攻击**：敏感接口加布隆过滤器（防穿透）
4. **高可用**：生产环境使用 Redis 集群 + 哨兵
