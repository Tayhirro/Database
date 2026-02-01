---
type: mechanism
tags:
  - springboot/web
  - tomcat
---

# TomcatComponentModel（Tomcat 组件模型：Server/Service/Connector/Protocol/Endpoint）

> **类型**：机制（Mechanism）

## 一句话
Tomcat 组件模型用一组分层组件（`Server`、`Service`、`Connector`、`ProtocolHandler`、`Endpoint` 等）表达“端口监听与连接接入 → 协议处理 → 请求分发到应用”的结构划分，并以各自的生命周期方法实现启动与停止。

## 严格定义
在 Tomcat 中，一个 `Server` 可包含多个 `Service`；每个 `Service` 通常包含一个 `Engine`（Servlet 容器部分）与若干 `Connector`（网络入口）。`Connector` 将网络连接处理委托给 `ProtocolHandler`（协议处理器）；在常见实现中，`ProtocolHandler.start()` 会进一步启动其 `Endpoint`（网络端点）以完成端口监听、连接管理与 I/O 事件处理，并把请求处理任务推进到后续处理链路。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `Server`：`Service` 的聚合容器
  - `Service`：`Engine` + `Connector[]`（以及可选的 `Executor[]`）
  - `Connector`：网络入口与协议适配点
  - `ProtocolHandler`：协议处理器（由 `Connector` 持有/引用）
  - `Endpoint`：端口监听与连接/I/O 管理的执行单元（由 `ProtocolHandler` 持有/创建）
- 约束：
  - 上述组件的“存在关系”用于描述职责边界；具体实现类型会随协议（HTTP/1.1、HTTP/2、AJP）与 I/O 模型（NIO/NIO2/APR）变化。

## 常用构造/操作（仅列出接口与符号）
- `Tomcat.start()`：推进 `Server/Service/Connector/...` 进入 started 状态
- `Connector.startInternal()`：触发 `ProtocolHandler.start()`
- `ProtocolHandler.start()`：启动协议栈与端点
- `Endpoint.start()`：启动端口监听与运行态执行单元

## 关系：上级/下级/等价/特例/推广
- 上级：`WebServer`（Boot 生命周期适配层，见 [../../../interface/WebServer.md](../../../interface/WebServer.md)）。
- 下级（按“组件 → 子组件/委托”）：
  - `Service` → `Connector`（网络入口）
  - `Connector` → `ProtocolHandler`（协议处理委托）
  - `ProtocolHandler` → `Endpoint`（端口与 I/O 管理）
- 下级（按“协议栈内部组成”）：
  - `ProtocolHandler` → `Processor`（协议解析与连接级状态机，见 [../interface/Processor.md](../interface/Processor.md)）
  - `Processor` → `CoyoteRequest`（协议解析后的请求对象，见 [../class/CoyoteRequest.md](../class/CoyoteRequest.md)）
  - `CoyoteAdapter` → `CatalinaRequest`（Servlet 请求视图，见 [../class/CoyoteAdapter.md](../class/CoyoteAdapter.md)、[../class/CatalinaRequest.md](../class/CatalinaRequest.md)）
- 相关页面：
  - `Connector`：见 [../class/Connector.md](../class/Connector.md)
  - `ProtocolHandler`：见 [../interface/ProtocolHandler.md](../interface/ProtocolHandler.md)
  - `AbstractProtocol`：见 [../class/AbstractProtocol.md](../class/AbstractProtocol.md)
  - `AbstractEndpoint`：见 [../class/AbstractEndpoint.md](../class/AbstractEndpoint.md)
  - `Http11NioProtocol`：见 [../class/Http11NioProtocol.md](../class/Http11NioProtocol.md)
  - `Http11Processor`：见 [../class/Http11Processor.md](../class/Http11Processor.md)
  - Tomcat 请求对象映射：见 [TomcatRequestObjectMapping.md](TomcatRequestObjectMapping.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → mechanism → TomcatComponentModel。
