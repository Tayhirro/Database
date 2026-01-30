# WebServerFactory（WebServer 工厂标记接口）

> **类型**：接口（Interface）

## 一句话
`WebServerFactory` 是 Spring Boot 对 WebServer 工厂的标记接口，用于将 Servlet 与 Reactive 两类创建工厂统一到同一类型层次下。

## 严格定义
在 Spring Boot 中，`org.springframework.boot.web.server.WebServerFactory` 是一个空接口（marker interface）；`ServletWebServerFactory` 与 `ReactiveWebServerFactory` 都继承该接口，并各自定义创建 `WebServer` 的工厂方法签名。

## 继承链（接口链 / 实现链）
- 接口链：`WebServerFactory`（marker）。
- 子接口：
  - `ServletWebServerFactory`（见 [ServletWebServerFactory.md](ServletWebServerFactory.md)）
  - `ReactiveWebServerFactory`（见 [ReactiveWebServerFactory.md](ReactiveWebServerFactory.md)）

## 接口：数据 + 约束
- 约束：
  - 作为 marker，不定义方法；创建行为由子接口定义。

## 常用构造/操作（仅列出接口与符号）
- 无（标记接口）。

## 关系：上级/下级/等价/特例/推广
- 下级：Servlet/Reactive 工厂接口（见本页“子接口”）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → interface → WebServerFactory → EmbeddedWebServer。

