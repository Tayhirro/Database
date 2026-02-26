---
title: Acceptor（Tomcat NIO 接入线程角色）
date: "2026-02-01"
categories:
  - springboot
tags:
  - springboot/web
  - tomcat
  - threading
description: Acceptor 是 Tomcat NIO 端点中负责接收（accept）新 TCP 连接并将连接交给后续 I/O 管理机制的运行态线程角色。
type: concept
---
# Acceptor（Tomcat NIO 接入线程角色）

## 一句话
Acceptor 是 Tomcat NIO 端点中负责接收（accept）新 TCP 连接并将连接交给后续 I/O 管理机制的运行态线程角色。

## 严格定义
在以 NIO 为代表的端点实现中，Acceptor 线程围绕“接入新连接”的循环运行：通过 `ServerSocketChannel.accept()` 接收连接并得到 `SocketChannel`，完成必要的通道初始化（例如配置阻塞/非阻塞与基础属性），并把新连接注册/提交到端点的 I/O 轮询与分发机制（例如交给 Poller/Selector 监管），使其进入后续读写事件处理流程。

## 接口：数据 + 约束
- 数据（语义级别）：
  - 监听 socket（例如 `ServerSocketChannel`）
  - 新建连接通道（例如 `SocketChannel`）
  - 端点的连接注册入口（例如注册到 Poller/Selector 的提交队列）
- 输入：
  - 新连接接入事件（TCP connect）
- 输出：
  - 已被端点接管并进入 I/O 轮询体系的连接（副作用）
- 约束：
  - Acceptor 不承担请求处理（HTTP 解析、Servlet 调用等）；其职责边界在“接入与移交”。

## 常用构造/操作（仅列出接口与符号）
- `accept()`：接收新连接（语义级）
- “注册/移交到轮询机制”：将连接提交给 Poller/Selector（语义级）

## 流程（概念级：Acceptor 的一次接入）
1. 监听通道接入：
   - `ServerSocketChannel.accept()` 产生一个新的 `SocketChannel`（见 [NioChannels.md](NioChannels.md)）。
2. 连接初始化（概念级）：
   - 对 `SocketChannel` 设置必要的 socket/channel 属性（例如将其置为非阻塞以便注册到 `Selector`）。
3. 移交到 Poller：
   - 将该 `SocketChannel` 与其连接状态对象提交到 Poller 的“待注册集合”（register queue，概念级）。
4. 触发 Poller 处理：
   - 若 Poller 当前阻塞在 `select()`，实现通常需要通过 `selector.wakeup()`（或等价机制）使 Poller 及时处理新的注册请求（实现细节）。

## 关系：上级/下级/等价/特例/推广
- 上级：`AbstractEndpoint`（见 [../AbstractEndpoint.md](../AbstractEndpoint.md)）。
- 并列角色：`Poller`（见 [Poller.md](Poller.md)）、`Executor`（见 [Executor.md](Executor.md)）。
- OS/TCP 对齐：`accept()` 与“监听 socket → 已连接 socket”的区分（见 [../../../../../../../网络/os/accept（监听socket与已连接socket）.md](../../../../../../../网络/os/accept（监听socket与已连接socket）.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → threading → Acceptor。
