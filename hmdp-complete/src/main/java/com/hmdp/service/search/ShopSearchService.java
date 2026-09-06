package com.hmdp.service.search;

/*
 * 现实业务背景：用户主动输入店铺关键词时，需要独立于店铺详情、缓存和管理命令的检索入口。
 * 实际触发：店铺搜索框调用 GET /search/shops；旧客户端的 GET /shop/of/name 也由搜索 Controller 兼容转发。
 *
 * 设计精华：
 * 1. 搜索是跨内容域能力，不把关键词 SQL 继续揉进 ShopController。
 * 2. Controller 只处理 HTTP 协议，本接口返回搜索 DTO（{@link ShopSearchItemDTO}，
 *    店铺搜索结果卡片：id、name、typeId、images、area、address、评分销量等摘要字段），
 *    数据库实现放在独立适配器中。
 * 3. 未来接 Elasticsearch 时替换实现，不改变前端的搜索合同，也不污染店铺核心 Service。
 */

import com.hmdp.dto.PageResultDTO;
import com.hmdp.dto.SearchQuery;
import com.hmdp.dto.ShopSearchItemDTO;

public interface ShopSearchService extends VerticalSearchService<ShopSearchItemDTO> {

    /** 店铺搜索是统一搜索中的一个垂直域，不是其他内容搜索的父接口。 */
    @Override
    default SearchScope scope() {
        return SearchScope.SHOP;
    }

    /**
     * 按店铺名称关键词分页搜索（当前 MySQL 实现只对 tb_shop 的 name 字段做 LIKE 匹配，
     * 不匹配 address 等其他字段，并按 id 升序保证翻页顺序稳定）。
     *
     * @param keyword 用户输入的关键词；空白关键词返回空页
     * @param current 从 1 开始的页码
     * @return 店铺摘要及明确的分页元数据
     */
    PageResultDTO<ShopSearchItemDTO> search(String keyword, Integer current);

    /**
     * 默认适配保留旧的两参数实现（忽略 pageSize，固定每页 10 条）；
     * 当前 MySQL 实现会覆盖本方法，读取 {@link SearchQuery}（统一搜索的公共查询上下文，
     * 含关键词、检索域、页码、页大小等）里的 pageSize，以支持统一搜索给每个分组分配更小的配额（默认每页 5 条）。
     */
    @Override
    default PageResultDTO<ShopSearchItemDTO> search(SearchQuery query) {
        return search(
                query == null ? null : query.getKeyword(),
                query == null ? 1 : query.getCurrent()
        );
    }
}
