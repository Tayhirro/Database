# TwoPhaseInitialization（两阶段初始化）

> **类型**：模式（Pattern）

## 一句话
Two-Phase Initialization 将对象的“配置/装配阶段”和“启动生效阶段”分离，要求在显式的第二阶段入口被调用后对象才进入可使用的运行态。

## 严格定义
给定对象 $O$，若其可观察状态可区分为至少两类：配置态 $C$ 与运行态 $R$，并且存在显式操作 $init$ 使 $O$ 从 $C$ 迁移到 $R$，同时规定在 $init$ 之前对外能力集合受限（例如不保证某些服务可用），则称该设计满足 Two-Phase Initialization。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - 配置载体：定义/规则/策略的集合（如注册表、配置对象、扩展点集合）
  - 状态：`CONFIGURING` / `RUNNING`（或等价划分）
  - 显式阶段入口：`init()` / `start()` / `refresh()`（名称依实现而定）
- 约束：
  - $init$ 之前允许变更配置载体；$init$ 之后配置变化的语义需被限定（禁止、延迟生效或以增量机制处理）。
  - 对外可用性应与状态一致：运行态才提供完整服务集合。

## 常用构造/操作（仅列出接口与符号）
- 构建：`new O()` / `builder.configure(...)`
- 配置：`register(...)` / `setEnvironment(...)` / `addProcessor(...)`
- 启动：`init()`（将配置转化为运行时结构并进入可用态）

## 关系：上级/下级/等价/特例/推广
- 上级：显式生命周期与状态迁移（见 [../concept/LifecycleStateMachine.md](../concept/LifecycleStateMachine.md)）。
- 相关：
  - 装配与运行分离：见 [../principle/SeparationOfWiringAndRunning.md](../principle/SeparationOfWiringAndRunning.md)
  - 模板方法：见 [TemplateMethod.md](TemplateMethod.md)
- 例化：
  - `ApplicationContext.refresh()`：见 [../../core/context/mechanism/ContextRefresh.md](../../core/context/mechanism/ContextRefresh.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → TwoPhaseInitialization → core/context/mechanism/ContextRefresh。

