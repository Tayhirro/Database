---
title: ServletWeb应用持续运行机制（AnnotationConfigServletWebServerApplicationContext）
date: "2026-02-03"
categories:
  - springboot
tags:
  - springboot/flow
  - web
description: "在 Servlet Web 应用形态下，SpringApplication.run(...) 本身不会阻塞；应用在 context.refresh() 过程中启动 WebServer，由 WebServer 启动的非守护线程（non-daemon threads）保持 JVM 进程持续运行，直..."
type: flow
---
# ServletWeb应用持续运行机制（AnnotationConfigServletWebServerApplicationContext）

## 一句话
在 Servlet Web 应用形态下，`SpringApplication.run(...)` 本身不会阻塞；应用在 `context.refresh()` 过程中启动 `WebServer`，由 WebServer 启动的非守护线程（non-daemon threads）保持 JVM 进程持续运行，直到 `ApplicationContext.close()` 停止 WebServer 并释放线程。

## 严格定义
给定 `WebApplicationType = SERVLET` 的一次启动调用，`SpringApplication.createApplicationContext()` 会创建一个 `ConfigurableApplicationContext` 的 Servlet Web 实现（常见为 `AnnotationConfigServletWebServerApplicationContext`）。在 `SpringApplication.refreshContext(context)` 调用 `context.refresh()` 时，该上下文在 refresh 的 hook 阶段创建并启动 `WebServer`；`WebServer.start()` 将启动端口监听与请求处理相关线程，且这些线程通常为非守护线程，从而使 JVM 在 `main` 线程结束后仍保持存活。关闭时，由 `context.close()`（或 shutdown hook）触发 WebServer 停止，线程退出后 JVM 才会自然结束。

## 接口：数据 + 约束
- 输入：
  - `SpringApplication.run(primarySources, args)`
  - `WebApplicationType` 的判定结果（servlet）
  - `ApplicationContext` 内部可用的 `ServletWebServerFactory` Bean（Tomcat/Jetty/Undertow 等）
- 输出：
  - `ConfigurableApplicationContext` 返回给调用方（run 返回）
  - `WebServer` 已启动并绑定端口（对外提供 HTTP 服务）
- 约束：
  - `run()` 的返回不等同于“进程退出/继续运行”的控制；进程存活依赖是否存在非守护线程。
  - 若 WebServer 未能创建或未启动（例如缺少 `ServletWebServerFactory`），则不会形成保活线程，进程可能在 `main` 线程结束后退出（除非存在其他非守护线程）。

## 时间线（从 run 到 JVM 保活）

### Phase 1：启动入口与上下文选择
- `main(String[] args)` 调用 `SpringApplication.run(...)`
- `createApplicationContext()` 通过 `ApplicationContextFactory` 按 `WebApplicationType` 选择并创建上下文实现：
  - `ApplicationContextFactory`：见 [../modules/core/bootstrap/interface/ApplicationContextFactory.md](../modules/core/bootstrap/interface/ApplicationContextFactory.md)
  - `ConfigurableApplicationContext`：见 [../modules/core/context/interface/ConfigurableApplicationContext.md](../modules/core/context/interface/ConfigurableApplicationContext.md)

### Phase 2：refresh 触发 WebServer 启动
- `refreshContext(context)` → `context.refresh()`
- 在 `ContextRefresh` 的 hook 阶段，Servlet Web 上下文覆写 `onRefresh()` 并触发 WebServer 创建与启动：
  - refresh 模板流程：见 [../modules/core/context/mechanism/ContextRefresh.md](../modules/core/context/mechanism/ContextRefresh.md)
- WebServer 机制总览：见 [../modules/web/mechanism/EmbeddedWebServer.md](../modules/web/mechanism/EmbeddedWebServer.md)
- WebServer 生命周期与线程保活：见 [../modules/web/mechanism/WebServerLifecycleAndThreads.md](../modules/web/mechanism/WebServerLifecycleAndThreads.md)

概念级调用链（Servlet Web）：
1) `SpringApplication.refreshContext(context)`  
2) `AnnotationConfigServletWebServerApplicationContext.refresh()`（在该实例上调用，方法实现来自 Framework 的 `AbstractApplicationContext.refresh()`）  
3) `AbstractApplicationContext.refresh()`（模板方法）  
4) `ServletWebServerApplicationContext.onRefresh()`（Boot 的 Servlet Web 上下文覆写 hook）  
5) `ServletWebServerApplicationContext.createWebServer()`（创建并写入 `webServer` 字段）  
6) `ServletWebServerFactory.getWebServer(...)` → `WebServer.start()`  
7) 容器实现启动端口监听与请求处理线程（non-daemon）

