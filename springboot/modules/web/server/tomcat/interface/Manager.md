---
title: Manager（Tomcat 会话管理器接口）
date: "2026-02-02"
categories:
  - springboot
tags:
  - springboot/web
  - tomcat
description: 类型：接口（Interface）
type: interface
---
# Manager（Tomcat 会话管理器接口）

> **类型**：接口（Interface）

## 一句话
`Manager` 定义了 Tomcat 在单个 `Context` 范围内创建、查找与回收 `Session` 的会话管理边界。

## 严格定义
在 Tomcat 中，`org.apache.catalina.Manager` 作为 `Context` 的会话子系统入口：它负责维护该 Web 应用的会话集合（`Session`），并提供会话创建、按 id 查找、会话回收/过期处理以及（可选）持久化/复制等能力；具体存储结构与回收策略由实现决定。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `sessions: {sessionId -> Session}`（会话集合的抽象视图）
  - `context: Context`（所属 Web 应用边界）
- 输入：
  - `createSession(sessionId)` / `findSession(sessionId)`（会话创建/查找，方法签名实现相关）
  - 过期处理与后台任务（例如周期性回收；方法名实现相关）
- 输出：
  - `Session`（会话对象；见 [Session.md](Session.md)）
- 约束：
  - `Manager` 的存储介质与一致性语义由实现决定：常见的“内存会话”实现将会话对象存放在 JVM 堆内；持久化/复制场景可能使用不同的存储或复制策略（实现相关）。

## 常用构造/操作（仅列出接口与符号）
- 会话创建/查找：`createSession(...)` / `findSession(...)`（实现相关）
- 生命周期协作：与 `Context` 的启动/停止及后台处理协作（实现相关）

## 关系：上级/下级/等价/特例/推广
- 上级：
  - `Context`（会话管理子系统所属边界，见 [../class/Context.md](../class/Context.md)）
- 下级：
  - `Manager` → `Session`：见 [Session.md](Session.md)
- 特例（常见实现）：
  - `StandardManager`：见 [../class/StandardManager.md](../class/StandardManager.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → interface → Manager。

