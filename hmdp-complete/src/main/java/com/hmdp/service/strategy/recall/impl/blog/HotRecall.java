package com.hmdp.service.strategy.recall.impl.blog;

/*
 * 现实业务背景：兴趣召回和协同过滤都依赖点赞历史，新用户两条通道都是空的；
 * 需要一条不依赖任何个人历史的通道兜底，保证推荐页永远有内容。
 * 实际触发：for_you 模式的多路召回调用本通道，返回近 7 天的全站热门博客。
 */

import com.hmdp.config.RecommendProperties;
import com.hmdp.mapper.RecommendQueryMapper;
import com.hmdp.service.strategy.recall.RecallContext;
import com.hmdp.service.strategy.recall.RecallStrategy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 热门召回通道（注册名 "hot"），推荐系统的冷启动兜底。
 *
 * 召回逻辑：{@link RecommendQueryMapper}（推荐召回专用查询 Mapper）的
 * selectHotBlogIds 一条 SQL——最近 7 天（UTC_TIMESTAMP 回溯）内点赞数最高的博客，
 * ORDER BY liked DESC, id DESC 取前 quota-hot（默认 60）条。
 *
 * 三个通道的名额关系（以候选池 200 为例）：for-you 80 + interest 40 + cf 20 + hot 60 = 200；
 * 冷启动用户 interest/cf 为空时，实际候选池变小（可能只剩 for-you 发现路 + hot 60 条），
 * 排序与快照逻辑按实际条数工作，不会补位凑满——这是简单可解释的取舍。
 *
 * 边界条件：不使用 maxTime/lastId 翻页边界（全局热度排名与时间边界语义不匹配）。
 */
@Component
public class HotRecall implements RecallStrategy {

    private final RecommendQueryMapper recommendQueryMapper;
    private final RecommendProperties recommendProperties;

    public HotRecall(RecommendQueryMapper recommendQueryMapper, RecommendProperties recommendProperties) {
        this.recommendQueryMapper = recommendQueryMapper;
        this.recommendProperties = recommendProperties;
    }

    @Override
    public List<Long> recall(RecallContext ctx) {
        List<Long> ids = recommendQueryMapper.selectHotBlogIds(recommendProperties.getQuotaHot());
        return ids == null ? Collections.emptyList() : new ArrayList<>(ids);
    }

    @Override
    public String getStrategyName() {
        return "hot";
    }
}
