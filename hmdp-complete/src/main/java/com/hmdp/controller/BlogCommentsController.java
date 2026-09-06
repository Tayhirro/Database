package com.hmdp.controller;


import com.hmdp.dto.BlogCommentCreateRequest;
import com.hmdp.dto.Result;
import com.hmdp.service.IBlogCommentsService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 *  前端控制器
 * @author 虎哥
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/blog-comments")
public class BlogCommentsController {

    private final IBlogCommentsService blogCommentsService;

    public BlogCommentsController(IBlogCommentsService blogCommentsService) {
        this.blogCommentsService = blogCommentsService;
    }

    /** 用户在博客详情发布一级评论或回复。 */
    @PostMapping
    public Result createComment(@RequestBody BlogCommentCreateRequest request) {
        return blogCommentsService.createComment(request);
    }

    /** 用户打开博客详情或继续下拉时分页读取评论。 */
    @GetMapping
    public Result queryComments(
            @RequestParam("blogId") Long blogId,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "20") Integer limit
    ) {
        return blogCommentsService.queryComments(blogId, cursor, limit);
    }

    /** 评论作者删除自己的评论。 */
    @DeleteMapping("/{id}")
    public Result deleteComment(@PathVariable("id") Long id) {
        return blogCommentsService.deleteComment(id);
    }
}
