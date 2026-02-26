---
title: HandlerMethodArgumentResolver（MVC 方法参数解析器接口）
date: "2026-02-02"
categories:
  - springboot
description: 类型：接口（Interface）
---
# HandlerMethodArgumentResolver（MVC 方法参数解析器接口）

> **类型**：接口（Interface）

## 一句话
`HandlerMethodArgumentResolver` 定义了 Spring Web MVC 在注解控制器方法调用前，如何将一次请求解析为方法参数值的扩展边界。

## 严格定义
在 Spring Web MVC 中，`org.springframework.web.method.support.HandlerMethodArgumentResolver` 用于支持“基于 handler method（控制器方法）”的参数绑定：当 `HandlerAdapter` 选择到以 `HandlerMethod` 作为 handler 的执行路径时，会遍历一组参数解析器，选择 `supportsParameter(...)` 为 true 的解析器来为方法参数产生实际值（解析细节与参数类型、注解、媒体类型等相关，实现相关）。

## 接口：数据 + 约束
- 输入：
  - `MethodParameter`（方法形参描述）
  - `NativeWebRequest` / `HttpServletRequest`（请求视图）
  - `WebDataBinderFactory` / `ModelAndViewContainer`（实现相关）
- 输出：
  - 形参对应的实参值 `Object`（可为 null）
- 约束：
  - 参数解析通常只负责“把请求映射为参数值”；对象校验、转换与绑定协作组件由具体实现与配置决定（实现相关）。

## 常用构造/操作（仅列出接口与符号）
- `supportsParameter(parameter): boolean`
- `resolveArgument(parameter, mavContainer, webRequest, binderFactory): Object`

## 关系：上级/下级/等价/特例/推广
- 上级：`HandlerAdapter`（在 handler method 执行路径中使用，见 [HandlerAdapter.md](HandlerAdapter.md)）。
- 相关：`HttpMessageConverter`（请求体读取与对象反序列化常由消息转换器协作完成，见 [HttpMessageConverter.md](HttpMessageConverter.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → interface → HandlerMethodArgumentResolver。

