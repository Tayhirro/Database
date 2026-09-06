package com.hmdp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 推荐系统配置，对应 application.yaml 的 hmdp.recommend 前缀。
 *
 * quota 是 for_you 模式各召回通道的名额（候选池固定 200 条）：
 * for-you=80（社交偏好召回）、interest=40（兴趣召回）、cf=20（协同过滤召回）、hot=60（热门召回）。
 * 四个名额之和等于候选池大小；某个通道没取满时多余名额不会自动转移，
 * 只会让候选池小于 200，排序与快照逻辑不受影响。
 */
@Configuration
@ConfigurationProperties(prefix = "hmdp.recommend")
public class RecommendProperties {

    /** 各召回通道名额，key 与 application.yaml 的 quota 段一致（for-you/interest/cf/hot）。 */
    private Map<String, Integer> quota = new LinkedHashMap<>();

    /** 协同过滤召回取当前用户最近点过赞的博客数（种子数量）。 */
    private int cfRecentLikes = 10;

    public Map<String, Integer> getQuota() {
        return quota;
    }

    public void setQuota(Map<String, Integer> quota) {
        this.quota = quota;
    }

    /** 社交偏好召回名额，默认 80。 */
    public int getQuotaForYou() {
        return quota.getOrDefault("for-you", 80);
    }

    /** 兴趣召回名额，默认 40。 */
    public int getQuotaInterest() {
        return quota.getOrDefault("interest", 40);
    }

    /** 协同过滤召回名额，默认 20。 */
    public int getQuotaCf() {
        return quota.getOrDefault("cf", 20);
    }

    /** 热门召回名额，默认 60。 */
    public int getQuotaHot() {
        return quota.getOrDefault("hot", 60);
    }

    public int getCfRecentLikes() {
        return cfRecentLikes;
    }

    public void setCfRecentLikes(int cfRecentLikes) {
        this.cfRecentLikes = cfRecentLikes;
    }
}
