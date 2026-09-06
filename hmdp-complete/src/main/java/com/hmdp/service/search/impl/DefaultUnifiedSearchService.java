package com.hmdp.service.search.impl;

/*
 * 现实业务背景：前端只有一个搜索框，但“综合”页需要一次请求同时展示店铺、笔记和用户三个分组。
 * 实际触发：GET /search；未指定 scope（SearchQuery.scopes 为空）时召回全部已注册域
 * （共执行 3 个子查询，对应 SHOP/BLOG/USER 三个 MySQL 分页 SELECT），指定 Tab 时只召回对应域（1 个子查询）。
 *
 * 设计精华：
 * 1. 第一阶段采用确定性路由：请求 scope 决定调用哪些垂直服务，不伪装成已经具备 AI 意图识别。
 * 2. 统一层只编排和分组，不写任何店铺、博客或用户 SQL；Spring 启动时把容器里所有
 *    {@link VerticalSearchService}（各搜索域的垂直搜索接口）实现按 scope 收进注册表，
 *    同一 scope 出现两个实现会在启动时直接失败，新域加一个实现类即自动接入。
 * 3. 按 SearchScope 枚举顺序稳定返回分组；“综合”是聚合视图，不是一个新的业务域。
 * 4. 3 个子查询在当前请求线程里串行执行，没有并行、超时或熔断；任一垂直域失败时
 *    本次请求整体失败，超时、隔离和部分降级留到故障增强阶段。
 * 5. 统一层负责参数标准化：关键词去首尾空格（超过 64 字符报错）、页码从 1 起、
 *    每页数量未指定时取 5（各域上限 10），scopes 为空时展开为全部已注册域。
 * 6. 搜索成功返回后执行一次 ZINCRBY search:hot: 1 关键词 记录热词（ZSet 成员=搜索词、
 *    分数=累计搜索次数，联想接口空关键词兜底时读它）；只统计非空关键词，Redis 异常
 *    只记 warn 日志，热词统计失败绝不让搜索主流程跟着失败。
 */

import cn.hutool.core.util.StrUtil;
import com.hmdp.dto.PageResultDTO;
import com.hmdp.dto.SearchQuery;
import com.hmdp.dto.SearchResultItemDTO;
import com.hmdp.dto.SearchSectionDTO;
import com.hmdp.dto.UnifiedSearchResultDTO;
import com.hmdp.service.search.SearchScope;
import com.hmdp.service.search.UnifiedSearchService;
import com.hmdp.service.search.VerticalSearchService;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class DefaultUnifiedSearchService implements UnifiedSearchService {

    private final Map<SearchScope, VerticalSearchService<? extends SearchResultItemDTO>> servicesByScope;
    private final StringRedisTemplate stringRedisTemplate;

    public DefaultUnifiedSearchService(
            List<VerticalSearchService<? extends SearchResultItemDTO>> verticalSearchServices,
            StringRedisTemplate stringRedisTemplate
    ) {
        this.servicesByScope = new EnumMap<>(SearchScope.class);
        for (VerticalSearchService<? extends SearchResultItemDTO> service : verticalSearchServices) {
            VerticalSearchService<? extends SearchResultItemDTO> previous =
                    servicesByScope.put(service.scope(), service);
            if (previous != null) {
                throw new IllegalStateException("搜索域存在重复实现: " + service.scope());
            }
        }
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public UnifiedSearchResultDTO search(SearchQuery query) {
        SearchQuery normalizedQuery = normalizeQuery(query);
        Set<SearchScope> requestedScopes = normalizedQuery.getScopes();
        List<SearchSectionDTO> sections = new ArrayList<>(requestedScopes.size());

        for (SearchScope scope : SearchScope.values()) {
            if (!requestedScopes.contains(scope)) {
                continue;
            }
            VerticalSearchService<? extends SearchResultItemDTO> service = servicesByScope.get(scope);
            if (service == null) {
                throw new IllegalStateException("搜索域缺少实现: " + scope);
            }
            sections.add(toSection(scope, service.search(normalizedQuery)));
        }

        UnifiedSearchResultDTO result = new UnifiedSearchResultDTO()
                .setNormalizedKeyword(normalizedQuery.getKeyword())
                .setSections(sections);
        recordHotKeyword(normalizedQuery.getKeyword());
        return result;
    }

    /**
     * 搜索成功返回后把本次关键词计入热词：ZINCRBY search:hot: 1 关键词，
     * ZSet 成员是搜索词、分数是累计搜索次数（例如搜两次“火锅”后分数为 2），
     * 首次出现的成员由 ZINCRBY 自动创建并置 1。联想接口空关键词兜底时读这个 ZSet。
     * 只在关键词非空时统计；Redis 异常只记 warn 日志，不让热词统计影响搜索主流程。
     */
    private void recordHotKeyword(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return;
        }
        try {
            stringRedisTemplate.opsForZSet().incrementScore(RedisConstants.SEARCH_HOT_KEY, keyword, 1);
        } catch (Exception e) {
            log.warn("搜索热词统计失败, key={}", RedisConstants.SEARCH_HOT_KEY, e);
        }
    }

    private SearchQuery normalizeQuery(SearchQuery query) {
        String keyword = MySqlSearchSupport.normalizeKeyword(query == null ? null : query.getKeyword());
        int pageNumber = MySqlSearchSupport.normalizePage(query == null ? null : query.getCurrent());
        int pageSize = MySqlSearchSupport.normalizePageSize(
                query == null ? null : query.getPageSize(),
                SystemConstants.DEFAULT_PAGE_SIZE
        );
        Set<SearchScope> requestedScopes = query == null ? null : query.getScopes();
        Set<SearchScope> normalizedScopes = requestedScopes == null || requestedScopes.isEmpty()
                ? EnumSet.allOf(SearchScope.class)
                : EnumSet.copyOf(requestedScopes);

        return new SearchQuery()
                .setKeyword(keyword == null ? "" : keyword)
                .setScopes(normalizedScopes)
                .setCurrent(pageNumber)
                .setPageSize(pageSize)
                .setCityCode(query == null ? null : query.getCityCode())
                .setLongitude(query == null ? null : query.getLongitude())
                .setLatitude(query == null ? null : query.getLatitude());
    }

    private SearchSectionDTO toSection(
            SearchScope scope,
            PageResultDTO<? extends SearchResultItemDTO> page
    ) {
        List<SearchResultItemDTO> items = new ArrayList<>(page.getList());
        return new SearchSectionDTO()
                .setScope(scope)
                .setItems(items)
                .setTotal(page.getTotal())
                .setHasMore(page.isHasMore());
    }
}
