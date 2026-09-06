package com.hmdp.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 店铺评价聚合统计（表 tb_shop_review_stat，迁移 V12 创建，主键即 shop_id，与店铺一对一）。
 *
 * 独立成表而不在 tb_shop 上加列：tb_shop 被多个项目共享，加列会让别的项目
 * 的实体映射面对陌生字段。平均分 = total_score / review_count：
 * 存"评分总和"而不是"平均分"，加减评价时只更新两个整数列，
 * 不会像"旧平均分反推新平均分"那样累积浮点误差。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_shop_review_stat")
public class ShopReviewStat implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 店铺 ID，主键即一对一。 */
    @TableId(value = "shop_id", type = com.baomidou.mybatisplus.annotation.IdType.INPUT)
    private Long shopId;

    /** 有效评价数（创建 +1，删除 -1）。 */
    private Integer reviewCount;

    /** 评分总和（1 星加 1，5 星加 5；删除评价时减回去）。 */
    private Long totalScore;

    /** 更新时间（数据库 ON UPDATE 维护）。 */
    private LocalDateTime updateTime;
}
