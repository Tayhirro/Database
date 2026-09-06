package com.hmdp.service.blog;

/*
 * 现实业务背景：用户打开博客详情、热门页、我的笔记或其他作者主页时，需要读取对应博客并稳定翻页。
 * 实际触发：GET /blog/{id}、/blog/hot、/blog/of/me、/blog/of/user 经博客门面（BlogServiceImpl）进入本类。
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.dto.BlogCardDTO;
import com.hmdp.dto.CursorPageDTO;
import com.hmdp.dto.CursorPayload;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Blog;
import com.hmdp.entity.BlogImage;
import com.hmdp.exception.BusinessException;
import com.hmdp.mapper.BlogMapper;
import com.hmdp.mapper.BlogImageMapper;
import com.hmdp.service.cursor.CursorCodec;
import com.hmdp.utils.SystemConstants;
import com.hmdp.utils.UserHolder;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 负责博客详情、热门博客和指定作者博客的只读查询。
 *     1. 数据库对象不直接给前端：Mapper 只查 Entity（与 tb_blog 表对应的 Blog 实体），
 *     {@link BlogAssembler}（把查出的 Blog 批量拼装成返回给前端的卡片/详情 DTO 的组件）再挑选接口允许的字段，
 *     数据库以后加列不会偷偷改变接口返回内容。
 *     2. 整页批量补充作者和点赞状态：以一页 20 篇博客（limit 最大 50，不传默认 10）为例，
 *     第 1 条 SQL 查博客本身（tb_blog），第 2 条把 20 篇的作者 ID（blog.user_id）去重后一次性查回作者（tb_user），
 *     第 3 条一次性查回当前用户对这 20 篇的点赞记录（tb_blog_like），共 3 条 SQL；
 *     不批量则为每篇博客各查一次作者和点赞，需要 1 + 20×2 = 41 条。
 *     3. 游标记录上一页最后位置（作者博客接口）：游标 =（score：上一页最后一条博客的 createTime 发布时间
 *     转成的 UTC epoch 毫秒值，id：博客主键）；排序规则 ORDER BY create_time DESC, id DESC，
 *     id 用来在两条博客发布时间相同时分先后，续查条件即“发布时间更早，或时间相同但 id 更小”。
 *     时间统一按 UTC 转换，服务器部署在不同时区也会得到同一个位置。
 *     4. 当前热榜会实时变化：热榜游标 =（score：liked 点赞数，id：博客主键）；
 *     排序规则 ORDER BY liked DESC, id DESC，id 用来在两条博客点赞数相同时分先后。
 *     翻页期间如果点赞数变化，极少数博客仍可能重复或漏掉；只有固定一份排名快照才能完全避免。
 *     （两类列表都通过多查 1 条，LIMIT pageSize + 1，来探测是否还有下一页。）
 * 
 */
@Service
public class BlogQueryService {

    private static final String HOT_CURSOR = "blog-hot-v1";
    private static final String USER_BLOG_CURSOR = "user-blog-v2";

    private final BlogMapper blogMapper;
    private final BlogImageMapper blogImageMapper;
    private final BlogAssembler blogAssembler;
    private final CursorCodec cursorCodec;

    public BlogQueryService(
            BlogMapper blogMapper,
            BlogImageMapper blogImageMapper,
            BlogAssembler blogAssembler,
            CursorCodec cursorCodec
    ) {
        this.blogMapper = blogMapper;
        this.blogImageMapper = blogImageMapper;
        this.blogAssembler = blogAssembler;
        this.cursorCodec = cursorCodec;
    }

    public Result detail(Long id) {
        Blog blog = requireBlog(id);
        return Result.ok(blogAssembler.toDetail(blog).setImageIds(blogImageMapper.selectList(
                        new LambdaQueryWrapper<BlogImage>()
                                .select(BlogImage::getId)
                                .eq(BlogImage::getBlogId, id)
                                .eq(BlogImage::getStatus, BlogImage.STATUS_BOUND)
                                .orderByAsc(BlogImage::getSortOrder, BlogImage::getId))
                .stream()
                .map(BlogImage::getId)
                .collect(Collectors.toList())));
    }

