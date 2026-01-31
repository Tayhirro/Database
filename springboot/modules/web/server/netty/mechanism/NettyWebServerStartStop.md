---
type: mechanism
tags:
  - springboot/web
  - netty
---

# NettyWebServerStartStop（NettyWebServer 的 start/stop 触发链）

> **类型**：机制（Mechanism）

## 一句话
`NettyWebServer.start()/stop()` 是 Boot 对 Reactor Netty 服务器生命周期的适配入口：`start()` 触发 `HttpServer.bindNow(...)` 并持有 `DisposableServer`，`stop()` 通过 `DisposableServer.disposeNow(...)` 停止服务并释放资源。

## 严格定义
在 Boot 2.7.4 中，`NettyWebServer` 实现 `WebServer` 并持有 `reactor.netty.http.server.HttpServer`；`start()` 通过 `startHttpServer()` 构造最终的 `HttpServer`（可选 routeProviders 或 handler），并调用 `bindNow([timeout])` 获得 `DisposableServer` 作为运行态句柄；`stop()` 通过 `DisposableServer.disposeNow([timeout])` 触发停止。

## 接口：数据 + 约束
- 数据：
  - `httpServer: HttpServer`
  - `disposableServer: DisposableServer | null`
  - `routeProviders: List<NettyRouteProvider>`
  - `lifecycleTimeout: Duration | null`
  - `gracefulShutdown: GracefulShutdown | null`（由 `Shutdown` 策略决定是否存在）
- 约束：
  - `start()` 返回表示“绑定已完成且 server 进入运行态”，而不是“在当前线程阻塞处理请求”；请求处理与 I/O 由底层 event loop 与 Netty 管道执行（见 [NettyThreadingAndExecutors.md](NettyThreadingAndExecutors.md)）。

## 触发链（实现级时间线）
### 1) `start()`
概念级步骤：
1. 若 `disposableServer == null`，调用 `startHttpServer()`：
   - 若 `routeProviders` 为空：`httpServer.handle(handler)`
   - 否则：`httpServer.route(consumer)`（将 routeProviders 组合为 route consumer）
2. 调用 `HttpServer.bindNow([lifecycleTimeout])`，得到 `DisposableServer`
3. 将 `disposableServer` 写入字段，用于后续 `getPort()/stop()`

### 2) `stop()`
概念级步骤：
- 若存在 `GracefulShutdown`，先 `abort()` 终止进行中的优雅关闭
- 调用 `DisposableServer.disposeNow([lifecycleTimeout])`
- 将 `disposableServer` 置空

## 关系：上级/下级/等价/特例/推广
- 上级：`WebServer` 生命周期语义（见 [../../../interface/WebServer.md](../../../interface/WebServer.md)）。
- 下级：Netty 的线程与 event loop 模型（见 [NettyThreadingAndExecutors.md](NettyThreadingAndExecutors.md)）。
- 相关：`NettyWebServer`（Boot 适配层，见 [../../../class/NettyWebServer.md](../../../class/NettyWebServer.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → netty → mechanism → NettyWebServerStartStop。

