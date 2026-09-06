package com.hmdp.service.strategy.recall;

/*
 * 现实业务背景：不同候选来源有不同查询规则，Feed 主流程只关心最终得到哪些 blogId。
 * 实际触发：每个召回实现提供名称和 recall()；{@link RecallOrchestrator}（召回编排器：
 * 按通道依次调用各策略、去重合并候选 ID）通过统一接口组合它们。
 *
 * 本项目现有两个实现：
 * 1. "follow"（FollowFeedRecall）：从用户已关注的作者中按发布时间倒序取博客 ID。
 * 2. "for-you"（ForYouRecall）：从偏好作者 + 陌生作者两路取博客 ID，供为你推荐发现圈外内容。
 */

import java.util.List;

/**
 * 召回策略接口。"召回"指从海量博客中先粗筛出一批候选 ID，之后再由排序策略决定先后。
 * 返回的 ID 顺序就是通道内的重要性顺序，编排器合并时保留这个顺序。
 */
public interface RecallStrategy {

    /**
     * 执行一次召回，返回候选博客 ID（最多 ctx.limit 条；没有候选时返回空列表而不是 null）。
     * 实现可以往 {@link RecallContext}（召回上下文：用户、时间边界、候选上限、共享信号）的
     * extra 里补充信号，供同一次请求里的其他通道或排序阶段复用。
     */
    List<Long> recall(RecallContext ctx);

    /**
     * 召回通道的注册名（如 "follow"、"for-you"）。
     * BlogFeedService 通过 FeedMode 决定用哪些通道：following -> ["follow"]，for_you -> ["follow","for-you"]。
     */
    String getStrategyName();
}
