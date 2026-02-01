---
type: class
tags:
  - springboot/web
  - tomcat
---

# Service（Tomcat Catalina Service 组件）

> **类型**：类（Class）

## 一句话
`Service` 是 Tomcat 的一组“网络入口 + 容器入口”的组合：聚合 `Connector[]` 并持有一个 `Engine`，从而把协议处理链路与 Servlet 容器处理链路连接起来。

## 严格定义
在 Tomcat 中，`org.apache.catalina.Service` 位于 `Server` 与请求处理链路之间：它通常包含若干 `Connector` 作为网络入口，并持有一个 `Engine` 作为容器链路顶层入口；常见实现为 `org.apache.catalina.core.StandardService`。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `connectors: Connector[]`：网络入口集合（协议/端口/线程模型入口）
  - `container: Engine`：容器入口（通常为 `Engine`）
  - （可选）`executors: Executor[]`：供协议栈/端点使用的执行器集合（实现相关）
- 字段与状态（常见实现；字段名可能随 Tomcat 版本变化）：
  - `name: String`：Service 名称
  - `server: Server`：反向引用所属 `Server`（实现相关）
  - `connectors: Connector[]`：连接器集合
  - `container: Engine`：容器入口（通常为 `Engine`）
  - `executors: Executor[]`：执行器集合（可选，实现相关）
  - `state`：生命周期状态（Tomcat `Lifecycle` 体系）
- 输入：
  - 连接器管理：`addConnector(connector)` / `removeConnector(connector)`
  - 容器绑定：`setContainer(engine)`
  - 生命周期触发：`start()` / `stop()`
- 输出：
  - `Connector` 侧：启动/停止协议栈与端点监听（经 `ProtocolHandler/Endpoint`）
  - `Engine` 侧：启动/停止容器链路（Host/Context/Wrapper）
- 约束：
  - `Service` 通过“Connector → ProtocolHandler/Endpoint”完成网络接入，通过“Engine → Host → Context → Wrapper”完成应用容器分发；两条链路通过请求适配层在运行态衔接（实现相关）。

## 常用构造/操作（仅列出接口与符号）
- `addConnector(Connector)` / `findConnectors()`
- `setContainer(Engine)`
- `start()` / `stop()`

## 关系：上级/下级/等价/特例/推广
- 上级：
  - `Server`：见 [Server.md](Server.md)
- 下级：
  - `Service` → `Connector`：见 [Connector.md](Connector.md)
  - `Service` → `Engine`：见 [Engine.md](Engine.md)
- 相关：
  - Tomcat 组件模型：见 [../mechanism/TomcatComponentModel.md](../mechanism/TomcatComponentModel.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → Service。
