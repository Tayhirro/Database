---
title: 文件描述符（FD）
date: "2026-02-01"
categories:
  - 网络
tags:
  - network
  - os
  - fd
description: 文件描述符（FD）是进程内用于引用内核对象（文件、socket 等）的整数句柄。
type: concept
---
# 文件描述符（FD）

## 一句话
文件描述符（FD）是进程内用于引用内核对象（文件、socket 等）的整数句柄。

## 严格定义
在类 Unix 操作系统的接口模型中，文件描述符是一个非负整数索引，用于在该进程的“打开文件/对象表”中定位一个条目；该条目进一步引用内核态的对象（例如文件、管道、socket 等）。对 socket 而言，FD 作为用户态可见的标识，使应用能够对某条连接执行 `read/write/close` 等系统调用，并用于 I/O 多路复用接口的监视对象。

## 接口：数据 + 约束
- 数据：
  - `fd: int`（进程作用域内的整数）
  - 内核对象引用（语义级）：文件/pipe/socket 等
- 约束：
  - FD 的唯一性作用域是“单进程的 FD 表”；不同进程中相同的 FD 数值不对应同一内核对象。

## 常用构造/操作（仅列出接口与符号）
- `close(fd)`：释放 FD 引用
- `dup/dup2`：复制 FD（共享或指向相同内核对象的语义依实现与接口约定）
- I/O 多路复用：`select/poll/epoll` 以 FD 作为监视对象（见 [epoll（I-O多路复用）.md](epoll（I-O多路复用）.md)）

## 关系：上级/下级/等价/特例/推广
- 相关：accept 产生新的已连接 socket（新 FD）（见 [accept（监听socket与已连接socket）.md](accept（监听socket与已连接socket）.md)）
- 相关：Selector（Java NIO 多路复用）对齐 OS 多路复用（见 [../java/Selector（JavaNIO多路复用）.md](../java/Selector（JavaNIO多路复用）.md)）

## 把新概念挂回框架（多级索引轨迹）
网络 → os → 文件描述符（FD）。

