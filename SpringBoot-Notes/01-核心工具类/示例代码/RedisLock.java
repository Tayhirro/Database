package com.example.demo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis 分布式锁实现
 * 
 * 特性：
 * 1. 支持锁的自动过期（防止死锁）
 * 2. 使用 Lua 脚本保证解锁原子性
 * 3. 支持可重入（需额外实现）
 */
@Component
public class RedisLock {

    @Autowired
    private StringRedisTemplate redisTemplate;
    
    // Lua 脚本：只有 value 匹配时才删除（防止误删别人的锁）
    private static final String UNLOCK_SCRIPT = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "return redis.call('del', KEYS[1]) " +
        "else return 0 end";
    
    /**
     * 尝试获取锁
     * 
     * @param lockKey 锁的 key
     * @param requestId 请求标识（UUID），用于解锁时验证
     * @param expireTime 过期时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId, int expireTime) {
        // SET key value NX EX seconds
        // NX - 只在键不存在时设置
        // EX - 设置过期时间（秒）
        Boolean result = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, requestId, expireTime, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(result);
    }
    
    /**
     * 释放锁
     * 
     * @param lockKey 锁的 key
     * @param requestId 请求标识（必须与加锁时一致）
     */
    public void unlock(String lockKey, String requestId) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(UNLOCK_SCRIPT);
        redisScript.setResultType(Long.class);
        
        // 执行 Lua 脚本
        Long result = redisTemplate.execute(
            redisScript, 
            Collections.singletonList(lockKey), 
            requestId
        );
        
        // result = 1 表示删除成功，0 表示锁不存在或不匹配
        if (result != null && result == 1) {
            System.out.println("锁释放成功: " + lockKey);
        } else {
            System.out.println("锁释放失败（可能已过期或被其他线程获取）: " + lockKey);
        }
    }
    
    /**
     * 生成请求 ID（UUID）
     */
    public String generateRequestId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 带超时的获取锁（自旋等待）
     * 
     * @param lockKey 锁的 key
     * @param requestId 请求标识
     * @param expireTime 锁过期时间（秒）
     * @param waitTime 最大等待时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLockWithTimeout(String lockKey, String requestId, 
                                      int expireTime, int waitTime) {
        long endTime = System.currentTimeMillis() + waitTime * 1000;
        
        while (System.currentTimeMillis() < endTime) {
            if (tryLock(lockKey, requestId, expireTime)) {
                return true;
            }
            
            // 短暂休眠，避免 CPU 空转
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        return false;
    }
}
