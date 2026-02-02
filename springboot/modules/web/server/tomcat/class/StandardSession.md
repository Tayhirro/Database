---
type: class
tags:
  - springboot/web
  - tomcat
---

# StandardSession（Tomcat 默认 Session 实现）

> **类型**：类（Class）

## 一句话
`StandardSession` 是 Tomcat 的常见 `Session` 实现之一，用于承载会话属性与会话状态并由 `Manager` 管理其生命周期。

## 严格定义
在 Tomcat 中，`org.apache.catalina.session.StandardSession` 实现 `org.apache.catalina.Session`：它在运行态保存会话 id、有效性、访问时间以及属性集合等状态，并与所属 `Manager` 协作完成会话创建、访问更新、失效与回收；具体属性存储与线程安全策略由实现决定（实现相关）。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `id: String`（会话标识）
  - `attributes: Map<String, Object>`（会话属性集合）
  - `creationTime/lastAccessedTime`（时间戳语义）
  - `isValid: boolean`（有效性）
- 字段与状态（常见实现；字段名可能随 Tomcat 版本变化）：
  - `attributes`：属性映射表（实现相关）
  - `manager: Manager`：所属管理器（实现相关）
  - `state`：有效性/过期相关状态（实现相关）
- 输入：
  - `setAttribute(name, value)` / `getAttribute(name)`（实现相关）
  - `invalidate()`（实现相关）
- 输出：
  - 属性集合变化与会话状态变化（副作用）
- 约束：
  - `StandardSession` 的创建与回收由 `Manager` 管理（见 [StandardManager.md](StandardManager.md)、[../interface/Manager.md](../interface/Manager.md)）。

## 常用构造/操作（仅列出接口与符号）
- 属性读写：`getAttribute` / `setAttribute`
- 失效：`invalidate`

## 关系：上级/下级/等价/特例/推广
- 上级：
  - `Session`：见 [../interface/Session.md](../interface/Session.md)
- 相关：
  - `StandardManager`：见 [StandardManager.md](StandardManager.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → StandardSession。

