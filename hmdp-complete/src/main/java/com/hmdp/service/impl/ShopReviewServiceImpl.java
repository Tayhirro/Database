package com.hmdp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.dto.ShopReviewCreateRequest;
import com.hmdp.dto.ShopReviewDTO;
import com.hmdp.dto.ShopReviewStatDTO;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Shop;
import com.hmdp.entity.ShopReview;
import com.hmdp.entity.ShopReviewStat;
import com.hmdp.entity.User;
import com.hmdp.exception.BusinessException;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.mapper.ShopReviewMapper;
import com.hmdp.mapper.ShopReviewStatMapper;
import com.hmdp.service.IUserInfoService;
import com.hmdp.service.IUserService;
import com.hmdp.service.IShopReviewService;
import com.hmdp.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 店铺评价服务实现。
 *
 * 一次完整评价的数据流（数字均与代码一致）：
 * 1. 用户 POST /shop-review，body =（shopId、rating 1~5、content <=500 字、images <=9 张）；
 * 2. 本类校验店铺存在（tb_shop）与字段边界，作者取当前登录用户；
 * 3. INSERT tb_shop_review（UNIQUE(shop_id, user_id) 拦截一人一店一评的重复提交）；
 * 4. 同一事务里 UPDATE tb_shop_review_stat：review_count +1、total_score +rating
 *    （统计行不存在时先 INSERT 初始行，并发首评靠主键冲突重试 UPDATE，见 createReview 注释）；
 * 5. 删除评价（DELETE /shop-review/{id}）：只能删本人的；物理删除行，统计回退
 *    （review_count 不低于 0、total_score 不低于 0），之后允许重新评价。
 *
 * 列表分页：游标 =（上一页最后一条的 createTime UTC 毫秒，id），条件是
 * "发布时间更早，或时间相同但 id 更小"（同一秒多条评价靠 id 分先后），
 * 排序 ORDER BY create_time DESC, id DESC，一页默认 10 条、上限 20 条。
 */
