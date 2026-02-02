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
    ↓ 变化：URL 路由映射（路径匹配）
DispatcherServlet (Spring MVC 入口)
    ↓ 数据转换：URI + method → HandlerExecutionChain
[HandlerExecutionChain] (包含：Controller + Interceptor 列表)
    例：{handler="UserController.login", interceptors=[RefreshTokenInterceptor, LoginInterceptor]}
    ↓ 变化：按顺序执行拦截器 preHandle
RefreshTokenInterceptor
    例：从 Header 读取 token="abc123"
    ↓ 变化：查 Redis 获取用户数据
    Map userMap = redisTemplate.opsForHash().entries("login:token:abc123")
    ↓ 数据转换：Map → UserDTO 对象（BeanUtil.fillBeanWithMap）
    UserDTO user = {id="1", nickName="张三", phone="13800138000"}
    ↓ 变化：存入 ThreadLocal（线程绑定）
    UserHolder.saveUser(user) // ThreadLocal.set(user)
    ↓ 变化：刷新 Redis 过期时间（续期）
    redisTemplate.expire("login:token:abc123", 30, TimeUnit.MINUTES)
    返回 true（放行）
LoginInterceptor
    例：检查 UserHolder.getUser() != null
    ↓ 变化：判断登录状态
    结果：已登录（放行）或 未登录（返回 401）
    response.setStatus(401) // 未登录时
    ↓ 数据转换：@RequestBody 触发 JSON 反序列化
Controller (UserController.login)
    输入 HTTP Body：{"phone":"13800138000","code":"123456"}
    ↓ 转换（Jackson ObjectMapper）：
    Content-Type: application/json → LoginFormDTO
    LoginFormDTO loginForm = {phone="13800138000", code="123456"}
    ↓ 执行业务逻辑
Service (UserServiceImpl)
    校验验证码 → 查询数据库 → 生成新 token → 保存到 Redis
    ↓ 返回结果
Controller 返回 Result 对象
    Result result = Result.ok(userDTO) // 包含 token 等信息
    ↓ 数据转换：@ResponseBody 触发序列化
    UserDTO → JSON 字符串（Jackson）
    {"success":true,"data":{"token":"xyz789","nickName":"张三"}}
    ↓ 写入 HttpServletResponse
HttpServletResponse (设置 status=200, Content-Type=application/json, body=JSON)
    ↓ 变化：拦截器 afterCompletion（清理资源）
RefreshTokenInterceptor.afterCompletion
    ↓ 变化：清理 ThreadLocal（必须！防止内存泄漏）
    UserHolder.removeUser() // ThreadLocal.remove()
    ↓ 返回给 Tomcat 容器
Tomcat CoyoteAdapter
    ↓ 数据转换：Response 对象 → HTTP 字节流
    HTTP/1.1 200 OK
    Content-Type: application/json
    Content-Length: 56
    
    {"success":true,"data":{"token":"xyz789","nickName":"张三"}}
    ↓ 写入 SocketChannel（TCP 发送）
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
| **DispatcherServlet** | HttpServletRequest | HandlerExecutionChain | URL 路由映射，匹配 Controller 和拦截器链 |
| **拦截器 preHandle** | HttpServletRequest/Response | boolean | 鉴权逻辑、ThreadLocal 存用户、Redis 续期 token |
| **Controller 入参** | HTTP Body（JSON） | Java Bean（@RequestBody） | Jackson 反序列化：JSON 字符串 → Java 对象 |
| **Controller 出参** | Java Bean/Result | HTTP Body（JSON） | Jackson 序列化：Java 对象 → JSON 字符串 |
| **拦截器 afterCompletion** | HttpServletRequest/Response | void | 清理 ThreadLocal、记录请求耗时、异常处理 |
| **Tomcat Response** | HttpServletResponse | TCP 报文 | 序列化 HTTP 响应（文本→字节），写入 Socket |

### 关键数据转换示例详解

#### 1. HTTP 协议解析（ByteBuffer → CoyoteRequest）
```
输入（16进制字节流）：
47 45 54 20 2F 75 73 65 72 2F 6C 6F 67 69 6E 3F...
对应 ASCII：GET /user/login?...

解析过程：
- 读取到空格：method = "GET"
- 读取到空格：uri = "/user/login"
- 读取到换行：protocol = "HTTP/1.1"
- 继续读取 Headers：
  Host: api.example.com\r\n
  Authorization: Bearer token123\r\n
  Content-Type: application/json\r\n
  \r\n（空行表示 Header 结束）
- 读取 Body（如果是 POST）：{...json...}

输出（CoyoteRequest 对象）：
{
  method: "GET",
  uri: "/user/login",
  protocol: "HTTP/1.1",
  headers: {
    "Host": "api.example.com",
    "Authorization": "Bearer token123",
    "Content-Type": "application/json"
  },
  body: null // GET 请求通常无 Body
}
```

