# State（状态）

> **类型**：模式（Pattern）

## 一句话
State 将状态相关行为封装到状态对象中，并通过状态迁移改变上下文对象的行为，而不在上下文中以条件分支显式区分状态。

## 严格定义
设上下文对象为 $C$，状态集合为 $Q$。状态模式定义状态接口 `State` 与一组具体状态实现 $\\{S(q)\\}_{q\\in Q}$，并令 $C$ 持有当前状态引用 `state`；对任一行为 `request()`，$C$ 委托给 `state.handle(C)`，且允许在处理过程中触发状态迁移 `C.setState(S(q'))`。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `Context(state)`
  - `State(handle)`
  - `ConcreteState`
- 约束：
  - 状态迁移的控制权需要定义（由 Context 决定或由 State 决定）。

## 常用构造/操作（仅列出接口与符号）
- 委托：`context.request() -> state.handle(context)`
- 迁移：`context.setState(newState)`

## 关系：上级/下级/等价/特例/推广
- 相关：Strategy（见 [Strategy.md](Strategy.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → State → GoFDesignPatterns。

