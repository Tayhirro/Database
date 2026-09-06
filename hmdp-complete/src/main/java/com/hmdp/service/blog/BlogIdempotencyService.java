package com.hmdp.service.blog;

/*
 * 现实业务背景：用户双击发布，或者发布成功响应在网络中丢失后再次提交，不能创建两篇相同博客。
 * 实际触发：BlogCommandService.publish() 在真正写博客前调用 begin()，成功后调用 complete() 保存首次 blogId。
 */

import com.hmdp.entity.IdempotencyRecord;
import com.hmdp.exception.BusinessException;
import com.hmdp.mapper.IdempotencyRecordMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 防止同一份发布请求创建出多篇博客。
 *
 * 为什么会收到重复请求：用户可能双击发布；也可能第一次已经成功，
 * 但成功响应在网络中丢失，浏览器只好再次发送完全相同的请求。
 *
 * 完整处理过程：
 * 
 *     1. 前端给一次发布生成 {@code clientRequestId}（1-64 位字母、数字、_ 或 -），同一次重试继续使用这个 ID。
 *     2. 服务端把商户 ID、标题、正文、图片 ID 列表按“长度前缀 + 内容”格式拼接后计算 SHA-256 摘要，
 *     得到 {@code requestHash}，用于判断两次请求内容是否完全相同（拼接方式见 BlogCommandService.calculatePublishHash）。
 *     3. 数据库通过 tb_idempotency_record 表上“user_id（用户 ID）+ request_key（请求键，即 clientRequestId）”
 *     两列的唯一约束，只允许一个并发请求取得创建资格：后来者重复插入不会新建行，
 *     只会通过 ON DUPLICATE KEY UPDATE 的 LAST_INSERT_ID 取回已有记录的 ID，从而和第一个请求读同一条记录。
 *     4. 第一次请求创建博客并记下博客 ID；以后收到相同请求时，不再创建，只把第一次的博客 ID 再返回一次。
 * 四个容易误解的设计原因：
 * 
 *     5. 记录为什么单独存（tb_idempotency_record 表，和博客表分开）：用户删除博客后，旧请求仍可能因为网络延迟再次到达。
 *     保留请求记录可以阻止旧请求把已删除的博客重新创建出来。此时只返回原博客 ID，不会恢复博客。
 *     6. 为什么先查请求记录（在校验商户、图片之前）：第一次发布后，图片已从临时状态变成已绑定，商户以后也可能被删除。
 *     相同请求再次到达时应该返回第一次结果，而不是拿变化后的图片或商户重新校验并报错。
 *     7. ownerToken 是什么：它是每个并发请求自己生成的一次性随机号码（去横线的 32 位 UUID，写入记录的 owner_token 列）。
 *     两个相同请求即使最终读同一条记录，也只有随机号码与数据库中一致的第一个请求可以创建博客，
 *     标记成功时的 UPDATE 条件同样带 owner_token，另一个请求必须等待或返回冲突。
 *     8. 为什么放在一个事务：请求记录、博客、图片绑定和成功状态要么全部提交，要么全部撤销，
 *     防止出现“记录显示成功但博客不存在”或“博客已创建但系统忘了这次请求”的半成品。
 * 
 */
@Service
public class BlogIdempotencyService {

    private static final long RETENTION_DAYS = 30L;

    private final IdempotencyRecordMapper mapper;

    public BlogIdempotencyService(IdempotencyRecordMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 判断当前请求应该继续创建博客，还是直接返回第一次创建的博客 ID。
     * 返回值是一个 IdempotencyDecision（决定对象，见本包 IdempotencyDecision）：继续创建（本请求取得资格）或返回旧结果两种。
     */
    public IdempotencyDecision begin(Long userId, String requestKey, String requestHash) {
        LocalDateTime now = LocalDateTime.now();
        mapper.deleteExpiredKey(userId, requestKey, now);

        String ownerToken = UUID.randomUUID().toString().replace("-", "");
        IdempotencyRecord candidate = new IdempotencyRecord()
                .setUserId(userId)
                .setRequestKey(requestKey)
                .setRequestHash(requestHash)
                .setResourceType(IdempotencyRecord.RESOURCE_BLOG)
                .setStatus(IdempotencyRecord.STATUS_PROCESSING)
                .setOwnerToken(ownerToken)
                .setExpireTime(now.plusDays(RETENTION_DAYS));
        mapper.insertOrGetId(candidate);
        if (candidate.getId() == null) {
            throw new IllegalStateException("获取幂等记录失败");
        }

        IdempotencyRecord persisted = mapper.selectByIdForUpdate(candidate.getId());
        if (persisted == null) {
            throw new IllegalStateException("幂等记录回读失败");
        }
        if (!requestHash.equals(persisted.getRequestHash())) {
            throw BusinessException.conflict(
                    "IDEMPOTENCY_KEY_REUSED",
                    "clientRequestId 已用于另一份发布内容");
        }
        if (IdempotencyRecord.STATUS_SUCCEEDED.equals(persisted.getStatus())) {
            if (persisted.getResourceId() == null) {
                throw new IllegalStateException("已完成幂等记录缺少资源ID");
            }
            return IdempotencyDecision.returnPreviousResult(
                    persisted.getId(), persisted.getResourceId());
        }
        if (ownerToken.equals(persisted.getOwnerToken())
                && IdempotencyRecord.STATUS_PROCESSING.equals(persisted.getStatus())) {
            return IdempotencyDecision.createBlog(persisted.getId(), ownerToken);
        }
        throw BusinessException.conflict("IDEMPOTENCY_IN_PROGRESS", "相同发布请求正在处理中，请稍后重试");
    }

    /**
     * 第一次请求创建博客后，保存博客 ID 并把记录标记为成功。
     * ownerToken 条件保证只有取得创建资格的请求可以完成这条记录。
     */
    public void complete(IdempotencyDecision decision, Long blogId) {
        if (decision == null || decision.shouldUsePreviousResult() || blogId == null) {
            throw new IllegalArgumentException("幂等完成参数非法");
        }
        if (mapper.markSucceeded(
                decision.getRecordId(),
                decision.getOwnerToken(),
                blogId,
                String.valueOf(blogId)) != 1) {
            throw new IllegalStateException("完成幂等记录失败");
        }
    }

    /** 分批删除超过保留期限（30 天，见 RETENTION_DAYS）的请求记录，避免表无限增长。
     *  每次调用只删一批：SQL 带 LIMIT，最多删除 batchSize 条并返回本次实际删除行数；调用方循环调用直到返回 0。 */
    public int cleanupExpired(int batchSize) {
        return mapper.deleteExpiredBatch(LocalDateTime.now(), Math.max(1, batchSize));
    }
}
