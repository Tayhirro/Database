package com.hmdp.dto;

import lombok.Data;

import java.util.List;

/**
 * 发布店铺评价的请求体（POST /shop-review）。
 *
 * 本项目没有引入 bean-validation，字段约束由 ShopReviewServiceImpl 手动校验：
 * shopId 必填、rating 取 1~5、content 非空且最长 500 字符、images 最多 9 张。
 */
@Data
public class ShopReviewCreateRequest {

    /** 被评价的店铺 ID，必填。 */
    private Long shopId;

    /** 评分，1~5 星。 */
    private Integer rating;

    /** 评价文字，必填，最长 500 字符。 */
    private String content;

    /** 评价图片 URL，最多 9 张，每个 URL 最长 512 字符，可为空。 */
    private List<String> images;
}
