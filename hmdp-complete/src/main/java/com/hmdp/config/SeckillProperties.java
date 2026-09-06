package com.hmdp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 秒杀消费端配置，对应 application.yaml 的 hmdp.seckill 前缀。
 */
@Configuration
@ConfigurationProperties(prefix = "hmdp.seckill")
public class SeckillProperties {

    /**
     * 订单消息消费失败后的最大重试次数。
     * 消息第一次读取失败会留在 Redis Stream 的 PEL（待确认列表），
     * 重试任务每次认领一批继续处理；连续失败超过该次数就转入死信列表。
     */
    private int maxRetry = 5;

    /** 重试任务每轮最多认领多少条待处理消息。 */
    private int claimBatchSize = 10;

    /** 活动开始前多少分钟给订阅用户发送开始提醒。 */
    private int remindAheadMinutes = 10;

    public int getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public int getClaimBatchSize() {
        return claimBatchSize;
    }

    public void setClaimBatchSize(int claimBatchSize) {
        this.claimBatchSize = claimBatchSize;
    }

    public int getRemindAheadMinutes() {
        return remindAheadMinutes;
    }

    public void setRemindAheadMinutes(int remindAheadMinutes) {
        this.remindAheadMinutes = remindAheadMinutes;
    }
}
