---
type: concept
tags:
  - springboot/web
  - tomcat
  - threading
  - nio
---

# NIO Channels（ServerSocketChannel / SocketChannel）

## 一句话
`ServerSocketChannel` 表达“监听端口并接入新连接”，`SocketChannel` 表达“单条已建立连接的读写通道”，两者在端点线程模型中分别对应 accept 阶段与后续 I/O 处理阶段的数据载体。

## 严格定义
- `java.nio.channels.ServerSocketChannel` 是面向服务器监听 socket 的 `SelectableChannel`：其典型职责是绑定本地地址并通过 `accept()` 产生新连接；该新连接以 `SocketChannel` 的形式返回。
- `java.nio.channels.SocketChannel` 是面向单条 TCP 连接的 `SelectableChannel`：其典型职责是对该连接执行 `read/write`，并可注册到 `Selector` 以参与多路复用的就绪事件轮询。

## 接口：数据 + 约束
- `ServerSocketChannel`：
  - 关键属性（概念级）：本地监听地址（local address）、是否阻塞（blocking mode）、socket 选项（例如 backlog/复用等的配置入口）
  - 输入：连接接入事件
  - 输出：`SocketChannel`（新连接通道）
  - 约束：不承载某条已建立连接的读写；其输出是“新连接的通道”
- `SocketChannel`：
  - 关键属性（概念级）：本地/远端地址（local/remote address）、连接状态（connected/closed）、是否阻塞（blocking mode）、socket 选项、与 `Selector` 的关联关系（通过 `SelectionKey` 表达）
  - 输入：读/写操作与就绪事件
  - 输出：字节流的读写效果（副作用）
  - 约束：端点实现通常将其切换到非阻塞模式以参与 selector 轮询

## 常用构造/操作（仅列出接口与符号）
- `ServerSocketChannel.bind(address)` / `accept()`
- `SocketChannel.configureBlocking(false)` / `read(ByteBuffer)` / `write(ByteBuffer)`
- `SocketChannel.register(selector, ops, attachment)` → `SelectionKey`

## 关系：上级/下级/等价/特例/推广
- `ServerSocketChannel.accept()` → `SocketChannel`
- `SocketChannel.register(...)` → `SelectionKey`（由 `Selector` 管理）
- 运行态角色：
  - `Acceptor` 典型输入/输出与上述两个 channel 对齐（见 [Acceptor.md](Acceptor.md)）
  - `Poller` 典型围绕 `Selector`/`SelectionKey` 运行（见 [Poller.md](Poller.md)）

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → threading → NioChannels。

