package com.hmdp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmdp.entity.FeedInbox;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 推模式收件箱（tb_feed_inbox）Mapper：除 MyBatis-Plus 通用 CRUD 外，提供四条专用 SQL——
 * 收件箱召回（"inbox" 通道）、批量推送（FeedPushService）、博客删除清理（BlogCommandService）、
 * 容量清理（FeedInboxCleanupJob）。
 */
public interface FeedInboxMapper extends BaseMapper<FeedInbox> {

    /**
     * 收件箱召回（"inbox" 通道用）：按收件人取博客 ID，按 score 倒序、blog_id 倒序返回。
     *
     * 翻页边界 =（上一页最后一条的 score 时间戳毫秒，blog_id）：
     * maxTime 为空（首页请求）不加时间条件；
     * 有 maxTime 无 lastId 时取 score 早于 maxTime 的记录；
     * 两者都有时取 score 早于 maxTime，或 score 等于 maxTime 且 blog_id 小于 lastId——
     * score 只精确到毫秒，同一毫秒内发布的博客靠 blog_id 分出先后，保证翻页不重复、不漏内容。
     * 查询走 (recipient_id, score) 索引，limit 由 RecallContext 传入（BlogFeedService 固定 200）。
     *
     * @param maxTime 游标时间边界（上一页最后一条的 score，UTC epoch 毫秒），为空时不限
     * @param lastId  游标 ID 边界（与 maxTime 同一毫秒时取 blog_id 更小的），可空
     * @param limit   最多返回条数
     */
    @Select("<script>" +
            "SELECT blog_id FROM tb_feed_inbox " +
            "WHERE recipient_id = #{userId} " +
            "<choose>" +
            "<when test='maxTime == null'></when>" +
            "<when test='lastId == null'>AND score &lt; #{maxTime}</when>" +
            "<otherwise>AND (score &lt; #{maxTime} OR (score = #{maxTime} AND blog_id &lt; #{lastId}))</otherwise>" +
            "</choose>" +
            "ORDER BY score DESC, blog_id DESC " +
            "LIMIT #{limit}" +
            "</script>")
    List<Long> selectInboxBlogIds(@Param("userId") Long userId,
                                  @Param("maxTime") Long maxTime,
                                  @Param("lastId") Long lastId,
                                  @Param("limit") int limit);

    /**
     * 批量推送（FeedPushService 每页 1000 个粉丝调一次，一页 1 条 SQL）：
     * INSERT IGNORE 依赖 UNIQUE(recipient_id, blog_id)，同一篇博客重复推送或重复发布事件时
     * 重复行被数据库直接忽略，天然幂等。score = 博客发布时间的 UTC epoch 毫秒，
     * create_time = NOW()（数据库当前时间，由建表默认值同款的 TIMESTAMP 语义维护）。
     *
     * @param recipients 本页粉丝（收件人）用户 ID 列表
     * @param blogId     新博客 ID
     * @param score      排序分值（发布时间 UTC epoch 毫秒）
     * @return 实际插入行数（被 IGNORE 的重复收件人不计入）
     */
    @Insert("<script>" +
            "INSERT IGNORE INTO tb_feed_inbox (recipient_id, blog_id, score, create_time) VALUES " +
            "<foreach collection='recipients' item='recipientId' separator=','>" +
            "(#{recipientId}, #{blogId}, #{score}, NOW())" +
            "</foreach>" +
            "</script>")
    int insertIgnoreBatch(@Param("recipients") List<Long> recipients,
                          @Param("blogId") Long blogId,
                          @Param("score") Long score);

    /**
     * 博客删除时清理该博客的全部收件箱记录（BlogCommandService.delete() 在事务内调用）。
     * 走 blog_id 索引（建表迁移 V3 的 idx_blog），一篇博客通常只有几百到几千行。
     */
    @Delete("DELETE FROM tb_feed_inbox WHERE blog_id = #{blogId}")
    int deleteByBlogId(@Param("blogId") Long blogId);

    /**
     * 找出收件箱超过容量上限的收件人（FeedInboxCleanupJob 每小时调用一次）：
     * GROUP BY recipient_id 后只保留记录数大于 capacity 的收件人，
     * 单轮最多 500 人，防止一次清理拖太久；还有更多人时下一轮（1 小时后）继续。
     */
    @Select("SELECT recipient_id FROM tb_feed_inbox " +
            "GROUP BY recipient_id HAVING COUNT(*) > #{capacity} LIMIT 500")
    List<Long> selectOverCapacityRecipients(@Param("capacity") int capacity);

    /**
     * 保留某收件人最新的 keep 条（默认 200），删除更旧的（FeedInboxCleanupJob 调用）。
     * 排序与召回一致：score 倒序、id 倒序，id 用来同一毫秒内分先后。
     * MySQL 5.7 不允许 DELETE 的子查询直接引用同表，所以内层先查出要保留的 keep 条 id，
     * 再包一层派生表（外层 SELECT）绕开限制，最后删除不在这份名单里的记录。
     *
     * @return 实际删除的行数
     */
    @Delete("DELETE FROM tb_feed_inbox " +
            "WHERE recipient_id = #{recipientId} " +
            "AND id NOT IN (" +
            "SELECT id FROM (" +
            "SELECT id FROM tb_feed_inbox WHERE recipient_id = #{recipientId} " +
            "ORDER BY score DESC, id DESC LIMIT #{keep}" +
            ") keep" +
            ")")
    int deleteInboxOverflow(@Param("recipientId") Long recipientId, @Param("keep") int keep);
}
