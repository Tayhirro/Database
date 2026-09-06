-- 关注表的被关注人索引：推模式收件箱按 follow_user_id 统计粉丝数、按页拉粉丝 ID；
-- 共同关注等查询也受益。没有该索引时这两类查询是全表扫描。
ALTER TABLE `tb_follow`
    ADD KEY `idx_follow_follow_user` (`follow_user_id`);
