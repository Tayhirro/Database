package com.hmdp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmdp.dto.AuthorInteractionDTO;
import com.hmdp.entity.BlogLike;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

public interface BlogLikeMapper extends BaseMapper<BlogLike> {

    @Insert("INSERT INTO tb_blog_like (blog_id, user_id, create_time) " +
            "VALUES (#{blogId}, #{userId}, #{createTime})")
    int insertRelation(
            @Param("blogId") Long blogId,
            @Param("userId") Long userId,
            @Param("createTime") LocalDateTime createTime
    );

    @Delete("DELETE FROM tb_blog_like " +
            "WHERE blog_id = #{blogId} AND user_id = #{userId}")
    int deleteRelation(@Param("blogId") Long blogId, @Param("userId") Long userId);

    /**
     * 统计当前用户分别给每位作者点过多少次赞，推荐服务用这个次数判断用户更常看哪些作者。
     * 这里只返回按作者汇总后的次数，不把每一条历史点赞记录都传给后续排序代码。
     */
    @Select("SELECT b.user_id AS authorId, COUNT(*) AS interactionCount " +
            "FROM tb_blog_like bl JOIN tb_blog b ON b.id = bl.blog_id " +
            "WHERE bl.user_id = #{userId} GROUP BY b.user_id " +
            "ORDER BY interactionCount DESC LIMIT 50")
    List<AuthorInteractionDTO> selectAuthorInteractions(@Param("userId") Long userId);
}
