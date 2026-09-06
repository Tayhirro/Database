package com.hmdp.service.feedcache;

/*
 * 现实业务背景：用户连续下拉 Feed 时需要保持同一轮排序结果，不能每翻一页都重新排序造成重复或遗漏。
 * 实际触发：BlogFeedService 查询时用 {@link #getPage} 分页读取快照、重建后用 {@link #cacheFeed} 写入快照，
 * 用户主动刷新（refresh=true）或关注关系变化后用 {@link #invalidate} 删除“当前指针”。
 * 注意 invalidate 只删指针 key（旧快照本体靠 5 分钟 TTL 自然过期，等它的旧游标也因此失效）。
 */

import com.hmdp.entity.Blog;
import com.hmdp.utils.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Feed 缓存只保存“有版本的排序快照”，不承担业务真相（博客正文、点赞数仍以 MySQL 为准）。
 * 存储结构（以 mode="for_you"、userId=1000、snapshotId=abc123 为例，key 前缀
 * {@link RedisConstants#FEED_CACHE_KEY} = "feed:cache:"）：
 *     1. 当前指针：key = "feed:cache:1000:for_you:v2:current"，value = 当前快照的 snapshotId。
 *     复合含义：前缀 +（用户 id）+（产品模式 apiValue）+（算法版本 v2）+ "current"。
 *     2. 快照列表：key = "feed:cache:1000:for_you:v2:snapshot:abc123"，类型为 Redis List：
 *     index 0 固定是标记 "__snapshot__"（防止空快照和其它数据结构混淆），
 *     之后每个元素是一条 {@link FeedCacheEntry} 序列化串 "blogId|createTimeMillis"，
 *     顺序即排序后的 Feed 顺序。
 *     3. 两类 key 的 TTL 都是 5 分钟（{@value #SNAPSHOT_TTL_MINUTES} 分钟），
 *     由 {@link #getPage} 每次命中时顺带续期。
 *
 * 一次翻页的完整读写：写入方 {@link #cacheFeed} 用一段 Lua 脚本在一次 Redis 往返里
 * 完成删除旧列表、RPUSH 标记和全部条目、EXPIRE 快照、SET 当前指针——读者永远看不到半份快照；
 * 读取方 {@link #getPage} 按游标里的 offset 用 LRANGE 取 count 条（调用方传 PAGE_SIZE+1=51 条，
 * 多取 1 条用于判断 hasMore）。
 *
 * 设计要点：
 * 1. 唯一 snapshotId 隔离刷新前后的翻页会话（旧游标带着旧 snapshotId 仍能读到旧快照）。
 * 2. Redis 异常返回 unavailable（{@link FeedCachePage#unavailable}），由上层回源召回；
 *    写入失败返回 null snapshotId，缓存故障不击穿业务可用性。
 */
@Slf4j
@Service
public class FeedCacheService {

    private static final long SNAPSHOT_TTL_MINUTES = 5;
    private static final String SNAPSHOT_MARKER = "__snapshot__";
    private static final String ALGORITHM_VERSION = "v2";
    private static final DefaultRedisScript<Long> CACHE_SCRIPT = new DefaultRedisScript<>(
            "redis.call('DEL', KEYS[1]); " +
                    "redis.call('RPUSH', KEYS[1], ARGV[3]); " +
                    "for i = 4, #ARGV do redis.call('RPUSH', KEYS[1], ARGV[i]); end; " +
                    "redis.call('EXPIRE', KEYS[1], ARGV[1]); " +
                    "redis.call('SET', KEYS[2], ARGV[2], 'EX', ARGV[1]); " +
                    "return 1;",
            Long.class
    );

    private final StringRedisTemplate stringRedisTemplate;

    public FeedCacheService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public FeedCachePage getPage(
            Long userId,
            String mode,
            String requestedSnapshotId,
            int offset,
            int count
    ) {
        try {
            String pointerKey = pointerKey(userId, mode);
            String snapshotId = requestedSnapshotId;
            if (snapshotId == null) {
                snapshotId = stringRedisTemplate.opsForValue().get(pointerKey);
            }
            if (snapshotId == null) {
                return FeedCachePage.unavailable();
            }

            String snapshotKey = snapshotKey(userId, mode, snapshotId);
            if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(snapshotKey))) {
                return FeedCachePage.unavailable();
            }
            long start = Math.max(0, offset) + 1L;
            long end = start + Math.max(0, count) - 1L;
            List<String> values = count <= 0
                    ? Collections.emptyList()
                    : stringRedisTemplate.opsForList().range(snapshotKey, start, end);
            stringRedisTemplate.expire(snapshotKey, SNAPSHOT_TTL_MINUTES, TimeUnit.MINUTES);
            if (requestedSnapshotId == null) {
                stringRedisTemplate.expire(pointerKey, SNAPSHOT_TTL_MINUTES, TimeUnit.MINUTES);
            }

            List<FeedCacheEntry> entries = values == null
                    ? Collections.emptyList()
                    : values.stream()
                            .map(FeedCacheEntry::parse)
                            .filter(java.util.Objects::nonNull)
                            .collect(Collectors.toList());
            return new FeedCachePage(true, snapshotId, entries);
        } catch (RuntimeException e) {
            log.warn("读取 Feed 快照失败，降级为重新召回，userId={}, mode={}", userId, mode, e);
            return FeedCachePage.unavailable();
        }
    }

    public String cacheFeed(Long userId, String mode, List<Blog> blogs) {
        String snapshotId = UUID.randomUUID().toString().replace("-", "");
        String snapshotKey = snapshotKey(userId, mode, snapshotId);
        String pointerKey = pointerKey(userId, mode);
        List<String> args = new ArrayList<>(blogs.size() + 3);
        args.add(String.valueOf(TimeUnit.MINUTES.toSeconds(SNAPSHOT_TTL_MINUTES)));
        args.add(snapshotId);
        args.add(SNAPSHOT_MARKER);
        for (Blog blog : blogs) {
            long createTime = blog.getCreateTime() == null
                    ? 0L
                    : blog.getCreateTime().toInstant(ZoneOffset.UTC).toEpochMilli();
            args.add(new FeedCacheEntry(blog.getId(), createTime).serialize());
        }
        try {
            stringRedisTemplate.execute(
                    CACHE_SCRIPT,
                    Arrays.asList(snapshotKey, pointerKey),
                    args.toArray(new String[0])
            );
            return snapshotId;
        } catch (RuntimeException e) {
            log.warn("写入 Feed 快照失败，本次结果仍直接返回，userId={}, mode={}", userId, mode, e);
            return null;
        }
    }

    public void invalidate(Long userId, String mode) {
        try {
            stringRedisTemplate.delete(pointerKey(userId, mode));
        } catch (RuntimeException e) {
            log.warn("失效 Feed 当前快照失败，userId={}, mode={}", userId, mode, e);
        }
    }

    private String pointerKey(Long userId, String mode) {
        return RedisConstants.FEED_CACHE_KEY + userId + ":" + mode + ":" + ALGORITHM_VERSION + ":current";
    }

    private String snapshotKey(Long userId, String mode, String snapshotId) {
        return RedisConstants.FEED_CACHE_KEY + userId + ":" + mode + ":" + ALGORITHM_VERSION
                + ":snapshot:" + snapshotId;
    }
}
