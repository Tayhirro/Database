package com.hmdp.service.strategy.ranking;

/*
 * 现实业务背景：关注流和为你推荐需要不同排序规则，但 Feed 主流程不应写死具体算法。
 * 实际触发：BlogFeedService 从注册表（RankingStrategyRegistry）取得实现后调用 score/rank；
 * 实现类通过名称声明自己的策略身份，本项目现有三个实现：
 * 1. "simple"（SimpleRankingStrategy）：新鲜度 + 热度 + 作者亲和度的加权基线排序。
 * 2. "time"（SimpleTimeRankingStrategy）：按发布时间倒序，关注流（following 模式）使用。
 * 3. "weighted"（WeightedRankingStrategy）：点赞、评论、新鲜度、不感兴趣信号加权，为你推荐（for_you 模式）使用。
 */

import java.util.List;

/**
 * 排序策略接口，泛型 T 是被排序的对象（本项目里都是 {@code Blog}）。
 * score 和 rank 必须给出一致的分数语义：rank 内部就是按 score 降序排，分数高者排前。
 */
public interface RankingStrategy<T> {

    /**
     * 给单个候选打分。分数只是"相对先后"的依据，不同策略的分数不能互相比较
     * （例如 "time" 策略的分数是毫秒时间戳，"weighted" 的分数是 1 量级以内的加权和）。
     */
    double score(T item, RankingContext context);

    /**
     * 对整批候选排序并返回排好序的列表。
     * 注意：实现类会直接对传入的 List 做 in-place 排序（List.sort），返回值就是原列表。
     */
    List<T> rank(List<T> items, RankingContext context);

    /**
     * 策略的注册名。BlogFeedService 通过 FeedMode 映射出名称（following -> "time"、
     * for_you -> "weighted"），再到 RankingStrategyRegistry 里按这个名字查找实现。
     */
    String getStrategyName();
}
