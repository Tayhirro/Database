package com.hmdp.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 博客评论响应。
 *
 * 评论 Entity 不直接返回前端；作者和被回复用户只暴露公开摘要，一级评论附带当前已有回复。
 */
@Data
@Accessors(chain = true)
public class BlogCommentDTO {

    private Long id;
    private Long blogId;
    private Long userId;
    private Long parentId;
    private Long answerId;
    private String content;
    private Integer liked;
    private LocalDateTime createTime;
    private UserDTO author;
    private UserDTO answerUser;
    private List<BlogCommentDTO> replies = Collections.emptyList();
}
