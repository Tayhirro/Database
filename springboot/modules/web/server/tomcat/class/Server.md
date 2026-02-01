---
type: class
tags:
  - springboot/web
  - tomcat
---

# Server（Tomcat Catalina Server 组件）

> **类型**：类（Class）

## 一句话
`Server` 是 Tomcat Catalina 组件层级的顶层聚合：持有一个或多个 `Service`，并将生命周期操作传播到其子组件。

## 严格定义
在 Tomcat 中，`org.apache.catalina.Server` 表示 Catalina 侧的“服务集合与生命周期协调”边界：它聚合 `Service`，并在 `start/stop` 等生命周期阶段推进各 `Service` 及其下级组件进入相应状态；常见实现为 `org.apache.catalina.core.StandardServer`。

## 接口：数据 + 约束
说明：本节分两层表述——“数据（语义级别）”描述该概念在接口/职责层面所管理的对象关系（稳定边界）；“字段与状态”记录某个常见实现（如 `StandardServer`）里可能出现的成员变量与运行态状态，可能与前者重叠且随版本变化。
- 数据（语义级别）：
  - `services: Service[]`：`Server` 所管理的一组 `Service`
  - （实现相关）关闭控制与全局资源：例如 shutdown 监听、命名资源等
- 字段与状态（常见实现；字段名可能随 Tomcat 版本变化）：
  - `port: int`：shutdown 监听端口（与 HTTP 监听端口不同）
  - `address: String`：shutdown 监听地址（可选）
  - `shutdown: String`：shutdown 命令 token
  - `state`：生命周期状态（Tomcat `Lifecycle` 体系）
  - `listeners`：生命周期监听器集合（实现相关）
- 输入：
  - 生命周期触发：`start()` / `stop()`（以及实现内部的对应阶段）
  - 服务管理：`addService(service)` / `removeService(service)` / `findServices()`
- 输出：
  - 子组件生命周期迁移（副作用）：推进 `Service` 及其子链路启动/停止
- 约束：
  - `Server` 作为聚合与生命周期协调边界，不直接承担协议解析与请求处理；请求处理链路在 `Service` 持有的 `Connector` 与 `Engine`（及其子容器）中完成。

## 常用构造/操作（仅列出接口与符号）
- `addService(Service)` / `removeService(Service)` / `findServices()`
- `start()` / `stop()`
- `await()`（实现相关：阻塞等待关闭条件）

## 关系：上级/下级/等价/特例/推广
- 上级：
  - Tomcat 组件模型：见 [../mechanism/TomcatComponentModel.md](../mechanism/TomcatComponentModel.md)
- 下级：
  - `Server` → `Service`：见 [Service.md](Service.md)
- 相关：
  - Boot 生命周期适配层：`WebServer`（见 [../../../interface/WebServer.md](../../../interface/WebServer.md)）
  - Tomcat start/stop 触发链：见 [../mechanism/TomcatWebServerStartStop.md](../mechanism/TomcatWebServerStartStop.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → Server。
