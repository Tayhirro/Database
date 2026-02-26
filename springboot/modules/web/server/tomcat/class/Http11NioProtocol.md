---
title: Http11NioProtocol（Tomcat HTTP/1.1 NIO 协议处理器）
date: "2026-02-02"
categories:
  - springboot
tags:
  - springboot/web
  - tomcat
  - http
  - nio
description: 类型：类（Class）
type: class
---
# Http11NioProtocol（Tomcat HTTP/1.1 NIO 协议处理器）

> **类型**：类（Class）

## 一句话
`Http11NioProtocol` 是 Tomcat 在 HTTP/1.1 + NIO 场景下的 `ProtocolHandler` 典型实现：组合 NIO 端点与 HTTP/1.1 `Processor`，并以适配器把请求推进到 Servlet 容器链路。

## 严格定义
在 Tomcat 的常见实现中，HTTP/1.1（NIO）协议处理器以 `Http11NioProtocol`（类名与继承层次随版本可能不同）出现：它作为 `ProtocolHandler` 被 `Connector` 持有；在启动阶段启动 NIO 端点（如 `NioEndpoint`）；在运行态由端点的连接事件分发触发 `Processor`（如 `Http11Processor`）进行 HTTP 协议解析，并通过 `CoyoteAdapter` 将 Coyote 请求/响应适配为 Catalina/Servlet 处理链路输入输出。

## 继承链（接口链 / 实现链）
- 上级（语义链）：`ProtocolHandler`（见 [../interface/ProtocolHandler.md](../interface/ProtocolHandler.md)）→ `AbstractProtocol`（见 [AbstractProtocol.md](AbstractProtocol.md)）→ `Http11NioProtocol`（实现级）。

## 接口：数据 + 约束
- 数据（语义级别；字段名可能随 Tomcat 版本变化）：
  - `endpoint`：NIO 端点（acceptor/poller/executor 的组织点，见 [AbstractEndpoint.md](AbstractEndpoint.md)）
  - `processor`/processor cache：HTTP/1.1 解析器实例的创建与复用（见 [Http11Processor.md](Http11Processor.md)）
  - `adapter`：Coyote→Catalina 适配器（见 [CoyoteAdapter.md](CoyoteAdapter.md)）
- 约束：
  - `Connector` 负责端口入口与生命周期委派；协议解析与 I/O 分发组织发生在 `ProtocolHandler` 及其端点/处理器组合中（见 [../mechanism/TomcatComponentModel.md](../mechanism/TomcatComponentModel.md)）。

## 常用构造/操作（仅列出接口与符号）
- 生命周期：`start()` / `stop()`（从 `ProtocolHandler` 继承的语义）

## 关系：上级/下级/等价/特例/推广
- 上级：`Connector`（持有并调用其生命周期，见 [Connector.md](Connector.md)）。
- 相关：Tomcat 请求对象映射（见 [../mechanism/TomcatRequestObjectMapping.md](../mechanism/TomcatRequestObjectMapping.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → Http11NioProtocol。

