---
title: AbstractEndpoint（Tomcat 网络端点抽象）
date: "2026-01-31"
categories:
  - springboot
tags:
  - springboot/web
  - tomcat
description: 类型：类（Class）
type: class
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
  - accept/poll 等运行态线程（角色存在性与数量由具体端点实现决定）
  - 运行态标志（例如 `running/paused` 等；字段名随版本可能不同）
- 字段与状态（面向“线程/执行器”理解；字段名可能随 Tomcat 版本与端点实现变化）：
  - `executor`：对外暴露/可注入的工作线程池（worker executor），端点将请求处理任务投递到该 executor 执行
  - `internalExecutor`（常见命名）：当未配置外部 `executor` 时，端点在启动阶段创建并持有的内部线程池
  - `maxThreads/minSpareThreads`（常见命名）：与 worker 线程池容量相关的配置项；常用于内部线程池的创建参数或对线程池实现的适配
  - `acceptorThreadCount` 与 acceptor 线程集合（常见命名为 `acceptor*`）：负责接入新连接的专用线程/线程组
  - I/O 轮询线程集合（常见命名为 `poller*` / `selector*`）：负责 select/poll 并触发后续处理阶段（存在性取决于 NIO/NIO2/APR 等实现）
  - 线程创建参数（常见为 `namePrefix/threadPriority/daemon`）：用于创建 acceptor/poller/worker 等线程的线程工厂（ThreadFactory）参数
- 字段与状态（面向“连接与事件分发”理解；字段名可能随 Tomcat 版本与端点实现变化）：
  - `handler`（常见命名）：端点的连接事件回调接口，由协议处理器提供实现；端点将“可读/可写/关闭”等连接事件回调到该 handler，由其进一步转发给 `Processor`（语义级）
  - `socketWrapper`（语义级）：对底层 socket/channel 的读写包装对象；Poller/worker 通常围绕 socket wrapper 进行状态转换与任务分发（实现类名依版本）
  - 连接注册与映射结构：用于在“连接集合”与“可轮询/可处理集合”之间维护关联（实现细节依端点）
- 输入：
  - `start()`：启动端点（绑定端口、启动运行态执行单元）
  - `stop()`：停止端点并释放资源
- 输出：
  - 端口监听与连接/I/O 管理能力的启动/停止（副作用）
- 约束：
  - 端点内部线程的数量与命名不属于稳定接口；本页只描述职责边界与与上层协议处理器的关系。

## 常用构造/操作（仅列出接口与符号）
- 生命周期：`start()` / `stop()` / `pause()` / `resume()`（存在性取决于具体实现）

## 运行态模型（以 HTTP/1.1 NIO 为例）

### 启动链路（Boot → AbstractEndpoint.start）
在 embedded Tomcat（Servlet）形态下，可用概念级链路表达 `AbstractEndpoint.start()` 的触发来源：

[`TomcatWebServer`](../../../class/TomcatWebServer.md) → `Tomcat.start()` → [`Connector`](Connector.md).`startInternal()` → [`ProtocolHandler`](../interface/ProtocolHandler.md).`start()` → [`AbstractProtocol`](AbstractProtocol.md).`start()` → `AbstractEndpoint.start()`

其中 `AbstractProtocol.start()` 的典型实现会调用其持有的 `endpoint.start()`，从而触发端点的“绑定端口 + 启动运行态线程/执行器”。

### 线程角色（acceptor / poller / worker）
在 NIO 端点（例如 `NioEndpoint`）语义模型下，可将运行态执行单元按职责划分为三类角色：

- `Acceptor`（接入线程角色，见 [threading/Acceptor.md](threading/Acceptor.md)）：
  - 输入：TCP 连接请求
  - 行为：执行 accept（例如 `ServerSocketChannel.accept()`）并初始化新连接，然后把连接交给 I/O 轮询机制管理
- `Poller`（I/O 轮询线程角色，见 [threading/Poller.md](threading/Poller.md)）：
  - 输入：已建立连接的 I/O 就绪事件
  - 行为：执行 select/poll（例如 `Selector.select()`）并将“就绪连接”转换为待执行任务投递到 worker executor
- `Executor` / worker（请求处理执行器，见 [threading/Executor.md](threading/Executor.md)）：
  - 输入：由 Poller/端点生成的处理任务（例如读取字节、解析 HTTP、驱动 Servlet 调用、写回响应）
  - 行为：在 `Executor` 提供的线程中执行任务

线程名（观测级）通常携带连接器与端口信息；其格式属于实现细节，可用于运行态识别 acceptor/poller/worker 三类角色，但不构成稳定接口。

