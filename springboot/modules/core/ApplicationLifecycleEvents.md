# 生命周期事件（Application Lifecycle Events）

> **类型**：机制（Mechanism）

## 一句话
生命周期事件是对启动过程阶段边界的事件化表达，允许监听器在特定阶段接入并执行观察或变换逻辑。

## 严格定义
对一次 `SpringApplication.run(...)`，可以定义一组有序阶段 $\{P_i\}$（例如 starting/environmentPrepared/contextPrepared/started/ready/failed 等），并在阶段边界发布事件；监听器订阅事件类型并在事件发生时被调用。

所有 Boot 启动事件均继承自抽象基类 `SpringApplicationEvent`（它是 `ApplicationEvent` 的子类），并通过 `ApplicationListener` 接收。它们按确定的时序依次触发，彼此互为“兄弟”关系。

## 接口：数据 + 约束
- 数据：
  - 事件（event）类型：均继承自 `SpringApplicationEvent`（归属于 `org.springframework.boot.context.event` 包）。
  - 载荷：`SpringApplication` 实例、`args`、以及阶段相关的 `Environment` 或 `ApplicationContext`。
  - 监听器（listener）集合
- 输出：
  - 监听器被触发的调用序列
- 约束：
  - 具体事件类型集合与触发点与 Boot 版本相关；本页将其视为“阶段边界事件模型”。

## 常用构造/操作（仅列出接口与符号）
- 监听器接口：`ApplicationListener`
- 启动过程中的发布点：见 [springboot/flows/启动流程.md](../../flows/启动流程.md)

## 事件时序清单（Key Events Sequence）
按 `SpringApplication.run()` 执行顺序触发：

1. **`ApplicationStartingEvent`**
   - **时机**：Run 一开始就发（最早）。
   - **状态**：除了注册 listeners/initializers 外，上下文与环境均未准备好。
   - **作用**：极早期钩子（如日志系统初始化）。

2. **`ApplicationEnvironmentPreparedEvent`**
   - **时机**：`Environment` 已准备好，但 `ApplicationContext` 还没创建。
   - **状态**：可以读取/修改配置（EnvironmentPostProcessor 在此阶段生效）。

3. **`ApplicationContextInitializedEvent`**
   - **时机**：Context 已准备好且 Initializers 已执行，但 BeanDefinition 还没加载。
   - **状态**：Context 结构已定，但 Bean 还没进场。

## 辨析：启动事件 vs 标准事件
- **Boot 启动事件**：继承自 `SpringApplicationEvent`，由 `SpringApplication` 调度，贯穿启动全流程（含 Context 创建前）。
- **标准 Context 事件**：继承自 `ApplicationContextEvent`（如 `ContextRefreshedEvent`），由 `ApplicationContext` 调度，仅在 Context 建立后发生。
> **注意**：启动完成后（`ApplicationReadyEvent` 之后），应用运行期间发布的通常是标准事件或业务自定义事件，**不再**继承自 `SpringApplicationEvent`。

## 关系：上级/下级/等价/特例/推广
- 上级：事件驱动模型（event-driven）。
- 相关：扩展点发现（监听器来源）、`SpringApplication`（发布者）、`ApplicationContext`（Spring 事件系统）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → 生命周期事件 → 启动流程。
