1：博客
- 查询我关注的博客： 
	- 查询关注redis（inbox） --- blog转换 ---返回    普通用户
	- 查询大v --- 大v-db选择
- 查询火的贴子 ：
	- 根据推荐系统有所排序
- 查询这个blog的点赞的用户： 查询缓存 --- 查询db  --- 返回
- saveblog
	- 写tb_blog
	- 写 tb_feed_inbox  写 redis    普通
	- 直接返回 大v



1：博客  查询火贴子
-点赞信息（user 相关）
-blog相关信息
数据存储
- id
- user_id（作者，外键指向用户表）
- title
- content
- images（或拆成子表）
- liked/comments（可做冗余计数）
- create_time/update_time
补充信息：
1：blog ---> user 是否点赞
2：bloger的相关信息


1：博客   分页
-offset 查询  前面数据变化导致 后续数据offset重复 通过时间 游标分页
-mintime+offset 替换 offset

1：博客  拉取策略
-高推送量，少量个体   通过拉取式缓存 
 - 避免同一数据被不同缓存
-低推送量， 大量个体 通过推送式缓存 







2：缓存击穿 --缓存雪崩
 高并发读
1. 单个热点 key（比如某个爆款博客）并发极高。
2. 这个 key 刚好过期或不在缓存。
3. 大量请求同时打到 DB。


4. 单个热点 key（比如某个爆款博客）并发极高。
5. 这个 key 刚好过期或不在缓存。
6. 大量请求同时打到 DB。

2: 缓存过期时间  -- 针对key设置 