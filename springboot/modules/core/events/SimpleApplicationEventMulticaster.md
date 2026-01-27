# SimpleApplicationEventMulticaster（默认事件多播器实现）

> **类型**：类（Class）

## 一句话
`SimpleApplicationEventMulticaster` 是 `ApplicationEventMulticaster` 的通用实现：在一次 `multicastEvent(...)` 调用中遍历匹配的监听器并触发回调，默认以同步方式执行，可通过 `Executor` 使分发异步化。

## 严格定义
在 Spring Framework 中，`org.springframework.context.event.SimpleApplicationEventMulticaster` 继承 `AbstractApplicationEventMulticaster`，并以 `taskExecutor`（可空）与 `errorHandler`（可空）作为可配置策略，决定监听器调用的并发与异常处理方式。它同时被 `ApplicationContext` 的默认事件系统与 Spring Boot 的启动早期事件发布逻辑复用。

## 接口：数据 + 约束
- 数据：
  - `taskExecutor: Executor | null`（为空时在调用线程执行）
  - `errorHandler: ErrorHandler | null`（为空时异常向上抛出；存在时由 handler 处理）
- 输入：
  - `ApplicationEvent event`
  - 可选：`ResolvableType eventType`
- 输出：
  - 无返回值（副作用为调用监听器）
- 约束：
  - 同步/异步是实现策略：当 `taskExecutor != null` 时，监听器调用可通过 `Executor.execute(...)` 触发。
  - 监听器筛选与排序依赖父类的监听器检索逻辑（事件类型匹配、`Ordered/@Order` 等）。

## 常用构造/操作（仅列出接口与符号）
- 构造：`new SimpleApplicationEventMulticaster()` / `new SimpleApplicationEventMulticaster(BeanFactory)`
- 策略配置：`setTaskExecutor(executor)` / `setErrorHandler(errorHandler)`
- 分发：`multicastEvent(event[, eventType])`

## 关系：上级/下级/等价/特例/推广
- 实现：`ApplicationEventMulticaster`（见 [ApplicationEventMulticaster.md](ApplicationEventMulticaster.md)）。
- 被使用：
  - Spring Framework：`AbstractApplicationContext` 缺省的 `applicationEventMulticaster`（若容器中未显式声明同名 Bean）。
  - Spring Boot：`EventPublishingRunListener` 的 `initialMulticaster`（启动早期事件分发，见 [../bootstrap/EventPublishingRunListener.md](../bootstrap/EventPublishingRunListener.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → events → SimpleApplicationEventMulticaster →（Default Multicaster / Executor / ErrorHandler）。

