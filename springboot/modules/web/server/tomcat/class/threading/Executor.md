---
title: Executor（Tomcat endpoint 的 worker 执行器）
date: "2026-02-01"
categories:
  - springboot
tags:
  - springboot/web
  - tomcat
  - threading
  - executor
description: Executor 是 Tomcat endpoint 用于承载请求处理任务的执行器抽象：端点把任务投递到 Executor 所提供的 worker 线程中执行。
type: concept
---
# Executor（Tomcat endpoint 的 worker 执行器）

## 一句话
Executor 是 Tomcat endpoint 用于承载请求处理任务的执行器抽象：端点把任务投递到 Executor 所提供的 worker 线程中执行。

## 严格定义
在 Tomcat 的端点模型中，`AbstractEndpoint` 持有一个 `Executor` 视图（可能为外部注入或内部创建）。当 Poller/端点识别到连接 I/O 就绪并形成可执行任务时，会将任务投递到该 `Executor`；该任务在 worker 线程中运行，并完成协议处理链路中属于“请求处理阶段”的工作（例如读入字节、解析 HTTP、驱动 Servlet 调用、生成并写回响应等）。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `Executor`：任务执行抽象
  - 线程池实现（可选）：例如 `ThreadPoolExecutor` 或 Tomcat 的线程池实现（实现类属于实现细节）
- 输入：
  - `Runnable` 任务（请求处理任务的抽象）
- 输出：
  - 任务在 worker 线程中执行的效果（副作用）
- 约束：
  - Executor 可能是端点内部线程池（internal）或外部注入线程池（external）；两者在“线程工厂、队列与容量”处具有不同的控制点。

## 常用构造/操作（仅列出接口与符号）
- `execute(Runnable)`：投递任务（语义级）
- `AbstractEndpoint.getExecutor()`：获取端点当前使用的执行器视图（见 [../AbstractEndpoint.md](../AbstractEndpoint.md)）

## 流程（概念级：一次请求处理任务）
1. Poller 分发任务：
   - Poller 发现某连接可读/可写就绪后，将该连接对应的处理动作封装为任务并投递到 `Executor`（见 [Poller.md](Poller.md)）。
2. worker 执行任务：
   - 在 worker 线程中执行与该连接相关的处理步骤（概念级）：读取字节、解析协议、推进到应用处理链路、生成并写回响应。
3. 决策下一步（概念级）：
   - 若连接关闭：释放连接资源并取消其 `SelectionKey`（具体由端点实现）。
   - 若连接保持 keep-alive：将连接重新提交到 Poller 的“待注册集合”，使其回到 `Selector` 监管并等待下一次可读事件。
   - 若需要继续写（例如写缓冲未完成）：更新该连接在 `Selector` 上关注的事件类型（例如关注可写），以便后续由 Poller 继续分发写相关任务（实现细节依端点）。

## 关系：上级/下级/等价/特例/推广
- 上级：`AbstractEndpoint`（见 [../AbstractEndpoint.md](../AbstractEndpoint.md)）。
- 任务来源：`Poller`（见 [Poller.md](Poller.md)）。
- 相关：Tomcat 线程与执行器模型（见 [../../mechanism/TomcatThreadingAndExecutors.md](../../mechanism/TomcatThreadingAndExecutors.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → threading → Executor。
