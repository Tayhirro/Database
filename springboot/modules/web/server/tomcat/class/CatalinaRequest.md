---
title: CatalinaRequest（org.apache.catalina.connector.Request）
date: "2026-02-02"
categories:
  - springboot
tags:
  - springboot/web
  - tomcat
  - catalina
  - servlet
description: 类型：类（Class）
type: class
---
# CatalinaRequest（org.apache.catalina.connector.Request）

> **类型**：类（Class）

## 一句话
`org.apache.catalina.connector.Request` 是 Tomcat Catalina 侧的 Servlet 请求封装：实现/提供 `javax.servlet` 请求视图，并持有对底层 Coyote 请求与输入流的桥接。

## 严格定义
在 Tomcat 内部，Coyote 层负责协议解析并形成 `org.apache.coyote.Request`；Catalina 层负责 Servlet 容器语义（管线、Valve、Wrapper、Servlet 调用）。`org.apache.catalina.connector.Request` 位于两者交界处：它在适配阶段被创建或复用，持有对 Coyote 请求的引用，并提供 `ServletRequest/HttpServletRequest` 语义方法，使上层容器与框架（例如 Spring MVC）通过标准接口读取请求参数、头部与请求体输入流。

## 继承链（接口链 / 实现链）
- 继承链：`java.lang.Object` → `org.apache.catalina.connector.Request` →（实现 `ServletRequest/HttpServletRequest` 的语义层次）。

## 接口：数据 + 约束
- 数据（语义级别；字段名可能随 Tomcat 版本变化）：
  - `coyoteRequest: org.apache.coyote.Request`（底层请求数据来源，见 [CoyoteRequest.md](CoyoteRequest.md)）
  - 输入流桥接（例如 `CoyoteInputStream` 的语义）：`getInputStream()` 从底层缓冲读取请求体字节
  - `connector` 引用：用于访问连接器配置与容器信息（存在性依实现变化）
- 输入：
  - 来自 `CoyoteAdapter` 的适配填充
- 输出：
  - `ServletRequest/HttpServletRequest` 的标准语义视图
- 约束：
  - 输入流中的数据是字节；字符解码为 Java `String` 的步骤通常在上层框架的消息转换/参数绑定阶段发生（例如 Spring MVC 的 `HttpMessageConverter`）。

## 常用构造/操作（仅列出接口与符号）
- 读取输入流：`getInputStream()`
- 读取请求元数据：`getMethod()` / `getRequestURI()` / `getHeader(name)` 等

## 关系：上级/下级/等价/特例/推广
- 上游：`CoyoteAdapter`（见 [CoyoteAdapter.md](CoyoteAdapter.md)）。
- 下游：Servlet 容器管线与 servlet 调用（机制见 [../mechanism/TomcatComponentModel.md](../mechanism/TomcatComponentModel.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → CatalinaRequest。

