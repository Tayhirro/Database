package com.hmdp.service.search;

/*
 * 现实业务背景：用户尚未点“搜索”时，输入框需要随输入实时下拉返回关键词补全或可直达的店铺/笔记/用户，
 * 让用户少打字、直接命中目标。
 * 实际触发：搜索框输入变化时调用 GET /search/suggest?keyword=...（SearchController 已暴露该端点）；
 * 当前实现类是 MySqlSearchSuggestionService（MySQL 前缀匹配联想 + Redis 热词兜底），
 * 它按店铺、博客、用户三个域各执行一条前缀 SQL，空关键词时返回热词。
 *
 * 设计精华：输入提示与完整搜索分别建模。提示接口（本文件）只返回少量低延迟候选
 * （{@link SearchSuggestionDTO}：可回填的 text，加上可选的所属检索域 scope 和直达对象 ID targetId），
 * 完整搜索（{@link UnifiedSearchService}，统一搜索入口，聚合各垂直域结果）才执行召回、分页和排序。
 */

import com.hmdp.dto.SearchQuery;
import com.hmdp.dto.SearchSuggestionDTO;

import java.util.List;

public interface SearchSuggestionService {

    /** 根据输入片段和城市/位置上下文返回少量建议。 */
    List<SearchSuggestionDTO> suggest(SearchQuery query);
}
