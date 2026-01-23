# 生命周期事件（Application Lifecycle Events）

## 一句话
生命周期事件是对启动过程阶段边界的事件化表达，允许监听器在特定阶段接入并执行观察或变换逻辑。

## 严格定义
对一次 `SpringApplication.run(...)`，可以定义一组有序阶段 $\{P_i\}$（例如 starting/environmentPrepared/contextPrepared/started/ready/failed 等），并在阶段边界发布事件；监听器订阅事件类型并在事件发生时被调用。

Spring Boot 中事件通常以 `ApplicationEvent` 及其子类型表示，并通过 `ApplicationListener` 接收。

## 接口：数据 + 约束
- 数据：
  - 事件（event）类型与载荷（例如 `Environment`、`ApplicationContext`、异常等）
  - 监听器（listener）集合
- 输出：
  - 监听器被触发的调用序列
- 约束：
  - 具体事件类型集合与触发点与 Boot 版本相关；本页将其视为“阶段边界事件模型”。

## 常用构造/操作（仅列出接口与符号）
- 监听器接口：`ApplicationListener`
- 启动过程中的发布点：见 [springboot/flows/启动流程.md](../flows/启动流程.md)

## 关系：上级/下级/等价/特例/推广
- 上级：事件驱动模型（event-driven）。
- 相关：扩展点发现（监听器来源）、`SpringApplication`（发布者）、`ApplicationContext`（Spring 事件系统）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → 生命周期事件 → 启动流程。

