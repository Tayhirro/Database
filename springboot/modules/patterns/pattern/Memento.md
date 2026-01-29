# Memento（备忘录）

> **类型**：模式（Pattern）

## 一句话
Memento 在不破坏封装的前提下捕获对象状态并外部化保存，使对象可在之后恢复到先前状态。

## 严格定义
设对象为 $O$，其状态空间为 $State(O)$。备忘录 $m$ 表示某一时刻的状态快照，使得 `createMemento(O)` 生成 $m$，并且 `restore(O, m)` 将 $O$ 恢复为生成 $m$ 时刻的状态；备忘录的内部内容对非 Originator 的对象不可见或不可变更。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `Originator`
  - `Memento`
  - `Caretaker`
- 约束：
  - 状态快照的范围、大小、持久化与版本数需要定义。

## 常用构造/操作（仅列出接口与符号）
- 快照：`createMemento()`
- 恢复：`restore(memento)`
- 管理：`stack.push(m)` / `stack.pop()`

## 关系：上级/下级/等价/特例/推广
- 相关：Command（见 [Command.md](Command.md)）、State（见 [State.md](State.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → Memento → GoFDesignPatterns。

