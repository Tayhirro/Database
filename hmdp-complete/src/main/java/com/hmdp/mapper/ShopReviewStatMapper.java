package com.hmdp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmdp.entity.ShopReviewStat;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 店铺评价统计表（tb_shop_review_stat）Mapper。
 * 聚合列的增减全部走原子 UPDATE（review_count = review_count + 1 这类写法），
 * 并发评价不会互相覆盖。
 */
public interface ShopReviewStatMapper extends BaseMapper<ShopReviewStat> {

    /**
     * 新增一条评价时累加统计：评价数 +1、评分总和 +rating。
     * 只有统计行已存在时才生效（返回 1）；不存在返回 0，由 Service 层改走插入。
     */
    @Update("UPDATE tb_shop_review_stat SET review_count = review_count + 1, total_score = total_score + #{rating} " +
            "WHERE shop_id = #{shopId}")
    int incrementOnCreate(@Param("shopId") Long shopId, @Param("rating") int rating);

    /**
     * 删除评价时回退统计：评价数 -1（不低于 0）、评分总和 -rating（不低于 0）。
     */
    @Update("UPDATE tb_shop_review_stat SET review_count = GREATEST(review_count - 1, 0), " +
            "total_score = GREATEST(total_score - #{rating}, 0) WHERE shop_id = #{shopId}")
    int decrementOnDelete(@Param("shopId") Long shopId, @Param("rating") int rating);
}
