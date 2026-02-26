---
title: Selector（Java NIO 多路复用）
date: "2026-02-01"
categories:
  - 网络
tags:
  - network
  - java
  - nio
  - selector
description: Selector 是 Java NIO 的多路复用抽象：在单线程中等待多个 channel 的 I/O 就绪事件，并以 SelectionKey 集合形式返回就绪结果。
type: concept
---
# Selector（Java NIO 多路复用）

## 一句话
`Selector` 是 Java NIO 的多路复用抽象：在单线程中等待多个 channel 的 I/O 就绪事件，并以 `SelectionKey` 集合形式返回就绪结果。

## 严格定义
在 Java NIO 中，`SelectableChannel`（例如 `SocketChannel`）可注册到 `Selector` 并指定关注的事件类型（interest ops）；调用方在 `select()`/`selectNow()` 上等待后，`Selector` 产生就绪键集合（ready keys），每个 `SelectionKey` 关联一个 channel，并携带 interest/ready 等事件状态与可选 attachment。JVM 对 `Selector` 的实现通常会映射到底层操作系统的 I/O 多路复用机制（例如 Linux epoll），但该映射属于实现细节。

## 接口：数据 + 约束
- 数据：
  - `Selector`
  - `SelectionKey`（包含 `interestOps/readyOps/attachment` 等语义字段）
  - `SelectableChannel`（例如 `SocketChannel`）
- 约束：
  - `SelectionKey` 的就绪状态以 `select` 调用周期更新；就绪集合是“本次轮询的结果”，并非连接的全量集合。

## 常用构造/操作（仅列出接口与符号）
- `channel.register(selector, ops, attachment)` → `SelectionKey`
- `selector.select()` / `selectNow()` / `wakeup()`

## 关系：上级/下级/等价/特例/推广
- 相关：epoll（I/O多路复用）（见 [../os/epoll（I-O多路复用）.md](../os/epoll（I-O多路复用）.md)）
- 相关：Tomcat Poller（NIO 轮询线程角色）（见 [../../springboot/modules/web/server/tomcat/class/threading/Poller.md](../../springboot/modules/web/server/tomcat/class/threading/Poller.md)）

## 把新概念挂回框架（多级索引轨迹）
网络 → java → nio → Selector。

