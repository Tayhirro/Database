# 嵌入式 WebServer（Embedded Web Server）

> **类型**：机制（Mechanism）

## 一句话
嵌入式 WebServer 是 Spring Boot 在 Web 应用形态下对“服务器创建、生命周期托管与与 `ApplicationContext` 绑定”的集成机制。

## 严格定义
当 `WebApplicationType` 为 servlet 或 reactive 时，Boot 会创建对应的 `ConfigurableApplicationContext`（常见为 Servlet/Reactive 的 web context 实现），并在启动过程中创建 `WebServer` 实例以接管端口与请求处理。

## 接口：数据 + 约束
- 输入：
  - `WebApplicationType`（servlet/reactive/none）
  - `Environment`（端口、上下文路径等配置项）
  - `ApplicationContext` 中的 server factory 相关 Bean 定义
- 输出：
  - `WebServer` 的启动与停止（作为应用生命周期的一部分）
- 约束：
  - 具体实现依赖所选服务器（Tomcat/Jetty/Undertow/Netty 等）与应用栈（Spring MVC / WebFlux）。

## 常用构造/操作（仅列出接口与符号）
- 服务器工厂：`WebServerFactory`（概念接口）
- 启动绑定位置：通常发生在 `ApplicationContext.refresh()` 前后的一段启动流程内（见 [springboot/flows/启动流程.md](../../../flows/启动流程.md)）。

## 关系：上级/下级/等价/特例/推广
- 上级：应用运行时（对外服务形态）。
- 相关：外部化配置（端口等）、生命周期事件（ready 边界）、`SpringApplication`（按 `WebApplicationType` 选择上下文实现）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → 嵌入式 WebServer → 启动流程。
