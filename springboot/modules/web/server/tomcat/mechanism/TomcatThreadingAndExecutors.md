---
title: TomcatThreadingAndExecutors（Tomcat 的线程与执行器模型）
date: "2026-01-31"
categories:
  - springboot
tags:
  - springboot/web
  - tomcat
  - threading
description: 类型：机制（Mechanism）
type: mechanism
---
# TomcatThreadingAndExecutors（Tomcat 的线程与执行器模型）

> **类型**：机制（Mechanism）

## 一句话
Tomcat 的线程与执行器模型描述了在连接器启动后，Tomcat 如何通过 endpoint/协议处理器与 executor 组织端口监听、连接轮询与请求处理，并以非守护线程等运行态执行单元使进程保持可服务状态。

## 严格定义
在 Tomcat（以 NIO/NIO2 等实现为例）中，`Connector` 将连接处理委托给 `ProtocolHandler`；`AbstractProtocol.start()` 调用 `AbstractEndpoint.start()` 启动网络端点，并由端点创建/使用执行器（executor）与若干专用线程以完成 accept、poll、以及将请求处理任务投递到工作线程池等职责。

## 前置概念（名词对齐）
- Tomcat 组件模型：见 [TomcatComponentModel.md](TomcatComponentModel.md)
- `Connector`：见 [../class/Connector.md](../class/Connector.md)
- `ProtocolHandler`：见 [../interface/ProtocolHandler.md](../interface/ProtocolHandler.md)
- `AbstractProtocol`：见 [../class/AbstractProtocol.md](../class/AbstractProtocol.md)
- `AbstractEndpoint`：见 [../class/AbstractEndpoint.md](../class/AbstractEndpoint.md)

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

## 端点视角（示例）
- 以 HTTP/1.1 NIO 端点为例的“启动链路/线程三件套/限流分层/Boot 配置映射”：见 [../class/AbstractEndpoint.md](../class/AbstractEndpoint.md)。
- 以 HTTP/1.1 NIO 为例的“字节流→CoyoteRequest→ServletRequest”对象映射：见 [TomcatRequestObjectMapping.md](TomcatRequestObjectMapping.md)。

## 线程属性（daemon/namePrefix/priority）

### 守护线程（daemon thread）
在 JVM 线程模型中，守护线程是“不会阻止 JVM 进程退出”的线程：当进程中只剩守护线程时，JVM 可以结束。Tomcat 的对外服务能力依赖若干运行态线程持续存在（accept/poll/worker 等）；这些线程是否为守护线程将影响“进程退出条件”的成立方式。

### Tomcat 中的控制点（语义级别）
- 线程是否为 daemon、线程名的前缀、线程优先级等属性，通常由创建线程的 `ThreadFactory` 决定。
- 当 endpoint 使用外部注入的 `Executor` 时，上述属性通常由该 executor 的线程工厂决定（例如其内部 `ThreadPoolExecutor` 的 `ThreadFactory`）。
- 当 endpoint 创建内部线程池时，上述属性通常由 endpoint 侧的线程工厂参数决定（例如 namePrefix/daemon/priority 之类的配置项；字段名随版本可能不同）。

## 与 Boot 的边界
- Boot 的 `TomcatWebServer.start()` 负责触发 `Tomcat.start()` 并校验连接器进入 started 状态（见 [TomcatWebServerStartStop.md](TomcatWebServerStartStop.md)）。
- Tomcat 的线程创建与 executor 组织属于底层容器实现；Boot 通过 `WebServer` 抽象表达生命周期边界，但不在 `WebServer` 接口层规定线程模型（见 [../../../interface/WebServer.md](../../../interface/WebServer.md)）。

## 关系：上级/下级/等价/特例/推广
- 上级：WebServer 生命周期与线程保活（见 [../../../mechanism/WebServerLifecycleAndThreads.md](../../../mechanism/WebServerLifecycleAndThreads.md)）。
- 相关：`TomcatWebServer`（Boot 适配层，见 [../../../class/TomcatWebServer.md](../../../class/TomcatWebServer.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → mechanism → TomcatThreadingAndExecutors。
