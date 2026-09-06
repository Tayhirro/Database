package com.hmdp.service.search.impl;

/*
 * 现实业务背景：用户还没点“搜索”时，输入框每敲一个字就要实时下拉返回可回填的候选，
 * 让用户少打字并直达目标店铺、笔记或用户，而不是等完整搜索返回后再选 Tab。
 * 实际触发：搜索框输入变化时调用 GET /search/suggest?keyword=...，由
 * {@link SearchSuggestionService}（搜索输入联想的接口合同）的实现类响应本请求。
 *
 * 设计精华：
 * 1. 一次联想请求按店铺、博客、用户的顺序在当前线程串行执行 3 条前缀匹配 SQL（共 3 条，无并行）：
 *    tb_shop.name LIKE 'kw%' ORDER BY id ASC LIMIT 4；
 *    tb_blog.title LIKE 'kw%' ORDER BY create_time DESC, id DESC LIMIT 3；
 *    tb_user.nick_name LIKE 'kw%' ORDER BY id ASC LIMIT 3，
 *    总数最多 4 + 3 + 3 = 10 条，按 SHOP、BLOG、USER 顺序拼进同一个列表。
 * 2. 前缀匹配用 likeRight 拼出 LIKE 'kw%'（只在右侧补 %），关键词里的 %、_、\ 转义
 *    与三个垂直搜索完全一致（复用 MySqlSearchSupport.escapeLikeKeyword），
 *    防止用户输入 LIKE 通配符扩大匹配范围。
 * 3. 关键词处理复用 MySqlSearchSupport.normalizeKeyword：去首尾空白、超过 64 字符直接报错；
 *    空关键词不查库，改为读 Redis 热词 ZSet 兜底（key 是 search:hot:，成员=搜索词、
 *    分数=搜索次数，由统一搜索成功后 ZINCRBY 维护），执行 ZREVRANGE search:hot: 0 9
 *    取分数最高的前 10 个热词，候选只填 text，scope 和 targetId 留空。
 * 4. 任何一条 SQL 异常只记 warn 日志并返回已拿到的部分结果，不让联想接口整体 500：
 *    例如店铺那条 SQL 失败时，博客和用户的候选照常返回。
 * 5. 用户域 SELECT 只读 id 和 nick_name 两列，绝不触碰 account、phone、password，
 *    在持久层就截断敏感数据，而不只依赖 JSON 忽略字段。
 * 6. 城市和位置上下文（SearchQuery 的 cityCode、longitude、latitude）当前与三个垂直
 *    搜索一样不参与 MySQL 匹配，按位置过滤候选留到后续阶段。
 */

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.dto.SearchQuery;
import com.hmdp.dto.SearchSuggestionDTO;
import com.hmdp.entity.Blog;
import com.hmdp.entity.Shop;
import com.hmdp.entity.User;
import com.hmdp.mapper.BlogMapper;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.search.SearchScope;
import com.hmdp.service.search.SearchSuggestionService;
import com.hmdp.utils.RedisConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MySqlSearchSuggestionService implements SearchSuggestionService {

    private static final int SHOP_SUGGEST_LIMIT = 4;
    private static final int BLOG_SUGGEST_LIMIT = 3;
    private static final int USER_SUGGEST_LIMIT = 3;

    /** 热词兜底数量，等于三个域的配额之和（4 + 3 + 3），与热词 ZSet 的 ZREVRANGE 0 9 对应。 */
    private static final int HOT_WORD_COUNT = 10;

    private final ShopMapper shopMapper;
    private final BlogMapper blogMapper;
    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public List<SearchSuggestionDTO> suggest(SearchQuery query) {
        String keyword = MySqlSearchSupport.normalizeKeyword(query == null ? null : query.getKeyword());
        if (StrUtil.isBlank(keyword)) {
            return listHotKeywords();
        }

        String escapedKeyword = MySqlSearchSupport.escapeLikeKeyword(keyword);
        List<SearchSuggestionDTO> suggestions =
                new ArrayList<>(SHOP_SUGGEST_LIMIT + BLOG_SUGGEST_LIMIT + USER_SUGGEST_LIMIT);
        suggestions.addAll(suggestShops(escapedKeyword));
        suggestions.addAll(suggestBlogs(escapedKeyword));
        suggestions.addAll(suggestUsers(escapedKeyword));
        return suggestions;
    }

    /**
     * 第 1 条 SQL：tb_shop.name LIKE 'kw%' ORDER BY id ASC LIMIT 4。
     * 只读 id 和 name 两列；按 id 升序保证同一前缀的下拉顺序稳定。
     * 失败时记 warn 日志并返回空列表，后续博客、用户的联想继续执行。
     */
    private List<SearchSuggestionDTO> suggestShops(String escapedKeyword) {
        try {
            List<Shop> shops = shopMapper.selectList(new LambdaQueryWrapper<Shop>()
                    .select(Shop::getId, Shop::getName)
                    .likeRight(Shop::getName, escapedKeyword)
                    .orderByAsc(Shop::getId)
                    .last("LIMIT " + SHOP_SUGGEST_LIMIT));
            return shops.stream()
                    .map(shop -> new SearchSuggestionDTO()
                            .setText(shop.getName())
                            .setScope(SearchScope.SHOP)
                            .setTargetId(shop.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("店铺输入联想查询失败, 已返回其余候选", e);
            return Collections.emptyList();
        }
    }

    /**
     * 第 2 条 SQL：tb_blog.title LIKE 'kw%' ORDER BY create_time DESC, id DESC LIMIT 3。
     * 只匹配标题，不搜正文；同笔记搜索一样按创建时间倒序、创建时间相同再比 id，保证顺序稳定。
     * 失败时记 warn 日志并返回空列表，后续用户的联想继续执行。
     */
    private List<SearchSuggestionDTO> suggestBlogs(String escapedKeyword) {
        try {
            List<Blog> blogs = blogMapper.selectList(new LambdaQueryWrapper<Blog>()
                    .select(Blog::getId, Blog::getTitle)
                    .likeRight(Blog::getTitle, escapedKeyword)
                    .orderByDesc(Blog::getCreateTime)
                    .orderByDesc(Blog::getId)
                    .last("LIMIT " + BLOG_SUGGEST_LIMIT));
            return blogs.stream()
                    .map(blog -> new SearchSuggestionDTO()
                            .setText(blog.getTitle())
                            .setScope(SearchScope.BLOG)
                            .setTargetId(blog.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("博客输入联想查询失败, 已返回其余候选", e);
            return Collections.emptyList();
        }
    }

    /**
     * 第 3 条 SQL：tb_user.nick_name LIKE 'kw%' ORDER BY id ASC LIMIT 3。
     * SELECT 只取 id 和 nick_name，敏感列不出现在 SQL 里；按 id 升序保证顺序稳定。
     * 失败时记 warn 日志并返回空列表，不影响前面已拿到的店铺、博客候选。
     */
    private List<SearchSuggestionDTO> suggestUsers(String escapedKeyword) {
        try {
            List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>()
                    .select(User::getId, User::getNickName)
                    .likeRight(User::getNickName, escapedKeyword)
                    .orderByAsc(User::getId)
                    .last("LIMIT " + USER_SUGGEST_LIMIT));
            return users.stream()
                    .map(user -> new SearchSuggestionDTO()
                            .setText(user.getNickName())
                            .setScope(SearchScope.USER)
                            .setTargetId(user.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("用户输入联想查询失败, 已返回其余候选", e);
            return Collections.emptyList();
        }
    }

    /**
     * 空关键词时的热词兜底：ZREVRANGE search:hot: 0 9，返回分数最高的前 10 个搜索词。
     * 热词是普通 Query 建议，不直达任何业务对象，因此只填 text，scope 和 targetId 留空。
     * Redis 异常只记 warn 日志并返回空列表，联想接口仍正常响应。
     */
    private List<SearchSuggestionDTO> listHotKeywords() {
        try {
            Set<String> hotWords = stringRedisTemplate.opsForZSet()
                    .reverseRange(RedisConstants.SEARCH_HOT_KEY, 0, HOT_WORD_COUNT - 1);
            if (hotWords == null || hotWords.isEmpty()) {
                return Collections.emptyList();
            }
            return hotWords.stream()
                    .map(word -> new SearchSuggestionDTO().setText(word))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("搜索联想热词兜底读取失败, key={}", RedisConstants.SEARCH_HOT_KEY, e);
            return Collections.emptyList();
        }
    }
}
