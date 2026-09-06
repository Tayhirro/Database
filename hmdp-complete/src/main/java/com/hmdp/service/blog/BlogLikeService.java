package com.hmdp.service.blog;

/*
 * 现实业务背景：用户点击点赞、取消点赞，或博客详情页加载最近点赞用户时，需要维护点赞关系和总数。
 * 实际触发：PUT/DELETE /blog/{id}/like 与 GET /blog/likes/{id} 经博客门面进入本类。
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.dto.BlogLikeStateDTO;
import com.hmdp.dto.CursorPageDTO;
import com.hmdp.dto.CursorPayload;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Blog;
import com.hmdp.entity.BlogLike;
import com.hmdp.entity.User;
import com.hmdp.exception.BusinessException;
import com.hmdp.mapper.BlogLikeMapper;
import com.hmdp.mapper.BlogMapper;
import com.hmdp.service.IUserService;
import com.hmdp.service.cursor.CursorCodec;
import com.hmdp.utils.SystemConstants;
import com.hmdp.utils.UserHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DuplicateKeyException;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 负责点赞、取消点赞、查询点赞状态和读取点赞用户列表。
 *     1. 接口直接表达最终目的：PUT 表示“操作完成后必须是已点赞”，DELETE 表示“必须是未点赞”。
 *     所以同一个 PUT 因网络问题执行两次，第二次插入会命中唯一键、被捕获后忽略，
 *     不会把点赞反转成取消；DELETE 重复执行时删不到行，也不会再扣减总数。
 *     2. 数据库决定谁真正点赞成功：tb_blog_like 表上 (blog_id 被点赞的博客 ID, user_id 点赞用户 ID)
 *     两列的唯一约束保证一个用户对一篇博客只有一条关系。
 *     两个并发 PUT 中只有一个能插入成功，只有它会把博客的点赞总数（tb_blog.liked 列）加一。
 *     3. 点赞关系和总数一起成功或一起撤销：二者放在同一个事务中。
 *     返回前重新读取 MySQL 最终状态，前端直接用返回的 liked（是否已点赞）和 likeCount（点赞总数）覆盖本地值。
 *     4. 点赞用户列表用游标分页：排序规则 ORDER BY create_time（点赞时间）DESC, id（tb_blog_like 主键）DESC，
 *     id 用来在两条点赞时间相同时分先后；游标 =（score：上一页最后一条点赞记录的 createTime 转成的 UTC epoch 毫秒值，
 *     id：该条点赞记录的主键），类型标记为 "blog-like-v2"；每次多查 1 条（LIMIT pageSize + 1）来探测是否还有下一页。
 *     5. 点赞用户批量查询：以一页 50 个用户（limit 上限 50，不传默认 10）为例，第 1 条 SQL 查本页的点赞关系
 *     （tb_blog_like），第 2 条把这页的用户 ID 一次性查回用户资料（tb_user），共 2 条 SQL；
 *     用户显示顺序在内存中按点赞时间恢复，既避免为每个用户单独查询（否则要 1 + 50 = 51 条），
 *     也避免拼接难维护的动态排序 SQL。
 * 
 */
@Service
public class BlogLikeService {

    private static final String LIKE_CURSOR = "blog-like-v2";

    private final BlogMapper blogMapper;
    private final BlogLikeMapper blogLikeMapper;
    private final IUserService userService;
    private final CursorCodec cursorCodec;

    public BlogLikeService(
            BlogMapper blogMapper,
            BlogLikeMapper blogLikeMapper,
            IUserService userService,
            CursorCodec cursorCodec
    ) {
        this.blogMapper = blogMapper;
        this.blogLikeMapper = blogLikeMapper;
        this.userService = userService;
        this.cursorCodec = cursorCodec;
    }

    @Transactional
    public Result like(Long blogId) {
        Long userId = requireCurrentUserId();
        requireBlog(blogId);
        boolean inserted = false;
        try {
            inserted = blogLikeMapper.insertRelation(blogId, userId, LocalDateTime.now()) == 1;
        } catch (DuplicateKeyException ignored) {
            // 唯一键只裁决相同 (blog_id 被点赞的博客 ID, user_id 点赞用户 ID) 组合；其他数据库错误不会像 INSERT IGNORE 一样被吞掉。
        }
        if (inserted && blogMapper.incrementLiked(blogId) != 1) {
            throw BusinessException.notFound("BLOG_NOT_FOUND", "笔记不存在");
        }
        return Result.ok(queryLikeState(blogId, userId));
    }

    @Transactional
    public Result unlike(Long blogId) {
        Long userId = requireCurrentUserId();
        int deleted = blogLikeMapper.deleteRelation(blogId, userId);
        if (deleted > 0 && blogMapper.decrementLiked(blogId) != 1) {
            throw BusinessException.notFound("BLOG_NOT_FOUND", "笔记不存在");
        }
        return Result.ok(queryLikeState(blogId, userId));
    }

