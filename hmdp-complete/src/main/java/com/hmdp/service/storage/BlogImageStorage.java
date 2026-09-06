package com.hmdp.service.storage;

/*
 * 现实业务背景：用户在博客编辑器选择图片后，服务端必须验证真实文件内容（不能只信前端传来的文件名）
 * 并把二进制安全地保存到受控目录，防止伪造扩展名上传非图片、路径穿越写到上传根目录之外。
 * 实际触发：BlogImageServiceImpl.upload()（博客图片上传流程）调用 store()；
 * delete() 由三类场景调用——用户删除临时图片、事务提交后删除解绑博客的图片、
 * 定时清理任务补偿清理过期 TEMP 和删除失败的 DELETING 图片记录。
 *
 * store() 的完整校验链（任一步不通过即抛 {@link BusinessException} 业务异常）：
 * 1. 文件非空且不超过配置上限（默认 5MB）；
 * 2. 用 ImageIO 探测真实内容，只接受 JPG、PNG、GIF，并读出宽高
 *    （单边上限默认 10000 像素、总像素默认 4000 万）；
 * 3. 原始文件名扩展名必须与真实内容一致（如 .jpg 文件实际解码必须是 JPEG）；
 * 4. 生成存储键 blogs/yyyy/MM/{hash两位}/{uuid}.{扩展名}（如 blogs/2026/08/3/12/xxx.jpg），
 *    解析目标路径时拒绝绝对路径和 .. 穿越，落盘前再用真实路径复核仍在上传根目录内。
 */

import com.hmdp.config.BlogImageProperties;
import com.hmdp.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

@Component
public class BlogImageStorage {

    private static final DateTimeFormatter MONTH_PATH = DateTimeFormatter.ofPattern("yyyy/MM");

    private final BlogImageProperties properties;

    public BlogImageStorage(BlogImageProperties properties) {
        this.properties = properties;
    }

    public StoredBlogImage store(MultipartFile file) {
        validateBasicFile(file);
        ImageMetadata metadata = inspectImage(file);
        validateOriginalExtension(file.getOriginalFilename(), metadata.getExtension());

        String storageKey = createStorageKey(metadata.getExtension());
        Path uploadRoot = getUploadRoot();
        Path target = resolveWithinUploadRoot(uploadRoot, storageKey);

        try {
            Files.createDirectories(uploadRoot);
            Files.createDirectories(target.getParent());
            Path realRoot = uploadRoot.toRealPath();
            Path realParent = target.getParent().toRealPath();
            if (!realParent.startsWith(realRoot)) {
                throw new BusinessException("图片路径超出上传目录");
            }
            file.transferTo(target.toFile());
            return new StoredBlogImage(
                    storageKey,
                    buildPublicUrl(storageKey),
                    metadata.getContentType(),
                    file.getSize(),
                    metadata.getWidth(),
                    metadata.getHeight()
            );
        } catch (BusinessException e) {
            deleteQuietly(target);
            throw e;
        } catch (IOException e) {
            deleteQuietly(target);
            throw new BusinessException("图片保存失败", e);
        }
    }

    public void delete(String storageKey) {
        Path uploadRoot = getUploadRoot();
        Path candidate = resolveWithinUploadRoot(uploadRoot, storageKey);
        if (!Files.exists(candidate, LinkOption.NOFOLLOW_LINKS)) {
            return;
        }
        if (!Files.isRegularFile(candidate, LinkOption.NOFOLLOW_LINKS)
                || Files.isSymbolicLink(candidate)) {
            throw new BusinessException("图片文件类型非法");
        }

        try {
            Path realRoot = uploadRoot.toRealPath();
            Path realFile = candidate.toRealPath();
            if (!realFile.startsWith(realRoot)) {
                throw new BusinessException("图片路径超出上传目录");
            }
            Files.delete(realFile);
        } catch (IOException e) {
            throw new BusinessException("图片删除失败", e);
        }
    }

    Path getUploadRoot() {
        String root = properties.getRoot();
        if (root == null || root.trim().isEmpty()) {
            throw new IllegalStateException("hmdp.upload.root 未配置");
        }
        return Paths.get(root).toAbsolutePath().normalize();
    }

