---
title: web（模块总览）
date: "2026-02-01"
categories:
  - springboot
tags:
  - springboot/web
  - moc
description: 本页用于汇总 modules/web/ 下的机制与入口，并用关系边表达"启动/运行态"的对象连接方式。
type: index
---
# web（模块总览）

> 本页用于汇总 `modules/web/` 下的机制与入口，并用关系边表达"启动/运行态"的对象连接方式。

导航：[springboot/README.md](../../README.md) | [springboot/索引.md](../../索引.md) | [springboot/概念图.md](../../概念图.md)

## 目录结构（模块内）
- `modules/web/class/`：类条目
- `modules/web/interface/`：接口条目
- `modules/web/mechanism/`：机制条目
- `modules/web/server/`：具体服务器实现（Tomcat/Netty 等）

---

## 架构演化背景：Spring MVC 的双模引擎

> **核心认知**：Spring MVC 诞生于 JSP 时代（2004），但为了兼容 20 年技术演化，同时支持 **SSR（服务器端渲染）** 和 **API（前后端分离）** 两种模式。理解这个历史背景，才能理解为什么 `DispatcherServlet` 里同时存在 `ModelAndView` 和 `@ResponseBody` 两条链路。

### 时代划分

| 时期 | 主流架构 | 后端返回 | 前端职责 | Spring MVC 核心组件 |
|------|----------|----------|----------|-------------------|
| **2005-2015** | SSR（服务器端渲染） | 完整 HTML | 无（浏览器直接展示） | `ModelAndView` + `ViewResolver` + JSP/Thymeleaf |
| **2015-至今** | CSR（前后端分离） | JSON 数据 | Vue/React 渲染 DOM | `@ResponseBody` + `HttpMessageConverter` |

### SSR 时代：ModelAndView 是核心

```java
// 2008 年的典型写法
@Controller
public class UserController {
    @GetMapping("/user/{id}")
    public ModelAndView getUser(@PathVariable Long id) {
        User user = service.findById(id);
        ModelAndView mv = new ModelAndView("user/detail"); // 视图名 → user/detail.jsp
        mv.addObject("user", user);                        // 模型数据 → ${user.name}
        return mv; // 后端渲染完整 HTML 返回给浏览器
    }
}
```

**数据流**：`Controller` → `Model`（数据）+ `View`（模板名）→ `ViewResolver` 解析模板 → `View.render()` 填充数据 → 完整 HTML

### API 时代：@ResponseBody 绕过 View 渲染

```java
// 2020 年的典型写法
@RestController  // = @Controller + @ResponseBody
public class ApiController {
    @GetMapping("/api/user/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        return service.findById(id); // 直接返回对象 → Jackson 序列化 → JSON
    }
}
```

**数据流**：`Controller` → 返回对象 → `HttpMessageConverter`（Jackson）→ JSON 写入 Response → **跳过 View 渲染**

### 为什么保留两套机制？

1. **存量系统**：银行/政务/国企 80% 系统基于 JSP/Thymeleaf 构建（2005-2015），Spring 必须向后兼容
2. **SEO 需求**：搜索引擎爬虫执行 JS 能力有限，服务端直出 HTML 更利于收录
3. **首屏性能**：弱网环境下，直接接收 HTML 比先下载 JS 再渲染更快（Next.js/Nuxt.js 的 SSR 本质上是回归）
4. **特殊场景**：邮件模板、PDF 生成、管理后台页面等仍需服务端渲染

---

## HTTP 请求完整数据流转（分层概览）

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           第一层：网络与传输层                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│ 浏览器 → TCP/IP → Tomcat Endpoint → ByteBuffer → CoyoteRequest             │
│ 数据变化：二进制字节流 → HTTP 协议解析 → ServletRequest 对象                 │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│                           第二层：Servlet 容器层                              │
├─────────────────────────────────────────────────────────────────────────────┤
│ CoyoteAdapter → RequestFacade → FilterChain → Engine → Host → Context      │
│                → Wrapper → DispatcherServlet                                │
│ 数据变化：协议对象 → HttpServletRequest/Response → Filter 处理 → Servlet    │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│                           第三层：Spring MVC 处理层                          │
├─────────────────────────────────────────────────────────────────────────────┤
│ DispatcherServlet 内部组件链：                                               │
│ HandlerMapping → HandlerExecutionChain → Interceptor → HandlerAdapter      │
│ → [Model生命周期] → ReturnValueHandler → HttpMessageConverter → View      │
│ → Interceptor.afterCompletion                                               │
│ 数据变化：Request → Handler → Model填充 → 返回值处理 → 响应序列化          │
└─────────────────────────────────────────────────────────────────────────────┘
```

### SSR vs API：第三层内部的分支判断

> **关键理解**：Step 1-2 对两种模式完全相同，**分支点在 Step 3 的返回值处理阶段**。

```
                    DispatcherServlet.doDispatch()
                              │
                    ┌─────────┴─────────┐
                    │  Step 1-2: 路由匹配   │  ← 两种模式完全相同
                    │  HandlerMapping     │
                    │  Interceptor.pre    │
                    └─────────┬─────────┘
                              │
                    ┌─────────┴─────────┐
                    │  Step 3: 执行 Handler │
                    │  参数解析 + 业务逻辑   │
                    └─────────┬─────────┘
                              │
              ┌───────────────┴───────────────┐
              │         返回值类型判断          │  ← 分支点
              └───────────────┬───────────────┘
                              │
         ┌────────────────────┼────────────────────┐
         │                    │                    │
         ▼                    ▼                    ▼
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│  SSR 分支        │  │  API 分支        │  │  void 分支      │
│  返回 String/    │  │  @ResponseBody  │  │  直接操作       │
│  ModelAndView   │  │  或 ResponseEntity│  │  Response      │
└────────┬────────┘  └────────┬────────┘  └────────┬────────┘
         │                    │                    │
         ▼                    ▼                    ▼
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│ ViewResolver    │  │ HttpMessage-    │  │ 无后续处理      │
│ 解析视图名       │  │ Converter       │  │ (已写入)        │
│ → View.render() │  │ (Jackson)       │  │                 │
│ → HTML 模板填充  │  │ → JSON 序列化    │  │                 │
└────────┬────────┘  └────────┬────────┘  └────────┬────────┘
         │                    │                    │
         ▼                    ▼                    ▼
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│ mv != null      │  │ mv = null       │  │ mv = null       │
│ 触发 render()   │  │ requestHandled  │  │ requestHandled  │
│                 │  │ = true          │  │ = true          │
└────────┬────────┘  └────────┬────────┘  └────────┬────────┘
         │                    │                    │
         └────────────────────┼────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    │  Step 4-6: 后处理   │  ← 两种模式相同
                    │  postHandle        │
                    │  afterCompletion   │
                    └───────────────────┘
