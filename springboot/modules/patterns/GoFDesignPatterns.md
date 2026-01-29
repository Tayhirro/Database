---
type: index
tags:
  - patterns/gof
---

# GoFDesignPatterns（GoF 设计模式目录：创建型/结构型/行为型）

> **类型**：目录（Catalog）

## 一句话
GoF 设计模式目录是《Design Patterns: Elements of Reusable Object-Oriented Software》（Gamma, Helm, Johnson, Vlissides, 1994）中对一组可复用的面向对象设计模式的分类与命名体系（创建型/结构型/行为型）。

## 严格定义
### 1. 设计模式（Design Pattern）
设计模式是对一类在给定上下文下反复出现的设计问题及其可复用解决结构的命名与结构化描述；其描述通常包含意图、适用性、参与者（类/对象角色）与协作关系、后果等。

### 2. GoF 目录（Catalog）
本页以 GoF 原书的条目为准：**23 个模式**，分为三类（创建型/结构型/行为型）。

## 接口：数据 + 约束
- 数据（目录项的最小信息形态）：
  - 名称：中文/英文（pattern name）
  - 归类：创建型 / 结构型 / 行为型（category）
  - 描述字段集合：意图（Intent）/ 适用性（Applicability）/ 参与者（Participants）/ 协作（Collaborations）/ 后果（Consequences）
- 约束：
  - 该目录以“面向对象设计”为默认语境；参与者以类/对象角色表达，依赖多态/组合等语言机制的存在或可模拟性。

## 常用构造/操作（仅列出接口与符号）
- 选型：在问题上下文 $C$ 下选择候选集合 $P\\subseteq\\text{Catalog}$。
- 组合：对 $p_1, p_2, \\dots$ 做组合并给出交互边界（参与者映射与职责划分）。
- 映射：将框架/库中的结构映射到模式参与者集合（roles → concrete types）。

## 目录（GoF 23）

### 创建型（Creational）
| 中文 | English |
| --- | --- |
| 抽象工厂 | Abstract Factory |
| 生成器 | Builder |
| 工厂方法 | Factory Method |
| 原型 | Prototype |
| 单例 | Singleton |

### 结构型（Structural）
| 中文 | English |
| --- | --- |
| 适配器 | Adapter |
| 桥接 | Bridge |
| 组合 | Composite |
| 装饰 | Decorator |
| 外观 | Facade |
| 享元 | Flyweight |
| 代理 | Proxy |

### 行为型（Behavioral）
| 中文   | English                 |
| ---- | ----------------------- |
| 职责链  | Chain of Responsibility |
| 命令   | Command                 |
| 解释器  | Interpreter             |
| 迭代器  | Iterator                |
| 中介者  | Mediator                |
| 备忘录  | Memento                 |
| 观察者  | Observer                |
| 状态   | State                   |
| 策略   | Strategy                |
| 模板方法 | Template Method         |
| 访问者  | Visitor                 |

## 关系：上级/下级/等价/特例/推广
- 上级：面向对象设计（Object-Oriented Design）。
- 相关（本库已建立条目）：
  - 观察者模式（Observer）：见 [pattern/ObserverPattern.md](pattern/ObserverPattern.md)
  - 模板方法（Template Method）：见 [pattern/TemplateMethod.md](pattern/TemplateMethod.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → GoFDesignPatterns →（Observer / TemplateMethod / …）。

