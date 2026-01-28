# SpringApplicationRunListener（运行监听器机制）

> **类型**：扩展接口（SPI Interface）与 组合机制（Composite Mechanism）

## 一句话
`SpringApplicationRunListener` 是 Spring Boot 启动流程中最底层的**生命周期钩子接口**；而 `SpringApplicationRunListeners` 是其**组合类**，负责将启动步骤的信号批量分发给所有已注册的监听器。

## 严格定义

### 1. 接口：`SpringApplicationRunListener`
定义了 `SpringApplication.run()` 方法执行过程中各个阶段的回调契约。它允许在 Context 创建甚至 Environment 准备之前就介入启动流程。

### 2. 组合类：`SpringApplicationRunListeners`
这是一个内部使用的组合模式（Composite）实现。`SpringApplication` 并不直接调用单个监听器，而是持有一个 `SpringApplicationRunListeners` 实例，由后者遍历并调用所有注册的 `SpringApplicationRunListener`。

## 继承链（接口链 / 实现链）
- 接口链：`SpringApplicationRunListener`（定义 `SpringApplication.run()` 各阶段回调契约；无上级接口）。
- 内置实现：`EventPublishingRunListener`（implements `SpringApplicationRunListener`, `Ordered`）。
- 组合分发器：`SpringApplicationRunListeners`（持有 `List<SpringApplicationRunListener>` 并转发各阶段回调）。

## 核心 API 与时序（Callback Timeline）
这些方法按调用顺序排列，勾勒出了 Boot 启动的全貌：

| 方法（Boot 2.3.x） | 触发时机 | 关键参数 |
| :--- | :--- | :--- |
| **`starting()`** | `run()` 刚开始，Environment/Context 均未创建。 | 无 |
| **`environmentPrepared(environment)`** | `Environment` 已创建并加载配置，但 Context 未创建。 | `ConfigurableEnvironment` |
| **`contextPrepared(context)`** | Context 已创建并完成基础准备（refresh 之前）。 | `ConfigurableApplicationContext` |
| **`contextLoaded(context)`** | BeanDefinition 已加载（`load(...)` 已执行），但在 `refresh()` 之前。 | `ConfigurableApplicationContext` |
| **`started(context)`** | Context `refresh()` 完成，Runner 执行之前。 | `ConfigurableApplicationContext` |
| **`running(context)`** | Runner 执行完毕，`run()` 即将返回。 | `ConfigurableApplicationContext` |
| **`failed(context, ex)`** | 启动过程中发生异常（catch 块中触发；context 可能为 `null`）。 | `ConfigurableApplicationContext` / `Throwable` |

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
    public void starting() { }
    // ... 其他方法
}
```

## 关系：上级/下级/等价/特例/推广
- **实现**：`EventPublishingRunListener`（唯一内置实现，见 [../class/EventPublishingRunListener.md](../class/EventPublishingRunListener.md)）。
- **调用方**：`SpringApplication`（见 [../class/SpringApplication.md](../class/SpringApplication.md)）。
- **关联**：`SpringApplicationRunListeners`（作为分发器，见 [../class/SpringApplicationRunListeners.md](../class/SpringApplicationRunListeners.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → bootstrap → SpringApplicationRunListener → （Interface / Listeners / Lifecycle Methods）。
