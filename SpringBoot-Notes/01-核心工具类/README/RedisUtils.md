# RedisUtils - Redis 操作工具类

## 简介

`RedisUtils` 是对 Spring Data Redis 的封装，提供常用的 Redis 操作 API，简化开发。

## 依赖引入

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

## 核心方法分类

### 1. String 操作（字符串）

| 方法 | 说明 | 使用场景 |
|------|------|----------|
| `set(key, value)` | 存储字符串 | 简单 KV 存储 |
| `set(key, value, timeout, unit)` | 存储 + 过期时间 | Token、验证码 |
| `get(key)` | 获取字符串 | 读取缓存 |
| `setIfAbsent(key, value, timeout, unit)` | 不存在才设置 | 分布式锁 |
| `increment(key)` | 原子递增 | 计数器 |

### 2. Hash 操作（哈希表）

| 方法 | 说明 | 使用场景 |
|------|------|----------|
| `hSet(key, field, value)` | 存储字段 | 对象属性存储 |
| `hSetAll(key, map)` | 批量存储 | 存储整个对象 |
| `hGet(key, field)` | 获取字段 | 读取对象属性 |
| `hGetAll(key)` | 获取所有字段 | 读取整个对象 |

### 3. List 操作（列表）

| 方法 | 说明 | 使用场景 |
|------|------|----------|
| `lPush(key, value)` | 左插入 | 消息队列（生产者） |
| `rPush(key, value)` | 右插入 | 消息队列（生产者） |
| `lPop(key, timeout, unit)` | 左弹出（阻塞） | 消息队列（消费者） |
| `lRange(key, start, end)` | 范围查询 | 分页获取 |

### 4. Set 操作（集合）

| 方法 | 说明 | 使用场景 |
|------|------|----------|
| `sAdd(key, members)` | 添加元素 | 标签、好友关系 |
| `sMembers(key)` | 获取所有成员 | 查看集合内容 |
| `sIsMember(key, member)` | 判断是否包含 | 检查存在性 |
| `sIntersect(key1, key2)` | 交集 | 共同好友 |

### 5. ZSet 操作（有序集合）

| 方法 | 说明 | 使用场景 |
|------|------|----------|
| `zAdd(key, member, score)` | 添加元素+分数 | 排行榜 |
| `zRangeByScore(key, min, max)` | 按分数范围查询 | 范围查询 |
| `zReverseRange(key, start, end)` | 倒序查询 | Top N 排行榜 |
| `zIncrementScore(key, member, delta)` | 增加分数 | 点赞、积分 |

## 使用示例

### 示例 1：用户缓存

```java
@Service
public class UserService {
    
    @Autowired
    private RedisUtils redisUtils;
    
    public User getUser(Long id) {
        // 1. 查缓存
        String userJson = redisUtils.get("user:" + id);
        if (userJson != null) {
            return JSON.parseObject(userJson, User.class);
        }
        
        // 2. 查数据库
        User user = userMapper.selectById(id);
        
        // 3. 存缓存（30分钟）
        redisUtils.set("user:" + id, JSON.toJSONString(user), 30, TimeUnit.MINUTES);
        
        return user;
    }
}
```

### 示例 2：分布式锁

```java
@Component
public class DistributedLock {
    
    @Autowired
    private RedisUtils redisUtils;
    
    public boolean tryLock(String lockKey, String requestId, int expireSeconds) {
        return redisUtils.setIfAbsent(lockKey, requestId, expireSeconds, TimeUnit.SECONDS);
    }
    
    public void unlock(String lockKey) {
        redisUtils.delete(lockKey);
    }
}
```

### 示例 3：排行榜

```java
@Service
public class RankService {
    
    @Autowired
    private RedisUtils redisUtils;
    
    // 更新分数
    public void updateScore(String game, String player, int score) {
        redisUtils.updateRank(game, player, score);
    }
    
    // 获取 Top 10
    public Set<String> getTop10(String game) {
        return redisUtils.getTopN(game, 10);
    }
}
```

## 注意事项

1. **Key 命名规范**：建议使用冒号分隔，如 `user:1001`、`order:2024:001`
2. **过期时间**：临时数据（验证码、Token）必须设置过期时间
3. **序列化**：对象存储时使用 JSON 序列化
4. **连接池**：生产环境注意调整连接池大小

完整 Redis 操作指南：[Redis 配置详解](../03-配置技巧/README/Redis-Configuration.md)
