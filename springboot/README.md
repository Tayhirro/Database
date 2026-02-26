---
title: springboot（总入口）
date: "2026-01-20"
categories:
  - springboot
tags:
  - springboot/index
  - moc
description: 用法：把本页当作入口，在 Obsidian 打开"本地关系图（Local graph）"，深度设为 2～3，用索引与概念图把整套内容串起来。
type: index
---
# springboot（总入口）

> 用法：把本页当作入口，在 Obsidian 打开"本地关系图（Local graph）"，深度设为 2～3，用索引与概念图把整套内容串起来。

## 导航
- 索引（MOC）：[索引.md](索引.md)
- 概念图（跨模块）：[概念图.md](概念图.md)

## 主题入口
- 启动流程（从 `SpringApplication.run` 到 ready）：[flows/启动流程.md](flows/启动流程.md)
- 运行全链路（从启动到结束）：[flows/运行全链路.md](flows/运行全链路.md)

## 目录结构（入口 → 索引 → 概念图 → 条目）
- `springboot/README.md`：入口与导航（本页）
- `springboot/索引.md`：按 Flow/Class/Interface/Mechanism 分类的术语入口
- `springboot/概念图.md`：跨模块关系边（只放"上级→下级/特例"）
- `springboot/flows/`：流程页（以 `SpringApplication.run` 时间线组织）
- `springboot/modules/`：概念页（按领域分目录，并在领域下按 type 二级归档：`class/`、`interface/`、`mechanism/`、`module/`、`pattern/`）
- `springboot/jvm/`：JVM 运行态条目（线程等运行态结构）

## 新概念的关系抽取维度（类/接口/机制）
- 类型：Class / Interface / Mechanism / Flow
- 归属：Spring Boot / Spring Framework
- 时间点：位于 `SpringApplication.run` 的哪个阶段（见 [flows/启动流程.md](flows/启动流程.md)）
- 关系边：创建/持有 → 调用 → 发布/订阅（只记录可检索的对象关系）
- 扩展面：发现/注册方式（例如 `spring.factories`、编程式注册、容器 Bean）
- 版本范围：目标 Boot 版本（只记录接口签名与阶段边界的差异点）

## 参考
- `spring.factories` 机制与排查：`HMDP-Redis/docs/spring-factories-mechanism.md`
- 真实源码对照（Boot 逐段走读）：`study/real_springboot_init_analysis.md`
