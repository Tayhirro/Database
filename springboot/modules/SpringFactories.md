# 扩展点发现（SpringFactoriesLoader / 声明资源聚合）

> **类型**：机制（Mechanism）

## 一句话
扩展点发现是从 classpath 聚合“接口/键 → 实现类列表”声明资源并实例化/装配到启动过程中的机制集合。

## 严格定义
在 Spring 生态中，`SpringFactoriesLoader` 以“声明资源文件 + 类加载器”为输入，输出某个工厂键（或接口类型）对应的实现类名序列，并按需完成加载与实例化。

## 接口：数据 + 约束
- 输入：
  - `ClassLoader`
  - 声明资源（例如 `META-INF/spring.factories` 或 Boot 3.x 拆分后的 imports 资源）
- 输出：
  - 实现类名列表（或已实例化对象列表，视调用点而定）
- 约束：
  - 资源路径与键空间在不同框架/版本中不同；该页只描述“聚合机制”这一抽象层。

## 常用构造/操作（仅列出接口与符号）
- Boot 2.x 常见资源：`META-INF/spring.factories`
- Boot 3.x 常见资源：
  - auto-configuration imports：`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
  - 其他类型仍可能使用 `SpringFactoriesLoader`（取决于调用点）

## 关系：上级/下级/等价/特例/推广
- 上级：启动期扩展点装配。
- 下级：`ApplicationListener` / `ApplicationContextInitializer` / auto-config 候选列表等（具体列表见各自模块页）。
- 相关：启动流程中 “构造与扩展点装配” 阶段（见 [springboot/flows/启动流程.md](../flows/启动流程.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → 扩展点发现 →（listeners/initializers/auto-config 候选）。

