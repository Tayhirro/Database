package com.hmdp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "hmdp.upload")
public class BlogImageProperties {

    private String root;

    private String publicPrefix = "/imgs/";

    private long maxBytes = 5 * 1024 * 1024L;

    private int maxWidth = 10000;

    private int maxHeight = 10000;

    private long maxPixels = 40_000_000L;

    private long tempRetentionHours = 24L;

    private int cleanupBatchSize = 100;

    /** DELETING 资产失败后的固定退避时间；后续可替换为 Outbox 消费者的指数退避。 */
    private long deletingRetryDelayMinutes = 5L;
}
