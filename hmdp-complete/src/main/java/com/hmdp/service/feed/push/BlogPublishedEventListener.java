package com.hmdp.service.feed.push;

/*
 * 现实业务背景：推模式收件箱写入必须等发布事务真正提交之后进行——如果在事务内就写收件箱，
 * 发布一旦回滚，收件箱里会留下指向不存在博客的脏记录。
 * 实际触发：Spring 在 BlogPublishedEvent 对应的事务提交后（AFTER_COMMIT 阶段）回调本监听器，
 * 把事件转交给 {@link FeedPushService}（推模式收件箱写入服务：统计作者粉丝、按阈值分批写 tb_feed_inbox）。
 * FeedPushService 内部自行捕获全部异常只记日志，所以监听器不需要额外的兜底逻辑。
 * 监听方法带 fallbackExecution = true：如果发布方不在事务里（理论兜底场景），
 * 事件也会立即执行而不是被丢弃。
 *
 * 例子：publish() 提交事务（博客 301、作者 1000、发布时间 1720000000000 毫秒）成功返回后，
 * 本监听器被调用，执行 feedPushService.pushToFanInbox(事件)，
 * 由它数作者 1000 的粉丝并按阈值决定是否逐批写入 tb_feed_inbox。
 */

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlogPublishedEventListener {

    private final FeedPushService feedPushService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleBlogPublished(BlogPublishedEvent event) {
        feedPushService.pushToFanInbox(event);
    }
}