### 队列与上限（分层）
以下条目用于将“连接接入/连接数量/请求处理”三层的排队与上限区分开来（字段名与精确定义可能随 Tomcat 版本与 OS 实现变化）：

- `acceptCount`（OS backlog，接入层）：
  - 语义：由操作系统维护的“等待应用 accept 的连接请求队列”的容量上界
- `maxConnections`（连接上限，连接层）：
  - 语义：端点/协议处理器对“已建立连接数量”的上界约束；到达上限后，新连接接入与排队行为取决于实现与平台
- `maxThreads` / `minSpareThreads` / `maxQueueSize`（执行层）：
  - 语义：worker executor 的最大线程数、最小保活线程数、以及任务队列容量（队列语义取决于 executor 实现）
  - 位置：当端点使用内部线程池时，上述参数通常直接作用于该线程池；当端点使用外部注入的 `Executor` 时，上述语义由外部 executor 的实现决定

### Spring Boot 配置映射（Servlet / Tomcat）
以下映射只表达“属性名 → Tomcat 语义位置”的关系；是否落到 `AbstractEndpoint` 字段、以及落到内部线程池还是外部 executor，取决于实际协议与实现。

| Spring Boot 属性 | Tomcat 语义位置 | 作用对象（概念级） |
| --- | --- | --- |
| `server.tomcat.accept-count` | `acceptCount`（OS backlog） | 接入层队列上限 |
| `server.tomcat.max-connections` | `maxConnections`（连接上限） | 连接层上限 |
| `server.tomcat.threads.max` | `maxThreads`（worker 线程上限） | 执行层：worker executor |
| `server.tomcat.threads.min-spare` | `minSpareThreads`（保活线程） | 执行层：worker executor |
| `server.tomcat.threads.max-queue-capacity` | `maxQueueSize`（任务队列容量） | 执行层：worker executor |

前提（可选）：若应用启用“以其他执行器替代传统 worker 线程池”的模式（例如虚拟线程执行器或显式注入外部 executor），上述 threads.* 属性与底层执行器之间可能不再是一一对应关系。

## 可观测接口（运行态）
- 连接与线程计数：
  - `getConnectionCount()`
  - `getCurrentThreadCount()`
  - `getCurrentThreadsBusy()`
- 执行器视图：
  - `getExecutor()`

## 代码示例
### 读取 endpoint 的运行态线程计数（通过 AbstractProtocol 反射获取 endpoint）
前提：应用使用 embedded Tomcat；`Connector.getProtocolHandler()` 返回的具体类型为 `AbstractProtocol`。

```java
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.tomcat.util.net.AbstractEndpoint;

AbstractEndpoint<?, ?> endpointOf(Connector connector) throws Exception {
  ProtocolHandler handler = connector.getProtocolHandler();
  if (!(handler instanceof AbstractProtocol<?> protocol)) {
    return null;
  }
  Method m = AbstractProtocol.class.getDeclaredMethod("getEndpoint");
  m.setAccessible(true);
  return (AbstractEndpoint<?, ?>) m.invoke(protocol);
}

void readThreadCounters(Connector connector) throws Exception {
  AbstractEndpoint<?, ?> endpoint = endpointOf(connector);
  if (endpoint == null) {
    return;
  }
  int currentThreadCount = endpoint.getCurrentThreadCount();
  int currentThreadsBusy = endpoint.getCurrentThreadsBusy();
  long connectionCount = endpoint.getConnectionCount();
  System.out.println("currentThreadCount=" + currentThreadCount);
  System.out.println("currentThreadsBusy=" + currentThreadsBusy);
  System.out.println("connectionCount=" + connectionCount);

  Executor executor = endpoint.getExecutor();
  System.out.println("executor=" + executor);
  if (executor instanceof ThreadPoolExecutor tpe) {
    System.out.println("poolSize=" + tpe.getPoolSize());
    System.out.println("active=" + tpe.getActiveCount());
    System.out.println("core=" + tpe.getCorePoolSize());
    System.out.println("max=" + tpe.getMaximumPoolSize());
    System.out.println("queueSize=" + tpe.getQueue().size());
  }
}
```

## 关系：上级/下级/等价/特例/推广
- 上级：`AbstractProtocol`（见 [AbstractProtocol.md](AbstractProtocol.md)）。
- 相关：Tomcat 线程与执行器模型（见 [../mechanism/TomcatThreadingAndExecutors.md](../mechanism/TomcatThreadingAndExecutors.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → AbstractEndpoint。
