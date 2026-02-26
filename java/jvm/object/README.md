---
title: 对象模型（Object Model）
date: "2026-02-01"
categories:
  - java
description: "导航：jvm/README.md | 索引.md"
---
# 对象模型（Object Model）

导航：[jvm/README.md](../README.md) | [索引.md](索引.md)

本目录包含 JVM 对象内存布局与访问机制。

---

## 条目列表

- [ObjectLayout](ObjectLayout.md)：对象内存布局
- [ObjectHeader](ObjectHeader.md)：对象头
- [MarkWord](MarkWord.md)：Mark Word（哈希码、锁状态、GC 年龄）
- [KlassPointer](KlassPointer.md)：类型指针
- [ObjectCreation](ObjectCreation.md)：对象创建过程
- [ObjectAccess](ObjectAccess.md)：对象访问定位（句柄/直接指针）

---

## 关系

- 上级：[JVM](../README.md)
- 相关：[Heap](../runtime/structure/Heap.md)（对象存储位置）
- 相关：[GC](../gc/README.md)（GC 与对象头交互）
- 相关：[Synchronized](../memory/Synchronized.md)（锁状态存于 MarkWord）
