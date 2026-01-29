# Command（命令）

> **类型**：模式（Pattern）

## 一句话
Command 将请求封装为对象，使请求可参数化、排队、记录、撤销，并将请求发起者与执行者解耦。

## 严格定义
设接收者为 $R$，操作为 $op$。命令对象 $c$ 封装对接收者的引用与参数集合，并提供统一执行入口 `execute()`（可选 `undo()`），使调用方仅依赖命令接口而不依赖接收者的具体实现。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `Command(execute[, undo])`
  - `ConcreteCommand(receiver, args...)`
  - `Receiver`
  - `Invoker`（可选）
- 约束：
  - 撤销语义需要定义状态保存位置与粒度（常与 Memento 组合）。

## 常用构造/操作（仅列出接口与符号）
- 执行：`execute()`
- 撤销：`undo()`（可选）
- 队列：`Queue<Command>`

## 关系：上级/下级/等价/特例/推广
- 相关：Memento（见 [Memento.md](Memento.md)）、Chain of Responsibility（见 [ChainOfResponsibility.md](ChainOfResponsibility.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → Command → GoFDesignPatterns。

