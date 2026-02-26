---
title: HttpMessageConverter（HTTP 消息转换器）
date: "2026-02-02"
categories:
  - springboot
description: 类型：接口（Interface）
---
# HttpMessageConverter（HTTP 消息转换器）

> **类型**：接口（Interface）

## 一句话
`HttpMessageConverter` 定义了 Spring 将“对象”与 HTTP 消息体（request/response body）在特定媒体类型（MediaType）下相互转换的边界。

## 严格定义
在 Spring 中，`org.springframework.http.converter.HttpMessageConverter<T>` 描述了对某些 Java 类型与某些媒体类型的读写能力：在 MVC 的 REST 场景中，控制器方法返回值可通过消息转换器序列化写入 `HttpServletResponse`；请求体也可通过消息转换器反序列化为 Java 对象（具体触发点与调用链取决于 MVC 组件配置与实现）。

## 继承链（接口链 / 实现链）
- 接口链：`HttpMessageConverter`（无上级接口）。
- 常见实现（实现选择与依赖相关）：
  - JSON：`MappingJackson2HttpMessageConverter`
  - 字符串：`StringHttpMessageConverter`
  - 表单：`FormHttpMessageConverter`

## 接口：数据 + 约束
- 输入：
  - `HttpInputMessage` / `HttpOutputMessage`（请求体/响应体抽象）
  - `clazz: Class<T>` / `MediaType`
- 输出：
  - 反序列化后的对象 `T` 或写入后的消息体（副作用）
- 约束：
  - 使用哪个转换器由“可读/可写能力 + 媒体类型协商”等规则决定（实现相关）。

## 常用构造/操作（仅列出接口与符号）
- `canRead(clazz, mediaType)` / `read(clazz, inputMessage)`
- `canWrite(clazz, mediaType)` / `write(t, mediaType, outputMessage)`
- `getSupportedMediaTypes()`

## 关系：上级/下级/等价/特例/推广
- 上级：Spring Web（请求体/响应体的对象化边界）。
- 相关：`DispatcherServlet`（MVC 入口，见 [../class/DispatcherServlet.md](../class/DispatcherServlet.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → interface → HttpMessageConverter。

