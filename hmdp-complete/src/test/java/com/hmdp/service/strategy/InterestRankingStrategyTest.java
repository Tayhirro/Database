package com.hmdp.service.strategy;

import com.hmdp.entity.Blog;
import com.hmdp.service.strategy.ranking.RankingContext;
import com.hmdp.service.strategy.ranking.impl.InterestRankingStrategy;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 个性化兴趣排序策略测试：验证公式方向（类型亲和度优先）、缺省中性值和 id 平手规则。
 */
class InterestRankingStrategyTest {

    private final InterestRankingStrategy strategy = new InterestRankingStrategy();

    private RankingContext context(Map<Long, Double> typeAffinity, Map<Long, Long> shopTypes) {
        return RankingContext.builder()
                .currentUserId(7L)
                .now(LocalDateTime.now())
                .authorAffinity(new HashMap<>())
                .authorInteractionCount(new HashMap<>())
                .typeAffinity(typeAffinity)
                .shopTypeByShopId(shopTypes)
                .build();
    }

    private Blog blog(long id, long shopId, int liked, LocalDateTime createTime) {
        Blog blog = new Blog();
        blog.setId(id);
        blog.setShopId(shopId);
        blog.setLiked(liked);
        blog.setComments(0);
        blog.setUserId(99L);
        blog.setCreateTime(createTime);
        return blog;
    }

    @Test
    void preferredTypeRanksAboveNeutral() {
        Map<Long, Double> affinity = new HashMap<>();
        affinity.put(1L, 1.0); // 用户最爱类型 1
        Map<Long, Long> shopTypes = new HashMap<>();
        shopTypes.put(100L, 1L); // 店铺 100 属于类型 1
        shopTypes.put(200L, 9L); // 店铺 200 属于无关类型 9

        ArrayList<Blog> blogs = new ArrayList<>();
        // 无关类型、高赞但发布 2 天（新鲜度衰减）；偏爱类型零赞且新鲜
        blogs.add(blog(2, 200L, 50, LocalDateTime.now().minusDays(2)));
        blogs.add(blog(1, 100L, 0, LocalDateTime.now()));

        strategy.rank(blogs, context(affinity, shopTypes));

        assertEquals(1L, blogs.get(0).getId(), "类型亲和度应把偏爱类型的博客排到前面");
    }

    @Test
    void coldStartUsesNeutralTypeScoreAndStillOrdersByQuality() {
        ArrayList<Blog> blogs = new ArrayList<>();
        blogs.add(blog(1, 100L, 0, LocalDateTime.now()));
        blogs.add(blog(2, 200L, 100, LocalDateTime.now()));

        strategy.rank(blogs, context(new HashMap<>(), new HashMap<>()));

        assertEquals(2L, blogs.get(0).getId(), "无画像时质量分（点赞）主导排序");
    }

    @Test
    void sameScoreBreaksTieByBlogIdDesc() {
        Map<Long, Double> affinity = new HashMap<>();
        Map<Long, Long> shopTypes = new HashMap<>();
        ArrayList<Blog> blogs = new ArrayList<>();
        blogs.add(blog(1, 100L, 0, LocalDateTime.now()));
        blogs.add(blog(2, 100L, 0, LocalDateTime.now()));

        strategy.rank(blogs, context(affinity, shopTypes));

        assertEquals(2L, blogs.get(0).getId(), "分数相同时按博客 ID 降序");
        assertTrue(strategy.score(blogs.get(0), context(affinity, shopTypes)) >= 0);
        assertTrue(strategy.score(blogs.get(0), context(affinity, shopTypes)) <= 1.0);
    }
}
