package com.hmdp.service.blog;

/*
 * 现实业务背景：用户在发布页点击发布，或作者在自己的博客上执行编辑、删除时，需要完整维护博客、图片和权限。
 * 实际触发：POST/PUT/DELETE 博客接口经 BlogServiceImpl 门面（博客模块对 Controller 的统一入口，
 * 再分发到查询/命令/点赞等服务）进入本类，事务边界也落在这些写命令上。
 */

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.dto.BlogPublishRequest;
import com.hmdp.dto.BlogUpdateRequest;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Blog;
import com.hmdp.entity.BlogComments;
import com.hmdp.entity.BlogImage;
import com.hmdp.entity.BlogLike;
import com.hmdp.exception.BusinessException;
import com.hmdp.mapper.BlogCommentsMapper;
import com.hmdp.mapper.BlogLikeMapper;
import com.hmdp.mapper.BlogMapper;
import com.hmdp.mapper.FeedInboxMapper;
import com.hmdp.service.IBlogImageService;
import com.hmdp.service.IShopService;
import com.hmdp.service.feed.push.BlogPublishedEvent;
import com.hmdp.utils.UserHolder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 负责发布、编辑和删除博客。
 *
 * 代码按下面的规则组织：
 * 
 *     1. 重复发布不重复创建：前端为一次发布生成唯一的 clientRequestId（1-64 位字母、数字、_ 或 -，重试时复用同一值），
 *     双击、超时重试等相同 POST 请求由 {@link BlogIdempotencyService}（幂等控制服务，保证同一份请求只生效一次）
 *     识别后直接返回第一次创建的博客 ID。
 *     2. 一次发布要完整成功：新增博客、绑定图片和保存请求结果处于同一个数据库事务；
 *     中间任何一步失败，前面的数据库修改都会撤销。
 *     3. 缩短编辑锁定时间：先用无锁查询校验标题（不超过 255 字符）、正文（不超过 2048 字符）、
 *     图片数量（1-9 张且不重复）和商户是否存在，全部通过后才用 SELECT ... FOR UPDATE 锁定博客行；
 *     无效请求不会长时间挡住其他人读取或修改这条记录。
 *     4. 只更新允许编辑的列：编辑走专门的 updateEditableFields 语句，只能修改商户、标题、正文和图片，
 *     不能借请求覆盖作者（user_id）、点赞数（liked）、评论数（comments）等服务端维护的数据。
     *     5. 正文只存普通文本：HTML 不写入数据库，页面展示时也不执行正文中的标签，
     *     既减少脚本注入风险，也避免反复编辑后出现 {@code &amp;lt;} 等重复转义。
     *     6. 发布成功后异步推粉丝收件箱：博客和图片都写成功后发布 {@link BlogPublishedEvent}（博客发布事件），
     *     监听器在事务提交后调用 {@link com.hmdp.service.feed.push.FeedPushService}（推模式收件箱写入服务：
     *     事务提交后把新博客按阈值分批写入粉丝的 tb_feed_inbox，作者粉丝数超过阈值 5000 就跳过）；
     *     推送失败只记日志，读侧由拉模式兜底。删除博客时在同一事务里清空该博客的收件箱记录。
 * 
 */
@Service
public class BlogCommandService {

    private static final int MAX_BLOG_IMAGES = 9;
    private static final int MAX_TITLE_LENGTH = 255;
    private static final int MAX_CONTENT_LENGTH = 2048;
    private static final int MAX_CLIENT_REQUEST_ID_LENGTH = 64;

    private final BlogMapper blogMapper;
    private final BlogLikeMapper blogLikeMapper;
    private final BlogCommentsMapper blogCommentsMapper;
    private final IShopService shopService;
    private final IBlogImageService blogImageService;
    private final BlogIdempotencyService idempotencyService;
    private final FeedInboxMapper feedInboxMapper;
    private final ApplicationEventPublisher eventPublisher;