```

**判断逻辑源码位置**：`DispatcherServlet.processDispatchResult()`:

```java
private void processDispatchResult(HttpServletRequest request, 
                                   HttpServletResponse response,
                                   HandlerExecutionChain mappedHandler, 
                                   ModelAndView mv,  // API 场景下为 null
                                   Exception exception) throws Exception {
    
    if (mv != null && !mv.wasCleared()) {
        // 【SSR 分支】渲染视图
        render(mv, request, response);
    }
    // 【API 分支】mv == null，response 已被 HttpMessageConverter 写入，直接结束
    
    // 无论哪条分支，都会触发 afterCompletion
    if (mappedHandler != null) {
        mappedHandler.triggerAfterCompletion(request, response, null);
    }
}
```

---

## 第一层：网络到 Servlet 容器（Tomcat）

### 数据转换链路

```
浏览器 HTTP 请求
    ↓
[TCP 数据包] (二进制：IP头+TCP头+HTTP报文)
    ↓ 转换：OS 内核 TCP 三次握手
Tomcat Acceptor 线程（NioEndpoint）
    ↓ 转换：SocketChannel → ByteBuffer
[ByteBuffer] (8KB buffer，原始 HTTP 字节)
    ↓ 转换：HTTP 协议解析
Http11Processor
    输入：47 45 54 20 2F ... (十六进制字节)
    解析：method="GET", uri="/user/1", protocol="HTTP/1.1"
    解析：headers={Host="api.example.com", Accept="application/json"}
    ↓ 转换：字节 → 结构化对象
[org.apache.coyote.Request] (Tomcat 内部对象)
    ↓ 转换：创建安全包装
CoyoteAdapter
    ↓ 转换：CoyoteRequest → RequestFacade
[org.apache.catalina.connector.RequestFacade] 
    实现：HttpServletRequest 接口
    数据：封装了 method/uri/headers/inputStream
```

### 关键变化说明

| 组件 | 输入 | 输出 | 核心变化 |
|------|------|------|----------|
| **NioEndpoint** | SocketChannel (网络连接) | ByteBuffer | 读取 TCP 流，封装为缓冲区 |
| **Http11Processor** | ByteBuffer (原始字节) | CoyoteRequest | HTTP 协议解析：提取请求行、头、体 |
| **CoyoteAdapter** | CoyoteRequest (内部实现) | RequestFacade | Facade 模式：对外暴露标准 Servlet 接口 |

---

## 第二层：Servlet 容器链（Filter → Servlet）

### 容器链与 Filter 处理

```
RequestFacade (HttpServletRequest)
    ↓ 进入 FilterChain
Filter 1 (例：CharacterEncodingFilter)
    变化：request.setCharacterEncoding("UTF-8")
    ↓
Filter 2 (例：UrlRewriteFilter)
    输入：GET http://www.api.example.com/user/1
    变化：URI 重写 → /user/1 (去掉 www)
    变化：request.setAttribute("originalUri", "...")
    ↓
Filter N ...
    ↓ 到达 DispatcherServlet
DispatcherServlet (继承 HttpServlet)
    service(request, response) → doDispatch(request, response)
