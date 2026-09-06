package com.hmdp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 推模式 Feed 配置，对应 application.yaml 的 hmdp.feed.push 前缀。
 *
 * fan-threshold（默认 5000）：作者粉丝数不超过它时，发布博客把新内容写进每个粉丝的收件箱
 * （推模式，{@link com.hmdp.service.feed.push.FeedPushService} 执行）；
 * 超过视为大 V，跳过推送（否则一次发布要写几万行收件箱记录），读侧由 follow 通道（拉模式）兜底。
 * 它同时也是单次推送的收件人上限：推送途中已写入数量达到阈值就停止，剩余粉丝交给拉模式。
 *
 * inbox-capacity（默认 200）：单个粉丝收件箱最多保留的博客条数，与关注流候选池大小 200 对齐；
 * {@link com.hmdp.service.feed.push.FeedInboxCleanupJob}（收件箱容量清理任务）每小时把
 * 超出容量的收件人裁剪到这个数，只保留最新的记录。
 */
@Configuration
@ConfigurationProperties(prefix = "hmdp.feed.push")
public class FeedPushProperties {

    /** 作者粉丝数的推模式阈值，默认 5000。 */
    private int fanThreshold = 5000;

    /** 单个粉丝收件箱的容量上限，默认 200。 */
    private int inboxCapacity = 200;

    public int getFanThreshold() {
        return fanThreshold;
    }

    public void setFanThreshold(int fanThreshold) {
        this.fanThreshold = fanThreshold;
    }

    public int getInboxCapacity() {
        return inboxCapacity;
    }

    public void setInboxCapacity(int inboxCapacity) {
        this.inboxCapacity = inboxCapacity;
    }
}
