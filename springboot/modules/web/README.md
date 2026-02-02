---
type: index
tags:
  - springboot/web
  - moc
---

# web（模块总览）

> 本页用于汇总 `modules/web/` 下的机制与入口，并用关系边表达“启动/运行态”的对象连接方式。

导航：[springboot/README.md](../../README.md) | [springboot/索引.md](../../索引.md) | [springboot/概念图.md](../../概念图.md)

## 目录结构（模块内）
- `modules/web/class/`：类条目
- `modules/web/interface/`：接口条目
- `modules/web/mechanism/`：机制条目
- `modules/web/server/`：具体服务器实现（Tomcat/Netty 等）

## WebServer 抽象（接口层）
- `WebServer`：见 [interface/WebServer.md](interface/WebServer.md)
- `WebServerFactory`：见 [interface/WebServerFactory.md](interface/WebServerFactory.md)
- `ServletWebServerFactory`：见 [interface/ServletWebServerFactory.md](interface/ServletWebServerFactory.md)
- `ReactiveWebServerFactory`：见 [interface/ReactiveWebServerFactory.md](interface/ReactiveWebServerFactory.md)

## Spring MVC 组件（接口层）
- `HandlerMapping`：见 [interface/HandlerMapping.md](interface/HandlerMapping.md)
- `HandlerAdapter`：见 [interface/HandlerAdapter.md](interface/HandlerAdapter.md)
- `HandlerInterceptor`：见 [interface/HandlerInterceptor.md](interface/HandlerInterceptor.md)
- `HandlerMethodArgumentResolver`：见 [interface/HandlerMethodArgumentResolver.md](interface/HandlerMethodArgumentResolver.md)
- `HandlerMethodReturnValueHandler`：见 [interface/HandlerMethodReturnValueHandler.md](interface/HandlerMethodReturnValueHandler.md)
- `HandlerExceptionResolver`：见 [interface/HandlerExceptionResolver.md](interface/HandlerExceptionResolver.md)
- `HttpMessageConverter`：见 [interface/HttpMessageConverter.md](interface/HttpMessageConverter.md)

## 启动链（Servlet / embedded Tomcat）

### Boot → WebServer（概念级）
- 嵌入式 WebServer 创建与绑定：见 [mechanism/EmbeddedWebServer.md](mechanism/EmbeddedWebServer.md)
- WebServer 生命周期与线程保活：见 [mechanism/WebServerLifecycleAndThreads.md](mechanism/WebServerLifecycleAndThreads.md)
- `ServletWebServerFactory` → `TomcatServletWebServerFactory`：见 [class/TomcatServletWebServerFactory.md](class/TomcatServletWebServerFactory.md)
- `TomcatServletWebServerFactory` → `TomcatWebServer`：见 [class/TomcatWebServer.md](class/TomcatWebServer.md)

### TomcatWebServer → ProtocolHandler → Endpoint（概念级）
- Tomcat start/stop 触发链：见 [server/tomcat/mechanism/TomcatWebServerStartStop.md](server/tomcat/mechanism/TomcatWebServerStartStop.md)
- Tomcat 组件模型：见 [server/tomcat/mechanism/TomcatComponentModel.md](server/tomcat/mechanism/TomcatComponentModel.md)
- Tomcat 组件委托链：`Connector`（端口入口 + 生命周期委派）→ `ProtocolHandler`（协议处理边界）→ `AbstractProtocol`（常见实现骨架）→ `AbstractEndpoint`（I/O 端点 + executor）
  - `Connector`：见 [server/tomcat/class/Connector.md](server/tomcat/class/Connector.md)
  - `ProtocolHandler`：见 [server/tomcat/interface/ProtocolHandler.md](server/tomcat/interface/ProtocolHandler.md)
  - `AbstractProtocol`：见 [server/tomcat/class/AbstractProtocol.md](server/tomcat/class/AbstractProtocol.md)
  - `AbstractEndpoint`：见 [server/tomcat/class/AbstractEndpoint.md](server/tomcat/class/AbstractEndpoint.md)

#### 组件用途与注意事项（语义级）

