---
title: Wrapper（Tomcat Catalina Wrapper 组件）
date: "2026-02-02"
categories:
  - springboot
tags:
  - springboot/web
  - tomcat
description: 类型：类（Class）
type: class
---
# Wrapper（Tomcat Catalina Wrapper 组件）

> **类型**：类（Class）

## 一句话
`Wrapper` 是“单个 Servlet 的容器侧包装”：负责 `Servlet` 实例的初始化、调用与销毁，并作为 `Context` 下的最小容器单元参与请求分发。

## 严格定义
在 Tomcat 中，`org.apache.catalina.Wrapper` 是 `Context` 的子容器，用于封装一个具体 `Servlet` 的容器级管理：它将 `Servlet` 的生命周期（init/service/destroy）纳入容器生命周期，并在请求到达时负责分配/调用对应 `Servlet`；常见实现为 `org.apache.catalina.core.StandardWrapper`。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `name: String`：Servlet 名称（用于映射与查找）
  - `servletClass: String`：Servlet 类名（实现相关）
  - `loadOnStartup: int`：启动加载语义（实现相关）
- 字段与状态（常见实现；字段名可能随 Tomcat 版本变化）：
  - `name: String`：Servlet 名称
  - `servletClass: String`：Servlet 类名
  - `loadOnStartup: int`：启动加载顺序/语义
  - `servlet: Servlet`：Servlet 实例（分配/缓存策略实现相关）
  - `instanceInitialized: boolean`：Servlet 实例初始化状态（实现相关）
  - `initParameters: Map<String, String>`：初始化参数
  - `multipartConfig`：multipart 上传配置（实现相关）
  - `state`：生命周期状态（Tomcat `Lifecycle` 体系）
- 输入：
  - 生命周期触发：随 `Context`/容器生命周期推进
  - 请求调用：分配 Servlet 实例并调用 `service(req, resp)`（实现相关）
- 输出：
  - `Servlet` 的 init/service/destroy 调用（副作用）
- 约束：
  - `Wrapper` 只表示“单个 Servlet 的容器管理单元”；URL pattern 到 `Wrapper` 的映射由 `Context` 级别的映射表维护（实现相关）。

## 常用构造/操作（仅列出接口与符号）
- `load()` / `unload()`（实现相关）
- `allocate()` / `deallocate()`（实现相关）

## 关系：上级/下级/等价/特例/推广
- 上级：
  - `Context`：见 [Context.md](Context.md)
- 下级：
  - `Wrapper` → `Servlet`（Servlet API）
- 相关：
  - 前端控制器（MVC）：`DispatcherServlet`（见 [../../../class/DispatcherServlet.md](../../../class/DispatcherServlet.md)）

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → Wrapper。
