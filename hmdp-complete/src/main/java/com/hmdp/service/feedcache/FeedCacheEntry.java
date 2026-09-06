package com.hmdp.service.feedcache;

/*
 * 现实业务背景：Feed 快照不仅要记住博客 ID，还要保留发布时间，以便 Redis 快照失效后按稳定边界回源
 * （游标里携带“上一页最后一条的时间 + ID”，回源 SQL 用它们切分同一毫秒发布的博客）。
 * 实际触发：{@link FeedCacheService}（把已生成的 Feed 分页结果缓存到 Redis 的服务）
 * 写入或读取 Redis List 元素时，对该轻量条目执行序列化和解析。
 *
 * 序列化格式是一条字符串：“博客 id” + "|" + “发布时间的 UTC 毫秒时间戳”，
 * 例如 blogId=42、createTime=2026-01-01 00:00:00 UTC 时就是 "42|1767225600000"。
 * 它作为 Redis List（快照 key 下的一个元素）存储；parse() 遇到 null、
 * 分段数不是 2、或数字解析失败时返回 null，由调用方跳过这条坏数据。
 */

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedCacheEntry {

    /** 快照里的博客 id，对应 tb_blog.id。 */
    private Long blogId;
    /** 博客发布时间的 UTC 毫秒时间戳；上游写入快照时把 null 时间按 0 处理。 */
    private Long createTime;

    public String serialize() {
        return blogId + "|" + createTime;
    }

    public static FeedCacheEntry parse(String value) {
        if (value == null) {
            return null;
        }
        String[] parts = value.split("\\|", -1);
        if (parts.length != 2) {
            return null;
        }
        try {
            return new FeedCacheEntry(Long.valueOf(parts[0]), Long.valueOf(parts[1]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