    static Path resolveWithinUploadRoot(Path uploadRoot, String storageKey) {
        if (storageKey == null || storageKey.trim().isEmpty()) {
            throw new BusinessException("图片存储路径不能为空");
        }

        Path root = uploadRoot.toAbsolutePath().normalize();
        Path relativePath = Paths.get(storageKey.replace('\\', '/'));
        if (relativePath.isAbsolute()) {
            throw new BusinessException("图片存储路径非法");
        }

        Path candidate = root.resolve(relativePath).normalize();
        if (!candidate.startsWith(root)) {
            throw new BusinessException("图片路径超出上传目录");
        }
        return candidate;
    }

    private void validateBasicFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择图片");
        }
        if (file.getSize() > properties.getMaxBytes()) {
            throw new BusinessException("图片不能超过 " + properties.getMaxBytes() / 1024 / 1024 + "MB");
        }
    }

    private ImageMetadata inspectImage(MultipartFile file) {
        try (InputStream input = file.getInputStream();
             ImageInputStream imageInput = ImageIO.createImageInputStream(input)) {
            if (imageInput == null) {
                throw new BusinessException("无法识别图片内容");
            }

            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInput);
            if (!readers.hasNext()) {
                throw new BusinessException("仅支持 JPG、PNG 或 GIF 图片");
            }

            ImageReader reader = readers.next();
            try {
                reader.setInput(imageInput, true, true);
                String format = normalizeFormat(reader.getFormatName());
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                validateDimensions(width, height);
                return new ImageMetadata(
                        format,
                        "image/" + ("jpg".equals(format) ? "jpeg" : format),
                        width,
                        height
                );
            } finally {
                reader.dispose();
            }
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            throw new BusinessException("图片内容校验失败", e);
        }
    }

    private String normalizeFormat(String formatName) {
        String format = formatName.toLowerCase(Locale.ROOT);
        if ("jpeg".equals(format) || "jpg".equals(format)) {
            return "jpg";
        }
        if ("png".equals(format) || "gif".equals(format)) {
            return format;
        }
        throw new BusinessException("仅支持 JPG、PNG 或 GIF 图片");
    }

    private void validateOriginalExtension(String originalFilename, String actualExtension) {
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new BusinessException("图片文件名不能为空");
        }

        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == originalFilename.length() - 1) {
            throw new BusinessException("图片扩展名不能为空");
        }

        String extension = originalFilename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
        boolean jpegMatches = "jpg".equals(actualExtension)
                && ("jpg".equals(extension) || "jpeg".equals(extension));
        if (!jpegMatches && !actualExtension.equals(extension)) {
            throw new BusinessException("图片扩展名与实际内容不一致");
        }
    }

    private void validateDimensions(int width, int height) {
        long pixels = (long) width * height;
        if (width <= 0 || height <= 0
                || width > properties.getMaxWidth()
                || height > properties.getMaxHeight()
                || pixels > properties.getMaxPixels()) {
            throw new BusinessException("图片尺寸超出限制");
        }
    }

    private String createStorageKey(String extension) {
        String month = LocalDate.now().format(MONTH_PATH);
        String uuid = UUID.randomUUID().toString();
        int hash = uuid.hashCode();
        int d1 = hash & 0xF;
        int d2 = (hash >> 4) & 0xF;
        return String.format("blogs/%s/%d/%d/%s.%s", month, d1, d2, uuid, extension);
    }

    private String buildPublicUrl(String storageKey) {
        String prefix = properties.getPublicPrefix();
        if (prefix == null || prefix.trim().isEmpty()) {
            prefix = "/imgs/";
        }
        if (!prefix.startsWith("/")) {
            prefix = "/" + prefix;
        }
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        return prefix + storageKey.replace('\\', '/');
    }

    private void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // The database record is not created when storage fails; cleanup can be handled operationally.
        }
    }

    private static class ImageMetadata {

        private final String extension;
        private final String contentType;
        private final int width;
        private final int height;

        private ImageMetadata(String extension, String contentType, int width, int height) {
            this.extension = extension;
            this.contentType = contentType;
            this.width = width;
            this.height = height;
        }

        private String getExtension() {
            return extension;
        }

        private String getContentType() {
            return contentType;
        }

        private int getWidth() {
            return width;
        }

        private int getHeight() {
            return height;
        }
    }
}
