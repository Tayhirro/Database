package com.hmdp.service.feed;

/*
 * 现实业务背景：用户浏览为你推荐后，短期内不希望反复看到同一批博客。
 * 实际触发：For You 重建候选时先过滤近期已曝光博客，页面返回后再记录本页实际曝光的 blogId。
 */

import com.hmdp.entity.Blog;
import com.hmdp.utils.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 记录“最近已经给这个用户展示过哪些博客”，减少 For You 连续刷到相同内容。
 *
 * 存储结构：Redis ZSet，key = "feed:exposure:" + userId（{@link RedisConstants#FEED_EXPOSURE_KEY}），
 * 成员 = 博客 id 的字符串形式（如 "42"），分数 = 该博客曝光时的毫秒时间戳。
 * 这里的“曝光”只表示博客曾经出现在推荐列表中，不代表用户点开、点赞或读完。
 *
 *     1. 只用于过滤重复推荐：博客正文和点赞关系仍以 MySQL 为准，
 *     Redis 中没有曝光记录不代表博客不存在。
 *     2. 记录有上限：只保留最近 7 天（{@link #filterUnseen} 每次先删除分数早于
 *     "当前时间 - 7 天" 的旧成员）且最多 {@value #MAX_RECENT_EXPOSURES} 条
 *     （{@link #record} 写入后若总量超过 5000，按分数从旧到新删掉多出的部分），
 *     防止活跃用户的 Redis ZSet 一直增长，占用越来越多内存。
 *     3. Redis 故障时继续返回 Feed：{@link #filterUnseen} 读取失败就原样返回全部候选
 *     （暂时不过滤），{@link #record} 写入失败就跳过本次记录。
 *     最坏结果只是偶尔看到重复内容，不能因为辅助去重功能故障而让整个推荐页不可用。
 *
 */
@Slf4j
@Service
public class FeedExposureService {

    private static final long RETENTION_DAYS = 7L;
    private static final int MAX_RECENT_EXPOSURES = 5000;

    private final StringRedisTemplate stringRedisTemplate;

    public FeedExposureService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public List<Blog> filterUnseen(Long userId, List<Blog> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return Collections.emptyList();
        }
        String key = RedisConstants.FEED_EXPOSURE_KEY + userId;
        try {
            long cutoff = System.currentTimeMillis() - Duration.ofDays(RETENTION_DAYS).toMillis();
            stringRedisTemplate.opsForZSet().removeRangeByScore(key, 0, cutoff);
            Set<String> values = stringRedisTemplate.opsForZSet().range(key, 0, -1);
            Set<String> seen = values == null ? Collections.emptySet() : new HashSet<>(values);
            List<Blog> unseen = new ArrayList<>(candidates.size());
            for (Blog blog : candidates) {
                if (blog.getId() != null && !seen.contains(blog.getId().toString())) {
                    unseen.add(blog);
                }
            }
            return unseen;
        } catch (RuntimeException e) {
            log.warn("读取 Feed 曝光失败，降级为不过滤，userId={}", userId, e);
            return candidates;
        }
    }

    public void record(Long userId, List<Blog> blogs) {
        if (blogs == null || blogs.isEmpty()) {
            return;
        }
        String key = RedisConstants.FEED_EXPOSURE_KEY + userId;
        double now = System.currentTimeMillis();
        Set<ZSetOperations.TypedTuple<String>> tuples = new LinkedHashSet<>();
        for (Blog blog : blogs) {
            if (blog.getId() != null) {
                tuples.add(new DefaultTypedTuple<>(blog.getId().toString(), now));
            }
        }
        try {
            if (!tuples.isEmpty()) {
                stringRedisTemplate.opsForZSet().add(key, tuples);
                Long size = stringRedisTemplate.opsForZSet().zCard(key);
                if (size != null && size > MAX_RECENT_EXPOSURES) {
                    stringRedisTemplate.opsForZSet().removeRange(key, 0, size - MAX_RECENT_EXPOSURES - 1);
                }
                stringRedisTemplate.expire(key, RETENTION_DAYS, TimeUnit.DAYS);
            }
        } catch (RuntimeException e) {
            log.warn("记录 Feed 曝光失败，不影响本次响应，userId={}", userId, e);
        }
    }
}
