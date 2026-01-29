# Bridge（桥接）

> **类型**：模式（Pattern）

## 一句话
Bridge 将抽象与实现分离并以组合连接，使抽象层与实现层可以独立变化。

## 严格定义
设抽象层接口为 $A$，实现层接口为 $I$。桥接模式要求抽象对象持有实现引用 `impl: I`，并将抽象操作映射为对 `impl` 的调用；从而抽象扩展（`RefinedAbstraction`）与实现扩展（`ConcreteImplementor`）互不依赖。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `Abstraction` / `RefinedAbstraction`
  - `Implementor` / `ConcreteImplementor`
- 约束：
  - 抽象层依赖实现层抽象而非具体实现类；映射边界由抽象层定义。

## 常用构造/操作（仅列出接口与符号）
- 组合：`Abstraction(Implementor impl)`
- 委托：`abstraction.operation() -> impl.operationImpl()`

## 关系：上级/下级/等价/特例/推广
- 相关：Strategy（见 [Strategy.md](Strategy.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → Bridge → GoFDesignPatterns。

