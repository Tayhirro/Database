package com.hmdp.service.feed.push;

/*
 * 现实业务背景：博客写库成功后要把新内容推给作者的粉丝（推模式收件箱），但推送是一批
 * 耗时的数据库写操作，绝不能拖慢发布接口，更不能在发布事务回滚后留下“博客不存在、收件箱却有记录”的脏数据。
 * 实际触发：BlogCommandService.publish() 在博客和图片都写成功后，通过 ApplicationEventPublisher
 * 在事务内发布本事件；{@link BlogPublishedEventListener}（博客发布事件监听器）在事务提交后
 * （AFTER_COMMIT 阶段）才消费它，转交给 {@link FeedPushService}（推模式收件箱写入服务）执行。
 * 事件只携带三个事实字段，不携带博客内容和粉丝列表。
 *
 * 例子：用户 1000 发布博客 301，发布时间记为 1720000000000（UTC epoch 毫秒），
 * 事务提交后监听器收到的事件就是 BlogPublishedEvent(blogId=301, authorId=1000, createTime=1720000000000)。
 */

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BlogPublishedEvent {

    /** 新发布的博客 id。 */
    private final Long blogId;

    /** 博客作者（发布者）的用户 id，推送时用它查粉丝列表。 */
    private final Long authorId;

    /** 博客发布时间，UTC epoch 毫秒；写入收件箱时直接作为 score（排序分值）。 */
    private final Long createTime;
}
