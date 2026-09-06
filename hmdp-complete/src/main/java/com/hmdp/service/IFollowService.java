package com.hmdp.service;

/*
 * 现实业务背景：用户在作者页关注/取关、查看按钮状态、关注列表或共同关注时，需要统一的社交关系服务。
 * 实际触发：FollowController 的四个对外方法直接调用本接口。
 * 存储结构：关系真相保存在 tb_follow（user_id 关注者、follow_user_id 被关注者，数据库唯一约束防重复关注）；
 * 同时每个用户在 Redis 维护一个关注 Set（key 为 follow:{userId}，成员是被关注用户的 ID），用于关注状态判断和共同关注交集。
 */

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.entity.Follow;
import com.hmdp.dto.Result;
/**
 * 关注关系服务。所有方法返回 {@link Result}（本项目统一的 HTTP 响应包装：
 * {@code success/data/errorCode/errorMsg/traceId}）。
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IFollowService extends IService<Follow> {

    /**
     * 按目标状态关注/取关（PUT /follow/{id}/{isFollow}）：isFollow=true 时用数据库唯一约束下的
     * insertIfAbsent 新增关系（重复请求不会插入第二行），false 时按（user_id=当前用户，follow_user_id=目标用户）删除关系；
     * 成功后发布关注变更事件，由监听器同步 Redis 关注 Set 和 Feed 相关缓存。
     */
    Result follow(Long followUserId, Boolean isFollow); // 按目标状态关注/取关

    /**
     * 是否已关注（GET /follow/or/not/{id}）：先查 Redis Set，命中直接返回；
     * 未命中再兜底查 MySQL，数据库里存在则回填 Redis 后返回 true。
     */
    Result isFollow(Long followUserId); // 是否已关注

    /**
     * 查询指定用户的关注列表（GET /follow/list/{id}）。
     * 批量示例：用户 7 关注了 9、11 共 2 人——第 1 条 SQL 查 tb_follow 里的 2 条关系，
     * 第 2 条把 2 个被关注用户 ID 用 IN 一次性查回昵称和头像，共 2 条 SQL。
     */
    Result getFollows(Long userId);

    /**
     * 获取当前用户与目标用户的共同关注（GET /follow/common/{id}）：
     * 在 Redis 中对双方的关注 Set（follow:{当前用户} 与 follow:{目标用户}）求交集，
     * 再把交集里的用户 ID 一次性批量查回用户资料；交集为空直接返回空列表，不查数据库。
     */
    Result followCommons(Long otherUserId); // 获取共同关注
}