| 组件                 | 用途（做什么）                                                                                | 注意事项（不做什么 / 边界）                                                                                                                     |
| ------------------ | -------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| `Connector`        | Tomcat 的网络入口组件：绑定端口并把连接处理委派给其持有的 `ProtocolHandler`，作为 Catalina（容器）侧与 Coyote（协议栈）侧的连接点。 | 不定义 accept/poller/worker 的线程模型；线程与执行器的组织在 `ProtocolHandler/Endpoint` 侧。连接器级配置通常用于选择/构造具体协议实现与触发生命周期迁移。                              |
| `ProtocolHandler`  | 协议处理器接口：表达“某种协议栈如何 start/stop 并处理连接”的生命周期边界，作为 `Connector` 与具体协议实现之间的接口。               | 只表达生命周期与职责边界，不固定线程模型与连接管理细节；协议解析与 I/O 组织由具体实现决定。                                                                                    |
| `AbstractProtocol` | `ProtocolHandler` 的常见抽象基类：提供协议处理器的通用骨架，并在 `start()` 中调用 `endpoint.start()` 启动端点。       | 其职责偏“协议处理器层”的组织点；worker executor 与 accept/poll 线程角色通常由 endpoint 组织。除 endpoint 外，协议层可能还持有辅助调度器（例如 `utilityExecutor`），与请求处理线程池属于不同角色。 |
| `AbstractEndpoint` | 网络端点抽象：端口监听、连接管理、I/O 轮询/事件分发，并把请求处理任务投递到 `Executor`（内部创建或外部注入）。                        | 端点的具体实现随 I/O 模型变化（NIO/NIO2/APR 等）；线程命名、线程数量、以及 executor 的具体类型属于实现细节。                                                                |

该委托链路把职责边界表述为：`Connector` 负责“端口入口 + 生命周期委派”，`ProtocolHandler` 负责“协议栈组织与推进”，`Endpoint` 负责“端口监听 + I/O 管理 + 任务投递”。

## 请求处理链（Servlet / Tomcat / Spring MVC）

### 一个从输入到输出的例子
请求（客户端 → 服务器）：

```
GET /myapp/product/list HTTP/1.1
Host: shop.company.com
```

响应（服务器 → 客户端）：

```
HTTP/1.1 200 OK
Content-Type: application/json

[{"id":42,"name":"Alice"}]
```

### 处理流程（按阶段）

| 阶段  | 组件（条目）                                                                                                                                                                                           | 作用（做什么）                                                                                       | 作用边界（分层语义）                        |
| --- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------- | --------------------------------- |
| 1   | `AbstractEndpoint`（见 [server/tomcat/class/AbstractEndpoint.md](server/tomcat/class/AbstractEndpoint.md)）                                                                                         | 接收连接、读写 socket、驱动 I/O 事件与任务投递                                                                 | I/O 与连接管理边界                       |
| 2   | `Processor` / `Http11Processor`（见 [server/tomcat/interface/Processor.md](server/tomcat/interface/Processor.md)、[server/tomcat/class/Http11Processor.md](server/tomcat/class/Http11Processor.md)） | 解析 HTTP 字节流，生成/填充 `org.apache.coyote.Request/Response`                                        | 协议解析与连接级状态机边界                     |
| 3   | `CoyoteAdapter`（见 [server/tomcat/class/CoyoteAdapter.md](server/tomcat/class/CoyoteAdapter.md)）                                                                                                  | 将 Coyote 请求/响应适配到 Catalina（容器）侧请求对象，并进入容器调用链                                                  | 协议栈 → 容器 的适配边界                    |
| 4   | `Mapper`（见 [server/tomcat/class/Mapper.md](server/tomcat/class/Mapper.md)）                                                                                                                       | 将 `Host` 头与 URI 路径映射为目标容器对象（`Host`/`Context`/`Wrapper`）                                       | 路由与索引边界（运行态查找）                    |
| 5   | `Engine`（见 [server/tomcat/class/Engine.md](server/tomcat/class/Engine.md)）                                                                                                                       | 容器链路顶层入口：确定目标 `Host` 并推进到下一层容器                                                                | 虚拟主机集合（跨 Host）边界                  |
| 6   | `Host`（见 [server/tomcat/class/Host.md](server/tomcat/class/Host.md)）                                                                                                                             | 虚拟主机容器：确定目标 `Context` 并推进到下一层容器                                                               | 单域名/单虚拟主机边界                       |
| 7   | `Context`（见 [server/tomcat/class/Context.md](server/tomcat/class/Context.md)）                                                                                                                    | Web 应用容器：建立应用级处理上下文并选择目标 `Wrapper`                                                            | 单 Web 应用边界（类加载、会话、映射等应用级设施由该边界组织） |
| 8   | `Wrapper`（见 [server/tomcat/class/Wrapper.md](server/tomcat/class/Wrapper.md)）                                                                                                                    | Servlet 容器单元：`allocate()` 获取 `Servlet`，构造 `ApplicationFilterChain` 并调用 `Servlet.service(...)` | 单 Servlet 边界（Servlet 生命周期与过滤器链入口） |
| 9   | `DispatcherServlet`（见 [class/DispatcherServlet.md](class/DispatcherServlet.md)）                                                                                                                  | 作为 Servlet 入口分发到 Spring MVC 的 Handler，并将返回值写入响应                                               | 框架层（MVC 分发与返回值处理）边界               |
| 10  | `Http11Processor`（同上）                                                                                                                                                                            | 将响应对象序列化为字节并写回 socket                                                                         | 协议输出边界                            |

