---
type: concept
tags:
  - java/jvm
  - jvm
  - runtime
  - structure
  - stack
---

# JVMStack（虚拟机栈）

## 一句话
虚拟机栈（JVM Stack）是每个线程私有的运行时数据区，用于存放该线程的栈帧（局部变量表、操作数栈、返回地址等）。

## 严格定义
对每个 Java 线程，JVM 维护一段线程私有栈空间；线程每次方法调用会创建一个栈帧并入栈，方法返回时栈帧出栈。栈帧包含局部变量与参数引用，因此线程栈上的引用是 GC Roots 的常见来源之一（见 [../../gc/mechanism/GCRoots.md](../../gc/mechanism/GCRoots.md)）。

## 接口：数据 + 约束
- 数据：
  - 栈帧序列（按调用深度组织）
  - 局部变量表、操作数栈、动态链接信息（实现相关）
- 输入：
  - 方法调用（入栈）/ 方法返回（出栈）
- 输出：
  - 当前执行上下文（局部变量与操作数）
- 约束：
  - 栈空间大小与溢出行为由实现与参数决定（例如 `StackOverflowError`）。

## 常用构造/操作（仅列出接口与符号）
- 线程与栈：见 [../threading/JvmRuntimeThreads.md](../threading/JvmRuntimeThreads.md)

## 关系：上级/下级/等价/特例/推广
- 上级：Runtime / structure（运行时数据区结构）。
- 相关：GC Roots（线程栈引用，见 [../../gc/mechanism/GCRoots.md](../../gc/mechanism/GCRoots.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → runtime → structure → JVMStack

