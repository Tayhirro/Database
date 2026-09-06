package com.hmdp.service.search.impl;

/*
 * 现实业务背景：用户输入创作者昵称后，需要找到可进入其主页的公开用户卡片。
 * 实际触发：GET /search/users，或 GET /search 的 USER 分组调用。
 *
 * 设计精华：
 * 1. 只以 tb_user 的 nick_name 字段做 MySQL LIKE 匹配，绝不允许通过 account、phone、password 搜索用户。
 * 2. SELECT 也只读取 id、nick_name、icon 三列，在持久层就截断敏感数据，而不只依赖 JSON 忽略字段。
 * 3. 按 id 升序稳定分页，页码从 1 起、每页上限 10；空关键词直接返回空页，
 *    不退化为公开用户目录或全表扫描；关键词里的 %、_、\ 会转义成普通字符，
 *    防止用户输入 LIKE 通配符扩大匹配范围。
 */

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmdp.dto.PageResultDTO;
import com.hmdp.dto.SearchQuery;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.search.UserSearchService;
import com.hmdp.utils.SystemConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MySqlUserSearchService implements UserSearchService {

    private final UserMapper userMapper;

    @Override
    public PageResultDTO<UserDTO> search(SearchQuery query) {
        int pageNumber = MySqlSearchSupport.normalizePage(query == null ? null : query.getCurrent());
        int pageSize = MySqlSearchSupport.normalizePageSize(
                query == null ? null : query.getPageSize(),
                SystemConstants.MAX_PAGE_SIZE
        );
        String keyword = MySqlSearchSupport.normalizeKeyword(query == null ? null : query.getKeyword());
        if (StrUtil.isBlank(keyword)) {
            return PageResultDTO.empty(pageNumber, pageSize);
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .select(User::getId, User::getNickName, User::getIcon)
                .like(User::getNickName, MySqlSearchSupport.escapeLikeKeyword(keyword))
                .orderByAsc(User::getId);
        Page<User> page = userMapper.selectPage(new Page<>(pageNumber, pageSize), wrapper);

        List<UserDTO> items = page.getRecords().stream()
                .map(this::toSearchItem)
                .collect(Collectors.toList());
        boolean hasMore = page.getCurrent() * page.getSize() < page.getTotal();
        return new PageResultDTO<>(items, page.getCurrent(), page.getSize(), page.getTotal(), hasMore);
    }

    private UserDTO toSearchItem(User user) {
        UserDTO item = new UserDTO();
        item.setId(user.getId());
        item.setNickName(user.getNickName());
        item.setIcon(user.getIcon());
        return item;
    }
}
