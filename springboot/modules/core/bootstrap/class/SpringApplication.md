# SpringApplication（Spring Boot 启动编排入口）

> **类型**：类（Class）

## 一句话
`SpringApplication` 是 Spring Boot 的启动编排器：从 `primarySources` 与启动参数出发，构造 `Environment`、创建 `ApplicationContext`、触发 `refresh()` 并发布启动生命周期事件。

## 严格定义
在 Spring Boot 中，`org.springframework.boot.SpringApplication` 是应用启动入口 `SpringApplication.run(...)` 的承载对象；其目标是产生一个可用的 `ConfigurableApplicationContext`（成功时）或抛出启动失败异常（失败时）。

## 继承链（接口链 / 实现链）
- 继承链：`java.lang.Object` → `SpringApplication`。
- 实现接口：无。

## 接口：数据 + 约束
- 输入：
  - `primarySources` / `sources`：Bean 定义的初始来源集合。
    - **形式**：Java 配置类（`@Configuration`）、XML 配置文件路径（如 `classpath:context.xml`）、扫描包名（Package）。
    - **作用**：被加载到 `ApplicationContext` 以注册 Bean 定义（load beans into context）。
  - `args`：命令行参数
- 输出：
  - `ConfigurableApplicationContext`
- 可配置项（示例，非穷举）：
  - `WebApplicationType`（servlet/reactive/none）
  - listeners/initializers 集合
  - banner、启动日志等
- 约束：
  - 启动阶段事件与扩展点加载列表在不同 Boot 版本间存在差异。

## 常用构造/操作（仅列出接口与符号）
- 启动入口：`SpringApplication.run(...)`
- 启动阶段化过程（概览）：见 [springboot/flows/启动流程.md](../../../../flows/启动流程.md)
- RunListener 回调：`SpringApplicationRunListener` / `SpringApplicationRunListeners`（见 [../interface/SpringApplicationRunListener.md](../interface/SpringApplicationRunListener.md)、[SpringApplicationRunListeners.md](SpringApplicationRunListeners.md)）
- 启动事件发布：`EventPublishingRunListener`（见 [EventPublishingRunListener.md](EventPublishingRunListener.md)）
- 扩展点发现：见 [springboot/modules/extension/mechanism/SpringFactoriesLoader.md](../../../extension/mechanism/SpringFactoriesLoader.md)
- 生命周期事件：见 [springboot/modules/core/events/mechanism/ApplicationLifecycleEvents.md](../../events/mechanism/ApplicationLifecycleEvents.md)

## 关系：上级/下级/等价/特例/推广
- 上级：应用生命周期（启动/运行/停止）。
- 下级：外部化配置（`Environment`）、`ApplicationContext`、自动配置、生命周期事件。
- 特例：Web 应用会创建并绑定嵌入式 WebServer（见 [springboot/modules/web/mechanism/EmbeddedWebServer.md](../../../web/mechanism/EmbeddedWebServer.md)）。

## 关键字段分层归纳

### 静态常量（Static Constants）
| 字段                                   | 说明                   |     |
| ------------------------------------ | -------------------- | --- |
| `DEFAULT_CONTEXT_CLASS`              | 默认上下文类（非 Web）        |     |
| `DEFAULT_SERVLET_WEB_CONTEXT_CLASS`  | 默认 Servlet Web 上下文类  |     |
| `DEFAULT_REACTIVE_WEB_CONTEXT_CLASS` | 默认 Reactive Web 上下文类 |     |
| `BANNER_LOCATION_PROPERTY_VALUE`     | Banner 默认文件名         |     |
| `BANNER_LOCATION_PROPERTY`           | Banner 位置属性键         |     |
| `SYSTEM_PROPERTY_JAVA_AWT_HEADLESS`  | AWT 无头模式系统属性         |     |
| `logger`                             | 日志实例                 |     |
|                                      |                      |     |

### 启动源（Primary Sources）
| 字段                     | 说明                      |
| ---------------------- | ----------------------- |
| `primarySources`       | 主启动源集合（`Set<Class<?>>`，通常为入口配置类） |
| `sources`              | 额外源类名集合（`Set<String>`）  |
| `mainApplicationClass` | 主应用类（推断得出的 main 方法所在类） |