```

### Filter 数据变化示例

```java
// Filter 可以修改的数据
public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) {
    // 1. 修改请求属性（常用）
    req.setAttribute("requestTime", System.currentTimeMillis());
    
    // 2. 修改 URI（URL 重写）
    HttpServletRequest wrappedReq = new HttpServletRequestWrapper(req) {
        @Override
        public String getRequestURI() {
            return req.getRequestURI().replace("/api/v1", "/api/v2");
        }
    };
    
    // 3. 包装请求（修改输入流，如解密）
    HttpServletRequest decryptedReq = new DecryptRequestWrapper(req);
    
    chain.doFilter(wrappedReq, res); // 传递修改后的请求
}
```

---

## 第三层：Spring MVC 核心处理链（DispatcherServlet 内部）

### 整体流程概览

```
DispatcherServlet.doDispatch(request, response)
    │
    ├─ Step 1: HandlerMapping
    │   输入：HttpServletRequest (URL=/user/1, method=GET)
    │   处理：根据 @RequestMapping 匹配 Handler
    │   输出：HandlerExecutionChain
    │         { handler=UserController.getUser(Long), 
    │           interceptors=[AuthInterceptor, LogInterceptor] }
    │
    ├─ Step 2: HandlerInterceptor.preHandle
    │   顺序执行拦截器
    │   AuthInterceptor: 从 Header 取 token → 查 Redis → ThreadLocal.set(user)
    │   LogInterceptor: 记录请求日志
    │   输出：boolean (true=放行, false=拦截)
    │   说明：若某个拦截器 preHandle=false，会立刻触发「已通过 preHandle 的拦截器」afterCompletion 并结束请求
    │
    ├─ Step 3: HandlerAdapter 驱动执行
    │   输入：HandlerExecutionChain, HttpServletRequest
    │   处理：调用 RequestMappingHandlerAdapter.handle(...) → invokeHandlerMethod()
    │   内部：
    │     3.1 参数解析：HandlerMethodArgumentResolver（@PathVariable/@RequestParam/@RequestBody...）
    │     3.2 执行 Controller 方法（业务逻辑）
    │     3.3 返回值处理：HandlerMethodReturnValueHandler
    │         - 视图场景：构建 ModelAndView（viewName + model），交给后续渲染
    │         - @ResponseBody 场景：RequestResponseBodyMethodProcessor → HttpMessageConverter(Jackson) 序列化 → 写入 response
    │           并标记 requestHandled=true（不再走视图渲染）
    │   输出：ModelAndView（视图）或 null（已写入 response）
    │
    ├─ Step 4: HandlerInterceptor.postHandle
    │   在 handler 正常返回后、渲染前回调（倒序执行）；若 handler 抛异常则不会执行，直接进入 Step 5.1
    │   备注：@ResponseBody 场景下，此时通常 response 已经在 Step 3 写入/提交（postHandle 仍会被调用，mv 可能为 null）
    │
    ├─ Step 5: 结果处理（异常处理 / 视图渲染）
    │   5.1 异常分支：HandlerExceptionResolver 处理异常 → 写入 response 或返回错误视图
    │   5.2 视图分支（仅当 mv != null 且未 requestHandled）：DispatcherServlet.render(mv)
    │       ModelAndView → View.render() → HTML
    │       数据：Model 属性 → Request Attributes → 模板填充 (${user.name})
    │
    └─ Step 6: HandlerInterceptor.afterCompletion
        最终回调（类似 finally，倒序执行）：清理资源（ThreadLocal.remove() 等）、记录总耗时（包含异常场景）
```

补充（异步请求）：如果 Controller 返回 `Callable` / `DeferredResult` / `WebAsyncTask` 等触发异步处理，`DispatcherServlet` 会在 Step 3 检测到并提前结束当前线程；后续异步完成会触发一次新的 async dispatch，再继续走后面的拦截器与结果处理流程。

---

### 深入 Step 3：HandlerAdapter 内部详细流程

这是 Spring MVC 最复杂的部分，包含 **Model 生命周期、参数解析、返回值处理**。
（可理解为上面「Step 3」的展开）
### HandlerAdapter 内部子流程

```
RequestMappingHandlerAdapter.invokeHandlerMethod(request, response, handlerMethod)
    │
    ├─ 3.0 创建 ModelAndViewContainer (MVC 容器)
    │   ModelAndViewContainer mavContainer = new ModelAndViewContainer();
    │   Model model = mavContainer.getModel(); // 空的 BindingAwareModelMap
    │
    ├─ 3.1 Model 初始化
    │   输入：空 Model
    │   处理：
    │     - @SessionAttributes: 从 Session 恢复属性
    │     - @ModelAttribute 方法: 执行并添加返回值到 Model
    │   输出：初始化后的 Model (可能已有 currentTime, user 等)
    │
    ├─ 3.2 参数解析 (ArgumentResolvers)
    │   输入：HTTP 请求各部分
    │   解析器工作：
    │     - @PathVariable: 从 URL 路径提取 /user/{id} → id=1
    │     - @RequestParam: 从 QueryString 提取 ?name=zhang → name="zhang"
    │     - @RequestBody: 从 Body 提取 JSON → 反序列化为 UserDTO
    │     - Model: 直接注入当前 Model
    │   输出：Controller 方法参数数组
    │
    ├─ 3.3 执行 Controller 方法
    │   输入：解析后的参数 + Model
    │   Controller 业务逻辑：
    │     User user = userService.findById(1L);
    │     model.addAttribute("user", user);  // 【Model 关键变化】
    │     model.addAttribute("timestamp", System.currentTimeMillis());
    │   返回：String(视图名) 或 ModelAndView 或 @ResponseBody 对象
    │
    ├─ 3.4 返回值处理 (ReturnValueHandlers)
    │   输入：Controller 返回值
    │   处理器选择：
    │     a) 返回 String "user/detail": 
    │        → 创建 ModelAndView("user/detail", model)
    │     b) 返回 ModelAndView:
    │        → 合并当前 Model
    │     c) @ResponseBody (UserDTO):
    │        → 调用 HttpMessageConverter 直接写入 Response
    │
    └─ 3.5 视图渲染或响应写入
        a) 模板引擎 (Thymeleaf/JSP):
           Model → Request.setAttribute() → 模板填充 → HTML
        b) @ResponseBody:
           Java Object → HttpMessageConverter → JSON → Response
