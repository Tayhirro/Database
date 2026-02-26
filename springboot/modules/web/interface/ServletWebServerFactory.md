---
title: ServletWebServerFactory（Servlet WebServer 工厂）
date: "2026-01-31"
categories:
  - springboot
description: 类型：接口（Interface）
---
# ServletWebServerFactory（Servlet WebServer 工厂）

> **类型**：接口（Interface）

## 一句话
`ServletWebServerFactory` 定义了创建 Servlet 场景 `WebServer` 的工厂方法：给定一组 `ServletContextInitializer`，返回一个可启动的 `WebServer` 实例。

## 严格定义
在 Spring Boot 中，`org.springframework.boot.web.servlet.server.ServletWebServerFactory` 继承 `WebServerFactory` 并定义 `getWebServer(ServletContextInitializer...)`；Servlet Web 上下文在 refresh 过程中从容器中获取一个 `ServletWebServerFactory` Bean，并调用该方法创建 `WebServer`，随后调用 `WebServer.start()` 启动端口监听与请求处理。

## 继承链（接口链 / 实现链）
- 接口链：`WebServerFactory` → `ServletWebServerFactory`。
- 常见实现：
  - Tomcat：`TomcatServletWebServerFactory`（见 [../class/TomcatServletWebServerFactory.md](../class/TomcatServletWebServerFactory.md)）
  - 其他实现（Jetty/Undertow）在 Boot 中提供对应工厂类（实现选择取决于依赖与自动配置）。

## 接口：数据 + 约束
- 输入：
  - `ServletContextInitializer...`：用于向 `ServletContext` 注册 Servlet/Filter/Listener 等初始化器集合
- 输出：
  - `WebServer`（Servlet 容器抽象，见 [WebServer.md](WebServer.md)）
- 约束：
  - 需要在 `ApplicationContext` 中存在且唯一的 `ServletWebServerFactory` Bean，才能创建 WebServer（多/少都会导致创建失败，语义取决于实现与调用点）。

## 常用构造/操作（仅列出接口与符号）
- 创建：`getWebServer(ServletContextInitializer...)`

## 关系：上级/下级/等价/特例/推广
- 上级：`WebServerFactory`（见 [WebServerFactory.md](WebServerFactory.md)）。
- 产物：`WebServer`（见 [WebServer.md](WebServer.md)）。
- 运行时触发点：Servlet Web 上下文的 `onRefresh()`（见 [springboot/flows/ServletWeb应用持续运行机制.md](../../../flows/ServletWeb应用持续运行机制.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → interface → ServletWebServerFactory → EmbeddedWebServer。

