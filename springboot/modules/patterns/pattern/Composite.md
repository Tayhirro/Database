---
title: Composite（组合）
date: "2026-01-29"
categories:
  - springboot
description: 类型：模式（Pattern）
---
# Composite（组合）

> **类型**：模式（Pattern）

## 一句话
Composite 将对象组织为树形结构以表达部分-整体层次，并使客户端可用同一接口对待叶子与组合节点。

## 严格定义
设组件接口为 $C$，叶子节点集合为 $L$，组合节点集合为 $G$。组合模式要求 $L$ 与 $G$ 共享接口 $C$，且 $G$ 维护子组件集合 `children ⊆ C`，使客户端对任一 $c\\in C$ 调用 `c.op()` 时无需区分其具体是叶子还是组合。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `Component`
  - `Leaf`
  - `Composite(children: List<Component>)`
- 约束：
  - 子节点管理接口（add/remove/getChild）放置位置与“透明/安全”口径相关。

## 常用构造/操作（仅列出接口与符号）
- 组合：`add(Component)` / `remove(Component)`
- 递归：`operation()` 遍历 children 并调用 `operation()`

## 关系：上级/下级/等价/特例/推广
- 相关：Iterator（见 [Iterator.md](Iterator.md)）、Visitor（见 [Visitor.md](Visitor.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → Composite → GoFDesignPatterns。