```

---

### 现代 RESTful API 完整流程（@RestController 专题）

> **本节重点**：如果你使用的是 `@RestController` + JSON API（前后端分离架构），这里是你真正关心的流程。上面的 Model 生命周期、ViewResolver 等内容可以**直接跳过**。

#### 典型 API 请求全链路

```
浏览器/App 发起请求：POST /api/users  Content-Type: application/json
Body: {"name": "张三", "email": "zhangsan@example.com"}
                              │
                              ▼
┌──────────────────────────────────────────────────────────────────────────┐
│  第一层：网络层（与 SSR 完全相同）                                          │
│  TCP → Tomcat NioEndpoint → Http11Processor → CoyoteRequest             │
│  → CoyoteAdapter → RequestFacade (HttpServletRequest)                   │
└──────────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────────────────┐
│  第二层：Filter 链（与 SSR 完全相同）                                       │
│  CharacterEncodingFilter → CorsFilter → ... → DispatcherServlet         │
└──────────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────────────────┐
│  第三层：DispatcherServlet.doDispatch()                                   │
├──────────────────────────────────────────────────────────────────────────┤
│  Step 1: HandlerMapping 路由匹配                                          │
│    RequestMappingHandlerMapping.getHandler(request)                      │
│    URL: /api/users + Method: POST → UserController.createUser()         │
│    输出: HandlerExecutionChain {handler, interceptors}                   │
├──────────────────────────────────────────────────────────────────────────┤
│  Step 2: Interceptor.preHandle()                                         │
│    JwtAuthInterceptor: 验证 Token → ThreadLocal.set(currentUser)         │
│    LogInterceptor: 记录请求开始                                           │
├──────────────────────────────────────────────────────────────────────────┤
│  Step 3: HandlerAdapter.handle() 【API 模式核心差异在这里】                 │
│  ┌────────────────────────────────────────────────────────────────────┐  │
│  │  3.1 参数解析 (ArgumentResolver)                                    │  │
│  │    @RequestBody UserDTO dto                                        │  │
│  │    → RequestResponseBodyMethodProcessor.resolveArgument()          │  │
│  │    → HttpMessageConverter (Jackson) 反序列化                        │  │
│  │    → JSON String → UserDTO 对象                                    │  │
│  │                                                                    │  │
│  │  3.2 执行 Controller 方法                                           │  │
│  │    UserController.createUser(dto) {                                │  │
│  │        User user = userService.save(dto);                          │  │
│  │        return Result.success(user);  // 返回包装对象                 │  │
│  │    }                                                               │  │
│  │                                                                    │  │
│  │  3.3 返回值处理 (ReturnValueHandler) 【关键分支点】                   │  │
│  │    检测到 @RestController/@ResponseBody                             │  │
│  │    → RequestResponseBodyMethodProcessor.handleReturnValue()        │  │
│  │    → mavContainer.setRequestHandled(true)  // 标记：跳过视图渲染    │  │
│  │    → writeWithMessageConverters()                                  │  │
│  │        → MappingJackson2HttpMessageConverter                       │  │
│  │        → ObjectMapper.writeValue(response.getOutputStream(), obj)  │  │
│  │        → JSON 直接写入 HttpServletResponse                          │  │
│  │    → return null (不创建 ModelAndView)                              │  │
│  └────────────────────────────────────────────────────────────────────┘  │
├──────────────────────────────────────────────────────────────────────────┤
│  Step 4: Interceptor.postHandle()                                        │
│    仍会调用，但 ModelAndView 参数为 null（无意义）                          │
├──────────────────────────────────────────────────────────────────────────┤
│  Step 5: processDispatchResult()                                         │
│    mv == null && requestHandled == true                                  │
│    → 【跳过 ViewResolver 和 View.render()】                               │
│    → 直接进入 afterCompletion                                            │
├──────────────────────────────────────────────────────────────────────────┤
│  Step 6: Interceptor.afterCompletion()                                   │
│    ThreadLocal.remove()、记录响应时间、释放资源                            │
└──────────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
浏览器/App 收到响应：HTTP 200 OK  Content-Type: application/json
Body: {"code":0,"message":"success","data":{"id":1,"name":"张三"}}
```

#### API 模式下被跳过的组件

| 组件 | SSR 模式 | API 模式 | 说明 |
|------|----------|----------|------|
| `ModelAndViewContainer.model` | 核心使用 | 存在但无意义 | API 模式下不需要向模板传递数据 |
| `@ModelAttribute` 方法 | 自动执行 | **仍会执行但无用** | 建议 API Controller 不要使用 |
| `@SessionAttributes` | 自动恢复 | **仍会执行但无用** | API 模式应使用 Token/Redis 管理状态 |
| `ViewResolver` | 核心组件 | **完全跳过** | `requestHandled=true` 时不触发 |
| `View.render()` | 核心组件 | **完全跳过** | 同上 |
| `Model → Request.setAttribute()` | 必须 | **不发生** | JSON 直接写入 Response |

#### @ResponseBody 如何触发跳过逻辑？

```java
// RequestResponseBodyMethodProcessor.handleReturnValue() 源码简化
@Override
public void handleReturnValue(Object returnValue, 
                              MethodParameter returnType,
                              ModelAndViewContainer mavContainer, 
                              NativeWebRequest webRequest) throws Exception {
    
    // 【关键】标记请求已处理，告诉 DispatcherServlet 跳过视图渲染
    mavContainer.setRequestHandled(true);
    
    // 获取 Request/Response
    ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
    ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);
    
    // 直接序列化并写入 Response（绕过 View 体系）
    writeWithMessageConverters(returnValue, returnType, inputMessage, outputMessage);
}
```

#### API 模式下的 Model 定位

虽然 `ModelAndViewContainer` 在 API 模式下仍然会创建，但它的 `model` 字段实际上**不参与数据传输**：

```
SSR 模式数据流：
Controller → model.addAttribute() → Model → View.render() → HTML ${} 替换

