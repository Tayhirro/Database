---
title: AbstractProtocol（Tomcat 协议处理器抽象基类）
date: "2026-01-31"
categories:
  - springboot
tags:
  - springboot/web
  - tomcat
description: 类型：类（Class）
type: class
---
# AbstractProtocol（Tomcat 协议处理器抽象基类）

> **类型**：类（Class）

## 一句话
`AbstractProtocol` 是 Tomcat `ProtocolHandler` 的抽象基类：提供通用的启动流程骨架，并在 `start()` 时调用其 `endpoint.start()` 以启动网络端点与相关运行态执行单元。

## 严格定义
在 Tomcat 中，`org.apache.coyote.AbstractProtocol` 实现 `ProtocolHandler`；其 `start()` 的典型结构包含：调用 `endpoint.start()` 启动端点，并调度/创建若干协议相关的辅助任务（例如通过 `ScheduledExecutorService` 调度周期性任务）。协议栈的具体行为由 `endpoint` 具体实现与子类策略决定。

## 继承链（接口链 / 实现链）
- 继承链：`java.lang.Object` → `AbstractProtocol` →（具体协议实现）。
- 实现接口：`ProtocolHandler`（见 [../interface/ProtocolHandler.md](../interface/ProtocolHandler.md)）。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `endpoint: AbstractEndpoint`（网络端点）
  - `utilityExecutor: ScheduledExecutorService`（辅助任务调度器）
- 字段与状态（面向“线程/执行器”理解；字段名可能随 Tomcat 版本变化）：
  - `endpoint`：协议处理器持有的网络端点；端点内部组织 accept/poll 与 worker executor（见 [AbstractEndpoint.md](AbstractEndpoint.md)）
  - `utilityExecutor`：协议级辅助任务调度器（例如周期性维护/监控任务）；与请求处理 worker 线程池是不同角色
  - 线程相关配置向端点传递：协议处理器层通常承载“把 maxThreads/maxConnections/acceptCount 等参数落到端点/线程池”的适配入口（具体由协议实现决定）
- 字段与状态（面向“协议解析与适配”理解；字段名可能随 Tomcat 版本变化）：
  - `adapter`（常见命名）：`CoyoteAdapter` 的引用，用于把 Coyote 请求/响应适配为 Catalina/Servlet 处理链路（见 [CoyoteAdapter.md](CoyoteAdapter.md)）
  - `processor`/processor cache（常见命名）：`Processor` 实例的创建与复用结构（例如缓存/回收栈），用于降低每连接/每请求的对象创建开销（见 [../interface/Processor.md](../interface/Processor.md)、[Http11Processor.md](Http11Processor.md)）
  - `handler`/connection handler（常见命名）：端点回调与连接事件分发入口：将 socket wrapper 的事件转发给 `Processor` 处理（语义级；实现细节依版本）
- 约束：
  - endpoint 的类型决定了 I/O 模型与线程组织方式；`AbstractProtocol` 只提供启动骨架与协议级别的组织点。

## 常用构造/操作（仅列出接口与符号）
- `start()`：触发 `endpoint.start()` 并启动/调度辅助任务
- `stop()`：停止端点与相关任务（边界由实现定义）

## 代码示例
### 通过 TomcatServletWebServerFactory 定制 AbstractProtocol 参数（Boot/Servlet/Tomcat 场景）
前提：应用使用 embedded Tomcat，且存在 `TomcatServletWebServerFactory`。

```java
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

@Bean
WebServerFactoryCustomizer<TomcatServletWebServerFactory> customizeTomcatProtocol() {
  return factory -> factory.addConnectorCustomizers(connector -> {
    ProtocolHandler handler = connector.getProtocolHandler();
    if (handler instanceof AbstractProtocol<?> protocol) {
      protocol.setMaxThreads(200);
      protocol.setAcceptCount(100);
      protocol.setMaxConnections(8192);
    }
  });
}
```

### 通过反射获取 endpoint 并读取线程计数（实现细节依赖）
前提：`ProtocolHandler` 的具体类型为 `AbstractProtocol`；`getEndpoint()` 为受保护成员，使用反射调用。

```java
import java.lang.reflect.Method;
import org.apache.coyote.AbstractProtocol;
import org.apache.tomcat.util.net.AbstractEndpoint;

AbstractEndpoint<?, ?> endpointOf(AbstractProtocol<?> protocol) throws Exception {
  Method m = AbstractProtocol.class.getDeclaredMethod("getEndpoint");
  m.setAccessible(true);
  return (AbstractEndpoint<?, ?>) m.invoke(protocol);
}
```

## 关系：上级/下级/等价/特例/推广
- 上级：`ProtocolHandler`（见 [../interface/ProtocolHandler.md](../interface/ProtocolHandler.md)）。
- 下级：`AbstractEndpoint`（见 [AbstractEndpoint.md](AbstractEndpoint.md)）。
- 相关：Tomcat 线程与执行器模型（见 [../mechanism/TomcatThreadingAndExecutors.md](../mechanism/TomcatThreadingAndExecutors.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → AbstractProtocol。
