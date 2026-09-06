package com.hmdp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmdp.entity.SeckillVoucher;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 秒杀优惠券表（tb_seckill_voucher，与 tb_voucher 一对一，主键即 voucherId）Mapper。
 */
public interface SeckillVoucherMapper extends BaseMapper<SeckillVoucher> {

    /**
     * 秒杀落库的条件扣减：只有 stock > 0 才扣 1。
     * 并发下多线程同时执行时数据库行锁保证不出现负数库存（防超卖的最终兜底）。
     *
     * @return 影响行数：1 表示扣减成功；0 表示库存已经为 0（超卖拦截）
     */
    @Update("UPDATE tb_seckill_voucher SET stock = stock - 1 WHERE voucher_id = #{voucherId} AND stock > 0")
    int decrementStock(@Param("voucherId") Long voucherId);

    /**
     * 管理端调库存/取消订单回补库存：允许增减，但结果不允许为负。
     *
     * @param delta 正数补货、负数减库存
     * @return 影响行数：1 成功；0 表示券不存在或库存不够减
     */
    @Update("UPDATE tb_seckill_voucher SET stock = stock + #{delta} WHERE voucher_id = #{voucherId} AND stock + #{delta} >= 0")
    int adjustStock(@Param("voucherId") Long voucherId, @Param("delta") int delta);

    /** 行级锁读秒杀配置：落库前锁定券行，避免扣减与配置修改并发交错。 */
    @Select("SELECT * FROM tb_seckill_voucher WHERE voucher_id = #{voucherId} FOR UPDATE")
    SeckillVoucher selectByVoucherIdForUpdate(@Param("voucherId") Long voucherId);
}
