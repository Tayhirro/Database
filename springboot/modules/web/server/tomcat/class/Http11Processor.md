---
type: class
tags:
  - springboot/web
  - tomcat
  - http
  - coyote
---

# Http11Processor（Tomcat HTTP/1.1 协议解析处理器）

> **类型**：类（Class）

## 一句话
`Http11Processor` 是 Tomcat 在 HTTP/1.1 场景下的 `Processor` 典型实现：把连接上的字节流解析为 Coyote 请求/响应对象，并与适配器协作将请求推进到 Servlet 容器链路。

## 严格定义
在 Tomcat 的 HTTP/1.1 协议实现中，`Http11Processor`（类名与包名随版本可能不同）实现 `org.apache.coyote.Processor`；其职责是对单条连接上的 I/O 就绪事件进行协议解析与处理：解析请求行、请求头与请求体，将解析结果写入 `org.apache.coyote.Request`；在需要进入应用处理链路时，调用适配器把 Coyote 请求适配为 `javax.servlet.ServletRequest` 视图并触发容器处理；并在 keep-alive、分块传输、异常与超时等协议细节上维护连接级状态。

## 继承链（接口链 / 实现链）
- 实现接口：`Processor`（见 [../interface/Processor.md](../interface/Processor.md)）。
- 上层组织：由 `ProtocolHandler` 组织其创建/复用并与 `Endpoint` 的事件分发连接（见 [AbstractProtocol.md](AbstractProtocol.md)）。

## 接口：数据 + 约束
- 数据（语义级别；字段名可能随 Tomcat 版本变化）：
  - `request: org.apache.coyote.Request`（Coyote 请求对象，见 [CoyoteRequest.md](CoyoteRequest.md)）
  - `response: org.apache.coyote.Response`（Coyote 响应对象）
  - `inputBuffer/outputBuffer`（协议层输入/输出缓冲，承载原始字节与编码后的响应字节）
  - `socketWrapper`（连接包装对象：对底层 socket/channel 的读写抽象）
  - keep-alive 与错误状态（例如 `keepAlive/errorState` 等语义字段）
  - `adapter: org.apache.catalina.connector.CoyoteAdapter`（请求适配器，见 [CoyoteAdapter.md](CoyoteAdapter.md)）
- 输入：
  - 连接上的 I/O 就绪事件（由 `Endpoint`/Poller 触发）
- 输出：
  - Coyote 请求对象的填充与状态迁移
  - 触发适配与容器处理（副作用）
- 约束：
  - `Http11Processor` 不负责端口监听与 I/O 多路复用；它消费由 `Endpoint` 产生的就绪事件与连接读写抽象。

## 常用构造/操作（仅列出接口与符号）
- 处理：`process(...)`
- 复用：`recycle(...)`（存在性依实现变化）

## 关系：上级/下级/等价/特例/推广
- 上级：`AbstractProtocol`（典型通过“连接处理器/缓存”组织 `Processor` 的创建与复用，见 [AbstractProtocol.md](AbstractProtocol.md)）。
- 相关：`CoyoteAdapter`（见 [CoyoteAdapter.md](CoyoteAdapter.md)）。
- 相关：Tomcat 请求对象映射（见 [../mechanism/TomcatRequestObjectMapping.md](../mechanism/TomcatRequestObjectMapping.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → Http11Processor。

