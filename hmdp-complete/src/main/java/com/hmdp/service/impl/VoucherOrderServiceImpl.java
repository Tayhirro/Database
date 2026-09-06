package com.hmdp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.config.AdminProperties;
import com.hmdp.config.SeckillProperties;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.dto.VoucherOrderDTO;
import com.hmdp.entity.SeckillVoucher;
import com.hmdp.entity.User;
import com.hmdp.entity.Voucher;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.exception.BusinessException;
import com.hmdp.mapper.SeckillVoucherMapper;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.IUserService;
import com.hmdp.service.IVoucherOrderService;
import com.hmdp.service.IVoucherService;
import com.hmdp.sms.SmsSender;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 秒杀订单服务实现：受理段（Lua + Stream 投递）与落库段（事务）都在这里。
 *
 * 一次完整秒杀的数据流（key、字段名、返回码都与代码一致）：
 * 1. 用户 POST /voucher-order/seckill/{voucherId}，本方法先查 tb_voucher（type=1 才是秒杀券）
 *    与 tb_seckill_voucher，校验活动时间窗；
 * 2. 执行 lua/seckill.lua：KEYS =（seckill:stock:{voucherId} 预热库存字符串、
 *    seckill:ordered:{voucherId} 资格 Set），ARGV =（userId），
 *    原子完成"有库存、没抢过、扣 1、记资格"，返回 0 成功 / 1 未预热 / 2 售罄 / 3 重复；
 * 3. 生成订单号（{@link RedisIdWorker}：1 位符号 + 31 位秒级时间戳 + 32 位 Redis 序列），
 *    XADD 到 seckill:stream:orders，消息字段 voucherId/userId/orderId；
 *    投递失败则回滚第 2 步（库存加回 1、资格移除），Redis 不留幽灵扣减；
 * 4. 消费者（{@link com.hmdp.service.seckill.SeckillOrderStreamConsumer}）读取消息，
 *    调用 fulfillOrder：行锁读券配置 -> stock > 0 条件扣减 -> 插入订单；
 *    UNIQUE(user_id, voucher_id) 冲突视为重复投递，对齐已有订单不再新建；
 * 5. 客户端 GET /voucher-order/{orderId} 轮询，订单未落库时返回空 data 表示"处理中"。
 */
