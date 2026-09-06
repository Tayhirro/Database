package com.hmdp.service.strategy.recall.impl.blog;

/*
 * 现实业务背景：只靠"点赞过的作者"召回会把推荐面锁死在已有兴趣圈里；
 * 用户点赞行为背后还有第二层信号——店铺类型（美食/休闲/KTV 等），值得单独开一条通道。
 * 实际触发：for_you 模式的多路召回调用本通道，按用户点赞历史里最偏爱的前 3 个店铺类型拉候选。
 */

import com.hmdp.config.RecommendProperties;
import com.hmdp.mapper.RecommendQueryMapper;
import com.hmdp.service.strategy.recall.RecallContext;
import com.hmdp.service.strategy.recall.RecallStrategy;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 兴趣召回通道（注册名 "interest"）。
 *
 * 召回逻辑：{@link RecommendQueryMapper}（推荐召回专用查询 Mapper）的
 * selectInterestBlogIds 一条 SQL 完成——内层按"用户点赞过的博客归属店铺类型"聚合取 TOP3 类型，
 * 外层返回这些类型下用户没点过赞、也不是自己写的博客，按 create_time DESC, id DESC 取前
 * quota-interest（默认 40）条。
 *
 * 具体例子：用户给 5 篇美食店博客、2 篇 KTV 博客点过赞，TOP3 类型就是美食/KTV（还有空位），
 * 候选就是这些类型下他没看过的最新博客。
 *
 * 冷启动：没有任何点赞历史时 TOP3 类型为空、SQL 返回空列表，本通道自然让位，
 * 候选池由 hot 通道（热门召回）兜底。
 *
 * 翻页边界与 FollowFeedRecall 一致：maxTime =（上一页最后一条的发布时间 UTC 毫秒）、
 * lastId =（其博客 ID），条件是"发布时间更早，或时间相同但 id 更小"。
 * 热门通道（hot）不使用边界：全局热度排名与翻页时间边界语义不匹配，跳过是刻意设计。
 */
@Component
public class InterestRecall implements RecallStrategy {

    private final RecommendQueryMapper recommendQueryMapper;
    private final RecommendProperties recommendProperties;

    public InterestRecall(RecommendQueryMapper recommendQueryMapper, RecommendProperties recommendProperties) {
        this.recommendQueryMapper = recommendQueryMapper;
        this.recommendProperties = recommendProperties;
    }

    @Override
    public List<Long> recall(RecallContext ctx) {
        if (ctx.getUserId() == null) {
            return Collections.emptyList();
        }
        LocalDateTime maxTime = toUtcTime(ctx.getMaxTime());
        Long lastId = LastIdSupport.lastIdOf(ctx);
        List<Long> ids = recommendQueryMapper.selectInterestBlogIds(
                ctx.getUserId(), maxTime, lastId, recommendProperties.getQuotaInterest());
        return ids == null ? Collections.emptyList() : new ArrayList<>(ids);
    }

    static LocalDateTime toUtcTime(Long epochMilli) {
        if (epochMilli == null || epochMilli <= 0) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneOffset.UTC);
    }

    @Override
    public String getStrategyName() {
        return "interest";
    }
}
