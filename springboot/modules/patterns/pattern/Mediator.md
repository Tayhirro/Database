---
title: Mediator（中介者）
date: "2026-01-29"
categories:
  - springboot
description: 类型：模式（Pattern）
---
# Mediator（中介者）

> **类型**：模式（Pattern）

## 一句话
Mediator 封装一组对象的交互规则，使对象之间不需要显式相互引用，从而降低耦合并集中管理协作逻辑。

## 严格定义
设同事对象集合为 $C=\\{c_1,\\dots,c_n\\}$。中介者模式引入中介者 $M$，使得当任一同事对象发生动作时，通过通知 $M$ 触发规则计算，由 $M$ 决定调用/通知哪些其他同事对象及其操作。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `Mediator` / `ConcreteMediator`
  - `Colleague` / `ConcreteColleague`
- 约束：
  - 协作规则集中在 Mediator；规则复杂度与集中度影响边界与可维护性口径。

## 常用构造/操作（仅列出接口与符号）
- 通知：`mediator.notify(sender, event)`
- 协作：`mediator` 调用其他 `colleague` 操作

## 关系：上级/下级/等价/特例/推广
- 相关：Facade（见 [Facade.md](Facade.md)）、Observer（见 [ObserverPattern.md](ObserverPattern.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → Mediator → GoFDesignPatterns。

