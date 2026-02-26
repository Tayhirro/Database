---
title: Singleton（单例）
date: "2026-01-29"
categories:
  - springboot
description: 类型：模式（Pattern）
---
# Singleton（单例）

> **类型**：模式（Pattern）

## 一句话
Singleton 在给定作用域内保证某个类型只有一个实例，并提供对该实例的受控访问入口。

## 严格定义
设类型为 $T$、作用域为 $S$，单例模式要求在 $S$ 内满足：
$$
|Inst_S(T)| = 1
$$
并通过唯一访问点返回该实例；并发可见性与初始化时机（饿汉/懒汉）属于实现约束的一部分。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - 单例实例引用（存储位置由实现定义）
  - 访问入口：`getInstance()`（或等价机制）
- 约束：
  - “作用域”的口径需要明确（进程/类加载器/容器/上下文）。
  - 并发语义需要满足唯一性与可见性约束。

## 常用构造/操作（仅列出接口与符号）
- 访问：`getInstance()`
- 初始化：eager / lazy

## 关系：上级/下级/等价/特例/推广
- 相关：Facade（见 [Facade.md](Facade.md)）、Flyweight（见 [Flyweight.md](Flyweight.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → Singleton → GoFDesignPatterns。

