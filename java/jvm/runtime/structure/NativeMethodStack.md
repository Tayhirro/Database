---
title: NativeMethodStack（本地方法栈）
date: "2026-02-02"
categories:
  - java
tags:
  - java/jvm
  - jvm
  - runtime
  - structure
  - native
  - stack
description: 本地方法栈（Native Method Stack）是 JVM 为执行本地方法（native）所使用的线程栈/调用栈抽象。
type: concept
---
# NativeMethodStack（本地方法栈）

## 一句话
本地方法栈（Native Method Stack）是 JVM 为执行本地方法（native）所使用的线程栈/调用栈抽象。

## 严格定义
当线程执行 native 方法时，JVM 会进入本地调用路径；本地方法栈用于描述该路径上的调用帧与相关数据的存放边界。其具体是否与虚拟机栈分离、以及栈帧结构与可见性由 JVM 实现决定（实现相关）。

## 接口：数据 + 约束
- 数据：
  - native 调用帧（实现相关）
- 输入：
  - native 方法调用与返回
- 输出：
  - native 路径上的执行上下文（实现相关）
- 约束：
  - 本地代码持有的引用可能作为 GC Roots 的来源之一（例如 JNI 引用，见 [../../gc/mechanism/GCRoots.md](../../gc/mechanism/GCRoots.md)）。

## 常用构造/操作（仅列出接口与符号）
- JNI：见 [../../native/README.md](../../native/README.md)

## 关系：上级/下级/等价/特例/推广
- 上级：Runtime / structure（运行时数据区结构）。
- 相关：GC Roots（JNI 引用，见 [../../gc/mechanism/GCRoots.md](../../gc/mechanism/GCRoots.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → runtime → structure → NativeMethodStack

