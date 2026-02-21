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


2：博客 




3：缓存击穿 --缓存雪崩

- 高并发读
- 1. 单个热点 key（比如某个爆款博客）并发极高。
2. 这个 key 刚好过期或不在缓存。
3. 大量请求同时打到 DB。



