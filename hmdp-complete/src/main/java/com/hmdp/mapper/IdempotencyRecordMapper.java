package com.hmdp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmdp.entity.IdempotencyRecord;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * 幂等记录的数据访问接口。
 *
 * 这里的 SQL 不负责创建博客，只负责保证多个相同请求中只有一个能取得创建资格，
 * 并保存第一次请求的最终结果。
 */
public interface IdempotencyRecordMapper extends BaseMapper<IdempotencyRecord> {

    /**
     * 尝试插入请求记录。
     *
     * 第一次请求会插入新行；相同“用户 ID + requestKey”已经存在时，SQL 不修改原内容，
     * 只通过 {@code LAST_INSERT_ID(id)} 把原记录 ID 放回 {@code record.id}。
     * 因此调用结束后，无论谁先到达，都能继续查询同一条记录。
     */
    @Insert("INSERT INTO tb_idempotency_record " +
            "(user_id, request_key, request_hash, resource_type, status, owner_token, expire_time) " +
            "VALUES (#{userId}, #{requestKey}, #{requestHash}, #{resourceType}, #{status}, " +
            "#{ownerToken}, #{expireTime}) " +
            "ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id)")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertOrGetId(IdempotencyRecord record);

    /**
     * 查询并锁住请求记录，直到当前事务结束；相同请求会按顺序判断，不能同时创建博客。
     */
    @Select("SELECT * FROM tb_idempotency_record WHERE id = #{id} FOR UPDATE")
    IdempotencyRecord selectByIdForUpdate(@Param("id") Long id);

    /**
     * 保存第一次创建的博客 ID 并标记成功。
     * 只有记录仍在处理中且 ownerToken 属于当前请求时才能更新一行。
     */
    @Update("UPDATE tb_idempotency_record " +
            "SET resource_id = #{resourceId}, response_data = #{responseData}, status = 'SUCCEEDED' " +
            "WHERE id = #{id} AND owner_token = #{ownerToken} AND status = 'PROCESSING'")
    int markSucceeded(
            @Param("id") Long id,
            @Param("ownerToken") String ownerToken,
            @Param("resourceId") Long resourceId,
            @Param("responseData") String responseData
    );

    /** 当前 key 已超过保留期时先删除它，使客户端以后可以重新使用该 key。 */
    @Delete("DELETE FROM tb_idempotency_record " +
            "WHERE user_id = #{userId} AND request_key = #{requestKey} " +
            "AND expire_time <= #{now}")
    int deleteExpiredKey(
            @Param("userId") Long userId,
            @Param("requestKey") String requestKey,
            @Param("now") LocalDateTime now
    );

    /** 定时任务按批次清理所有过期记录，防止一次删除太多行。 */
    @Delete("DELETE FROM tb_idempotency_record WHERE expire_time <= #{now} LIMIT #{limit}")
    int deleteExpiredBatch(@Param("now") LocalDateTime now, @Param("limit") int limit);
}
