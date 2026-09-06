package com.hmdp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmdp.entity.VoucherOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 秒杀订单表（tb_voucher_order）Mapper。
 */
public interface VoucherOrderMapper extends BaseMapper<VoucherOrder> {

    /**
     * 支付模拟：只有本人未支付（status=1）的订单才能改为已支付（status=2）并记录支付方式与时间。
     * 条件更新负责状态机的合法迁移，两个并发支付只有一个成功。
     *
     * @return 影响行数：1 成功；0 表示订单不存在、不是本人或状态已变化
     */
    @Update("UPDATE tb_voucher_order SET status = 2, pay_type = #{payType}, pay_time = NOW() " +
            "WHERE id = #{orderId} AND user_id = #{userId} AND status = 1")
    int updatePaySimulated(@Param("orderId") Long orderId,
                           @Param("userId") Long userId,
                           @Param("payType") Integer payType);

    /**
     * 取消本人未支付订单：status 1 -> 4（已取消）。
     * 记录不删除，UNIQUE(user_id, voucher_id) 继续阻止同一用户重复抢同一张券。
     *
     * @return 影响行数：1 成功；0 表示订单已支付/已取消或不是本人订单
     */
    @Update("UPDATE tb_voucher_order SET status = 4 WHERE id = #{orderId} AND user_id = #{userId} AND status = 1")
    int cancelOwnOrder(@Param("orderId") Long orderId, @Param("userId") Long userId);
}
