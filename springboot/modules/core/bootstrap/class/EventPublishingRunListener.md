# EventPublishingRunListener（启动事件广播器）

> **类型**：核心实现类（Core Implementation）

## 一句话
`EventPublishingRunListener` 是 Spring Boot 中 `SpringApplicationRunListener` 接口的唯一内置实现，负责将 `SpringApplication.run()` 的各个执行阶段转换为相应的生命周期事件（`SpringApplicationEvent`）并广播给所有监听器。

## 严格定义
`org.springframework.boot.context.event.EventPublishingRunListener` 是一个适配器类。它在 `SpringApplication` 启动之初通过 SPI 加载，并在类内部持有一个 `SimpleApplicationEventMulticaster` 类型的字段 `initialMulticaster`，用于将 `run()` 方法的阶段回调（如 starting、environmentPrepared）转化为事件发布调用。

## 继承链（接口链 / 实现链）
- 继承链：`java.lang.Object` → `EventPublishingRunListener`。
- 实现接口：
  - `SpringApplicationRunListener`（接收启动阶段回调，并在实现中把阶段映射为事件发布）
  - `Ordered`（参与排序：决定与其他 `SpringApplicationRunListener` 的调用先后）

## 核心机制：从“步骤”到“事件”的桥梁
它将 `SpringApplicationRunListener` 的阶段回调映射为一组 `SpringApplicationEvent`，并在不同阶段选择不同的发布通道（早期多播器 / Context 发布）。

### 1. 映射关系（事件名）
它实现了 `SpringApplicationRunListener` 接口的回调方法，并映射为事件发布：

| RunListener 回调方法（Boot 2.3.x）       | 发布的事件                                 | 备注                             |
| :--------------------------------- | :------------------------------------ | :----------------------------- |
| `starting()`                       | `ApplicationStartingEvent`            | Context 未创建                    |
| `environmentPrepared(environment)` | `ApplicationEnvironmentPreparedEvent` | Environment 已就绪                |
| `contextPrepared(context)`         | `ApplicationContextInitializedEvent`  | Context 已创建（refresh 之前）        |
| `contextLoaded(context)`           | `ApplicationPreparedEvent`            | BeanDefinition 已加载（refresh 之前） |
| `started(context)`                 | `ApplicationStartedEvent`             | refresh 完成，Runner 执行前          |
| `running(context)`                 | `ApplicationReadyEvent`               | Runner 执行完毕                    |
| `failed(context, ex)`              | `ApplicationFailedEvent`              | 启动异常（context 可能为 `null`）       |

### 2. 发布通道（Early Multicast vs Context Publish）
它同时使用两条事件发布通道：

| 阶段（Boot 2.3.x）                                               | 发布入口                                                                   | 分发器                                                                      |
| :----------------------------------------------------------- | :--------------------------------------------------------------------- | :----------------------------------------------------------------------- |
| `starting/environmentPrepared/contextPrepared/contextLoaded` | `initialMulticaster.multicastEvent(...)`                               | `initialMulticaster`（`SimpleApplicationEventMulticaster`）                |
| `started/running`                                            | `context.publishEvent(...)`                                            | `applicationEventMulticaster`（Context 内部的 `ApplicationEventMulticaster`） |
| `failed`                                                     | `context.publishEvent(...)` 或 `initialMulticaster.multicastEvent(...)` | 依赖 `context` 是否存在且 active                                                |

`contextLoaded(context)` 阶段会将 `SpringApplication.getListeners()` 中的监听器注册到 `context`（并对实现了 `ApplicationContextAware` 的监听器注入 `ApplicationContext`），以便后续通过 `context.publishEvent(...)` 继续接收事件。

## 接口：数据 + 约束
- **数据（类内字段）**：
  - `application: SpringApplication`
  - `args: String[]`
  - `initialMulticaster: SimpleApplicationEventMulticaster`
- **输入**：`SpringApplication` 实例，`args` 参数（构造器注入）。
- **输出**：无返回值（副作用为广播事件）。
- **约束**：必须在 `META-INF/spring.factories` 中注册才能被 `SpringApplication` 发现。

## 常用构造/操作
- **自动装配**：用户通常不需要直接使用该类，它是 Boot 内部自动加载的。
- **注册 Key**：`org.springframework.boot.SpringApplicationRunListener`。

## 关系：上级/下级/等价/特例/推广
- 接口：`SpringApplicationRunListener`（见 [../interface/SpringApplicationRunListener.md](../interface/SpringApplicationRunListener.md)）。
- 调用链：`SpringApplication` → `SpringApplicationRunListeners` → `EventPublishingRunListener`（见 [SpringApplicationRunListeners.md](SpringApplicationRunListeners.md)）。
- 下游：
  - `initialMulticaster`：`SimpleApplicationEventMulticaster`（见 [../../events/class/SimpleApplicationEventMulticaster.md](../../events/class/SimpleApplicationEventMulticaster.md)）
  - 监听器：`ApplicationListener`（见 [../../events/interface/ApplicationListener.md](../../events/interface/ApplicationListener.md)）
- 产物：`SpringApplicationEvent`（见 [../../events/mechanism/ApplicationLifecycleEvents.md](../../events/mechanism/ApplicationLifecycleEvents.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → EventPublishingRunListener → （RunListener / Multicaster）。
