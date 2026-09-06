package com.hmdp.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * 全局唯一 ID 生成器测试：验证位结构（符号位为正、时间戳递增）与序列号来源。
 */
@ExtendWith(MockitoExtension.class)
class RedisIdWorkerTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisIdWorker worker;

    @Test
    void nextIdPacksTimestampAndSequence() {
        org.mockito.Mockito.lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(eq(RedisConstants.ID_WORKER_KEY + "order"))).thenReturn(1L).thenReturn(2L);

        long first = worker.nextId("order");
        long second = worker.nextId("order");

        assertTrue(first > 0, "符号位恒为 0，ID 必须是正数");
        long firstTs = first >> 32;
        long secondTs = second >> 32;
        assertTrue(secondTs >= firstTs, "时间戳部分单调不减");
        assertEquals(1L, first & 0xFFFFFFFFL, "同一秒内序列号来自 Redis INCR");
        assertEquals(2L, second & 0xFFFFFFFFL);
    }
}