    public BlogCommandService(
            BlogMapper blogMapper,
            BlogLikeMapper blogLikeMapper,
            BlogCommentsMapper blogCommentsMapper,
            IShopService shopService,
            IBlogImageService blogImageService,
            BlogIdempotencyService idempotencyService,
            FeedInboxMapper feedInboxMapper,
            ApplicationEventPublisher eventPublisher
    ) {
        this.blogMapper = blogMapper;
        this.blogLikeMapper = blogLikeMapper;
        this.blogCommentsMapper = blogCommentsMapper;
        this.shopService = shopService;
        this.blogImageService = blogImageService;
        this.idempotencyService = idempotencyService;
        this.feedInboxMapper = feedInboxMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Result publish(BlogPublishRequest request) {
        Long userId = requireCurrentUserId();
        if (request == null) {
            throw BusinessException.badRequest("BLOG_PUBLISH_REQUIRED", "发布内容不能为空");
        }

        // 这里只做确定性的本地规范化；幂等命中必须早于会变化的商户和图片数据库校验。
        String requestKey = validateClientRequestId(request.getClientRequestId());
        String title = normalizeTitle(request.getTitle());
        String content = normalizeContent(request.getContent());
        validateImageIds(request.getImageIds());
        String requestHash = calculatePublishHash(
                request.getShopId(), title, content, request.getImageIds());

        IdempotencyDecision decision = idempotencyService.begin(userId, requestKey, requestHash);
        if (decision.shouldUsePreviousResult()) {
            return Result.ok(decision.getResourceId());
        }

        validateShop(request.getShopId());
        List<BlogImage> images = blogImageService.loadOwnedTemporaryImages(request.getImageIds(), userId);
        Blog blog = new Blog()
                .setShopId(request.getShopId())
                .setUserId(userId)
                .setTitle(title)
                .setContent(content)
                .setImages(joinImageUrls(images))
                // 发布时间显式取应用时钟的 UTC 时间写入：MyBatis-Plus 插入不会回填数据库默认值，
                // 而发布事件和推模式收件箱 score 都需要这个值，且要与 Feed 读侧游标的时间戳口径
                // （BlogFeedService 按 UTC 解读 create_time）保持同一时间轴。
                .setCreateTime(LocalDateTime.now(ZoneOffset.UTC));
        if (blogMapper.insert(blog) != 1 || blog.getId() == null) {
            throw new IllegalStateException("保存博客失败");
        }

        blogImageService.bindToBlog(request.getImageIds(), userId, blog.getId());
        idempotencyService.complete(decision, blog.getId());
        // 博客和图片都写成功后才发布事件；监听器注册在 AFTER_COMMIT 阶段，
        // 事务真正提交后才会执行粉丝收件箱推送，推送失败也只记日志，不影响本次发布。
        eventPublisher.publishEvent(new BlogPublishedEvent(
                blog.getId(), userId, blog.getCreateTime().toInstant(ZoneOffset.UTC).toEpochMilli()));
        return Result.ok(blog.getId());
    }

    @Transactional
    public Result update(Long id, BlogUpdateRequest request) {
        Long userId = requireCurrentUserId();
        if (request == null) {
            throw BusinessException.badRequest("BLOG_UPDATE_REQUIRED", "编辑内容不能为空");
        }

        // 无锁校验先失败，避免无效请求占用热门博客的行锁。
        requireBlogId(id);
        String title = normalizeTitle(request.getTitle());
        String content = normalizeContent(request.getContent());
        validateImageIds(request.getImageIds());
        validateShop(request.getShopId());

        Blog existing = loadOwnedBlogForWrite(id, userId);
        List<BlogImage> removed = blogImageService.replaceBlogImages(
                request.getImageIds(), userId, existing.getId());
        List<BlogImage> images = blogImageService.loadOwnedBlogImages(
                request.getImageIds(), userId, existing.getId());
        if (blogMapper.updateEditableFields(
                existing.getId(),
                userId,
                request.getShopId(),
                title,
                content,
                joinImageUrls(images)) != 1) {
            throw new IllegalStateException("更新博客失败");
        }
        blogImageService.schedulePhysicalDeletionAfterCommit(removed);
        return Result.ok(existing.getId());
    }

    @Transactional
    public Result delete(Long id) {
        Long userId = requireCurrentUserId();
        Blog existing = loadOwnedBlogForWrite(id, userId);
        List<BlogImage> images = blogImageService.detachAllBoundImages(userId, existing.getId());
        blogLikeMapper.delete(new LambdaQueryWrapper<BlogLike>().eq(BlogLike::getBlogId, id));
        blogCommentsMapper.delete(new LambdaQueryWrapper<BlogComments>().eq(BlogComments::getBlogId, id));
        // 推模式收件箱是博客的派生数据：博客删除后残留记录会让 inbox 通道召回出已删除的博客
        // （读侧批量查博客时虽然会自然丢弃，但残留行会白白占空间、拖慢收件箱查询），所以在同一事务里清理。
        feedInboxMapper.deleteByBlogId(id);
        if (blogMapper.deleteById(id) != 1) {
            throw new IllegalStateException("删除博客失败");
        }
        // 不删除请求记录：防止已经在路上的旧发布请求，在博客删除后又创建一篇相同博客。
        // 旧请求再次到达时只返回原 blogId，不会恢复或重新创建已删除的博客。
        blogImageService.schedulePhysicalDeletionAfterCommit(images);
        return Result.ok();
    }

    private Blog loadOwnedBlogForWrite(Long id, Long userId) {
        requireBlogId(id);
        Blog blog = blogMapper.selectByIdForUpdate(id);
        if (blog == null) {
            throw BusinessException.notFound("BLOG_NOT_FOUND", "笔记不存在");
        }
        if (!userId.equals(blog.getUserId())) {
            throw BusinessException.forbidden("无权修改该笔记");
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

    private void requireBlogId(Long id) {
        if (id == null) {
            throw BusinessException.badRequest("BLOG_ID_REQUIRED", "博客ID不能为空");
        }
    }

    private String validateClientRequestId(String value) {
        if (StrUtil.isBlank(value)) {
            throw BusinessException.badRequest("CLIENT_REQUEST_ID_REQUIRED", "clientRequestId 不能为空");
        }
        String normalized = value.trim();
        if (normalized.length() > MAX_CLIENT_REQUEST_ID_LENGTH
                || !normalized.matches("[A-Za-z0-9_-]+")) {
            throw BusinessException.badRequest(
                    "INVALID_CLIENT_REQUEST_ID",
                    "clientRequestId 仅支持 1-64 位字母、数字、_ 或 -");
        }
        return normalized;
    }

    private String normalizeTitle(String title) {
        if (StrUtil.isBlank(title)) {
            throw BusinessException.badRequest("BLOG_TITLE_REQUIRED", "标题不能为空");
        }
        String normalized = title.trim();
        if (normalized.length() > MAX_TITLE_LENGTH) {
            throw BusinessException.badRequest(
                    "BLOG_TITLE_TOO_LONG",
                    "标题不能超过 " + MAX_TITLE_LENGTH + " 个字符");
        }
        return normalized;
    }

    private String normalizeContent(String content) {
        if (StrUtil.isBlank(content)) {
            throw BusinessException.badRequest("BLOG_CONTENT_REQUIRED", "正文不能为空");
        }
        String normalized = content.trim().replace("\r\n", "\n").replace('\r', '\n');
        if (normalized.length() > MAX_CONTENT_LENGTH) {
            throw BusinessException.badRequest("BLOG_CONTENT_TOO_LONG", "正文内容过长");
        }
        return normalized;
    }

    private void validateShop(Long shopId) {
        if (shopId == null) {
            throw BusinessException.badRequest("SHOP_ID_REQUIRED", "请选择商户");
        }
        if (shopService.getById(shopId) == null) {
            throw BusinessException.badRequest("SHOP_NOT_FOUND", "请选择有效商户");
        }
    }

    private void validateImageIds(List<Long> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) {
            throw BusinessException.badRequest("BLOG_IMAGE_REQUIRED", "请至少上传一张图片");
        }
        if (imageIds.size() > MAX_BLOG_IMAGES) {
            throw BusinessException.badRequest(
                    "TOO_MANY_BLOG_IMAGES",
                    "最多上传 " + MAX_BLOG_IMAGES + " 张图片");
        }
        Set<Long> distinct = new LinkedHashSet<>(imageIds);
        if (distinct.contains(null) || distinct.size() != imageIds.size()) {
            throw BusinessException.badRequest("INVALID_BLOG_IMAGE_IDS", "图片ID不能为空或重复");
        }
    }

    private String joinImageUrls(List<BlogImage> images) {
        return images.stream().map(BlogImage::getPublicUrl).collect(Collectors.joining(","));
    }

    private String calculatePublishHash(
            Long shopId,
            String title,
            String content,
            List<Long> imageIds
    ) {
        String canonical = encodeHashPart(String.valueOf(shopId))
                + encodeHashPart(title)
                + encodeHashPart(content)
                + encodeHashPart(imageIds.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(",")));
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(canonical.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(digest.length * 2);
            for (byte value : digest) {
                hex.append(String.format("%02x", value & 0xff));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("JVM 缺少 SHA-256 支持", e);
        }
    }

    private String encodeHashPart(String value) {
        return value.length() + ":" + value;
    }
}