    public Result hot(String cursor, Integer limit) {
        int pageSize = normalizePageSize(limit);
        CursorPayload position = cursorCodec.decode(cursor, HOT_CURSOR);
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<Blog>()
                .orderByDesc(Blog::getLiked, Blog::getId)
                .last("LIMIT " + (pageSize + 1));
        if (position != null) {
            requirePosition(position);
            int liked = toLikedScore(position.getScore());
            wrapper.and(query -> query.lt(Blog::getLiked, liked)
                    .or(nested -> nested.eq(Blog::getLiked, liked)
                            .lt(Blog::getId, position.getId())));
        }
        return Result.ok(toCursorPage(blogMapper.selectList(wrapper), pageSize, HOT_CURSOR, true));
    }

    public Result currentUserBlogs(String cursor, Integer limit) {
        return authorBlogs(requireCurrentUserId(), cursor, limit);
    }

    public Result userBlogs(Long userId, String cursor, Integer limit) {
        if (userId == null) {
            throw BusinessException.badRequest("USER_ID_REQUIRED", "用户ID不能为空");
        }
        return authorBlogs(userId, cursor, limit);
    }

    private Result authorBlogs(Long userId, String cursor, Integer limit) {
        int pageSize = normalizePageSize(limit);
        CursorPayload position = cursorCodec.decode(cursor, USER_BLOG_CURSOR);
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<Blog>()
                .eq(Blog::getUserId, userId)
                .orderByDesc(Blog::getCreateTime, Blog::getId)
                .last("LIMIT " + (pageSize + 1));
        if (position != null) {
            requirePosition(position);
            LocalDateTime time = toUtcLocalDateTime(position.getScore());
            wrapper.and(query -> query.lt(Blog::getCreateTime, time)
                    .or(nested -> nested.eq(Blog::getCreateTime, time)
                            .lt(Blog::getId, position.getId())));
        }
        return Result.ok(toCursorPage(
                blogMapper.selectList(wrapper), pageSize, USER_BLOG_CURSOR, false));
    }

    private CursorPageDTO<BlogCardDTO> toCursorPage(
            List<Blog> rows,
            int pageSize,
            String cursorType,
            boolean useLikedScore
    ) {
        boolean hasMore = rows.size() > pageSize;
        List<Blog> pageRows = hasMore
                ? new ArrayList<>(rows.subList(0, pageSize))
                : rows;
        String nextCursor = null;
        if (hasMore && !pageRows.isEmpty()) {
            Blog last = pageRows.get(pageRows.size() - 1);
            long score = useLikedScore
                    ? (last.getLiked() == null ? 0L : last.getLiked().longValue())
                    : toEpochMilli(last.getCreateTime());
            CursorPayload payload = new CursorPayload();
            payload.setType(cursorType);
            payload.setScore(score);
            payload.setId(last.getId());
            nextCursor = cursorCodec.encode(payload);
        }
        return new CursorPageDTO<>(blogAssembler.toCards(pageRows), nextCursor, hasMore);
    }

    private Blog requireBlog(Long id) {
        if (id == null) {
            throw BusinessException.badRequest("BLOG_ID_REQUIRED", "博客ID不能为空");
        }
        Blog blog = blogMapper.selectById(id);
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

    private int toLikedScore(Long score) {
        if (score == null || score < 0 || score > Integer.MAX_VALUE) {
            throw BusinessException.badRequest("INVALID_CURSOR", "热榜分页游标超出有效范围");
        }
        return score.intValue();
    }

    private long toEpochMilli(LocalDateTime time) {
        if (time == null) {
            throw new IllegalStateException("分页字段 createTime 不能为空");
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
