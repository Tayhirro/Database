---
type: interface
tags:
  - springboot/web
  - tomcat
  - coyote
---

# Processor（Tomcat Coyote 处理器接口）

> **类型**：接口（Interface）

## 一句话
`Processor` 是 Tomcat Coyote 层用于抽象“对单条连接上的 I/O 就绪事件进行协议解析与处理”的接口，其典型实现负责把字节流解析为 `org.apache.coyote.Request` 并推动后续适配与容器处理链路。

## 严格定义
在 Tomcat 的 Coyote 协议栈中，`org.apache.coyote.Processor` 表达了对连接事件的处理能力：当 `Endpoint` 侧检测到某连接可读/可写等就绪事件时，会将该连接的包装对象（语义级：socket wrapper）交给 `Processor` 处理；处理过程中，`Processor` 按协议（如 HTTP/1.1）解析请求行/请求头/请求体等，并与上层适配器协作将请求推进到 Servlet 容器处理链路。该接口的实例通常具有“可复用”的生命周期（recycle/reuse），以降低对象创建开销。

## 继承链（接口链 / 实现链）
- 接口链：`Processor`（无上级接口）。
- 常见实现：HTTP/1.1 `Http11Processor`（见 [../class/Http11Processor.md](../class/Http11Processor.md)）。

## 接口：数据 + 约束
- 数据（语义级别）：
  - 输入来源：`Endpoint` 侧的连接包装对象（socket wrapper 语义）
  - 协议解析状态：keep-alive、错误状态、协议状态机等（字段名随实现变化）
  - 请求/响应对象：`org.apache.coyote.Request` / `org.apache.coyote.Response`（见 [../class/CoyoteRequest.md](../class/CoyoteRequest.md)）
- 输入：
  - 连接上的 I/O 事件（可读/可写/超时/关闭等语义事件）
- 输出：
  - 对请求处理链路的推进（副作用）：解析请求、驱动适配、可能写回响应
- 约束：
  - `Processor` 不负责监听端口与 I/O 轮询；端口监听与轮询由 `Endpoint`（acceptor/poller）承担（见 [../class/AbstractEndpoint.md](../class/AbstractEndpoint.md) 与 [../class/threading/Poller.md](../class/threading/Poller.md)）。

## 常用构造/操作（仅列出接口与符号）
- 处理连接事件：`process(...)`（方法签名依版本/实现变化）
- 复用：`recycle(...)`（存在性依实现变化）

## 关系：上级/下级/等价/特例/推广
- 上级：`ProtocolHandler`（由协议处理器组织处理器实例与生命周期，见 [ProtocolHandler.md](ProtocolHandler.md)）。
- 相关：`CoyoteAdapter`（将 Coyote 请求适配为 Servlet 请求并进入容器处理链路，见 [../class/CoyoteAdapter.md](../class/CoyoteAdapter.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → interface → Processor。

