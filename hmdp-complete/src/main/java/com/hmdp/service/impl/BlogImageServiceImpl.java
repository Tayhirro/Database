package com.hmdp.service.impl;

/*
 * 现实业务背景：用户选择、移除、发布或重新编辑博客图片时，文件与数据库资产状态必须一起受控。
 * 实际触发：UploadController、BlogCommandService 和 BlogImageCleanupJob 分别触发上传、绑定/解绑和补偿清理。
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hmdp.config.BlogImageProperties;
import com.hmdp.dto.BlogImageUploadDTO;
import com.hmdp.entity.BlogImage;
import com.hmdp.exception.BusinessException;
import com.hmdp.mapper.BlogImageMapper;
import com.hmdp.service.IBlogImageService;
import com.hmdp.service.storage.BlogImageStorage;
import com.hmdp.service.storage.StoredBlogImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class BlogImageServiceImpl implements IBlogImageService {

    private static final int MAX_DELETION_ERROR_LENGTH = 1000;

    private final BlogImageMapper blogImageMapper;
    private final BlogImageStorage storage;
    private final BlogImageProperties properties;

    public BlogImageServiceImpl(
            BlogImageMapper blogImageMapper,
            BlogImageStorage storage,
            BlogImageProperties properties
    ) {
        this.blogImageMapper = blogImageMapper;
        this.storage = storage;
        this.properties = properties;
    }

    /**
     * 上传博客图片的完整流程：校验调用方已有用户 ID，把文件交给存储层校验并写入磁盘，
     * 再用返回的路径、URL、类型、大小和宽高创建 TEMP 图片资产记录；数据库插入失败时立即反向删除刚写入的文件。
     * 具体例子：用户 7 上传 a.jpg 后，磁盘生成唯一 storageKey，数据库保存 {@code userId=7,status=TEMP}，
     * 接口只返回 imageId 和 publicUrl；这时图片还没有属于任何博客。
     */
    @Override
    public BlogImageUploadDTO upload(MultipartFile file, Long userId) {
        requireUser(userId);
        StoredBlogImage stored = storage.store(file);

        BlogImage image = new BlogImage()
                .setUserId(userId)
                .setStorageKey(stored.getStorageKey())
                .setPublicUrl(stored.getPublicUrl())
                .setContentType(stored.getContentType())
                .setFileSize(stored.getFileSize())
                .setWidth(stored.getWidth())
                .setHeight(stored.getHeight())
                .setStatus(BlogImage.STATUS_TEMP);
        try {
            if (blogImageMapper.insert(image) != 1) {
                throw new IllegalStateException("创建图片资产记录失败");
            }
        } catch (RuntimeException e) {
            deleteStoredFileQuietly(stored.getStorageKey());
            throw e;
        }
        return new BlogImageUploadDTO(image.getId(), image.getPublicUrl());
    }

    /**
     * 删除未发布图片的完整流程：校验图片存在、属于当前用户、仍为 TEMP 且尚未绑定博客，
     * 再用条件更新（{@code id + user_id + status=TEMP + blog_id IS NULL}）抢占为 DELETING；
     * 之后分两步删除并分别裁决：物理文件删除失败（文件还在磁盘上）时把状态恢复成 TEMP 供用户重试；
     * 数据库元数据删除失败（此时文件已经不存在）则保持 DELETING、记录错误并延后重试，
     * 由补偿任务 cleanupDeletingImages 幂等地继续删（对已不存在的文件删除是空操作），绝不恢复 TEMP，
     * 避免"文件已删但记录回到 TEMP，重试又对不存在的文件再删一次"的错位。
     * 具体例子：用户 7 可以删除自己尚未发布的图片 501；用户 8 删除 501，或 501 已绑定博客 100，都会在物理文件操作前被拒绝。
     */
    @Override
    public void deleteTemporaryImage(Long imageId, Long userId) {
        requireUser(userId);
        if (imageId == null) {
            throw new BusinessException("图片ID不能为空");
        }

        BlogImage image = blogImageMapper.selectById(imageId);
        validateTemporaryOwnership(image, userId);
        if (!claimForDeletion(image.getId(), userId)) {
            throw new BusinessException("图片状态已变化，请刷新后重试");
        }

        try {
            storage.delete(image.getStorageKey());
        } catch (RuntimeException e) {
            // 文件删除失败说明文件仍在磁盘，恢复 TEMP 让用户直接重试。
            restoreTemporaryStatus(image.getId(), userId);
            throw e;
        }
        try {
            blogImageMapper.deleteById(image.getId());
        } catch (RuntimeException e) {
            // 文件已删除：保持 DELETING，记录错误交给补偿任务重试；恢复 TEMP 会造成文件缺失。
            recordDeletionFailure(image.getId(), e);
            log.warn("图片文件已删除但元数据删除失败，保留 DELETING 等待重试，imageId={}", image.getId(), e);
            throw e;
        }
    }

    /**
     * 校验待发布图片的完整流程：要求 ID 列表非空、无 null、无重复，批量查询后确认一张不少；
     * 每张图片还必须属于当前用户、状态为 TEMP 且没有 blogId，最后按请求中的 ID 顺序重新排列返回。
     * 具体例子：用户 7 提交 [502,501]，即使数据库批量查询返回 [501,502]，方法仍按 [502,501] 返回，
     * 后续发布就以 502 为首图；只要其中一张属于用户 8，整个发布请求都会失败。
     */
    @Override
    public List<BlogImage> loadOwnedTemporaryImages(List<Long> imageIds, Long userId) {
        requireUser(userId);
        if (imageIds == null || imageIds.isEmpty()) {
            throw new BusinessException("请至少上传一张图片");
        }

        Set<Long> distinctIds = new LinkedHashSet<>(imageIds);
        if (distinctIds.contains(null) || distinctIds.size() != imageIds.size()) {
            throw new BusinessException("图片ID不能为空或重复");
        }

        List<BlogImage> images = blogImageMapper.selectBatchIds(distinctIds);
        if (images == null || images.size() != distinctIds.size()) {
            throw new BusinessException("存在无效的图片");
        }

        Map<Long, BlogImage> imageById = new HashMap<>(images.size());
        for (BlogImage image : images) {
            validateTemporaryOwnership(image, userId);
            imageById.put(image.getId(), image);
        }

        List<BlogImage> orderedImages = new ArrayList<>(imageIds.size());
        for (Long imageId : imageIds) {
            orderedImages.add(imageById.get(imageId));
        }
        return orderedImages;
    }

    /**
     * 将临时图片绑定新博客的完整流程：逐张按列表下标设置 blogId、BOUND、sortOrder 和 bindTime；
     * 更新条件同时限制图片 ID、所有者、TEMP 状态和未绑定状态，防止并发请求抢用同一张图；
     * 若图片已经被同一请求绑定到相同博客和顺序，则视为幂等成功，否则中止事务。
     * 具体例子：博客 100 使用 [502,501] 时，502 的 sortOrder=0、501=1；若 501 已被博客 99 占用，博客 100 的发布会整体回滚。
     */
    @Override
    public void bindToBlog(List<Long> imageIds, Long userId, Long blogId) {
        requireUser(userId);
        if (blogId == null) {
            throw new IllegalArgumentException("博客ID不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        for (int index = 0; index < imageIds.size(); index++) {
            Long imageId = imageIds.get(index);
            int updated = blogImageMapper.update(
                    null,
                    new LambdaUpdateWrapper<BlogImage>()
                            .eq(BlogImage::getId, imageId)
                            .eq(BlogImage::getUserId, userId)
                            .eq(BlogImage::getStatus, BlogImage.STATUS_TEMP)
                            .isNull(BlogImage::getBlogId)
                            .set(BlogImage::getBlogId, blogId)
                            .set(BlogImage::getStatus, BlogImage.STATUS_BOUND)
                            .set(BlogImage::getSortOrder, index)
                            .set(BlogImage::getBindTime, now)
            );
            if (updated != 1) {
                BlogImage current = blogImageMapper.selectById(imageId);
                boolean alreadyBoundBySameRequest = current != null
                        && userId.equals(current.getUserId())
                        && blogId.equals(current.getBlogId())
                        && BlogImage.STATUS_BOUND.equals(current.getStatus())
                        && Integer.valueOf(index).equals(current.getSortOrder());
                if (!alreadyBoundBySameRequest) {
                    throw new BusinessException("图片状态已变化，发布失败");
                }
            }
        }
    }

    /**
     * 编辑时替换完整图片列表的完整流程：先校验请求图片无空值/重复，并确认每张要么是本人新上传的 TEMP 图片，
     * 要么是本博客原有的 BOUND 图片；再读取当前图片，把不再保留的标为 DELETING，绑定新增图片并按新列表重排全部图片，返回待物理删除列表。
     * 具体例子：博客 100 原有 [501,502]，请求改为 [502,503]，结果是 501→DELETING、502→sortOrder 0、
     * 503 从 TEMP 绑定并设为 sortOrder 1；若请求夹带别人的 504，任何状态都不会改变。
     */
    @Override
    public List<BlogImage> replaceBlogImages(List<Long> imageIds, Long userId, Long blogId) {
        requireUser(userId);
        requireDistinctImageIds(imageIds);
        List<BlogImage> requested = blogImageMapper.selectBatchIds(imageIds);
        if (requested == null || requested.size() != imageIds.size()) {
            throw new BusinessException("存在无效的图片");
        }
        Map<Long, BlogImage> requestedById = new HashMap<>();
        for (BlogImage image : requested) {
            boolean ownedTemporary = userId.equals(image.getUserId())
                    && BlogImage.STATUS_TEMP.equals(image.getStatus())
                    && image.getBlogId() == null;
            boolean retainedBound = userId.equals(image.getUserId())
                    && BlogImage.STATUS_BOUND.equals(image.getStatus())
                    && blogId.equals(image.getBlogId());
            if (!ownedTemporary && !retainedBound) {
                throw new BusinessException("图片不属于当前博客或不可绑定");
            }
            requestedById.put(image.getId(), image);
        }

        List<BlogImage> current = blogImageMapper.selectList(new LambdaQueryWrapper<BlogImage>()
                .eq(BlogImage::getUserId, userId)
                .eq(BlogImage::getBlogId, blogId)
                .eq(BlogImage::getStatus, BlogImage.STATUS_BOUND));
        Set<Long> requestedIds = new LinkedHashSet<>(imageIds);
        List<BlogImage> removed = new ArrayList<>();
        for (BlogImage image : current) {
            if (!requestedIds.contains(image.getId())) {
                int updated = blogImageMapper.update(null, new LambdaUpdateWrapper<BlogImage>()
                        .eq(BlogImage::getId, image.getId())
                        .eq(BlogImage::getUserId, userId)
                        .eq(BlogImage::getBlogId, blogId)
                        .eq(BlogImage::getStatus, BlogImage.STATUS_BOUND)
                        .set(BlogImage::getStatus, BlogImage.STATUS_DELETING)
                        .set(BlogImage::getRetryCount, 0)
                        .set(BlogImage::getLastError, null)
                        .set(BlogImage::getNextRetryTime, nextDeletionRetryTime()));
                if (updated != 1) {
                    throw new BusinessException("图片状态已变化，请重试");
                }
                removed.add(image.setStatus(BlogImage.STATUS_DELETING));
            }
        }

        LocalDateTime now = LocalDateTime.now();
        for (int index = 0; index < imageIds.size(); index++) {
            BlogImage image = requestedById.get(imageIds.get(index));
            LambdaUpdateWrapper<BlogImage> update = new LambdaUpdateWrapper<BlogImage>()
                    .eq(BlogImage::getId, image.getId())
                    .eq(BlogImage::getUserId, userId);
            if (BlogImage.STATUS_TEMP.equals(image.getStatus())) {
                update.eq(BlogImage::getStatus, BlogImage.STATUS_TEMP)
                        .isNull(BlogImage::getBlogId)
                        .set(BlogImage::getBlogId, blogId)
                        .set(BlogImage::getStatus, BlogImage.STATUS_BOUND)
                        .set(BlogImage::getBindTime, now);
            } else {
                update.eq(BlogImage::getStatus, BlogImage.STATUS_BOUND)
                        .eq(BlogImage::getBlogId, blogId);
            }
            update.set(BlogImage::getSortOrder, index);
            if (blogImageMapper.update(null, update) != 1) {
                throw new BusinessException("图片状态已变化，请重试");
            }
        }
        return removed;
    }

    /**
     * 读取博客已绑定图片的完整流程：校验列表非空且 ID 唯一，批量加载并确认数量完全一致，
     * 逐张检查“当前用户所有 + 属于指定博客 + BOUND”，最后按请求顺序返回，用于生成博客 images URL 字段。
     * 具体例子：编辑博客 100 后传 [502,503]，方法只在两张图都已正确绑定时按该顺序返回；属于博客 101 的图片会被拒绝。
     */
    @Override
    public List<BlogImage> loadOwnedBlogImages(List<Long> imageIds, Long userId, Long blogId) {
        requireUser(userId);
        requireDistinctImageIds(imageIds);
        List<BlogImage> images = blogImageMapper.selectBatchIds(imageIds);
        if (images == null || images.size() != imageIds.size()) {
            throw new BusinessException("存在无效的图片");
        }
        Map<Long, BlogImage> byId = new HashMap<>();
        for (BlogImage image : images) {
            if (!userId.equals(image.getUserId())
                    || !blogId.equals(image.getBlogId())
                    || !BlogImage.STATUS_BOUND.equals(image.getStatus())) {
                throw new BusinessException("图片不属于当前博客");
            }
            byId.put(image.getId(), image);
        }
        List<BlogImage> ordered = new ArrayList<>(imageIds.size());
        for (Long imageId : imageIds) {
            ordered.add(byId.get(imageId));
        }
        return ordered;
    }

    /**
     * 删除博客前解绑全部图片的完整流程：查询该用户、该博客下全部 BOUND 图片，逐张用条件更新改为 DELETING，
     * 清零重试信息并设置下次可重试时间；任一图片并发变更都会抛错使外层博客删除事务回滚，最后返回待删资产。
     * 具体例子：用户 7 删除博客 100 时，图片 501、502 先进入 DELETING；事务成功后才能删文件，事务失败则状态更新一起回滚。
     */
    @Override
    public List<BlogImage> detachAllBoundImages(Long userId, Long blogId) {
        requireUser(userId);
        List<BlogImage> images = blogImageMapper.selectList(new LambdaQueryWrapper<BlogImage>()
                .eq(BlogImage::getUserId, userId)
                .eq(BlogImage::getBlogId, blogId)
                .eq(BlogImage::getStatus, BlogImage.STATUS_BOUND));
        for (BlogImage image : images) {
            int updated = blogImageMapper.update(null, new LambdaUpdateWrapper<BlogImage>()
                    .eq(BlogImage::getId, image.getId())
                    .eq(BlogImage::getUserId, userId)
                    .eq(BlogImage::getBlogId, blogId)
                    .eq(BlogImage::getStatus, BlogImage.STATUS_BOUND)
                    .set(BlogImage::getStatus, BlogImage.STATUS_DELETING)
                    .set(BlogImage::getRetryCount, 0)
                    .set(BlogImage::getLastError, null)
                    .set(BlogImage::getNextRetryTime, nextDeletionRetryTime()));
            if (updated != 1) {
                throw new BusinessException("图片状态已变化，请重试");
            }
            image.setStatus(BlogImage.STATUS_DELETING);
        }
        return images;
    }

    /**
     * 安排物理文件删除的完整流程：空列表直接结束；没有活动事务时立即删除；有事务时注册 afterCommit 回调，
     * 只有 MySQL 真正提交后才逐张删除文件和 DELETING 元数据，失败记录保留给定时任务重试。
     * 具体例子：编辑博客把图片 501 移除后，如果数据库事务回滚，回调不会执行、文件仍在；提交成功后才删除 501。
     */
    @Override
    public void schedulePhysicalDeletionAfterCommit(List<BlogImage> images) {
        if (images == null || images.isEmpty()) {
            return;
        }
        Runnable deletion = () -> deletePhysicalAssets(images);
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            deletion.run();
            return;
        }
        // 外部文件不参与 MySQL 事务：必须提交后再删，避免数据库回滚却丢失图片。
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            /**
             * 提交回调的完整流程：事务提交成功后执行上面准备好的删除任务；
             * 具体例子：事务回滚时本方法不会触发，因此不会误删仍被博客引用的文件。
             */
            @Override
            public void afterCommit() {
                deletion.run();
            }
        });
    }

    private void deletePhysicalAssets(List<BlogImage> images) {
        for (BlogImage image : images) {
            deletePhysicalAsset(image);
        }
    }

    private void requireDistinctImageIds(List<Long> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) {
            throw new BusinessException("请至少保留一张图片");
        }
        Set<Long> distinct = new LinkedHashSet<>(imageIds);
        if (distinct.contains(null) || distinct.size() != imageIds.size()) {
            throw new BusinessException("图片ID不能为空或重复");
        }
    }

    /**
     * 清理过期临时图片的完整流程：按配置保留时长和批量大小查询旧 TEMP 记录，逐张条件抢占为 DELETING，
     * 再分两步删除并分别裁决：物理文件删除失败（文件还在）时恢复 TEMP、记录日志并继续处理下一张；
     * 数据库元数据删除失败（文件已不存在）时保持 DELETING、记录错误并延后重试，交给补偿任务幂等重试。
     * 单张失败都不会中断整批，成功数作为任务结果返回。
     * 具体例子：按默认配置保留期 {@code tempRetentionHours=24} 小时、每批 {@code cleanupBatchSize=100} 条，
     * 图片 501 上传 30 小时仍未发布会被清理；刚上传 2 小时的 502 不会进入本批次。
     */
    @Override
    public int cleanupExpiredTemporaryImages() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(properties.getTempRetentionHours());
        List<BlogImage> expiredImages = blogImageMapper.selectList(
                new LambdaQueryWrapper<BlogImage>()
                        .eq(BlogImage::getStatus, BlogImage.STATUS_TEMP)
                        .lt(BlogImage::getCreateTime, cutoff)
                        .orderByAsc(BlogImage::getCreateTime)
                        .last("LIMIT " + Math.max(1, properties.getCleanupBatchSize()))
        );
        if (expiredImages == null || expiredImages.isEmpty()) {
            return 0;
        }

        int cleaned = 0;
        for (BlogImage image : expiredImages) {
            if (!claimForDeletion(image.getId(), image.getUserId())) {
                continue;
            }
            try {
                storage.delete(image.getStorageKey());
            } catch (RuntimeException e) {
                // 文件删除失败说明文件仍在磁盘，恢复 TEMP 供下轮继续尝试。
                restoreTemporaryStatus(image.getId(), image.getUserId());
                log.warn("清理临时博客图片失败，imageId={}", image.getId(), e);
                continue;
            }
            try {
                blogImageMapper.deleteById(image.getId());
            } catch (RuntimeException e) {
                // 文件已删除：保持 DELETING 记录错误，由 cleanupDeletingImages 幂等重试；不恢复 TEMP。
                recordDeletionFailure(image.getId(), e);
                log.warn("临时图片文件已删除但元数据删除失败，保留 DELETING 等待重试，imageId={}", image.getId(), e);
                continue;
            }
            cleaned++;
        }
        return cleaned;
    }

    /**
     * 重试待删除图片的完整流程：按 {@code status=DELETING 且 nextRetryTime 已到} 分批（每批 {@code cleanupBatchSize} 条）读取记录，
     * 先用条件更新原子地把 nextRetryTime 向后推进 {@code deletingRetryDelayMinutes}（默认 5）分钟以抢占任务，
     * 再删除物理文件和条件删除元数据；失败时增加 retryCount、保存错误摘要并把重试时间再延后 5 分钟，成功数作为任务结果返回。
     * 具体例子：图片 501 上次因磁盘暂时不可用删除失败，nextRetryTime 到达后再次执行；成功则记录消失，失败则保留记录并延后下一次尝试。
     */
    @Override
    public int cleanupDeletingImages() {
        LocalDateTime now = LocalDateTime.now();
        List<BlogImage> deletingImages = blogImageMapper.selectList(
                new LambdaQueryWrapper<BlogImage>()
                        .eq(BlogImage::getStatus, BlogImage.STATUS_DELETING)
                        .and(query -> query.isNull(BlogImage::getNextRetryTime)
                                .or()
                                .le(BlogImage::getNextRetryTime, now))
                        .orderByAsc(BlogImage::getNextRetryTime, BlogImage::getId)
                        .last("LIMIT " + Math.max(1, properties.getCleanupBatchSize()))
        );
        if (deletingImages == null || deletingImages.isEmpty()) {
            return 0;
        }

        int cleaned = 0;
        for (BlogImage image : deletingImages) {
            if (!claimDeletingRetry(image.getId(), now)) {
                continue;
            }
            if (deletePhysicalAsset(image)) {
                cleaned++;
            }
        }
        return cleaned;
    }

    /** 文件不存在同样是目标状态；物理删除和条件删元数据都保持幂等。 */
    private boolean deletePhysicalAsset(BlogImage image) {
        try {
            storage.delete(image.getStorageKey());
            blogImageMapper.delete(new LambdaQueryWrapper<BlogImage>()
                    .eq(BlogImage::getId, image.getId())
                    .eq(BlogImage::getStatus, BlogImage.STATUS_DELETING));
            return true;
        } catch (RuntimeException e) {
            recordDeletionFailure(image.getId(), e);
            log.warn("删除博客图片失败，保留 DELETING 记录等待重试，imageId={}", image.getId(), e);
            return false;
        }
    }

    private boolean claimDeletingRetry(Long imageId, LocalDateTime now) {
        return blogImageMapper.update(
                null,
                new LambdaUpdateWrapper<BlogImage>()
                        .eq(BlogImage::getId, imageId)
                        .eq(BlogImage::getStatus, BlogImage.STATUS_DELETING)
                        .and(query -> query.isNull(BlogImage::getNextRetryTime)
                                .or()
                                .le(BlogImage::getNextRetryTime, now))
                        .set(BlogImage::getNextRetryTime,
                                now.plusMinutes(Math.max(1, properties.getDeletingRetryDelayMinutes())))) == 1;
    }

    private void recordDeletionFailure(Long imageId, RuntimeException error) {
        String message = error.getMessage() == null
                ? error.getClass().getSimpleName()
                : error.getClass().getSimpleName() + ": " + error.getMessage();
        if (message.length() > MAX_DELETION_ERROR_LENGTH) {
            message = message.substring(0, MAX_DELETION_ERROR_LENGTH);
        }
        LocalDateTime nextRetry = LocalDateTime.now()
                .plusMinutes(Math.max(1, properties.getDeletingRetryDelayMinutes()));
        blogImageMapper.update(
                null,
                new LambdaUpdateWrapper<BlogImage>()
                        .eq(BlogImage::getId, imageId)
                        .eq(BlogImage::getStatus, BlogImage.STATUS_DELETING)
                        .setSql("retry_count = retry_count + 1")
                        .set(BlogImage::getLastError, message)
                        .set(BlogImage::getNextRetryTime, nextRetry)
        );
    }

    private boolean claimForDeletion(Long imageId, Long userId) {
        int updated = blogImageMapper.update(
                null,
                new LambdaUpdateWrapper<BlogImage>()
                        .eq(BlogImage::getId, imageId)
                        .eq(BlogImage::getUserId, userId)
                        .eq(BlogImage::getStatus, BlogImage.STATUS_TEMP)
                        .isNull(BlogImage::getBlogId)
                        .set(BlogImage::getStatus, BlogImage.STATUS_DELETING)
                        .set(BlogImage::getRetryCount, 0)
                        .set(BlogImage::getLastError, null)
                        .set(BlogImage::getNextRetryTime, nextDeletionRetryTime())
        );
        return updated == 1;
    }

    private void restoreTemporaryStatus(Long imageId, Long userId) {
        blogImageMapper.update(
                null,
                new LambdaUpdateWrapper<BlogImage>()
                        .eq(BlogImage::getId, imageId)
                        .eq(BlogImage::getUserId, userId)
                        .eq(BlogImage::getStatus, BlogImage.STATUS_DELETING)
                        .isNull(BlogImage::getBlogId)
                        .set(BlogImage::getStatus, BlogImage.STATUS_TEMP)
                        .set(BlogImage::getRetryCount, 0)
                        .set(BlogImage::getLastError, null)
                        .set(BlogImage::getNextRetryTime, null)
        );
    }

    private LocalDateTime nextDeletionRetryTime() {
        return LocalDateTime.now()
                .plusMinutes(Math.max(1, properties.getDeletingRetryDelayMinutes()));
    }

    private void validateTemporaryOwnership(BlogImage image, Long userId) {
        if (image == null) {
            throw new BusinessException("图片不存在");
        }
        if (!userId.equals(image.getUserId())) {
            throw new BusinessException("无权操作该图片");
        }
        if (!BlogImage.STATUS_TEMP.equals(image.getStatus()) || image.getBlogId() != null) {
            throw new BusinessException("已发布的图片不能直接删除或重复绑定");
        }
    }

    private void requireUser(Long userId) {
        if (userId == null) {
            throw new BusinessException("请先登录");
        }
    }

    private void deleteStoredFileQuietly(String storageKey) {
        try {
            storage.delete(storageKey);
        } catch (RuntimeException cleanupError) {
            log.warn("回滚未登记的图片文件失败，storageKey={}", storageKey, cleanupError);
        }
    }
}
