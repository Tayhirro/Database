---
title: ChainOfResponsibility（职责链）
date: "2026-01-29"
categories:
  - springboot
description: 类型：模式（Pattern）
---
# ChainOfResponsibility（职责链）

> **类型**：模式（Pattern）

## 一句话
Chain of Responsibility 将请求沿一条处理器链传递，使多个对象都有机会处理请求，并将请求发送者与处理者解耦。

## 严格定义
设处理器序列为 $H=\\langle h_1,\\dots,h_n\\rangle$，请求为 $r$。职责链要求每个处理器实现 `handle(r)`，并在处理时按约定决定是否将 $r$ 继续传递给后继处理器（例如：处理并终止、部分处理后继续、不处理直接继续）。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `Handler`
  - `ConcreteHandler`
  - 链结构：`next` 指针或 `List<Handler>`
- 约束：
  - 是否允许多个处理器对同一请求产生效果需要被定义；链的终止条件需要被定义。

## 常用构造/操作（仅列出接口与符号）
- 链接：`setNext(handler)` / `handlers: List<Handler>`
- 处理：`handle(request)` / `passToNext(request)`

## 关系：上级/下级/等价/特例/推广
- 相关：Command（见 [Command.md](Command.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → ChainOfResponsibility → GoFDesignPatterns。

