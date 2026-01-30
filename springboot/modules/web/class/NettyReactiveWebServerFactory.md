# NettyReactiveWebServerFactory（Netty Reactive 工厂）

> **类型**：类（Class）

## 一句话
`NettyReactiveWebServerFactory` 是 `ReactiveWebServerFactory` 的 Netty 实现：根据 `HttpHandler` 创建并返回一个封装 Reactor Netty 的 `NettyWebServer`。

## 严格定义
在 Spring Boot 中，`org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory` 继承 `AbstractReactiveWebServerFactory` 并实现 `getWebServer(HttpHandler)`；它负责构建 Reactor Netty 的 `HttpServer` 并创建 `NettyWebServer`，由后者在 `start()` 时绑定端口并启动 event loop。

## 继承链（接口链 / 实现链）
- 继承链：`AbstractReactiveWebServerFactory` → `NettyReactiveWebServerFactory`。
- 实现接口：`ReactiveWebServerFactory`（见 [../interface/ReactiveWebServerFactory.md](../interface/ReactiveWebServerFactory.md)）。

## 接口：数据 + 约束
- 输入：
  - `HttpHandler`（Reactive 处理入口）
- 输出：
  - `WebServer`（通常为 `NettyWebServer`）
- 约束：
  - 依赖 Reactor Netty；event loop 与线程模型由底层库实现决定。

## 常用构造/操作（仅列出接口与符号）
- 创建：`getWebServer(HttpHandler)`
- 自定义：`addServerCustomizers(...)` / `addRouteProviders(...)`

## 关系：上级/下级/等价/特例/推广
- 上级：`ReactiveWebServerFactory`（见 [../interface/ReactiveWebServerFactory.md](../interface/ReactiveWebServerFactory.md)）。
- 产物：`NettyWebServer`（见 [NettyWebServer.md](NettyWebServer.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → class → NettyReactiveWebServerFactory → EmbeddedWebServer。

