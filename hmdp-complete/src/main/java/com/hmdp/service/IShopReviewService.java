package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.dto.Result;
import com.hmdp.dto.ShopReviewCreateRequest;
import com.hmdp.dto.ShopReviewDTO;
import com.hmdp.dto.ShopReviewStatDTO;
import com.hmdp.entity.ShopReview;

/**
 * 店铺评价服务：给店铺打 1~5 星分并留文字/图片评价。
 *
 * 数据模型（迁移 V12）：评价明细存 tb_shop_review（UNIQUE(shop_id, user_id)，一人一店一评），
 * 聚合存 tb_shop_review_stat（review_count 评价数、total_score 评分总和，平均分 = 总和/数量，
 * 存总和而非平均分是为了加减时只动整数列，不累积浮点误差）。
 * 明细与统计在同一次数据库事务里维护，不会出现"评价有了但统计没动"的中间态。
 */
public interface IShopReviewService extends IService<ShopReview> {

    /** 发布评价（POST /shop-review）：一人一店一评，重复提交返回失败；同步累加统计。 */
    Result createReview(ShopReviewCreateRequest request);

    /**
     * 店铺评价列表（GET /shop-review/list/{shopId}）：游标 =（上一页最后一条的 createTime，id），
     * 按"时间倒序、同秒按 id 倒序"翻页，一页默认 10 条、最多 20 条；
     * 整页作者信息批量补齐（1 条 SQL 查作者，不逐条查）。
     */
    Result listByShop(Long shopId, Long lastTime, Long lastId, Integer pageSize);

    /** 删除本人评价（DELETE /shop-review/{id}）：物理删除，同步回退统计，之后可重新评价。 */
    Result deleteReview(Long reviewId);

    /** 店铺评价统计（GET /shop-review/stat/{shopId}）：评价数 + 平均分（1 位小数），无评价全 0。 */
    Result statOfShop(Long shopId);

    /**
     * 组装一页评价 DTO：补充作者昵称/头像。
     * 供列表接口使用；ids 为空时返回空列表。
     */
    java.util.List<ShopReviewDTO> assembleDTOs(java.util.List<ShopReview> reviews);
}
