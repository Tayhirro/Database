# SpringApplicationRunListener（运行监听器机制）

> **类型**：扩展接口（SPI Interface）与 组合机制（Composite Mechanism）

## 一句话
`SpringApplicationRunListener` 是 Spring Boot 启动流程中最底层的**生命周期钩子接口**；而 `SpringApplicationRunListeners` 是其**组合类**，负责将启动步骤的信号批量分发给所有已注册的监听器。

## 严格定义

### 1. 接口：`SpringApplicationRunListener`
定义了 `SpringApplication.run()` 方法执行过程中各个阶段的回调契约。它允许在 Context 创建甚至 Environment 准备之前就介入启动流程。

### 2. 组合类：`SpringApplicationRunListeners`
这是一个内部使用的组合模式（Composite）实现。`SpringApplication` 并不直接调用单个监听器，而是持有一个 `SpringApplicationRunListeners` 实例，由后者遍历并调用所有注册的 `SpringApplicationRunListener`。

## 核心 API 与时序（Callback Timeline）
这些方法按调用顺序排列，勾勒出了 Boot 启动的全貌：

| 方法 | 触发时机 | 关键参数 |
| :--- | :--- | :--- |
| **`starting()`** | `run()` 刚开始，除了 listeners/initializers 初始化外，其他均未就绪。 | `bootstrapContext` |
| **`environmentPrepared(...)`** | `Environment` 已创建并加载配置（Profile 已定），但 Context 未创建。 | `ConfigurableEnvironment` |
| **`contextPrepared(...)`** | Context 已创建，Initializers 已执行，但 BeanDefinition 未加载。 | `ConfigurableApplicationContext` |
| **`contextLoaded(...)`** | BeanDefinition 已加载（主配置类已解析），但在 `refresh()` 之前。 | `ConfigurableApplicationContext` |
| **`started(...)`** | Context `refresh()` 完成，且 `ApplicationRunner`/`CommandLineRunner` **执行之前**。 | `ConfigurableApplicationContext` |
| **`ready(...)`** | 所有 Runner 执行完毕，`run()` 方法即将返回（启动成功）。 | `ConfigurableApplicationContext` |
| **`failed(...)`** | 启动过程中发生异常（Catch 块中触发）。 | `Throwable` |

## 常用构造/操作
- **加载方式**：通过 `SpringFactoriesLoader` 从 `META-INF/spring.factories` 加载。
- **构造器约束**：实现类必须提供一个接收 `(SpringApplication, String[])` 的公共构造器。

```java
// 示例：自定义 RunListener
public class MyRunListener implements SpringApplicationRunListener {
    public MyRunListener(SpringApplication app, String[] args) {
        // 必须有此构造器
    }
    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        System.out.println("Boot is starting!");
    }
    // ... 其他方法
}
```

## 关系：上级/下级/等价/特例/推广
- **实现**：`EventPublishingRunListener`（唯一内置实现，见 [EventPublishingRunListener.md](EventPublishingRunListener.md)）。
- **调用方**：`SpringApplication`（见 [SpringApplication.md](SpringApplication.md)）。
- **关联**：`SpringApplicationRunListeners`（作为 Dispatcher）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → bootstrap → SpringApplicationRunListener → （Interface / Listeners / Lifecycle Methods）。