> **Source 形式与作用**：
> 启动源（Sources）用于引导 `ApplicationContext` 的填充。支持三种形式：
> 1. **类（Class）**：Java 配置类（Annotated Class）。
> 2. **XML**：XML 配置文件路径。
> 3. **包（Package）**：触发组件扫描的包名。
>
> **设定链路（Origin & Setter）**：
> `primarySources` 严格源于**代码侧**显式指定（Code-based），无法通过外部配置替换：
> 1. **构造器/静态入口**：`SpringApplication.run(Main.class)` 内部实例化时将参数存入 `primarySources`。
> 2. **Builder 模式**：`new SpringApplicationBuilder().sources(...)` 指定。
> 3. **API 追加**：`addPrimarySources(...)` 可在已有集合上追加（注：常规扩展推荐使用 `sources` 而非修改 primary）。
>
> **边界辨析（Seed vs Result）**：
> Sources 是**启动输入种子**（Seed Inputs），仅作为 `ApplicationContext` 初始化的起点。
> - **包含**：显式传入的配置类/XML/包名。
> - **不包含**：由 `@ComponentScan` 扫描到的组件或 `@EnableAutoConfiguration` 导入的配置类（这些属于容器 refresh 过程中的内部扩张结果，不会回写到 sources 集合）。
>
> **典型追加场景（Additional Sources Use Cases）**：
> 除了默认的主配置类，可通过 `getSources()` 或 `spring.main.sources` 追加额外源（Sources），常见于：
> 1. **包扫描扩展**：添加包名字符串以扫描主包外的组件。
>    ```java
>    SpringApplication app = new SpringApplication(Main.class);
>    app.getSources().add("com.legacy.module"); // 触发 ClassPathBeanDefinitionScanner
>    ```
> 2. **Legacy XML 集成**：添加 XML 路径以复用旧配置。
>    ```java
>    app.getSources().add("classpath:/legacy-context.xml"); // 触发 XmlBeanDefinitionReader
>    ```
> 3. **动态/插件化配置**：通过全限定类名字符串加载可选配置。
>    ```java
>    app.getSources().add("com.example.ExtraConfig"); // 触发 AnnotatedBeanDefinitionReader
>    ```
> 4. **外部化注入**：利用 `spring.main.sources` 属性在运行时动态追加源。
>    ```bash
>    java -jar app.jar --spring.main.sources=com.example.ExtraConfig,classpath:/context.xml
>    ```
> 5. **层级上下文**：使用 `SpringApplicationBuilder` 为 parent/child 上下文分别指定不同的 sources。
>    ```java
>    new SpringApplicationBuilder()
>      .parent(ParentConfig.class)
>      .child(WebConfig.class).run(args);
>    ```

### 环境与上下文（Environment & Context）
| 字段 | 说明 |
| --- | --- |
| `environment` | 配置环境（`ConfigurableEnvironment`） |
| `applicationContextClass` | 应用上下文类 |
| `webApplicationType` | Web 应用类型（servlet/reactive/none） |
| `isCustomEnvironment` | 是否自定义环境 |
| `defaultProperties` | 默认属性映射 |
| `additionalProfiles` | 额外配置文件集合 |

### 资源与生成器（Resources & Generators）
| 字段 | 说明 |
| --- | --- |
| `resourceLoader` | 资源加载器 |
| `beanNameGenerator` | Bean 名称生成器 |

### 初始化器与监听器（Initializers & Listeners）
| 字段             | 说明                                                  |
| -------------- | --------------------------------------------------- |
| `initializers` | 上下文初始化器列表（`List<ApplicationContextInitializer<?>>`） |
| `listeners`    | 应用监听器列表（`List<ApplicationListener<?>>`）             |

> **扩展点设计哲学（Extension Mechanism）**：
> Spring Boot 采用“SPI + 事件驱动”的插件化架构，允许在不修改 `run()` 核心逻辑的前提下介入启动流程：
> 1. **生命周期挂钩**：将启动过程切分为标准阶段（事件），通过 `ApplicationListener` 监听（如 Environment 准备完毕、Context 创建完毕）。
> 2. **Context 定制**：通过 `ApplicationContextInitializer` 在 `refresh()` 之前直接对 `ConfigurableApplicationContext` 进行编程配置（如注册属性源、激活 Profile）。
> 3. **无侵入发现**：基于 `SpringFactoriesLoader` 机制（`spring.factories` / `imports`），使得第三方 Starter 仅需在 classpath 声明即可自动生效，无需用户显式注册（见 [springboot/modules/extension/mechanism/SpringFactoriesLoader.md](../../../extension/mechanism/SpringFactoriesLoader.md)）。
>
> 常见 SPI 清单（如 `EnvironmentPostProcessor`, `FailureAnalyzer` 等）见 **[springboot/modules/extension/mechanism/ExtensionPoints.md](../../../extension/mechanism/ExtensionPoints.md)**。

### 启动行为开关（Startup Behavior Flags）
| 字段 | 说明 |
| --- | --- |
| `bannerMode` | Banner 显示模式 |
| `logStartupInfo` | 是否记录启动信息 |
| `addCommandLineProperties` | 是否添加命令行属性 |
| `addConversionService` | 是否添加转换服务 |
| `headless` | 是否无头模式 |
| `registerShutdownHook` | 是否注册关闭钩子 |
| `allowBeanDefinitionOverriding` | 是否允许 Bean 定义覆盖 |
| `lazyInitialization` | 是否延迟初始化 |

### Banner
| 字段 | 说明 |
| --- | --- |
| `banner` | Banner 对象 |

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → SpringApplication → flows/启动流程。
