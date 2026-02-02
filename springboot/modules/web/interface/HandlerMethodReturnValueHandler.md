# HandlerMethodReturnValueHandler（MVC 方法返回值处理器接口）

> **类型**：接口（Interface）

## 一句话
`HandlerMethodReturnValueHandler` 定义了 Spring Web MVC 在注解控制器方法返回后，如何处理返回值并形成响应/视图结果的扩展边界。

## 严格定义
在 Spring Web MVC 中，`org.springframework.web.method.support.HandlerMethodReturnValueHandler` 用于支持 handler method 的返回值处理：当控制器方法执行完成后，框架会遍历一组返回值处理器，选择 `supportsReturnType(...)` 为 true 的处理器来消费返回值，并将其转换为 `ModelAndView`、响应体写回的副作用、或其他可被渲染/提交的结果（实现相关）。

## 接口：数据 + 约束
- 输入：
  - `returnValue: Object`（方法返回值）
  - `MethodParameter`（返回类型描述）
  - `ModelAndViewContainer`（模型/视图容器，实现相关）
  - `NativeWebRequest`（请求/响应视图）
- 输出：
  - 对 `ModelAndViewContainer` 的修改或对响应的写回副作用（实现相关）
- 约束：
  - 在响应体写回场景，返回值处理器通常与 `HttpMessageConverter` 协作（见 [HttpMessageConverter.md](HttpMessageConverter.md)）。

## 常用构造/操作（仅列出接口与符号）
- `supportsReturnType(returnType): boolean`
- `handleReturnValue(returnValue, returnType, mavContainer, webRequest): void`

## 关系：上级/下级/等价/特例/推广
- 上级：`HandlerAdapter`（在 handler method 执行路径中使用，见 [HandlerAdapter.md](HandlerAdapter.md)）。
- 相关：`HttpMessageConverter`（响应体序列化边界，见 [HttpMessageConverter.md](HttpMessageConverter.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → interface → HandlerMethodReturnValueHandler。

