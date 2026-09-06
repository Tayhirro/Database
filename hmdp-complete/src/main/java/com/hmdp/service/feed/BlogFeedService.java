package com.hmdp.service.feed;

/*
 * 现实业务背景：已登录用户打开关注流或为你推荐、继续下拉或主动刷新时，需要生成一页稳定的内容列表。
 * 实际触发：GET /blog/feed 经 BlogServiceImpl.queryBlogFeed() 进入本类，再协调召回、排序、快照和曝光服务。
 */

import com.hmdp.dto.AuthorInteractionDTO;
import com.hmdp.dto.BlogCardDTO;
import com.hmdp.dto.CursorPageDTO;
import com.hmdp.dto.CursorPayload;
import com.hmdp.dto.Result;
import com.hmdp.entity.Blog;
import com.hmdp.entity.Shop;
import com.hmdp.exception.BusinessException;
import com.hmdp.mapper.BlogMapper;
import com.hmdp.mapper.RecommendQueryMapper;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.blog.BlogAssembler;
import com.hmdp.service.cursor.CursorCodec;
import com.hmdp.service.feedcache.FeedCacheEntry;
import com.hmdp.service.feedcache.FeedCachePage;
import com.hmdp.service.feedcache.FeedCacheService;
import com.hmdp.service.strategy.ranking.RankingContext;
import com.hmdp.service.strategy.ranking.RankingStrategy;
import com.hmdp.service.strategy.ranking.RankingStrategyRegistry;
import com.hmdp.service.strategy.recall.RecallContext;
import com.hmdp.service.strategy.recall.RecallOrchestrator;
import com.hmdp.service.strategy.recall.impl.blog.ForYouRecall;
import com.hmdp.utils.UserHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Feed 读链路的核心边界。一个完整请求的路径是：
 * GET /blog/feed?mode=for_you&cursor=xxx → 本类 query() → 先读 Redis 快照缓存
 * {@link FeedCacheService}（把已生成的 Feed 分页结果缓存到 Redis 的服务），
 * 未命中则走"召回 → 过滤曝光 → 排序 → 写快照"重建一整轮结果。
 *
 * 协作的组件：
 * - {@link RecallOrchestrator}（召回编排器：按通道名调用各召回策略并用 LinkedHashSet 合并博客 ID）；
 * - {@link RankingStrategyRegistry}（排序策略注册表：按策略名取出对应的排序实现）；
 * - {@link FeedExposureService}（曝光记录服务：用 Redis ZSet 记录用户最近看过哪些博客，用于推荐去重）；
 * - {@link CursorCodec}（游标编解码器：把 {@link CursorPayload} 里的分页位置编成不透明字符串，
 *   或把字符串游标解码回 {@link CursorPayload}）。
 *
 * 设计约束：
 * 1. 产品模式（following / for_you，见 {@link FeedMode}）显式选择召回与排序组合，
 *    不把内部策略名（"follow"、"weighted" 等）暴露给客户端。
 * 2. 召回决定“有哪些候选”（一次最多 {@value #CANDIDATE_POOL_SIZE} 条 ID），
 *    排序/重排决定“先看谁”和作者多样性（同一作者在前 {@value #MAX_PER_AUTHOR_BEFORE_BACKFILL} 条内最多出现 2 次）。
 * 3. 不可变快照保证连续翻页稳定；刷新（refresh=true）只删除当前指针并重置游标，旧快照靠 TTL 自然过期，不破坏旧游标。
 * 4. 曝光是可降级副作用，Redis 故障不能阻断数据库回源和 Feed 返回。
 * 5. 无法严格复现 For You 跨页顺序时返回 hasMore=false，不制造虚假分页承诺。
 */
@Service
public class BlogFeedService {

    private static final int PAGE_SIZE = 50;
    private static final int CANDIDATE_POOL_SIZE = 200;
    private static final int MAX_PER_AUTHOR_BEFORE_BACKFILL = 2;

    private final BlogMapper blogMapper;
    private final BlogAssembler blogAssembler;
    private final RecallOrchestrator recallOrchestrator;
    private final RankingStrategyRegistry rankingStrategyRegistry;
    private final FeedCacheService feedCacheService;
    private final FeedExposureService exposureService;
    private final CursorCodec cursorCodec;
    private final ShopMapper shopMapper;
    private final RecommendQueryMapper recommendQueryMapper;

    public BlogFeedService(
            BlogMapper blogMapper,
            BlogAssembler blogAssembler,
            RecallOrchestrator recallOrchestrator,
            RankingStrategyRegistry rankingStrategyRegistry,
            FeedCacheService feedCacheService,
            FeedExposureService exposureService,
            CursorCodec cursorCodec,
            ShopMapper shopMapper,
            RecommendQueryMapper recommendQueryMapper
    ) {
        this.blogMapper = blogMapper;
        this.blogAssembler = blogAssembler;
        this.recallOrchestrator = recallOrchestrator;
        this.rankingStrategyRegistry = rankingStrategyRegistry;
        this.feedCacheService = feedCacheService;
        this.exposureService = exposureService;
        this.cursorCodec = cursorCodec;
        this.shopMapper = shopMapper;
        this.recommendQueryMapper = recommendQueryMapper;
    }

    public Result query(String cursor, String rawMode, Boolean refresh) {
        Long userId = requireCurrentUserId();
        FeedMode mode = FeedMode.from(rawMode);
        String cursorType = cursorType(mode);
        CursorPayload position = cursorCodec.decode(cursor, cursorType);

        if (Boolean.TRUE.equals(refresh)) {
            feedCacheService.invalidate(userId, mode.getApiValue());
            position = null;
        }

        int offset = position == null || position.getOffset() == null ? 0 : position.getOffset();
        if (offset < 0) {
            throw invalidCursor();
        }
        String snapshotId = position == null ? null : position.getSnapshotId();
        FeedCachePage cached = feedCacheService.getPage(
                userId, mode.getApiValue(), snapshotId, offset, PAGE_SIZE + 1);
        if (cached.isAvailable()) {
            return Result.ok(fromSnapshot(userId, mode, cached, offset));
        }

        // 快照不可用时用游标里的边界回源重新召回：boundaryTime = 上一页最后一条博客的发布毫秒时间戳，
        // boundaryId = 该博客的 id；召回 SQL 会取 create_time < boundaryTime，
        // 或 create_time = boundaryTime 且 id < boundaryId 的数据——同一毫秒发布的博客靠 ID 继续切分，
        // 保证回源结果至少不重复返回已翻过的数据。两个字段缺任何一个都视为游标损坏，直接报 INVALID_CURSOR。
        Long boundaryTime = position == null ? null : position.getBoundaryScore();
        Long boundaryId = position == null ? null : position.getBoundaryId();
        if (position != null && (boundaryTime == null || boundaryId == null)) {
            throw invalidCursor();
        }
        return Result.ok(rebuild(userId, mode, boundaryTime, boundaryId));
    }

    private CursorPageDTO<BlogCardDTO> fromSnapshot(
            Long userId,
            FeedMode mode,
            FeedCachePage cached,
            int offset
    ) {
        List<FeedCacheEntry> entries = cached.getEntries();
        boolean hasMore = entries.size() > PAGE_SIZE;
        List<FeedCacheEntry> pageEntries = hasMore
                ? new ArrayList<>(entries.subList(0, PAGE_SIZE))
                : entries;
        List<Blog> blogs = hydrateInOrder(pageEntries.stream()
                .map(FeedCacheEntry::getBlogId)
                .collect(Collectors.toList()));
        exposureService.record(userId, blogs);

        String nextCursor = hasMore && !pageEntries.isEmpty()
                ? encodeCursor(mode, cached.getSnapshotId(), offset + pageEntries.size(),
                        pageEntries.get(pageEntries.size() - 1))
                : null;
        return new CursorPageDTO<>(blogAssembler.toCards(blogs), nextCursor, hasMore);
    }

    private CursorPageDTO<BlogCardDTO> rebuild(
            Long userId,
            FeedMode mode,
            Long boundaryTime,
            Long boundaryId
    ) {
        Map<String, Object> extra = new HashMap<>();
        if (boundaryId != null) {
            extra.put("lastId", boundaryId);
        }
        RecallContext context = RecallContext.builder()
                .userId(userId)
                .maxTime(boundaryTime)
                .limit(CANDIDATE_POOL_SIZE)
                .extra(extra)
                .build();
        // 各模式召回通道（名额与用途）：
        // - following：inbox（推模式收件箱，发布时写入的粉丝收件箱，通道不存在时编排器自动跳过）
        //   + follow（拉模式兜底，按关注作者查博客），两通道都用足候选池；
        // - for_you：for-you（社交偏好，配额 80）+ interest（兴趣召回，40）+ cf（协同过滤，20）
        //   + hot（热门兜底，60），四通道名额之和 = 200，由 RecommendProperties 配置。
        List<String> recallChannels = mode == FeedMode.FOLLOWING
                ? Arrays.asList("inbox", "follow")
                : Arrays.asList("for-you", "interest", "cf", "hot");
        List<Long> candidateIds = recallOrchestrator.multiRecall(recallChannels, context);
        List<Blog> candidates = hydrateInOrder(candidateIds);

        if (mode == FeedMode.FOR_YOU) {
            candidates = exposureService.filterUnseen(userId, candidates);
        }
        RankingStrategy<Blog> ranking = rankingStrategyRegistry.getStrategy(mode.getRankingStrategy());
        candidates = ranking.rank(candidates, rankingContext(mode, userId, extra, candidates));
        if (mode == FeedMode.FOR_YOU) {
            candidates = diversifyAuthors(candidates);
        }

        String snapshotId = feedCacheService.cacheFeed(userId, mode.getApiValue(), candidates);
        boolean hasMore = candidates.size() > PAGE_SIZE;
        List<Blog> page = hasMore
                ? new ArrayList<>(candidates.subList(0, PAGE_SIZE))
                : candidates;
        exposureService.record(userId, page);

        // 推荐排序离开快照后无法严格复现（同一批候选重排一次结果就可能不同）；
        // 缓存故障（cacheFeed 返回 null snapshotId）时宁可只返回当前页并置 hasMore=false，
        // 也不伪造一个下次必然取不到数据的可继续游标。
        if (snapshotId == null && mode == FeedMode.FOR_YOU) {
            return new CursorPageDTO<>(blogAssembler.toCards(page), null, false);
        }
        String nextCursor = null;
        if (hasMore && !page.isEmpty()) {
            Blog last = page.get(page.size() - 1);
            nextCursor = encodeCursor(mode, snapshotId, PAGE_SIZE,
                    new FeedCacheEntry(last.getId(), epochMilli(last)));
        }
        return new CursorPageDTO<>(blogAssembler.toCards(page), nextCursor, hasMore);
    }

    private List<Blog> hydrateInOrder(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> distinctIds = ids.stream()
                .filter(Objects::nonNull)
                .distinct()
                .limit(CANDIDATE_POOL_SIZE + 1L)
                .collect(Collectors.toList());
        Map<Long, Blog> byId = blogMapper.selectBatchIds(distinctIds).stream()
                .collect(Collectors.toMap(Blog::getId, blog -> blog, (left, right) -> left));
        return distinctIds.stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @SuppressWarnings("unchecked")
    private RankingContext rankingContext(
            FeedMode mode,
            Long userId,
            Map<String, Object> extra,
            List<Blog> candidates
    ) {
        List<AuthorInteractionDTO> rows = extra.get(ForYouRecall.AUTHOR_INTERACTIONS) instanceof List
                ? (List<AuthorInteractionDTO>) extra.get(ForYouRecall.AUTHOR_INTERACTIONS)
                : Collections.emptyList();
        Map<Long, Integer> counts = new HashMap<>();
        Map<Long, Double> affinity = new HashMap<>();
        for (AuthorInteractionDTO row : rows) {
            int count = row.getInteractionCount() == null ? 0 : row.getInteractionCount();
            counts.put(row.getAuthorId(), count);
            affinity.put(row.getAuthorId(), Math.min(1D, count / 5D));
        }
        if (mode != FeedMode.FOR_YOU) {
            return RankingContext.builder()
                    .currentUserId(userId)
                    .now(LocalDateTime.now())
                    .authorAffinity(affinity)
                    .authorInteractionCount(counts)
                    .build();
        }
        // 兴趣画像：for_you 模式才需要（InterestRankingStrategy 消费）。
        // typeAffinity = 店铺类型 -> 亲和度（用户赞得最多的类型归一化为 1.0）；
        // shopTypeByShopId = 候选博客的店铺 -> 类型，批量查一次，避免排序阶段逐篇查库。
        Map<Long, Double> typeAffinity = loadTypeAffinity(userId);
        Map<Long, Long> shopTypeByShopId = loadShopTypes(candidates);
        return RankingContext.builder()
                .currentUserId(userId)
                .now(LocalDateTime.now())
                .authorAffinity(affinity)
                .authorInteractionCount(counts)
                .typeAffinity(typeAffinity)
                .shopTypeByShopId(shopTypeByShopId)
                .build();
    }

    /**
     * 用户类型画像：一条聚合 SQL（tb_blog_like join tb_blog join tb_shop）拿到各类型点赞次数，
     * 除以最高次数归一化到 0~1。没有点赞历史返回空 Map，排序端取中性值。
     */
    private Map<Long, Double> loadTypeAffinity(Long userId) {
        Map<Long, Double> affinity = new HashMap<>();
        if (userId == null) {
            return affinity;
        }
        List<Map<String, Object>> rows = recommendQueryMapper.selectUserTypeAffinity(userId);
        if (rows == null || rows.isEmpty()) {
            return affinity;
        }
        long max = 0;
        Map<Long, Long> counts = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Long typeId = asLong(row.get("typeId"));
            Long likeCount = asLong(row.get("likeCount"));
            if (typeId == null || likeCount == null) {
                continue;
            }
            counts.put(typeId, likeCount);
            max = Math.max(max, likeCount);
        }
        if (max > 0) {
            final long maxCount = max;
            counts.forEach((typeId, likeCount) -> affinity.put(typeId, likeCount / (double) maxCount));
        }
        return affinity;
    }

    /** 候选博客的店铺类型映射：按候选 shopId 去重后一次 selectBatchIds 查回（1 条 SQL）。 */
    private Map<Long, Long> loadShopTypes(List<Blog> candidates) {
        Map<Long, Long> shopTypeByShopId = new HashMap<>();
        if (candidates == null || candidates.isEmpty()) {
            return shopTypeByShopId;
        }
        List<Long> shopIds = candidates.stream()
                .map(Blog::getShopId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (shopIds.isEmpty()) {
            return shopTypeByShopId;
        }
        for (Shop shop : shopMapper.selectBatchIds(shopIds)) {
            if (shop.getId() != null && shop.getTypeId() != null) {
                shopTypeByShopId.put(shop.getId(), shop.getTypeId());
            }
        }
        return shopTypeByShopId;
    }

    private Long asLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private List<Blog> diversifyAuthors(List<Blog> ranked) {
        Map<Long, Integer> counts = new LinkedHashMap<>();
        List<Blog> primary = new ArrayList<>(ranked.size());
        List<Blog> deferred = new ArrayList<>();
        for (Blog blog : ranked) {
            int count = counts.getOrDefault(blog.getUserId(), 0);
            if (count < MAX_PER_AUTHOR_BEFORE_BACKFILL) {
                primary.add(blog);
                counts.put(blog.getUserId(), count + 1);
            } else {
                deferred.add(blog);
            }
        }
        primary.addAll(deferred);
        return primary;
    }

    private String encodeCursor(
            FeedMode mode,
            String snapshotId,
            int offset,
            FeedCacheEntry boundary
    ) {
        CursorPayload payload = new CursorPayload();
        payload.setType(cursorType(mode));
        payload.setSnapshotId(snapshotId);
        payload.setOffset(offset);
        payload.setBoundaryScore(boundary.getCreateTime());
        payload.setBoundaryId(boundary.getBlogId());
        return cursorCodec.encode(payload);
    }

    private long epochMilli(Blog blog) {
        return blog.getCreateTime() == null
                ? 0L
                : blog.getCreateTime().toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    private Long requireCurrentUserId() {
        if (UserHolder.getUser() == null || UserHolder.getUser().getId() == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        return UserHolder.getUser().getId();
    }

    private String cursorType(FeedMode mode) {
        return "feed-" + mode.getApiValue() + "-v2";
    }

    private BusinessException invalidCursor() {
        return BusinessException.badRequest("INVALID_CURSOR", "Feed 分页游标缺少必要位置");
    }
}
