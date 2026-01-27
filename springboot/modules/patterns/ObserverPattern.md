# Observer Pattern（观察者模式）

> **类型**：模式（Pattern）/ 机制（Mechanism）

## 一句话
观察者模式（Observer Pattern）是对象间的一对多依赖关系：当被观察者状态变化时，会通过通知机制触发所有已注册观察者的更新逻辑。

## 严格定义
设被观察者对象 $S$ 维护观察者集合 $\mathcal{O}$，通知操作为 $\mathrm{notify}$。观察者模式定义一组操作，使得：
- 注册：$\mathrm{subscribe}(o)$ 将 $o$ 加入 $\mathcal{O}$
- 取消：$\mathrm{unsubscribe}(o)$ 将 $o$ 从 $\mathcal{O}$ 移除
- 通知：当 $S$ 发生某个事件/状态变化 $e$ 时，执行 $\mathrm{notify}(e)$，对所有 $o \in \mathcal{O}$ 调用 $o.\mathrm{update}(e)$

该定义强调“订阅集合的维护”和“事件触发时的批量通知”两个机制点；事件载荷的结构、同步/异步与错误处理属于实现策略。

## 交互面：数据 + 约束
- 数据：
  - 观察者集合 $\mathcal{O}$
  - 事件/状态变化载荷 $e$
- 输入：
  - `subscribe/unsubscribe`（订阅集合变更）
  - `notify`（触发一次通知）
- 输出：
  - 对观察者的回调调用（副作用）
- 约束：
  - 通知是触发式（push）：仅在发生 $e$ 且执行 `notify` 时进行一次分发。
  - 观察者的选择规则（全部/按类型过滤/按条件过滤）、顺序与并发属于实现细节。

## 常用构造/操作（仅列出接口与符号）
- 订阅接口：`subscribe(o)` / `unsubscribe(o)`
- 通知接口：`notify(e)`
- 过滤与排序（可选）：`match(e, o)` / `order(o)`

## 关系：上级/下级/等价/特例/推广
- 特例：事件驱动（Event-driven）中的“事件发布/订阅”。
- 推广：反应式流（Reactive Streams）中的订阅与背压机制（语义与约束不同）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → ObserverPattern →（events 相关条目引用）。

