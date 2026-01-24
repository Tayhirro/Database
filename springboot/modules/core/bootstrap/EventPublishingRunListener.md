# EventPublishingRunListener（启动事件广播器）

> **类型**：核心实现类（Core Implementation）

## 一句话
`EventPublishingRunListener` 是 Spring Boot 中 `SpringApplicationRunListener` 接口的唯一内置实现，负责将 `SpringApplication.run()` 的各个执行阶段转换为相应的生命周期事件（`SpringApplicationEvent`）并广播给所有监听器。

## 严格定义
`org.springframework.boot.context.event.EventPublishingRunListener` 是一个适配器类。它在 `SpringApplication` 启动之初通过 SPI 加载，持有一个内部的 `SimpleApplicationEventMulticaster`，用于将硬编码的 `run()` 方法步骤（如 starting, environmentPrepared）转化为解耦的事件发布调用。

## 核心机制：从“步骤”到“事件”的桥梁
它是连接 **启动流程（Flow）** 与 **事件体系（Event）** 的关键组件。

### 1. 映射关系
它实现了 `SpringApplicationRunListener` 接口的所有回调方法，并一一映射为事件发布：

| RunListener 回调方法           | 发布的事件                                 | 备注                          |
| :------------------------- | :------------------------------------ | :-------------------------- |
| `starting()`               | `ApplicationStartingEvent`            | 此时 Context 未创建              |
| `environmentPrepared(...)` | `ApplicationEnvironmentPreparedEvent` | 此时 Environment 已就绪          |
| `contextPrepared(...)`     | `ApplicationContextInitializedEvent`  | Context 已创建，Initializer 已执行 |
| `contextLoaded(...)`       | `ApplicationPreparedEvent`            | BeanDefinition 已加载          |
| `started(...)`             | `ApplicationStartedEvent`             | Refresh 完成，Runner 执行前       |
| `ready(...)`               | `ApplicationReadyEvent`               | 全部就绪                        |
| `failed(...)`              | `ApplicationFailedEvent`              | 启动异常                        |

### 2. 多播器（Multicaster）
- 它在构造时会初始化一个 `SimpleApplicationEventMulticaster`。
- 它会将 `SpringApplication` 中的 `listeners` 集合注册到这个多播器中。
- **注意**：在 `ApplicationContext` 创建并刷新之前，事件是通过这个**临时多播器**分发的；Context 准备好后，它会将多播器中的监听器移交给 Context 内部的标准多播器（或继续代理，视版本实现细节而定）。

## 接口：数据 + 约束
- **输入**：`SpringApplication` 实例，`args` 参数（构造器注入）。
- **输出**：无返回值（副作用为广播事件）。
- **约束**：必须在 `META-INF/spring.factories` 中注册才能被 `SpringApplication` 发现。

## 常用构造/操作
- **自动装配**：用户通常不需要直接使用该类，它是 Boot 内部自动加载的。
- **注册 Key**：`org.springframework.boot.SpringApplicationRunListener`。

## 关系：上级/下级/等价/特例/推广
- **接口**：`SpringApplicationRunListener`（扩展点接口）。
- **协作**：
  - 上游：`SpringApplication`（调用方）。
  - 下游：`ApplicationListener`（接收方）。
  - 产物：`SpringApplicationEvent`（见 [modules/core/events/ApplicationLifecycleEvents.md](../events/ApplicationLifecycleEvents.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → EventPublishingRunListener → （RunListener / Multicaster）。
