---
type: mechanism
tags:
  - springboot/web
  - tomcat
---

# TomcatWebServerStartStop（TomcatWebServer 的 start/stop 触发链）

> **类型**：机制（Mechanism）

## 一句话
`TomcatWebServer.start()/stop()` 是 Boot 对 Tomcat 生命周期的适配入口：`start()` 触发 `Tomcat.start()` 并推进连接器启动，`stop()` 触发停止与销毁以释放端口与线程等资源。

## 严格定义
在 Boot 2.7.4 中，`TomcatWebServer` 持有 `org.apache.catalina.startup.Tomcat` 实例并实现 `WebServer`；其 `initialize()` 内部调用 `Tomcat.start()` 启动 Tomcat，`start()` 负责执行启动前的连接器补全/校验并标记运行态，`stop()` 负责触发停止并调用 `Tomcat.destroy()` 释放资源。

## 接口：数据 + 约束
- 数据：
  - `tomcat: Tomcat`
  - `started: boolean`
  - `gracefulShutdown: GracefulShutdown | null`（由 `Shutdown` 策略决定是否存在）
- 输入：
  - `start()` / `stop()`（生命周期触发）
- 输出：
  - 端口监听与请求处理能力的启动/停止（副作用）
- 约束：
  - `start()`/`stop()` 的主要语义是“触发生命周期迁移”，并不等价于“在当前线程持续处理请求”；请求处理在底层容器线程中进行（见 [TomcatThreadingAndExecutors.md](TomcatThreadingAndExecutors.md)）。

## 触发链（实现级时间线）

### 1) 初始化阶段：`initialize()` 中触发 `Tomcat.start()`
Boot 的 `TomcatWebServer` 在构造后会执行一次初始化流程（语义上包含“启动 Tomcat”）：

1. `TomcatWebServer.initialize()`
2. `Tomcat.start()`
3. Tomcat 推进其 `Server/Service/Engine/Connector` 等组件进入 started 状态
4. `Connector.startInternal()` → `ProtocolHandler.start()` → `AbstractProtocol.start()` → `AbstractEndpoint.start()`

其中 `Connector.startInternal()` 将启动委托给 `ProtocolHandler.start()`；`AbstractProtocol.start()` 会调用 `endpoint.start()` 并启动/调度若干与连接处理相关的后台执行单元（例如 endpoint、监控任务等）。

### 2) 运行阶段：`start()` 只完成“触发与校验”
Boot 的 `TomcatWebServer.start()` 在已完成 `Tomcat.start()` 的前提下，主要执行：
- 补回之前移除的连接器（`addPreviouslyRemovedConnectors()`）
- 可选的延迟加载触发（`performDeferredLoadOnStartup()`）
- 校验连接器是否进入 started 状态（`checkThatConnectorsHaveStarted()`）
- 标记 `started=true` 并记录端口信息

`start()` 返回后 Tomcat 持续对外服务的条件来自 Tomcat 自身创建的线程/执行器持续运行，而不是 `start()` 持续阻塞当前线程。

### 3) 关闭阶段：`stop()` 与 `Tomcat.destroy()`
Boot 的 `TomcatWebServer.stop()` 概念级步骤：
- 将 `started` 置为 false
- 若存在 `GracefulShutdown`，则中止正在进行的优雅关闭（`abort()`）以进入一致的停止路径
- `stopTomcat()`（触发 Tomcat 停止逻辑）
- `Tomcat.destroy()`（释放资源）

## 关系：上级/下级/等价/特例/推广
- 上级：`WebServer` 生命周期语义（见 [../../../interface/WebServer.md](../../../interface/WebServer.md)）。
- 下级：Tomcat 线程与执行器模型（见 [TomcatThreadingAndExecutors.md](TomcatThreadingAndExecutors.md)）。
- 相关：Servlet Web 应用持续运行机制（见 [springboot/flows/ServletWeb应用持续运行机制.md](../../../../../flows/ServletWeb应用持续运行机制.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → mechanism → TomcatWebServerStartStop。

