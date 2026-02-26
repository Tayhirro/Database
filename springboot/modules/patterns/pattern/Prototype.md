---
title: Prototype（原型）
date: "2026-01-29"
categories:
  - springboot
description: 类型：模式（Pattern）
---
# Prototype（原型）

> **类型**：模式（Pattern）

## 一句话
Prototype 通过复制既有对象（原型）来创建新对象，使创建过程不依赖具体类的构造细节，并允许以更换原型来改变可创建对象集合。

## 严格定义
设对象集合为 $\\mathcal{O}$，复制操作为 $clone: \\mathcal{O}\\to\\mathcal{O}$。原型模式以原型对象 $p\\in\\mathcal{O}$ 作为创建源，令新对象 $o=clone(p)$；复制等价关系（浅拷贝/深拷贝、共享引用等）作为复制语义的一部分需要被定义。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `Prototype`：定义复制操作
  - `ConcretePrototype`：实现复制语义
  - 原型注册表（可选）：`name -> prototype`
- 约束：
  - 复制语义需要明确（对象图、可变性、共享引用的处理）。

## 常用构造/操作（仅列出接口与符号）
- 复制：`clone()`
- 选择：`prototypeRegistry[name].clone()`

## 关系：上级/下级/等价/特例/推广
- 相关：Factory Method（见 [FactoryMethod.md](FactoryMethod.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → Prototype → GoFDesignPatterns。

