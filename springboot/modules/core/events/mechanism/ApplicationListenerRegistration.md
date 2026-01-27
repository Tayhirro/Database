# ApplicationListenerRegistration（监听器注册与装配）

> **类型**：机制（Mechanism）

## 一句话
监听器注册与装配是将不同来源的 `ApplicationListener`（对象或 beanName）写入 `ApplicationEventMulticaster` 的注册表，使其在一次 `publishEvent/multicastEvent` 中可被匹配与回调的一组过程。

## 严格定义
给定一个 `ApplicationEventMulticaster` 与一组监听器来源（编程式添加的 listener 对象、容器内 `ApplicationListener` 类型的 bean、以及 Boot 启动期维护的 listeners 集合），监听器注册与装配是指在确定的时点（例如 `AbstractApplicationContext.refresh()` 的 `registerListeners()` 步骤，或 `ConfigurableApplicationContext.addApplicationListener(...)` 被调用时）执行注册操作：
$$
\texttt{addApplicationListener(listener)} \quad \text{或} \quad \texttt{addApplicationListenerBean(beanName)},
$$
从而使事件发布时的监听器匹配集合 $\mathcal{L}(event, eventType)$ 可从多播器注册表中计算得到。

## 交互面：数据 + 约束
- 数据（语义级别）：
  - 监听器来源：
    - listener 对象：由 `addApplicationListener(listener)` 直接注册
    - listener beanName：由 `addApplicationListenerBean(beanName)` 注册（分发时可再解析实例）
  - 注册表与匹配：由 `AbstractApplicationEventMulticaster` 维护（见 [../class/AbstractApplicationEventMulticaster.md](../class/AbstractApplicationEventMulticaster.md)）
- 输入：
  - listener 对象 / beanName
  - 触发点（时点）：启动阶段方法调用或 `refresh()` 内部步骤
- 输出：
  - 多播器注册表的更新（副作用）
- 约束：
  - 注册发生在“有多播器实例可写入”的前提下；在 `refresh()` 内部，多播器通常在 `initApplicationEventMulticaster()` 之后可用。
  - listener 以 beanName 形式注册时，事件匹配与实例解析依赖 `BeanFactory` 的类型信息与实例化时机（实现细节在 Framework 层）。

## 常用构造/操作（仅列出接口与符号）
### 阶段化模型（发现 → 登记 → 匹配 → 调用）
- 发现：确定候选监听器来自何处（listener 对象集合 / listener beanName 集合）。
- 登记：将候选写入多播器注册表（`addApplicationListener*`）。
- 匹配：给定 `(event, eventType)` 计算匹配集合 $\mathcal{L}(event, eventType)$（由 `AbstractApplicationEventMulticaster` 提供）。
- 调用：遍历匹配集合并回调 `onApplicationEvent(event)`（由 `SimpleApplicationEventMulticaster` 负责执行策略）。

| 阶段（Boot 2.3.x / Framework） | 来源 | 注册入口 | 写入目标 |
| :--- | :--- | :--- | :--- |
| Boot 早期事件分发 | `SpringApplication.getListeners()` | `initialMulticaster.addApplicationListener(listener)` | `initialMulticaster`（`SimpleApplicationEventMulticaster`） |
| Boot 将 listeners 关联到 Context | `SpringApplication.getListeners()` | `context.addApplicationListener(listener)` | Context 的 listener 集合（并可能同步写入多播器） |
| Context refresh：注册监听器 | Context 的 listener 集合 | `applicationEventMulticaster.addApplicationListener(listener)` | `applicationEventMulticaster` |
| Context refresh：注册监听器 Bean | `ApplicationListener` 类型 beanName 集合 | `applicationEventMulticaster.addApplicationListenerBean(beanName)` | `applicationEventMulticaster` |

## 关系：上级/下级/等价/特例/推广
- 上级：观察者模式（Observer Pattern）：见 [../../../patterns/pattern/ObserverPattern.md](../../../patterns/pattern/ObserverPattern.md)
- 关联：
  - `ApplicationListener`（见 [../interface/ApplicationListener.md](../interface/ApplicationListener.md)）
  - `ApplicationEventMulticaster`（见 [../interface/ApplicationEventMulticaster.md](../interface/ApplicationEventMulticaster.md)）
  - `AbstractApplicationEventMulticaster`（注册表与匹配，见 [../class/AbstractApplicationEventMulticaster.md](../class/AbstractApplicationEventMulticaster.md)）
  - `SimpleApplicationEventMulticaster`（分发执行，见 [../class/SimpleApplicationEventMulticaster.md](../class/SimpleApplicationEventMulticaster.md)）
  - Boot 启动事件桥接：`EventPublishingRunListener`（见 [../../bootstrap/class/EventPublishingRunListener.md](../../bootstrap/class/EventPublishingRunListener.md)）

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → events → ApplicationListenerRegistration →（Listener Sources / Registration Timing / Multicaster Registry）。
