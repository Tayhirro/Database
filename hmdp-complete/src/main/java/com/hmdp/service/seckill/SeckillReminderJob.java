package com.hmdp.service.seckill;

import com.hmdp.service.IVoucherOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 秒杀活动定时任务：活动开始前提醒订阅用户。
 *
 * 调度节奏：@Scheduled fixedDelay 60 秒，本轮跑完再计时下一轮；
 * 是否真的发送由 {@link IVoucherOrderService#remindUpcomingActivities} 内部的
 * Redis 标记（seckill:reminded:{voucherId}）控制，每个活动只提醒一次。
 */
@Slf4j
@Component
public class SeckillReminderJob {

    @Resource
    private IVoucherOrderService voucherOrderService;

    @Scheduled(fixedDelay = 60000)
    public void remindUpcoming() {
        try {
            voucherOrderService.remindUpcomingActivities();
        } catch (RuntimeException e) {
            // 定时任务失败不影响主流程，记录后下一轮重试
            log.warn("秒杀活动开始提醒任务异常：{}", e.getMessage());
        }
    }
}
