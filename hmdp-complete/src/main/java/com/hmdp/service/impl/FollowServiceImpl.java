package com.hmdp.service.impl;

/*
 * 现实业务背景：用户点击关注按钮、进入关注列表或查看共同关注时，需要读写 tb_follow 并协调缓存。
 * 实际触发：FollowController 的 follow、isFollow、getFollows、followCommons 四个方法进入本类。
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Follow;
import com.hmdp.entity.User;
import com.hmdp.mapper.FollowMapper;
import com.hmdp.service.IFollowService;
import com.hmdp.service.IUserService;
import com.hmdp.service.follow.FollowChangedEvent;
import com.hmdp.utils.UserHolder;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;

import static com.hmdp.utils.RedisConstants.FOLLOW_KEY;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 *  服务实现类
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
@RequiredArgsConstructor
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

    private final StringRedisTemplate stringRedisTemplate;
    private final IUserService userService;
    private final FollowMapper followMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 关注或取关的完整流程：
     * 1. 当前用户从登录上下文取得，校验目标用户、目标状态，并禁止关注自己。
     * 2. {@code isFollow=true} 时用数据库唯一约束下的 insertIfAbsent（按 {@code user_id + follow_user_id} 插入 tb_follow，重复请求不会多插一行）
     *    新增关系；false 时按双方 ID 删除关系。
     * 3. 数据库操作成功后发布关注变更事件，由监听器在事务提交后同步/失效 Redis 关注 Set
     *    （每个用户一个 key：{@code follow:<用户ID>}，Set 成员是该用户关注的用户 ID）；方法处于事务中，异常会回滚关系写入。
     * 具体例子：用户 7 请求 {@code follow(9,true)} 后新增“7 关注 9”；重复请求不会新增第二行；
     * {@code follow(9,false)} 则删除这行，并通知 Feed/关注缓存更新。
     */
    @Override
    @Transactional
    public Result follow(Long followUserId, Boolean isFollow){
        Long userId = UserHolder.getUser().getId();
        String validationError = validateFollowRequest(userId, followUserId, isFollow);
        if (validationError != null) {
            return Result.fail(validationError);
        }

        if (Boolean.TRUE.equals(isFollow)) {
            followMapper.insertIfAbsent(userId, followUserId);
        } else {
            followMapper.deleteRelation(userId, followUserId);
        }
        eventPublisher.publishEvent(new FollowChangedEvent(userId, followUserId, isFollow));
        return Result.ok();
    }

    private String validateFollowRequest(Long userId, Long followUserId, Boolean isFollow) {
        if (followUserId == null) {
            return "目标用户不能为空";
        }
        if (userId.equals(followUserId)) {
            return "不能关注自己";
        }
        if (isFollow == null) {
            return "关注状态不能为空";
        }
        return null;
    }

    /**
     * 查询关注列表的完整流程：校验被查看用户 ID，查询其全部关注关系，提取目标用户 ID，
     * 批量读取用户资料并转换为只包含公开摘要的 UserDTO；没有关系或用户时返回空列表。
     * 具体例子：用户 7 关注了 9、11，调用 {@code getFollows(7)} 会一次查出两条关系，再批量返回用户 9、11 的昵称和头像。
     */
    @Override
    public Result getFollows(Long userId){
        if (userId == null) {
            return Result.fail("用户ID不能为空");
        }
        List<Follow> follows = query().eq("user_id", userId).list();
        if (follows == null || follows.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }
        List<Long> followUserIds = follows.stream().map(Follow::getFollowUserId).collect(Collectors.toList());
        List<User> users = userService.listByIds(followUserIds);
        if (users == null || users.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }
        List<UserDTO> userDTOs = users.stream().map(user -> BeanUtil.copyProperties(user, UserDTO.class)).collect(Collectors.toList());
        return Result.ok(userDTOs);
    }

    /**
     * 查询共同关注的完整流程：取得当前登录用户 ID，在 Redis 中对双方的关注 Set 求交集
     * （即 {@code SINTER follow:<我的ID> follow:<对方ID>}，key 由常量 {@code FOLLOW_KEY="follow:"} 拼用户 ID 组成），
     * 把交集 ID 批量查询为用户并转换成 DTO；交集为空时直接返回空列表。
     * 具体例子：当前用户 7 关注 {9,11}，用户 8 关注 {9,12}，{@code followCommons(8)} 返回用户 9。
     */
    @Override
    public Result followCommons(Long userId){
        if (userId == null) {
            return Result.fail("目标用户不能为空");
        }
        Long myId = UserHolder.getUser().getId();
        Set<String> followIds = stringRedisTemplate.opsForSet().intersect(FOLLOW_KEY + myId, FOLLOW_KEY + userId);
        if (followIds == null || followIds.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }
        List<Long> ids = followIds.stream().map(id -> Long.valueOf(id.toString())).collect(Collectors.toList());
        //follow-id  --->  user ---> userDTO
        List<User> followUsers = userService.listByIds(ids);
        List<UserDTO> userDTOs = followUsers.stream().map(user -> BeanUtil.copyProperties(user, UserDTO.class)).collect(Collectors.toList());
        return Result.ok(userDTOs);
    }

    /**
     * 判断关注状态的完整流程：取得当前用户后，直接按 tb_follow 的唯一索引（user_id + follow_user_id）
     * 执行一次 selectCount，count 大于 0 即视为已关注，数据库是唯一事实来源。
     * 取舍说明：旧实现“Redis 关注 Set 命中即返回 true”，一旦取关后事件监听器同步 Set 失败（只记 warn 日志），
     * 残留成员会让该用户长期被误判为仍在关注；改为一次按索引的 COUNT 查询后，取关立即生效，代价是一次走索引的点查。
     * Redis 关注 Set（key 为 {@code follow:<用户ID>}）仍由关注/取关事件维护，继续服务 followCommons 的 SINTER 求交集，
     * 本方法不再读写它。
     * 具体例子：用户 7 关注过用户 9 后取关，即使 {@code follow:7} 里仍残留成员 9，本方法也返回 false；
     * 反之关系行存在就返回 true，不依赖 Redis 是否命中。
     */
    @Override
    public Result isFollow(Long followUserId){
        if (followUserId == null) {
            return Result.fail("目标用户不能为空");
        }
        Long selfId = UserHolder.getUser().getId();
        // 以数据库唯一索引（user_id + follow_user_id）为准，一次 selectCount 判定；
        // 不再读 Redis 关注 Set：取关后 Set 删除失败会残留成员，SISMEMBER 命中会把“已取关”误报成“仍在关注”。
        Long count = followMapper.selectCount(
                new LambdaQueryWrapper<Follow>()
                        .eq(Follow::getUserId, selfId)
                        .eq(Follow::getFollowUserId, followUserId));
        return Result.ok(count != null && count > 0);
    }
}
