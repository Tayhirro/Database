---
type: index
tags:
  - springboot/web
  - tomcat
  - threading
  - moc
---

# 端点线程角色（Tomcat / NIO）

> 本目录用于描述 Tomcat endpoint 在运行态的线程角色分工（概念级），以便与 `AbstractEndpoint` 的启动与执行器模型对齐。

| 概念 | 一句话 | 入口 |
| --- | --- | --- |
| Acceptor | 接入线程角色：接收 TCP 连接并交给 I/O 轮询机制管理 | [Acceptor.md](Acceptor.md) |
| Poller | 轮询线程角色：驱动 `Selector` 并将 I/O 就绪事件分发为任务 | [Poller.md](Poller.md) |
| Executor | 执行器：承载请求处理任务的 worker 线程池抽象（内部或外部注入） | [Executor.md](Executor.md) |

## 关系（概念级）
- `AbstractEndpoint` → `Acceptor`
- `AbstractEndpoint` → `Poller`
- `AbstractEndpoint` → `Executor`

