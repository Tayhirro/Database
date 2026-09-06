package com.hmdp.service.impl;

/*
 * 现实业务背景：店铺详情页需要展示优惠券，运营后台需要新增带库存和时间窗的秒杀券，
 * 并能查看券详情、调整上架状态与活动时间。
 * 实际触发：GET /voucher/list/{shopId}、POST /voucher/seckill、GET/PUT /voucher/{id} 进入本类；
 * 订单、库存调整、售罄订阅属于 IVoucherOrderService 的范围，不在本类。
 */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.config.AdminProperties;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.SeckillVoucher;
import com.hmdp.entity.Voucher;
import com.hmdp.exception.BusinessException;
import com.hmdp.mapper.SeckillVoucherMapper;
import com.hmdp.mapper.VoucherMapper;
import com.hmdp.service.ISeckillVoucherService;
import com.hmdp.service.IVoucherService;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券服务实现。
 */
@Slf4j
@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;
    @Resource
    private SeckillVoucherMapper seckillVoucherMapper;
    @Resource
    private AdminProperties adminProperties;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 查询店铺优惠券的完整流程：把 shopId 交给 VoucherMapper 的联表查询
     * （{@code tb_voucher v LEFT JOIN tb_seckill_voucher sv ON v.id = sv.voucher_id WHERE v.shop_id = ? AND v.status = 1}），
     * 一次读取普通券字段及秒杀券的库存、开始和结束时间，然后原样放入 Result 返回；本方法只读，不修改库存。
     * 具体例子：店铺 100 同时有一张代金券和一张 20:00 开抢的秒杀券，调用后返回两张券，秒杀券额外带 stock/beginTime/endTime。
     */
    @Override
    public Result queryVoucherOfShop(Long shopId) {
        // 查询优惠券信息
        List<Voucher> vouchers = getBaseMapper().queryVoucherOfShop(shopId);
        // 返回结果
        return Result.ok(vouchers);
    }

    /**
     * 新增秒杀券的完整流程：先把券的基础信息保存到 {@code tb_voucher} 并取得主键，
     * 再把同一主键、库存和有效时间组装成 SeckillVoucher 保存到 {@code tb_seckill_voucher}；两步处于同一事务，任一步失败都会回滚。
     * 事务提交后把库存预热进 Redis（seckill:stock:{voucherId}）——Lua 资格脚本要求库存 key 必须存在。
     * 具体例子：新增"50 元券"、库存 100、20:00～22:00，若基础券生成 ID 300，则秒杀表保存 {@code voucherId=300,stock=100}，
     * Redis 写入 seckill:stock:300 = "100"。
     */
    @Override
    @Transactional
    public void addSeckillVoucher(Voucher voucher) {
        requireAdmin();
        validateSeckillVoucher(voucher);
        // 保存优惠券
        save(voucher);
        // 保存秒杀信息
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setVoucherId(voucher.getId());
        seckillVoucher.setStock(voucher.getStock());
        seckillVoucher.setBeginTime(voucher.getBeginTime());
        seckillVoucher.setEndTime(voucher.getEndTime());
        seckillVoucherService.save(seckillVoucher);
        Long voucherId = voucher.getId();
        Integer stock = voucher.getStock();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    stringRedisTemplate.opsForValue()
                            .set(RedisConstants.SECKILL_STOCK_KEY + voucherId, String.valueOf(stock));
                } catch (RuntimeException e) {
                    log.warn("秒杀券库存预热失败（可通过管理端调库存接口重新同步），voucherId={}", voucherId, e);
                }
            }
        });
    }

    @Override
    public Result queryVoucherDetail(Long voucherId) {
        Voucher voucher = getById(voucherId);
        if (voucher == null) {
            return Result.fail("优惠券不存在");
        }
        if (voucher.getType() != null && voucher.getType() == 1) {
            SeckillVoucher seckillVoucher = seckillVoucherMapper.selectById(voucherId);
            if (seckillVoucher != null) {
                voucher.setStock(seckillVoucher.getStock());
                voucher.setBeginTime(seckillVoucher.getBeginTime());
                voucher.setEndTime(seckillVoucher.getEndTime());
            }
        }
        return Result.ok(voucher);
    }

    @Override
    @Transactional
    public Result updateVoucher(Long voucherId, Voucher changes) {
        requireAdmin();
        Voucher exists = getById(voucherId);
        if (exists == null) {
            return Result.fail("优惠券不存在");
        }
        if (changes.getBeginTime() != null && changes.getEndTime() != null
                && !changes.getBeginTime().isBefore(changes.getEndTime())) {
            return Result.fail("活动开始时间必须早于结束时间");
        }
        int affected = getBaseMapper().updateAdminFields(
                voucherId,
                changes.getTitle(),
                changes.getSubTitle(),
                changes.getRules(),
                changes.getStatus(),
                changes.getBeginTime(),
                changes.getEndTime());
        if (affected == 0) {
            return Result.fail("没有字段被修改或券不存在");
        }
        // 秒杀券改了时间窗要同步 tb_seckill_voucher，秒杀下单校验读的是秒杀表
        if (exists.getType() != null && exists.getType() == 1
                && (changes.getBeginTime() != null || changes.getEndTime() != null)) {
            SeckillVoucher seckillVoucher = seckillVoucherMapper.selectById(voucherId);
            if (seckillVoucher != null) {
                if (changes.getBeginTime() != null) {
                    seckillVoucher.setBeginTime(changes.getBeginTime());
                }
                if (changes.getEndTime() != null) {
                    seckillVoucher.setEndTime(changes.getEndTime());
                }
                seckillVoucherMapper.updateById(seckillVoucher);
            }
        }
        return Result.ok();
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

    private void validateSeckillVoucher(Voucher voucher) {
        if (voucher.getShopId() == null) {
            throw BusinessException.badRequest("SHOP_REQUIRED", "秒杀券必须归属一个店铺");
        }
        if (voucher.getTitle() == null || voucher.getTitle().isEmpty()) {
            throw BusinessException.badRequest("TITLE_REQUIRED", "券标题不能为空");
        }
        if (voucher.getStock() == null || voucher.getStock() < 0) {
            throw BusinessException.badRequest("STOCK_INVALID", "秒杀库存必须大于等于 0");
        }
        if (voucher.getBeginTime() == null || voucher.getEndTime() == null
                || !voucher.getBeginTime().isBefore(voucher.getEndTime())) {
            throw BusinessException.badRequest("TIME_INVALID", "活动开始时间必须早于结束时间");
        }
        if (voucher.getStatus() == null) {
            voucher.setStatus(1);
        }
        LocalDateTime now = LocalDateTime.now();
        if (voucher.getBeginTime().isBefore(now.minusHours(1))) {
            throw BusinessException.badRequest("TIME_TOO_EARLY", "活动开始时间不能早于当前时间 1 小时以上");
        }
    }
}
