package com.hmdp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmdp.entity.Follow;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *
 *  Mapper 接口
 * @author 虎哥
 * @since 2021-12-22
 */
public interface FollowMapper extends BaseMapper<Follow> {

    @Insert("INSERT INTO tb_follow (user_id, follow_user_id) " +
            "VALUES (#{userId}, #{followUserId}) " +
            "ON DUPLICATE KEY UPDATE id = id")
    int insertIfAbsent(@Param("userId") Long userId, @Param("followUserId") Long followUserId);

    @Delete("DELETE FROM tb_follow " +
            "WHERE user_id = #{userId} AND follow_user_id = #{followUserId}")
    int deleteRelation(@Param("userId") Long userId, @Param("followUserId") Long followUserId);

    /**
     * 作者的粉丝数：关注关系里 follow_user_id 等于作者的行数
     * （FeedPushService 用它判断是否超过推模式阈值 fan-threshold，默认 5000）。
     */
    @Select("SELECT COUNT(*) FROM tb_follow WHERE follow_user_id = #{authorId}")
    int countFollowers(@Param("authorId") Long authorId);

    /**
     * 按游标翻页取作者的粉丝用户 ID（FeedPushService 每页 1000 个）：
     * user_id 严格大于 lastFanId 的前 limit 个粉丝，按 user_id 升序返回；
     * 用 user_id 游标代替 OFFSET 深分页，首页传 0。批内 user_id 不会重复
     * （tb_follow 有 UNIQUE(user_id, follow_user_id) 约束），翻页不重不漏。
     */
    @Select("SELECT user_id FROM tb_follow " +
            "WHERE follow_user_id = #{authorId} AND user_id > #{lastFanId} " +
            "ORDER BY user_id ASC LIMIT #{limit}")
    List<Long> listFollowerIdsAfter(@Param("authorId") Long authorId,
                                    @Param("lastFanId") long lastFanId,
                                    @Param("limit") int limit);
}
