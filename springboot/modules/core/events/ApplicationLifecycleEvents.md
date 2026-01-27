# 生命周期事件（Application Lifecycle Events）

> **类型**：机制（Mechanism）

## 一句话
生命周期事件是 Spring Boot 启动全过程的**时间轴锚点**，涵盖了从 Run 开始、环境准备、容器刷新到最终就绪的所有关键节点。

## 1. 事件体系分类（Taxonomy）
> **静态视角**：根据继承关系，启动过程中的事件分为两大阵营。

### A. Boot 启动事件（Bootstrap）
- **基类**：`SpringApplicationEvent`
- **来源**：由 `SpringApplication`（通过 `RunListener`）发布。
- **特征**：携带 `String[] args` 和 `SpringApplication` 实例；贯穿 Context 创建前后的全过程。

### B. Spring 标准事件（Standard）
- **基类**：`ApplicationContextEvent`
- **来源**：由 `ApplicationContext` 发布。
- **特征**：仅在 Context 创建并 refresh 完成后触发；关注容器内部状态（如 Refreshed, Closed）。

---

## 2. 完整执行时序（Timeline）
> **动态视角**：按 `run()` 方法的时间轴，Boot 事件与标准事件是**穿插执行**的。

| 序号 | 阶段 | 事件类型 | 归属体系 | 关键状态 |
| :--- | :--- | :--- | :--- | :--- |
| **1** | Run 开始 | `ApplicationStartingEvent` | **Boot** | 最早钩子，Environment/Context 均未创建。 |
| **2** | 环境准备 | `ApplicationEnvironmentPreparedEvent` | **Boot** | Environment 已加载，配置文件已读取。 |
| **3** | 上下文预备 | `ApplicationContextInitializedEvent` | **Boot** | Context 对象已创建，`Initializers` 已执行。 |
| **4** | 加载完成 | `ApplicationPreparedEvent` | **Boot** | BeanDefinition 已加载（Class 已读入），**Refresh 之前**。 |
| **5** | **容器刷新** | `ContextRefreshedEvent` | **Standard** | **核心分界点**：所有 Bean 单例实例化完成。 |
| **6** | 启动完成 | `ApplicationStartedEvent` | **Boot** | Refresh 结束，`CommandLineRunner` 执行前。 |
| **7** | 服务就绪 | `ApplicationReadyEvent` | **Boot** | 所有 Runner 执行完毕，应用完全可用。 |

---

## 3. 详细定义与接口

### 严格定义
启动事件流是一组实现了 `ApplicationEvent` 接口的对象序列，通过 `SpringApplicationRunListener`（Boot 事件）或 `ApplicationContext`（标准事件）进行广播。所有 Boot 体系事件均继承自 `org.springframework.boot.context.event.SpringApplicationEvent`。

### 发布链路（Publisher → Multicaster → Listener）
| 事件 | 发布入口（Boot 2.3.x） | 分发器 |
| :--- | :--- | :--- |
| `ApplicationStartingEvent` | `EventPublishingRunListener.starting()` | `initialMulticaster`（`SimpleApplicationEventMulticaster`） |
| `ApplicationEnvironmentPreparedEvent` | `EventPublishingRunListener.environmentPrepared(...)` | `initialMulticaster` |
| `ApplicationContextInitializedEvent` | `EventPublishingRunListener.contextPrepared(...)` | `initialMulticaster` |
| `ApplicationPreparedEvent` | `EventPublishingRunListener.contextLoaded(...)` | `initialMulticaster` |
| `ContextRefreshedEvent` | `AbstractApplicationContext.refresh()` | `applicationEventMulticaster`（Context 内部） |
| `ApplicationStartedEvent` | `EventPublishingRunListener.started(context)` | `applicationEventMulticaster`（通过 `context.publishEvent(...)`） |
| `ApplicationReadyEvent` | `EventPublishingRunListener.running(context)` | `applicationEventMulticaster`（通过 `context.publishEvent(...)`） |
| `ApplicationFailedEvent` | `EventPublishingRunListener.failed(context, ex)` | `applicationEventMulticaster` 或 `initialMulticaster`（依赖 context 状态） |

### 接口：数据 + 约束
- **数据**：
  - 载荷：当前阶段可用的核心对象（Environment, Context 等）。
- **约束**：
  - 监听器的执行顺序遵循 `@Order` 或 `Ordered` 接口。
  - **早期限制**：在 `Context` 创建前的事件（如 Starting, EnvPrepared），无法注入 Bean，只能通过 `spring.factories` 注册的监听器捕获。
  - 分发为触发式（push）：`publishEvent(...)` 触发一次 `multicastEvent(...)`；默认同步调用，异步取决于多播器实现与其 `Executor` 配置（见 [ApplicationEventMulticaster.md](ApplicationEventMulticaster.md)、[SimpleApplicationEventMulticaster.md](SimpleApplicationEventMulticaster.md)）。

## 4. 常用构造/操作
- **监听器接口**：`ApplicationListener<E>`
- **注册方式**：
  - 早期事件：必须在 `META-INF/spring.factories` 注册（因为 Context 还没醒）。
  - 晚期事件：可以是容器里的 `@Component` Bean。

## 5. 关系：上级/下级/等价/特例/推广
- **上级**：观察者模式（Observer Pattern）。
- **包含**：`SpringApplicationEvent`（Boot 系）、`ApplicationContextEvent`（Standard 系）。
- **消费方**：`SpringApplication`（发布者）、扩展点体系（监听者）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → 生命周期事件 → （Boot系 / Standard系）。
