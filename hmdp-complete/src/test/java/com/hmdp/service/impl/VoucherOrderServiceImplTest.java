package com.hmdp.service.impl;

import com.hmdp.config.AdminProperties;
import com.hmdp.config.SeckillProperties;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.SeckillVoucher;
import com.hmdp.entity.Voucher;
import com.hmdp.exception.BusinessException;
import com.hmdp.mapper.SeckillVoucherMapper;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.IVoucherService;
import com.hmdp.sms.SmsSender;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.UserHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 秒杀订单服务单元测试：覆盖 Lua 返回码分支、活动时间窗、落库条件扣减与死信补偿。
 * Redis 与数据库全部用 Mockito 打桩，不依赖外部服务。
 */
@ExtendWith(MockitoExtension.class)
class VoucherOrderServiceImplTest {

    @Mock
    private SeckillVoucherMapper seckillVoucherMapper;
    @Mock
    private IVoucherService voucherService;
    @Mock
    private com.hmdp.service.IUserService userService;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private StreamOperations<String, Object, Object> streamOperations;
    @Mock
    private org.springframework.data.redis.core.SetOperations<String, String> setOperations;
    @Mock
    private RedisIdWorker redisIdWorker;
    @Mock
    private SeckillProperties seckillProperties;
    @Mock
    private SmsSender smsSender;
    @Mock
    private AdminProperties adminProperties;
    @Mock
    private VoucherOrderMapper voucherOrderMapper;

    @InjectMocks
    private VoucherOrderServiceImpl service;

    @BeforeEach
    void setUp() {
        UserDTO user = new UserDTO();
        user.setId(7L);
        UserHolder.saveUser(user);
        lenient().when(stringRedisTemplate.opsForStream()).thenReturn(streamOperations);
        lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(stringRedisTemplate.opsForSet()).thenReturn(setOperations);
    }

    @AfterEach
    void tearDown() {
        UserHolder.removeUser();
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    private Voucher seckillVoucher() {
        Voucher voucher = new Voucher();
        voucher.setId(300L);
        voucher.setType(1);
        return voucher;
    }

    private SeckillVoucher activity(LocalDateTime begin, LocalDateTime end) {
        SeckillVoucher sv = new SeckillVoucher();
        sv.setVoucherId(300L);
        sv.setStock(100);
        sv.setBeginTime(begin);
        sv.setEndTime(end);
        return sv;
    }

    @Test
    void seckillAcceptsOrderWhenLuaPasses() {
        when(voucherService.getById(300L)).thenReturn(seckillVoucher());
        when(seckillVoucherMapper.selectById(300L)).thenReturn(activity(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(1)));
        when(stringRedisTemplate.execute(any(RedisScript.class), anyList(), anyString())).thenReturn(0L);
        when(redisIdWorker.nextId("order")).thenReturn(123L);

        Result result = service.seckillVoucher(300L);

        assertEquals(123L, result.getData());
        // 消息字段必须是 voucherId/userId/orderId 三件套
        verify(streamOperations).add(eq(RedisConstants.SECKILL_STREAM_KEY), any());
    }

    @Test
    void seckillRejectsSoldOutAndDuplicate() {
        when(voucherService.getById(300L)).thenReturn(seckillVoucher());
        when(seckillVoucherMapper.selectById(300L)).thenReturn(activity(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(1)));
        when(stringRedisTemplate.execute(any(RedisScript.class), anyList(), anyString()))
                .thenReturn(2L)   // 售罄
                .thenReturn(3L);  // 重复抢购

        Result soldOut = service.seckillVoucher(300L);
        Result duplicated = service.seckillVoucher(300L);

        assertTrue(String.valueOf(soldOut.getErrorMsg()).contains("售罄"));
        assertTrue(String.valueOf(duplicated.getErrorMsg()).contains("重复"));
        verify(streamOperations, never()).add(anyString(), any());
    }

    @Test
    void seckillRejectsBeforeBeginAndAfterEnd() {
        when(voucherService.getById(300L)).thenReturn(seckillVoucher());
        when(seckillVoucherMapper.selectById(300L)).thenReturn(activity(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2)));

        Result notStarted = service.seckillVoucher(300L);
        assertTrue(String.valueOf(notStarted.getErrorMsg()).contains("未开始"));

        when(seckillVoucherMapper.selectById(300L)).thenReturn(activity(LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1)));
        Result ended = service.seckillVoucher(300L);
        assertTrue(String.valueOf(ended.getErrorMsg()).contains("已结束"));
    }

    @Test
    void fulfillOrderThrowsWhenStockExhausted() {
        TransactionSynchronizationManager.initSynchronization();
        when(seckillVoucherMapper.selectByVoucherIdForUpdate(300L))
                .thenReturn(activity(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(1)));
        when(seckillVoucherMapper.decrementStock(300L)).thenReturn(0);

        assertThrows(BusinessException.class, () -> service.fulfillOrder(300L, 7L, 999L));
        verify(voucherOrderMapper, never()).insert(any());
    }

    @Test
    void compensateFailedOrderRestoresStockAndQualification() {
        Map<String, String> fields = new HashMap<>();
        fields.put("voucherId", "300");
        fields.put("userId", "7");

        service.compensateFailedOrder(fields);

        verify(valueOperations).increment(RedisConstants.SECKILL_STOCK_KEY + "300");
        verify(setOperations).remove(RedisConstants.SECKILL_ORDERED_KEY + "300", "7");
    }

    @Test
    void adjustStockSyncsRedisAfterCommit() {
        SeckillVoucher before = activity(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(1));
        before.setStock(0);
        SeckillVoucher after = activity(before.getBeginTime(), before.getEndTime());
        after.setStock(50);
        when(seckillVoucherMapper.selectById(300L)).thenReturn(before).thenReturn(after);
        when(seckillVoucherMapper.adjustStock(300L, 50)).thenReturn(1);
        TransactionSynchronizationManager.initSynchronization();

        int newStock = service.adjustStock(300L, 50);

        assertEquals(50, newStock);
        for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
            synchronization.afterCommit();
        }
        // 补货后以数据库新值为准覆盖 Redis，并触发到货通知
        verify(valueOperations).set(RedisConstants.SECKILL_STOCK_KEY + "300", "50");
    }
}
