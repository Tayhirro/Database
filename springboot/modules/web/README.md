---
type: index
tags:
  - springboot/web
  - moc
---

# web（模块总览）

> 本页用于汇总 `modules/web/` 下的机制与入口，并用关系边表达"启动/运行态"的对象连接方式。

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
| `ProtocolHandler`  | 协议处理器接口：表达"某种协议栈如何 start/stop 并处理连接"的生命周期边界，作为 `Connector` 与具体协议实现之间的接口。               | 只表达生命周期与职责边界，不固定线程模型与连接管理细节；协议解析与 I/O 组织由具体实现决定。                                                                                    |
| `AbstractProtocol` | `ProtocolHandler` 的常见抽象基类：提供协议处理器的通用骨架，并在 `start()` 中调用 `endpoint.start()` 启动端点。       | 其职责偏"协议处理器层"的组织点；worker executor 与 accept/poll 线程角色通常由 endpoint 组织。除 endpoint 外，协议层可能还持有辅助调度器（例如 `utilityExecutor`），与请求处理线程池属于不同角色。 |
| `AbstractEndpoint` | 网络端点抽象：端口监听、连接管理、I/O 轮询/事件分发，并把请求处理任务投递到 `Executor`（内部创建或外部注入）。                        | 端点的具体实现随 I/O 模型变化（NIO/NIO2/APR 等）；线程命名、线程数量、以及 executor 的具体类型属于实现细节。                                                                |

该委托链路把职责边界表述为：`Connector` 负责"端口入口 + 生命周期委派"，`ProtocolHandler` 负责"协议栈组织与推进"，`Endpoint` 负责"端口监听 + I/O 管理 + 任务投递"。

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
- service()

| 步骤  | 组件（条目）                                                                                                                            | 作用（做什么）                                                   | 输出（用于下一步）                                       |
| --- | --------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------- | ----------------------------------------------- |
| 9.0 | `MultipartResolver`（实现相关）                                                                                                         | 在 multipart 请求场景下将请求解析为可访问上传文件/表单字段的请求视图                  | 可能被包装的 `HttpServletRequest`                     |
| 9.1 | `HandlerMapping`（见 [interface/HandlerMapping.md](interface/HandlerMapping.md)）                                                    | 根据 `HttpServletRequest` 查找处理器（handler）与拦截器链               | `HandlerExecutionChain`（handler + interceptors） |
| 9.2 | `HandlerInterceptor`（见 [interface/HandlerInterceptor.md](interface/HandlerInterceptor.md)）                                        | 在 handler 执行前后提供拦截点（preHandle/postHandle/afterCompletion） | 对 handler 执行的放行/中断决策与副作用                        |
| 9.3 | `HandlerAdapter`（见 [interface/HandlerAdapter.md](interface/HandlerAdapter.md)）                                                    | 选择与 handler 匹配的适配器并驱动执行                                   | `ModelAndView` 或“已写入响应”的副作用                     |
| 9.4 | `HandlerMethodArgumentResolver`（见 [interface/HandlerMethodArgumentResolver.md](interface/HandlerMethodArgumentResolver.md)）       | 在注解控制器方法场景解析方法参数（路径变量、请求参数、请求体等）                          | 控制器方法入参集合（语义）                                   |
| 9.5 | `HandlerMethodReturnValueHandler`（见 [interface/HandlerMethodReturnValueHandler.md](interface/HandlerMethodReturnValueHandler.md)） | 在注解控制器方法场景处理返回值（视图名、模型、响应体等）                              | `ModelAndView` 或响应写回意图                          |
| 9.6 | `HttpMessageConverter`（见 [interface/HttpMessageConverter.md](interface/HttpMessageConverter.md)）                                  | 在响应体写回场景把返回对象序列化为 HTTP 消息体（并设置 Content-Type 等）            | `HttpServletResponse` 的 body/header/status      |
| 9.7 | `HandlerExceptionResolver`（见 [interface/HandlerExceptionResolver.md](interface/HandlerExceptionResolver.md)）                      | 在异常场景将异常转换为可返回的响应（或 `ModelAndView`）                       | 异常到响应/视图的解析结果（实现相关）                             |

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

---

## HTTP 请求数据流转与转换（详细链路）

