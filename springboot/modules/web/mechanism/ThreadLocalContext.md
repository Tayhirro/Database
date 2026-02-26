---
title: ThreadLocalContext（线程绑定上下文）
date: "2026-02-03"
categories:
  - springboot
tags:
  - springboot/web
  - threadlocal
  - context
description: 类型：机制（Mechanism）
type: mechanism
---
# ThreadLocalContext（线程绑定上下文）

> **类型**：机制（Mechanism）

## 一句话
ThreadLocalContext 描述在同一线程的调用链路中，使用 `ThreadLocal<T>` 绑定与读取运行态上下文数据（例如当前用户、请求属性、事务上下文）的机制。

## 严格定义
给定一个线程集合 $Th$ 与值域 $V$，`ThreadLocal<T>` 在语义上提供一个部分映射 $f: Th \rightharpoonup V$：对每个线程 $t \in Th$，`set(v)` 将 $f(t)$ 设为 $v$，`get()` 读取 $f(t)$，`remove()` 删除 $f(t)$。

在 Servlet Web 请求处理过程中，容器通常以线程池复用工作线程处理请求；ThreadLocalContext 用于把“与当前请求相关、且只在该线程调用链内消费的数据”绑定到当前线程，使下游组件无需显式透传参数即可访问该上下文（实现与使用点相关）。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `thread -> value`：线程到上下文值的绑定关系
  - `value`：上下文数据（类型由使用者定义，例如 `UserDTO`、请求属性视图、事务资源句柄等）
- 输入：
  - 绑定：`ThreadLocal.set(value)`
  - 读取：`ThreadLocal.get()`
  - 解绑：`ThreadLocal.remove()`
- 输出：
  - 对当前线程可见的上下文值（或空）
- 约束：
  - 线程池复用导致线程可跨请求复用：若未执行解绑，则同一线程在后续请求中可能观察到前一次绑定的值（请求间污染语义）。
  - 跨线程执行（异步/任务提交）不属于同一线程调用链：未传播的上下文在新线程中不可见（传播策略实现相关）。

## 常用构造/操作（仅列出接口与符号）
- 绑定/解绑位置（Servlet 场景）：
  - `HandlerInterceptor.preHandle(...)`：绑定（见 [../interface/HandlerInterceptor.md](../interface/HandlerInterceptor.md)）
  - `HandlerInterceptor.afterCompletion(...)`：解绑（见 [../interface/HandlerInterceptor.md](../interface/HandlerInterceptor.md)）
  - `Filter.doFilter(...)`：绑定/解绑（实现相关）
- 访问点（示例）：
  - “业务代码”读取：`ThreadLocal.get()`

## 关系：上级/下级/等价/特例/推广
- 上级：
  - 线程与执行器模型（Web 运行态线程）：见 [WebServerLifecycleAndThreads.md](WebServerLifecycleAndThreads.md)
- 相关：
  - WebScopes（request/session scope 的运行态实例存取需要请求上下文可访问）：见 [WebScopes.md](WebScopes.md)
  - Servlet Web 应用持续运行机制（运行态链路与拦截器位置）：见 [springboot/flows/ServletWeb应用持续运行机制.md](../../../flows/ServletWeb应用持续运行机制.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → mechanism → ThreadLocalContext。
