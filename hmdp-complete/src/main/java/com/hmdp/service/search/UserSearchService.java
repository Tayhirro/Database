package com.hmdp.service.search;

/*
 * 现实业务背景：用户在统一搜索框输入创作者昵称时，需要找到公开用户并进入其主页，
 * 而不是检索登录凭证等敏感数据。
 * 实际触发：GET /search 综合页的用户分组，或用户切换到“用户”Tab 后调用 GET /search/users。
 *
 * 设计精华：
 * 1. 只搜索 tb_user 的 nick_name 字段（MySQL LIKE 匹配），只返回 id、nickName、icon；
 *    账号（account）、手机号（phone）和密码（password）既不参与匹配也不进入查询结果。
 * 2. 用户搜索与店铺、笔记平级（同为 {@link VerticalSearchService} 垂直搜索接口的实现），
 *    统一层只按 scope 调度，不感知 tb_user 的内部字段。
 * 3. 第一阶段使用 MySQL LIKE 和按 id 升序的稳定分页，后续搜索引擎实现可以在不改接口的情况下替换。
 */

import com.hmdp.dto.UserDTO;

public interface UserSearchService extends VerticalSearchService<UserDTO> {

    /** 声明当前垂直服务只负责公开用户搜索。 */
    @Override
    default SearchScope scope() {
        return SearchScope.USER;
    }
}
