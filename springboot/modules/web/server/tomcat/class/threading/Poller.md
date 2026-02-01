---
type: concept
tags:
  - springboot/web
  - tomcat
  - threading
  - nio
---

# Poller（Tomcat NIO 轮询线程角色）

## 一句话
Poller 是 Tomcat NIO 端点中负责驱动 I/O 多路复用（select/poll）并把就绪事件分发为可执行任务的运行态线程角色。

## 严格定义
在 NIO 端点实现中，Poller 线程通常持有并驱动一个 `Selector`：将已接入的 `SocketChannel` 注册到 `Selector` 并获得 `SelectionKey`，周期性调用 `select()` 获取就绪的 `SelectionKey` 集合，将“可读/可写等就绪事件”转化为端点内部的处理任务，并将任务投递到端点的 `Executor`（worker 执行器）或等价的任务执行通道，从而把就绪连接推进到请求处理阶段。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `Selector`：Java NIO 多路复用器，用于在一个线程中等待并获取多个通道的 I/O 就绪事件
  - 事件集合：`SelectionKey`（就绪事件的标识载体，语义级）
  - 任务投递入口：`Executor.execute(Runnable)` 或等价机制
- 输入：
  - 已建立连接在读写层面的就绪事件（I/O readiness）
- 输出：
  - 被投递到执行器的处理任务（副作用）
- 约束：
  - Poller 的职责边界在“轮询 + 分发”；请求处理的计算与业务调用由执行器线程承担。

## 常用构造/操作（仅列出接口与符号）
- `Selector.select()`：等待并获取就绪事件（语义级）
- `Executor.execute(task)`：投递任务到 worker 执行器（语义级）

## 关系：上级/下级/等价/特例/推广
- 上级：`AbstractEndpoint`（见 [../AbstractEndpoint.md](../AbstractEndpoint.md)）。
- 并列角色：`Acceptor`（见 [Acceptor.md](Acceptor.md)）、`Executor`（见 [Executor.md](Executor.md)）。
- OS 对齐：epoll 与 Selector 的映射（实现依赖）（见 [../../../../../../../网络/os/epoll（I-O多路复用）.md](../../../../../../../网络/os/epoll（I-O多路复用）.md)、[../../../../../../../网络/java/Selector（JavaNIO多路复用）.md](../../../../../../../网络/java/Selector（JavaNIO多路复用）.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → threading → Poller。
