---
type: class
tags:
  - springboot/web
  - tomcat
---

# Wrapper（Tomcat Catalina Wrapper 组件）

> **类型**：类（Class）

## 一句话
`Wrapper` 是“单个 Servlet 的容器侧包装”：负责 `Servlet` 实例的初始化、调用与销毁，并作为 `Context` 下的最小容器单元参与请求分发。

## 严格定义
在 Tomcat 中，`org.apache.catalina.Wrapper` 是 `Context` 的子容器，用于封装一个具体 `Servlet` 的容器级管理：它将 `Servlet` 的生命周期（init/service/destroy）纳入容器生命周期，并在请求到达时负责分配/调用对应 `Servlet`；常见实现为 `org.apache.catalina.core.StandardWrapper`。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `name: String`：Servlet 名称（用于映射与查找）
  - `servletClass: String`：Servlet 类名（实现相关）
  - `loadOnStartup: int`：启动加载语义（实现相关）
- 输入：
  - 生命周期触发：随 `Context`/容器生命周期推进
  - 请求调用：分配 Servlet 实例并调用 `service(req, resp)`（实现相关）
- 输出：
  - `Servlet` 的 init/service/destroy 调用（副作用）
- 约束：
  - `Wrapper` 只表示“单个 Servlet 的容器管理单元”；URL pattern 到 `Wrapper` 的映射由 `Context` 级别的映射表维护（实现相关）。

## 常用构造/操作（仅列出接口与符号）
- `load()` / `unload()`（实现相关）
- `allocate()` / `deallocate()`（实现相关）

## 关系：上级/下级/等价/特例/推广
- 上级：
  - `Context`：见 [Context.md](Context.md)
- 下级：
  - `Wrapper` → `Servlet`（Servlet API）
- 相关：
  - 前端控制器（MVC）：`DispatcherServlet`（见 [../../../class/DispatcherServlet.md](../../../class/DispatcherServlet.md)）

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → Wrapper。

