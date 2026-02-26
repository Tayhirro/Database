---
title: NettyWebServer（Netty WebServer 封装）
date: "2026-01-31"
categories:
  - springboot
description: 类型：类（Class）
---
# NettyWebServer（Netty WebServer 封装）

> **类型**：类（Class）

## 一句话
`NettyWebServer` 是 `WebServer` 的 Netty 适配实现：封装 Reactor Netty `HttpServer` 的启动、停止与端口绑定信息。

## 严格定义
在 Spring Boot 中，`org.springframework.boot.web.embedded.netty.NettyWebServer` 实现 `WebServer`；其 `start()` 触发 Reactor Netty 服务器绑定端口并启动 event loop，`stop()` 停止并释放资源，`getPort()` 返回实际绑定端口。

## 继承链（接口链 / 实现链）
- 继承链：`java.lang.Object` → `NettyWebServer`。
- 实现接口：`WebServer`（见 [../interface/WebServer.md](../interface/WebServer.md)）。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `httpServer: reactor.netty.http.server.HttpServer`
  - `handlerAdapter: ReactorHttpHandlerAdapter`
- 构造签名（Boot 2.7.4）：
  - `NettyWebServer(HttpServer, ReactorHttpHandlerAdapter, Duration, Shutdown)`
- 可选路由扩展（Boot 2.7.4）：
  - `setRouteProviders(List<NettyRouteProvider>)`
  - 其中 `NettyRouteProvider` 为 `Function<HttpServerRoutes, HttpServerRoutes>` 形式的路由变换器，用于在 server 启动前组合/注入路由规则。
- 约束：
  - 线程模型由 Reactor Netty 的 event loop 决定；`NettyWebServer` 负责触发生命周期。
  - `Duration` 与 `Shutdown` 作为构造输入用于描述停止/优雅关闭相关的时间与策略参数（具体边界由实现定义）。

## 常用构造/操作（仅列出接口与符号）
- 生命周期：`start()` / `stop()` / `getPort()`
- 优雅关闭：`shutDownGracefully(callback)`

## 关系：上级/下级/等价/特例/推广
- 上级：`WebServer`（见 [../interface/WebServer.md](../interface/WebServer.md)）。
- 创建者：`NettyReactiveWebServerFactory`（见 [NettyReactiveWebServerFactory.md](NettyReactiveWebServerFactory.md)）。
- 相关：Reactor Netty（`HttpServer` / event loop）提供实际 I/O 与线程模型；`NettyWebServer` 在其上提供 Boot 的生命周期适配层。
- 机制细化：
  - `start/stop` 触发链：见 [../server/netty/mechanism/NettyWebServerStartStop.md](../server/netty/mechanism/NettyWebServerStartStop.md)
  - 线程与 event loop：见 [../server/netty/mechanism/NettyThreadingAndExecutors.md](../server/netty/mechanism/NettyThreadingAndExecutors.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → class → NettyWebServer → EmbeddedWebServer。
