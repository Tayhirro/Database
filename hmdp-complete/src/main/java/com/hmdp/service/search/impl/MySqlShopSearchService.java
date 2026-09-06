package com.hmdp.service.search.impl;

/*
 * 现实业务背景：当前数据量较小时，用户输入“火锅”等店名片段，由 MySQL 提供第一版可验证的搜索基线。
 * 实际触发：ShopSearchService.search() 收到标准化关键词后对 tb_shop 的 name 字段做 LIKE 匹配
 * （当前只匹配店名，不匹配 address 等其他字段），并组装搜索卡片。
 *
 * 设计精华：
 * 1. MySQL 是当前搜索数据源；搜索引擎以后只作为可重建索引，不能反过来成为店铺真相源。
 * 2. 空白关键词直接返回空页，避免一次误操作退化为“分页浏览全表”；
 *    关键词里的 %、_、\ 会转义成普通字符，防止用户输入 LIKE 通配符扩大匹配范围。
 * 3. 固定按 id 升序排序，避免同一份数据翻页时顺序漂移；页码从 1 起，每页数量上限 10
 *    （店铺独立 Tab 未传 pageSize 时即用每页 10 条）。
 * 4. Shop Entity 只停留在持久层，本服务统一转换为 ShopSearchItemDTO
 *    （店铺搜索卡片：id、name、typeId、images、area、address、均单价、销量、评价数、评分、营业时间）。
 */

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmdp.dto.PageResultDTO;
import com.hmdp.dto.SearchQuery;
import com.hmdp.dto.ShopSearchItemDTO;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.search.ShopSearchService;
import com.hmdp.utils.SystemConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MySqlShopSearchService implements ShopSearchService {

    private final ShopMapper shopMapper;

    @Override
    public PageResultDTO<ShopSearchItemDTO> search(String keyword, Integer current) {
        return searchInternal(keyword, current, SystemConstants.MAX_PAGE_SIZE);
    }

    /** 统一搜索会传入较小的分组配额（默认每页 5 条）；店铺独立 Tab 未指定 pageSize 时仍沿用默认每页 10 条。 */
    @Override
    public PageResultDTO<ShopSearchItemDTO> search(SearchQuery query) {
        return searchInternal(
                query == null ? null : query.getKeyword(),
                query == null ? null : query.getCurrent(),
                query == null ? null : query.getPageSize()
        );
    }

    private PageResultDTO<ShopSearchItemDTO> searchInternal(
            String keyword,
            Integer current,
            Integer requestedPageSize
    ) {
        int pageNumber = MySqlSearchSupport.normalizePage(current);
        int pageSize = MySqlSearchSupport.normalizePageSize(
                requestedPageSize,
                SystemConstants.MAX_PAGE_SIZE
        );
        String normalizedKeyword = MySqlSearchSupport.normalizeKeyword(keyword);
        if (StrUtil.isBlank(normalizedKeyword)) {
            return PageResultDTO.empty(pageNumber, pageSize);
        }

        LambdaQueryWrapper<Shop> query = new LambdaQueryWrapper<Shop>()
                .like(Shop::getName, escapeLikeKeyword(normalizedKeyword))
                .orderByAsc(Shop::getId);
        Page<Shop> page = shopMapper.selectPage(
                new Page<>(pageNumber, pageSize),
                query
        );

        List<ShopSearchItemDTO> items = page.getRecords().stream()
                .map(this::toSearchItem)
                .collect(Collectors.toList());
        boolean hasMore = page.getCurrent() * page.getSize() < page.getTotal();
        return new PageResultDTO<>(items, page.getCurrent(), page.getSize(), page.getTotal(), hasMore);
    }

    private String escapeLikeKeyword(String keyword) {
        return MySqlSearchSupport.escapeLikeKeyword(keyword);
    }

    private ShopSearchItemDTO toSearchItem(Shop shop) {
        return new ShopSearchItemDTO()
                .setId(shop.getId())
                .setName(shop.getName())
                .setTypeId(shop.getTypeId())
                .setImages(shop.getImages())
                .setArea(shop.getArea())
                .setAddress(shop.getAddress())
                .setAvgPrice(shop.getAvgPrice())
                .setSold(shop.getSold())
                .setComments(shop.getComments())
                .setScore(shop.getScore())
                .setOpenHours(shop.getOpenHours());
    }
}
