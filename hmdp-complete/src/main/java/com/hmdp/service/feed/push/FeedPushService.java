package com.hmdp.service.feed.push;

/*
 * 现实业务背景：发布博客时把新内容预写进每个粉丝的收件箱（tb_feed_inbox），粉丝打开关注流时
 * 直接按收件人读收件箱（推模式，"inbox" 召回通道），不用再按关注列表实时圈定作者范围。
 * 收件箱本质是可重建的缓存：某次推送失败或被跳过都没关系，读侧的 follow 通道（拉模式）
 * 会按关注列表实时查博客兜底，所以本服务任何失败都只记日志，绝不向上抛。
 * 实际触发：博客发布事务提交后，{@link BlogPublishedEventListener}（博客发布事件监听器）
 * 携带 {@link BlogPublishedEvent}（博客发布事件：blogId、authorId、发布时间毫秒）调用 pushToFanInbox。
 *
 * 推送规则（数字与代码一致）：
 * 1. 先数粉丝：SELECT COUNT(*) FROM tb_follow WHERE follow_user_id = 作者ID，1 条 SQL。
 * 2. 粉丝数不超过 fan-threshold（默认 5000，{@link com.hmdp.config.FeedPushProperties} 提供）才推送；
 *    超过视为大 V，一条收件箱记录都不写，直接记 INFO 日志，由拉模式兜底。
 *    例如作者有 6000 个粉丝：只跑 1 条 COUNT SQL 就结束；有 4999 个粉丝则继续推送。
 * 3. 分页取粉丝：每页 1000 个（FAN_PAGE_SIZE），按 user_id 升序做游标翻页
 *    （WHERE follow_user_id = 作者ID AND user_id 大于上一页最后的 user_id，LIMIT 1000），
 *    避免深分页 OFFSET 全表扫描。
 * 4. 每页 1 条批量 INSERT IGNORE 写入 tb_feed_inbox：score = 事件的 createTime（UTC epoch 毫秒），
 *    create_time = NOW()（数据库时间）；UNIQUE(recipient_id, blog_id) 保证同一篇博客
 *    重复推送、重复发布事件都幂等。4999 个粉丝时共 5 批：5 条批量 INSERT（4 批满页 1000 行、
 *    最后一批 999 行），加上 1 条 COUNT 和 5 条粉丝分页查询，一轮最多 11 条 SQL。
 * 5. 单次推送的收件人达到 fan-threshold 就停止并记 INFO 日志（防御场景：数粉丝和翻页之间
 *    又有新用户关注了作者，实际粉丝数超过当初的统计值），剩余粉丝交给拉模式。
 */

import com.hmdp.config.FeedPushProperties;
import com.hmdp.mapper.FeedInboxMapper;
import com.hmdp.mapper.FollowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FeedPushService {

    /** 每批拉取并写入的粉丝数量：fan-threshold 取默认 5000 时，一次发布最多 5 批。 */
    static final int FAN_PAGE_SIZE = 1000;

    private final FollowMapper followMapper;
    private final FeedInboxMapper feedInboxMapper;
    private final FeedPushProperties properties;

    public FeedPushService(FollowMapper followMapper,
                           FeedInboxMapper feedInboxMapper,
                           FeedPushProperties properties) {
        this.followMapper = followMapper;
        this.feedInboxMapper = feedInboxMapper;
        this.properties = properties;
    }

    /**
     * 把一篇新博客推送到作者的粉丝收件箱。整个方法兜底捕获异常只记日志：
     * 收件箱是可重建缓存，推送失败由 follow 通道（拉模式）在读取时兜底，不影响发布结果。
     */
    public void pushToFanInbox(BlogPublishedEvent event) {
        if (event == null || event.getBlogId() == null || event.getAuthorId() == null) {
            log.warn("推模式收件箱事件字段缺失，跳过推送：event={}", event);
            return;
        }
        try {
            doPush(event);
        } catch (Exception e) {
            log.warn("推模式收件箱写入失败，blogId={}, authorId={}",
                    event.getBlogId(), event.getAuthorId(), e);
        }
    }

    private void doPush(BlogPublishedEvent event) {
        Long blogId = event.getBlogId();
        Long authorId = event.getAuthorId();
        Long score = event.getCreateTime();
        if (score == null || score <= 0) {
            log.warn("推模式收件箱缺少发布时间，跳过推送，blogId={}, authorId={}", blogId, authorId);
            return;
        }

        int threshold = properties.getFanThreshold();
        int fanCount = followMapper.countFollowers(authorId);
        if (fanCount > threshold) {
            log.info("作者粉丝数超过推模式阈值，跳过收件箱推送（拉模式兜底），"
                            + "blogId={}, authorId={}, fanCount={}, threshold={}",
                    blogId, authorId, fanCount, threshold);
            return;
        }
        if (fanCount == 0) {
            log.info("作者没有粉丝，无需写入收件箱，blogId={}, authorId={}", blogId, authorId);
            return;
        }

        long lastFanId = 0L;
        int pushed = 0;
        while (true) {
            List<Long> fans = followMapper.listFollowerIdsAfter(authorId, lastFanId, FAN_PAGE_SIZE);
            if (fans == null || fans.isEmpty()) {
                break;
            }
            // 同一篇博客重复推送时，UNIQUE(recipient_id, blog_id) + INSERT IGNORE 保证幂等不重复。
            feedInboxMapper.insertIgnoreBatch(fans, blogId, score);
            pushed += fans.size();
            lastFanId = fans.get(fans.size() - 1);
            if (fans.size() < FAN_PAGE_SIZE) {
                // 最后一批不满页（如 4999 个粉丝的最后一批 999 个），说明粉丝已全部取完。
                break;
            }
            if (pushed >= threshold) {
                log.info("推模式收件箱单次推送收件人达到阈值，停止写入（拉模式兜底），"
                                + "blogId={}, authorId={}, pushed={}, threshold={}",
                        blogId, authorId, pushed, threshold);
                break;
            }
        }
        log.info("推模式收件箱写入完成，blogId={}, authorId={}, 收件人数={}", blogId, authorId, pushed);
    }
}
