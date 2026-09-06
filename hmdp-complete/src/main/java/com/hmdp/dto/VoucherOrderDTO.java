package com.hmdp.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 返回给前端的秒杀订单视图。
 *
 * 不直接返回 VoucherOrder 实体：数据库列（如 payType 内部编码）以后变化不会泄露到接口；
 * 前端轮询抢购结果时只需要这里列出的字段。
 */
@Data
public class VoucherOrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 订单 ID，由 RedisIdWorker 生成，全局唯一。 */
    private Long id;

    /** 购买的秒杀券 ID。 */
    private Long voucherId;

    /**
     * 订单状态：1 未支付；2 已支付；3 已核销；4 已取消。
     * 5 退款中、6 已退款在本阶段没有支付渠道对接，暂不出现。
     */
    private Integer status;

    /** 下单时间。 */
    private LocalDateTime createTime;

    /** 支付时间，未支付时为 null。 */
    private LocalDateTime payTime;
}
