---
type: concept
tags:
  - network
  - os
  - socket
  - accept
---

# accept（监听socket与已连接socket）

## 一句话
`accept()` 在监听 socket 上接入一个新连接，并返回一个新的“已连接 socket”（对应新的文件描述符 FD）。

## 严格定义
在类 Unix socket 接口中，应用通过 `socket/bind/listen` 创建并进入监听状态的 socket（监听 socket），该 socket 用于接收连接接入请求；当有连接可接入时，调用 `accept()` 会从内核维护的接入队列中取出一个连接并创建一个新的已连接 socket，返回其 FD。监听 socket 与已连接 socket 的职责边界是：前者承载“端口监听与连接接入”，后者承载“单条连接上的数据读写”。

## 接口：数据 + 约束
- 数据：
  - 监听 socket（listen socket）：绑定本地地址与端口，处于监听状态
  - 已连接 socket（connected socket）：绑定到具体的远端端点，承载读写
  - 新 FD：`accept()` 的返回值（进程作用域内）
- 约束：
  - 监听 socket 的 FD 与已连接 socket 的 FD 是不同的；`accept()` 不会把监听 socket “变成”已连接 socket，而是派生出新的 socket/FD。

## 常用构造/操作（仅列出接口与符号）
- `socket()` / `bind()` / `listen()` / `accept()`
- Java NIO 对应：`ServerSocketChannel.accept()` → `SocketChannel`

## 关系：上级/下级/等价/特例/推广
- 上级：TCP 连接标识（五元组）（见 [../transport/tcp/TCP连接标识（五元组）.md](../transport/tcp/TCP连接标识（五元组）.md)）
- 相关：文件描述符（FD）（见 [文件描述符（FD）.md](文件描述符（FD）.md)）
- 相关：Tomcat NIO 端点流程（Acceptor/Poller/Executor）（见 [../../springboot/modules/web/server/tomcat/class/threading/README.md](../../springboot/modules/web/server/tomcat/class/threading/README.md)）

## 把新概念挂回框架（多级索引轨迹）
网络 → os → accept（监听socket与已连接socket）。

