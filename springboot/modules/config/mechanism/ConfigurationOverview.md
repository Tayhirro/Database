# 配置体系总览（Configuration Overview）

> **类型**：总述 / 指南（Guide）

## 一句话
本页旨在建立 Spring Boot 配置体系的**统一心智模型**，厘清“配置从哪来”、“何时生效”以及“Starter 与应用如何协作”的核心逻辑。

## 1. 核心模型：两条并行线
Spring Boot 的配置体系可以抽象为两条并行工作的线：

1.  **配置供给线（Externalized Configuration）**
    - **职责**：负责收集数据。
    - **内容**：命令行、环境变量、配置文件（yml/properties）、配置中心等。
    - **产物**：统一的 `Environment` 视图（Key-Value 字典）。
2.  **配置消费线（Configuration Logic）**
    - **职责**：负责使用数据做决策。
    - **内容**：自动配置类（AutoConfiguration）、条件注解（`@Conditional`）、属性绑定（`@ConfigurationProperties`）。
    - **产物**：Spring Bean（如 `DataSource`, `RedisTemplate`）。

> **误区澄清**：第三方 Starter（如 redis-starter）通常只负责**第 2 条线**（定义如何用配置创建 Bean），而不负责**第 1 条线**（它不会自动读取 jar 包内的 yml 作为配置源）。配置值通常由**应用侧**提供。

## 2. 核心对象：Environment
`Environment` 是配置的**最终统一视图**，它是“覆盖规则”的本体。详情见 [Environment.md](../../core/context/interface/Environment.md)。
- **查询逻辑**：按 `PropertySource` 列表顺序查找，**First Win（先命中者胜出）**。

## 3. 时间线：Env 前、中、后
理解配置生效时机是排查问题的关键：

| 阶段 | 时间点 | 可用来源 | 典型任务 |
| :--- | :--- | :--- | :--- |
| **A. Env 前** | `starting` 阶段 | System Properties, OS Env, Args | 日志引导、系统属性设置、埋点 |
| **B. Env 内** | `environmentPrepared` | 配置文件 (`application.yml`) 被加载进 Env | 属性覆盖、配置源拼装 |
| **C. Env 后** | `context.refresh` | 完整的 Environment | Bean 创建、自动配置、属性绑定 |

> **关键点**：在阶段 A（Env 前），你读不到 `application.yml` 里的配置。此时只能依赖系统属性或环境变量。

## 4. 配置来源与优先级（80% 常用）
按优先级**从高到低**排列（越靠前越优先）：

1.  **命令行参数** (`--server.port=9000`) - 临时覆盖神器。
2.  **JVM 系统属性** (`-Dserver.port=9000`) - 启动前必须知道的配置。
3.  **OS 环境变量** (`SERVER_PORT=9000`) - 容器/K8s 注入。
4.  **应用配置文件** (`application-{profile}.yml` / `application.yml`) - 日常开发运维。
5.  **代码默认值** (`SpringApplication.setDefaultProperties`) - 兜底。

## 5. 注入与扩展方式
- **配置文件注入**：Boot 自动通过 `ConfigFileApplicationListener` (2.x) 或 `ConfigDataEnvironmentPostProcessor` (2.4+) 将 yml 解析为 PropertySource。
- **命令行注入**：`SpringApplication` 自动将 `args` 包装为 PropertySource。
- **自定义注入**：通过实现 `EnvironmentPostProcessor` 接口，在阶段 B 插入自定义配置源（如从远程 Vault 读取）。

## 6. 消费方式（How to use）
1.  **`@Value`**：适合简单、零散的字段读取。
2.  **`@ConfigurationProperties`**：**主流推荐**。类型安全、批量绑定、支持验证（见 [ConfigurationProperties.md](ConfigurationProperties.md)）。
3.  **`@ConditionalOnProperty`**：基于配置值决定是否加载某个 Bean（开关逻辑）。

## 7. 调试手段
当配置不生效时，排查三板斧：
1.  **看顺序**：打印 `environment.getPropertySources()`，确认来源顺序。
2.  **Actuator**：访问 `/actuator/env`，查看最终值及来源（Hit Source）。
3.  **Debug 日志**：调整配置加载相关包的日志级别，观察文件加载过程。

## 导航
- 外部化配置机制：[ExternalizedConfiguration.md](ExternalizedConfiguration.md)
- 属性绑定详解：[ConfigurationProperties.md](ConfigurationProperties.md)
- 自动配置原理：[AutoConfiguration.md](AutoConfiguration.md)
- 环境接口定义：[../../core/context/interface/Environment.md](../../core/context/interface/Environment.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → config → ConfigurationOverview → （Timeline / Sources / Consumption）。
