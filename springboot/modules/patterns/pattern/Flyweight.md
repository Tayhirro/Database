---
title: Flyweight（享元）
date: "2026-01-29"
categories:
  - springboot
description: 类型：模式（Pattern）
---
# Flyweight（享元）

> **类型**：模式（Pattern）

## 一句话
Flyweight 通过共享细粒度对象来支持大量对象的高效使用，并将可共享的内部状态与不可共享的外部状态分离。

## 严格定义
设对象状态可分为内部状态 $s_i$（可共享）与外部状态 $s_e$（由使用方提供）。享元模式要求将对象实例的可变部分尽可能外部化为 $s_e$，并通过共享池按 key 复用相同 $s_i$ 的实例；使用时以 `operation(extrinsicState)` 方式注入 $s_e$。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `Flyweight`：共享对象接口
  - `FlyweightFactory`：共享池（`key -> flyweight`）
  - 内部状态 / 外部状态
- 约束：
  - 共享对象不应持有与调用上下文相关的外部状态，否则共享语义不成立。

## 常用构造/操作（仅列出接口与符号）
- 获取：`getFlyweight(key)`
- 使用：`flyweight.operation(extrinsicState)`

## 关系：上级/下级/等价/特例/推广
- 相关：Singleton（见 [Singleton.md](Singleton.md)）、Prototype（见 [Prototype.md](Prototype.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → Flyweight → GoFDesignPatterns。

