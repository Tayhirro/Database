# 扩展点体系（Startup Extension Points）

> **类型**：机制（Mechanism）/ 参考（Reference）

## 一句话
Spring Boot 提供了一套基于 `spring.factories`（或 `imports`）的 SPI 扩展体系，允许开发者在容器刷新前、配置加载时、自动配置导入期间或启动失败时切入启动流程。

## 严格定义
扩展点是指在 `SpringApplication.run()` 生命周期中预留的、通过 `SpringFactoriesLoader` 等机制发现并回调的接口集合。它们区别于常规 Bean，通常在 ApplicationContext 尚未准备好或需要干预 Context 创建过程时执行。

## 接口分类与清单

### A. 启动最早期：配置数据与启动底座
| 接口                             | 作用                                                                               | 注册 Key (spring.factories)                                            |
| :----------------------------- | :------------------------------------------------------------------------------- | :------------------------------------------------------------------- |
| `BootstrapRegistryInitializer` | **上下文创建前**：注册启动期专用对象/工厂（如 ConfigServer 连接器）。                                     | `org.springframework.boot.BootstrapRegistryInitializer`              |
| `ConfigDataLocationResolver`   | **配置解析**：将自定义 location 字符串解析为资源（Boot 2.4+）。                                      | `org.springframework.boot.context.config.ConfigDataLocationResolver` |
| `ConfigDataLoader`             | **配置加载**：将解析出的资源加载为配置数据（Boot 2.4+）。                                              | `org.springframework.boot.context.config.ConfigDataLoader`           |
| `EnvironmentPostProcessor`     | **环境后置处理**：在 Context refresh 前修改/追加 `Environment`（如加 PropertySource、激活 Profile）。 | `org.springframework.boot.env.EnvironmentPostProcessor`              |
| `PropertySourceLoader`         | **文件格式扩展**：定义如何加载特定后缀（如 .properties, .yml）的配置文件。                                 | `org.springframework.boot.env.PropertySourceLoader`                  |
|                                |                                                                                  |                                                                      |

### B. SpringApplication / ApplicationContext 结构级扩展
| 接口                              | 作用                                                               | 注册 Key (spring.factories)                                   |
| :------------------------------ | :--------------------------------------------------------------- | :---------------------------------------------------------- |
| `ApplicationContextInitializer` | **上下文初始化**：在 refresh 前对 Context 进行编程式定制（如注册 BeanDefinition）。     | `org.springframework.context.ApplicationContextInitializer` |
| `ApplicationListener`           | **事件监听**：监听 Boot 启动阶段事件（Starting, EnvironmentPrepared 等），强调早期注册。 | `org.springframework.context.ApplicationListener`           |
| `SpringApplicationRunListener`  | **生命周期钩子**：`run()` 方法内部步骤的回调接口（每次 run 均新建实例，用于发布事件）。             | `org.springframework.boot.SpringApplicationRunListener`     |
|                                 |                                                                  |                                                             |

### C. 自动配置导入阶段（Auto-Configuration Import）
> **注意**：此类接口用于**干预或观察**自动配置的导入决策，而非自动配置类本身。

| 接口                                | 作用                                               | 注册 Key (spring.factories)                                                |
| :-------------------------------- | :----------------------------------------------- | :----------------------------------------------------------------------- |
| `AutoConfigurationImportFilter`   | **快速过滤**：在读取字节码前过滤掉不符合条件的 AutoConfiguration 候选类。 | `org.springframework.boot.autoconfigure.AutoConfigurationImportFilter`   |
| `AutoConfigurationImportListener` | **导入观察**：接收“最终导入了哪些自动配置类”的事件通知。                  | `org.springframework.boot.autoconfigure.AutoConfigurationImportListener` |

### D. 启动失败诊断（Failure Analysis）
| 接口                | 作用                                                       | 注册 Key (spring.factories)                              |
| :---------------- | :------------------------------------------------------- | :----------------------------------------------------- |
| `FailureAnalyzer` | **异常分析**：将启动时的原始异常转换为可读性强的 `FailureAnalysis`（包含描述与行动建议）。 | `org.springframework.boot.diagnostics.FailureAnalyzer` |

## 执行模型（批量遍历 / 分派点）
扩展点的“执行”可以抽象为两类组织方式：
- 有序批量遍历：收集实现 → 排序 → 在某个启动阶段依次回调全部实现。
- 分派点选择：在解析/加载流程遇到输入对象时，从实现集合中选择匹配者执行。

对应的执行模型条目：见 [ExtensionExecutionModels.md](ExtensionExecutionModels.md)。

## 辨析：扩展点 vs 自动配置
- **扩展点（Extensions）**：实现特定接口，由 Boot 基础设施回调，通常在容器生命周期之外或边缘工作（Key 为接口全名）。
- **自动配置（Auto-Configuration）**：普通的 `@Configuration` 类，用于向容器贡献 Bean，由 Boot 导入机制加载（Key 为 `EnableAutoConfiguration` 或在 `imports` 文件中声明）。

## 注册示例（Registration Example）

一个 `spring.factories` 文件可同时注册多种类型的扩展点（这也是 `SpringFactoriesLoader` 的设计初衷）：

```properties
# 事件监听器
org.springframework.context.ApplicationListener=\
com.example.MyAppListener

# 上下文初始化器
org.springframework.context.ApplicationContextInitializer=\
com.example.MyCtxInitializer

# 环境后置处理
org.springframework.boot.env.EnvironmentPostProcessor=\
com.example.MyEnvPostProcessor

# 自动配置过滤
org.springframework.boot.autoconfigure.AutoConfigurationImportFilter=\
com.example.MyAutoConfigFilter
```

## 关系：上级/下级/等价/特例/推广
- **上级**：扩展点发现（见 [springboot/modules/extension/mechanism/SpringFactoriesLoader.md](SpringFactoriesLoader.md)）。
- **消费方**：`SpringApplication`（见 [springboot/modules/core/bootstrap/class/SpringApplication.md](../../core/bootstrap/class/SpringApplication.md)）。
- **执行时机**：见 [springboot/flows/启动流程.md](../../../flows/启动流程.md)。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → 扩展点体系 →（Early/Context/Import/Diagnosis）。
