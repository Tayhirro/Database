-- 店铺评价域：评价主表 + 聚合统计表。
-- 统计独立成表而不在 tb_shop 上加列，避免影响共享同一张店铺表的其他项目。
-- total_score 存评分总和：平均分 = total_score / review_count，
-- 加减评价时只更新这两个列，不用“旧平均分反推”，避免浮点漂移。
CREATE TABLE IF NOT EXISTS `tb_shop_review` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `shop_id` BIGINT UNSIGNED NOT NULL COMMENT '被评价的店铺ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '评价人用户ID',
    `rating` TINYINT UNSIGNED NOT NULL COMMENT '评分 1-5 星',
    `content` VARCHAR(500) NOT NULL COMMENT '评价文字',
    `images` VARCHAR(2000) NULL COMMENT '评价图片URL，多张以逗号分隔，最多9张',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_shop_user` (`shop_id`, `user_id`) USING BTREE,
    KEY `idx_shop_cursor` (`shop_id`, `create_time`, `id`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '店铺评价表';

CREATE TABLE IF NOT EXISTS `tb_shop_review_stat` (
    `shop_id` BIGINT UNSIGNED NOT NULL COMMENT '店铺ID，主键即一对一',
    `review_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '有效评价数',
    `total_score` BIGINT NOT NULL DEFAULT 0 COMMENT '评分总和，平均分 = total_score / review_count',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`shop_id`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '店铺评价聚合统计表';
