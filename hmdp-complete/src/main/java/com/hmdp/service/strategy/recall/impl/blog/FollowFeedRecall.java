package com.hmdp.service.strategy.recall.impl.blog;

/*
 * 现实业务背景：用户打开关注流时，候选内容只能来自自己已经关注的作者，并按发布时间继续向后读取。
 * 实际触发：following 和 for_you 的召回编排都可能调用 follow 通道；本类先取得关注 ID，再从 MySQL 查询博客 ID。
 *
 * recall() 的具体步骤（以一次翻页请求为例）：
 * 1. 取关注列表：先走 {@link FollowCacheService}（关注列表缓存服务：Redis 缓存各用户的关注 ID，
 *    未命中时用下面的 loader 回源查询 tb_follow 表），得到用户关注的作者 ID 列表；
 *    没有关注任何人则直接返回空列表。
 * 2. 查博客候选：SELECT id FROM tb_blog WHERE user_id IN (关注作者们)，再叠加游标边界
 *    （来自 {@link RecallContext}（召回上下文：用户、时间边界、候选上限、共享信号））：
 *    - maxTime 为空（首页）：不加时间条件；
 *    - 有 maxTime 且 extra 里没有 lastId：只取 create_time < maxTime；
 *    - 有 maxTime 且有 lastId（上一页最后一条的时间+ID）：取 create_time < maxTime，
 *      或者 create_time = maxTime 且 id < lastId——同一时刻发布的博客靠 ID 继续切分，
 *      保证翻页不重复、不漏内容。
 * 3. 排序与截断：ORDER BY create_time DESC, id DESC，然后 LIMIT ctx.getLimit()
 *    （BlogFeedService 传入 200），只查 id 一列，按查出的顺序返回博客 ID。
 * 通道注册名为 "follow"；following 模式只用它，for_you 模式把它和 "for-you" 通道的结果合并。
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hmdp.entity.Blog;
import com.hmdp.entity.Follow;
import com.hmdp.mapper.BlogMapper;
import com.hmdp.service.IFollowService;
import com.hmdp.service.feedcache.FollowCacheService;
import com.hmdp.service.strategy.recall.RecallContext;
import com.hmdp.service.strategy.recall.RecallStrategy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FollowFeedRecall implements RecallStrategy {

    @Resource
    private IFollowService followService;

    @Resource
    private BlogMapper blogMapper;

    @Resource
    private FollowCacheService followCacheService;

    @Override
    public List<Long> recall(RecallContext ctx) {
        List<Long> followedIds = followCacheService.getFollowedIds(ctx.getUserId(), id -> {
            List<Follow> follows = followService.query().eq("user_id", id).list();
            return follows.stream().map(Follow::getFollowUserId).collect(Collectors.toList());
        });
        if (followedIds == null || followedIds.isEmpty()) {
            return Collections.emptyList();
        }

        QueryWrapper<Blog> wrapper = new QueryWrapper<>();
        wrapper.select("id").in("user_id", followedIds);
        if (ctx.getMaxTime() != null && ctx.getMaxTime() > 0) {
            LocalDateTime maxDateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(ctx.getMaxTime()), ZoneOffset.UTC);
            Long lastId = getLastId(ctx.getExtra());
            if (lastId == null) {
                wrapper.lt("create_time", maxDateTime);
            } else {
                wrapper.and(w -> w.lt("create_time", maxDateTime)
                        .or()
                        .eq("create_time", maxDateTime)
                        .lt("id", lastId));
            }
        }
        wrapper.orderByDesc("create_time");
        wrapper.orderByDesc("id");
        wrapper.last("LIMIT " + ctx.getLimit());

        List<Blog> blogs = blogMapper.selectList(wrapper);
        return blogs.stream().map(Blog::getId).collect(Collectors.toList());
    }

    private Long getLastId(Map<String, Object> extra) {
        if (extra == null) {
            return null;
        }
        Object value = extra.get("lastId");
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    @Override
    public String getStrategyName() {
        return "follow";
    }
}
