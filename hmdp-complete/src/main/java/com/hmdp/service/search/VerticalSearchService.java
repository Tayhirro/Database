package com.hmdp.service.search;

/*
 * 现实业务背景：统一搜索（{@link UnifiedSearchService}，聚合店铺/笔记/用户结果的 /search 入口）
 * 把同一个 Query 分发给不同供给域时，需要一个共同扩展点。
 * 实际触发：统一搜索根据请求 scope（{@link SearchScope}，枚举出 SHOP/BLOG/USER 三个允许被搜索的域）
 * 选择若干垂直搜索执行；当前店铺、笔记和用户三个域均已接入。
 *
 * 设计精华：
 * 1. 各垂直域平级实现本接口，BlogSearchService（笔记搜索）不继承 ShopSearchService（店铺搜索）。
 * 2. 泛型 T 保留每个域自己的结果 DTO（如店铺的 ShopSearchItemDTO、博客的 BlogCardDTO），
 *    避免制造包含所有字段的万能搜索对象；T 统一实现 SearchResultItemDTO
 *    （只约定返回业务对象公开 ID 的最小合同）。
 * 3. 本接口只定义召回边界；跨域配额、融合和最终排序属于统一编排层。
 */

import com.hmdp.dto.PageResultDTO;
import com.hmdp.dto.SearchQuery;
import com.hmdp.dto.SearchResultItemDTO;

public interface VerticalSearchService<T extends SearchResultItemDTO> {

    /** 返回该实现负责的唯一检索域，供统一搜索注册和路由。 */
    SearchScope scope();

    /** 在本检索域内执行搜索，不负责跨域融合。 */
    PageResultDTO<T> search(SearchQuery query);
}