API 模式数据流：
Controller → return Object → HttpMessageConverter → JSON → Response
                ↑
             Model 被绕过，数据直接从返回值序列化
```

**最佳实践**：在 `@RestController` 中，**不要注入 `Model` 参数**，直接返回 POJO/DTO 即可。

```java
// ❌ 不推荐（SSR 风格混入 API）
@RestController
public class BadApiController {
    @GetMapping("/api/user/{id}")
    public void getUser(@PathVariable Long id, Model model, HttpServletResponse response) {
        model.addAttribute("user", userService.findById(id)); // Model 在 API 模式无意义
        // 然后手动写 response？很乱
    }
}

// ✅ 推荐（纯 API 风格）
@RestController
public class GoodApiController {
    @GetMapping("/api/user/{id}")
    public Result<UserDTO> getUser(@PathVariable Long id) {
        return Result.success(userService.findById(id)); // 直接返回，清晰简洁
    }
}
```

---

### 3.1 Model 生命周期详解（SSR 场景）

> **注意**：本节内容主要适用于 SSR（服务器端渲染）场景。如果你使用 `@RestController` + JSON API，可以跳过本节。

```
Model 状态变化时间线：
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
时间点               Model 内容                      触发者
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
创建时               {} (空 Map)                     HandlerAdapter
初始化后             {currentTime=2024-01-15T10:30}  @ModelAttribute 方法
                   {user=User@123} (从 Session 恢复) @SessionAttributes
Controller 执行前    同上                            保持不变
Controller 执行中    {currentTime, user,              model.addAttribute()
                    product=Product@456, 
                    message="查询成功"}
Controller 返回后    同上                            复制到 ModelAndView
视图渲染时           同上                            设置到 Request
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

**Model 代码示例（SSR 场景）：**

> **注意**：以下代码仅适用于 SSR（服务器端渲染）场景。如果你使用 `@RestController`，请跳过本示例。

```java
@Controller
@SessionAttributes("user")  // 声明 Session 属性
public class ProductController {
    
    // 每个请求前自动执行
    @ModelAttribute("currentTime")
    public LocalDateTime addCurrentTime() {
        return LocalDateTime.now();  // 自动添加到 Model
    }
    
    @GetMapping("/product/{id}")
    public String getProduct(@PathVariable Long id, Model model) {
        // 此时 model 已有: {currentTime, user} (user 从 session 恢复)
        
        Product product = productService.findById(id);
        model.addAttribute("product", product);   // Model 变化！
        model.addAttribute("category", "electronics");
        
        // 最终 Model: {currentTime, user, product, category}
        return "product/detail";
    }
}
```

---

### 3.4 返回值处理与 HttpMessageConverter 详解

这是 **REST API 最核心的转换机制**，同时支持 SSR 和 API 两种场景。

#### 场景 A：模板引擎（返回视图名）—— SSR 场景

> 适用于：`@Controller` + Thymeleaf/JSP

```java
@Controller
public class PageController {
    @GetMapping("/user/{id}")
    public String userPage(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "user/detail";  // 视图名
    }
}

处理流程：
1. ReturnValueHandler: ViewNameMethodReturnValueHandler
2. 创建: ModelAndView(viewName="user/detail", model={user})
3. ViewResolver: 解析为 ThymeleafView(/templates/user/detail.html)
4. View.render(): 
   - model.asMap().forEach((k,v) -> request.setAttribute(k,v))
   - 模板引擎替换 ${user.name} 等表达式
   - 生成 HTML 写入 response
```

#### 场景 B：@ResponseBody（返回 JSON）—— API 场景（现代主流）

> 适用于：`@RestController` + 前后端分离

