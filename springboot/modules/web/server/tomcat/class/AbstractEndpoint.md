---
type: class
tags:
  - springboot/web
  - tomcat
---

# AbstractEndpoint（Tomcat 网络端点抽象）

> **类型**：类（Class）

## 一句话
`AbstractEndpoint` 是 Tomcat 对“端口监听、连接管理与 I/O 事件处理”的抽象端点：在 `start()` 后创建/调度运行态执行单元以接入连接并推进请求处理。

## 严格定义
在 Tomcat 的常见协议实现中，`ProtocolHandler.start()` 会启动一个 `AbstractEndpoint` 实例；端点负责绑定端口并组织连接接入、I/O 事件轮询与任务投递等过程。端点的具体实现决定了 accept/poll 与工作线程池（executor）的组织方式。

## 继承链（接口链 / 实现链）
- 继承链：`java.lang.Object` → `AbstractEndpoint` →（NIO/NIO2/APR 等具体端点实现）。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `executor: Executor | null`（请求处理工作线程池的抽象；是否存在与类型由实现决定）
  - 运行态线程与调度器（accept/poll 等角色）
- 输入：
  - `start()`：启动端点（绑定端口、启动运行态执行单元）
  - `stop()`：停止端点并释放资源
- 输出：
  - 端口监听与连接/I/O 管理能力的启动/停止（副作用）
- 约束：
  - 端点内部线程的数量与命名不属于稳定接口；本页只描述职责边界与与上层协议处理器的关系。

## 常用构造/操作（仅列出接口与符号）
- 生命周期：`start()` / `stop()` / `pause()` / `resume()`（存在性取决于具体实现）

## 关系：上级/下级/等价/特例/推广
- 上级：`AbstractProtocol`（见 [AbstractProtocol.md](AbstractProtocol.md)）。
- 相关：Tomcat 线程与执行器模型（见 [../mechanism/TomcatThreadingAndExecutors.md](../mechanism/TomcatThreadingAndExecutors.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → AbstractEndpoint。