    public Result queryUsers(Long blogId, String cursor, Integer limit) {
        requireBlog(blogId);
        int pageSize = normalizePageSize(limit);
        CursorPayload position = cursorCodec.decode(cursor, LIKE_CURSOR);
        LambdaQueryWrapper<BlogLike> wrapper = new LambdaQueryWrapper<BlogLike>()
                .select(BlogLike::getId, BlogLike::getUserId, BlogLike::getCreateTime)
                .eq(BlogLike::getBlogId, blogId)
                .orderByDesc(BlogLike::getCreateTime, BlogLike::getId)
                .last("LIMIT " + (pageSize + 1));
        if (position != null) {
            requirePosition(position);
            LocalDateTime time = toUtcLocalDateTime(position.getScore());
            wrapper.and(query -> query.lt(BlogLike::getCreateTime, time)
                    .or(nested -> nested.eq(BlogLike::getCreateTime, time)
                            .lt(BlogLike::getId, position.getId())));
        }

        List<BlogLike> rows = blogLikeMapper.selectList(wrapper);
        boolean hasMore = rows.size() > pageSize;
        List<BlogLike> pageRows = hasMore
                ? new ArrayList<>(rows.subList(0, pageSize))
                : rows;
        List<Long> userIds = pageRows.stream()
                .map(BlogLike::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<UserDTO> users = hydrateUsersInOrder(userIds);
        String nextCursor = hasMore && !pageRows.isEmpty()
                ? encodePosition(pageRows.get(pageRows.size() - 1))
                : null;
        return Result.ok(new CursorPageDTO<>(users, nextCursor, hasMore));
    }

    private BlogLikeStateDTO queryLikeState(Long blogId, Long userId) {
        Blog blog = blogMapper.selectById(blogId);
        if (blog == null) {
            throw BusinessException.notFound("BLOG_NOT_FOUND", "笔记不存在");
        }
        boolean liked = blogLikeMapper.selectOne(new LambdaQueryWrapper<BlogLike>()
                .select(BlogLike::getId)
                .eq(BlogLike::getBlogId, blogId)
                .eq(BlogLike::getUserId, userId)
                .last("LIMIT 1")) != null;
        int count = blog.getLiked() == null ? 0 : Math.max(blog.getLiked(), 0);
        return new BlogLikeStateDTO(liked, count);
    }

    private List<UserDTO> hydrateUsersInOrder(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, User> byId = new HashMap<>();
        for (User user : userService.listByIds(userIds)) {
            byId.put(user.getId(), user);
        }
        return userIds.stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(user.getId());
                    dto.setNickName(user.getNickName());
                    dto.setIcon(user.getIcon());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private Blog requireBlog(Long blogId) {
        if (blogId == null) {
            throw BusinessException.badRequest("BLOG_ID_REQUIRED", "博客ID不能为空");
        }
        Blog blog = blogMapper.selectById(blogId);
        if (blog == null) {
            throw BusinessException.notFound("BLOG_NOT_FOUND", "笔记不存在");
        }
        return blog;
    }

    private Long requireCurrentUserId() {
        UserDTO user = UserHolder.getUser();
        if (user == null || user.getId() == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        return user.getId();
    }

    private int normalizePageSize(Integer limit) {
        if (limit == null) {
            return SystemConstants.MAX_PAGE_SIZE;
        }
        if (limit < 1 || limit > 50) {
            throw BusinessException.badRequest("INVALID_PAGE_SIZE", "limit 必须在 1 到 50 之间");
        }
        return limit;
    }

    private void requirePosition(CursorPayload payload) {
        if (payload.getScore() == null || payload.getScore() < 0
                || payload.getId() == null || payload.getId() <= 0) {
            throw BusinessException.badRequest("INVALID_CURSOR", "分页游标缺少必要位置");
        }
    }

    private String encodePosition(BlogLike last) {
        CursorPayload payload = new CursorPayload();
        payload.setType(LIKE_CURSOR);
        payload.setScore(toEpochMilli(last.getCreateTime()));
        payload.setId(last.getId());
        return cursorCodec.encode(payload);
    }

    private long toEpochMilli(LocalDateTime time) {
        if (time == null) {
            throw new IllegalStateException("点赞时间不能为空");
        }
        return time.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    private LocalDateTime toUtcLocalDateTime(long epochMilli) {
        try {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneOffset.UTC);
        } catch (DateTimeException e) {
            throw BusinessException.badRequest("INVALID_CURSOR", "分页游标时间超出有效范围");
        }
    }
}
