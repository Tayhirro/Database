---
title: Adapter（适配器）
date: "2026-01-29"
categories:
  - springboot
description: 类型：模式（Pattern）
---
# Adapter（适配器）

> **类型**：模式（Pattern）

## 一句话
Adapter 将既有实现的接口转换为客户端期望的接口，使接口不兼容的组件可以协同工作。

## 严格定义
设目标接口为 $I_t$，既有实现提供源接口 $I_s$。适配器 $A$ 实现 $I_t$，并通过组合或继承持有/获得 $I_s$ 的实现，使得对任一 $m\\in I_t$，$A.m$ 可被定义为到 $I_s$ 操作序列的映射（包含参数/返回值的转换规则）。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `Target`：目标接口
  - `Adaptee`：源接口/既有实现
  - `Adapter`：实现 Target 并映射到 Adaptee
- 约束：
  - 映射关系需要定义：参数、返回值、异常语义与默认值策略。

## 常用构造/操作（仅列出接口与符号）
- 组合式：`class Adapter implements Target { Adaptee a; }`
- 继承式：`class Adapter extends Adaptee implements Target`

## 关系：上级/下级/等价/特例/推广
- 相关：Facade（见 [Facade.md](Facade.md)）、Proxy（见 [Proxy.md](Proxy.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → Adapter → GoFDesignPatterns。

