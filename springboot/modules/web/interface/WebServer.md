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
- 输入：
  - `start()`：启动对外服务（副作用）
  - `stop()`：停止对外服务并释放资源（副作用）
- 输出：
  - `getPort(): int`：返回实际绑定的端口
- 约束：
  - 线程与 I/O 模型由具体实现与底层容器决定；`WebServer` 仅表达生命周期边界。
  - 进程存活通常与 `WebServer.start()` 启动的非守护线程相关（见 [../mechanism/WebServerLifecycleAndThreads.md](../mechanism/WebServerLifecycleAndThreads.md)）。

## 常用构造/操作（仅列出接口与符号）
- 生命周期：`start()` / `stop()` / `getPort()`
- 优雅关闭（可选）：`shutDownGracefully(callback)`

## 关系：上级/下级/等价/特例/推广
- 上级：应用对外服务形态（Web 形态的运行时承载）。
- 被创建者：
  - `ServletWebServerFactory.getWebServer(...)`：见 [ServletWebServerFactory.md](ServletWebServerFactory.md)
  - `ReactiveWebServerFactory.getWebServer(...)`：见 [ReactiveWebServerFactory.md](ReactiveWebServerFactory.md)
- 运行时宿主：Servlet/Reactive Web 上下文在 `refresh()` 中创建并持有（见 [springboot/flows/ServletWeb应用持续运行机制.md](../../../flows/ServletWeb应用持续运行机制.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → interface → WebServer → flows/ServletWeb应用持续运行机制。