@Slf4j
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    /** Lua 资格脚本返回码：库存 key 不存在（未预热或已清理）。 */
    private static final long LUA_NO_STOCK_KEY = 1L;
    /** Lua 资格脚本返回码：售罄。 */
    private static final long LUA_SOLD_OUT = 2L;
    /** Lua 资格脚本返回码：重复抢购。 */
    private static final long LUA_DUPLICATED = 3L;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("lua/seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    @Resource
    private SeckillVoucherMapper seckillVoucherMapper;
    @Resource
    private IVoucherService voucherService;
    @Resource
    private IUserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisIdWorker redisIdWorker;
    @Resource
    private SeckillProperties seckillProperties;
    @Resource
    private SmsSender smsSender;
    @Resource
    private AdminProperties adminProperties;

    @Override
    public Result seckillVoucher(Long voucherId) {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            return Result.fail("请先登录");
        }
        Voucher voucher = voucherService.getById(voucherId);
        if (voucher == null || voucher.getType() == null || voucher.getType() != 1) {
            return Result.fail("优惠券不存在或不是秒杀券");
        }
        SeckillVoucher seckillVoucher = seckillVoucherMapper.selectById(voucherId);
        if (seckillVoucher == null) {
            return Result.fail("活动配置不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(seckillVoucher.getBeginTime())) {
            return Result.fail("活动未开始");
        }
        if (now.isAfter(seckillVoucher.getEndTime())) {
            return Result.fail("活动已结束");
        }
        Long luaResult = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.singletonList(RedisConstants.SECKILL_STOCK_KEY + voucherId),
                user.getId().toString());
        if (luaResult == null || luaResult == LUA_NO_STOCK_KEY) {
            return Result.fail("活动未就绪，请稍后再试");
        }
        if (luaResult == LUA_SOLD_OUT) {
            return Result.fail("券已售罄");
        }
        if (luaResult == LUA_DUPLICATED) {
            return Result.fail("您已抢到过该券，不能重复抢购");
        }
        long orderId = redisIdWorker.nextId("order");
        try {
            Map<String, String> message = new HashMap<>();
            message.put("voucherId", voucherId.toString());
            message.put("userId", user.getId().toString());
            message.put("orderId", Long.toString(orderId));
            stringRedisTemplate.opsForStream().add(RedisConstants.SECKILL_STREAM_KEY, message);
        } catch (RuntimeException e) {
            // 投递失败必须回滚 Lua 的扣减与资格，否则用户"扣了库存却没有订单"
            stringRedisTemplate.opsForValue().increment(RedisConstants.SECKILL_STOCK_KEY + voucherId);
            stringRedisTemplate.opsForSet().remove(RedisConstants.SECKILL_ORDERED_KEY + voucherId, user.getId().toString());
            log.error("秒杀消息投递失败，已回滚 Redis 资格。voucherId={}, userId={}", voucherId, user.getId(), e);
            return Result.fail("抢购繁忙，请稍后再试");
        }
        return Result.ok(orderId);
    }

    @Override
    @Transactional
    public void fulfillOrder(Long voucherId, Long userId, Long orderId) {
        SeckillVoucher seckillVoucher = seckillVoucherMapper.selectByVoucherIdForUpdate(voucherId);
        if (seckillVoucher == null) {
            throw new BusinessException("秒杀券配置不存在：" + voucherId);
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(seckillVoucher.getBeginTime()) || now.isAfter(seckillVoucher.getEndTime())) {
            throw new BusinessException("活动时间窗外拒绝落库：" + voucherId);
        }
        int affected = seckillVoucherMapper.decrementStock(voucherId);
        if (affected == 0) {
            // Lua 放行但数据库已无库存：Redis 与 DB 漂移，条件更新拦住超卖；
            // 抛出业务异常让消费端恢复 Redis 资格并转入死信
            throw new BusinessException("库存不足，落库被拦截：" + voucherId);
        }
        VoucherOrder order = new VoucherOrder();
        order.setId(orderId);
        order.setUserId(userId);
        order.setVoucherId(voucherId);
        order.setPayType(1);
        order.setStatus(1);
        order.setCreateTime(now);
        order.setUpdateTime(now);
        try {
            save(order);
        } catch (DuplicateKeyException e) {
            // UNIQUE(user_id, voucher_id) 冲突：同一用户同一券已有订单（消息重放/重复投递）。
            // 数据库真相优先：不新建第二条；第一条订单使用的就是本次扣减的那份库存，不回滚
            log.warn("订单唯一键冲突，视为重复投递。voucherId={}, userId={}, orderId={}", voucherId, userId, orderId);
        }
    }

    @Override
    public Result queryOrderById(Long orderId) {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            return Result.fail("请先登录");
        }
        VoucherOrder order = getById(orderId);
        if (order == null) {
            // 受理段刚返回 orderId、订单还在队列里：返回空 data 表示"处理中"，不是错误
            return Result.ok(null);
        }
        if (!order.getUserId().equals(user.getId()) && !adminProperties.isAdmin(user.getId())) {
            return Result.fail("无权查看他人订单");
        }
        return Result.ok(toDTO(order));
    }

    @Override
    public Result queryMyOrders(Long voucherId, Long lastId) {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            return Result.fail("请先登录");
        }
        LambdaQueryWrapper<VoucherOrder> wrapper = new LambdaQueryWrapper<VoucherOrder>()
                .eq(VoucherOrder::getUserId, user.getId())
                .eq(voucherId != null, VoucherOrder::getVoucherId, voucherId)
                .lt(lastId != null, VoucherOrder::getId, lastId)
                .orderByDesc(VoucherOrder::getId)
                .last("LIMIT 10");
        List<VoucherOrderDTO> orders = list(wrapper).stream().map(this::toDTO).collect(Collectors.toList());
        return Result.ok(orders);
    }

    @Override
    @Transactional
    public Result payOrder(Long orderId, Integer payType) {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            return Result.fail("请先登录");
        }
        int type = (payType == null || payType < 1 || payType > 3) ? 1 : payType;
        int affected = baseMapper.updatePaySimulated(orderId, user.getId(), type);
        if (affected == 0) {
            return Result.fail("订单不存在、不是本人订单或状态不是未支付");
        }
        return Result.ok();
    }

    @Override
    @Transactional
    public Result cancelOrder(Long orderId) {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            return Result.fail("请先登录");
        }
        VoucherOrder order = getById(orderId);
        if (order == null || !order.getUserId().equals(user.getId())) {
            return Result.fail("订单不存在或不是本人订单");
        }
        int affected = baseMapper.cancelOwnOrder(orderId, user.getId());
        if (affected == 0) {
            return Result.fail("只有未支付订单可以取消");
        }
        int affectedStock = seckillVoucherMapper.adjustStock(order.getVoucherId(), 1);
        if (affectedStock == 0) {
            log.error("取消订单回补库存失败，需要人工对账。orderId={}, voucherId={}", orderId, order.getVoucherId());
        }
        // Redis 库存恢复与到货通知必须等数据库事务提交后再做：
        // 事务回滚时不能让 Redis 先多出一份库存
        Long voucherId = order.getVoucherId();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    stringRedisTemplate.opsForValue().increment(RedisConstants.SECKILL_STOCK_KEY + voucherId);
                } catch (RuntimeException e) {
                    log.warn("取消订单后 Redis 库存恢复失败（偏低方向，不超卖），voucherId={}", voucherId, e);
                }
                try {
                    notifyRestock(voucherId);
                } catch (RuntimeException e) {
                    log.warn("取消订单后到货通知失败（不影响订单），voucherId={}", voucherId, e);
                }
            }
        });
        return Result.ok();
    }

    @Override
    public Result subscribeArrival(Long voucherId) {
        UserDTO user = requireLogin();
        if (!isSeckillVoucher(voucherId)) {
            return Result.fail("秒杀券不存在");
        }
        stringRedisTemplate.opsForSet().add(RedisConstants.SECKILL_SUBSCRIBE_KEY + voucherId, user.getId().toString());
        return Result.ok();
    }

    @Override
    public Result unsubscribeArrival(Long voucherId) {
        UserDTO user = requireLogin();
        stringRedisTemplate.opsForSet().remove(RedisConstants.SECKILL_SUBSCRIBE_KEY + voucherId, user.getId().toString());
        return Result.ok();
    }

    @Override
    public Result subscribeStatus(Long voucherId) {
        UserDTO user = requireLogin();
        Boolean member = stringRedisTemplate.opsForSet()
                .isMember(RedisConstants.SECKILL_SUBSCRIBE_KEY + voucherId, user.getId().toString());
        return Result.ok(Boolean.TRUE.equals(member));
    }

    @Override
    public void notifyRestock(Long voucherId) {
        Set<String> subscribers = stringRedisTemplate.opsForSet()
                .members(RedisConstants.SECKILL_SUBSCRIBE_KEY + voucherId);
        if (subscribers == null || subscribers.isEmpty()) {
            return;
        }
        Voucher voucher = voucherService.getById(voucherId);
        String title = voucher == null ? ("优惠券 " + voucherId) : voucher.getTitle();
        for (String phone : loadPhones(subscribers)) {
            try {
                smsSender.sendVoucherArrival(phone, title);
            } catch (RuntimeException e) {
                log.warn("到货短信发送失败，phone={}", phone, e);
            }
        }
        // 通知过一轮后清空订阅集合：这批订阅者的诉求已经满足
        stringRedisTemplate.delete(RedisConstants.SECKILL_SUBSCRIBE_KEY + voucherId);
    }

    @Override
    public void remindUpcomingActivities() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = now.plusMinutes(seckillProperties.getRemindAheadMinutes());
        List<SeckillVoucher> upcoming = seckillVoucherMapper.selectList(
                new LambdaQueryWrapper<SeckillVoucher>()
                        .gt(SeckillVoucher::getBeginTime, now)
                        .le(SeckillVoucher::getBeginTime, deadline));
        for (SeckillVoucher activity : upcoming) {
            String remindedKey = RedisConstants.SECKILL_REMINDED_KEY + activity.getVoucherId();
            Long first = stringRedisTemplate.opsForSet().add(remindedKey, "sent");
            if (first == null || first == 0) {
                continue;
            }
            try {
                Set<String> subscribers = stringRedisTemplate.opsForSet()
                        .members(RedisConstants.SECKILL_SUBSCRIBE_KEY + activity.getVoucherId());
                if (subscribers == null || subscribers.isEmpty()) {
                    continue;
                }
                Voucher voucher = voucherService.getById(activity.getVoucherId());
                String title = voucher == null ? ("优惠券 " + activity.getVoucherId()) : voucher.getTitle();
                for (String phone : loadPhones(subscribers)) {
                    try {
                        smsSender.sendVoucherArrival(phone, "【即将开始】" + title);
                    } catch (RuntimeException e) {
                        log.warn("活动开始提醒发送失败，phone={}", phone, e);
                    }
                }
            } catch (RuntimeException e) {
                // 发送失败清除标记，下一轮定时任务重试
                stringRedisTemplate.delete(remindedKey);
                log.warn("活动开始提醒处理失败，voucherId={}", activity.getVoucherId(), e);
            }
        }
    }

    @Override
    @Transactional
    public int adjustStock(Long voucherId, int delta) {
        SeckillVoucher before = seckillVoucherMapper.selectById(voucherId);
        if (before == null) {
            return -1;
        }
        int oldStock = before.getStock() == null ? 0 : before.getStock();
        int affected = seckillVoucherMapper.adjustStock(voucherId, delta);
        if (affected == 0) {
            return -1;
        }
        SeckillVoucher after = seckillVoucherMapper.selectById(voucherId);
        int newStock = after == null || after.getStock() == null ? 0 : after.getStock();
        boolean restocked = oldStock == 0 && delta > 0;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    // 以数据库新值为准整体覆盖预热库存，避免多次增量调整累计漂移
                    stringRedisTemplate.opsForValue().set(RedisConstants.SECKILL_STOCK_KEY + voucherId, String.valueOf(newStock));
                } catch (RuntimeException e) {
                    log.warn("调整库存后同步 Redis 失败，以数据库为准等待下次预热，voucherId={}", voucherId, e);
                }
                if (restocked) {
                    try {
                        notifyRestock(voucherId);
                    } catch (RuntimeException e) {
                        log.warn("补货后到货通知失败，voucherId={}", voucherId, e);
                    }
                }
            }
        });
        return newStock;
    }

    @Override
    public void compensateFailedOrder(Map<String, String> messageFields) {
        String voucherId = messageFields.get("voucherId");
        String userId = messageFields.get("userId");
        if (voucherId == null || userId == null) {
            return;
        }
        try {
            stringRedisTemplate.opsForValue().increment(RedisConstants.SECKILL_STOCK_KEY + voucherId);
        } catch (RuntimeException e) {
            log.error("死信补偿恢复 Redis 库存失败，voucherId={}", voucherId, e);
        }
        try {
            stringRedisTemplate.opsForSet().remove(RedisConstants.SECKILL_ORDERED_KEY + voucherId, userId);
        } catch (RuntimeException e) {
            log.error("死信补偿清除资格失败，voucherId={}, userId={}", voucherId, userId, e);
        }
    }

    private UserDTO requireLogin() {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        return user;
    }

    private boolean isSeckillVoucher(Long voucherId) {
        Voucher voucher = voucherService.getById(voucherId);
        return voucher != null && voucher.getType() != null && voucher.getType() == 1
                && seckillVoucherMapper.selectById(voucherId) != null;
    }

    private List<String> loadPhones(Set<String> subscriberIds) {
        List<Long> ids = new ArrayList<>();
        for (String id : subscriberIds) {
            try {
                ids.add(Long.parseLong(id));
            } catch (NumberFormatException ignored) {
                // 集合里的非数字脏成员直接跳过
            }
        }
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return userService.listByIds(ids).stream()
                .map(User::getPhone)
                .filter(phone -> phone != null && !phone.isEmpty())
                .collect(Collectors.toList());
    }

    private VoucherOrderDTO toDTO(VoucherOrder order) {
        VoucherOrderDTO dto = new VoucherOrderDTO();
        dto.setId(order.getId());
        dto.setVoucherId(order.getVoucherId());
        dto.setStatus(order.getStatus());
        dto.setCreateTime(order.getCreateTime());
        dto.setPayTime(order.getPayTime());
        return dto;
    }
}
