# HandlerInterceptor（MVC 拦截器接口）

> **类型**：接口（Interface）

## 一句话
`HandlerInterceptor` 定义了 Spring Web MVC 在 handler 执行前后插入横切逻辑的拦截点接口。

## 严格定义
在 Spring Web MVC 中，`org.springframework.web.servlet.HandlerInterceptor` 可与一次请求的 handler 绑定为一条拦截链：在 handler 执行前调用 `preHandle(...)`，在 handler 执行后、视图渲染前调用 `postHandle(...)`，在请求完成后调用 `afterCompletion(...)`；拦截器链通常由 `HandlerMapping` 在返回 `HandlerExecutionChain` 时提供，并由 `DispatcherServlet` 在分发流程中驱动执行。

## 接口：数据 + 约束
- 输入：
  - `HttpServletRequest` / `HttpServletResponse`
  - `handler: Object`
  - `ModelAndView`（可选，postHandle 阶段可见）
  - `Exception`（可选，afterCompletion 阶段可见）
- 输出：
  - `preHandle(...)` 的 boolean 返回值（放行/中断语义）
  - 其余方法以副作用表达（例如写 response、记录上下文等）
- 约束：
  - 拦截器链的执行顺序与组合方式由 `HandlerExecutionChain` 决定（实现相关）。
  - 拦截器不负责 handler 的选择与执行；handler 的选择由 `HandlerMapping` 负责（见 [HandlerMapping.md](HandlerMapping.md)），handler 的执行由 `HandlerAdapter` 负责（见 [HandlerAdapter.md](HandlerAdapter.md)）。

## 常用构造/操作（仅列出接口与符号）
- `preHandle(request, response, handler): boolean`
- `postHandle(request, response, handler, modelAndView): void`
- `afterCompletion(request, response, handler, ex): void`

## 关系：上级/下级/等价/特例/推广
- 上级：`DispatcherServlet`（请求分发宿主，见 [../class/DispatcherServlet.md](../class/DispatcherServlet.md)）。
- 相关：`HandlerMapping`（拦截器链随 `HandlerExecutionChain` 一并返回，见 [HandlerMapping.md](HandlerMapping.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → interface → HandlerInterceptor。

