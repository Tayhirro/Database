---
title: ApplicationEventMulticaster（事件多播器）
date: "2026-01-28"
categories:
  - springboot
description: 类型：接口（Interface）
---
# ApplicationEventMulticaster（事件多播器）

> **类型**：接口（Interface）

## 一句话
`ApplicationEventMulticaster` 是 Spring 事件体系中的分发接口：维护监听器注册表，并在事件发布时将 `ApplicationEvent` 分发给匹配的 `ApplicationListener`。

## 严格定义
在 Spring Framework 中，`org.springframework.context.event.ApplicationEventMulticaster` 定义了监听器注册与事件多播的最小契约，包括监听器对象与监听器 BeanName 两类注册方式，以及 `multicastEvent(...)` 的两种签名（是否显式提供 `ResolvableType`）。

## 继承链（接口链 / 实现链）
- 接口链：`ApplicationEventMulticaster`（定义 listener 注册/移除与 `multicastEvent(...)` 多播契约；无上级接口）。
- 常见实现链：`AbstractApplicationEventMulticaster` → `SimpleApplicationEventMulticaster`。

## 接口：数据 + 约束
- 数据（抽象）：
  - 监听器注册表：`ApplicationListener<?>` 集合与监听器 BeanName 集合
- 输入：
  - `ApplicationEvent event`
  - 可选：`ResolvableType eventType`（用于泛型事件类型解析）
- 输出：
  - 无返回值（副作用为调用 `ApplicationListener.onApplicationEvent(event)`）
- 约束：
  - 分发为触发式（push）：由 `publishEvent(...)` 或显式 `multicastEvent(...)` 触发一次分发循环。
  - 分发的同步/异步、错误处理与排序由具体实现决定（例如 `SimpleApplicationEventMulticaster`）。

## 常用构造/操作（仅列出接口与符号）
- 注册：`addApplicationListener(listener)` / `addApplicationListenerBean(beanName)`
- 取消：`removeApplicationListener(listener)` / `removeApplicationListenerBean(beanName)` / `removeAllListeners()`
- 分发：`multicastEvent(event)` / `multicastEvent(event, eventType)`

## 关系：上级/下级/等价/特例/推广
- 上级：观察者模式（Observer Pattern）：见 [../../../patterns/pattern/ObserverPattern.md](../../../patterns/pattern/ObserverPattern.md)
- 抽象实现：`AbstractApplicationEventMulticaster`（见 [../class/AbstractApplicationEventMulticaster.md](../class/AbstractApplicationEventMulticaster.md)）。
- 实现：`SimpleApplicationEventMulticaster`（见 [../class/SimpleApplicationEventMulticaster.md](../class/SimpleApplicationEventMulticaster.md)）。
- 消费方：`ApplicationListener`（见 [ApplicationListener.md](ApplicationListener.md)）。
- 运行时持有者：`ApplicationContext`（通过 bean 名 `applicationEventMulticaster` 维护默认多播器）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → events → ApplicationEventMulticaster →（Multicast / Listener Registry）。
