package com.hmdp.service.follow;

/*
 * 现实业务背景：数据库关注关系提交成功后，页面上的关注状态和 Feed 候选不能长期沿用旧缓存。
 * 实际触发：Spring 在 FollowChangedEvent 对应的事务提交后（AFTER_COMMIT 阶段）回调本监听器，
 * 依次做三件事；任何一步的 Redis/缓存异常都只记 warn 日志，不影响其余步骤和已提交的事务。
 * 监听方法带 fallbackExecution = true：如果发布方不在事务里（理论兜底场景），事件也会立即执行而不是被丢弃。
 *
 * 以“用户 1000 关注了用户 2000”为例，三步的具体动作：
 *     1. synchronizeRedisFollowSet：维护 Redis Set“我关注了谁”——
 *     key = "follow:" +（操作者 userId），即 "follow:1000"，
 *     成员 =（目标用户 id 的字符串），即 "2000"；
 *     关注（followed=true）执行 SADD，取关执行 SREM。
 *     该 Set 只服务 FollowServiceImpl 的“共同关注”求交集；isFollow 判断已改为回源 MySQL
 *     （按 user_id + follow_user_id 唯一索引 selectCount），不再读这个 Set。
 *     2. invalidateFollowCache：删掉 {@link FollowCacheService}（关注列表的 Caffeine 本地缓存）里
 *     userId 这一条，下次召回时重新查 tb_follow 回源。
 *     3. invalidateFeedCaches：遍历 FeedMode 的所有取值（following、for_you），
 *     逐个调用 {@link FeedCacheService#invalidate} 删除 Feed 当前指针 key
 *     （如 "feed:cache:1000:following:v2:current"），让关注流/推荐流下一页走重建；
 *     旧快照本体靠 5 分钟 TTL 自然过期。
 */

import com.hmdp.service.feedcache.FeedCacheService;
import com.hmdp.service.feedcache.FollowCacheService;
import com.hmdp.service.feed.FeedMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.hmdp.utils.RedisConstants.FOLLOW_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowChangedEventListener {

    private final StringRedisTemplate stringRedisTemplate;
    private final FollowCacheService followCacheService;
    private final FeedCacheService feedCacheService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleFollowChanged(FollowChangedEvent event) {
        synchronizeRedisFollowSet(event);
        invalidateFollowCache(event.getUserId());
        invalidateFeedCaches(event.getUserId());
    }

    private void synchronizeRedisFollowSet(FollowChangedEvent event) {
        String key = FOLLOW_KEY + event.getUserId();
        String member = event.getFollowUserId().toString();
        try {
            if (event.isFollowed()) {
                stringRedisTemplate.opsForSet().add(key, member);
            } else {
                stringRedisTemplate.opsForSet().remove(key, member);
            }
        } catch (RuntimeException e) {
            log.warn("同步 Redis 关注集合失败，userId={}, followUserId={}",
                    event.getUserId(), event.getFollowUserId(), e);
        }
    }

    private void invalidateFollowCache(Long userId) {
        try {
            followCacheService.invalidate(userId);
        } catch (RuntimeException e) {
            log.warn("失效关注本地缓存失败，userId={}", userId, e);
        }
    }

    private void invalidateFeedCaches(Long userId) {
        for (FeedMode mode : FeedMode.values()) {
            try {
                feedCacheService.invalidate(userId, mode.getApiValue());
            } catch (RuntimeException e) {
                log.warn("失效 Feed 缓存失败，userId={}, mode={}", userId, mode.getApiValue(), e);
            }
        }
    }
}
