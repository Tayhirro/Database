package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.dto.Result;
import com.hmdp.entity.VoucherOrder;

import java.util.Map;

/**
 * 优惠券订单服务：完整的秒杀交易链路。
 *
 * 下单是异步两段式（订单表 tb_voucher_order，见实体 {@link VoucherOrder}）：
 * 1. 受理段（同步）：Lua 脚本原子完成"查库存、一人一单判重、扣 Redis 库存、记资格"，
 *    通过后生成订单号并向 Redis Stream 投递一条订单消息，接口立即返回 orderId，
 *    语义是"已受理"；此时数据库里还没有订单行。
 * 2. 落库段（异步）：{@link com.hmdp.service.seckill.SeckillOrderStreamConsumer}
 *    （Redis Stream 消费者，订单消息的落库执行者）消费消息，在数据库事务里
 *    条件扣减 MySQL 库存并插入订单；客户端拿 orderId 轮询查询接口看最终状态。
 *
 * 一人一单的最终裁决者是 tb_voucher_order 的 UNIQUE(user_id, voucher_id)；
 * 取消订单只改状态为 4（已取消）并恢复库存，记录不删除，
 * 因此取消后同一用户也不能对同一张券再抢一次。
 */
public interface IVoucherOrderService extends IService<VoucherOrder> {

    /**
     * 秒杀下单受理段：校验券与活动时间，Lua 扣减 Redis 库存并记资格，
     * 投递订单消息到 Redis Stream，返回 orderId（此时订单尚未写库）。
     */
    Result seckillVoucher(Long voucherId);

    /**
     * 订单消息落库段：在数据库事务里条件扣减 MySQL 库存（stock > 0 才扣）
     * 并插入订单。由消费者调用，不直接暴露给 HTTP。
     * 唯一键冲突视为重复投递：对齐到已有订单后返回，不重复扣库存。
     */
    void fulfillOrder(Long voucherId, Long userId, Long orderId);

    /** 按订单 ID 查询抢购结果（轮询用）；只允许订单主人或管理员查看。 */
    Result queryOrderById(Long orderId);

    /**
     * 查询当前用户的订单列表；voucherId 不传查全部，
     * 按 id 倒序 + 可选 lastId 游标翻页（一页最多 10 条）。
     */
    Result queryMyOrders(Long voucherId, Long lastId);

    /** 支付模拟：把本人未支付订单改为已支付。没有真实支付渠道，仅推进状态机。 */
    Result payOrder(Long orderId, Integer payType);

    /** 取消本人未支付订单：状态改为已取消，数据库与 Redis 库存各恢复 1，并触发到货提醒。 */
    Result cancelOrder(Long orderId);

    /** 订阅售罄券的到货/开始提醒。 */
    Result subscribeArrival(Long voucherId);

    /** 取消订阅。 */
    Result unsubscribeArrival(Long voucherId);

    /** 查询本人对某张券的订阅状态。 */
    Result subscribeStatus(Long voucherId);

    /**
     * 库存从 0 恢复为正数后给订阅用户发送到货通知；
     * 由取消订单（事务提交后）和管理端调库存调用。
     */
    void notifyRestock(Long voucherId);

    /**
     * 活动开始前提醒：扫描未来 remind-ahead-minutes 分钟内开始的秒杀券，
     * 给订阅用户发送提醒；每个活动只提醒一次（Redis 标记）。
     * 供定时任务调用。
     */
    void remindUpcomingActivities();

    /**
     * 供管理端调库存时同步 Redis 预热库存；
     * delta 为正数（补货）且券售罄过时会触发到货通知。
     *
     * @return 调整后的数据库库存值，-1 表示调整失败（库存不够减等）
     */
    int adjustStock(Long voucherId, int delta);

    /**
     * 供消费端重试任务把超过最大重试次数的消息恢复资格：
     * 恢复 Redis 库存 1 并从资格集合移除该用户，然后消息转入死信列表。
     */
    void compensateFailedOrder(Map<String, String> messageFields);
}
