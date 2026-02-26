---
title: FactoryBean（工厂 Bean）
date: "2026-01-31"
categories:
  - springboot
description: 类型：接口（Interface）
---
# FactoryBean（工厂 Bean）

> **类型**：接口（Interface）

## 一句话
`FactoryBean<T>` 是一种特殊 Bean：容器对其默认暴露的是 `T`（由 `getObject()` 产生的产物），而不是 `FactoryBean` 实例本身。

## 严格定义
在 Spring Framework 中，`org.springframework.beans.factory.FactoryBean<T>` 通过 `T getObject()` 将“工厂对象”与“被生产对象”区分开来；当客户端调用 `BeanFactory.getBean(beanName)` 时，若该 beanName 对应的定义类型为 `FactoryBean`，则默认返回 `getObject()` 的结果；若使用 `&beanName` 前缀，则返回 `FactoryBean` 实例本身。

## 继承链（接口链 / 实现链）
- 接口链：`FactoryBean<T>`（无上级接口）。
- 常见使用方式：由容器创建 `FactoryBean` 实例，再通过其 `getObject()` 产生产品对象。

## 接口：数据 + 约束
- 输入：
  - `getObject()`：无显式入参（通过 FactoryBean 内部状态与容器注入决定产物）
- 输出：
  - `T`（产品对象）
- 约束：
  - `getObject()` 的幂等性与缓存语义由实现与容器策略共同决定（singleton/prototype 等）。
  - `&beanName` 作为对工厂本体的访问约定，需要与普通 beanName 区分。

## 常用构造/操作（仅列出接口与符号）
- 产物：`getObject(): T`
- 类型：`getObjectType(): Class<?>`
- 单例声明：`isSingleton(): boolean`
- 访问规则：`getBean(\"name\")` vs `getBean(\"&name\")`

## 关系：上级/下级/等价/特例/推广
- 上级：Bean 生命周期中的特殊实例化/取值语义（见 [../mechanism/BeanLifecycle.md](../mechanism/BeanLifecycle.md)）。
- 相关：Bean 注册与创建流程中的 FactoryBean 场景（见 [springboot/flows/Bean注册与创建流程.md](../../../../flows/Bean注册与创建流程.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → beans → interface → FactoryBean → flows/Bean注册与创建流程。
