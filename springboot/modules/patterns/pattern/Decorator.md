---
title: Decorator（装饰）
date: "2026-01-29"
categories:
  - springboot
description: 类型：模式（Pattern）
---
# Decorator（装饰）

> **类型**：模式（Pattern）

## 一句话
Decorator 通过包装对象并在调用转发前后追加行为，实现对对象功能的动态扩展而不改变其接口。

## 严格定义
设组件接口为 $C$，被装饰对象为 $x\\in C$。装饰器 $d\\in C$ 持有 `wrap: C` 并对任一 $op\\in C$ 定义：
$$
d.op() = before();\\ wrap.op();\\ after()
$$
其中 `before/after` 是装饰器新增行为；装饰器与被装饰对象共享接口以保持可替换性。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `Component` / `ConcreteComponent`
  - `Decorator(wrap: Component)` / `ConcreteDecorator`
- 约束：
  - 追加行为的顺序由装饰链的嵌套顺序决定；副作用与异常语义需要定义。

## 常用构造/操作（仅列出接口与符号）
- 包装：`new Decorator(component)`
- 链：`new D2(new D1(component))`

## 关系：上级/下级/等价/特例/推广
- 相关：Proxy（见 [Proxy.md](Proxy.md)）、Adapter（见 [Adapter.md](Adapter.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → Decorator → GoFDesignPatterns。

