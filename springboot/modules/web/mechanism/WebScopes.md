---
title: WebScopes（Web 作用域：request/session 等）
date: "2026-01-29"
categories:
  - springboot
description: 类型：机制（Mechanism）
---
# WebScopes（Web 作用域：request/session 等）

> **类型**：机制（Mechanism）

## 一句话
WebScopes 机制描述了 Web 场景下 `request`/`session` 等 scope 如何被注册到 BeanFactory 的 scope 注册表，以及在请求处理期间如何通过请求上下文提供实例存取语义。

## 严格定义
在 WebApplicationContext 的实现中，refresh 过程的 `postProcessBeanFactory(beanFactory)` hook 可向 `ConfigurableBeanFactory` 注册 Web scope（例如 `request` 与 `session`）；当某个 BeanDefinition 的 scopeName 为 `request`/`session` 时，实例解析将委托给对应的 `Scope` 实现，该实现以“当前请求/会话”的 attributes 作为实例缓存介质。

## 接口：数据 + 约束
- 输入：
  - BeanFactory（`ConfigurableBeanFactory` 视图）
  - Web 请求上下文（请求进入时绑定，结束时解绑）
- 输出：
  - scope 注册：`registerScope("request", scope)` / `registerScope("session", scope)`
  - 运行态实例存取：按 request/session attributes 取/存实例
- 约束：
  - 若当前线程无法访问请求上下文，则 request/session scope 无法提供实例（例如在非 Web 线程或未绑定请求上下文的执行路径中）。
  - Web scope 名称需要与 BeanDefinition.scopeName 匹配（见 [../../core/beans/mechanism/ScopeResolution.md](../../core/beans/mechanism/ScopeResolution.md)）。

## 常用构造/操作（仅列出接口与符号）
- 注册入口：`ConfigurableBeanFactory.registerScope(name, scope)`（见 [../../core/beans/interface/ConfigurableBeanFactory.md](../../core/beans/interface/ConfigurableBeanFactory.md)）
- Bean 获取：`BeanFactory.getBean(name)`（见 [../../core/beans/interface/BeanFactory.md](../../core/beans/interface/BeanFactory.md)）

## 关系：上级/下级/等价/特例/推广
- 上级：Scope（作用域）机制（见 [../../core/beans/interface/Scope.md](../../core/beans/interface/Scope.md)）。
- 相关：
  - Spring MVC 请求入口：`DispatcherServlet`（见 [../class/DispatcherServlet.md](../class/DispatcherServlet.md)）
  - ThreadLocalContext（请求上下文在运行态的线程绑定视图）：见 [ThreadLocalContext.md](ThreadLocalContext.md)
  - scope 注册与解析：见 [../../core/beans/mechanism/ScopeResolution.md](../../core/beans/mechanism/ScopeResolution.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → mechanism → WebScopes → core/beans/mechanism/ScopeResolution。
