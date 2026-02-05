# Spring Boot 学习笔记

本仓库用于积累 Spring Boot 开发过程中的核心工具、常用注解、配置技巧和最佳实践。

## 目录结构

```
SpringBoot-Notes/
├── 01-核心工具类/          # 常用工具类总结
│   ├── README/            # 工具类文档
│   │   ├── BeanUtils.md
│   │   ├── RedisUtils.md
│   │   └── RedisLock.md
│   └── 示例代码/          # 实际使用示例
│       ├── BeanUtilsDemo.java
│       ├── RedisUtils.java
│       └── RedisLock.java
├── 02-数据访问/           # 数据库、缓存等数据访问技术
│   ├── README/
│   │   └── Redis-Complete-Guide.md
│   └── 示例代码/
├── 03-配置技巧/           # application.yml 配置技巧
│   ├── README/
│   │   └── Redis-Configuration.md
│   └── 示例代码/
├── 04-最佳实践/           # 项目开发最佳实践
│   ├── README/
│   │   └── Redis-Cache-Problems.md
│   └── 示例代码/
└── 05-源码分析/           # 核心源码解析
    ├── README/
    └── 示例代码/
```

## 已整理内容

### 01-核心工具类

- **[BeanUtils](01-核心工具类/README/BeanUtils.md)** - Bean 属性拷贝工具
  - 基本用法、CopyOptions 配置
  - DTO 与 Entity 转换场景
  - 工具对比（Spring、Hutool、MapStruct）

- **[RedisUtils](01-核心工具类/README/RedisUtils.md)** - Redis 操作封装
  - String、Hash、List、Set、ZSet 操作
  - 实用场景方法（验证码、Token、排行榜）

- **[RedisLock](01-核心工具类/README/RedisLock.md)** - 分布式锁实现
  - 基于 Redis 的分布式锁
  - 防止超卖、重复提交等场景
  - 与 Redisson 对比

### 02-数据访问

- **[Redis 完整指南](02-数据访问/README/Redis-Complete-Guide.md)** ⭐  
  从 java/redis 迁移的完整文档，包含：
  - 基础配置与连接池详解
  - 五大数据类型操作
  - 对象序列化存储
  - 分布式锁实现
  - 管道操作
  - 缓存问题解决方案

### 03-配置技巧

- **[Redis 配置详解](03-配置技巧/README/Redis-Configuration.md)**
  - Maven 依赖配置
  - application.yml 详解
  - Lettuce 连接池配置
  - 常见问题排查
  - 序列化配置

### 04-最佳实践

- **[Redis 缓存问题解决方案](04-最佳实践/README/Redis-Cache-Problems.md)**
  - 缓存穿透：空值缓存、布隆过滤器
  - 缓存击穿：互斥锁、逻辑过期
  - 缓存雪崩：随机过期、多级缓存

## 使用建议

1. **README 目录**：存放理论知识、使用说明、注意事项
2. **示例代码目录**：存放可直接运行的示例代码
3. 每个工具类都应包含：简介、核心方法、使用场景、注意事项、示例代码

## 待整理清单

### 核心工具类
- [x] BeanUtils - Bean 属性拷贝
- [x] RedisUtils - Redis 操作封装
- [x] RedisLock - 分布式锁
- [ ] Validation - 参数校验
- [ ] Jackson - JSON 处理
- [ ] HttpClient - HTTP 客户端（RestTemplate / WebClient）
- [ ] Cache - 缓存注解
- [ ] Async - 异步处理

### 配置技巧
- [x] Redis 配置
- [ ] 数据库连接池配置（Druid/HikariCP）
- [ ] 日志配置
- [ ] 多环境配置
- [ ] 配置文件加密

### 最佳实践
- [x] 缓存问题解决方案
- [ ] 全局异常处理
- [ ] 统一响应封装
- [ ] 接口幂等性设计
- [ ] 接口限流
- [ ] 日志规范

### 数据访问
- [x] Redis 完整指南
- [ ] MyBatis-Plus 使用
- [ ] JPA 使用
- [ ] 多数据源配置
- [ ] 事务管理

### 源码分析
- [ ] Spring Boot 自动配置原理
- [ ] Spring IOC 容器启动流程
- [ ] Spring AOP 实现原理
- [ ] Spring Transaction 事务原理

## 快速导航

| 场景 | 推荐文档 |
|------|----------|
| 刚接触 Spring Boot Redis | [Redis 配置详解](03-配置技巧/README/Redis-Configuration.md) → [RedisUtils](01-核心工具类/README/RedisUtils.md) |
| 需要完整 Redis 操作参考 | [Redis 完整指南](02-数据访问/README/Redis-Complete-Guide.md) |
| 遇到缓存问题 | [Redis 缓存问题解决方案](04-最佳实践/README/Redis-Cache-Problems.md) |
| 需要分布式锁 | [RedisLock](01-核心工具类/README/RedisLock.md) |

---

**最后更新**：2024-02-06  
**迁移记录**：java/redis → SpringBoot-Notes/02-数据访问
