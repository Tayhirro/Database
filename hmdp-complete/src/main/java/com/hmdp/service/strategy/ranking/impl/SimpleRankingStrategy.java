package com.hmdp.service.strategy.ranking.impl;

/*
 * 现实业务背景：当系统需要一个通用内容排序基线时，综合新鲜度、热度和作者亲和度比只按单一字段更贴近浏览体验。
 * 实际触发：RankingStrategyRegistry 在请求指定 simple 或找不到策略时提供本实现；当前产品 Feed 主要使用 time/weighted。
 *
 * 排序分 = 0.5 * 新鲜度 + 0.3 * 热度 + 0.2 * 作者亲和度，各因子归一到 0~1：
 * 1. 新鲜度 recency = 1 / (1 + 博客年龄小时数 / 24)。刚发布约 1.0，每过 24 小时约衰减一半
 *    （如 1 天前约 0.5，2 天前约 0.33）。年龄 = now - createTime。
 * 2. 热度 popularity = 点赞数 / (点赞数 + 10)。10 赞约 0.5，90 赞约 0.9，越大越接近 1 但永不封顶。
 * 3. 作者亲和度 affinity：从 {@link RankingContext}（排序输入信号：用户、当前时间、作者亲和度等）的
 *    authorAffinity 取该博客作者的分值，作者不在 Map 里或上下文没带 Map 时默认 0.5。
 * 三条权重在常量 RECENCY_WEIGHT / POPULARITY_WEIGHT / AFFINITY_WEIGHT 中定义。
 * 同分时按 blogId 降序（新博客排前）；rank() 直接对传入 List 就地排序。
 */

import com.hmdp.entity.Blog;
import com.hmdp.service.strategy.ranking.RankingContext;
import com.hmdp.service.strategy.ranking.RankingStrategy;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class SimpleRankingStrategy implements RankingStrategy<Blog> {

    private static final double RECENCY_WEIGHT = 0.5;
    private static final double POPULARITY_WEIGHT = 0.3;
    private static final double AFFINITY_WEIGHT = 0.2;

    @Override
    public double score(Blog blog, RankingContext ctx) {
        double recency = calcRecency(blog, ctx);
        double popularity = calcPopularity(blog);
        double affinity = calcAffinity(blog, ctx);
        return RECENCY_WEIGHT * recency + POPULARITY_WEIGHT * popularity + AFFINITY_WEIGHT * affinity;
    }

    @Override
    public List<Blog> rank(List<Blog> blogs, RankingContext context) {
        blogs.sort((a, b) -> {
            int scoreCompare = Double.compare(score(b, context), score(a, context));
            if (scoreCompare != 0) {
                return scoreCompare;
            }
            long aId = a.getId() == null ? Long.MIN_VALUE : a.getId();
            long bId = b.getId() == null ? Long.MIN_VALUE : b.getId();
            return Long.compare(bId, aId);
        });
        return blogs;
    }

    @Override
    public String getStrategyName() {
        return "simple";
    }

    private double calcRecency(Blog blog, RankingContext ctx) {
        if (blog.getCreateTime() == null || ctx.getNow() == null) return 0;
        long ageHours = ChronoUnit.HOURS.between(blog.getCreateTime(), ctx.getNow());
        return 1.0 / (1.0 + ageHours / 24.0);
    }

    private double calcPopularity(Blog blog) {
        int liked = blog.getLiked() != null ? blog.getLiked() : 0;
        return (double) liked / (liked + 10);
    }

    private double calcAffinity(Blog blog, RankingContext ctx) {
        return ctx.getAuthorAffinity() != null
                ? ctx.getAuthorAffinity().getOrDefault(blog.getUserId(), 0.5)
                : 0.5;
    }
}
