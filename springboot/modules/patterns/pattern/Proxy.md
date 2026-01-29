# Proxy（代理）

> **类型**：模式（Pattern）

## 一句话
Proxy 为另一个对象提供替身，以控制对该对象的访问并在访问前后插入附加行为。

## 严格定义
设主题接口为 $S$，真实对象为 $r\\in S$。代理对象 $p\\in S$ 提供与 $r$ 相同的接口，并持有对 $r$ 的引用或获取 $r$ 的能力；对任一 $op\\in S$，$p.op$ 通过 `pre -> delegate -> post` 的结构定义访问控制、延迟加载、缓存、远程通信等语义。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `Subject`
  - `RealSubject`
  - `Proxy`
- 约束：
  - Proxy 与 RealSubject 共享同一接口；代理引入的附加语义（权限/缓存一致性/延迟）需要定义。

## 常用构造/操作（仅列出接口与符号）
- 访问：`proxy.request()`（内部转发到 real）
- 延迟：`ensureRealInitialized()`

## 关系：上级/下级/等价/特例/推广
- 相关：Decorator（见 [Decorator.md](Decorator.md)）、Adapter（见 [Adapter.md](Adapter.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → Proxy → GoFDesignPatterns。

