---
type: mechanism
tags:
  - springboot/jvm
  - jvm
  - threading
---

# JvmRuntimeThreads（JVM 运行态线程）

## 一句话
JVM 运行态线程描述的是：一个 Java 进程在运行期间除了应用代码创建的线程外，还会存在一组由 JVM 管理的内部服务线程，它们承担 GC、JIT、引用处理与信号分发等职责。

## 严格定义
在具体 JVM 实现（例如 HotSpot/OpenJDK）中，JVM 会在运行态创建并管理若干内部线程，用于完成运行时系统工作（垃圾回收、即时编译、引用队列处理、终结处理、信号分发等）。这些线程通常可被线程转储工具观测到（例如 `jstack` 输出中的线程名与栈）。

## 接口：数据 + 约束
- 数据（观测级）：
  - 线程名（thread name）
  - 是否守护线程（daemon flag）
  - 线程状态（thread state）
  - 栈帧与锁信息（stack/monitor info）
- 输入：
  - 运行态事件（GC 周期、热点编译请求、引用入队请求、终结任务、进程信号等）
- 输出：
  - 对运行时状态的推进（副作用）
- 约束：
  - 内部线程的集合、命名与栈形态依赖 JVM 实现与运行参数；本页仅描述职责边界与可观测维度。

## 常用构造/操作（仅列出接口与符号）
- 观测：`jstack <pid>`（线程转储）
- 观测（可选）：`jcmd <pid> Thread.print`（线程打印）

## 关系：上级/下级/等价/特例/推广
- 下级（JVM 内部服务线程）：
  - GC Threads（见 [GCThreads.md](GCThreads.md)）
  - CompilerThread (C1/C2)（见 [CompilerThreads.md](CompilerThreads.md)）
  - Reference Handler（见 [ReferenceHandlerThread.md](ReferenceHandlerThread.md)）
  - Finalizer（见 [FinalizerThread.md](FinalizerThread.md)）
  - Signal Dispatcher（见 [SignalDispatcherThread.md](SignalDispatcherThread.md)）
- 相关（同一进程内的应用线程示例）：
  - Tomcat 端点线程角色（Acceptor/Poller/Executor）：见 [../../modules/web/server/tomcat/class/threading/README.md](../../modules/web/server/tomcat/class/threading/README.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → jvm → mechanism → JvmRuntimeThreads。

