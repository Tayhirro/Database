---
type: class
tags:
  - springboot/web
  - tomcat
---

# AbstractProtocol（Tomcat 协议处理器抽象基类）

> **类型**：类（Class）

## 一句话
`AbstractProtocol` 是 Tomcat `ProtocolHandler` 的抽象基类：提供通用的启动流程骨架，并在 `start()` 时调用其 `endpoint.start()` 以启动网络端点与相关运行态执行单元。

## 严格定义
在 Tomcat 中，`org.apache.coyote.AbstractProtocol` 实现 `ProtocolHandler`；其 `start()` 的典型结构包含：调用 `endpoint.start()` 启动端点，并调度/创建若干协议相关的辅助任务（例如通过 `ScheduledExecutorService` 调度周期性任务）。协议栈的具体行为由 `endpoint` 具体实现与子类策略决定。

## 继承链（接口链 / 实现链）
- 继承链：`java.lang.Object` → `AbstractProtocol` →（具体协议实现）。
- 实现接口：`ProtocolHandler`（见 [../interface/ProtocolHandler.md](../interface/ProtocolHandler.md)）。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `endpoint: AbstractEndpoint`（网络端点）
  - `utilityExecutor: ScheduledExecutorService`（辅助任务调度器）
- 约束：
  - endpoint 的类型决定了 I/O 模型与线程组织方式；`AbstractProtocol` 只提供启动骨架与协议级别的组织点。

## 常用构造/操作（仅列出接口与符号）
- `start()`：触发 `endpoint.start()` 并启动/调度辅助任务
- `stop()`：停止端点与相关任务（边界由实现定义）

## 关系：上级/下级/等价/特例/推广
- 上级：`ProtocolHandler`（见 [../interface/ProtocolHandler.md](../interface/ProtocolHandler.md)）。
- 下级：`AbstractEndpoint`（见 [AbstractEndpoint.md](AbstractEndpoint.md)）。
- 相关：Tomcat 线程与执行器模型（见 [../mechanism/TomcatThreadingAndExecutors.md](../mechanism/TomcatThreadingAndExecutors.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → AbstractProtocol。