```java
@RestController
public class ApiController {
    @GetMapping("/api/user/{id}")
    @ResponseBody  // 标记返回体直接序列化
    public UserDTO getUser(@PathVariable Long id) {
        return userService.findById(id);  // 直接返回对象
    }
    
    // 或使用统一包装
    @GetMapping("/api/users")
    public Result<List<UserDTO>> listUsers() {
        List<UserDTO> users = userService.list();
        return Result.success(users);  // 返回 Result 对象
    }
}
```

**@ResponseBody 处理详细流程：**

```
Controller 返回: UserDTO {id=1, name="张三"}
    ↓
ReturnValueHandler: RequestResponseBodyMethodProcessor
    ↓ 标记 requestHandled=true（不需要视图渲染）
    ↓ 调用 writeWithMessageConverters()
HttpMessageConverter 选择：
    ↓ 遍历所有 Converters，找到能处理的
    ByteArrayHttpMessageConverter? 不支持 UserDTO
    StringHttpMessageConverter? 不支持 UserDTO
    MappingJackson2HttpMessageConverter? canWrite(UserDTO.class, JSON) → true!
    ↓
MappingJackson2HttpMessageConverter.write():
    ObjectMapper mapper = getObjectMapper();
    mapper.writeValue(response.getOutputStream(), userDTO);
    ↓ 序列化
JSON 输出: {"id":1,"name":"张三"}
    ↓ 写入
HttpServletResponse (Content-Type: application/json)
```

#### HttpMessageConverter 类型与选择机制

```java
// Spring Boot 默认注册的 Converters（按优先级排序）

// 1. 字节数组 (处理 byte[])
ByteArrayHttpMessageConverter
    - 读取: application/octet-stream, */*
    - 写入: application/octet-stream, */*
    - 用途: 文件下载(byte[])

// 2. 字符串 (处理 String)
StringHttpMessageConverter
    - 读取: text/plain, */*
    - 写入: text/plain, */*
    - 用途: 纯文本响应

// 3. 资源 (处理 org.springframework.core.io.Resource)
ResourceHttpMessageConverter
    - 读取: */*
    - 用途: 文件下载(Resource)

// 4. 表单数据 (处理 MultiValueMap)
AllEncompassingFormHttpMessageConverter
    - 读取: application/x-www-form-urlencoded
    - 写入: application/x-www-form-urlencoded, multipart/form-data

// 5. XML (处理 XML 注解的对象)
MappingJackson2XmlHttpMessageConverter (如果添加了 jackson-dataformat-xml)
    - 读取: application/xml, text/xml
    - 写入: application/xml, text/xml
    - 用途: XML API

// 6. JSON (主要，处理任意对象)
MappingJackson2HttpMessageConverter (Spring Boot 默认)
    - 读取: application/json, application/*+json
    - 写入: application/json, application/*+json
    - 用途: REST API (最常用)
    
// 7. 其他可能的 Converter
// - GsonHttpMessageConverter (如果使用 Gson 而非 Jackson)
// - ProtobufHttpMessageConverter (如果使用 Protobuf)
```

**Converter 选择算法：**

```java
// 伪代码：writeWithMessageConverters()
void writeWithMessageConverters(T value, MethodParameter returnType, 
                               HttpOutputMessage outputMessage) {
    // 1. 确定目标媒体类型
    List<MediaType> acceptableTypes = getAcceptableMediaTypes(request);
    // 从 request.getHeader("Accept") 解析
    // 默认: [application/json, */*]
    
    List<MediaType> producibleTypes = getProducibleMediaTypes(returnType);
    // 从 @RequestMapping.produces 或默认配置
    // 默认: [application/json]
    
    MediaType selectedMediaType = selectMediaType(acceptableTypes, producibleTypes);
    // 选择最佳匹配: application/json
    
    // 2. 查找 Converter
    for (HttpMessageConverter<?> converter : messageConverters) {
        // 检查是否能写这种类型
        if (converter.canWrite(value.getClass(), selectedMediaType)) {
            // 执行转换
            ((HttpMessageConverter<T>) converter).write(value, selectedMediaType, outputMessage);
            return;
        }
    }
    
    throw new HttpMediaTypeNotAcceptableException("找不到合适的 Converter");
}
```

#### 自定义 HttpMessageConverter 示例

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 添加自定义 Converter 到最前面
        converters.add(0, new CustomResultConverter());
    }
}

