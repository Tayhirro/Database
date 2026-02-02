# HandlerExceptionResolver（MVC 异常解析器接口）

> **类型**：接口（Interface）

## 一句话
`HandlerExceptionResolver` 定义了 Spring Web MVC 将“处理过程中的异常”解析为“可返回的响应或视图结果”的扩展边界。

## 严格定义
在 Spring Web MVC 中，`org.springframework.web.servlet.HandlerExceptionResolver` 用于在 handler 执行或渲染过程中发生异常时提供恢复路径：给定 request/response、handler 与异常对象，解析器可以返回一个 `ModelAndView` 或直接对响应进行写回，从而将异常转换为可返回给客户端的结果；`DispatcherServlet` 会遍历一组解析器并选择第一个成功解析的结果（顺序与配置相关）。

## 接口：数据 + 约束
- 输入：
  - `HttpServletRequest` / `HttpServletResponse`
  - `handler: Object`（可为 null，取决于异常发生阶段）
  - `ex: Exception`
- 输出：
  - `ModelAndView`（可选；也可能通过写 response 直接完成响应）
- 约束：
  - 解析器的选择顺序与返回策略由配置决定（实现相关）。

## 常用构造/操作（仅列出接口与符号）
- `resolveException(request, response, handler, ex): ModelAndView`

## 关系：上级/下级/等价/特例/推广
- 上级：`DispatcherServlet`（异常分发与结果处理宿主，见 [../class/DispatcherServlet.md](../class/DispatcherServlet.md)）。
- 相关：`HandlerAdapter`（异常发生在 handler 执行路径上时的协作边界，见 [HandlerAdapter.md](HandlerAdapter.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → interface → HandlerExceptionResolver。

