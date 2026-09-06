-- 秒杀订单约束：一人一单的最终裁决者 + 常用查询索引。
-- 一人一单规则：同一用户在同一张券上最多有一条订单记录（含已取消）。
-- 取消订单只把状态改为 4（已取消）并释放库存，不删除记录，
-- 因此唯一键天然阻止“取消后再抢一张”，这与 Lua 资格集合的语义保持一致。
ALTER TABLE `tb_voucher_order`
    ADD UNIQUE KEY `uk_user_voucher` (`user_id`, `voucher_id`),
    ADD KEY `idx_voucher` (`voucher_id`),
    ADD KEY `idx_user_create` (`user_id`, `create_time`);
