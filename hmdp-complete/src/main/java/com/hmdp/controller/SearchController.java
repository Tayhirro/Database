package com.hmdp.controller;

import com.hmdp.dto.PageResultDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.SearchQuery;
import com.hmdp.dto.SearchSuggestionDTO;
import com.hmdp.dto.ShopSearchItemDTO;
import com.hmdp.service.search.BlogSearchService;
import com.hmdp.service.search.SearchScope;
import com.hmdp.service.search.SearchSuggestionService;
import com.hmdp.service.search.ShopSearchService;
import com.hmdp.service.search.UnifiedSearchService;
import com.hmdp.service.search.UserSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 统一搜索入口。
 *
 * 搜索属于跨店铺、博客和用户的独立读取能力，不归属于任一业务实体的 CRUD Controller。
 * 第一阶段已落地三个 MySQL 垂直搜索与确定性统一编排；中文分词、语义检索和自动意图路由尚未实现。
 *
 * 设计要点：
 * 
 *     1. 新客户端只依赖 {@code /search/**} 合同，底层从 MySQL 升级到 Elasticsearch 时不改页面路由。
 *     2. 每类内容使用独立搜索 Service，避免未来把所有检索逻辑重新堆进一个大函数。
 *     3. 旧路径只做兼容适配，不再拥有查询逻辑；移除前应先确认所有旧客户端已经迁移。
 * 
 */
@RestController
@RequiredArgsConstructor
public class SearchController {

    private final ShopSearchService shopSearchService;
    private final BlogSearchService blogSearchService;
    private final UserSearchService userSearchService;
    private final UnifiedSearchService unifiedSearchService;
    private final SearchSuggestionService searchSuggestionService;

    /**
     * 一个搜索框的统一入口。scope 为空表示“综合”；传 BLOG、SHOP 或 USER 时只返回所选 Tab。
     */
    @GetMapping("/search")
    public Result search(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "scope", required = false) Set<SearchScope> scopes,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize
    ) {
        SearchQuery query = new SearchQuery()
                .setKeyword(keyword)
                .setScopes(scopes)
                .setCurrent(current)
                .setPageSize(pageSize);
        return Result.ok(unifiedSearchService.search(query));
    }

    /**
     * 搜索框输入联想：随输入实时返回最多 10 条下拉候选。
     *
     * 返回 {@link SearchSuggestionDTO}（搜索联想候选条目：可回填 text，加上可选的
     * scope 和 targetId）列表；keyword 参数名与 /search 主接口保持一致。
     * keyword 非空时返回店铺、博客、用户的前缀匹配候选，
     * 为空时返回最多 10 个热词兜底（text 为搜索词，scope 和 targetId 为空）。
     */
    @GetMapping("/search/suggest")
    public Result suggest(
            @RequestParam(value = "keyword", defaultValue = "") String keyword
    ) {
        return Result.ok(searchSuggestionService.suggest(new SearchQuery().setKeyword(keyword)));
    }

    /**
     * 用户在店铺列表输入名称片段并提交搜索。
     */
    @GetMapping("/search/shops")
    public Result searchShops(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "current", defaultValue = "1") Integer current
    ) {
        return Result.ok(shopSearchService.search(keyword, current));
    }

    /** 用户切换到“笔记”Tab 后，只分页查询公开笔记。 */
    @GetMapping("/search/blogs")
    public Result searchBlogs(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        return Result.ok(blogSearchService.search(new SearchQuery()
                .setKeyword(keyword)
                .setCurrent(current)
                .setPageSize(pageSize)));
    }

    /** 用户切换到“用户”Tab 后，只按公开昵称分页查询用户。 */
    @GetMapping("/search/users")
    public Result searchUsers(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        return Result.ok(userSearchService.search(new SearchQuery()
                .setKeyword(keyword)
                .setCurrent(current)
                .setPageSize(pageSize)));
    }

    /**
     * 旧版店铺名称搜索兼容入口。
     *
     * 继续返回 records 数组和顶层 total，避免旧页面因响应结构变化失效；
     * 新代码不得继续调用该路径。
     */
    @Deprecated
    @GetMapping("/shop/of/name")
    public Result searchShopsLegacy(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "current", defaultValue = "1") Integer current
    ) {
        PageResultDTO<ShopSearchItemDTO> page = shopSearchService.search(name, current);
        return Result.ok(page.getList(), page.getTotal());
    }
}
