# AbstractFactory（抽象工厂）

> **类型**：模式（Pattern）

## 一句话
Abstract Factory 定义一组用于创建相关对象族的接口，使客户端在不指定具体类的前提下生成属于同一族的多个产品对象。

## 严格定义
设产品族集合为 $F$，产品类型集合为 $\\{P_1,\\dots,P_k\\}$。抽象工厂给出工厂接口 $A$ 与创建操作集合 $\\{create_1,\\dots,create_k\\}$，并定义一组具体工厂实现 $\\{A_f\\}_{f\\in F}$，使得对任一 $f\\in F$、任一 $j$：
$$
create_j(A_f) \\in P_j
$$
且由同一 $A_f$ 创建的产品满足“属于同一族”的一致性约束（约束内容由问题上下文定义）。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `AbstractFactory`：多个产品的创建接口集合
  - `ConcreteFactory`：面向某一产品族的创建实现
  - `AbstractProduct` / `ConcreteProduct`：产品抽象与具体实现
- 约束：
  - 客户端依赖抽象工厂与抽象产品；产品族切换通过替换工厂实现完成。

## 常用构造/操作（仅列出接口与符号）
- 创建：`createProductA()` / `createProductB()` / ...
- 族切换：替换 `ConcreteFactory`

## 关系：上级/下级/等价/特例/推广
- 相关：Factory Method（见 [FactoryMethod.md](FactoryMethod.md)）、Builder（见 [Builder.md](Builder.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → AbstractFactory → GoFDesignPatterns。

