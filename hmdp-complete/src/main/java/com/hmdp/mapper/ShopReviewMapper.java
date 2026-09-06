package com.hmdp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmdp.entity.ShopReview;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 店铺评价表（tb_shop_review）Mapper。
 */
public interface ShopReviewMapper extends BaseMapper<ShopReview> {

    /**
     * 评价列表的复合游标分页：游标 =（上一页最后一条的 create_time，id），
     * 条件是"发布时间更早，或时间相同但 id 更小"；排序 ORDER BY create_time DESC, id DESC，
     * id 用来同一秒内发布的评价分先后。首页（lastTime 为 null）不带边界条件。
     *
     * @return 最多 limit 条评价，按时间倒序
     */
    @Select("<script>" +
            "SELECT * FROM tb_shop_review " +
            "WHERE shop_id = #{shopId} " +
            "<if test='lastTime != null'>" +
            "AND (create_time &lt; #{lastTime} OR (create_time = #{lastTime} AND id &lt; #{lastId})) " +
            "</if>" +
            "ORDER BY create_time DESC, id DESC " +
            "LIMIT #{limit}" +
            "</script>")
    List<ShopReview> selectPageByShop(@Param("shopId") Long shopId,
                                      @Param("lastTime") LocalDateTime lastTime,
                                      @Param("lastId") Long lastId,
                                      @Param("limit") int limit);
}
