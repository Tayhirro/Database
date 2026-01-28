# Environment（环境抽象）

> **类型**：核心接口（Core Interface）

## 一句话
`Environment` 是 Spring 应用运行时的**配置统一查询视图**，它将来自不同来源的配置（命令行、系统属性、文件、配置中心等）按优先级叠加，并提供统一的 Profile 管理和属性查询接口。

## 严格定义
`org.springframework.core.env.Environment` 是 Spring Framework 的核心接口（继承自 `PropertyResolver`）。它为应用提供了两大核心信息的访问能力：
1.  **Profiles**：当前激活了哪些环境（如 `dev`, `prod`），用于控制 Bean 或配置段的生效。
2.  **Properties**：应用属性的聚合视图（由有序的 `PropertySources` 列表组成）。

> 该接口在运行时作为“外部化配置”机制的状态载体与查询入口。

## 继承链（接口链 / 实现链）
- 接口链：
  - `PropertyResolver`（按 key 查询属性值与类型转换入口）
  - `Environment`（在 `PropertyResolver` 之上补充 profiles 与属性源聚合视图）
  - `ConfigurableEnvironment`（在 `Environment` 之上补充 profiles 与 `PropertySources` 的可变更能力）
- 常见实现类：`StandardEnvironment` / `StandardServletEnvironment` / `StandardReactiveWebEnvironment`。

### 1. `ConfigurableEnvironment`（写能力接口）
继承自 `Environment`，增加了**修改**配置的能力。
- **作用**：允许在启动期间（Context refresh 之前）添加/移除 `PropertySource`、设置激活的 Profile。
- **关键 API**：
  - `getPropertySources()`：获取可变的源列表（`MutablePropertySources`）。
  - `setActiveProfiles(String...)`：设置激活的环境。

### 2. 主要实现类
Spring Boot 根据应用类型（Web/非 Web）选择不同的实现：
- **`StandardEnvironment`**：标准非 Web 环境。默认包含系统属性（System Properties）和环境变量（System Environment）。
- **`StandardServletEnvironment`**：Servlet Web 环境。额外包含 `ServletConfig` 和 `ServletContext` 参数。
- **`StandardReactiveWebEnvironment`**：Reactive Web 环境。

## 核心结构：两块信息

### 1. Profiles（环境隔离）
- **Active Profiles**：当前显式激活的 Profile 集合（例如 `-Dspring.profiles.active=prod`）。
- **Default Profiles**：当没有激活任何 Profile 时生效的默认集合（通常是 `default`）。
- **作用**：控制 `@Profile` 注解的 Bean 是否加载，以及 `application-{profile}.yml` 是否生效。

### 2. PropertySources（属性源列表）
这是“覆盖规则”的本体。`Environment` 内部维护了一个**有序的** `MutablePropertySources` 列表。
- **查询逻辑**：当调用 `getProperty("server.port")` 时，Environment 会按照列表顺序（优先级从高到低）依次查找各个 Source。
- **命中规则**：**先命中者胜出（First Win）**。一旦在前面的 Source 找到了 key，就不再看后面的。

#### 典型优先级（由高到低，部分示例）
1.  Devtools 全局设置（~/.spring-boot-devtools.properties）
2.  `@TestPropertySource`（测试用）
3.  **命令行参数**（Command line arguments）
4.  `SPRING_APPLICATION_JSON`（内嵌 JSON）
5.  `ServletConfig` / `ServletContext` 参数
6.  JNDI 属性
7.  **Java 系统属性**（`System.getProperties()`）
8.  **操作系统环境变量**（OS environment variables）
9.  `random.*` 属性
10. **配置文件**（`application-{profile}.properties/yml`）
11. **配置文件**（`application.properties/yml`）
12. `@PropertySource` 注解
13. 默认属性（`SpringApplication.setDefaultProperties`）

## 接口：数据 + 约束
- **输入**：key（字符串）。
- **输出**：value（字符串，或转换为特定类型）。
- **约束**：`Environment` 对象通常在 `ApplicationContext` 创建之前（`prepareEnvironment` 阶段）就已经准备就绪，并在 Context 刷新期间被锁定（部分 Source 可能变为只读）。

## 常用构造/操作
- **查询属性**：`env.getProperty("server.port")`
- **检查 Profile**：`env.acceptsProfiles("prod")`
- **编程式修改**（需转型为 `ConfigurableEnvironment`）：
  ```java
  ConfigurableEnvironment env = context.getEnvironment();
  env.getPropertySources().addFirst(new MapPropertySource("mySource", map));
  ```

## 关系：上级/下级/等价/特例/推广
- **上级**：`PropertyResolver`。
- **宿主**：`ApplicationContext`（实现了 `EnvironmentCapable`，持有一个 Environment 实例）。
- **填充者**：外部化配置机制（见 [ExternalizedConfiguration.md](../../../config/mechanism/ExternalizedConfiguration.md)）。
- **绑定**：`@ConfigurationProperties`（将 Environment 中的值绑定到对象）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → context → Environment → （Profiles / PropertySources）。
