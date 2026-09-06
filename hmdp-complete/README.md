# 代码使用说明(本项目来自b站[黑马程序员](https://space.bilibili.com/37974444)[redis教程](https://www.bilibili.com/video/BV1cr4y1671t)，仅供参考)

项目代码包含2个分支：
- master : 主分支，包含完整版代码，作为大家的编码参考使用
- init : 初始化分支，实战篇的初始代码，建议大家以这个分支作为自己开发的基础代码
- 前端资源在src/main/resources/nginx-1.18.0下
- 本仓库新增 Nuxt UI 前端：`hmdp/frontend`（用于替代旧的静态页面）

视频地址:
- [黑马程序员Redis入门到实战教程，深度透析redis底层原理+redis分布式锁+企业解决方案+redis实战](https://www.bilibili.com/video/BV1cr4y1671t)
- [https://www.bilibili.com/video/BV1cr4y1671t](https://www.bilibili.com/video/BV1cr4y1671t)
  - P24起 实战篇

## 知识库（本仓库补充）

- Spring 注入注解：`docs/knowledge/spring/di-annotations.md`

## 1.下载
克隆完整项目
```git
git clone https://github.com/cs001020/hmdp.git
```
切换分支
```git
git checkout init
```

## 2.常见问题
部分同学直接使用了master分支项目来启动，控制台会一直报错:
```
NOGROUP No such key 'stream.orders' or consumer group 'g1' in XREADGROUP with GROUP option
```
这是因为我们完整版代码会尝试访问Redis，连接Redis的Stream。建议同学切换到init分支来开发，如果一定要运行master分支，请先在Redis运行一下命令：
```text
XGROUP CREATE stream.orders g1 $ MKSTREAM
```

### 短信验证码（开发环境）
本仓库默认不接入真实短信平台，发送验证码时会通过 `SmsSender` 的日志实现打印验证码。
- 配置项：`hmdp.sms.provider=log`
- 对应实现：`src/main/java/com/hmdp/sms/impl/LogSmsSender.java`
- 需要接入真实短信平台时：新增一个 `SmsSender` 实现并把 `hmdp.sms.provider` 切换到对应值

## hmdp-complete 增强版说明（2026-08-31）

本目录是 `hmdp` 的增强副本，在原项目基础上补全了未完成功能并做了工程化加固。
**默认使用独立数据库 `hmdp_complete`**（首次启动 Flyway 从 V1 全量建库并自带演示数据），
与原 `hmdp` 项目互不影响；禁止把本副本指向共享的 `hmdp` 库——
副本新增的迁移（V11+）会写进共享库的 Flyway 历史，导致原项目启动校验失败。

### 本次补全的功能

1. **秒杀交易闭环**：Lua 原子资格校验（查库存/判重/扣减/记资格）→ Redis Stream 异步落库
   （消费组 + PEL 重试 + 死信列表 + 资格补偿）→ 订单轮询/取消/支付模拟；
   订单号来自 Redis 全局 ID 生成器；`UNIQUE(user_id, voucher_id)` 兜底一人一单。
2. **秒杀券管理闭环**：券详情、白名单字段修改、库存调整（同步 Redis 预热库存）；
   管理身份 = `hmdp.admin.user-ids` 白名单；库存预热在创建秒杀券事务提交后写入。
3. **售罄订阅与提醒**：订阅/取消/状态查询；补货自动通知订阅用户；活动开始前 10 分钟提醒（每活动一次）。
4. **推荐系统（分接口，简单→复杂）**：hot/interest/for-you/cf 四路召回（配额 60/40/80/20）
   + time/simple/weighted/interest 四个排序策略；详见 `docs/推荐系统设计.md`。
5. **Feed 推拉结合**：普通作者发布写入粉丝收件箱（tb_feed_inbox），大 V（粉丝数超
   `hmdp.feed.push.fan-threshold`）不推；following 模式收件箱优先、拉模式兜底；每小时清理超容量收件箱。
6. **搜索联想**：`GET /search/suggest`，三域前缀匹配 + 热词兜底（`search:hot:` ZSet）。
7. **店铺评价域**：`/shop-review` 发布/列表/删除/统计（一人一店一评，统计表聚合，
   平均分 = 总分/条数）。
8. **第二轮加固**：注册与资料初始化同事务、Token 原子写入、isFollow 回源 MySQL、
   店铺更新/删除影响行数裁决与缓存/GEO 同步、清理任务互斥与失败监控、
   图片删除失败保持 DELETING 重试、点赞计数对账任务、异常分类（DataAccessException → 503）。

### 启动

```bash
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS hmdp_complete DEFAULT CHARACTER SET utf8mb4"
mvn spring-boot:run   # Flyway 自动建库表（V1~V12）并带演示数据
mvn test              # 98+ 单元测试
```
