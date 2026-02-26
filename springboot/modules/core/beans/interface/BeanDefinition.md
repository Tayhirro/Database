---
title: BeanDefinition（Bean 定义）
date: "2026-01-29"
categories:
  - springboot
description: 类型：接口（Interface）
---
# BeanDefinition（Bean 定义）

> **类型**：接口（Interface）

## 一句话
`BeanDefinition` 是对一个 Bean 的元数据描述，包含其类信息、依赖、生命周期回调与 scopeName 等可用于创建与装配的定义信息。

## 严格定义
在 Spring Framework 中，`org.springframework.beans.factory.config.BeanDefinition` 表示一个可被 BeanFactory 解析与创建的 Bean 描述；其中 `scope` 字段（scopeName）决定实例的存取策略：`singleton`/`prototype` 或其他已注册的自定义 scope（如 `request`/`session`）。

## 继承链（接口链 / 实现链）
- 接口链：`BeanDefinition`（无上级接口）。
- 常见实现：`RootBeanDefinition` / `GenericBeanDefinition` 等（实现细节因定义来源不同而异）。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `beanClass` / `beanClassName`
  - `scope`（scopeName，空/`singleton`/`prototype`/自定义名称）
  - 依赖与装配信息（构造参数、属性值、依赖关系）
  - 生命周期回调元数据（init/destroy 方法名等）
- 约束：
  - `scope` 非空且非内置名称时，要求 BeanFactory 已注册同名 `Scope`（见 [Scope.md](Scope.md)、[../mechanism/ScopeResolution.md](../mechanism/ScopeResolution.md)）。

## 常用构造/操作（仅列出接口与符号）
- 读取：`getScope()` / `getBeanClassName()` / `getPropertyValues()`
- 写入：`setScope(scopeName)` 等（通常由解析阶段写入）

## 关系：上级/下级/等价/特例/推广
- 被使用：`BeanDefinitionRegistry`（注册表）：见 [BeanDefinitionRegistry.md](BeanDefinitionRegistry.md)。
- 被解析：`BeanFactory`（创建/获取）：见 [BeanFactory.md](BeanFactory.md)。
- 相关：Scope 解析（见 [../mechanism/ScopeResolution.md](../mechanism/ScopeResolution.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → beans → interface → BeanDefinition → flows/Bean 注册与创建流程。

