---
type: class
tags:
  - springboot/web
  - tomcat
---

# Engine（Tomcat Catalina Engine 组件）

> **类型**：类（Class）

## 一句话
`Engine` 是 `Service` 侧容器链路的顶层入口：按虚拟主机（Host）规则选择 `Host` 并将请求推进到其子级容器。

## 严格定义
在 Tomcat 中，`org.apache.catalina.Engine` 是 `Service` 持有的容器入口（`Service.setContainer(...)` 的典型取值）：它作为 `Host` 的父容器，维护 `defaultHost` 等配置，并将请求分派到匹配的 `Host`（虚拟主机）继续处理；常见实现为 `org.apache.catalina.core.StandardEngine`。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `hosts: Host[]`：子容器集合
  - `defaultHost: String`：默认虚拟主机名（实现相关）
- 输入：
  - 子容器管理：`addChild(host)` / `findChild(name)`
  - 配置：`setDefaultHost(name)`
- 输出：
  - 将请求分派到某个 `Host` 的处理链路（运行态行为）
- 约束：
  - `Engine` 作为容器链路入口，不包含网络监听逻辑；网络入口由 `Connector/ProtocolHandler/Endpoint` 提供。

## 常用构造/操作（仅列出接口与符号）
- `addChild(Host)` / `findChild(String)`
- `setDefaultHost(String)`

## 关系：上级/下级/等价/特例/推广
- 上级：
  - `Service`：见 [Service.md](Service.md)
- 下级：
  - `Engine` → `Host`：见 [Host.md](Host.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → Engine。

