package com.hmdp.service.blog;

/*
 * 现实业务背景：博客详情、热榜、作者主页或 Feed 流查出数据库 Blog（tb_blog 表对应的实体）后，
 * 前端还需要作者昵称、头像和当前用户是否点赞，这些信息不在博客表里。
 * 实际触发：BlogQueryService（博客只读查询服务，本包）或 BlogFeedService（个性化 Feed 流服务，service/feed 包）
 * 准备返回页面数据时调用；本类批量补齐关联信息并转换成 Detail/Card DTO（返回给前端的字段集合）。
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.dto.BlogCardDTO;
import com.hmdp.dto.BlogDetailDTO;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Blog;
import com.hmdp.entity.BlogLike;
import com.hmdp.entity.User;
import com.hmdp.mapper.BlogLikeMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.UserHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 把数据库查询出的博客（Blog 实体）转换成真正返回给前端的数据（DTO，只含接口允许公开的字段）。
 *     1. 一次处理整页数据，避免 N+1 查询：以一页 20 篇博客为例，调用方先用第 1 条 SQL 查出这 20 篇 Blog；
 *     本类的 loadEnrichment 再执行第 2 条 SQL，把 20 篇的作者 ID（blog.user_id）去重后一次性查回作者（tb_user 表），
 *     第 3 条 SQL 一次性查回当前用户对这 20 篇的点赞记录（tb_blog_like 表中 user_id = 当前用户且 blog_id IN 这 20 个博客 ID）。
 *     整页共 3 条 SQL；若不批量、每篇博客单独查一次作者和点赞，则要 1 + 20×2 = 41 条。
 *     2. 查询后在内存中配对：通过 ID 把作者和点赞状态放回对应博客，
 *     保持 Mapper（MyBatis-Plus 数据访问接口）返回的博客顺序，不再产生额外 SQL。
 *     3. 不把数据库对象直接返回：Entity（如 Blog，与数据库表一行记录对应）是与表结构绑定的对象；
 *     DTO 是接口允许公开的字段集合。显式转换后，即使数据库以后新增内部列，也不会自动出现在前端响应中。
 *     4. 列表和详情分开：列表使用 {@link BlogCardDTO}（博客列表卡片 DTO，只有标题、封面图、点赞数等，不含完整正文）；
 *     用户进入详情页后才通过 {@link BlogDetailDTO}（博客详情 DTO，额外包含完整正文 content）获取正文，从而减少列表响应大小。
 * 
 */
@Component
public class BlogAssembler {

    private final IUserService userService;
    private final BlogLikeMapper blogLikeMapper;

    public BlogAssembler(IUserService userService, BlogLikeMapper blogLikeMapper) {
        this.userService = userService;
        this.blogLikeMapper = blogLikeMapper;
    }

    public BlogDetailDTO toDetail(Blog blog) {
        if (blog == null) {
            return null;
        }
        Enrichment enrichment = loadEnrichment(Collections.singletonList(blog));
        User author = enrichment.authorById.get(blog.getUserId());
        return new BlogDetailDTO()
                .setId(blog.getId())
                .setShopId(blog.getShopId())
                .setUserId(blog.getUserId())
                .setIcon(author == null ? null : author.getIcon())
                .setName(author == null ? null : author.getNickName())
                .setIsLike(enrichment.likedBlogIds.contains(blog.getId()))
                .setTitle(blog.getTitle())
                .setImages(blog.getImages())
                .setContent(blog.getContent())
                .setLiked(normalizeCount(blog.getLiked()))
                .setComments(normalizeCount(blog.getComments()))
                .setCreateTime(blog.getCreateTime())
                .setUpdateTime(blog.getUpdateTime());
    }

    public List<BlogCardDTO> toCards(List<Blog> blogs) {
        if (blogs == null || blogs.isEmpty()) {
            return Collections.emptyList();
        }
        Enrichment enrichment = loadEnrichment(blogs);
        return blogs.stream().map(blog -> {
            User author = enrichment.authorById.get(blog.getUserId());
            return new BlogCardDTO()
                    .setId(blog.getId())
                    .setShopId(blog.getShopId())
                    .setUserId(blog.getUserId())
                    .setIcon(author == null ? null : author.getIcon())
                    .setName(author == null ? null : author.getNickName())
                    .setIsLike(enrichment.likedBlogIds.contains(blog.getId()))
                    .setTitle(blog.getTitle())
                    .setImages(blog.getImages())
                    .setLiked(normalizeCount(blog.getLiked()))
                    .setComments(normalizeCount(blog.getComments()))
                    .setCreateTime(blog.getCreateTime());
        }).collect(Collectors.toList());
    }

    private Enrichment loadEnrichment(List<Blog> blogs) {
        Set<Long> authorIds = blogs.stream()
                .map(Blog::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, User> authorById = new HashMap<>(authorIds.size());
        if (!authorIds.isEmpty()) {
            for (User author : userService.listByIds(authorIds)) {
                authorById.put(author.getId(), author);
            }
        }

        UserDTO currentUser = UserHolder.getUser();
        Set<Long> likedBlogIds = new HashSet<>();
        if (currentUser != null && currentUser.getId() != null) {
            List<Long> blogIds = blogs.stream()
                    .map(Blog::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (!blogIds.isEmpty()) {
                List<BlogLike> likes = blogLikeMapper.selectList(new LambdaQueryWrapper<BlogLike>()
                        .select(BlogLike::getBlogId)
                        .eq(BlogLike::getUserId, currentUser.getId())
                        .in(BlogLike::getBlogId, blogIds));
                likedBlogIds.addAll(likes.stream()
                        .map(BlogLike::getBlogId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()));
            }
        }

        return new Enrichment(authorById, likedBlogIds);
    }

    private int normalizeCount(Integer count) {
        return count == null ? 0 : Math.max(count, 0);
    }

    private static class Enrichment {

        private final Map<Long, User> authorById;
        private final Set<Long> likedBlogIds;

        private Enrichment(Map<Long, User> authorById, Set<Long> likedBlogIds) {
            this.authorById = authorById;
            this.likedBlogIds = likedBlogIds;
        }
    }
}