### Phase 3：run 返回但进程仍存活
- `SpringApplication.run(...)` 返回 `ApplicationContext`
- `main` 线程可以结束
- JVM 继续运行的条件是：仍存在非守护线程（典型来自 WebServer 线程池与监听线程）

### Phase 4：退出路径（关闭与线程释放）
- 触发来源：
  - 显式：`context.close()`
  - 隐式：JVM shutdown hook（若注册）
- 典型效果：
  - `ApplicationContext` 关闭阶段停止 `WebServer`，释放端口与线程资源
  - 非守护线程退出后 JVM 结束

## 关系：上级/下级/等价/特例/推广
- 上级：启动流程（Boot）：见 [启动流程.md](启动流程.md)
- 相关：
  - `DispatcherServlet`（请求进入点）：见 [../modules/web/class/DispatcherServlet.md](../modules/web/class/DispatcherServlet.md)
- 特例：Reactive Web 应用以 `ReactiveWebServerApplicationContext` 形成类似“server threads 保活”的语义，但底层容器与线程模型不同。

## 把新概念挂回框架（多级索引轨迹）
springboot → flows → ServletWeb应用持续运行机制 →（SpringApplication.run / ContextRefresh.onRefresh / EmbeddedWebServer / WebServer.start）。

---

## 附录：HTTP 请求数据流转与转换详解

### 数据转换概览（带变化细节）

```
浏览器 HTTP 请求
    ↓
[TCP 数据包] (二进制字节流)
    ↓
Tomcat Endpoint (NioEndpoint)
    ↓ 数据转换：SocketChannel → ByteBuffer
[ByteBuffer] (包含原始 HTTP 字节)
    ↓ 变化：解析 HTTP 协议（请求行、Headers、Body）
Tomcat Processor (Http11Processor)
    ↓ 数据转换：ByteBuffer → CoyoteRequest
[org.apache.coyote.Request] (内部对象，含 method/uri/headers)
    ↓ 变化：创建 Facade 安全包装
Tomcat Adapter (CoyoteAdapter)
    ↓ 数据转换：CoyoteRequest → RequestFacade
[org.apache.catalina.connector.RequestFacade] (HttpServletRequest 实现)
    ↓ 变化：经过 FilterChain（可能修改请求）
Filter 链（如：UrlRewriteFilter）
    例：GET http://www.api.example.com/user/login
    ↓ 变化：去掉 www 前缀
    例：GET http://api.example.com/user/login
    ↓ 数据不变，属性变化：setAttribute("originalUri", ...)
[HttpServletRequest] (可能被包装)
    ↓ 变化：DispatcherServlet 路由映射
DispatcherServlet (Spring MVC 入口)
    ↓ 数据转换：URI → HandlerExecutionChain
[HandlerExecutionChain] (包含 Controller + 拦截器)
    ↓ 变化：执行拦截器 preHandle
RefreshTokenInterceptor (HMDP)
    例：从 Header 取 token=abc123
    ↓ 变化：查 Redis 获取用户，存入 ThreadLocal
    UserHolder.saveUser(userDTO) → ThreadLocal 绑定
    ↓ 变化：刷新 token 有效期（Redis.expire）
LoginInterceptor (HMDP)
    例：检查 UserHolder.getUser() == null?
    ↓ 变化：如果有用户，放行；否则返回 401
    response.setStatus(401)
    ↓ 数据转换：@RequestBody 注解触发
Controller (UserController.login)
    例：POST /user/login
    Content-Type: application/json
    Body: {"phone":"13800138000","code":"123456"}
    ↓ 数据转换：HttpMessageConverter (Jackson)
    JSON 字符串 → LoginFormDTO 对象
[LoginFormDTO] (Java Bean: phone, code 字段)
    ↓ 执行业务逻辑
Service (UserServiceImpl)
    例：校验验证码 → 查数据库 → 生成 token
    ↓ 返回结果
Controller 返回 Result 对象
    ↓ 数据转换：@ResponseBody 注解触发
    Result (Java Object) → JSON 字符串
    {"success":true,"data":{"token":"xyz789"}}
    ↓ 写入 HttpServletResponse
HttpServletResponse (携带 JSON body)
    ↓ 拦截器 afterCompletion（清理资源）
RefreshTokenInterceptor.afterCompletion
    ↓ 变化：清理 ThreadLocal
    UserHolder.removeUser() // 防止内存泄漏
    ↓ 返回给 Tomcat
Tomcat CoyoteAdapter
    ↓ 数据转换：Response → HTTP 字节流
    HTTP/1.1 200 OK
    Content-Type: application/json
    {"success":true,"data":{...}}
    ↓ 写入 SocketChannel
TCP 数据包
    ↓
浏览器接收响应
```

