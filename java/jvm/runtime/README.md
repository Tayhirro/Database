# 运行时（Runtime）

导航：[jvm/README.md](../README.md) | [索引.md](索引.md)

本目录包含 JVM 运行时数据区与运行态线程。

---

## 子目录

| 目录 | 说明 |
|------|------|
| [structure/](structure/) | 运行时数据区结构 |
| [threading/](threading/) | JVM 运行态线程 |

---

## 条目列表

### 内存结构（structure/）
- [Heap](structure/Heap.md)：堆，存放对象实例
- [MethodArea](structure/MethodArea.md)：方法区，存放类元信息
- [Metaspace](structure/Metaspace.md)：元空间（JDK 8+ 替代永久代）
- [JVMStack](structure/JVMStack.md)：虚拟机栈，线程私有
- [NativeMethodStack](structure/NativeMethodStack.md)：本地方法栈
- [ProgramCounter](structure/ProgramCounter.md)：程序计数器
- [DirectMemory](structure/DirectMemory.md)：直接内存（堆外）

### 运行态线程（threading/）
- [JvmRuntimeThreads](threading/JvmRuntimeThreads.md)：JVM 运行态线程概述
- [GCThreads](threading/GCThreads.md)：GC 线程
- [CompilerThreads](threading/CompilerThreads.md)：JIT 编译器线程
- [ReferenceHandlerThread](threading/ReferenceHandlerThread.md)：引用处理线程
- [FinalizerThread](threading/FinalizerThread.md)：终结器线程
- [SignalDispatcherThread](threading/SignalDispatcherThread.md)：信号分发线程

---

## 关系

- 上级：[JVM](../README.md)
- 相关：[GC](../gc/README.md)（GC 线程与堆内存）
- 相关：[Execution](../execution/README.md)（编译器线程与 JIT）
