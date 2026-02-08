# Builder（生成器）

> **类型**：模式（Pattern）

## 一句话
Builder 将复杂对象的构建过程拆分为可组合的步骤，并通过分步调用与最终收口操作生成目标对象，从而使构建过程与对象表示相互独立。

## 严格定义
设目标对象为 $O$，构建步骤集合为 $\\{s_1,\\dots,s_n\\}$。Builder 定义构建接口 $B$ 与步骤操作 $s_i$，并（可选）由 Director 以确定顺序调用步骤，使得相同步骤序列在不同 `ConcreteBuilder` 下可得到不同表示的对象 $O'$。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `Builder`：步骤接口
  - `ConcreteBuilder`：具体表示的构建实现
  - `Director`（可选）：步骤编排者
  - `Product`：最终对象
- 约束：
  - 中间状态与最终状态的可见性需要由接口定义（例如仅 `build()` 后可对外暴露）。

## 常用构造/操作（仅列出接口与符号）
- 分步：`setX(...)` / `addY(...)`
- 收口：`build()`
- 编排：`director.construct(builder)`

## 关系：上级/下级/等价/特例/推广
- 相关：Abstract Factory（见 [AbstractFactory.md](AbstractFactory.md)）、Template Method（见 [TemplateMethod.md](TemplateMethod.md)）。

## 具体例子
看不懂上面的抽象定义？看这个实际例子：
- [Builder-Example.md](Builder-Example.md) - 用"组装电脑"的具体代码，对比不用Builder的痛苦 vs 用Builder的优雅

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → Builder → GoFDesignPatterns。