> 补充各层之间的数据变化细节，展示从网络字节流到 Java 对象的完整转换过程。

### 完整数据转换链路（带变化标注）

```
浏览器 HTTP 请求
    ↓
[TCP 数据包] (二进制字节流：IP头+TCP头+HTTP报文)
    ↓ 变化：OS 内核解析 TCP，建立连接
Tomcat Endpoint (NioEndpoint)
    ↓ 数据转换：SocketChannel → ByteBuffer
[ByteBuffer] (8KB 堆外/堆内存，包含原始 HTTP 字节)
    ↓ 变化：解析 HTTP 协议（请求行、Headers、Body）
Tomcat Processor (Http11Processor)
    ↓ 数据转换：ByteBuffer 字节 → CoyoteRequest 结构化对象
[org.apache.coyote.Request] (内部对象：method/uri/protocol/headers)
    例：{method="GET", uri="/user/login", headers={Host="api.example.com"}}
    ↓ 变化：创建安全包装（Facade 模式）
Tomcat Adapter (CoyoteAdapter)
    ↓ 数据转换：CoyoteRequest → RequestFacade
[org.apache.catalina.connector.RequestFacade] (HttpServletRequest 实现)
    ↓ 变化：经过 FilterChain（可能修改请求属性）
Filter 链（如：UrlRewriteFilter）
    输入：GET http://www.api.example.com/user/login
    ↓ 变化：修改 URI（去掉 www 前缀）
    输出：GET http://api.example.com/user/login
    ↓ 同时保留原始值：request.setAttribute("originalUri", "http://www.api.example.com/user/login")
[HttpServletRequest] (对象引用不变，内部状态变化)
    ↓ 变化：DispatcherServlet 路由映射
DispatcherServlet (Spring MVC 入口)
    ├─ 9.0 MultipartResolver
    │   ↓ 如果是 multipart/form-data 请求
    │   数据转换：StandardHttpServletRequest → StandardMultipartHttpServletRequest
    │   变化：可以调用 request.getFile("avatar") 获取上传文件
    │
    ├─ 9.1 HandlerMapping
    │   ↓ 根据 URL + method 查找 Handler
    │   数据转换：HttpServletRequest → HandlerExecutionChain
    │   例：{handler=UserController.login", int"erceptors=[RefreshTokenInterceptor, LoginInterceptor]}
    │
    ├─ 9.2 HandlerInterceptor.preHandle
    │   ↓ 按 order 顺序执行拦截器
    │   RefreshTokenInterceptor：查 Redis → ThreadLocal.set(user)
    │   LoginInterceptor：检查 ThreadLocal.get() → 返回 true/false
    │
    ├─ 9.3 HandlerAdapter
    │   ↓ 创建 ModelAndViewContainer（包含 Model）
    │   数据转换：创建 BindingAwareModelMap（空 Model）
    │   Model model = mavContainer.getModel()
    │   ↓ 初始化 Model（@ModelAttribute 方法、@SessionAttributes）
    │   例：model.addAttribute("currentTime", LocalDateTime.now())
    │
    ├─ 9.4 HandlerMethodArgumentResolver
    │   ↓ 解析 Controller 方法参数
    │   @PathVariable：从 URL 路径提取 → Long id = 1L
    │   @RequestParam：从 QueryString 提取 → String category = "electronics"
    │   @RequestBody：从 HTTP Body 提取 → JSON 反序列化为 LoginFormDTO
    │   Model：直接注入 mavContainer.getModel()
    │
    ├─ Controller 方法执行
    │   ↓ 业务逻辑 + Model 数据填充（核心变化！）
    │   Product product = productService.findById(1L)
    │   model.addAttribute("product", product)      // Model 变化！
    │   model.addAttribute("category", "electronics") // Model 变化！
    │   ↓ 方法返回
    │   返回 "product/detail"（视图名）
    │   或返回 ModelAndView
    │   或 @ResponseBody 直接返回对象（走 HttpMessageConverter）
    │
    ├─ 9.5 HandlerMethodReturnValueHandler
    │   ↓ 处理 Controller 返回值
    │   如果返回 String：创建 ModelAndView(viewName, model)
    │   如果返回 ModelAndView：合并当前 model
    │   如果 @ResponseBody：调用 HttpMessageConverter
    │
    ├─ 9.6 HttpMessageConverter（@ResponseBody 场景）
    │   ↓ 数据转换：Java Object → JSON/XML/等
    │   UserDTO {id=1, nickName="张三"} 
    │   → {"id":1,"nickName":"张三"}
    │   ↓ 写入 HttpServletResponse
    │
    ├─ 9.7 HandlerExceptionResolver（如果有异常）
    │   ↓ 异常 → ModelAndView（错误页面）
    │   或异常 → JSON 错误响应
    │
    ├─ 视图渲染（如果是模板引擎，如 Thymeleaf/JSP）
    │   ↓ 数据转换：Model → Request Attributes
    │   model.asMap().forEach((k,v) -> request.setAttribute(k, v))
    │   ↓ 模板引擎渲染
    │   Thymeleaf：解析 HTML 模板，替换 ${product.name} 等表达式
    │   JSP：执行 Java 代码片段，生成 HTML
    │   ↓ 写入 response.getWriter()
    │
    ├─ HandlerInterceptor.postHandle
    │   ↓ 可以修改 ModelAndView（很少用）
    │
    ├─ HandlerInterceptor.afterCompletion
    │   ↓ 清理资源
    │   UserHolder.removeUser() // ThreadLocal.remove()
    │   记录请求耗时日志
    │
    ↓ 返回给 Tomcat
[HttpServletResponse] (包含 status/headers/body)
    ↓ 数据转换：Response → HTTP 字节流
Tomcat CoyoteAdapter
    HTTP/1.1 200 OK
    Content-Type: application/json
    {"id":1,"nickName":"张三"}
    ↓ 写入 SocketChannel
TCP 数据包
    ↓
浏览器接收响应
```

