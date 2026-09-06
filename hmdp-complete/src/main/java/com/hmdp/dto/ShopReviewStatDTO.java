package com.hmdp.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 店铺评价统计视图（GET /shop-review/stat/{shopId}）。
 * 平均分 = 评分总和 / 评价数，保留 1 位小数；没有评价时全部为 0。
 */
@Data
public class ShopReviewStatDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 店铺 ID。 */
    private Long shopId;

    /** 有效评价数。 */
    private Long reviewCount;

    /** 平均评分（1~5，1 位小数），无评价时 0.0。 */
    private Double averageScore;
}
