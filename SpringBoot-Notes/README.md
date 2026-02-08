# Spring Boot 学习笔记

本仓库用于积累 Spring Boot 开发过程中的**真实存在的官方工具类**、常用注解、配置技巧和最佳实践。

## ⚠️ 重要说明

**本笔记只记录 Spring Boot 官方真实提供的 API 和工具类，不包含自定义封装！**

---

## 目录结构

```
SpringBoot-Notes/
├── 01-核心工具类/          # Spring 官方提供的工具类总结
│   ├── README/
│   │   ├── BeanUtils.md              # ✅ Spring 官方
│   │   ├── StringRedisTemplate.md    # ✅ Spring Data Redis 官方
│   │   └── BitFieldSubCommands.md    # ✅ Redis 位字段操作
│   └── 示例代码/
│       └── BeanUtilsDemo.java
│
├── 02-数据访问/           # 数据库、缓存等数据访问技术
│   └── README/
│       ├── Redis-Complete-Guide.md   # Redis 完整操作指南
│       ├── MyBatis-Plus-IService.md  # MyBatis-Plus IService CRUD 封装
│       ├── MyBatis-Plus-Page.md      # MyBatis-Plus 分页对象
│
├── 03-配置技巧/           # application.yml 配置技巧
│   └── README/
│       └── Redis-Configuration.md    # Redis 配置详解
│
├── 04-最佳实践/           # 项目开发最佳实践
│   └── README/
│       └── Redis-Cache-Problems.md   # 缓存问题解决方案
│       └── SpringBoot-Testing-Interview-CheatSheet.md  # 测试面试速记
│
└── 05-源码分析/           # 核心源码解析
    └── README/
        └── (待补充)
```

---

## 已整理内容

### 01-核心工具类（Spring 官方 API）

#### ✅ BeanUtils - Bean 属性拷贝
- **来源**: `org.springframework.beans.BeanUtils`
- **文档**: [BeanUtils.md](01-核心工具类/README/BeanUtils.md)
- **功能**: 
  - 对象属性拷贝
  - DTO ↔ Entity 转换
  - 忽略某些字段

#### ✅ StringRedisTemplate - Redis 字符串操作
- **来源**: `org.springframework.data.redis.core.StringRedisTemplate`
- **文档**: [StringRedisTemplate.md](01-核心工具类/README/StringRedisTemplate.md)
- **功能**:
  - String、Hash、List、Set、ZSet 操作
  - 过期时间设置
  - 分布式锁基础

#### ✅ BitFieldSubCommands - Redis 位字段操作
- **来源**: `org.springframework.data.redis.connection.BitFieldSubCommands`
- **文档**: [BitFieldSubCommands.md](01-核心工具类/README/BitFieldSubCommands.md)
- **功能**:
  - 位级整数读写（1-64位）
  - 签到系统、多字段紧凑存储
  - 比传统方案节省 90%+ 空间
---

### 02-数据访问

#### Redis 完整指南
- **文档**: [Redis-Complete-Guide.md](02-数据访问/README/Redis-Complete-Guide.md)
- **内容**: 从配置到操作的完整手册（1072行）


#### MyBatis-Plus IService - CRUD 封装
- **来源**: `com.baomidou.mybatisplus.extension.service.IService`
- **文档**: [MyBatis-Plus-IService.md](02-数据访问/README/MyBatis-Plus-IService.md)
- **内容**:
  - Service 层 CRUD 方法（save/remove/get/list/page/update）
  - 批量操作、链式调用
  - 自定义 Mapper 方法扩展
---

### 03-配置技巧


#### MyBatis-Plus Page - 分页对象
- **来源**: `com.baomidou.mybatisplus.extension.plugins.pagination.Page<T>`
- **文档**: [MyBatis-Plus-Page.md](02-数据访问/README/MyBatis-Plus-Page.md)
- **功能**:
  - 封装分页参数（current, size）
  - 接收分页结果（total, pages, records）
  - 泛型支持（Page<User>, Page<Order>）