@Slf4j
@Service
public class ShopReviewServiceImpl extends ServiceImpl<ShopReviewMapper, ShopReview> implements IShopReviewService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 20;
    private static final int MAX_CONTENT_LENGTH = 500;
    private static final int MAX_IMAGES = 9;
    private static final int MAX_IMAGE_URL_LENGTH = 512;

    @Resource
    private ShopMapper shopMapper;
    @Resource
    private ShopReviewStatMapper shopReviewStatMapper;
    @Resource
    private IUserService userService;

    @Override
    @Transactional
    public Result createReview(ShopReviewCreateRequest request) {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        if (request == null || request.getShopId() == null) {
            throw BusinessException.badRequest("SHOP_REQUIRED", "必须指定要评价的店铺");
        }
        Shop shop = shopMapper.selectById(request.getShopId());
        if (shop == null) {
            throw BusinessException.notFound("SHOP_NOT_FOUND", "店铺不存在");
        }
        validate(request);
        ShopReview review = new ShopReview()
                .setShopId(request.getShopId())
                .setUserId(user.getId())
                .setRating(request.getRating())
                .setContent(request.getContent().trim())
                .setImages(joinImages(request.getImages()))
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        try {
            save(review);
        } catch (DuplicateKeyException e) {
            // UNIQUE(shop_id, user_id)：同一用户对同一店铺已评价过
            throw BusinessException.conflict("REVIEW_DUPLICATED", "您已评价过该店铺");
        }
        applyStatCreate(request.getShopId(), request.getRating());
        return Result.ok(review.getId());
    }

    /**
     * 统计累加（与评价插入同事务）：
     * 先尝试 UPDATE（统计行已存在的常态路径）；UPDATE 影响行数为 0 说明是该店第一条评价，
     * INSERT 初始统计行；并发首评同时 INSERT 会撞主键，撞上的那个改为再试一次 UPDATE。
     */
    private void applyStatCreate(Long shopId, int rating) {
        int updated = shopReviewStatMapper.incrementOnCreate(shopId, rating);
        if (updated == 0) {
            ShopReviewStat stat = new ShopReviewStat()
                    .setShopId(shopId)
                    .setReviewCount(1)
                    .setTotalScore((long) rating)
                    .setUpdateTime(LocalDateTime.now());
            try {
                shopReviewStatMapper.insert(stat);
            } catch (DuplicateKeyException e) {
                if (shopReviewStatMapper.incrementOnCreate(shopId, rating) == 0) {
                    throw new BusinessException("评价统计累加失败，shopId=" + shopId);
                }
            }
        }
    }

    @Override
    public Result listByShop(Long shopId, Long lastTime, Long lastId, Integer pageSize) {
        if (shopId == null) {
            throw BusinessException.badRequest("SHOP_REQUIRED", "必须指定店铺");
        }
        int limit = pageSize == null || pageSize < 1 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        LocalDateTime lastCreateTime = toUtcTime(lastTime);
        List<ShopReview> reviews = getBaseMapper()
                .selectPageByShop(shopId, lastCreateTime, lastId, limit + 1);
        boolean hasMore = reviews.size() > limit;
        List<ShopReview> page = hasMore ? reviews.subList(0, limit) : reviews;
        List<ShopReviewDTO> dtos = assembleDTOs(page);
        Long nextTime = null;
        Long nextId = null;
        if (hasMore && !page.isEmpty()) {
            ShopReview last = page.get(page.size() - 1);
            nextTime = epochMilli(last.getCreateTime());
            nextId = last.getId();
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", dtos);
        data.put("hasMore", hasMore);
        data.put("lastTime", nextTime);
        data.put("lastId", nextId);
        return Result.ok(data);
    }

    @Override
    @Transactional
    public Result deleteReview(Long reviewId) {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        ShopReview review = getById(reviewId);
        if (review == null || !review.getUserId().equals(user.getId())) {
            return Result.fail("评价不存在或不是本人评价");
        }
        // 物理删除：直接走 Mapper，不走 ServiceImpl.removeById（后者依赖 MP 元数据，行为等价）
        getBaseMapper().deleteById(reviewId);
        shopReviewStatMapper.decrementOnDelete(review.getShopId(), review.getRating() == null ? 0 : review.getRating());
        return Result.ok();
    }

    @Override
    public Result statOfShop(Long shopId) {
        if (shopId == null) {
            throw BusinessException.badRequest("SHOP_REQUIRED", "必须指定店铺");
        }
        ShopReviewStat stat = shopReviewStatMapper.selectById(shopId);
        ShopReviewStatDTO dto = new ShopReviewStatDTO();
        dto.setShopId(shopId);
        long count = stat == null || stat.getReviewCount() == null ? 0 : stat.getReviewCount();
        long total = stat == null || stat.getTotalScore() == null ? 0 : stat.getTotalScore();
        dto.setReviewCount(count);
        dto.setAverageScore(count == 0 ? 0D : Math.round(total * 10D / count) / 10D);
        return Result.ok(dto);
    }

    @Override
    public List<ShopReviewDTO> assembleDTOs(List<ShopReview> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> authorIds = reviews.stream()
                .map(ShopReview::getUserId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> authorById = new HashMap<>();
        if (!authorIds.isEmpty()) {
            for (User author : userService.listByIds(authorIds)) {
                authorById.put(author.getId(), author);
            }
        }
        List<ShopReviewDTO> dtos = new ArrayList<>(reviews.size());
        for (ShopReview review : reviews) {
            ShopReviewDTO dto = new ShopReviewDTO();
            dto.setId(review.getId());
            dto.setShopId(review.getShopId());
            dto.setUserId(review.getUserId());
            dto.setRating(review.getRating());
            dto.setContent(review.getContent());
            dto.setImages(splitImages(review.getImages()));
            dto.setCreateTime(review.getCreateTime());
            User author = review.getUserId() == null ? null : authorById.get(review.getUserId());
            if (author != null) {
                dto.setAuthorName(author.getNickName());
                dto.setAuthorIcon(author.getIcon());
            }
            dtos.add(dto);
        }
        return dtos;
    }

    private void validate(ShopReviewCreateRequest request) {
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw BusinessException.badRequest("RATING_INVALID", "评分必须是 1~5 星");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw BusinessException.badRequest("CONTENT_REQUIRED", "评价内容不能为空");
        }
        if (request.getContent().trim().length() > MAX_CONTENT_LENGTH) {
            throw BusinessException.badRequest("CONTENT_TOO_LONG", "评价内容不能超过 500 字");
        }
        if (request.getImages() != null) {
            if (request.getImages().size() > MAX_IMAGES) {
                throw BusinessException.badRequest("IMAGES_TOO_MANY", "评价图片最多 9 张");
            }
            for (String image : request.getImages()) {
                if (image != null && image.length() > MAX_IMAGE_URL_LENGTH) {
                    throw BusinessException.badRequest("IMAGE_URL_TOO_LONG", "图片 URL 过长");
                }
            }
        }
    }

    private String joinImages(List<String> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return String.join(",", images);
    }

    private List<String> splitImages(String images) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        for (String image : images.split(",")) {
            if (!image.trim().isEmpty()) {
                result.add(image.trim());
            }
        }
        return result;
    }

    private LocalDateTime toUtcTime(Long epochMilli) {
        if (epochMilli == null || epochMilli <= 0) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneOffset.UTC);
    }

    private Long epochMilli(LocalDateTime time) {
        return time == null ? null : time.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
