# SpringApplication（Spring Boot 启动编排入口）

> **类型**：类（Class）

## 一句话
`SpringApplication` 是 Spring Boot 的启动编排器：从 `primarySources` 与启动参数出发，构造 `Environment`、创建 `ApplicationContext`、触发 `refresh()` 并发布启动生命周期事件。

## 严格定义
在 Spring Boot 中，`org.springframework.boot.SpringApplication` 是应用启动入口 `SpringApplication.run(...)` 的承载对象；其目标是产生一个可用的 `ConfigurableApplicationContext`（成功时）或抛出启动失败异常（失败时）。

## 接口：数据 + 约束
- 输入：
  - `primarySources`：启动源（通常包含 `@SpringBootApplication` 标注的配置类）
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
- 启动阶段化过程（概览）：见 [springboot/flows/启动流程.md](../flows/启动流程.md)
- 扩展点发现：见 [springboot/modules/SpringFactories.md](SpringFactories.md)
- 生命周期事件：见 [springboot/modules/ApplicationLifecycleEvents.md](ApplicationLifecycleEvents.md)

## 关系：上级/下级/等价/特例/推广
- 上级：应用生命周期（启动/运行/停止）。
- 下级：外部化配置（`Environment`）、`ApplicationContext`、自动配置、生命周期事件。
- 特例：Web 应用会创建并绑定嵌入式 WebServer（见 [springboot/modules/EmbeddedWebServer.md](EmbeddedWebServer.md)）。

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
| 字段 | 说明 |
| --- | --- |
| `primarySources` | 主启动源集合（`Set<Class<?>>`） |
| `sources` | 额外源类名集合（`Set<String>`） |
| `mainApplicationClass` | 主应用类 |

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
| 字段 | 说明 |
| --- | --- |
| `initializers` | 上下文初始化器列表 |
| `listeners` | 应用监听器列表 |

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

