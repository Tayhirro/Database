package com.hmdp.service;

/*
 * 现实业务背景：博客 Controller 需要一个稳定入口处理发布、编辑、删除、点赞、详情、榜单和 Feed，而不能直接操作通用 CRUD。
 * 实际触发：所有 /blog 对外接口先调用本门面契约，再由实现分派给 Command、Like、Query 或 Feed 服务。
 * 实现说明：BlogServiceImpl 只做委托——发布/编辑/删除走 BlogCommandService，点赞与点赞用户列表走 BlogLikeService，
 * 详情/热榜/个人博客列表走 BlogQueryService，关注流与推荐流走 BlogFeedService；各子服务自持事务边界。
 */

import com.hmdp.dto.BlogPublishRequest;
import com.hmdp.dto.BlogUpdateRequest;
import com.hmdp.dto.Result;

/**
 * 博客用例门面，仅暴露完整业务用例，不继承可绕过权限、图片和幂等规则的通用 CRUD。
 * 所有方法成功时返回 {@link Result}（本项目统一的 HTTP 响应包装：
 * {@code success/data/errorCode/errorMsg/traceId}；分页类接口的 data 是含 list/nextCursor/hasMore 的游标分页对象）。
 * 下列各服务均为本项目的内部类：
 * {@link com.hmdp.service.blog.BlogCommandService}（博客发布、编辑、删除的命令服务，负责参数校验、幂等与图片绑定）、
 * {@link com.hmdp.service.blog.BlogLikeService}（点赞、取消点赞和“最近点赞用户”列表）、
 * {@link com.hmdp.service.blog.BlogQueryService}（博客详情、热门榜、我的/他人博客列表）、
 * {@link com.hmdp.service.blog.BlogFeedService}（关注流 following 与推荐流 for_you 的 Feed 分页）。
 */
public interface IBlogService {
    /** 点赞博客（PUT /blog/{id}/like）：按“博客 ID + 当前用户”唯一新增点赞关系，首次成功才把博客点赞数加一。 */
    Result likeBlog(Long id);

    /** 取消点赞（DELETE /blog/{id}/like）：删除点赞关系，确实删到才把点赞数减一；重复取消幂等。 */
    Result unlikeBlog(Long id);

    /** 博客详情（GET /blog/{id}）：返回博客正文、图片、作者摘要和当前用户点赞状态；不内嵌评论列表，评论由 /blog-comments 单独分页加载。 */
    Result queryBlogById(Long id);

    /** 博客“最近点赞”用户列表（GET /blog/likes/{id}）：按点赞时间和关系 ID 倒序的游标分页，limit 有效范围 1～50，默认 10。 */
    Result queryBlogLikes(Long id, String cursor, Integer limit);

    /**
     * Feed 流（GET /blog/feed）：mode=following 关注流 / for_you 推荐流，每页最多 50 张博客卡片；
     * 刷新（refresh=true）会废弃当前快照重新召回，普通翻页按游标沿用同一快照，保证下拉加载期间内容不跳变。
     */
    Result queryBlogFeed(String cursor, String mode, Boolean refresh);

    /**
     * 热门博客榜（GET /blog/hot）。
     * 游标 =（score：上一页最后一条博客的 liked 点赞数，id：博客主键）；
     * 排序规则 ORDER BY liked DESC, id DESC，id 用来在两篇博客点赞数相同时分先后。
     */
    Result queryHotBlog(String cursor, Integer limit);

    /**
     * 当前登录用户自己的博客列表（GET /blog/of/me）：作者 ID 隐式取自登录上下文，不能指定他人。
     * 游标 =（score：上一页最后一条博客的 createTime 发布时间，id：博客主键）；
     * 排序规则 ORDER BY create_time DESC, id DESC。
     */
    Result queryMyBlogs(String cursor, Integer limit);

    /**
     * 指定作者的公开博客列表（GET /blog/of/user?id={userId}）。
     * 游标 =（score：上一页最后一条博客的 createTime 发布时间，id：博客主键）；
     * 排序规则 ORDER BY create_time DESC, id DESC；不存在的 userId 返回空列表而不是 404。
     */
    Result queryBlogsByUserId(Long userId, String cursor, Integer limit);

    /**
     * 发布博客（POST /blog）：校验标题 1～255 字、正文 1～2048 字、商户存在和 1～9 张本人 TEMP 图片，
     * 并用请求里的 clientRequestId（1～64 位字母数字下划线横线）做幂等——同一请求号重试返回同一篇博客。
     */
    Result saveBlog(BlogPublishRequest request);

    /**
     * 编辑博客（PUT /blog/{id}）：request 是编辑后的完整状态而非增量，imageIds 是最终图片顺序（1～9 个）；
     * 只有作者本人可编辑，被移除的图片在事务提交后才物理删除。
     */
    Result updateBlog(Long id, BlogUpdateRequest request);

    /** 删除博客（DELETE /blog/{id}）：只有作者本人可删；同一事务里清理点赞关系、评论和博客行，图片等事务提交后由后台任务删除文件。 */
    Result deleteBlog(Long id);
}
