# Interpreter（解释器）

> **类型**：模式（Pattern）

## 一句话
Interpreter 为一种语言定义其文法表示，并定义解释器以解释该语言中的句子（通常以 AST 的形式组织表达式）。

## 严格定义
设语言文法为 $G$，表达式集合为 $E$。解释器模式为 $G$ 的关键产生式定义表达式类（AST 节点）并提供 `interpret(context)`，使得对任一 $e\\in E$，`e.interpret(ctx)` 返回在上下文 $ctx$ 下的语义结果。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `AbstractExpression(interpret)`
  - `TerminalExpression` / `NonterminalExpression`
  - `Context`
- 约束：
  - 语法扩展通常对应新增表达式类；性能与可维护性受 AST 规模影响。

## 常用构造/操作（仅列出接口与符号）
- 构建：AST（`Expression` 组合）
- 求值：`interpret(context)`

## 关系：上级/下级/等价/特例/推广
- 相关：Composite（见 [Composite.md](Composite.md)）、Visitor（见 [Visitor.md](Visitor.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → Interpreter → GoFDesignPatterns。

