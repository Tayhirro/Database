---
title: JVM 知识库
date: "2026-02-01"
categories:
  - java
description: "导航：java/README.md | 索引.md | 概念图.md"
---
# JVM 知识库

导航：[java/README.md](../README.md) | [索引.md](索引.md) | [概念图.md](概念图.md)

本目录系统组织 Java 虚拟机（JVM）相关知识。

---

## 目录结构

| 分支 | 说明 | 入口 |
|------|------|------|
| runtime/ | 运行时数据区与线程 | [README](runtime/README.md) |
| classloading/ | 类加载机制 | [README](classloading/README.md) |
| execution/ | 执行引擎 | [README](execution/README.md) |
| gc/ | 垃圾回收 | [README](gc/README.md) |
| memory/ | 内存模型 | [README](memory/README.md) |
| object/ | 对象模型 | [README](object/README.md) |
| tuning/ | 性能调优 | [README](tuning/README.md) |
| native/ | 本地接口 | [README](native/README.md) |

---

## 快速导航

### 运行时
- 内存结构：[Heap](runtime/structure/Heap.md) | [MethodArea](runtime/structure/MethodArea.md) | [JVMStack](runtime/structure/JVMStack.md) | [ProgramCounter](runtime/structure/ProgramCounter.md)
- 线程：[JvmRuntimeThreads](runtime/threading/JvmRuntimeThreads.md) | [GCThreads](runtime/threading/GCThreads.md) | [CompilerThreads](runtime/threading/CompilerThreads.md)

### 类加载
- [ClassLoadingProcess](classloading/mechanism/ClassLoadingProcess.md) | [ParentDelegation](classloading/classloader/ParentDelegation.md)

### 执行引擎
- [Interpreter](execution/interpreter/Interpreter.md) | [JIT](execution/jit/JIT.md) | [TieredCompilation](execution/jit/TieredCompilation.md)

### 垃圾回收
- 机制：[GCRoots](gc/mechanism/GCRoots.md) | [SafePoint](gc/mechanism/SafePoint.md)
- 算法：[MarkSweep](gc/algorithm/MarkSweep.md) | [Copying](gc/algorithm/Copying.md) | [GenerationalGC](gc/algorithm/GenerationalGC.md)
- 收集器：[G1](gc/collector/G1.md) | [ZGC](gc/collector/ZGC.md) | [CMS](gc/collector/CMS.md)

### 内存模型
- [JMM](memory/JMM.md) | [HappensBefore](memory/HappensBefore.md) | [Volatile](memory/Volatile.md)

### 调优
- 工具：[jstack](tuning/tools/jstack.md) | [jmap](tuning/tools/jmap.md) | [jstat](tuning/tools/jstat.md)
