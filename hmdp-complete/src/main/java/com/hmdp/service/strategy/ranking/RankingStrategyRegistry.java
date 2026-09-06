package com.hmdp.service.strategy.ranking;

/*
 * 现实业务背景：系统启动后需要把多个排序实现按名称登记，Feed 请求才能根据产品模式稳定选择。
 * 实际触发：Spring 初始化时收集所有 RankingStrategy；BlogFeedService 每次重建 Feed 时按名称查询。
 *
 * 工作方式：
 * 1. 字段 strategies 由 Spring 注入容器里全部 {@link RankingStrategy}（排序策略接口）实现，
 *    当前是 simple / time / weighted 三个 @Component。
 * 2. init() 在 Bean 创建完成后（@PostConstruct）把列表转成 "策略名 -> 实现" 的 Map，
 *    策略名来自各实现的 getStrategyName()。
 * 3. getStrategy() 按名字取策略；名字不存在时回退到 "simple"，保证调用方永远拿得到非空策略。
 *    例如 FeedMode 给 following 映射 "time"、给 for_you 映射 "weighted"，
 *    即使将来加新模式配错名字，Feed 也不会因为找不到策略而报错，只是退回基线排序。
 * 4. getDefaultStrategy() 显式取 "simple"；getStrategyNames() 返回全部已注册策略名，便于排查注册情况。
 */

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RankingStrategyRegistry {

    @Resource
    private List<RankingStrategy<?>> strategies;

    private Map<String, RankingStrategy<?>> strategyMap;

    @PostConstruct
    public void init() {
        strategyMap = strategies.stream()
                .collect(Collectors.toMap(RankingStrategy::getStrategyName, Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public <T> RankingStrategy<T> getStrategy(String strategyName) {
        RankingStrategy<?> strategy = strategyMap.get(strategyName);
        if (strategy == null) {
            strategy = strategyMap.get("simple");
        }
        return (RankingStrategy<T>) strategy;
    }

    public <T> RankingStrategy<T> getDefaultStrategy() {
        return getStrategy("simple");
    }

    public List<String> getStrategyNames() {
        return strategies.stream()
                .map(RankingStrategy::getStrategyName)
                .collect(Collectors.toList());
    }
}
