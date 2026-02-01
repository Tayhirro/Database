---
type: concept
tags:
  - network
  - os
  - epoll
  - io-multiplexing
---

# epoll（I/O多路复用）

## 一句话
epoll 是 Linux 的 I/O 多路复用接口：在一个等待点上监视多个文件描述符（FD）的就绪事件，并返回就绪集合。

## 严格定义
epoll 通过“epoll 实例 + 被监视的 FD 集合 + 事件类型”的结构，允许应用在单个线程中等待多个 I/O 对象的就绪事件。应用将 FD 注册到 epoll 实例，随后调用等待接口获得就绪事件集合；就绪事件通常包含“哪个 FD 就绪”与“就绪的事件类型（可读/可写等）”。

## 接口：数据 + 约束
- 数据（语义级别）：
  - epoll 实例（由 `epoll_create1` 创建；概念级）
  - 监视集合：$\{fd_i\}$
  - 事件类型（readable/writable 等；概念级）
- 约束：
  - epoll 以 FD 作为监视对象；FD 的作用域为“进程的 FD 表”（见 [文件描述符（FD）.md](文件描述符（FD）.md)）。

## 常用构造/操作（仅列出接口与符号）
- `epoll_create1` / `epoll_ctl` / `epoll_wait`

## 关系：上级/下级/等价/特例/推广
- 相关：Selector（Java NIO 多路复用）通常映射到底层的 I/O 多路复用实现（见 [../java/Selector（JavaNIO多路复用）.md](../java/Selector（JavaNIO多路复用）.md)）

## 把新概念挂回框架（多级索引轨迹）
网络 → os → epoll（I/O多路复用）。

