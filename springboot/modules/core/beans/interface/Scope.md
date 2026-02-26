---
title: Scope（作用域）
date: "2026-01-29"
categories:
  - springboot
description: 类型：接口（Interface）
---
# Scope（作用域）

> **类型**：接口（Interface）

## 一句话
`Scope` 定义了非 singleton/prototype 场景下 Bean 实例的存取与销毁回调注册方式，使 BeanFactory 可将实例缓存委托给外部上下文（如 request/session）。

## 严格定义
在 Spring Framework 中，`org.springframework.beans.factory.config.Scope` 定义了 `get(String name, ObjectFactory<?> objectFactory)` 等方法；当 BeanDefinition 的 `scopeName` 为某个已注册 scope 时，BeanFactory 将把实例的获取/创建委托给对应的 `Scope` 实现完成。

## 继承链（接口链 / 实现链）
- 接口链：`Scope`（无上级接口）。
- 常见实现：Web 场景下的 `RequestScope` / `SessionScope` 等（由 Web 体系在 BeanFactory 中注册后可用）。

## 接口：数据 + 约束
- 输入：
  - `name`（beanName）
  - `objectFactory`（创建 Bean 实例的回调）
- 输出：
  - scope 内的实例（存在则返回，不存在则由 `objectFactory` 创建并保存后返回）
- 约束：
  - scope 的“上下文”由实现决定；例如 request scope 依赖“当前请求”的可访问性（见 [../../../web/mechanism/WebScopes.md](../../../web/mechanism/WebScopes.md)）。

## 常用构造/操作（仅列出接口与符号）
- 获取/创建：`get(name, objectFactory)`
- 移除：`remove(name)`
- 销毁回调：`registerDestructionCallback(name, callback)`

## 关系：上级/下级/等价/特例/推广
- 上级：观察者模式中的注册表/查找表思想的实例化缓存应用（与具体框架无关）。
- 相关：
  - `ConfigurableBeanFactory.registerScope(...)`：见 [ConfigurableBeanFactory.md](ConfigurableBeanFactory.md)
  - scope 解析机制：见 [../mechanism/ScopeResolution.md](../mechanism/ScopeResolution.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → beans → interface → Scope → flows/Bean 注册与创建流程。

