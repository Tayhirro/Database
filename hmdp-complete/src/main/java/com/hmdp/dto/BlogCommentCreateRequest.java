package com.hmdp.dto;

import lombok.Data;

/**
 * 创建博客评论或回复的请求。
 *
 * 现实触发：用户在博客详情输入内容后发布；回复评论时同时携带一级评论和被回复评论 ID。
 */
@Data
public class BlogCommentCreateRequest {

    /** 目标博客。 */
    private Long blogId;

    /** 评论正文，第一版最多 255 个字符。 */
    private String content;

    /** 一级评论 ID；发表一级评论时为空。 */
    private Long parentId;

    /** 被回复的评论 ID；发表一级评论时为空。 */
    private Long answerId;
}
