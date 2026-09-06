package com.hmdp.service.feed;

/*
 * 现实业务背景：产品只允许用户选择“关注”或“为你推荐”，不把内部算法类名暴露为接口参数。
 * 实际触发：BlogFeedService 收到 mode 参数时解析本枚举，并据此选择固定召回通道和排序策略。
 */

import com.hmdp.exception.BusinessException;

/**
 * Feed 的两种产品模式。API 只暴露产品语义（following / for_you），不暴露内部算法名：
 * 算法可以升级，客户端契约保持稳定。mode 参数缺省按 FOLLOWING 处理，其余值报 INVALID_FEED_MODE。
 *
 * 两个枚举值的召回与排序对比（通道名、策略名均以代码为准）：
 * 1. FOLLOWING（apiValue="following"）→ 召回通道 "inbox"（推模式收件箱：发布时把博客写入粉丝的
 *    tb_feed_inbox，作者粉丝数超过 hmdp.feed.push.fan-threshold 的大 V 不推、由拉模式兜底）
 *    + "follow"（拉模式：按关注作者实时查博客），排序策略 = "time"
 *    （SimpleTimeRankingStrategy：按发布时间倒序，同毫秒按 blogId 倒序）。
 * 2. FOR_YOU（apiValue="for_you"）→ 四路召回按名额合并：
 *    "for-you"（社交偏好，80）+ "interest"（兴趣召回，40）+ "cf"（协同过滤，20）
 *    + "hot"（热门兜底，60），名额来自 hmdp.recommend.quota；
 *    先用曝光服务过滤已看过的博客，再排序 = "weighted"
 *    （WeightedRankingStrategy：点赞/评论/新鲜度加权分；备用策略 "interest" 提供类型画像个性化排序），
 *    最后做作者多样性打散（同一作者在前 2 条窗口内最多出现 2 次）。
 *
 * 关于推模式 / 拉模式 / 混合模式：FOLLOWING 已实现"推拉结合"——普通作者（粉丝数不超过阈值）
 * 发布时把博客推进粉丝收件箱（推模式，读收件箱表），大 V 不推，读链路用 "follow" 通道实时拉取兜底；
 * 两种模式都会再用 Redis 快照（FeedCacheService）把同一轮结果缓存 5 分钟供连续翻页，
 * 避免每翻一页都重新召回排序。
 */
public enum FeedMode {
    FOLLOWING("following", "time"),
    FOR_YOU("for_you", "weighted");

    private final String apiValue;
    private final String rankingStrategy;

    FeedMode(String apiValue, String rankingStrategy) {
        this.apiValue = apiValue;
        this.rankingStrategy = rankingStrategy;
    }

    public String getApiValue() {
        return apiValue;
    }

    public String getRankingStrategy() {
        return rankingStrategy;
    }

    public static FeedMode from(String value) {
        String normalized = value == null ? FOLLOWING.apiValue : value.trim().toLowerCase();
        for (FeedMode mode : values()) {
            if (mode.apiValue.equals(normalized)) {
                return mode;
            }
        }
        throw BusinessException.badRequest("INVALID_FEED_MODE", "mode 仅支持 following 或 for_you");
    }
}
