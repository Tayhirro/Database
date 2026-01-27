# SpringFactoriesLoader（SPI 加载工具）

> **类型**：工具类（Utility）/ 机制（Mechanism）

## 一句话
`SpringFactoriesLoader` 是 Spring 框架内部通用的 SPI（Service Provider Interface）加载工具，负责从 `META-INF/spring.factories` 文件中加载接口的实现类名并实例化。

## 严格定义
`org.springframework.core.io.support.SpringFactoriesLoader` 是一个 final 工具类。它约定在 classpath 下的所有 JAR 包中查找 `META-INF/spring.factories` 资源，解析其中的 Key-Value 对（Key=接口全限定名, Value=实现类全限定名列表），并提供实例化能力。

## 核心 API（Static Methods）

### 1. 加载类名（Lightweight）
```java
public static List<String> loadFactoryNames(Class<?> factoryType, @Nullable ClassLoader classLoader)
```
- **作用**：仅获取实现类的**全限定类名**，不进行实例化。
- **场景**：`@EnableAutoConfiguration` 的候选类过滤（在实例化之前先检查是否存在对应的依赖类）。

### 2. 加载并实例化（Heavyweight）
```java
public static <T> List<T> loadFactories(Class<T> factoryType, @Nullable ClassLoader classLoader)
```
- **作用**：获取类名后，立即通过反射（无参构造器）实例化对象，并进行 `AnnotationAwareOrderComparator` 排序。
- **场景**：加载 `ApplicationListener`、`ApplicationContextInitializer` 等启动期组件。

## 配置文件格式
`META-INF/spring.factories` 采用标准 Properties 格式，支持多值（逗号分隔）：

```properties
# Interface / Annotation
org.springframework.context.ApplicationListener=\
com.example.ListenerA,\
com.example.ListenerB
```

## 关系：上级/下级/等价/特例/推广
- **上级**：Java SPI（`ServiceLoader`）的 Spring 定制版。
- **对比**：Java `ServiceLoader` 使用 `META-INF/services/`，Spring 使用 `META-INF/spring.factories`。
- **应用**：被 `SpringApplication` 用于加载初始化器和监听器（见 [springboot/modules/core/bootstrap/class/SpringApplication.md](../../core/bootstrap/class/SpringApplication.md)）。
- **扩展**：扩展点体系清单见 [springboot/modules/extension/mechanism/ExtensionPoints.md](ExtensionPoints.md)。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → extension → SpringFactoriesLoader → （loadFactories / spring.factories）。
