# TomcatWebServer（Tomcat WebServer 封装）

> **类型**：类（Class）

## 一句话
`TomcatWebServer` 是 `WebServer` 的 Tomcat 适配实现：封装 `Tomcat` 的启动与停止，并向 Boot 暴露统一的 `start/stop/getPort` 生命周期接口。

## 严格定义
在 Spring Boot 中，`org.springframework.boot.web.embedded.tomcat.TomcatWebServer` 实现 `WebServer`，内部持有 `org.apache.catalina.startup.Tomcat`；`start()` 会启动 Tomcat 并绑定端口，`stop()` 停止并释放资源；端口由 `getPort()` 返回。

## 继承链（接口链 / 实现链）
- 继承链：`java.lang.Object` → `TomcatWebServer`。
- 实现接口：`WebServer`（见 [../interface/WebServer.md](../interface/WebServer.md)）。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `tomcat: org.apache.catalina.startup.Tomcat`
- 构造签名（Boot 2.7.4）：
  - `TomcatWebServer(Tomcat)`
  - `TomcatWebServer(Tomcat, boolean)`
  - `TomcatWebServer(Tomcat, boolean, Shutdown)`
- 约束：
  - 非守护线程的创建与 I/O 模型由 Tomcat 实现决定；`TomcatWebServer` 负责触发其生命周期（见 [../mechanism/WebServerLifecycleAndThreads.md](../mechanism/WebServerLifecycleAndThreads.md)）。
  - `Shutdown` 作为关闭策略输入可影响 `shutDownGracefully(...)` 与停止阶段的语义边界（策略枚举定义由 Boot 提供）。

## 常用构造/操作（仅列出接口与符号）
- 生命周期：`start()` / `stop()` / `getPort()`
- 暴露底层对象：`getTomcat()`
- 优雅关闭（实现）：`shutDownGracefully(callback)`

## 关系：上级/下级/等价/特例/推广
- 上级：`WebServer`（见 [../interface/WebServer.md](../interface/WebServer.md)）。
- 创建者：`TomcatServletWebServerFactory`（见 [TomcatServletWebServerFactory.md](TomcatServletWebServerFactory.md)）。
- 相关：Tomcat 组件模型（`Tomcat` / `Service` / `Connector` / `Engine` / `Host` / `Context`）属于底层容器结构；`TomcatWebServer` 在该结构之上提供 Boot 的生命周期适配层。
- 机制细化：
  - `start/stop` 触发链：见 [../server/tomcat/mechanism/TomcatWebServerStartStop.md](../server/tomcat/mechanism/TomcatWebServerStartStop.md)
  - 线程与执行器：见 [../server/tomcat/mechanism/TomcatThreadingAndExecutors.md](../server/tomcat/mechanism/TomcatThreadingAndExecutors.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → class → TomcatWebServer → flows/ServletWeb应用持续运行机制。
