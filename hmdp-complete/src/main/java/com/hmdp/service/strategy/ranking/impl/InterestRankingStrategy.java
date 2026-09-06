package com.hmdp.service.strategy.ranking.impl;

/*
 * 现实业务背景：多路召回（for-you/interest/cf/hot）合并后的候选需要一条"以用户兴趣画像为主"的排序；
 * weighted 只看博客自身热度，对"用户最爱什么类型"无感。interest 排序补上这一层个性化。
 * 实际触发：BlogFeedService 在 for_you 模式按需选用（FeedMode 的 rankingStrategy 决定默认策略，
 * 本策略通过 RankingStrategyRegistry 以名称 "interest" 注册，供算法升级和测试切换）。
 */

import com.hmdp.entity.Blog;
import com.hmdp.service.strategy.ranking.RankingContext;
import com.hmdp.service.strategy.ranking.RankingStrategy;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * 个性化兴趣排序策略（注册名 "interest"）。
 *
 * 打分公式（四项加权，取值都在 0~1，总分 0~1）：
 * 1. 0.40 x 类型亲和度：博客所属店铺类型在用户画像里的亲和度（最高赞类型 = 1.0）；
 *    用户没有类型画像（冷启动）或店铺类型缺失时取中性值 0.5；
 * 2. 0.30 x 质量分：liked / (liked + 10)，10 是平滑常数——0 赞 0 分、10 赞 0.5 分、100 赞约 0.91 分；
 * 3. 0.20 x 新鲜度：1 / (1 + 博客年龄小时 / 12)，发布 12 小时内约 0.5 分以上，2 天约 0.2 分；
 * 4. 0.10 x 作者亲和度：当前用户给该作者点过赞的 min(1, 次数/5)；没互动过的作者取 0
 *    （与 SimpleRankingStrategy 的 0.5 中性值不同：本策略的类型项已经承担"陌生内容"的份额，
 *     作者项取 0 让位给类型信号）。
 *
 * 这是可解释的启发式排序分，不冒充经过训练的点击概率；分数只在同一次快照内比较。
 * 排序规则：分数降序，分数相同按博客 ID 降序（与 weighted 策略的全序约定一致）。
 */
@Component
public class InterestRankingStrategy implements RankingStrategy<Blog> {

    private static final double TYPE_WEIGHT = 0.40;
    private static final double QUALITY_WEIGHT = 0.30;
    private static final double FRESHNESS_WEIGHT = 0.20;
    private static final double AUTHOR_WEIGHT = 0.10;

    /** 质量分平滑常数：liked / (liked + 10)。 */
    private static final double QUALITY_SMOOTHING = 10D;
    /** 新鲜度半衰常数（小时）：1 / (1 + 小时/12)。 */
    private static final double FRESHNESS_HALF_LIFE_HOURS = 12D;
    /** 类型信号缺失（冷启动/无画像）时的中性值。 */
    private static final double NEUTRAL_TYPE_AFFINITY = 0.5D;

    @Override
    public double score(Blog blog, RankingContext ctx) {
        double typeSignal = typeSignal(blog, ctx);
        double quality = qualitySignal(blog);
        double freshness = freshnessSignal(blog, ctx);
        double author = authorSignal(blog, ctx);
        return TYPE_WEIGHT * typeSignal
                + QUALITY_WEIGHT * quality
                + FRESHNESS_WEIGHT * freshness
                + AUTHOR_WEIGHT * author;
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
        return "interest";
    }

    private double typeSignal(Blog blog, RankingContext ctx) {
        if (ctx.getShopTypeByShopId() == null || ctx.getTypeAffinity() == null
                || blog.getShopId() == null) {
            return NEUTRAL_TYPE_AFFINITY;
        }
        Long typeId = ctx.getShopTypeByShopId().get(blog.getShopId());
        if (typeId == null) {
            return NEUTRAL_TYPE_AFFINITY;
        }
        return ctx.getTypeAffinity().getOrDefault(typeId, NEUTRAL_TYPE_AFFINITY);
    }

    private double qualitySignal(Blog blog) {
        int liked = blog.getLiked() != null ? blog.getLiked() : 0;
        return liked / (liked + QUALITY_SMOOTHING);
    }

    private double freshnessSignal(Blog blog, RankingContext ctx) {
        if (blog.getCreateTime() == null) {
            return 0D;
        }
        LocalDateTime now = ctx.getNow() != null ? ctx.getNow() : LocalDateTime.now(ZoneOffset.UTC);
        long hours = Math.max(0, Duration.between(blog.getCreateTime(), now).toHours());
        return 1D / (1D + hours / FRESHNESS_HALF_LIFE_HOURS);
    }

    private double authorSignal(Blog blog, RankingContext ctx) {
        if (blog.getUserId() == null || ctx.getAuthorAffinity() == null) {
            return 0D;
        }
        return ctx.getAuthorAffinity().getOrDefault(blog.getUserId(), 0D);
    }
}
