# Facade（外观）

> **类型**：模式（Pattern）

## 一句话
Facade 为子系统提供一个统一的高层接口，使客户端主要依赖该接口而非子系统内部协作细节。

## 严格定义
设子系统对象集合为 $S=\\{s_1,\\dots,s_n\\}$。外观对象 $F$ 定义一组高层操作，将对 $S$ 的常用调用序列封装为更粗粒度的接口，使客户端的依赖边界主要落在 $F$ 上。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `Facade`
  - `Subsystem`（多个子组件）
- 约束：
  - Facade 不要求禁止直接访问子系统，但其接口用于收敛默认协作路径。

## 常用构造/操作（仅列出接口与符号）
- 封装：`facade.operation()` 内部调用 `s1/s2/...`

## 关系：上级/下级/等价/特例/推广
- 相关：Adapter（见 [Adapter.md](Adapter.md)）、Mediator（见 [Mediator.md](Mediator.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → Facade → GoFDesignPatterns。

