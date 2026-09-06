package com.hmdp.service.strategy.recall;

/*
 * 现实业务背景：系统需要把 follow、for-you 等召回实现按稳定名称注册，避免依赖 Spring Bean 枚举顺序。
 * 实际触发：Spring 启动时收集全部 {@link RecallStrategy}（召回策略接口：给定上下文，返回一批候选博客 ID）；
 * {@link RecallOrchestrator}（召回编排器：按通道调用各策略并去重合并）在 Feed 请求中按产品模式查找。
 *
 * 工作方式：
 * 1. strategies 由 Spring 注入容器里全部召回实现，当前是 "follow"（FollowFeedRecall）和
 *    "for-you"（ForYouRecall）两个 @Component。
 * 2. init() 在 Bean 创建完成后（@PostConstruct）把列表转成 "通道名 -> 实现" 的 Map。
 *    注意 Collectors.toMap 遇到重复通道名会直接抛异常，相当于启动时就能暴露命名冲突。
 * 3. 与排序侧的 RankingStrategyRegistry 不同：getStrategy() 找不到名字时返回 null 而不是回退默认值，
 *    由编排器决定跳过（RecallOrchestrator.multiRecall 对 null 直接 continue）。
 * 4. getAllStrategies() 返回全部实现，供 multiRecallAll 做"全通道召回"。
 */

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RecallStrategyRegistry {

    @Resource
    private List<RecallStrategy> strategies;

    private Map<String, RecallStrategy> strategyMap;

    @PostConstruct
    public void init() {
        strategyMap = strategies.stream()
                .collect(Collectors.toMap(RecallStrategy::getStrategyName, Function.identity()));
    }

    public RecallStrategy getStrategy(String strategyName) {
        return strategyMap.get(strategyName);
    }

    public List<RecallStrategy> getAllStrategies() {
        return strategies;
    }
}
