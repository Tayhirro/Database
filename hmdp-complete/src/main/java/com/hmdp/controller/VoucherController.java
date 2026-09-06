package com.hmdp.controller;

import com.hmdp.config.AdminProperties;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Voucher;
import com.hmdp.exception.BusinessException;
import com.hmdp.service.IVoucherOrderService;
import com.hmdp.service.IVoucherService;
import com.hmdp.utils.UserHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 优惠券接口。/voucher 前缀在登录拦截里被排除（浏览类接口游客可访问），
 * 因此管理写接口在方法内自行校验"已登录 + 在 hmdp.admin.user-ids 白名单"。
 */
@RestController
@RequestMapping("/voucher")
public class VoucherController {

    @Resource
    private IVoucherService voucherService;
    @Resource
    private IVoucherOrderService voucherOrderService;
    @Resource
    private AdminProperties adminProperties;

    /**
     * 新增普通券
     * @param voucher 优惠券信息
     * @return 优惠券id
     */
    @PostMapping
    public Result addVoucher(@RequestBody Voucher voucher) {
        requireAdmin();
        voucherService.save(voucher);
        return Result.ok(voucher.getId());
    }

    /**
     * 新增秒杀券
     * @param voucher 优惠券信息，包含秒杀信息
     * @return 优惠券id
     */
    @PostMapping("seckill")
    public Result addSeckillVoucher(@RequestBody Voucher voucher) {
        voucherService.addSeckillVoucher(voucher);
        return Result.ok(voucher.getId());
    }

    /**
     * 查询店铺的优惠券列表
     * @param shopId 店铺id
     * @return 优惠券列表
     */
    @GetMapping("/list/{shopId}")
    public Result queryVoucherOfShop(@PathVariable("shopId") Long shopId) {
       return voucherService.queryVoucherOfShop(shopId);
    }

    /** 券详情：秒杀券额外带实时库存与活动时间窗，任何用户可查。 */
    @GetMapping("{id}")
    public Result queryVoucherDetail(@PathVariable("id") Long voucherId) {
        return voucherService.queryVoucherDetail(voucherId);
    }

    /** 管理端修改券配置（标题/副标题/规则/状态/活动时间白名单字段）。 */
    @PutMapping("{id}")
    public Result updateVoucher(@PathVariable("id") Long voucherId, @RequestBody Voucher changes) {
        return voucherService.updateVoucher(voucherId, changes);
    }

    /**
     * 管理端调库存：body {"delta": -5} 或 {"delta": 20}。
     * 数据库条件增减（结果不允许为负），事务提交后以新值为准覆盖 Redis 预热库存；
     * 售罄券补货会自动给订阅用户发到货通知。
     */
    @PostMapping("{id}/stock")
    public Result adjustStock(@PathVariable("id") Long voucherId, @RequestBody Map<String, Integer> body) {
        requireAdmin();
        Integer delta = body == null ? null : body.get("delta");
        if (delta == null || delta == 0) {
            return Result.fail("delta 必须是非 0 整数");
        }
        int newStock = voucherOrderService.adjustStock(voucherId, delta);
        if (newStock < 0) {
            return Result.fail("券不存在或库存不够减");
        }
        return Result.ok(newStock);
    }

    private void requireAdmin() {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        if (!adminProperties.isAdmin(user.getId())) {
            throw BusinessException.forbidden("仅运营人员可以执行该操作");
        }
    }
}
