package com.hmdp.controller;

import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.exception.BusinessException;
import com.hmdp.service.IBlogImageService;
import com.hmdp.utils.UserHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload/blog")
public class UploadController {

    private final IBlogImageService blogImageService;

    public UploadController(IBlogImageService blogImageService) {
        this.blogImageService = blogImageService;
    }

    @PostMapping
    public Result uploadImage(@RequestParam("file") MultipartFile image) {
        return Result.ok(blogImageService.upload(image, currentUserId()));
    }

    @DeleteMapping("/{imageId}")
    public Result deleteBlogImage(@PathVariable("imageId") Long imageId) {
        blogImageService.deleteTemporaryImage(imageId, currentUserId());
        return Result.ok();
    }

    private Long currentUserId() {
        UserDTO user = UserHolder.getUser();
        if (user == null || user.getId() == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        return user.getId();
    }
}
