---
title: Session（Tomcat Session 接口）
date: "2026-02-02"
categories:
  - springboot
tags:
  - springboot/web
  - tomcat
description: 类型：接口（Interface）
type: interface
---
# Session（Tomcat Session 接口）

> **类型**：接口（Interface）

## 一句话
`Session` 定义了 Tomcat 容器侧对一次会话状态的抽象视图，用于在 `Manager` 中被创建、查找与回收。

## 严格定义
在 Tomcat 中，`org.apache.catalina.Session` 表示容器侧会话对象：它包含会话标识、创建/访问时间、有效性与属性集合等状态，并与请求处理链路中的会话解析与写回协作（例如基于 cookie 的 session id 关联；具体机制实现相关）。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `id: String`（会话标识）
  - `isValid: boolean`（有效性）
  - `creationTime/lastAccessedTime`（时间戳语义，实现相关）
  - `attributes: Map<String, Object>`（会话属性集合）
- 输入：
  - 属性读写：`getAttribute(name)` / `setAttribute(name, value)`（实现相关）
  - 有效性控制：`invalidate()`（实现相关）
- 输出：
  - 属性集合变化与会话状态变化（副作用）
- 约束：
  - 会话对象的生命周期通常由 `Manager` 管理：创建、过期回收、以及（可选）持久化/复制（见 [Manager.md](Manager.md)）。

## 常用构造/操作（仅列出接口与符号）
- 属性操作：`getAttribute` / `setAttribute`
- 失效：`invalidate`

## 关系：上级/下级/等价/特例/推广
- 上级：
  - `Manager`（会话管理器，见 [Manager.md](Manager.md)）
- 特例（常见实现）：
  - `StandardSession`：见 [../class/StandardSession.md](../class/StandardSession.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → interface → Session。

