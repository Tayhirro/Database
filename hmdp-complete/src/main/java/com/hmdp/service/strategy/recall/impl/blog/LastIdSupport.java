package com.hmdp.service.strategy.recall.impl.blog;

import com.hmdp.service.strategy.recall.RecallContext;

import java.util.Map;

/**
 * 召回通道共用的小工具：从 {@link RecallContext} 的 extra 里取翻页游标 ID。
 *
 * extra 的 "lastId" 键由 BlogFeedService 放入（上一页最后一条博客的 ID），
 * 各召回通道用它配合 maxTime 表达"同一发布时刻内按 id 分先后"的边界。
 */
final class LastIdSupport {

    private LastIdSupport() {
    }

    /**
     * @return extra 里的 lastId；不存在或类型不对时返回 null（首页请求）
     */
    static Long lastIdOf(RecallContext ctx) {
        if (ctx == null || ctx.getExtra() == null) {
            return null;
        }
        Map<String, Object> extra = ctx.getExtra();
        return extra.get("lastId") instanceof Number ? ((Number) extra.get("lastId")).longValue() : null;
    }
}
