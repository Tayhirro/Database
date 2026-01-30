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
- 约束：
  - 线程模型由 Reactor Netty 的 event loop 决定；`NettyWebServer` 负责触发生命周期。

## 常用构造/操作（仅列出接口与符号）
- 生命周期：`start()` / `stop()` / `getPort()`
- 优雅关闭：`shutDownGracefully(callback)`

## 关系：上级/下级/等价/特例/推广
- 上级：`WebServer`（见 [../interface/WebServer.md](../interface/WebServer.md)）。
- 创建者：`NettyReactiveWebServerFactory`（见 [NettyReactiveWebServerFactory.md](NettyReactiveWebServerFactory.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → class → NettyWebServer → EmbeddedWebServer。

