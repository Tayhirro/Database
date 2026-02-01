---
type: mechanism
tags:
  - springboot/web
  - tomcat
  - springboot/embedded
---

# EmbeddedTomcatIntegration（Spring Boot 与 Embedded Tomcat 的接入）

> **类型**：机制（Mechanism）

## 一句话
Embedded Tomcat 接入机制描述 Spring Boot 在 Servlet Web 应用形态下，如何通过 `ServletWebServerFactory` 创建并托管 Tomcat 的生命周期，并将其绑定到 `ApplicationContext` 的启动/停止流程中。

## 严格定义
在 Spring Boot 的 Servlet Web 应用形态中，`ServletWebServerApplicationContext` 在 `refresh()` 过程中创建 `WebServer` 并负责其生命周期托管；当选择的实现为 Tomcat 时，该 `WebServer` 通常由 `TomcatServletWebServerFactory` 创建（返回 `TomcatWebServer`），并通过内部的 `org.apache.catalina.startup.Tomcat` 组装 `Server/Service/Connector/Engine/Host/Context/...` 等 Tomcat 组件以承载 Servlet 请求处理。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `ServletWebServerApplicationContext`：Web 应用上下文实现（创建并持有 `WebServer`）
  - `ServletWebServerFactory`：WebServer 工厂抽象（Servlet 栈）
  - `TomcatServletWebServerFactory`：Tomcat 的工厂实现（创建 `TomcatWebServer`）
  - `TomcatWebServer`：Boot 的 `WebServer` 适配层（封装 Tomcat 的 start/stop/destroy）
  - `org.apache.catalina.startup.Tomcat`：Tomcat 侧组装入口（创建/持有 Catalina 组件）
- 输入：
  - `ApplicationContext.refresh()`（触发 WebServer 创建与启动绑定）
  - `Environment`/配置属性（端口、地址、SSL、线程与连接参数等；以工厂/定制器形式生效）
- 输出：
  - `WebServer.start()` / `WebServer.stop()` 的生命周期行为（副作用）
  - Tomcat 组件树（`Server/Service/...`）的创建、启动与销毁（副作用）
- 约束：
  - 该机制仅适用于 `WebApplicationType=servlet`；在 reactive 形态下使用 `ReactiveWebServerFactory` 及其实现。
  - 服务器实现由工厂 Bean 与 classpath 条件共同决定；Tomcat/Jetty/Undertow 可替换。
  - Tomcat 侧类型（`org.apache.catalina.*`、`org.apache.coyote.*` 等）来自 Apache Tomcat 依赖；Boot 侧通过 `WebServer`/`ServletWebServerFactory` 抽象边界完成生命周期与配置接入。

## 常用构造/操作（仅列出接口与符号）
- `ServletWebServerApplicationContext.onRefresh()` → `createWebServer()`
- `TomcatServletWebServerFactory.getWebServer(...)` → `TomcatWebServer`
- `WebServer.start()` / `WebServer.stop()`

## 关系：上级/下级/等价/特例/推广
- 上级：
  - 嵌入式 WebServer：见 [../../../mechanism/EmbeddedWebServer.md](../../../mechanism/EmbeddedWebServer.md)
- 下级：
  - `TomcatWebServer` → Tomcat start/stop 触发链：见 [TomcatWebServerStartStop.md](TomcatWebServerStartStop.md)
  - Tomcat 组件模型：见 [TomcatComponentModel.md](TomcatComponentModel.md)
- 相关：
  - Context refresh（Framework）：见 [../../../../core/context/mechanism/ContextRefresh.md](../../../../core/context/mechanism/ContextRefresh.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → mechanism → EmbeddedTomcatIntegration。
