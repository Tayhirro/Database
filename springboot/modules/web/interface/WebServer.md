---
title: WebServer（嵌入式服务器抽象）
date: "2026-01-31"
categories:
  - springboot
description: 类型：接口（Interface）
---
# WebServer（嵌入式服务器抽象）

> **类型**：接口（Interface）

## 一句话
`WebServer` 是 Spring Boot 对“可启动/可停止的对外服务端”抽象出的最小接口，用于统一表示 Servlet 容器或 Reactive 服务器的启动与关闭行为。

## 严格定义
在 Spring Boot 中，`org.springframework.boot.web.server.WebServer` 定义了 `start()`、`stop()` 与 `getPort()`（以及可选的优雅关闭）等方法；Boot 的 Web 类型 `ApplicationContext` 会在 `refresh()` 过程中创建并持有一个 `WebServer` 实例，并在关闭阶段停止该实例。

## 继承链（接口链 / 实现链）
- 接口链：`WebServer`（无上级接口）。
- 常见实现（按应用形态与容器不同）：
  - Tomcat：`TomcatWebServer`（见 [../class/TomcatWebServer.md](../class/TomcatWebServer.md)）
  - Netty（Reactive）：`NettyWebServer`（见 [../class/NettyWebServer.md](../class/NettyWebServer.md)）

## 接口：数据 + 约束
### 方法签名（Boot 2.7.4）
- `start(): void throws WebServerException`
- `stop(): void throws WebServerException`
- `getPort(): int`
- `shutDownGracefully(callback: GracefulShutdownCallback): void`（default method）

### 语义约束（接口层）
- `start()`：使服务端进入“可对外接收请求”的运行态（副作用）；失败以 `WebServerException` 表达。
- `stop()`：使服务端停止对外服务并释放底层资源（副作用）；失败以 `WebServerException` 表达。
- `getPort()`：返回底层容器“当前对外绑定端口”的可观测值；端口的确定时机与是否支持随机端口由具体实现决定。
- `shutDownGracefully(callback)`：请求以“优雅关闭”语义停止服务，并通过 `callback.shutdownComplete(result)` 回传一次结果；是否支持、以及“优雅”的具体边界由实现决定。

### 约束
- `WebServer` 不定义线程模型、请求调度或协议细节；这些由底层容器（Tomcat/Jetty/Undertow/Netty 等）实现。
- 接口未规定 `start()/stop()` 的幂等性与重复调用语义；重复调用的行为由实现决定。
- 进程存活通常与 `WebServer.start()` 启动的非守护线程相关（见 [../mechanism/WebServerLifecycleAndThreads.md](../mechanism/WebServerLifecycleAndThreads.md)）。

## 常用构造/操作（仅列出接口与符号）
- 生命周期：`start()` / `stop()` / `getPort()`
- 优雅关闭（可选）：`shutDownGracefully(callback)`

## 关系：上级/下级/等价/特例/推广
- 上级：应用对外服务形态（Web 形态的运行时承载）。
- 相关类型（回调/异常/策略）：
  - `WebServerException`（`start/stop` 失败的异常类型）
  - `GracefulShutdownCallback` / `GracefulShutdownResult`（优雅关闭结果回传）
  - `Shutdown`（关闭策略枚举：`GRACEFUL/IMMEDIATE`，常见由 server 配置驱动）
- 被创建者：
  - `ServletWebServerFactory.getWebServer(...)`：见 [ServletWebServerFactory.md](ServletWebServerFactory.md)
  - `ReactiveWebServerFactory.getWebServer(...)`：见 [ReactiveWebServerFactory.md](ReactiveWebServerFactory.md)
- 被持有者：
  - `ServletWebServerApplicationContext.getWebServer()`（Servlet web context 暴露的持有者视图）
- 运行时宿主：Servlet/Reactive Web 上下文在 `refresh()` 中创建并持有（见 [springboot/flows/ServletWeb应用持续运行机制.md](../../../flows/ServletWeb应用持续运行机制.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → interface → WebServer → flows/ServletWeb应用持续运行机制。
