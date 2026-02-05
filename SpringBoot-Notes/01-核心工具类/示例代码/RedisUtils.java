package com.example.demo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类封装
 * 基于 RedisTemplate 和 StringRedisTemplate 的常用操作
 */
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    // ==================== String 操作 ====================
    
    /**
     * 设置值
     */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }
    
    /**
     * 设置值 + 过期时间
     */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }
    
    /**
     * 获取值
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
    
    /**
     * 如果不存在则设置（分布式锁基础）
     */
    public Boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
    }
    
    /**
     * 原子递增
     */
    public Long increment(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }
    
    /**
     * 原子递减
     */
    public Long decrement(String key) {
        return stringRedisTemplate.opsForValue().decrement(key);
    }
    
    // ==================== Hash 操作 ====================
    
    /**
     * 存储 Hash 字段
     */
    public void hSet(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }
    
    /**
     * 批量存储 Hash
     */
    public void hSetAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }
    
    /**
     * 获取 Hash 字段
     */
    public Object hGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }
    
    /**
     * 获取整个 Hash
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }
    
    /**
     * 删除 Hash 字段
     */
    public Long hDelete(String key, Object... fields) {
        return redisTemplate.opsForHash().delete(key, fields);
    }
    
    // ==================== List 操作 ====================
    
    /**
     * 左插入（队列头部）
     */
    public Long lPush(String key, String value) {
        return stringRedisTemplate.opsForList().leftPush(key, value);
    }
    
    /**
     * 右插入（队列尾部）
     */
    public Long rPush(String key, String value) {
        return stringRedisTemplate.opsForList().rightPush(key, value);
    }
    
    /**
     * 左弹出（阻塞式）
     */
    public String lPop(String key, long timeout, TimeUnit unit) {
        return stringRedisTemplate.opsForList().leftPop(key, timeout, unit);
    }
    
    /**
     * 范围查询
     */
    public List<String> lRange(String key, long start, long end) {
        return stringRedisTemplate.opsForList().range(key, start, end);
    }
    
    // ==================== Set 操作 ====================
    
    /**
     * 添加元素
     */
    public Long sAdd(String key, String... members) {
        return stringRedisTemplate.opsForSet().add(key, members);
    }
    
    /**
     * 获取所有成员
     */
    public Set<String> sMembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }
    
    /**
     * 判断是否包含
     */
    public Boolean sIsMember(String key, String member) {
        return stringRedisTemplate.opsForSet().isMember(key, member);
    }
    
    /**
     * 交集
     */
    public Set<String> sIntersect(String key1, String key2) {
        return stringRedisTemplate.opsForSet().intersect(key1, key2);
    }
    
    // ==================== ZSet 操作 ====================
    
    /**
     * 添加元素（带分数）
     */
    public Boolean zAdd(String key, String member, double score) {
        return stringRedisTemplate.opsForZSet().add(key, member, score);
    }
    
    /**
     * 按分数范围查询
     */
    public Set<String> zRangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().rangeByScore(key, min, max);
    }
    
    /**
     * 倒序查询（Top N）
     */
    public Set<String> zReverseRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().reverseRange(key, start, end);
    }
    
    /**
     * 增加分数
     */
    public Double zIncrementScore(String key, String member, double delta) {
        return stringRedisTemplate.opsForZSet().incrementScore(key, member, delta);
    }
    
    // ==================== 通用操作 ====================
    
    /**
     * 设置过期时间
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }
    
    /**
     * 获取剩余过期时间
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
    
    /**
     * 删除 key
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }
    
    /**
     * 判断 key 是否存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
    
    // ==================== 实用场景方法 ====================
    
    /**
     * 设置验证码（5分钟过期）
     */
    public void setCode(String phone, String code) {
        set("code:" + phone, code, 5, TimeUnit.MINUTES);
    }
    
    /**
     * 获取验证码
     */
    public String getCode(String phone) {
        return get("code:" + phone);
    }
    
    /**
     * 设置 Token（7天过期）
     */
    public void setToken(String userId, String token) {
        set("token:" + userId, token, 7, TimeUnit.DAYS);
    }
    
    /**
     * 获取 Token
     */
    public String getToken(String userId) {
        return get("token:" + userId);
    }
    
    /**
     * 排行榜更新分数
     */
    public void updateRank(String game, String player, int score) {
        zAdd("rank:" + game, player, score);
    }
    
    /**
     * 获取排行榜 Top N
     */
    public Set<String> getTopN(String game, int n) {
        return zReverseRange("rank:" + game, 0, n - 1);
    }
}
