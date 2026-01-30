# TomcatServletWebServerFactory（Tomcat 工厂）

> **类型**：类（Class）

## 一句话
`TomcatServletWebServerFactory` 是 `ServletWebServerFactory` 的 Tomcat 实现：根据端口/上下文/连接器等配置创建 Tomcat，并返回一个封装 Tomcat 生命周期的 `TomcatWebServer`。

## 严格定义
在 Spring Boot 中，`org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory` 继承 `AbstractServletWebServerFactory` 并实现 `getWebServer(ServletContextInitializer...)`；它负责创建与配置 `org.apache.catalina.startup.Tomcat` 实例，并将其封装为 `TomcatWebServer` 作为 `WebServer` 返回值。

## 继承链（接口链 / 实现链）
- 继承链：`AbstractServletWebServerFactory` → `TomcatServletWebServerFactory`。
- 实现接口：`ServletWebServerFactory`（见 [../interface/ServletWebServerFactory.md](../interface/ServletWebServerFactory.md)）。

## 接口：数据 + 约束
- 输入：
  - `ServletContextInitializer...`（用于注册 Servlet/Filter/Listener 等）
  - 端口、上下文路径、连接器配置等（来自外部化配置与自定义器集合）
- 输出：
  - `WebServer`（通常为 `TomcatWebServer`）
- 约束：
  - 依赖 Tomcat 相关库（`org.apache.catalina.*`）；若运行时缺少依赖，将无法创建 Tomcat 实例。

## 常用构造/操作（仅列出接口与符号）
- 创建：`getWebServer(ServletContextInitializer...)`
- 配置入口（示例级别）：`setProtocol(...)` / `setBaseDirectory(...)` / `addContextCustomizers(...)`

## 关系：上级/下级/等价/特例/推广
- 上级：`ServletWebServerFactory`（见 [../interface/ServletWebServerFactory.md](../interface/ServletWebServerFactory.md)）。
- 产物：`TomcatWebServer`（见 [TomcatWebServer.md](TomcatWebServer.md)）。
- WebServer 持续运行语义：见 [../mechanism/WebServerLifecycleAndThreads.md](../mechanism/WebServerLifecycleAndThreads.md)。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → class → TomcatServletWebServerFactory → EmbeddedWebServer。

