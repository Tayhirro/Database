package com.hmdp.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 推荐召回专用查询 Mapper：兴趣召回、协同过滤召回、热门召回和类型亲和度统计。
 *
 * 这四条 SQL 只服务于推荐链路（BlogFeedService 的 for_you 模式），
 * 都是只读查询，不修改任何数据。
 */
public interface RecommendQueryMapper {

    /**
     * 兴趣召回：找出当前用户最偏爱的店铺类型（按点赞过的博客归属类型聚合，取前 3 类），
     * 再返回这些类型下、用户没点赞过且不是自己写的博客，按发布时间倒序。
     *
     * SQL 结构：内层子查询按 type_id 聚合点赞数取 TOP3；外层 join tb_shop 过滤类型。
     * 排序规则 ORDER BY create_time DESC, id DESC，id 用来同一时刻分先后。
     *
     * @param maxTime 游标时间边界（上一页最后一条的发布时间，UTC），为空时不限
     * @param lastId  游标 ID 边界（与 maxTime 同一时刻时取 id 更小的），maxTime 为空时忽略
     * @return 博客 ID 列表，最多 limit 条
     */
    @Select("<script>" +
            "SELECT b.id FROM tb_blog b " +
            "JOIN tb_shop s ON s.id = b.shop_id " +
            "WHERE s.type_id IN (" +
            "  SELECT t.type_id FROM (" +
            "    SELECT s2.type_id AS type_id, COUNT(*) AS cnt " +
            "    FROM tb_blog_like l " +
            "    JOIN tb_blog b2 ON b2.id = l.blog_id " +
            "    JOIN tb_shop s2 ON s2.id = b2.shop_id " +
            "    WHERE l.user_id = #{userId} " +
            "    GROUP BY s2.type_id " +
            "    ORDER BY cnt DESC, s2.type_id ASC " +
            "    LIMIT 3" +
            "  ) t" +
            ") " +
            "AND b.user_id != #{userId} " +
            "AND b.id NOT IN (SELECT blog_id FROM tb_blog_like WHERE user_id = #{userId}) " +
            "<if test='maxTime != null'>" +
            "AND (b.create_time &lt; #{maxTime} OR (b.create_time = #{maxTime} AND b.id &lt; #{lastId})) " +
            "</if>" +
            "ORDER BY b.create_time DESC, b.id DESC " +
            "LIMIT #{limit}" +
            "</script>")
    List<Long> selectInterestBlogIds(@Param("userId") Long userId,
                                     @Param("maxTime") LocalDateTime maxTime,
                                     @Param("lastId") Long lastId,
                                     @Param("limit") int limit);

    /**
     * 协同过滤召回（item-based CF）：以当前用户最近点过赞的博客为种子，
     * 找"也点赞过这些种子的其他用户"还点赞了什么，按共同点赞人数倒序返回候选。
     *
     * SQL 结构：l1 是种子的点赞记录（谁点过种子），l2 是这些人的其他点赞；
     * COUNT(DISTINCT l1.user_id) 是每篇候选博客的"共同点赞人数"；
     * 排除用户已点赞的（NOT IN 子查询）和种子本身（l2.blog_id != l1.blog_id）。
     *
     * @param seeds 种子博客 ID（用户最近点赞的 cf-recent-likes 篇），为空时不要调用本方法
     * @return 博客 ID 列表（按共同点赞人数降序），最多 limit 条
     */
    @Select("<script>" +
            "SELECT l2.blog_id AS blogId " +
            "FROM tb_blog_like l1 " +
            "JOIN tb_blog_like l2 ON l2.user_id = l1.user_id AND l2.blog_id != l1.blog_id " +
            "WHERE l1.blog_id IN " +
            "<foreach collection='seeds' item='seed' open='(' separator=',' close=')'>#{seed}</foreach> " +
            "AND l2.blog_id NOT IN (SELECT blog_id FROM tb_blog_like WHERE user_id = #{userId}) " +
            "GROUP BY l2.blog_id " +
            "ORDER BY COUNT(DISTINCT l1.user_id) DESC, l2.blog_id DESC " +
            "LIMIT #{limit}" +
            "</script>")
    List<Long> selectCollaborativeBlogIds(@Param("userId") Long userId,
                                          @Param("seeds") List<Long> seeds,
                                          @Param("limit") int limit);

    /**
     * 热门召回：最近 7 天内（UTC_TIMESTAMP 回溯）点赞数最高的博客，
     * ORDER BY liked DESC, id DESC，id 用来同点赞数分先后。冷启动用户（没有任何点赞历史）
     * 的 interest/cf 通道为空时，由本通道兜底填满候选池。
     */
    @Select("SELECT id FROM tb_blog " +
            "WHERE create_time > DATE_SUB(UTC_TIMESTAMP(), INTERVAL 7 DAY) " +
            "ORDER BY liked DESC, id DESC " +
            "LIMIT #{limit}")
    List<Long> selectHotBlogIds(@Param("limit") int limit);

    /**
     * 用户店铺类型亲和度：按用户点赞过的博客归属类型聚合点赞次数。
     *
     * @return 每行两个字段：typeId（店铺类型 ID）、likeCount（该类型的累计点赞次数），
     *         按 likeCount 降序；没有点赞历史时返回空列表
     */
    @Select("SELECT s.type_id AS typeId, COUNT(*) AS likeCount " +
            "FROM tb_blog_like l " +
            "JOIN tb_blog b ON b.id = l.blog_id " +
            "JOIN tb_shop s ON s.id = b.shop_id " +
            "WHERE l.user_id = #{userId} " +
            "GROUP BY s.type_id " +
            "ORDER BY likeCount DESC")
    List<Map<String, Object>> selectUserTypeAffinity(@Param("userId") Long userId);
}
