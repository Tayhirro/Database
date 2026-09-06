package com.hmdp.service.blog;

/*
 * 现实业务背景：幂等记录检查后，发布流程必须明确选择“继续创建”还是“返回第一次创建结果”。
 * 实际触发：BlogIdempotencyService.begin() 创建该决定，BlogCommandService.publish() 根据它选择后续动作。
 */

import lombok.Getter;

/**
 * 查询幂等记录后的处理决定（纯值对象，不可变），调用方只会得到两种结果：
 *     1. 继续创建：这是第一次请求，当前请求取得了创建资格。
 *     2. 返回旧结果：相同请求以前已经成功，不再创建博客，直接返回第一次的博客 ID。
 * 
 */
@Getter
public class IdempotencyDecision {

    /** 是否应直接返回第一次的结果，而不是再次创建博客。 */
    private final boolean usePreviousResult;

    /** 本次对应的幂等记录 ID（tb_idempotency_record 表主键，两种结果都有值）。 */
    private final Long recordId;

    /** 当前请求取得创建资格时写入 owner_token 列的一次性随机标识（去横线的 32 位 UUID）；返回旧结果时为 null。 */
    private final String ownerToken;

    /** 第一次创建的博客 ID；返回旧结果时有值，继续创建时为 null。 */
    private final Long resourceId;

    private IdempotencyDecision(
            boolean usePreviousResult,
            Long recordId,
            String ownerToken,
            Long resourceId
    ) {
        this.usePreviousResult = usePreviousResult;
        this.recordId = recordId;
        this.ownerToken = ownerToken;
        this.resourceId = resourceId;
    }

    /** 当前请求是第一个到达的请求，可以继续创建博客。 */
    public static IdempotencyDecision createBlog(Long recordId, String ownerToken) {
        return new IdempotencyDecision(false, recordId, ownerToken, null);
    }

    /** 相同请求以前已经成功，应直接返回第一次创建的博客 ID。 */
    public static IdempotencyDecision returnPreviousResult(Long recordId, Long resourceId) {
        return new IdempotencyDecision(true, recordId, null, resourceId);
    }

    /** 比 Lombok 生成的布尔 Getter 更直接地表达调用方应该执行的动作。 */
    public boolean shouldUsePreviousResult() {
        return usePreviousResult;
    }
}
