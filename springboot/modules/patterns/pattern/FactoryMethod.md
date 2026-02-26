---
title: FactoryMethod（工厂方法）
date: "2026-01-29"
categories:
  - springboot
description: 类型：模式（Pattern）
---
# FactoryMethod（工厂方法）

> **类型**：模式（Pattern）

## 一句话
Factory Method 在抽象创建者中定义创建产品的接口，并把创建具体产品的选择延迟到子类或具体实现中，从而解耦产品使用逻辑与实例化逻辑。

## 严格定义
设产品抽象为 $P$。工厂方法模式要求创建者抽象 `Creator` 定义创建操作 $factoryMethod(): P$，并在其业务流程中通过该创建操作获得产品实例；具体创建者 `ConcreteCreator` 覆写 $factoryMethod$，返回某个具体产品 $p\\in P$。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `Creator`：定义 `factoryMethod()` 与使用产品的流程
  - `ConcreteCreator`：实现创建选择
  - `Product` / `ConcreteProduct`
- 约束：
  - 客户端对 `Product` 抽象编程；创建选择不外泄到客户端调用点。

## 常用构造/操作（仅列出接口与符号）
- 创建：`factoryMethod(): Product`
- 使用：`operation()` 内部调用 `factoryMethod()`

## 关系：上级/下级/等价/特例/推广
- 相关：Abstract Factory（见 [AbstractFactory.md](AbstractFactory.md)）、Prototype（见 [Prototype.md](Prototype.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → FactoryMethod → GoFDesignPatterns。

