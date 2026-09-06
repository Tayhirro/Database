package com.hmdp.service.strategy.ranking;

/*
 * 现实业务背景：同一篇博客对不同用户、不同时间可能有不同排序分，需要把当前用户和计算时刻等信号一起传入策略。
 * 实际触发：{@link com.hmdp.service.feed.BlogFeedService}（Feed 读链路的入口服务）构建该上下文，
 * 具体 {@link RankingStrategy}（排序策略接口）在 score/rank 时读取。
 *
 * 四个字段都是排序策略的输入信号：
 * 1. currentUserId：当前登录用户 ID，排序结果是为这个用户算的。
 * 2. now：排序计算时刻。策略用它算博客"多老了"，例如博客发布于 2 天前、now 是此刻，
 *    则博客年龄约 48 小时。同一个快照内用同一个 now，保证分数可复现。
 * 3. authorAffinity：作者 ID -> 当前用户对该作者的亲和度，取值 0~1。由 BlogFeedService 按点赞次数算出：
 *    亲和度 = min(1.0, 点赞次数 / 5.0)，即给某作者点过 5 次赞后亲和度封顶为 1.0；
 *    没点过赞的作者不进这个 Map，策略端会取默认值（见 SimpleRankingStrategy 的 0.5）。
 * 4. authorInteractionCount：作者 ID -> 当前用户给该作者博客点过赞的累计次数。
 *    WeightedRankingStrategy 用"是否超过 3 次"来决定给该作者的博客加成。
 * 5. typeAffinity：店铺类型 ID -> 用户对该类型的亲和度，取值 0~1（用户赞得最多的类型归一化为 1.0）。
 *    由 BlogFeedService 通过 RecommendQueryMapper 的点赞-类型聚合统计算出，仅 for_you 模式填充；
 *    其他模式为空 Map，InterestRankingStrategy 对缺失值取中性默认值。
 * 6. shopTypeByShopId：店铺 ID -> 店铺类型 ID。排序候选（Blog 实体）只有 shopId，
 *    本映射让策略不必查库就能把博客映射到类型；由 BlogFeedService 对候选博客的店铺
 *    批量查一次（tb_shop）填充，仅 for_you 模式填充。
 */

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class RankingContext {
    private Long currentUserId;
    private LocalDateTime now;
    private Map<Long, Double> authorAffinity;
    private Map<Long, Integer> authorInteractionCount;
    /** 店铺类型 ID -> 用户亲和度（0~1），见类注释第 5 点。 */
    private Map<Long, Double> typeAffinity;
    /** 店铺 ID -> 店铺类型 ID，见类注释第 6 点。 */
    private Map<Long, Long> shopTypeByShopId;
}
