package com.hmdp.service.strategy.ranking.impl;

/*
 * 现实业务背景：用户打开关注流时通常希望先看到关注作者最新发布的内容。
 * 实际触发：following 模式完成关注召回后调用本策略，按发布时间降序、同时间按 blogId 降序排列。
 *
 * 实现要点：
 * 1. score 就是博客发布时间 createTime 转成的 UTC 毫秒时间戳（toEpochMilli），
 *    所以"分数高 = 发布得晚 = 内容更新"；createTime 为 null 时记 0 分（排最后）。
 *    注意这里的分数是时间戳量级，和 WeightedRankingStrategy 那种 0~1 的分数没有可比性。
 * 2. rank() 按分数降序排；两篇博客发布时间完全相同（同一毫秒）时，再按 blogId 降序，
 *    保证顺序稳定可复现，翻页时不因排序抖动出现重复或遗漏。
 * 3. 策略注册名为 "time"，由 FeedMode 的 FOLLOWING 模式指定（following -> "time"）。
 * 4. 本策略完全不读 {@link RankingContext}（排序输入信号：用户、当前时间、作者亲和度等），
 *    关注流对所有用户都是同一套"最新优先"规则。
 */

import com.hmdp.entity.Blog;
import com.hmdp.service.strategy.ranking.RankingContext;
import com.hmdp.service.strategy.ranking.RankingStrategy;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;

@Component
public class SimpleTimeRankingStrategy implements RankingStrategy<Blog> {

    @Override
    public double score(Blog blog, RankingContext ctx) {
        if (blog.getCreateTime() == null) return 0;
        return blog.getCreateTime().toInstant(ZoneOffset.UTC).toEpochMilli();
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
        return "time";
    }
}
