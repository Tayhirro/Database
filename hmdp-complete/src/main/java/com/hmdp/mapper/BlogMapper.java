package com.hmdp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmdp.entity.Blog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 
 *  Mapper 接口
 * @author 虎哥
 * @since 2021-12-22
 */
public interface BlogMapper extends BaseMapper<Blog> {

    /**
     * 写操作先锁定博客行：同一博客的编辑/删除串行执行，后到请求基于最新状态校验，
     * 避免两个事务各自按旧图片集合计算差异。
    */
    @Select("SELECT * FROM tb_blog WHERE id = #{id} FOR UPDATE")
    Blog selectByIdForUpdate(@Param("id") Long id);

    @Update("UPDATE tb_blog SET liked = liked + 1 WHERE id = #{id}")
    int incrementLiked(@Param("id") Long id);

    @Update("UPDATE tb_blog SET liked = IF(liked > 0, liked - 1, 0) WHERE id = #{id}")
    int decrementLiked(@Param("id") Long id);

    @Update("UPDATE tb_blog SET comments = comments + 1 WHERE id = #{id}")
    int incrementComments(@Param("id") Long id);

    @Update("UPDATE tb_blog SET comments = GREATEST(comments - #{amount}, 0) WHERE id = #{id}")
    int decrementComments(@Param("id") Long id, @Param("amount") int amount);

    /** 编辑字段白名单：所有权同时进入 WHERE，系统字段永远不会被实体回写覆盖。 */
    @Update("UPDATE tb_blog SET shop_id = #{shopId}, title = #{title}, " +
            "content = #{content}, images = #{images} " +
            "WHERE id = #{id} AND user_id = #{userId}")
    int updateEditableFields(
            @Param("id") Long id,
            @Param("userId") Long userId,
            @Param("shopId") Long shopId,
            @Param("title") String title,
            @Param("content") String content,
            @Param("images") String images
    );

    /**
     * 点赞对账第一步：找出最近 48 小时有点赞活动的博客 ID（去重，最多 500 篇）。
     * 48 小时和 500 都是固定值：48 小时覆盖两个完整自然日的活跃窗口，500 防止单轮对账过长。
     * 命中索引 idx_blog_time 前缀不够时会退化为全表去重扫描，靠 LIMIT 500 和每日一跑控制成本。
     */
    @Select("SELECT DISTINCT blog_id FROM tb_blog_like " +
            "WHERE create_time > NOW() - INTERVAL 48 HOUR LIMIT 500")
    List<Long> selectRecentlyLikedBlogIds();

    /**
     * 点赞对账第二步：统计某篇博客在 tb_blog_like 里的真实点赞关系条数。
     * 表上有 uk_blog_user 唯一键（blog_id + user_id），COUNT 不会因重复行虚高。
     */
    @Select("SELECT COUNT(*) FROM tb_blog_like WHERE blog_id = #{blogId}")
    long countLikesByBlogId(@Param("blogId") Long blogId);

    /**
     * 点赞对账第三步：用关系表真实条数覆盖 tb_blog.liked 冗余计数。
     * 只在计数漂移（例如历史故障导致不一致）时由 LikeCountReconciliationJob 调用；
     * 正常点赞/取消点赞走 incrementLiked/decrementLiked，不经过本方法。
     */
    @Update("UPDATE tb_blog SET liked = #{actualCount} WHERE id = #{blogId}")
    int resetLikedToCount(@Param("blogId") Long blogId, @Param("actualCount") long actualCount);
}
