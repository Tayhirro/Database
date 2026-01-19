# Spring Boot 介绍：启动、`spring.factories`、`ApplicationContext`

> 迁移提示：Spring Boot“出书式”整理已迁移到顶层目录 `springboot/`。本页保留为早期笔记；新入口见 `springboot/README.md`。

> 目标：把 Spring Boot 的“启动链路 + 扩展点发现机制 + 上下文类型”用一套统一的概念讲清楚，方便在阅读源码/排查问题时定位。

## 1. Spring Boot 在做什么

Spring Boot 的核心价值不是“替代 Spring”，而是基于 Spring Framework 提供：

- **约定优于配置**：常用配置有默认值，减少样板
- **Starter 依赖聚合**：用 `spring-boot-starter-*` 把一组常用依赖打包成“可组合模块”
- **自动配置（Auto-Configuration）**：根据 classpath、配置项、已有 Bean 等条件，自动创建一批 Bean
- **启动流程编排**：`SpringApplication` 负责环境准备、事件发布、创建并刷新 `ApplicationContext`

## 2. `ApplicationContext` vs `ConfigurableApplicationContext`

### 2.1 继承关系（接口层面）

`ApplicationContext` 可以理解为“容器的对外使用视角”（拿 Bean、资源、事件等）；  
`ConfigurableApplicationContext` 则是在此基础上增加“容器生命周期与配置能力”的子接口。

简化理解：

- `ConfigurableApplicationContext` **extends** `ApplicationContext`
- `ConfigurableApplicationContext` 额外提供：`refresh()`、`close()`、`registerShutdownHook()`、`getBeanFactory()` 等管理能力

### 2.2 为什么 `SpringApplication.run()` 返回的是 `ConfigurableApplicationContext`

Spring Boot 启动过程需要：

- 创建上下文实例（servlet/reactive/none 不同实现）
- 往上下文里塞环境、初始化器、监听器、BeanFactory 后处理器等
- **触发 `refresh()`** 完成 Bean 定义加载、实例化、生命周期回调、事件发布等
- 在失败/退出时可 **关闭 `close()`** 并清理资源

因此 Boot 内部必须握住一个“可刷新/可关闭”的上下文引用，所以 API 选择返回 `ConfigurableApplicationContext`。

> 业务代码里你也经常只声明 `ApplicationContext` 类型：因为大多数业务只需要“读”容器（取 Bean），不需要直接控制生命周期。

### 2.3 常见实现类（直觉）

实际运行时拿到的对象通常是某个具体实现类（取决于 Web 类型），例如：

- Servlet Web：`AnnotationConfigServletWebServerApplicationContext`
- Reactive Web：`AnnotationConfigReactiveWebServerApplicationContext`
- 非 Web：`AnnotationConfigApplicationContext`

## 3. 我自己项目没有 `spring.factories`，为什么还能自动配置？

因为 **`spring.factories` 不是“你的业务项目必须提供”的文件**；它是 Spring/Boot 的一种 SPI 配置方式，通常由 **starter/框架依赖的 jar** 提供。

### 3.1 `spring.factories` 是怎么被用到的（Boot 2.x 语境）

Spring 提供 `SpringFactoriesLoader`，它会：

1. 调用 `ClassLoader.getResources("META-INF/spring.factories")`
2. 扫描 **classpath 上所有 jar**（包含你项目与所有依赖）
3. 读取并合并这些 jar 中的 `META-INF/spring.factories`
4. 按 key（接口/约定扩展点）拿到对应的实现类列表

Boot 启动时典型会从 `spring.factories` 加载：

- `ApplicationContextInitializer`
- `ApplicationListener`
- `EnableAutoConfiguration` 对应的自动配置类列表

### 3.2 结合 HMDP-Redis 的直观解释

在 `HMDP-Redis/hm-dianping/pom.xml` 里你用的是 Spring Boot `2.3.12.RELEASE`（Boot 2.3 仍以 `spring.factories` 为主）。  
你的业务工程 `src/main/resources` 下没有 `META-INF/spring.factories` 并不影响启动，因为：

- `spring-boot-autoconfigure-2.3.12.RELEASE.jar` 自己就带了 `META-INF/spring.factories`
- 其他 starter / 三方库也可能带 `META-INF/spring.factories`
- 启动时会把它们全合并起来生效

你在 IDE 的 External Libraries 或本机 Maven 仓库里可以直接定位到类似路径：

- `~/.m2/repository/org/springframework/boot/spring-boot-autoconfigure/2.3.12.RELEASE/spring-boot-autoconfigure-2.3.12.RELEASE.jar!/META-INF/spring.factories`

### 3.3 版本提示：`spring.factories` vs `AutoConfiguration.imports`

从 Spring Boot 2.7/3.x 开始，自动配置类列表逐步迁移到：

- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

但你的项目是 Boot 2.3，所以理解 `spring.factories` 的机制最关键。

## 4. 相关阅读（更详细的源码/机制）

- `springboot/flows/运行全链路.md`：从启动到结束的主线框架（当前先写启动）
- `study/real_springboot_init_analysis.md`：真实 Boot 启动链路的源码级梳理
- `HMDP-Redis/docs/spring-factories-mechanism.md`：`spring.factories` 的机制、场景与排查 checklist
