package com.hmdp.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 返回给前端的店铺评价视图（列表和统计页共用）。
 *
 * 不直接返回 ShopReview 实体：作者昵称/头像是批量补充出来的展示字段，
 * 不是评价表的列；数据库以后加列也不会泄露到接口。
 */
@Data
public class ShopReviewDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 评价 ID。 */
    private Long id;

    /** 店铺 ID。 */
    private Long shopId;

    /** 评价人用户 ID。 */
    private Long userId;

    /** 评分 1~5。 */
    private Integer rating;

    /** 评价文字。 */
    private String content;

    /** 评价图片 URL 数组（后端已按逗号拆好），无图时为空数组。 */
    private java.util.List<String> images;

    /** 评价时间。 */
    private LocalDateTime createTime;

    /** 评价人昵称（批量补充，用户可能已注销，此时为 null）。 */
    private String authorName;

    /** 评价人头像（批量补充）。 */
    private String authorIcon;
}
