---
type: mechanism
tags:
  - springboot/web
  - netty
  - threading
---

# NettyThreadingAndExecutors（Netty/Reactor Netty 的线程与 event loop 模型）

> **类型**：机制（Mechanism）

## 一句话
Netty/Reactor Netty 的线程与 event loop 模型描述了 server 绑定后如何通过 event loop 线程处理 I/O 与回调链路，并以运行态线程集合维持持续对外服务的执行环境。

## 严格定义
在 Reactor Netty 的 HTTP server 形态下，`HttpServer.bindNow(...)` 返回 `DisposableServer` 作为运行态句柄；server 的 I/O 处理由 Netty 的 event loop 线程执行，并在管道（pipeline）中调用 handler/route 所对应的逻辑。Boot 的 `NettyWebServer` 负责触发生命周期并提供 stop/port 等统一视图，但不在接口层规定 event loop 的线程数量与命名。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `DisposableServer`（运行态句柄）
  - event loop 线程（I/O 与回调执行的承载）
- 约束：
  - 在 `NettyWebServer` 构造阶段，Boot 会为 `HttpServer` 设置 `ChannelGroup`（`DefaultChannelGroup`），以便对已创建 channel 做统一管理；该 `ChannelGroup` 使用 `DefaultEventExecutor` 作为执行器之一。
  - event loop 的具体实现与线程资源生命周期属于 Reactor Netty/Netty 的实现边界；本页只表达角色分工与与 Boot 的接口边界。

## 角色划分（按职责）
### A. Event loop（I/O 线程）
用于处理 accept/read/write 事件并推进 pipeline 回调，是请求处理链路的主要执行载体之一。

### B. Channel 管理执行器（ChannelGroup executor）
用于对 channel 集合的操作（广播关闭等）提供执行上下文；在 Boot 适配层可观察到 `DefaultChannelGroup(new DefaultEventExecutor())` 的构造与注入。

### C. 业务/阻塞任务的外部线程池（可选）
当 handler 内存在阻塞操作时，需要将阻塞任务迁移到独立的阻塞线程池执行；该类线程池不属于 `WebServer` 接口的一部分，而是由应用或框架在更高层定义与注入。

## 与 Boot 的边界
- Boot 的 `NettyWebServer.start()` 负责触发 `bindNow(...)` 并持有 `DisposableServer`，随后 `start()` 返回（见 [NettyWebServerStartStop.md](NettyWebServerStartStop.md)）。
- server 持续运行语义由运行态线程集合维持；当 `stop()` 触发 `disposeNow(...)` 后，相关线程/资源释放完成且 JVM 不再存在非守护线程时进程才会自然结束（见 [../../../mechanism/WebServerLifecycleAndThreads.md](../../../mechanism/WebServerLifecycleAndThreads.md)）。

## 关系：上级/下级/等价/特例/推广
- 上级：WebServer 生命周期与线程保活（见 [../../../mechanism/WebServerLifecycleAndThreads.md](../../../mechanism/WebServerLifecycleAndThreads.md)）。
- 相关：`NettyWebServer`（Boot 适配层，见 [../../../class/NettyWebServer.md](../../../class/NettyWebServer.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → netty → mechanism → NettyThreadingAndExecutors。

