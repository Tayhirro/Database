---
type: class
tags:
  - springboot/web
  - tomcat
---

# StandardManager（Tomcat 默认内存会话管理器）

> **类型**：类（Class）

## 一句话
`StandardManager` 是 Tomcat 的常见 `Manager` 实现之一，用于在 JVM 堆内维护 `Session` 集合并执行会话过期回收。

## 严格定义
在 Tomcat 中，`org.apache.catalina.session.StandardManager` 作为 `Context` 的会话管理器实现，通常以“内存存储”为主：会话对象以 `sessionId -> Session` 的索引结构存放在 JVM 堆中，并由后台处理或请求访问路径触发过期检查与回收；其具体回收策略与持久化能力取决于实现与配置（实现相关）。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `sessions: {sessionId -> Session}`（内存索引视图）
  - `maxInactiveInterval`（会话超时语义，配置来源实现相关）
- 字段与状态（常见实现；字段名可能随 Tomcat 版本变化）：
  - `sessions`：会话映射表（实现相关）
  - `context: Context`：所属 Web 应用边界（实现相关）
  - `random` / id 生成器：会话 id 生成相关组件（实现相关）
  - `state`：生命周期状态（Tomcat `Lifecycle` 体系）
- 输入：
  - `createSession(...)` / `findSession(...)`（会话创建/查找，签名实现相关）
  - 后台处理（过期扫描/回收，方法名实现相关）
- 输出：
  - `Session` 的创建、查找、失效与回收（副作用）
- 约束：
  - “内存会话”语义下，会话对象位于 JVM 堆内，生命周期受可达性与回收策略共同影响：会话过期与失效由 `Manager` 逻辑处理；对象回收由 GC 处理（见 [../../../../../../java/jvm/gc/mechanism/ReachabilityAnalysis.md](../../../../../../java/jvm/gc/mechanism/ReachabilityAnalysis.md)）。

## 常用构造/操作（仅列出接口与符号）
- 会话创建/查找：`createSession(...)` / `findSession(...)`
- 失效：`expire(...)`（实现相关）

## 关系：上级/下级/等价/特例/推广
- 上级：
  - `Manager`：见 [../interface/Manager.md](../interface/Manager.md)
- 下级：
  - `StandardManager` → `Session`：见 [../interface/Session.md](../interface/Session.md)
- 相关：
  - `Context`：见 [Context.md](Context.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → StandardManager。