### 各层数据变化细节表

| 层级 | 输入 | 输出 | 数据变化/处理 |
|------|------|------|--------------|
| **网络层** | TCP 报文 | SocketChannel | OS 内核处理连接 |
| **Tomcat Endpoint** | SocketChannel | ByteBuffer | 读取字节流（8KB buffer） |
| **Tomcat Processor** | ByteBuffer | CoyoteRequest | 解析 HTTP 协议（method/uri/version/headers/body） |
| **Tomcat Adapter** | CoyoteRequest | RequestFacade | 创建安全包装（Facade 模式） |
| **Filter** | HttpServletRequest | HttpServletRequest | 例：修改 URI（去掉 www）、设置编码、CORS 处理 |
| **DispatcherServlet** | HttpServletRequest | HandlerExecutionChain | URL 路由映射，绑定拦截器链 |
| **拦截器 preHandle** | HttpServletRequest/Response | boolean | 鉴权、日志、ThreadLocal 存用户 |
| **Controller** | @RequestBody + HttpServletRequest | @ResponseBody | JSON ↔ Java Bean 转换（Jackson） |
| **拦截器 afterCompletion** | HttpServletRequest/Response | void | 清理 ThreadLocal、记录耗时 |
| **Tomcat Response** | HttpServletResponse | TCP 报文 | 序列化 HTTP 响应，写入 Socket |

### 关键数据转换点详解

#### 1. HTTP 协议解析（ByteBuffer → CoyoteRequest）
```
原始数据：
47 45 54 20 2F 75 73 65 72 2F 6C 6F 67 69 6E 3F... (16进制字节)

解析后：
- method = "GET"
- uri = "/user/login"
- protocol = "HTTP/1.1"
- headers = {Host: "api.example.com", Authorization: "token123"}
- body = null（GET 请求）
```

#### 2. Filter 链示例（URL 重写）
```java
public class UrlRewriteFilter implements Filter {
    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) {
        String uri = req.getRequestURI();  // /www.api/user/login
        if (uri.startsWith("/www.")) {
            // 变化：去掉 www 前缀
            String newUri = uri.replaceFirst("/www\\.", "/");
            req.setAttribute("originalUri", uri);  // 保留原始值
            req.getRequestDispatcher(newUri).forward(req, res);
        }
        chain.doFilter(req, res);
    }
}

变化前后对比：
- 输入 URI：http://www.api.example.com/user/login
- Filter 处理后：http://api.example.com/user/login
- 数据：HttpServletRequest 对象没变，但 getRequestURI() 返回值变了
```

#### 3. JSON ↔ Java Bean 转换（Controller 层）
```java
// 请求到达 Controller 时：
HttpServletRequest.getInputStream() 包含：
{"phone":"13800138000","code":"123456"}

@RequestBody 转换过程：
1. Jackson ObjectMapper.readValue()
2. JSON 字段映射到 LoginFormDTO 属性
3. 生成 LoginFormDTO 对象

变化：
- 输入：JSON 字符串（HTTP Body）
- 输出：LoginFormDTO 对象（Java Bean）
- phone 字段 = "13800138000"
- code 字段 = "123456"
```

#### 4. ThreadLocal 存取（HMDP 特色）
相关机制：ThreadLocalContext（线程绑定上下文）：见 [../modules/web/mechanism/ThreadLocalContext.md](../modules/web/mechanism/ThreadLocalContext.md)。
```
RefreshTokenInterceptor.preHandle：
- 从 Redis 获取用户数据（Hash 结构）
- Map<String, String> userMap = {id:"1", nickName:"张三"}
- 转换：Map → UserDTO 对象
- 变化：UserHolder.saveUser(userDTO) → ThreadLocal.set(userDTO)
- 效果：当前线程绑定用户对象

LoginInterceptor.preHandle：
- 检查 UserHolder.getUser() == null?
- 变化：判断是否有登录用户
- 结果：决定返回 true（放行）或 false（拦截）

afterCompletion：
- 变化：UserHolder.removeUser() → ThreadLocal.remove()
- 效果：清理线程绑定，防止内存泄漏
```

### 总结：数据 vs 变化

**数据不变的情况**：
- HttpServletRequest/Response 对象本身（Filter 里转发除外）
- ByteBuffer 的内容（复制后传递）

**数据变化的情况**：
- Filter 修改 URI、Header、Attribute
- JSON 字符串 ↔ Java Bean 转换
- ThreadLocal 存取（线程绑定数据）
- Redis 读写（外部数据变化）

**关键原则**：
每层可能保持对象引用不变，但通过方法调用改变其内部状态或包装新的对象。
