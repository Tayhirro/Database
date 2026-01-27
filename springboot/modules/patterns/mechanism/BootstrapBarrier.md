# BootstrapBarrier（启动屏障）

> **类型**：机制（Mechanism）

## 一句话
启动屏障是一种将多项依赖顺序与就绪条件收敛到单一入口的组织方式，使系统在跨越该入口前后具有不同的可用性边界与一致性保证。

## 严格定义
设系统初始化需要满足约束集合 $\mathcal{C}$（例如基础设施就位、扩展点注册完成、对象图构建完成），若存在显式入口 $B$，使得：
1) 在 $B$ 之前允许对配置与扩展点进行累积性变更；
2) $B$ 执行期间按确定顺序建立满足 $\mathcal{C}$ 的运行时结构；
3) $B$ 之后系统对外暴露满足预期的能力集合；
则称 $B$ 为启动屏障；其语义不要求完全原子，但要求“阶段边界可被识别，且就绪条件被集中校验/建立”。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - 约束集合 $\mathcal{C}$：就绪条件与顺序依赖
  - 屏障入口 $B$：`refresh()` / `start()` / `run()`（名称依实现而定）
  - 失败语义：失败时的状态与资源回收策略
- 约束：
  - 进入 $B$ 前需要定义“可变更集合”（允许继续注册/修改的内容）。
  - 通过 $B$ 后需要定义“可观察就绪”的判定（事件、标志或可用能力集合）。

## 常用构造/操作（仅列出接口与符号）
- 累积配置：`register(...)` / `addProcessor(...)` / `addListener(...)`
- 跨越屏障：`bootstrap()` / `refresh()`

## 关系：上级/下级/等价/特例/推广
- 相关：
  - 两阶段初始化：见 [../pattern/TwoPhaseInitialization.md](../pattern/TwoPhaseInitialization.md)
  - 模板方法：见 [../pattern/TemplateMethod.md](../pattern/TemplateMethod.md)
  - 装配与运行分离：见 [../principle/SeparationOfWiringAndRunning.md](../principle/SeparationOfWiringAndRunning.md)
- 例化：
  - `ApplicationContext.refresh()`：见 [../../core/context/mechanism/ContextRefresh.md](../../core/context/mechanism/ContextRefresh.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → mechanism → BootstrapBarrier → core/context/mechanism/ContextRefresh。

