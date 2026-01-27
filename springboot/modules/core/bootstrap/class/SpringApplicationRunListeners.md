# SpringApplicationRunListeners（RunListener 组合与分发器）

> **类型**：类（Class）/ 组合机制（Composite Mechanism）

## 一句话
`SpringApplicationRunListeners` 是 `SpringApplicationRunListener` 的组合分发器：持有一组 RunListener，并在 `SpringApplication.run()` 的各阶段把回调按顺序转发给每个监听器。

## 严格定义
在 Spring Boot 中，`org.springframework.boot.SpringApplicationRunListeners` 是包级可见的内部协作类，封装了 `Collection<SpringApplicationRunListener>` 并提供与接口同名的阶段方法（`starting/environmentPrepared/.../failed`），用于将一次启动过程的阶段信号转发给多个 RunListener 实现。

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
- 构造：`new SpringApplicationRunListeners(Log log, Collection<? extends SpringApplicationRunListener> listeners)`
- 分发：`starting()` / `environmentPrepared(env)` / `contextPrepared(ctx)` / `contextLoaded(ctx)` / `started(ctx)` / `running(ctx)` / `failed(ctx, ex)`

## 关系：上级/下级/等价/特例/推广
- 上级：组合模式（Composite）。
- 下级：`SpringApplicationRunListener`（被组合的接口，见 [../interface/SpringApplicationRunListener.md](../interface/SpringApplicationRunListener.md)）。
- 调用方：`SpringApplication`（启动编排入口，见 [SpringApplication.md](SpringApplication.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → bootstrap → SpringApplicationRunListeners →（Composite / RunListener Dispatch）。
