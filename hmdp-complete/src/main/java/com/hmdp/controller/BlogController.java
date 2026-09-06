package com.hmdp.controller;


import com.hmdp.dto.BlogPublishRequest;
import com.hmdp.dto.BlogUpdateRequest;
import com.hmdp.dto.Result;
import com.hmdp.service.IBlogService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 
 * 前端控制器
 * @author 虎哥
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/blog")
public class BlogController {

    @Resource
    private IBlogService blogService;

    @PostMapping
    public Result saveBlog(@RequestBody BlogPublishRequest request) {
        return blogService.saveBlog(request);
    }

    /** 编辑当前用户自己的博客。 */
    @PutMapping("/{id}")
    public Result updateBlog(
            @PathVariable("id") Long id,
            @RequestBody BlogUpdateRequest request
    ) {

        return blogService.updateBlog(id, request);
    }

    /** 删除当前用户自己的博客。 */
    @DeleteMapping("/{id}")
    public Result deleteBlog(@PathVariable("id") Long id) {
        return blogService.deleteBlog(id);
    }

    @PutMapping("/{id}/like")
    public Result likeBlog(@PathVariable("id") Long id) {
        return blogService.likeBlog(id);
    }

    @DeleteMapping("/{id}/like")
    public Result unlikeBlog(@PathVariable("id") Long id) {
        return blogService.unlikeBlog(id);
    }

    /**
     * 根据id查询博客详情
     */
    @GetMapping("/{id}")
    public Result queryBlogById(@PathVariable("id") Long id) {
        return blogService.queryBlogById(id);
    }

    /** 点赞用户榜以 MySQL 关系为权威数据源。 */
    @GetMapping("/likes/{id}")
    public Result queryBlogLikes(
            @PathVariable("id") Long id,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit
    ) {
        return blogService.queryBlogLikes(id, cursor, limit);
    }

    /** following / for_you 是稳定产品模式，排序算法不作为 API 参数暴露。 */
    @GetMapping("/feed")
    public Result queryBlogFeed(
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "mode", defaultValue = "following") String mode,
            @RequestParam(value = "refresh", defaultValue = "false") Boolean refresh
    ) {
        return blogService.queryBlogFeed(cursor, mode, refresh);
    }

    @GetMapping("/of/me")
    public Result queryMyBlog(
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit
    ) {
        return blogService.queryMyBlogs(cursor, limit);
    }

    /**
     * 查询指定用户发布的博客
     */
    @GetMapping("/of/user")
    public Result queryBlogByUserId(
            @RequestParam("id") Long id,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit
    ) {
        return blogService.queryBlogsByUserId(id, cursor, limit);
    }

    @GetMapping("/hot")
    public Result queryHotBlog(
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit
    ) {
        return blogService.queryHotBlog(cursor, limit);
    }
}
