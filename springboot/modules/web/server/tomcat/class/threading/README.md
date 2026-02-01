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

| 概念           | 一句话                                                | 入口                               |
| ------------ | -------------------------------------------------- | -------------------------------- |
| Acceptor     | 接入线程角色：接收 TCP 连接并交给 I/O 轮询机制管理                     | [Acceptor.md](Acceptor.md)       |
| Poller       | 轮询线程角色：驱动 `Selector` 并将 I/O 就绪事件分发为任务              | [Poller.md](Poller.md)           |
| Executor     | 执行器：承载请求处理任务的 worker 线程池抽象（内部或外部注入）                | [Executor.md](Executor.md)       |
| NIO Channels | `ServerSocketChannel`/`SocketChannel` 的职责区分与连接接入语义 | [NioChannels.md](NioChannels.md) |

## 关系（概念级）
- `AbstractEndpoint` → `Acceptor`
- `AbstractEndpoint` → `Poller`
- `AbstractEndpoint` → `Executor`

## 相关（OS/TCP 对齐）
- TCP 连接标识（五元组）：见 [../../../../../../../网络/transport/tcp/TCP连接标识（五元组）.md](../../../../../../../网络/transport/tcp/TCP连接标识（五元组）.md)
- accept（监听 socket → 已连接 socket/新 FD）：见 [../../../../../../../网络/os/accept（监听socket与已连接socket）.md](../../../../../../../网络/os/accept（监听socket与已连接socket）.md)
- 文件描述符（FD）：见 [../../../../../../../网络/os/文件描述符（FD）.md](../../../../../../../网络/os/文件描述符（FD）.md)
- epoll（Linux I/O 多路复用）：见 [../../../../../../../网络/os/epoll（I-O多路复用）.md](../../../../../../../网络/os/epoll（I-O多路复用）.md)
- Selector（Java NIO 多路复用）：见 [../../../../../../../网络/java/Selector（JavaNIO多路复用）.md](../../../../../../../网络/java/Selector（JavaNIO多路复用）.md)

## 流程（以 `http-nio-8080` 为例：从 accept 到 Servlet）

> 说明：以下为概念级时间线，用于对齐线程角色与分层职责；具体类名/方法名会随 Tomcat 版本与端点实现（NIO/NIO2/APR）变化。

### 0) 触发前提（启动链路）
`Connector` → `ProtocolHandler` → `AbstractProtocol` → `AbstractEndpoint.start()`（见 [../AbstractEndpoint.md](../AbstractEndpoint.md)）。

### 1) 接入（Acceptor）
1. 客户端对 `:8080` 发起 TCP connect。
2. `Acceptor` 线程在监听通道上执行 accept（见 [Acceptor.md](Acceptor.md)）：
   - `ServerSocketChannel`（监听端口的通道；关键属性：local address、blocking mode、socket options（概念级））
   - `ServerSocketChannel.accept()` → `SocketChannel`（单条连接通道；关键属性：local/remote address、connected/closed、blocking mode、socket options（概念级））
3. Acceptor 对 `SocketChannel` 做基础初始化（例如切换为非阻塞），并把连接移交给 I/O 轮询体系（提交到 Poller 侧注册）。

### 2) 轮询与分发（Poller / Selector）
1. `Poller` 线程持有并驱动 `Selector`（见 [Poller.md](Poller.md)）。
2. Poller 将 `SocketChannel` 注册到 `Selector` 并得到 `SelectionKey`：
   - `SocketChannel.register(selector, ops, attachment)` → `SelectionKey`（关键内容：interest ops / ready ops / attachment（概念级））
3. Poller 调用 `select()` 获得就绪的 `SelectionKey` 后，将“就绪连接”封装为可执行任务（概念级）并投递到 `Executor`。

### 3) 执行（Executor / worker）
1. `Executor`（worker 线程池）接收任务并在 worker 线程中执行（见 [Executor.md](Executor.md)）。
2. worker 线程完成请求处理阶段的工作（概念级）：
   - 从 socket 读取字节并解析 HTTP（请求行/headers/body）
   - 将请求推进到 Servlet 容器处理链路（例如通过适配器进入 `Engine/Host/Context/Wrapper` 的管线模型；见 [../../mechanism/TomcatComponentModel.md](../../mechanism/TomcatComponentModel.md)）
   - 生成响应并写回 socket

### 4) Keep-Alive 与连接回收（概念级）
1. 若连接保持 keep-alive，连接在一次请求处理完成后返回到 Poller/Selector 的监管，等待下一次就绪事件。
2. 若连接关闭或超时，端点释放连接相关资源并从轮询体系中移除。