### 各层数据变化细节表

| 层级 | 输入数据 | 输出数据 | 数据变化/处理说明 |
|------|---------|---------|------------------|
| **网络层** | TCP 报文（二进制） | SocketChannel | OS 内核处理三次握手，建立连接 |
| **Tomcat Endpoint** | SocketChannel | ByteBuffer | 读取字节流到 8KB buffer（堆外/堆内存） |
| **Tomcat Processor** | ByteBuffer（原始字节） | CoyoteRequest | 解析 HTTP：请求行+Headers+Body（文本→结构化对象） |
| **Tomcat Adapter** | CoyoteRequest | RequestFacade | 创建安全包装（Facade 模式，隐藏内部实现） |
| **Filter 链** | HttpServletRequest | HttpServletRequest（包装后） | 可修改：URI、Header、编码、Attribute；例：去掉 www 前缀 |
| **MultipartResolver** | HttpServletRequest | MultipartHttpServletRequest | 如果是文件上传，包装请求以支持 getFile() |
| **HandlerMapping** | HttpServletRequest | HandlerExecutionChain | URL 路由映射，匹配 Controller 和拦截器链 |
| **HandlerInterceptor** | HttpServletRequest/Response | boolean | 鉴权逻辑、ThreadLocal 存用户、Redis 续期 token |
| **HandlerAdapter** | HttpServletRequest | ModelAndViewContainer | 创建 Model 对象（BindingAwareModelMap） |
| **Model 初始化** | 空 Model | 填充后的 Model | 执行 @ModelAttribute 方法、从 Session 恢复属性 |
| **ArgumentResolver** | HTTP 请求各部分 | Controller 参数 | @PathVariable、@RequestParam、@RequestBody 解析 |
| **Controller** | 业务参数 + Model | Model（填充后） | 执行业务逻辑，model.addAttribute() 添加数据 |
| **ReturnValueHandler** | Controller 返回值 | ModelAndView | 处理返回值：视图名/ModelAndView/@ResponseBody |
| **HttpMessageConverter** | Java Bean | HTTP Body | JSON/XML 序列化：Java Object → 字节流 |
| **View 渲染** | Model | HttpServletResponse | Model → Request Attributes → 模板渲染 → HTML |
| **Interceptor 清理** | - | void | ThreadLocal.remove()、资源释放 |
| **Tomcat Response** | HttpServletResponse | TCP 报文 | 序列化 HTTP 响应（文本→字节），写入 Socket |

### Model 对象完整生命周期（重点补充）

#### Model 创建与初始化

