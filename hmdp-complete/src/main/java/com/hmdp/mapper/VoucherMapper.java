package com.hmdp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmdp.entity.Voucher;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券表（tb_voucher）Mapper。
 */
public interface VoucherMapper extends BaseMapper<Voucher> {

    List<Voucher> queryVoucherOfShop(@Param("shopId") Long shopId);

    /**
     * 管理端字段白名单更新：只允许改标题、副标题、规则、状态与活动时间。
     * shopId、type、payValue、actualValue、stock 不在白名单里——
     * 归属店铺和面值参与订单语义，库存走独立条件接口，不能被实体整体覆盖。
     *
     * @return 影响行数：1 修改成功；0 表示券不存在或全部字段为 null（无列被更新）
     */
    @Update("<script>" +
            "UPDATE tb_voucher " +
            "<set>" +
            "  <if test='title != null'>title = #{title},</if>" +
            "  <if test='subTitle != null'>sub_title = #{subTitle},</if>" +
            "  <if test='rules != null'>rules = #{rules},</if>" +
            "  <if test='status != null'>status = #{status},</if>" +
            "  <if test='beginTime != null'>begin_time = #{beginTime},</if>" +
            "  <if test='endTime != null'>end_time = #{endTime},</if>" +
            "</set>" +
            "WHERE id = #{voucherId}" +
            "</script>")
    int updateAdminFields(@Param("voucherId") Long voucherId,
                          @Param("title") String title,
                          @Param("subTitle") String subTitle,
                          @Param("rules") String rules,
                          @Param("status") Integer status,
                          @Param("beginTime") LocalDateTime beginTime,
                          @Param("endTime") LocalDateTime endTime);
}
