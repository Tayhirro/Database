# 配置属性绑定（Configuration Properties Binding）

## 一句话
配置属性绑定把 `Environment` 中的键值对按规则映射到类型化对象（properties bean），用于在应用中以结构化方式消费配置。

## 严格定义
给定配置视图 `Environment` 与目标类型 `T`，绑定过程可抽象为
$$
\text{bind}:\ (\texttt{Environment},\ \text{prefix},\ T)\ \to\ T,
$$
其中 `prefix` 指定属性键空间（例如 `server.port` 的 `server` 前缀）。

Spring Boot 中常见入口为 `@ConfigurationProperties(prefix=...)` 与对应的绑定器实现（binder）。

## 接口：数据 + 约束
- 输入：
  - `Environment`（见 [springboot/modules/ExternalizedConfiguration.md](ExternalizedConfiguration.md)）
  - 目标类型（字段/构造器形态、校验约束等）
- 输出：
  - 类型化对象实例（或绑定失败信息）
- 约束：
  - 绑定规则（命名松散匹配、集合与嵌套对象展开等）属于实现细节，可随版本变化。

## 常用构造/操作（仅列出接口与符号）
- 注解入口：`@ConfigurationProperties`
- 校验：JSR-303/380 约束（如 `@Validated`）

## 关系：上级/下级/等价/特例/推广
- 上级：外部化配置。
- 相关：自动配置对配置属性的使用（见 [springboot/modules/AutoConfiguration.md](AutoConfiguration.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → 外部化配置 → 配置属性绑定。

