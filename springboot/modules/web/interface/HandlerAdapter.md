# HandlerAdapter（MVC 处理器适配器接口）

> **类型**：接口（Interface）

## 一句话
`HandlerAdapter` 定义了 Spring Web MVC 如何执行某类 handler 的适配边界：判断是否支持，并在支持时驱动 handler 执行并产出模型/视图或副作用响应。

## 严格定义
在 Spring Web MVC 中，`org.springframework.web.servlet.HandlerAdapter` 将“handler 的多种形态”与 `DispatcherServlet` 的统一分发流程解耦：`DispatcherServlet` 从 `HandlerMapping` 得到 handler 之后，选择一个 `supports(handler)=true` 的适配器，调用其 `handle(...)` 执行 handler，并将结果推进到后续的渲染/写回阶段（实现相关）。

## 继承链（接口链 / 实现链）
- 接口链：`HandlerAdapter`（无上级接口）。
- 常见实现（实现选择与项目配置相关）：
  - 注解控制器方法：`RequestMappingHandlerAdapter`
  - 简单控制器：`SimpleControllerHandlerAdapter`

## 接口：数据 + 约束
- 输入：
  - `HttpServletRequest` / `HttpServletResponse`
  - `handler: Object`（由 `HandlerMapping` 选择出的处理器）
- 输出：
  - `ModelAndView`（可选；也可能通过写 response 直接完成响应）
- 约束：
  - handler 的解析（URL → handler）不属于 `HandlerAdapter`；由 `HandlerMapping` 负责（见 [HandlerMapping.md](HandlerMapping.md)）。
  - 具体如何进行参数绑定、返回值处理与消息转换属于实现细节（例如注解控制器方法的参数解析与返回值处理；REST 场景通常通过 `HttpMessageConverter` 写回响应体，见 [HttpMessageConverter.md](HttpMessageConverter.md)）。

## 常用构造/操作（仅列出接口与符号）
- `supports(handler): boolean`
- `handle(request, response, handler): ModelAndView`
- `getLastModified(request, handler): long`（可选）

## 关系：上级/下级/等价/特例/推广
- 上级：`DispatcherServlet`（请求分发宿主，见 [../class/DispatcherServlet.md](../class/DispatcherServlet.md)）。
- 相关：
  - `HandlerMapping`（handler 解析边界，见 [HandlerMapping.md](HandlerMapping.md)）。
  - `HttpMessageConverter`（响应体写回边界，见 [HttpMessageConverter.md](HttpMessageConverter.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → interface → HandlerAdapter。

