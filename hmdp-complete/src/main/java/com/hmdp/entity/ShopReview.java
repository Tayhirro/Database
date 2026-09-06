package com.hmdp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 店铺评价（表 tb_shop_review，迁移 V12 创建）。
 *
 * 一人一店一评：UNIQUE(shop_id, user_id) 保证同一用户对同一店铺只有一条有效评价，
 * 重复提交会被数据库唯一键拦截；删除是物理删除（评价少、无审计需求），
 * 删除后允许重新评价。评分与文字都必填，图片可选。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_shop_review")
public class ShopReview implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键，自增。 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 被评价的店铺 ID（tb_shop.id）。 */
    private Long shopId;

    /** 评价人用户 ID（tb_user.id）。 */
    private Long userId;

    /** 评分，1~5 星，Service 层校验。 */
    private Integer rating;

    /** 评价文字，最长 500 字符，Service 层校验。 */
    private String content;

    /** 评价图片 URL，逗号分隔，最多 9 张，可为空。 */
    private String images;

    /** 评价时间。 */
    private LocalDateTime createTime;

    /** 更新时间（数据库 ON UPDATE 维护）。 */
    private LocalDateTime updateTime;
}
