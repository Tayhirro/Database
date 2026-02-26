---
title: CoyoteAdapter（Coyote→Catalina 适配器）
date: "2026-02-02"
categories:
  - springboot
tags:
  - springboot/web
  - tomcat
  - catalina
  - coyote
description: 类型：类（Class）
type: class
---
# CoyoteAdapter（Coyote→Catalina 适配器）

> **类型**：类（Class）

## 一句话
`CoyoteAdapter` 是 Tomcat 将 Coyote 层请求/响应（`org.apache.coyote.Request/Response`）适配为 Catalina/Servlet 处理链路输入输出的适配器。

## 严格定义
在 Tomcat 内部结构中，连接器的协议栈（Coyote）负责 I/O 与协议解析，但 Servlet 容器（Catalina）负责 `javax.servlet` 语义的请求分发与应用调用。`org.apache.catalina.connector.CoyoteAdapter` 处在两者之间：它接收已被 `Processor` 填充的 Coyote 请求/响应对象，创建或复用 Catalina 侧的 `org.apache.catalina.connector.Request/Response` 封装，并把处理流程推进到容器管线与最终的 `Servlet.service(...)` 调用。

## 继承链（接口链 / 实现链）
- 继承链：`java.lang.Object` → `CoyoteAdapter`。

## 接口：数据 + 约束
- 数据（语义级别；字段名可能随 Tomcat 版本变化）：
  - `connector: org.apache.catalina.connector.Connector`（连接器引用，用于访问容器与配置）
  - Catalina 请求/响应对象池或复用结构（存在性依实现变化）
- 输入：
  - `org.apache.coyote.Request` / `org.apache.coyote.Response`
- 输出：
  - `org.apache.catalina.connector.Request` / `org.apache.catalina.connector.Response`（Servlet 语义视图）
  - 容器处理链路的推进（副作用）
- 约束：
  - 适配器不负责协议解析；解析由 `Processor` 完成（见 [Http11Processor.md](Http11Processor.md)）。

## 常用构造/操作（仅列出接口与符号）
- 适配入口：`service(coyoteRequest, coyoteResponse)`（方法名与签名依版本可能不同）

## 关系：上级/下级/等价/特例/推广
- 上游：`Processor`（见 [../interface/Processor.md](../interface/Processor.md)）。
- 下游：Catalina 请求对象（见 [CatalinaRequest.md](CatalinaRequest.md)）。
- 相关：Tomcat 组件模型（见 [../mechanism/TomcatComponentModel.md](../mechanism/TomcatComponentModel.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → CoyoteAdapter。