// 自定义 Converter：专门处理 Result 对象
public class CustomResultConverter extends AbstractHttpMessageConverter<Result<?>> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public CustomResultConverter() {
        super(MediaType.APPLICATION_JSON);
    }
    
    @Override
    protected boolean supports(Class<?> clazz) {
        return Result.class.isAssignableFrom(clazz);
    }
    
    @Override
    protected Result<?> readInternal(Class<? extends Result<?>> clazz, 
                                    HttpInputMessage inputMessage) {
        // 不需要读，只写
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected void writeInternal(Result<?> result, HttpOutputMessage outputMessage) 
            throws IOException {
        
        // 自定义序列化逻辑
        OutputStream out = outputMessage.getBody();
        
        // 对 data 进行二次序列化
        String dataJson = objectMapper.writeValueAsString(result.getData());
        
        String response = String.format(
            "{\"code\":%d,\"message\":\"%s\",\"data\":%s,\"timestamp\":%d}",
            result.getCode(),
            result.getMessage(),
            dataJson,
            System.currentTimeMillis()
        );
        
        out.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
```

---

## 第四层：响应返回流程（反向链路）

```
Controller 返回值处理完毕
    ↓
HandlerInterceptor.postHandle (很少使用)
    ↓
如果是 @ResponseBody:
    HttpMessageConverter 已写入 HttpServletResponse
    ↓
如果是视图渲染:
    View.render() → HTML 写入 HttpServletResponse
    ↓
HandlerInterceptor.afterCompletion
    - ThreadLocal.remove() (清理用户上下文)
    - 记录总耗时
    ↓
FilterChain 返回 (逆向穿过所有 Filter)
    ↓
Wrapper (Tomcat)
    ↓
Context → Host → Engine → CoyoteAdapter
    ↓
Http11Processor: 序列化 Response 为 HTTP 字节
    ↓
NioEndpoint: 写入 SocketChannel
    ↓
TCP 发送
    ↓
浏览器接收
```

---

## 数据转换总结表（全链路）

| 阶段 | 组件 | 输入 | 输出 | 数据变化说明 |
|------|------|------|------|-------------|
| **网络** | OS + Tomcat | TCP 报文 | SocketChannel | 三次握手，建立连接 |
| **I/O** | NioEndpoint | SocketChannel | ByteBuffer | 读取字节流到 buffer |
| **协议** | Http11Processor | ByteBuffer | CoyoteRequest | HTTP 解析：字节→结构化对象 |
| **适配** | CoyoteAdapter | CoyoteRequest | RequestFacade | Facade 包装 |
| **过滤** | FilterChain | HttpServletRequest | HttpServletRequest | 可修改 URI、Header、Attribute |
| **路由** | HandlerMapping | HttpServletRequest | HandlerExecutionChain | URL 匹配到 Controller 方法 |
| **拦截** | Interceptor | Request/Response | boolean | 鉴权、日志、ThreadLocal 操作 |
| **Model** | HandlerAdapter | - | Model | 创建 → 初始化(@ModelAttribute) → 填充(Controller.addAttribute) |
| **参数** | ArgumentResolver | HTTP 各部分 | Controller 参数 | @PathVariable/@RequestParam/@RequestBody 解析 |
| **返回** | ReturnValueHandler | Controller 返回值 | ModelAndView/void | 视图名/ModelAndView/@ResponseBody 分支 |
| **序列化** | HttpMessageConverter | Java Bean | HTTP Body | JSON/XML 序列化 (Object→字节) |
| **视图** | View (Thymeleaf/JSP) | Model | HTML | Model→Request Attributes→模板渲染 |
| **清理** | afterCompletion | - | void | ThreadLocal.remove(), 资源释放 |

---

## SSR vs API 架构全景对比

> **知识库定位指南**：根据你的项目架构，选择对应的学习路径。

### 核心差异速查表

| 维度 | SSR（服务器端渲染） | API（前后端分离） |
|------|-------------------|------------------|
| **Controller 注解** | `@Controller` | `@RestController` (= `@Controller` + `@ResponseBody`) |
| **方法返回值** | `String`（视图名）/ `ModelAndView` | POJO / `ResponseEntity<T>` / `Result<T>` |
| **数据传递方式** | `Model.addAttribute()` → 模板 `${}` | 返回值 → Jackson → JSON |
| **核心处理器** | `ViewNameMethodReturnValueHandler` | `RequestResponseBodyMethodProcessor` |
| **序列化组件** | `ViewResolver` + `View.render()` | `HttpMessageConverter`（Jackson） |
| **响应 Content-Type** | `text/html` | `application/json` |
| **前端技术栈** | JSP / Thymeleaf / Freemarker | Vue / React / Angular |
| **SEO 友好度** | 天然友好（HTML 直出） | 需要 SSR 框架（Next.js/Nuxt.js） |
| **首屏性能** | 快（直接返回完整 HTML） | 慢（需加载 JS 后渲染） |
| **状态管理** | `@SessionAttributes` / HttpSession | Token + Redis（无状态） |
| **典型年代** | 2005-2015 | 2015-至今 |

### 组件参与度对比

```
                          SSR 模式                    API 模式
                        ─────────────              ─────────────
Step 1: HandlerMapping      ✅ 使用                   ✅ 使用
Step 2: Interceptor.pre     ✅ 使用                   ✅ 使用
        ┌───────────────────────────────────────────────────────────────┐
Step 3: │ ModelAndViewContainer  ✅ 核心                 ⚪ 存在但无意义    │
        │ @ModelAttribute        ✅ 自动执行              ⚪ 执行但无用     │
        │ @SessionAttributes     ✅ 自动恢复              ⚪ 不推荐使用     │
        │ ArgumentResolver       ✅ 使用                 ✅ 使用（@RequestBody 核心）│
        │ ReturnValueHandler     ViewName → ModelAndView  @ResponseBody → JSON │
        └───────────────────────────────────────────────────────────────┘
Step 4: Interceptor.post    ✅ mv 有值                 ⚪ mv 为 null
Step 5: ViewResolver        ✅ 解析视图名              ❌ 跳过
        View.render()       ✅ 模板填充               ❌ 跳过
Step 6: afterCompletion     ✅ 清理资源               ✅ 清理资源

图例：✅ 核心使用  ⚪ 存在但无实际作用  ❌ 完全跳过
```

### 选择建议

| 场景 | 推荐架构 | 理由 |
|------|----------|------|
| **新项目、前后端分离、移动端 API** | `@RestController` + JSON | 现代标准，生态成熟 |
| **管理后台（简单 CRUD）** | `@RestController` + Vue Admin | 开发效率高 |
| **需要 SEO 的官网/博客** | Thymeleaf SSR 或 Next.js/Nuxt.js | 爬虫友好 |
| **邮件模板、PDF 生成** | Thymeleaf + `@Controller` | 服务端渲染 HTML |
| **老旧系统维护** | 保持原有 JSP/Thymeleaf | 避免大规模重构 |
| **微服务内部通信** | `@RestController` + JSON/Protobuf | 性能优先 |

### 你的智能医疗项目应该用哪个？

根据现代项目架构，**99% 的情况应该使用 `@RestController` + JSON API**：

```java
// ✅ 你的项目应该长这样
@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {
    
    @GetMapping("/{id}")
    public Result<PatientDTO> getPatient(@PathVariable Long id) {
        return Result.success(patientService.findById(id));
    }
    
    @PostMapping
    public Result<PatientDTO> createPatient(@RequestBody @Valid PatientCreateRequest request) {
        return Result.success(patientService.create(request));
    }
}
```

**这意味着**：
- 本文档中关于 `Model`、`@ModelAttribute`、`@SessionAttributes`、`ViewResolver`、`View.render()` 的内容，你可以作为**了解性知识**
- 重点关注：`@RequestBody`/`@ResponseBody`、`HttpMessageConverter`、`ArgumentResolver`、`ReturnValueHandler`

---

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

#### 组件用途与注意事项（语义级）

| 组件 | 用途（做什么） | 注意事项（不做什么 / 边界） |
|------|---------------|---------------------------|
| `Connector` | Tomcat 的网络入口组件：绑定端口并把连接处理委派给其持有的 `ProtocolHandler`，作为 Catalina（容器）侧与 Coyote（协议栈）侧的连接点。 | 不定义 accept/poller/worker 的线程模型；线程与执行器的组织在 `ProtocolHandler/Endpoint` 侧。 |
| `ProtocolHandler` | 协议处理器接口：表达"某种协议栈如何 start/stop 并处理连接"的生命周期边界，作为 `Connector` 与具体协议实现之间的接口。 | 只表达生命周期与职责边界，不固定线程模型与连接管理细节。 |
| `AbstractProtocol` | `ProtocolHandler` 的常见抽象基类：提供协议处理器的通用骨架，并在 `start()` 中调用 `endpoint.start()` 启动端点。 | 其职责偏"协议处理器层"的组织点；worker executor 与 accept/poll 线程角色通常由 endpoint 组织。 |
| `AbstractEndpoint` | 网络端点抽象：端口监听、连接管理、I/O 轮询/事件分发，并把请求处理任务投递到 `Executor`（内部创建或外部注入）。 | 端点的具体实现随 I/O 模型变化（NIO/NIO2/APR 等）；线程命名、线程数量、以及 executor 的具体类型属于实现细节。 |

---

## Spring MVC 组件（接口层）
- `HandlerMapping`：见 [interface/HandlerMapping.md](interface/HandlerMapping.md)
- `HandlerAdapter`：见 [interface/HandlerAdapter.md](interface/HandlerAdapter.md)
- `HandlerInterceptor`：见 [interface/HandlerInterceptor.md](interface/HandlerInterceptor.md)
- `HandlerMethodArgumentResolver`：见 [interface/HandlerMethodArgumentResolver.md](interface/HandlerMethodArgumentResolver.md)
- `HandlerMethodReturnValueHandler`：见 [interface/HandlerMethodReturnValueHandler.md](interface/HandlerMethodReturnValueHandler.md)
- `HandlerExceptionResolver`：见 [interface/HandlerExceptionResolver.md](interface/HandlerExceptionResolver.md)
- `HttpMessageConverter`：见 [interface/HttpMessageConverter.md](interface/HttpMessageConverter.md)

## WebServer 抽象（接口层）
- `WebServer`：见 [interface/WebServer.md](interface/WebServer.md)
- `WebServerFactory`：见 [interface/WebServerFactory.md](interface/WebServerFactory.md)
- `ServletWebServerFactory`：见 [interface/ServletWebServerFactory.md](interface/ServletWebServerFactory.md)
- `ReactiveWebServerFactory`：见 [interface/ReactiveWebServerFactory.md](interface/ReactiveWebServerFactory.md)

## Web 作用域（WebScopes）
- Web 作用域（request/session）：见 [mechanism/WebScopes.md](mechanism/WebScopes.md)
- ThreadLocalContext（线程绑定上下文）：见 [mechanism/ThreadLocalContext.md](mechanism/ThreadLocalContext.md)

## 前端控制器（可选）
- `DispatcherServlet`（Spring MVC）：见 [class/DispatcherServlet.md](class/DispatcherServlet.md)
- `DispatcherHandler`（Spring WebFlux）：见 [class/DispatcherHandler.md](class/DispatcherHandler.md)