### `DispatcherServlet` 内部处理链（Spring MVC）

| 步骤 | 组件（条目） | 作用（做什么） | 输出（用于下一步） |
| --- | --- | --- | --- |
| 9.0 | `MultipartResolver`（实现相关） | 在 multipart 请求场景下将请求解析为可访问上传文件/表单字段的请求视图 | 可能被包装的 `HttpServletRequest` |
| 9.1 | `HandlerMapping`（见 [interface/HandlerMapping.md](interface/HandlerMapping.md)） | 根据 `HttpServletRequest` 查找处理器（handler）与拦截器链 | `HandlerExecutionChain`（handler + interceptors） |
| 9.2 | `HandlerInterceptor`（见 [interface/HandlerInterceptor.md](interface/HandlerInterceptor.md)） | 在 handler 执行前后提供拦截点（preHandle/postHandle/afterCompletion） | 对 handler 执行的放行/中断决策与副作用 |
| 9.3 | `HandlerAdapter`（见 [interface/HandlerAdapter.md](interface/HandlerAdapter.md)） | 选择与 handler 匹配的适配器并驱动执行 | `ModelAndView` 或“已写入响应”的副作用 |
| 9.4 | `HandlerMethodArgumentResolver`（见 [interface/HandlerMethodArgumentResolver.md](interface/HandlerMethodArgumentResolver.md)） | 在注解控制器方法场景解析方法参数（路径变量、请求参数、请求体等） | 控制器方法入参集合（语义） |
| 9.5 | `HandlerMethodReturnValueHandler`（见 [interface/HandlerMethodReturnValueHandler.md](interface/HandlerMethodReturnValueHandler.md)） | 在注解控制器方法场景处理返回值（视图名、模型、响应体等） | `ModelAndView` 或响应写回意图 |
| 9.6 | `HttpMessageConverter`（见 [interface/HttpMessageConverter.md](interface/HttpMessageConverter.md)） | 在响应体写回场景把返回对象序列化为 HTTP 消息体（并设置 Content-Type 等） | `HttpServletResponse` 的 body/header/status |
| 9.7 | `HandlerExceptionResolver`（见 [interface/HandlerExceptionResolver.md](interface/HandlerExceptionResolver.md)） | 在异常场景将异常转换为可返回的响应（或 `ModelAndView`） | 异常到响应/视图的解析结果（实现相关） |

### 容器链的调用形式（Pipeline/Valve）
容器层级（`Engine`/`Host`/`Context`/`Wrapper`）在运行态通常通过 Pipeline/Valve 组织调用：每一层容器的 Pipeline 由若干 Valve 组成，末尾的 Basic Valve 负责把请求推进到下一层容器；到达 `Wrapper` 层后，Basic Valve 触发 `Servlet` 的分配与调用（并在此处构造过滤器链）。

## 运行态线程角色（Tomcat / NIO）
- Tomcat 线程与执行器模型：见 [server/tomcat/mechanism/TomcatThreadingAndExecutors.md](server/tomcat/mechanism/TomcatThreadingAndExecutors.md)
- 端点线程角色（Acceptor/Poller/Executor）：见 [server/tomcat/class/threading/README.md](server/tomcat/class/threading/README.md)

## Web 作用域（WebScopes）
- Web 作用域（request/session）：见 [mechanism/WebScopes.md](mechanism/WebScopes.md)

## 前端控制器（可选）
- `DispatcherServlet`（Spring MVC）：见 [class/DispatcherServlet.md](class/DispatcherServlet.md)
- `DispatcherHandler`（Spring WebFlux）：见 [class/DispatcherHandler.md](class/DispatcherHandler.md)
