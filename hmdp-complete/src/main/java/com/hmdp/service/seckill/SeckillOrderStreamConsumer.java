package com.hmdp.service.seckill;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmdp.config.SeckillProperties;
import com.hmdp.exception.BusinessException;
import com.hmdp.service.IVoucherOrderService;
import com.hmdp.utils.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.ByteRecord;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 秒杀订单消息消费者：从 Redis Stream（seckill:stream:orders）读取下单消息并落库。
 *
 * 消费模型（消费组 order-writer，消费者名 order-writer-1）：
 * 1. 启动时 XGROUP CREATE 建组（MKSTREAM），组已存在（BUSYGROUP）则跳过；
 *    启动时 Redis 不可用不阻塞应用，主循环里会反复重试建组；
 * 2. 后台线程 XREADGROUP BLOCK 2 秒 COUNT 10 读取新消息，逐条调用
 *    {@link IVoucherOrderService#fulfillOrder}（数据库事务），
 *    成功后 XACK 确认；失败不确认，消息留在 PEL（待确认列表）等待重试；
 * 3. 两种失败分流（重试由 retryPendingMessages 完成）：
 *    - {@link BusinessException}（配置缺失、时间窗外、库存被拦截）：永久失败，
 *      立即恢复 Redis 资格并转入死信 List seckill:stream:orders:dead；
 *    - 其他异常（数据库抖动等）：留在 PEL，等下一轮重试，最多 max-retry 次；
 * 4. 重试任务每 30 秒执行：XPENDING 查待确认消息，投递次数未达上限的消息
 *    XCLAIM（最短闲置 30 秒，避免与活跃消费撞车）后重新处理；
 *    达到上限的走死信流程：补偿 Redis 库存与资格 + 写入死信 + XACK。
 */
@Slf4j
@Component
public class SeckillOrderStreamConsumer {

    private static final String CONSUMER_NAME = "order-writer-1";

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private IVoucherOrderService voucherOrderService;
    @Resource
    private SeckillProperties seckillProperties;
    @Resource
    private ObjectMapper objectMapper;

    private volatile boolean running = true;
    private volatile boolean groupReady = false;
    private Thread workerThread;

    @PostConstruct
    public void start() {
        ensureGroup();
        workerThread = new Thread(this::consumeLoop, "seckill-order-consumer");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    @PreDestroy
    public void stop() {
        running = false;
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }

    /** 建消费组；组已存在或 Redis 不可用时静默，由主循环/重试任务再次尝试。 */
    private void ensureGroup() {
        try {
            stringRedisTemplate.opsForStream().createGroup(
                    RedisConstants.SECKILL_STREAM_KEY, ReadOffset.from("0"), RedisConstants.SECKILL_STREAM_GROUP);
            groupReady = true;
        } catch (RuntimeException e) {
            if (String.valueOf(e.getMessage()).contains("BUSYGROUP")) {
                groupReady = true;
            } else {
                groupReady = false;
                log.warn("创建秒杀消费组失败（Redis 未就绪时属预期，会自动重试）：{}", e.getMessage());
            }
        }
    }

    private void consumeLoop() {
        StreamReadOptions options = StreamReadOptions.empty().count(10).block(Duration.ofSeconds(2));
        while (running) {
            try {
                if (!groupReady) {
                    ensureGroup();
                    if (!groupReady) {
                        Thread.sleep(3000);
                        continue;
                    }
                }
                List<MapRecord<String, Object, Object>> records = stringRedisTemplate.opsForStream().read(
                        Consumer.from(RedisConstants.SECKILL_STREAM_GROUP, CONSUMER_NAME),
                        options,
                        StreamOffset.create(RedisConstants.SECKILL_STREAM_KEY, ReadOffset.lastConsumed()));
                if (records == null || records.isEmpty()) {
                    continue;
                }
                for (MapRecord<String, Object, Object> record : records) {
                    handleRecord(record);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (RuntimeException e) {
                // Redis 断连等运行期异常：记日志，循环继续（下轮自动重连重试建组）
                groupReady = false;
                log.warn("秒杀消息消费循环异常，稍后重试：{}", e.getMessage());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    /** 处理单条消息：落库成功即确认；业务性永久失败走死信；其他异常留在 PEL。 */
    private void handleRecord(MapRecord<String, Object, Object> record) {
        handleFields(record.getId(), toPlainMap(record.getValue()));
    }

    private void handleFields(RecordId recordId, Map<String, String> fields) {
        try {
            voucherOrderService.fulfillOrder(
                    Long.parseLong(fields.get("voucherId")),
                    Long.parseLong(fields.get("userId")),
                    Long.parseLong(fields.get("orderId")));
            acknowledge(recordId);
            log.info("秒杀订单落库成功。orderId={}, voucherId={}, userId={}",
                    fields.get("orderId"), fields.get("voucherId"), fields.get("userId"));
        } catch (BusinessException e) {
            // 永久失败：恢复 Redis 资格后转入死信，避免无意义重试
            log.error("秒杀订单永久失败，转入死信。recordId={}, fields={}, reason={}", recordId, fields, e.getMessage());
            moveToDeadLetter(recordId, fields, e.getMessage());
        } catch (RuntimeException e) {
            // 不确认，消息留在 PEL 由 retryPendingMessages 认领重试
            log.error("秒杀订单落库异常，等待重试。recordId={}, fields={}", recordId, fields, e);
        }
    }

    /**
     * PEL 重试任务：每 30 秒把超时未确认的消息认领回来重做；
     * 达到最大重试次数的转入死信并补偿 Redis。
     */
    @Scheduled(fixedDelay = 30000)
    public void retryPendingMessages() {
        if (!groupReady) {
            ensureGroup();
            if (!groupReady) {
                return;
            }
        }
        try {
            PendingMessages pending = stringRedisTemplate.opsForStream().pending(
                    RedisConstants.SECKILL_STREAM_KEY,
                    RedisConstants.SECKILL_STREAM_GROUP,
                    org.springframework.data.domain.Range.unbounded(),
                    seckillProperties.getClaimBatchSize());
            if (pending == null || pending.isEmpty()) {
                return;
            }
            for (PendingMessage message : pending) {
                if (message.getTotalDeliveryCount() >= seckillProperties.getMaxRetry()) {
                    deadLetterPending(message);
                } else if (message.getElapsedTimeSinceLastDelivery().compareTo(Duration.ofSeconds(30)) > 0) {
                    reprocessClaimed(message.getId());
                }
            }
        } catch (RuntimeException e) {
            log.warn("秒杀消息重试任务异常：{}", e.getMessage());
        }
    }

    /** 认领一条闲置超过 30 秒的待确认消息并重新处理（成功确认，业务失败走死信）。 */
    private void reprocessClaimed(RecordId recordId) {
        try {
            List<ClaimedOrder> claimed = claimToSelf(recordId);
            for (ClaimedOrder order : claimed) {
                handleFields(order.recordId, order.fields);
            }
        } catch (RuntimeException e) {
            log.warn("认领待确认消息失败，recordId={}", recordId, e);
        }
    }

    /** 达到最大重试次数：补偿 Redis、写死信、确认消息。 */
    private void deadLetterPending(PendingMessage message) {
        try {
            List<ClaimedOrder> claimed = claimToSelf(message.getId());
            if (claimed.isEmpty()) {
                return;
            }
            for (ClaimedOrder order : claimed) {
                voucherOrderService.compensateFailedOrder(order.fields);
                moveToDeadLetter(order.recordId, order.fields,
                        "超过最大重试次数 " + seckillProperties.getMaxRetry());
            }
        } catch (RuntimeException e) {
            log.warn("死信转移失败，下一轮重试。recordId={}", message.getId(), e);
        }
    }

    /**
     * XCLAIM 认领：Spring Data Redis 2.7 的 StreamOperations 没有 claim 高层方法，
     * 这里走连接层 xClaim（最短闲置 30 秒），把消息所有权转给本消费者并带回消息体。
     */
    private List<ClaimedOrder> claimToSelf(RecordId... recordIds) {
        List<ByteRecord> claimed = stringRedisTemplate.execute((RedisCallback<List<ByteRecord>>) connection ->
                connection.streamCommands().xClaim(
                        RedisConstants.SECKILL_STREAM_KEY.getBytes(StandardCharsets.UTF_8),
                        RedisConstants.SECKILL_STREAM_GROUP,
                        CONSUMER_NAME,
                        RedisStreamCommands.XClaimOptions.minIdle(Duration.ofSeconds(30)).ids(recordIds)));
        List<ClaimedOrder> result = new ArrayList<>();
        if (claimed == null) {
            return result;
        }
        for (ByteRecord record : claimed) {
            Map<String, String> fields = new HashMap<>();
            record.getValue().forEach((k, v) ->
                    fields.put(new String(k, StandardCharsets.UTF_8), new String(v, StandardCharsets.UTF_8)));
            result.add(new ClaimedOrder(record.getId(), fields));
        }
        return result;
    }

    /** 认领结果：记录 ID + 消息字段。 */
    private static final class ClaimedOrder {
        private final RecordId recordId;
        private final Map<String, String> fields;

        private ClaimedOrder(RecordId recordId, Map<String, String> fields) {
            this.recordId = recordId;
            this.fields = fields;
        }
    }

    /** 把一条永久失败的消息写入死信 List（JSON：原字段 + 失败原因 + 时间）并确认。 */
    private void moveToDeadLetter(RecordId recordId, Map<String, String> fields, String reason) {
        try {
            Map<String, Object> dead = new HashMap<>(fields);
            dead.put("recordId", recordId == null ? null : recordId.getValue());
            dead.put("reason", reason);
            dead.put("deadAt", java.time.Instant.now().toString());
            stringRedisTemplate.opsForList().rightPush(
                    RedisConstants.SECKILL_STREAM_DEAD_KEY,
                    objectMapper.writeValueAsString(dead));
            acknowledge(recordId);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("死信消息序列化失败，recordId={}", recordId, e);
        } catch (RuntimeException e) {
            log.error("写入死信列表失败，recordId={}", recordId, e);
        }
    }

    private void acknowledge(RecordId recordId) {
        stringRedisTemplate.opsForStream().acknowledge(
                RedisConstants.SECKILL_STREAM_KEY, RedisConstants.SECKILL_STREAM_GROUP, recordId);
    }

    private Map<String, String> toPlainMap(Map<?, ?> raw) {
        Map<String, String> fields = new HashMap<>();
        if (raw != null) {
            for (Map.Entry<?, ?> entry : raw.entrySet()) {
                fields.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
        }
        return fields;
    }
}
