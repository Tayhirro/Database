---
title: Poller（Tomcat NIO 轮询线程角色）
date: "2026-02-01"
categories:
  - springboot
tags:
  - springboot/web
  - tomcat
  - threading
  - nio
description: Poller 是 Tomcat NIO 端点中负责驱动 I/O 多路复用（select/poll）并把就绪事件分发为可执行任务的运行态线程角色。
type: concept
---
# Poller（Tomcat NIO 轮询线程角色）

## 一句话
Poller 是 Tomcat NIO 端点中负责驱动 I/O 多路复用（select/poll）并把就绪事件分发为可执行任务的运行态线程角色。

## 严格定义
在 NIO 端点实现中，Poller 线程通常持有并驱动一个 `Selector`：将已接入的 `SocketChannel` 注册到 `Selector` 并获得 `SelectionKey`，周期性调用 `select()` 获取就绪的 `SelectionKey` 集合，将“可读/可写等就绪事件”转化为端点内部的处理任务，并将任务投递到端点的 `Executor`（worker 执行器）或等价的任务执行通道，从而把就绪连接推进到请求处理阶段。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `Selector`：Java NIO 多路复用器，用于在一个线程中等待并获取多个通道的 I/O 就绪事件
  - 事件集合：`SelectionKey`（就绪事件的标识载体，语义级）
  - 任务投递入口：`Executor.execute(Runnable)` 或等价机制
- 输入：
  - 已建立连接在读写层面的就绪事件（I/O readiness）
- 输出：
  - 被投递到执行器的处理任务（副作用）
- 约束：
  - Poller 的职责边界在“轮询 + 分发”；请求处理的计算与业务调用由执行器线程承担。

## 常用构造/操作（仅列出接口与符号）
- `Selector.select()`：等待并获取就绪事件（语义级）
- `Executor.execute(task)`：投递任务到 worker 执行器（语义级）

## 流程（概念级：Poller 的一次轮询循环）

### 输入来源（谁会把 channel 交给 Poller）
- 新连接接入：`Acceptor` 接收到 `SocketChannel` 后，把该 channel 提交到“待注册集合”，并触发 Poller 侧注册（见 [Acceptor.md](Acceptor.md)）。
- 既有连接回到轮询：worker 执行完一次请求处理后，如果连接处于 keep-alive 等待下一次请求的状态，会把该连接对应的 channel 重新提交到“待注册集合”，以便 Poller 继续监管其可读/可写就绪事件（再分发下一次处理任务）。
- 内部控制事件：例如关闭、超时处理等会导致 key/channel 被取消或修改其关注事件（interest ops）；实现细节依端点而定。

### 关键数据结构（概念级）
- `Selector`：等待就绪事件并维护已注册 channel 集合。
- `SelectionKey`：
  - `interest ops`：本次希望关注的事件类型（例如读/写）。
  - `ready ops`：本次 select 返回的就绪事件类型。
  - `attachment`：与该 key/channel 关联的连接状态对象（语义级；具体类型依实现）。
- “待注册集合”（register queue）：
  - 存放“需要注册/修改 interest ops 的 channel 或连接状态对象”的队列/集合（语义级；具体类型依实现）。

### 主循环（概念级时间线）
1. 处理待注册集合：
   - 将新接入或待继续轮询的 `SocketChannel` 注册到 `Selector`；
   - 或更新其 `SelectionKey.interestOps(...)`（例如从写关注切回读关注）。
2. 进入等待：
   - Poller 调用 `selector.select(timeout)`（或等价调用）等待就绪事件；
   - 若有新的注册请求到来，通常需要通过 `selector.wakeup()` 使 Poller 及时退出阻塞并处理待注册集合（实现细节）。
3. 取得就绪集合：
   - Poller 遍历 `selectedKeys`（就绪的 `SelectionKey` 集合）。
4. 事件分发：
   - 对每个 `SelectionKey`，根据 `ready ops` 判断“可读/可写等就绪事件”；
   - 将“该连接需要推进到下一阶段处理”的动作封装为任务，并投递到 `Executor` 执行。
5. 事件与状态更新（概念级）：
   - 清理本轮 `selectedKeys`；
   - 根据连接状态更新 `interest ops`（例如暂停读关注、等待写完成等）或取消 key（关闭连接等）。

### 输出（Poller 做到哪里为止）
Poller 的输出是“把就绪事件转化为待执行任务并投递到 `Executor`”；请求处理的计算与应用调用发生在 `Executor` 的 worker 线程中（见 [Executor.md](Executor.md)）。

## 关系：上级/下级/等价/特例/推广
- 上级：`AbstractEndpoint`（见 [../AbstractEndpoint.md](../AbstractEndpoint.md)）。
- 并列角色：`Acceptor`（见 [Acceptor.md](Acceptor.md)）、`Executor`（见 [Executor.md](Executor.md)）。
- OS 对齐：epoll 与 Selector 的映射（实现依赖）（见 [../../../../../../../网络/os/epoll（I-O多路复用）.md](../../../../../../../网络/os/epoll（I-O多路复用）.md)、[../../../../../../../网络/java/Selector（JavaNIO多路复用）.md](../../../../../../../网络/java/Selector（JavaNIO多路复用）.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → threading → Poller。
