package com.hmdp.service.feed.push;

/*
 * 现实业务背景：推模式收件箱随发布不断增长，单个粉丝的收件箱如果不设上限会无限膨胀，
 * 收件箱召回查询（inbox 通道按 recipient_id + score 索引取数）也会越来越慢。
 * 实际触发：Spring 定时调度自动触发，每小时把超过容量的收件人裁剪到上限、只保留最新记录。
 *
 * 容量来自 {@link FeedPushProperties}（推模式 Feed 配置）的 inbox-capacity，默认 200 条，
 * 与关注流候选池大小 200 对齐。裁剪规则：按 score 倒序、id 倒序保留最新 200 条，删除其余；
 * 例如某收件人有 350 条记录时，保留最新 200 条、删除 150 条。
 *
 * 调度节奏：@Scheduled fixedDelay = 3600000 毫秒 = 每 1 小时一轮（本轮跑完再计时下一轮）。
 * 每轮最多处理 500 个超容收件人（selectOverCapacityRecipients 的 LIMIT 500），
 * 超容用户更多时下一轮继续，避免单轮删除太多行、长时间持有行锁。
 * 整个任务用 try/catch 包住并记日志：清理失败不影响任何业务——收件箱只是缓存，
 * 最坏情况是查询稍慢、旧内容多留一会儿。
 */

import com.hmdp.config.FeedPushProperties;
import com.hmdp.mapper.FeedInboxMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class FeedInboxCleanupJob {

    /** 每轮最多处理的超容收件人数量，与 selectOverCapacityRecipients 的 LIMIT 500 一致。 */
    static final int MAX_RECIPIENTS_PER_ROUND = 500;

    private final FeedInboxMapper feedInboxMapper;
    private final FeedPushProperties properties;

    public FeedInboxCleanupJob(FeedInboxMapper feedInboxMapper, FeedPushProperties properties) {
        this.feedInboxMapper = feedInboxMapper;
        this.properties = properties;
    }

    @Scheduled(fixedDelay = 3600000)
    public void trimOverCapacityInboxes() {
        try {
            int capacity = properties.getInboxCapacity();
            List<Long> recipients = feedInboxMapper.selectOverCapacityRecipients(capacity);
            if (recipients == null || recipients.isEmpty()) {
                return;
            }
            int deleted = 0;
            for (Long recipientId : recipients) {
                // 保留该收件人最新的 capacity 条（按 score 倒序、id 倒序），删除其余。
                deleted += feedInboxMapper.deleteInboxOverflow(recipientId, capacity);
            }
            log.info("收件箱容量清理完成，本轮处理收件人={} 人，capacity={}，删除={} 条",
                    recipients.size(), capacity, deleted);
        } catch (Exception e) {
            log.warn("收件箱容量清理失败，等下一轮（1 小时后）再试", e);
        }
    }
}