```java
// 在 HandlerAdapter.invokeHandlerMethod() 中
protected ModelAndView invokeHandlerMethod(...) {
    // 1. 创建 ModelAndViewContainer（核心容器）
    ModelAndViewContainer mavContainer = new ModelAndViewContainer();
    
    // 2. 获取 Model 对象（此时是空的 BindingAwareModelMap）
    Model model = mavContainer.getModel();
    // model = {} (empty map)
    
    // 3. 初始化 Model（关键变化！）
    modelFactory.initModel(request, mavContainer, handlerMethod);
    // 此时 Model 可能包含：
    // - 从 @SessionAttributes 恢复的数据
    // - @ModelAttribute 方法添加的数据
}
```

#### Model 初始化示例

```java
@Controller
@SessionAttributes("user")  // 声明 Session 属性
public class UserController {
    
    // 每个请求前执行，自动添加到 Model
    @ModelAttribute("currentTime")
    public LocalDateTime addCurrentTime() {
        return LocalDateTime.now();
    }
    
    @GetMapping("/profile")
    public String profile(Model model) {
        // 此时 model 中已有：
        // 1. "user"（从 session 恢复，如果存在）
        // 2. "currentTime"（来自 @ModelAttribute 方法）
        
        // Controller 可以继续添加数据
        model.addAttribute("title", "个人中心");
        
        return "profile";
    }
}
```

#### Controller 中 Model 的变化

```java
@GetMapping("/product/{id}")
public String getProduct(@PathVariable Long id, Model model) {
    // 1. 初始 Model（来自初始化阶段）
    // model = {currentTime=2024-01-15T10:30:00}
    
    // 2. 查询数据
    Product product = productService.findById(id);
    
    // 3. 添加数据到 Model（核心变化！）
    model.addAttribute("product", product);
    model.addAttribute("category", "electronics");
    model.addAttribute("recommended", true);
    
    // 4. 最终 Model 状态
    // model = {
    //   currentTime: 2024-01-15T10:30:00,
    //   product: Product@1234,
    //   category: "electronics",
    //   recommended: true
    // }
    
    return "product/detail";
}
```

#### Model → ModelAndView 转换

```java
// Controller 返回 "product/detail"
// HandlerAdapter 处理返回值：

// 创建 ModelAndView
ModelAndView mav = new ModelAndView("product/detail");

// 将 Model 复制到 ModelAndView
mav.addAllObjects(model.asMap());

// mav 现在包含：
// - viewName: "product/detail"
// - model: {currentTime, product, category, recommended}
```

#### Model → Request Attributes（视图渲染）

```java
// 在 View.render() 方法中（Thymeleaf/JSP）
protected void renderMergedOutputModel(
        Map<String, Object> model,
        HttpServletRequest request,
        HttpServletResponse response) {
    
    // 关键变化：Model 数据 → Request 属性
    model.forEach((key, value) -> {
        request.setAttribute(key, value);
    });
    
    // 现在 JSP/Thymeleaf 可以通过 EL 表达式访问：
    // ${product.name}, ${category}, ${currentTime}
}
```

#### Model 变化时间线总结

```
时间点                Model 状态                    触发者
─────────────────────────────────────────────────────────────
HandlerAdapter        {} (空 map)                   HandlerAdapter
初始化后              {currentTime, user}           @ModelAttribute + Session
Controller 执行前     {currentTime, user}           保持不变
Controller 执行中     {currentTime, user, product,  Controller.addAttribute()
                      category, recommended}
Controller 返回后     同上（复制到 ModelAndView）   HandlerAdapter
视图渲染              同上（设置到 Request）        View.render()
```

### REST API 与 Model 的特殊情况

```java
@RestController
public class ApiController {
    
    @GetMapping("/api/user/{id}")
    public UserDTO getUser(@PathVariable Long id, Model model) {
        UserDTO user = userService.getUser(id);
        
        // 虽然可以往 Model 添加数据
        model.addAttribute("requestTime", System.currentTimeMillis());
        
        // 但 @RestController + @ResponseBody 会：
        // 1. 直接返回 user 对象
        // 2. HttpMessageConverter 序列化为 JSON
        // 3. Model 中的数据不会进入响应！
        
        return user;  // 只有这个被序列化
    }
}

// 响应结果：
// {"id":1,"nickName":"张三"}
// 注意：Model 中的 requestTime 不会出现在 JSON 中！
```

