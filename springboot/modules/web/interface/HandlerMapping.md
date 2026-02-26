---
title: HandlerMapping（MVC 处理器映射接口）
date: "2026-02-02"
categories:
  - springboot
description: 类型：接口（Interface）
---
# HandlerMapping（MVC 处理器映射接口）

> **类型**：接口（Interface）

## 一句话
`HandlerMapping` 定义了 Spring Web MVC 将一次 `HttpServletRequest` 映射为“处理器（handler）+ 拦截器链”的查找边界。

## 严格定义
在 Spring Web MVC 中，`org.springframework.web.servlet.HandlerMapping` 的核心职责是：给定一次请求 `HttpServletRequest`，返回可执行的 `HandlerExecutionChain`（包含 handler 与可选的 `HandlerInterceptor[]`）；`DispatcherServlet` 在分发阶段遍历一组 `HandlerMapping` 以完成 handler 的解析。

## 继承链（接口链 / 实现链）
- 接口链：`HandlerMapping`（无上级接口）。
- 常见实现（实现选择与项目配置相关）：
  - 注解映射：`RequestMappingHandlerMapping`
  - 显式 URL 映射：`SimpleUrlHandlerMapping`

## 接口：数据 + 约束
- 输入：
  - `HttpServletRequest`（请求视图）
- 输出：
  - `HandlerExecutionChain`（handler + interceptors）
- 约束：
  - `HandlerMapping` 只定义“查找结果”，不定义 handler 的执行方式；执行由 `HandlerAdapter` 负责（见 [HandlerAdapter.md](HandlerAdapter.md)）。
  - 当多个 `HandlerMapping` 同时存在时，通常按顺序遍历并取第一个匹配结果（顺序与配置相关）。

## 常用构造/操作（仅列出接口与符号）
- `getHandler(request): HandlerExecutionChain`

## 关系：上级/下级/等价/特例/推广
- 上级：`DispatcherServlet`（请求分发宿主，见 [../class/DispatcherServlet.md](../class/DispatcherServlet.md)）。
- 相关：`HandlerAdapter`（执行边界，见 [HandlerAdapter.md](HandlerAdapter.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → interface → HandlerMapping。

