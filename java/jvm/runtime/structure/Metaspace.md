---
title: Metaspace（元空间）
date: "2026-02-02"
categories:
  - java
tags:
  - java/jvm
  - jvm
  - runtime
  - structure
  - metaspace
description: 元空间（Metaspace）是 HotSpot JDK 8+ 中承载主要类元数据的方法区实现形态。
type: concept
---
# Metaspace（元空间）

## 一句话
元空间（Metaspace）是 HotSpot JDK 8+ 中承载主要类元数据的方法区实现形态。

## 严格定义
在 HotSpot（JDK 8+）中，元空间用于存放类元数据（类的结构描述、方法元信息、运行时常量池等）的主要部分，并与类加载器生命周期关联以支持类卸载；其容量与增长策略受 JVM 参数与实现策略约束（实现相关）。

## 接口：数据 + 约束
- 数据：
  - 类元数据集合（按类加载器维度组织，具体项实现相关）
- 输入：
  - 类加载/链接产生的元数据分配
- 输出：
  - 运行态对元数据的查询与使用（解析、反射、调用分派等）
- 约束：
  - 元空间属于方法区的实现形态之一（见 [MethodArea.md](MethodArea.md)）；其回收与类卸载相关（实现相关）。

## 常用构造/操作（仅列出接口与符号）
- 参数：`-XX:MaxMetaspaceSize`（实现相关；见调优参数目录）

## 关系：上级/下级/等价/特例/推广
- 上级：MethodArea（见 [MethodArea.md](MethodArea.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → runtime → structure → Metaspace