**REST API 中 Model 的用途：**
- 存放处理过程的元数据
- 供拦截器使用（如记录处理时间）
- 不直接进入响应（除非使用 ModelAndView 或包装对象）

### 关键数据转换示例详解

#### 1. MultipartResolver（文件上传处理）

```java
// 请求：Content-Type: multipart/form-data; boundary=----WebKitFormBoundary

// MultipartResolver 检测并处理：
if (isMultipart) {
    // 包装原始请求
    MultipartHttpServletRequest multipartRequest = 
        multipartResolver.resolveMultipart(request);
    
    // 现在可以获取文件：
    MultipartFile avatar = multipartRequest.getFile("avatar");
    // avatar.getOriginalFilename() = "photo.jpg"
    // avatar.getSize() = 2048000
    // avatar.getInputStream() = 文件输入流
    
    // 也可以获取普通表单字段：
    String username = multipartRequest.getParameter("username");
}
```

#### 2. HandlerMapping 路由匹配

```java
// 请求：GET /user/123/profile

// HandlerMapping 匹配过程：
// 1. RequestMappingInfo 匹配
//    - path: /user/{id}/profile ✓ 匹配成功
//    - method: GET ✓
//    - params: (无要求) ✓
//    - headers: (无要求) ✓

// 2. 找到 HandlerMethod
HandlerMethod handlerMethod = new HandlerMethod(
    userController,                    // bean
    UserController.class.getMethod("profile", Long.class)  // method
);

// 3. 包装为 HandlerExecutionChain
return new HandlerExecutionChain(handlerMethod, interceptors);
```

#### 3. HandlerMethodArgumentResolver 参数解析

```java
// Controller 方法：
public String getProduct(
    @PathVariable Long id,                    // 从 URL 路径
    @RequestParam String category,            // 从 QueryString
    @RequestBody ProductForm form,            // 从 JSON Body
    @RequestHeader("Authorization") String token,  // 从 Header
    Model model                               // Spring 注入
) { ... }

// 解析过程：
// 1. PathVariableMethodArgumentResolver
//    从 request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)
//    提取 id = 123，转换 String → Long

// 2. RequestParamMethodArgumentResolver
//    从 request.getParameter("category")
//    提取 category = "electronics"

// 3. RequestResponseBodyMethodProcessor
//    读取 request.getInputStream()
//    JSON → Jackson → ProductForm 对象

// 4. RequestHeaderMethodArgumentResolver
//    从 request.getHeader("Authorization")
//    提取 token = "Bearer xxx"

// 5. ModelMethodProcessor
//    直接注入 mavContainer.getModel()
```

#### 4. HttpMessageConverter 序列化

```java
// Controller 返回：Result.ok(userDTO)
// Result {success=true, data=UserDTO{id=1, nickName="张三"}}

// HttpMessageConverter 工作：
// 1. 选择 converter（根据 Accept header 或 produces）
//    默认：MappingJackson2HttpMessageConverter

// 2. 序列化
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(result);
// 输出：{"success":true,"data":{"id":1,"nickName":"张三"}}

// 3. 写入响应
response.setContentType("application/json");
response.getWriter().write(json);
```

### 数据 vs 变化原则总结

**对象引用不变（Reuse）：**
- HttpServletRequest/Response 对象本身（Filter 里 forward 除外）
- ByteBuffer 的内容（只读传递）

**对象状态变化（Mutate）：**
- Filter 修改 URI、Header、Attribute（setXxx 方法）
- MultipartResolver 包装请求
- Model 添加属性（model.addAttribute）
- 拦截器修改 ThreadLocal、Redis、Session

**对象转换（Transform）：**
- ByteBuffer → CoyoteRequest（协议解析）
- CoyoteRequest → RequestFacade（包装）
- JSON 字符串 ↔ Java Bean（序列化/反序列化）
- Model → ModelAndView → Request Attributes
- Map（Redis）→ UserDTO（Bean 转换）

**副作用（Side Effect）：**
- Redis 续期（expire）
- 数据库查询/写入
- Session 同步（@SessionAttributes）
- ThreadLocal 清理（remove）
- 日志记录