---
type: class
tags:
  - springboot/web
  - tomcat
---

# Context（Tomcat Catalina Context 组件）

> **类型**：类（Class）

## 一句话
`Context` 表示一个 Web 应用（ServletContext 的容器侧表示）：承载该应用的路径、类加载与 Servlet 映射，并组织一组 `Wrapper`（单个 Servlet 的容器包装）。

## 严格定义
在 Tomcat 中，`org.apache.catalina.Context` 是 `Host` 的子容器，用于表示一个具体 Web 应用：它与 Servlet API 的 `ServletContext` 存在对应关系，并持有/组织该应用下的 `Wrapper`（每个 `Wrapper` 对应一个 Servlet）；常见实现为 `org.apache.catalina.core.StandardContext`。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `path: String`：上下文路径（例如 `""` 或 `"/app"`；具体含义随配置与容器实现约定）
  - `wrappers: Wrapper[]`：子容器集合（每个 Wrapper 对应一个 Servlet）
  - （实现相关）类加载/会话/安全等子系统：`Loader`、`Manager`、`Realm` 等
- 字段与状态（常见实现；字段名可能随 Tomcat 版本变化）：
  - `path: String`：上下文路径
  - `docBase: String`：应用内容根目录/资源基准位置（部署相关，实现相关）
  - `children: Map<String, Wrapper>`：子容器集合（按 servletName 索引，实现相关）
  - `servletMappings: Map<String, String>`：URL pattern → servletName 的映射表（实现相关）
  - `loader`：类加载子系统（实现相关）
  - `resources`：静态资源与 Web 资源抽象（实现相关）
  - `manager`：会话管理子系统（实现相关）
  - `pipeline`：容器调用链（Valve 链，Tomcat Pipeline 体系）
  - `state`：生命周期状态（Tomcat `Lifecycle` 体系）
- 输入：
  - 子容器管理：`addChild(wrapper)` / `findChild(name)`
  - Servlet 映射与注册（实现相关）：例如为 `Wrapper` 建立 URL pattern 映射
- 输出：
  - 将请求分派到某个 `Wrapper` 并最终调用对应 `Servlet` 的处理链路（运行态行为）
- 约束：
  - `Context` 的“应用边界”同时适用于多应用部署与单应用嵌入式部署；嵌入式 Tomcat 下通常由宿主框架/工厂负责创建与绑定该 `Context`。

## 常用构造/操作（仅列出接口与符号）
- `addChild(Wrapper)` / `findChild(String)`
- （映射）为 `Wrapper` 绑定 URL pattern（实现相关）

## 关系：上级/下级/等价/特例/推广
- 上级：
  - `Host`：见 [Host.md](Host.md)
- 下级：
  - `Context` → `Wrapper`：见 [Wrapper.md](Wrapper.md)
- 相关：
  - 前端控制器（MVC）：`DispatcherServlet`（见 [../../../class/DispatcherServlet.md](../../../class/DispatcherServlet.md)）

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → Context。
