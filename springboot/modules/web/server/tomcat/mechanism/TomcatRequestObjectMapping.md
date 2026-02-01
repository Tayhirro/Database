---
type: mechanism
tags:
  - springboot/web
  - tomcat
  - http
  - request
---

# TomcatRequestObjectMapping（字节流→CoyoteRequest→ServletRequest）

> **类型**：机制（Mechanism）

## 一句话
TomcatRequestObjectMapping 描述了 Tomcat 在一次 HTTP 请求处理中如何把连接上的字节流解析为 `org.apache.coyote.Request`，再适配为 `org.apache.catalina.connector.Request`（Servlet 请求视图）并推进到容器与框架处理链路。

## 严格定义
在 Tomcat 的 HTTP/1.1（NIO）场景下，网络端点负责接入连接与 I/O 就绪事件轮询；当连接可读时，`Processor`（如 `Http11Processor`）从连接读取字节并按 HTTP 协议解析请求行/头部/请求体，将解析结果写入 `org.apache.coyote.Request`；随后 `CoyoteAdapter` 将 Coyote 请求/响应适配为 Catalina 侧的 `org.apache.catalina.connector.Request/Response`，并把处理推进到容器管线与最终的 `Servlet.service(...)` 调用。

## 接口：数据 + 约束
- 数据（语义级别）：
  - 连接字节流（readable bytes）
  - `CoyoteRequest`：`org.apache.coyote.Request`（见 [../class/CoyoteRequest.md](../class/CoyoteRequest.md)）
  - `CatalinaRequest`：`org.apache.catalina.connector.Request`（见 [../class/CatalinaRequest.md](../class/CatalinaRequest.md)）
- 约束：
  - “字节→字符串”的字符集解码通常发生在更上层（例如 Spring MVC 的 `HttpMessageConverter`），而不是在 Coyote/Catalina 请求对象本身完成。

## 常用构造/操作（仅列出接口与符号）
- 解析：`Processor.process(...)`（见 [../interface/Processor.md](../interface/Processor.md)）
- 适配：`CoyoteAdapter.service(...)`（见 [../class/CoyoteAdapter.md](../class/CoyoteAdapter.md)）

## 关系：上级/下级/等价/特例/推广
- 上游：`AbstractEndpoint`（I/O 事件来源，见 [../class/AbstractEndpoint.md](../class/AbstractEndpoint.md)）。
- 上游：`Http11Processor`（HTTP/1.1 解析器，见 [../class/Http11Processor.md](../class/Http11Processor.md)）。
- 下游：Servlet 容器路由与 servlet 调用（见 [TomcatComponentModel.md](TomcatComponentModel.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → mechanism → TomcatRequestObjectMapping。