#### 2. Filter 层数据变化（URL 重写示例）
```java
public class UrlRewriteFilter implements Filter {
    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) {
        // 原始 URI
        String originalUri = req.getRequestURI(); 
        // 例：/www.api/user/login 或完整 URL http://www.api.example.com/user/login
        
        if (originalUri.contains("www.")) {
            // 变化：去掉 www 前缀
            String newUri = originalUri.replace("www.", "");
            
            // 保留原始值（供后续使用）
            req.setAttribute("originalUri", originalUri);
            req.setAttribute("rewriteTime", System.currentTimeMillis());
            
            // 创建新的请求包装器（可选）
            HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(req) {
                @Override
                public String getRequestURI() {
                    return newUri; // 返回修改后的 URI
                }
            };
            
            // 用包装后的请求继续
            chain.doFilter(wrappedRequest, res);
        } else {
            chain.doFilter(req, res);
        }
    }
}

数据变化总结：
- 输入 URI：http://www.api.example.com/user/login
- 输出 URI：http://api.example.com/user/login
- 数据对象：HttpServletRequestWrapper（包装原始请求）
- 附加状态：setAttribute 添加了原始 URI 和时间戳
```

#### 3. JSON ↔ Java Bean 转换（Controller 层）
```java
// 请求到达 Controller 时的数据转换
@PostMapping("/login")
public Result login(@RequestBody LoginFormDTO loginForm) {
    // @RequestBody 触发以下转换：
}

转换过程（入参）：
1. HTTP Request Body 读取为字符串：
   "{\"phone\":\"13800138000\",\"code\":\"123456\"}"

2. Jackson ObjectMapper.readValue() 解析：
   - 读取 JSON 字段 phone → 设置到 LoginFormDTO.phone
   - 读取 JSON 字段 code → 设置到 LoginFormDTO.code
   - 类型转换：JSON 字符串 → Java String

3. 生成 Java 对象：
   LoginFormDTO loginForm = new LoginFormDTO();
   loginForm.setPhone("13800138000");
   loginForm.setCode("123456");

转换过程（出参）：
1. Controller 返回 Result 对象：
   Result result = Result.ok(userDTO);
   // Result {success=true, data=UserDTO{id=1, nickName="张三"}}

2. @ResponseBody 触发 Jackson 序列化：
   - UserDTO 对象 → JSON 对象
   - Result 对象 → 最外层 JSON

3. 输出 JSON 字符串：
   {
     "success": true,
     "data": {
       "id": 1,
       "nickName": "张三",
       "token": "xyz789"
     }
   }

4. 写入 HttpServletResponse.getOutputStream()
```

#### 4. ThreadLocal 存取（HMDP 登录态传递）
```java
// RefreshTokenInterceptor.preHandle
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    // 1. 从 Header 获取 token
    String token = request.getHeader("authorization");
    // 例：token = "abc123"
    
    // 2. 查 Redis（数据变化：网络 I/O）
    String key = "login:token:" + token;
    Map<Object, Object> userMap = redisTemplate.opsForHash().entries(key);
    // 例：userMap = {id="1", nickName="张三", phone="13800138000"}
    
    // 3. Map 转换为 UserDTO（数据转换）
    UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
    // UserDTO {id="1", nickName="张三", phone="13800138000"}
    
    // 4. 存入 ThreadLocal（关键变化！）
    UserHolder.saveUser(userDTO);
    // 实际执行：threadLocal.set(userDTO)
    // 效果：当前线程绑定用户对象，后续代码可通过 UserHolder.getUser() 获取
    
    // 5. 刷新 Redis 过期时间（副作用）
    redisTemplate.expire(key, 30, TimeUnit.MINUTES);
    // 数据变化：Redis 中该 key 的 TTL 重置为 30 分钟
    
    return true; // 放行
}

// LoginInterceptor.preHandle
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    // 从 ThreadLocal 读取（跨方法数据传递）
    UserDTO user = UserHolder.getUser();
    // threadLocal.get() → UserDTO 或 null
    
    if (user == null) {
        // 未登录，拦截
        response.setStatus(401);
        return false;
    }
    return true; // 已登录，放行
}

// afterCompletion（必须清理！）
@Override
public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    // 清理 ThreadLocal（防止内存泄漏）
    UserHolder.removeUser();
    // threadLocal.remove()
    // 数据变化：当前线程绑定的用户数据被清除
}
```

### 数据 vs 变化原则总结

**对象引用不变（Reuse）**：
- HttpServletRequest/Response 对象本身（Filter 里 forward 除外）
- ByteBuffer 的内容（只读传递，不修改）

**对象状态变化（Mutate）**：
- Filter 修改 URI、Header、Attribute（setXxx 方法）
- 拦截器修改 ThreadLocal、Redis、Session
- Controller 修改数据库、缓存、外部服务

**对象转换（Transform）**：
- ByteBuffer → CoyoteRequest（协议解析）
- CoyoteRequest → RequestFacade（包装）
- JSON 字符串 ↔ Java Bean（序列化/反序列化）
- Map（Redis）→ UserDTO（Bean 转换）

**副作用（Side Effect）**：
- Redis 续期（expire）
- 数据库查询/写入
- 日志记录
- ThreadLocal 清理（remove）
