---
type: class
tags:
  - springboot/web
  - tomcat
  - coyote
  - http
---

# CoyoteRequest（org.apache.coyote.Request）

> **类型**：类（Class）

## 一句话
`org.apache.coyote.Request` 是 Tomcat Coyote 层的请求对象：承载 HTTP 方法、URI、头部与输入缓冲等协议解析结果，并作为 `Processor` 与适配器之间的中间表示。

## 严格定义
在 Tomcat 的 Coyote 协议栈中，`org.apache.coyote.Request` 是协议解析后的请求表示：`Processor` 将从连接读到的字节流按 HTTP/1.1 等协议解析后，填充到该对象（请求行、请求头、内容相关元数据、以及面向读取请求体的缓冲/输入通道）。之后 `CoyoteAdapter` 以该对象为输入，构造 Servlet 语义的请求视图并进入 Catalina 管线处理。

## 继承链（接口链 / 实现链）
- 继承链：`java.lang.Object` → `org.apache.coyote.Request`。

## 接口：数据 + 约束
- 数据（语义级别；字段名可能随 Tomcat 版本变化）：
  - 请求行：`method` / `requestURI` / `protocol`（通常为 byte/string 的内部表示）
  - 头部：`MimeHeaders headers`（语义级：header name/value 集合）
  - `contentType` / `contentLength`（请求体元数据）
  - 输入：`inputBuffer`（承载请求体字节读取的缓冲/通道语义）
- 输入：
  - 由 `Processor` 解析得到的请求信息
- 输出：
  - 被 `CoyoteAdapter` 读取并映射为 Servlet 请求视图（副作用）
- 约束：
  - 该对象不等价于 `javax.servlet.http.HttpServletRequest`；Servlet 语义由 Catalina 请求对象提供（见 [CatalinaRequest.md](CatalinaRequest.md)）。

## 常用构造/操作（仅列出接口与符号）
- 读取请求头：`headers`
- 读取请求体：通过输入缓冲/输入流语义读取（实现细节依版本）

## 关系：上级/下级/等价/特例/推广
- 上游：`Http11Processor`（见 [Http11Processor.md](Http11Processor.md)）。
- 下游：`CoyoteAdapter`（见 [CoyoteAdapter.md](CoyoteAdapter.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → CoyoteRequest。

