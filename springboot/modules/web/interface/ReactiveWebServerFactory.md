---
title: ReactiveWebServerFactory（Reactive WebServer 工厂）
date: "2026-01-31"
categories:
  - springboot
description: 类型：接口（Interface）
---
# ReactiveWebServerFactory（Reactive WebServer 工厂）

> **类型**：接口（Interface）

## 一句话
`ReactiveWebServerFactory` 定义了 Reactive 场景 `WebServer` 的创建方式：给定一个 `HttpHandler`，返回一个可启动的 `WebServer` 实例。

## 严格定义
在 Spring Boot 中，`org.springframework.boot.web.reactive.server.ReactiveWebServerFactory` 继承 `WebServerFactory` 并定义 `getWebServer(HttpHandler)`；Reactive Web 上下文在 refresh 过程中获取一个 `ReactiveWebServerFactory` Bean，调用该方法创建 `WebServer` 并启动。

## 继承链（接口链 / 实现链）
- 接口链：`WebServerFactory` → `ReactiveWebServerFactory`。
- 常见实现：
  - Netty：`NettyReactiveWebServerFactory`（见 [../class/NettyReactiveWebServerFactory.md](../class/NettyReactiveWebServerFactory.md)）

## 接口：数据 + 约束
- 输入：
  - `HttpHandler`：Reactive HTTP 处理入口
- 输出：
  - `WebServer`（见 [WebServer.md](WebServer.md)）
- 约束：
  - 需要可用的 Reactive server 工厂 Bean；具体实现选择取决于依赖与自动配置。

## 常用构造/操作（仅列出接口与符号）
- 创建：`getWebServer(HttpHandler)`

## 关系：上级/下级/等价/特例/推广
- 上级：`WebServerFactory`（见 [WebServerFactory.md](WebServerFactory.md)）。
- 产物：`WebServer`（见 [WebServer.md](WebServer.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → interface → ReactiveWebServerFactory → EmbeddedWebServer。

