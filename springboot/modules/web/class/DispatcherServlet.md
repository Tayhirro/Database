# DispatcherServlet（前端控制器）

> **类型**：核心组件（Core Component）

## 一句话
`DispatcherServlet` 是 Spring Web MVC 的核心前端控制器（Front Controller），负责接收所有 HTTP 请求并将其分发给合适的处理器（Controller）进行处理，最后返回响应。

## 严格定义
`org.springframework.web.servlet.DispatcherServlet` 是一个标准的 Servlet，它通过继承 `FrameworkServlet` 和 `HttpServlet` 实现了 Java Servlet 规范。在 Spring Boot 中，它默认被自动配置并注册到根路径 `/`，作为应用处理 HTTP 请求的统一入口。

## 继承链（接口链 / 实现链）
- 继承链（Framework）：`HttpServlet` → `HttpServletBean` → `FrameworkServlet` → `DispatcherServlet`。

`DispatcherServlet` 的能力建立在 Servlet 规范与 WebApplicationContext 集成之上：

1.  **`Servlet` (Interface)**
    - **定义**：Java EE/Jakarta EE 标准接口，定义了 `init()`, `service()`, `destroy()` 生命周期。
    - **作用**：所有 Servlet 的顶级契约。

2.  **`HttpServlet` (Abstract Class)**
    - **定义**：基于 HTTP 协议的 Servlet 实现。
    - **作用**：将通用的 `service()` 方法分发为 `doGet()`, `doPost()` 等 HTTP 动作相关的方法。

3.  **`FrameworkServlet` (Spring Class)**
    - **定义**：Spring Web 框架的基础 Servlet。
    - **作用**：将 Servlet 生命周期与 Spring `ApplicationContext` 集成（确保 WebContext 被正确初始化和刷新）。

4.  **`DispatcherServlet` (Spring Class)**
    - **定义**：具体的前端控制器实现。
    - **作用**：实现了统一的请求分发逻辑（`doDispatch`），协调 HandlerMapping、HandlerAdapter、ViewResolver 等组件工作。

## 接口：数据 + 约束
- **输入**：`HttpServletRequest`, `HttpServletResponse`。
- **输出**：写入 Response 的响应数据（JSON/HTML）。
- **依赖**：需要一个 `WebApplicationContext` 来查找 MVC 组件（Controller, ViewResolver 等）。

## 常用构造/操作（配置与定制）
- **自动配置**：`DispatcherServletAutoConfiguration`
- **注册映射**：默认映射到 `/`（可配置 `spring.mvc.servlet.path`）。
- **扩展点**：
  - `HandlerInterceptor`：拦截器。
  - `WebMvcConfigurer`：配置类接口。

## 关系：上级/下级/等价/特例/推广
- **上级**：`Servlet` / `HttpServlet`（它是 Servlet 的一种实现）。
- **下级**：MVC 组件（HandlerMapping, HandlerAdapter, ViewResolver）。
- **协作**：`EmbeddedWebServer`（容器负责加载和运行 Servlet）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → DispatcherServlet → （Servlet体系 / MVC流程）。