#### Redis 配置详解
- **文档**: [Redis-Configuration.md](03-配置技巧/README/Redis-Configuration.md)
- **内容**:
  - Maven 依赖
  - application.yml 配置
  - 连接池参数详解
  - 序列化配置

---

### 04-最佳实践

#### Redis 缓存问题解决方案
- **文档**: [Redis-Cache-Problems.md](04-最佳实践/README/Redis-Cache-Problems.md)
- **内容**:
  - 缓存穿透（空值缓存、布隆过滤器）
  - 缓存击穿（互斥锁、逻辑过期）
  - 缓存雪崩（随机过期、多级缓存）

#### Spring Boot 测试面试速记
- **文档**: [SpringBoot-Testing-Interview-CheatSheet.md](04-最佳实践/README/SpringBoot-Testing-Interview-CheatSheet.md)
- **内容**:
  - JUnit5 常用注解与断言
  - Mockito Mock/verify 用法
  - `@SpringBootTest` / `@WebMvcTest` 对比
  - 面试高频问答与测试设计方法

---

## 快速导航

### 按场景查找

| 需求 | 推荐文档 |
|------|----------|
| 对象属性拷贝 | [BeanUtils](01-核心工具类/README/BeanUtils.md) |
| Redis 基本操作 | [StringRedisTemplate](01-核心工具类/README/StringRedisTemplate.md) |
| Redis 配置 | [Redis 配置详解](03-配置技巧/README/Redis-Configuration.md) |
| Redis 完整手册 | [Redis 完整指南](02-数据访问/README/Redis-Complete-Guide.md) |
| 缓存问题 | [Redis 缓存问题](04-最佳实践/README/Redis-Cache-Problems.md) |
| 测试面试速记 | [Testing CheatSheet](04-最佳实践/README/SpringBoot-Testing-Interview-CheatSheet.md) |

### 学习路径

#### 入门（Redis）
1. [Redis 配置详解](03-配置技巧/README/Redis-Configuration.md) - 先配置好
2. [StringRedisTemplate](01-核心工具类/README/StringRedisTemplate.md) - 学习基本操作

#### 进阶（Redis）
3. [Redis 完整指南](02-数据访问/README/Redis-Complete-Guide.md) - 深入学习
4. [Redis 缓存问题](04-最佳实践/README/Redis-Cache-Problems.md) - 生产必看

---

## 待整理清单

### 核心工具类（Spring 官方）
- [x] BeanUtils - Bean 属性拷贝 ✅
- [x] StringRedisTemplate - Redis 字符串操作 ✅
- [ ] RedisTemplate - Redis 对象操作
- [ ] RestTemplate - HTTP 客户端
- [ ] WebClient - 响应式 HTTP 客户端
- [ ] JdbcTemplate - JDBC 操作
- [ ] ObjectMapper - JSON 序列化（Jackson）

### 常用注解
- [ ] @Cacheable / @CacheEvict - 缓存注解
- [ ] @Async - 异步处理
- [ ] @Transactional - 事务管理
- [ ] @Validated - 参数校验
- [ ] @Scheduled - 定时任务

### 配置技巧
- [x] Redis 配置 ✅
- [ ] 数据库连接池配置（HikariCP）
- [ ] Logback 日志配置
- [ ] 多环境配置

### 最佳实践
- [x] Redis 缓存问题 ✅
- [ ] 全局异常处理
- [ ] 统一响应封装
- [ ] 接口幂等性
- [ ] 接口限流

---

## 说明

### 为什么只记录官方 API？

1. **真实可靠** - 官方提供，有完整文档和社区支持
2. **团队通用** - 所有 Spring Boot 项目都能直接使用
3. **IDE 支持** - 有完整的代码提示和跳转
4. **长期维护** - 官方持续更新和维护

### 如何使用本笔记？

1. **查找工具类** - 在 `01-核心工具类` 中找到需要的官方 API
2. **查看示例** - 每个工具类都有完整的使用示例
3. **快速上手** - 复制示例代码，根据需求修改

---

**最后更新**: 2024-02-06  
**原则**: 只记录 Spring Boot 官方真实存在的 API
