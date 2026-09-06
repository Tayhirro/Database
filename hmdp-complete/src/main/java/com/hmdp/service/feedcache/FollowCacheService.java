package com.hmdp.service.feedcache;

/*
 * 现实业务背景：生成关注 Feed 时会频繁读取同一用户关注了谁，短时间内重复查 MySQL 没有必要。
 * 实际触发：FollowFeedRecall 召回候选时读取本缓存；关注或取关事务提交后由
 * {@link com.hmdp.service.follow.FollowChangedEventListener}（关注变更事件监听器）立即调用 {@link #invalidate} 失效。
 *
 * 这里的缓存是 Caffeine——JVM 进程内的本地缓存，不是 Redis：
 *     1. 缓存条目：key =（用户 id），value =（该用户关注的作者 id 列表，
 *     由 {@link #getFollowedIds} 的 loader 参数回源查询 tb_follow 表得到）。
 *     2. 容量与过期：写入后 5 分钟过期（expireAfterWrite），最多缓存 10000 个用户（maximumSize），
 *     超出后按 LRU 淘汰。
 *     3. 一致性代价：因为是各服务实例各自的本地缓存，过期前其它实例看不到关注变化，
 *     所以关注/取关后必须主动失效，否则关注流最长 5 分钟内仍按旧关注列表召回。
 */

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
public class FollowCacheService {

    private final Cache<Long, List<Long>> followCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    public List<Long> getFollowedIds(Long userId, Function<Long, List<Long>> loader) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return followCache.get(userId, loader::apply);
    }

    public void invalidate(Long userId) {
        if (userId != null) {
            followCache.invalidate(userId);
        }
    }
}
