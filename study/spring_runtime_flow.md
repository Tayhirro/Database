---
title: Spring / Spring Boot 运行流程
date: "2026-01-20"
categories:
  - study
description: 迁移提示：更完整的“从运行到结束”全链路主线已迁移到 springboot/flows/运行全链路.md，本页保留为草稿。
---
# Spring / Spring Boot 运行流程

> 迁移提示：更完整的“从运行到结束”全链路主线已迁移到 `springboot/flows/运行全链路.md`，本页保留为草稿。


## 0. 先记住两句话

- **Spring 的核心**：用 `ApplicationContext` 管“对象世界”（Bean 的创建、依赖注入、生命周期、事件、资源、配置）。
- **Spring Boot 的核心**：用 `SpringApplication` 把“这个对象世界如何启动起来”标准化，并通过 classpath 自动发现（starter/auto-config）把基础设施装配好。

---

## 1. 启动哲学（Spring Boot 想解决什么）

Spring Boot 的“启动哲学”可以概括成：**把应用的组装过程产品化**。

- **约定优于配置**：能推断就推断（从 classpath、配置项、已有 Bean 推断），给出合理默认值，减少样板。
- **依赖即能力**：引入一个 starter 依赖≈把一组“基础设施能力”加入 classpath，Boot 会在启动时自动发现并接入。
- **条件化自动装配**：一切自动配置都不是“强行生效”，而是通过 `@Conditional...` 在合适场景才装配，避免冲突。
- **分阶段 + 可插拔 hook**：启动被拆成固定阶段（准备环境→创建上下文→refresh→ready），每个阶段都能插入监听器/初始化器/后处理器。
- **可解释/可排查**：自动配置为何生效/为何不生效，应该能通过报告与条件评估追踪出来（例如 `--debug`）。

---

## 2. 上下文（ApplicationContext）是什么：一种“运行时世界观”

很多人把 `ApplicationContext` 理解成“Bean 容器”，但更准确的说法是：**应用运行时的对象世界（world）**。

### 2.1 它解决的本质问题

在没有容器时，你需要自己解决：

- 对象何时创建、创建顺序、依赖怎么传递（new + 手动 wiring）
- 配置怎么注入、不同环境怎么切换
- 生命周期怎么管理（初始化/销毁回调）
- 横切逻辑怎么统一（事务、缓存、权限、日志、AOP）

Spring 的答案是：把这些“组装与治理”的责任交给上下文，用 IoC/DI 把业务代码从基础设施中解耦出来。

### 2.2 `ApplicationContext` vs `ConfigurableApplicationContext`

- `ApplicationContext`：偏“使用侧视角”（拿 Bean、资源、事件、环境等）
- `ConfigurableApplicationContext`：在前者之上增加“启动/关闭/刷新”的生命周期控制能力（`refresh()`、`close()`…）

Spring Boot 启动必须控制生命周期，所以 `SpringApplication.run()` 返回 `ConfigurableApplicationContext`。

---

## 3. 总览：从 `main()` 到应用可用（Boot 的启动流程骨架）

下面是一张“导航地图”，先把阶段记住，读源码时就知道自己在哪一段：

```
main()
  -> SpringApplication.run(primarySource, args)
       1) 构造 SpringApplication（推断 Web 类型、加载 initializer/listener）
       2) run(args)
            2.1 准备 Environment（配置源、profile、属性）
            2.2 创建 ApplicationContext（不同 Web 类型不同实现）
            2.3 准备 Context（把 env/initializers/listeners 等塞进去）
            2.4 refresh()（进入 Spring Framework 的核心启动模板）
            2.5 afterRefresh + runners（ApplicationRunner/CommandLineRunner）
            2.6 发布 ready 事件（应用对外可服务）
       -> 返回 ConfigurableApplicationContext
```

---

## 4. `spring.factories` 在哪用：为什么你项目里没有也能工作

（以 Spring Boot 2.x 为主；你当前 HMDP 项目是 Boot `2.3.x`）

Boot 会通过 `SpringFactoriesLoader` 扫描 **classpath 上所有 jar** 的 `META-INF/spring.factories` 并合并结果，用来发现一批“扩展点实现”，典型包括：

- `ApplicationContextInitializer`
- `ApplicationListener`
- `EnableAutoConfiguration` 对应的自动配置类列表（auto-config 的入口）

所以：

- 业务项目通常 **不需要自己提供** `spring.factories`
- 你引入的 starter/框架依赖 jar 里提供了它们各自的 `spring.factories`
- 启动时统一被扫描并生效

> 版本演进：Boot 2.7/3.x 将自动配置类列表迁移到 `AutoConfiguration.imports`，但扩展点思想不变。

---

## 5. `refresh()` 才是 Spring Framework 的“点火键”

如果说 Boot 负责“跑启动编排”，那么 Spring Framework 的核心点火过程是：

- `ConfigurableApplicationContext.refresh()`

它由 `AbstractApplicationContext.refresh()` 提供模板方法（Template Method），高层结构大致是：

```
refresh()
  -> 准备上下文状态/环境
  -> 获取/创建 BeanFactory 并加载 BeanDefinitions
  -> 执行 BeanFactoryPostProcessor（修改 Bean 定义）
  -> 注册 BeanPostProcessor（拦截 Bean 创建：AOP 等在这里介入）
  -> 初始化事件广播器 / 注册监听器
  -> 实例化非懒加载单例 Bean（finishBeanFactoryInitialization）
  -> 发布 ContextRefreshedEvent
```

你理解了 refresh 的“模板骨架”，再回头看 Boot 的流程会更清晰：Boot 做的很多工作，都是在 refresh 前把材料准备好，让 refresh 能按既定模板把“世界”点起来。

---

## 6. 扩展点地图：要改行为，你通常插在哪

按“越早越底层”的直觉顺序：

- `EnvironmentPostProcessor`：非常早期修改环境/配置（Boot 侧）
- `ApplicationContextInitializer`：refresh 之前定制 `ApplicationContext`
- `ApplicationListener`：监听启动各阶段事件（starting/envPrepared/contextPrepared/started/ready…）
- `BeanFactoryPostProcessor`：改 Bean 定义（Bean 还没创建）
- `BeanPostProcessor`：拦截 Bean 创建（AOP/代理常在这里生效）
- `ApplicationRunner` / `CommandLineRunner`：容器已 ready 后跑一些启动任务

---

## 7. 推荐阅读路径（从“会用”到“会读源码”）

- 如果你想先形成概念闭环：`study/springboot_intro.md`
- 如果你关心 `spring.factories` 的机制与排查：`HMDP-Redis/docs/spring-factories-mechanism.md`
- 如果你要对照真实 Boot 源码逐行走：`study/real_springboot_init_analysis.md`
- 如果你想从 mini-spring 反推框架设计：`study/springboot_init_analysis.md`
