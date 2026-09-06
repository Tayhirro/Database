package com.hmdp.controller;

import com.hmdp.dto.Result;
import com.hmdp.service.IVoucherOrderService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 秒杀订单接口。除浏览外的全部动作都在登录拦截范围内（未排除该前缀）。
 *
 * 下单是异步两段式：POST seckill 返回 orderId 只代表"已受理"（Redis 已扣资格、
 * 消息已入队），客户端轮询 GET /{orderId}，data 为 null 表示还在落库，
 * 有值即最终订单状态。
 */
@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {

    @Resource
    private IVoucherOrderService voucherOrderService;

    /** 秒杀下单（受理段）：返回 orderId，异步落库后可通过查询接口确认。 */
    @PostMapping("seckill/{id}")
    public Result seckillVoucher(@PathVariable("id") Long voucherId) {
        return voucherOrderService.seckillVoucher(voucherId);
    }

    /** 轮询抢购结果：订单未落库时 data 为 null；只允许订单主人或管理员查看。 */
    @GetMapping("{id}")
    public Result queryOrder(@PathVariable("id") Long orderId) {
        return voucherOrderService.queryOrderById(orderId);
    }

    /** 本人订单列表：voucherId 可选；lastId 游标翻页（按订单 ID 倒序，一页 10 条）。 */
    @GetMapping("mine")
    public Result queryMyOrders(@RequestParam(value = "voucherId", required = false) Long voucherId,
                                @RequestParam(value = "lastId", required = false) Long lastId) {
        return voucherOrderService.queryMyOrders(voucherId, lastId);
    }

    /** 支付模拟：body 可选 {"payType": 1|2|3}，把本人未支付订单推进为已支付。 */
    @PostMapping("{id}/pay")
    public Result payOrder(@PathVariable("id") Long orderId,
                           @RequestBody(required = false) Map<String, Integer> body) {
        Integer payType = body == null ? null : body.get("payType");
        return voucherOrderService.payOrder(orderId, payType);
    }

    /** 取消本人未支付订单：状态改为已取消，数据库与 Redis 库存各恢复 1。 */
    @PostMapping("{id}/cancel")
    public Result cancelOrder(@PathVariable("id") Long orderId) {
        return voucherOrderService.cancelOrder(orderId);
    }

    /** 订阅到货/开始提醒（售罄或活动未开始时可订阅）。 */
    @PostMapping("seckill/{id}/subscribe")
    public Result subscribe(@PathVariable("id") Long voucherId) {
        return voucherOrderService.subscribeArrival(voucherId);
    }

    /** 取消订阅提醒。 */
    @DeleteMapping("seckill/{id}/subscribe")
    public Result unsubscribe(@PathVariable("id") Long voucherId) {
        return voucherOrderService.unsubscribeArrival(voucherId);
    }

    /** 查询本人对某张券的订阅状态，data 为 true/false。 */
    @GetMapping("seckill/{id}/subscribe")
    public Result subscribeStatus(@PathVariable("id") Long voucherId) {
        return voucherOrderService.subscribeStatus(voucherId);
    }
}
