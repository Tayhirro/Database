# DispatcherHandler（WebFlux 前端控制器）

> **类型**：核心组件（Core Component）

## 一句话
`DispatcherHandler` 是 Spring WebFlux 的核心前端控制器，负责以非阻塞、响应式的方式接收 HTTP 请求并协调 HandlerMapping、HandlerAdapter 和 HandlerResultHandler 完成处理。

## 严格定义
`org.springframework.web.reactive.DispatcherHandler` 是实现了 `WebHandler` 接口的 Spring Bean。它不依赖于 Servlet API，而是基于 Reactor 的 `Mono`/`Flux` 模型设计，是 WebFlux 应用的请求处理总入口。

## 继承体系（Reactive Foundation）
与 `DispatcherServlet` 的 Servlet 继承树不同，`DispatcherHandler` 的体系更为扁平且专注于 Reactive Streams：

1.  **`WebHandler` (Interface)**
    - **定义**：WebFlux 的顶级处理接口，定义了 `handle(ServerWebExchange exchange): Mono<Void>`。
    - **作用**：任何想要处理 Web 请求的组件（包括过滤器链的末端）都实现此接口。

2.  **`DispatcherHandler` (Spring Class)**
    - **定义**：具体的请求分发实现。
    - **作用**：通过发现并委托给 Spring 容器中的策略组件（Mapping/Adapter/ResultHandler）来完成复杂的 MVC 式处理。

## 接口：数据 + 约束
- **输入**：`ServerWebExchange`（包含 `ServerHttpRequest` 和 `ServerHttpResponse`）。
- **输出**：`Mono<Void>`（表示处理过程的异步完成信号，响应数据直接写入 Exchange）。
- **约束**：全链路必须是非阻塞的（Non-blocking）。如果在 Handler 中执行阻塞操作（如 JDBC），必须显式调度到阻塞线程池，否则会卡死少量的工作线程（如 Netty EventLoop）。

## 辨析：Servlet vs Reactive (走什么路？)

| 特性 | Servlet 栈 (Spring MVC) | Reactive 栈 (Spring WebFlux) |
| :--- | :--- | :--- |
| **核心组件** | `DispatcherServlet` (extends `HttpServlet`) | `DispatcherHandler` (implements `WebHandler`) |
| **I/O 模型** | **同步阻塞** (Blocking I/O) | **异步非阻塞** (Non-blocking I/O) |
| **数据载体** | `HttpServletRequest` / `HttpServletResponse` | `ServerWebExchange` (封装了 Request/Response) |
| **底层容器** | Tomcat, Jetty (Servlet Container) | Netty, Tomcat, Jetty, Undertow (Reactive Adapter) |
| **线程模型** | **Thread-per-Request** (一请求一线程) | **Event Loop** (少量线程处理高并发) |
| **编程范式** | 命令式 (Imperative) | 响应式 (Functional/Declarative) |

> **链路对比**：
> - **MVC**: Socket 字节流 $\to$ Tomcat 解析为 `HttpServletRequest` $\to$ `DispatcherServlet.service()` (同步) $\to$ Controller。
> - **WebFlux**: Socket 字节流 $\to$ Netty Buffer $\to$ `ServerWebExchange` $\to$ `DispatcherHandler.handle()` (返回 `Mono`) $\to$ Reactor 订阅链。

## 关系：上级/下级/等价/特例/推广
- **上级**：`WebHandler`。
- **对标**：`DispatcherServlet`（MVC 中的等价角色）。
- **下级**：WebFlux 组件（`RouterFunction`, `HandlerAdapter`）。
- **协作**：`HttpHandler`（更底层的适配接口，用于适配 Netty/Servlet 容器）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → DispatcherHandler → （WebHandler / WebFlux）。
