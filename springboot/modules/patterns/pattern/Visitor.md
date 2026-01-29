# Visitor（访问者）

> **类型**：模式（Pattern）

## 一句话
Visitor 将对对象结构的一组操作从元素类中分离出来，通过新增访问者扩展操作集合而不修改元素类。

## 严格定义
设元素类型集合为 $E=\\{E_1,\\dots,E_n\\}$。访问者模式要求每个元素类型定义 `accept(Visitor v)`，访问者接口定义对每个元素类型的 `visit(E_i e)`，并通过 `accept` 触发双重分派，使 `v` 的具体类型与 `e` 的具体类型共同决定执行的 `visit` 分支。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `Element(accept)`
  - `Visitor(visitE1..visitEn)`
  - `ObjectStructure`（可选）：元素集合
- 约束：
  - 新增元素类型通常需要修改访问者接口；新增操作通常通过新增 Visitor 完成。

## 常用构造/操作（仅列出接口与符号）
- 接受：`element.accept(visitor)`
- 访问：`visitor.visit(element)`

## 关系：上级/下级/等价/特例/推广
- 相关：Composite（见 [Composite.md](Composite.md)）、Interpreter（见 [Interpreter.md](Interpreter.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → Visitor → GoFDesignPatterns。

