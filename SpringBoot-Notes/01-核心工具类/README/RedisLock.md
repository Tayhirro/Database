# RedisLock - 分布式锁实现

## 简介

基于 Redis 实现的分布式锁，用于解决多进程/多服务下的资源竞争问题。

## 核心特性

1. **原子性加锁**：使用 `SET key value NX EX` 命令
2. **防误删**：使用 Lua 脚本验证 value 后才删除
3. **自动过期**：防止死锁
4. **可重试**：支持带超时的获取锁

## 实现原理

### 加锁流程

```
SET lock:order:123 uuid NX EX 10

NX - 只在键不存在时设置（避免覆盖别人的锁）
EX 10 - 10秒后自动过期（防止死锁）
```

### 解锁流程（Lua 脚本）

```lua
if redis.call('get', KEYS[1]) == ARGV[1] then 
    return redis.call('del', KEYS[1]) 
else 
    return 0 
end
```

**为什么用 Lua 脚本？**
- GET 和 DEL 必须原子执行
- 防止判断和删除之间锁被其他线程获取

## 使用示例

### 示例 1：防止超卖

```java
@Service
public class StockService {
    
    @Autowired
    private RedisLock redisLock;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    public boolean deductStock(String productId) {
        String lockKey = "lock:stock:" + productId;
        String requestId = redisLock.generateRequestId();
        
        try {
            // 尝试获取锁（10秒过期）
            boolean locked = redisLock.tryLock(lockKey, requestId, 10);
            
            if (!locked) {
                throw new RuntimeException("系统繁忙，请重试");
            }
            
            // 获取当前库存
            String stockStr = redisTemplate.opsForValue().get("stock:" + productId);
            int stock = Integer.parseInt(stockStr);
            
            if (stock > 0) {
                // 扣减库存
                redisTemplate.opsForValue().decrement("stock:" + productId);
                System.out.println("扣减成功，剩余库存: " + (stock - 1));
                return true;
            } else {
                System.out.println("库存不足");
                return false;
            }
            
        } finally {
            // 确保释放锁
            redisLock.unlock(lockKey, requestId);
        }
    }
}
```

### 示例 2：防止重复提交

```java
@RestController
public class OrderController {
    
    @Autowired
    private RedisLock redisLock;
    
    @PostMapping("/order/create")
    public Result createOrder(@RequestBody OrderDTO dto) {
        // 基于用户 ID 加锁，防止同一用户重复提交
        String lockKey = "lock:order:user:" + dto.getUserId();
        String requestId = redisLock.generateRequestId();
        
        try {
            // 尝试获取锁（5秒过期，3秒等待）
            boolean locked = redisLock.tryLockWithTimeout(
                lockKey, requestId, 5, 3);
            
            if (!locked) {
                return Result.fail("请勿重复提交");
            }
            
            // 执行业务逻辑
            orderService.createOrder(dto);
            return Result.ok();
            
        } finally {
            redisLock.unlock(lockKey, requestId);
        }
    }
}
```

### 示例 3：定时任务幂等性

```java
@Component
public class ScheduledTask {
    
    @Autowired
    private RedisLock redisLock;
    
    // 每5分钟执行一次
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void syncData() {
        String lockKey = "lock:task:syncData";
        String requestId = redisLock.generateRequestId();
        
        try {
            // 尝试获取锁（5分钟过期，与任务周期一致）
            boolean locked = redisLock.tryLock(lockKey, requestId, 5 * 60);
            
            if (!locked) {
                System.out.println("其他实例正在执行，跳过本次任务");
                return;
            }
            
            // 执行同步逻辑
            System.out.println("开始执行数据同步...");
            // ...
            
        } finally {
            redisLock.unlock(lockKey, requestId);
        }
    }
}
```

## 注意事项

### 1. 锁过期时间设置

```java
// 建议：业务执行时间的 2-3 倍
// 如果业务执行时间不确定，考虑看门狗机制

// 简单方案：设置足够长的时间
redisLock.tryLock(lockKey, requestId, 30);  // 30秒

// 高级方案：看门狗自动续期（需额外实现）
```

### 2. 锁粒度控制

```java
// ❌ 错误：锁粒度太大
String lockKey = "lock:order";

// ✅ 正确：细化锁粒度
String lockKey = "lock:order:" + productId;
```

### 3. 异常处理

```java
public void businessMethod() {
    String lockKey = "lock:business";
    String requestId = redisLock.generateRequestId();
    boolean locked = false;
    
    try {
        locked = redisLock.tryLock(lockKey, requestId, 10);
        if (!locked) {
            return;
        }
        
        // 执行业务逻辑
        doBusiness();
        
    } catch (Exception e) {
        // 记录日志
        log.error("业务执行异常", e);
        throw e;
    } finally {
        // 必须释放锁
        if (locked) {
            redisLock.unlock(lockKey, requestId);
        }
    }
}
```

## 与 Redisson 对比

| 特性 | 本实现 | Redisson |
|------|--------|----------|
| 复杂度 | 简单，几十行代码 | 功能完善，较重 |
| 看门狗 | ❌ 不支持 | ✅ 支持 |
| 可重入 | ❌ 不支持 | ✅ 支持 |
| 红锁 | ❌ 不支持 | ✅ 支持 |
| 适用场景 | 简单场景 | 复杂分布式系统 |

**建议**：
- 简单项目：使用本实现
- 复杂项目：使用 Redisson

## 进阶：Redisson 示例

```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>3.23.5</version>
</dependency>
```

```java
@Service
public class RedissonLockService {
    
    @Autowired
    private RedissonClient redissonClient;
    
    public void businessMethod() {
        RLock lock = redissonClient.getLock("lock:order:123");
        
        try {
            // 尝试获取锁，最多等待 3 秒，锁 10 秒后自动释放
            boolean locked = lock.tryLock(3, 10, TimeUnit.SECONDS);
            
            if (locked) {
                // 执行业务逻辑
                // Redisson 会自动续期（看门狗机制）
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
```
