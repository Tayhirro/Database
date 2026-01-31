---
type: mechanism
tags:
  - springboot/web
  - tomcat
  - threading
---

# TomcatThreadingAndExecutors（Tomcat 的线程与执行器模型）

> **类型**：机制（Mechanism）

## 一句话
Tomcat 的线程与执行器模型描述了在连接器启动后，Tomcat 如何通过 endpoint/协议处理器与 executor 组织端口监听、连接轮询与请求处理，并以非守护线程等运行态执行单元使进程保持可服务状态。

## 严格定义
在 Tomcat（以 NIO/NIO2 等实现为例）中，`Connector` 将连接处理委托给 `ProtocolHandler`；`AbstractProtocol.start()` 调用 `AbstractEndpoint.start()` 启动网络端点，并由端点创建/使用执行器（executor）与若干专用线程以完成 accept、poll、以及将请求处理任务投递到工作线程池等职责。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `Connector`（连接器）
  - `ProtocolHandler` / `AbstractProtocol`（协议处理器）
  - `AbstractEndpoint`（网络端点）
  - `Executor`（工作线程池的抽象）
- 约束：
  - 线程命名、数量与是否共享 executor 取决于具体协议与配置；本页只描述“角色与职责边界”，不将某个实现细节固定为唯一形态。

## 角色划分（按职责）

### A. 端口监听与连接接入（accept）
`AbstractEndpoint` 在启动后需要将“新连接接入”推进到协议栈内部；该过程通常由专用线程执行（acceptor 角色）。

### B. I/O 事件轮询与分发（poll/select）
在 NIO 模型下，连接的读写就绪事件需要轮询；该过程通常由 poller 角色承担，并把就绪连接交给后续处理阶段。

### C. 请求处理工作线程（worker / executor）
请求处理（解析、过滤链、Servlet 调用等）通常在工作线程中执行，工作线程来源通常为 `Executor`（例如 ThreadPoolExecutor 形态的实现）。`ProtocolHandler`/endpoint 会把任务投递到该 executor 中运行。

### D. 监控与辅助任务（scheduled/utility）
`AbstractProtocol.start()` 会创建/使用 `ScheduledExecutorService` 来调度周期性任务（例如协议监控/维护任务），这类任务属于运行态辅助执行单元。

## 与 Boot 的边界
- Boot 的 `TomcatWebServer.start()` 负责触发 `Tomcat.start()` 并校验连接器进入 started 状态（见 [TomcatWebServerStartStop.md](TomcatWebServerStartStop.md)）。
- Tomcat 的线程创建与 executor 组织属于底层容器实现；Boot 通过 `WebServer` 抽象表达生命周期边界，但不在 `WebServer` 接口层规定线程模型（见 [../../../interface/WebServer.md](../../../interface/WebServer.md)）。

## 关系：上级/下级/等价/特例/推广
- 上级：WebServer 生命周期与线程保活（见 [../../../mechanism/WebServerLifecycleAndThreads.md](../../../mechanism/WebServerLifecycleAndThreads.md)）。
- 相关：`TomcatWebServer`（Boot 适配层，见 [../../../class/TomcatWebServer.md](../../../class/TomcatWebServer.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → mechanism → TomcatThreadingAndExecutors。

