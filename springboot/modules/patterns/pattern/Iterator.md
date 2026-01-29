# Iterator（迭代器）

> **类型**：模式（Pattern）

## 一句话
Iterator 提供顺序访问聚合对象元素的方法而不暴露其内部表示，并将遍历算法与聚合结构分离。

## 严格定义
设聚合为 $A$，元素序列为 $\\langle e_1,\\dots,e_n\\rangle$。迭代器对象 $it$ 提供 `hasNext()` 与 `next()`（或等价操作），使客户端可通过 $it$ 遍历 $A$ 的元素，而无需了解 $A$ 的内部存储结构。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `Iterator`
  - `Aggregate(createIterator)`
- 约束：
  - 并发修改语义（fail-fast、弱一致等）需要明确。

## 常用构造/操作（仅列出接口与符号）
- 创建：`aggregate.createIterator()`
- 遍历：`hasNext()` / `next()`

## 关系：上级/下级/等价/特例/推广
- 相关：Composite（见 [Composite.md](Composite.md)）、Visitor（见 [Visitor.md](Visitor.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → Iterator → GoFDesignPatterns。

