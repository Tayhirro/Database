---
title: SpringApplicationRunListeners（RunListener 组合与分发器）
date: "2026-01-30"
categories:
  - springboot
description: 类型：类（Class）/ 组合机制（Composite Mechanism）
---
# SpringApplicationRunListeners（RunListener 组合与分发器）

> **类型**：类（Class）/ 组合机制（Composite Mechanism）

## 一句话
`SpringApplicationRunListeners` 是 `SpringApplicationRunListener` 的组合分发器：持有一组 RunListener，并在 `SpringApplication.run()` 的各阶段把回调按顺序转发给每个监听器。

## 严格定义
在 Spring Boot 中，`org.springframework.boot.SpringApplicationRunListeners` 是包级可见的内部协作类，封装了 `Collection<SpringApplicationRunListener>` 并提供与接口同名的阶段方法（`starting/environmentPrepared/.../failed`），用于将一次启动过程的阶段信号转发给多个 RunListener 实现。

## 继承链（接口链 / 实现链）
- 继承链：`java.lang.Object` → `SpringApplicationRunListeners`。
- 实现接口：无。

## 接口：数据 + 约束
- 数据：
  - `listeners`：`List<SpringApplicationRunListener>`（构造时由外部集合拷贝得到）
  - `log`：用于记录失败回调中的异常
- 输入：
  - 各阶段方法的参数集合（`ConfigurableEnvironment`、`ConfigurableApplicationContext`、`Throwable`）
- 输出：
  - 无返回值（副作用为调用每个 `SpringApplicationRunListener` 的对应方法）
- 约束：
  - 分发为触发式（push）：仅在 `SpringApplication.run()` 进入相应阶段时调用一次。
  - `failed(context, ex)` 分发对单个监听器的异常进行捕获与记录；当 `context == null` 时，异常可能被重新抛出（由实现细节决定）。

## 常用构造/操作（仅列出接口与符号）

### 构造
- `new SpringApplicationRunListeners(Log log, Collection<? extends SpringApplicationRunListener> listeners)`

### 阶段分发方法（按执行顺序）

| 方法 | 触发时机 | 对应事件 | 说明 |
|------|----------|----------|------|
| `starting()` | run() 最开始 | `ApplicationStartingEvent` | Environment/Context 均未创建；最早钩子 |
| `environmentPrepared(env)` | Environment 就绪后 | `ApplicationEnvironmentPreparedEvent` | 配置文件已加载，profiles 已解析 |
| `contextPrepared(ctx)` | Context 创建后 | `ApplicationContextInitializedEvent` | Initializers 已执行，refresh 之前 |
| `contextLoaded(ctx)` | BeanDefinition 加载后 | `ApplicationPreparedEvent` | 配置类已解析，refresh 即将开始 |
| `started(ctx)` | refresh 完成后 | `ApplicationStartedEvent` | **Bean 已就绪，Runner 未执行**；适合预热 |
| `running(ctx)` | Runner 执行后 | `ApplicationReadyEvent` | **应用完全就绪对外服务** |
| `failed(ctx, ex)` | 启动失败时 | `ApplicationFailedEvent` | context 可能为 null，异常被记录或重抛 |

### 阶段时序图

```
SpringApplication.run()
    │
    ▼
starting() ────────────────────────▶ 发布 ApplicationStartingEvent
    │                                  (Environment 未创建)
    ▼
environmentPrepared(env) ──────────▶ 发布 ApplicationEnvironmentPreparedEvent
    │                                  (Environment 就绪)
    ▼
contextPrepared(ctx) ──────────────▶ 发布 ApplicationContextInitializedEvent
    │                                  (Context 已创建，refresh 之前)
    ▼
contextLoaded(ctx) ────────────────▶ 发布 ApplicationPreparedEvent
    │                                  (BeanDefinition 已加载)
    ▼
context.refresh()                    (Bean 实例化)
    │
    ▼
started(ctx) ──────────────────────▶ 发布 ApplicationStartedEvent
    │                                  ★ Bean 就绪，但 Runner 未执行
    ▼
ApplicationRunner /                  (用户自定义启动逻辑)
CommandLineRunner.run()
    │
    ▼
running(ctx) ──────────────────────▶ 发布 ApplicationReadyEvent
    │                                  ★ 完全就绪，对外服务
    │
└─── 启动成功

异常路径：
    │
    ▼
failed(ctx, ex) ───────────────────▶ 发布 ApplicationFailedEvent
```

## 关系：上级/下级/等价/特例/推广
- 上级：组合模式（Composite）。
- 下级：`SpringApplicationRunListener`（被组合的接口，见 [../interface/SpringApplicationRunListener.md](../interface/SpringApplicationRunListener.md)）。
- 调用方：`SpringApplication`（启动编排入口，见 [SpringApplication.md](SpringApplication.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → bootstrap → SpringApplicationRunListeners →（Composite / RunListener Dispatch）。
