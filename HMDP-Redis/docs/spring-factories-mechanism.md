# Spring / Spring Boot 中 META-INF/spring.factories 的作用、适用场景与排查方法

> 本文档以"机制解释"为主，帮助你理解 spring.factories 的本质，而不是停留在"知道怎么用"的层面。

---

## 目录

1. [如何自查 Spring Boot 版本](#1-如何自查-spring-boot-版本)
2. [Spring SPI 与工厂加载机制](#2-spring-spi-与工厂加载机制)
3. [Spring Boot 自动配置的总体链路](#3-spring-boot-自动配置的总体链路)
4. [spring.factories 在其中的角色](#4-springfactories-在其中的角色)
5. [版本演进：spring.factories vs AutoConfiguration.imports](#5-版本演进springfactories-vs-autoconfigurationimports)
6. [业务项目 vs Starter 项目：为什么你可能不需要它](#6-业务项目-vs-starter-项目为什么你可能不需要它)
7. [常见问题与排查 Checklist](#7-常见问题与排查-checklist)
8. [最小示例：自定义 Starter 的 spring.factories](#8-最小示例自定义-starter-的-springfactories)
9. [快速定位流程图](#9-快速定位流程图)

---

## 2. Spring SPI 与工厂加载机制

### 2.1 什么是 SPI？

**SPI（Service Provider Interface）** 是一种服务发现机制。核心思想是：

> 框架定义接口，第三方提供实现，框架通过**约定的配置文件**自动发现并加载实现。

Java 原生 SPI 使用 `META-INF/services/` 目录，Spring 则设计了自己的变体：`META-INF/spring.factories`。

### 2.2 为什么放在 META-INF 下？

这是 **JAR 规范的约定**：

- `META-INF/` 是 JAR 文件的元数据目录
- `META-INF/MANIFEST.MF` 存放 JAR 的基本信息
- `META-INF/services/` 是 Java SPI 的标准位置
- Spring 沿用这个约定，把扩展点配置也放在这里

**好处**：
1. 符合业界惯例，开发者容易理解
2. 构建工具（Maven/Gradle）默认会把 `src/main/resources/META-INF` 打进 JAR
3. 类加载器可以通过 `getResources("META-INF/spring.factories")` 扫描所有 JAR

### 2.3 SpringFactoriesLoader 的工作原理

Spring 提供了 `SpringFactoriesLoader` 类来加载 `spring.factories`：

```
加载过程：
1. 调用 ClassLoader.getResources("META-INF/spring.factories")
2. 遍历 classpath 上所有 JAR（包括你的项目和所有依赖）
3. 读取每个 JAR 中的 spring.factories 文件
4. 解析为 key-value 格式（key 是接口/注解全限定名，value 是实现类列表）
5. 合并所有结果，返回给调用方
```

它会扫描**所有 JAR**，不只是你的项目。这就是为什么你引入一个 starter 依赖，它的自动配置就能生效——因为 starter 的 JAR 里有 `spring.factories`，会被自动发现。
---

## 3. Spring Boot 自动配置的总体链路

### 3.1 从 main 方法到自动配置生效

```
main()
  │
  ▼
SpringApplication.run(MyApp.class, args)
  │
  ├── 1. 创建 SpringApplication 实例
  │     └── 加载 ApplicationContextInitializer（从 spring.factories）
  │     └── 加载 ApplicationListener（从 spring.factories）
  │
  ├── 2. 执行 run() 方法
  │     ├── 准备 Environment
  │     ├── 创建 ApplicationContext
  │     └── refreshContext() ──────────────────────────────┐
  │                                                        │
  │     ┌──────────────────────────────────────────────────┘
  │     ▼
  │     3. 解析 @SpringBootApplication
  │        │
  │        ├── @SpringBootConfiguration → 标记为配置类
  │        ├── @ComponentScan → 扫描同包及子包的 Bean
  │        └── @EnableAutoConfiguration → 【触发自动配置】
  │              │
  │              ▼
  │        4. @Import(AutoConfigurationImportSelector.class)
  │              │
  │              ▼
  │        5. AutoConfigurationImportSelector.selectImports()
  │              │
  │              ├── 调用 SpringFactoriesLoader.loadFactoryNames()
  │              │     └── 读取 spring.factories 中 key 为
  │              │         EnableAutoConfiguration 的所有类
  │              │
  │              ├── 过滤：排除 @Exclude 指定的类
  │              ├── 过滤：根据 @Conditional 条件判断
  │              │
  │              ▼
  │        6. 返回需要导入的自动配置类列表
  │
  ▼
Bean 容器初始化完成，自动配置生效
```

### 3.2 为什么需要这么复杂？

**核心问题**：Spring Boot 的 starter 是独立的 JAR，它们的包名（如 `org.springframework.boot.autoconfigure`）和你的业务代码包名（如 `com.hmdp`）完全不同。

如果只靠 `@ComponentScan`，它只会扫描 `com.hmdp` 及其子包，根本发现不了 `org.springframework.boot.autoconfigure` 下的配置类。

**解决方案**：通过 `spring.factories` 显式声明"哪些类是自动配置类"，由 `AutoConfigurationImportSelector` 主动加载，而不是靠包扫描。

---

## 4. spring.factories 在其中的角色

### 4.1 文件格式

```properties
# spring.factories 是 properties 格式
# key = 接口或注解的全限定名
# value = 实现类的全限定名（多个用逗号分隔）

org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.example.MyAutoConfiguration,\
  com.example.AnotherAutoConfiguration

org.springframework.context.ApplicationListener=\
  com.example.MyAppListener
```

### 4.2 典型的 key 类型

| Key（接口/注解全限定名） | 用途 | 加载时机 |
|------------------------|------|---------|
| `EnableAutoConfiguration` | 自动配置类 | refresh() 阶段，解析 @EnableAutoConfiguration 时 |
| `ApplicationContextInitializer` | 上下文初始化器 | 创建 SpringApplication 实例时 |
| `ApplicationListener` | 应用事件监听器 | 创建 SpringApplication 实例时 |
| `EnvironmentPostProcessor` | 环境后处理器 | 准备 Environment 时 |
| `FailureAnalyzer` | 启动失败分析器 | 启动失败时 |

### 4.3 加载时机的重要性

**为什么要区分加载时机？**

因为有些扩展点需要在容器启动的**非常早期**介入：

- `EnvironmentPostProcessor`：在配置文件加载后、Bean 创建前，修改环境变量
- `ApplicationContextInitializer`：在 refresh() 前，对 ApplicationContext 做初始化
- `ApplicationListener`：监听整个启动生命周期的事件

如果这些类靠 `@Component` 注解注册，那时候 Bean 容器还没初始化，根本来不及！所以必须通过 `spring.factories` 提前声明。

---

## 5. 版本演进：spring.factories vs AutoConfiguration.imports

### 5.1 为什么要演进？

**spring.factories 的问题：**

1. **职责混杂**：自动配置、监听器、初始化器全放一个文件，不清晰
2. **解析开销**：每次都要解析整个文件，即使只需要某一类扩展点
3. **命名不直观**：key 是注解全限定名，对新手不友好

### 5.2 新机制的设计

从 Spring Boot 2.7 开始，自动配置类改用独立文件：

```
旧机制位置：META-INF/spring.factories
新机制位置：META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

**新文件格式更简单**（每行一个类名，不需要 key）：

```
# AutoConfiguration.imports
com.example.MyAutoConfiguration
com.example.AnotherAutoConfiguration
```

### 5.3 兼容性说明

| 版本 | EnableAutoConfiguration in spring.factories | AutoConfiguration.imports |
|-----|---------------------------------------------|--------------------------|
| 2.6 及之前 | ✅ 唯一方式 | ❌ 不支持 |
| 2.7 | ✅ 仍支持 | ✅ 推荐使用 |
| 3.0+ | ❌ 已移除该 key | ✅ 唯一方式 |

**注意**：spring.factories 文件本身没有被废弃！只是其中 `EnableAutoConfiguration` 这个 key 被移除了。其他 key（如 `ApplicationListener`）仍然有效。

### 5.4 如果你找不到 spring.factories 中的自动配置

**可能原因**：你用的是 Spring Boot 3.x，官方 starter 已经迁移到新机制。

**验证方法**：
```powershell
# 搜索新机制文件
jar tf spring-boot-autoconfigure-3.x.x.jar | Select-String "AutoConfiguration.imports"
```

---

## 6. 业务项目 vs Starter 项目：为什么你可能不需要它

### 6.1 业务项目的 Bean 发现机制

你的 `HmDianPingApplication` 启动类上有 `@SpringBootApplication`，它包含 `@ComponentScan`：

```
@SpringBootApplication 所在包：com.hmdp
                              │
                              ▼
自动扫描范围：com.hmdp 及所有子包
                              │
    ┌─────────────────────────┼─────────────────────────┐
    ▼                         ▼                         ▼
com.hmdp.controller      com.hmdp.service          com.hmdp.config
    │                         │                         │
    ▼                         ▼                         ▼
@Controller 自动注册    @Service 自动注册      @Configuration 自动注册
```

**结论**：业务代码都在 `com.hmdp` 包下，会被自动扫描，**不需要 spring.factories**。

### 6.2 Starter 项目为什么必须用 spring.factories

假设你写了一个 `my-redis-starter`，包结构是：

```
my-redis-starter.jar
└── com.mycompany.redis
    ├── MyRedisAutoConfiguration.java
    └── MyRedisProperties.java
```

当别人引入你的 starter 时：

```
用户的项目包：com.hmdp
Starter 的包：com.mycompany.redis  ← 完全不同！
```

`@ComponentScan` 只扫描 `com.hmdp`，根本不会碰 `com.mycompany.redis`。

**解决方案**：在 starter 的 `META-INF/spring.factories` 中声明自动配置类，让 Spring Boot 主动加载。

### 6.3 何时业务项目也需要 spring.factories

| 场景 | 需要 spring.factories | 原因 |
|-----|----------------------|------|
| 普通 Controller/Service | ❌ 不需要 | 包扫描自动发现 |
| 自定义 Starter | ✅ 需要 | 包名不在扫描范围 |
| 全局 EnvironmentPostProcessor | ✅ 需要 | 需要在容器启动前加载 |
| 全局 ApplicationListener（监听早期事件） | ✅ 需要 | @Component 注册太晚 |
| 跨模块的配置类（多模块项目） | 视情况 | 如果不在主模块扫描范围内 |

---

## 7. 常见问题与排查 Checklist

### 7.1 文件没打进 JAR

**症状**：IDE 里能看到 spring.factories，但运行时不生效。

**排查**：
```powershell
# 检查 JAR 内容
jar tf target/xxx.jar | Select-String "spring.factories"
```

**常见原因**：
- 文件放在了 `src/main/java/META-INF`（错误），应该是 `src/main/resources/META-INF`
- Maven 资源过滤配置排除了该文件

**解决**：确保 pom.xml 中：
```xml
<resources>
    <resource>
        <directory>src/main/resources</directory>
        <includes>
            <include>**/*</include>  <!-- 包含所有文件 -->
        </includes>
    </resource>
</resources>
```

### 7.2 路径写错

**正确路径**：
```
src/main/resources/META-INF/spring.factories
```

**常见错误**：
```
src/main/resources/spring.factories          ← 缺少 META-INF 目录
src/main/META-INF/spring.factories           ← 不在 resources 下
src/main/resources/meta-inf/spring.factories ← 大小写错误
```

### 7.3 Key 写错 / 类名写错

**症状**：文件存在，但自动配置不生效。

**排查**：打开 spring.factories，检查：
1. Key 是否正确：`org.springframework.boot.autoconfigure.EnableAutoConfiguration`（一个字母都不能错）
2. Value 是否是**全限定类名**：`com.example.MyAutoConfiguration`（不是 `MyAutoConfiguration`）
3. 类名是否真的存在（没有拼写错误）

### 7.4 条件注解没触发

**症状**：自动配置类被加载了，但里面的 Bean 没有注册。

**排查**：检查自动配置类上的 `@Conditional` 注解：

```java
@Configuration
@ConditionalOnClass(RedisTemplate.class)  // 需要 classpath 有这个类
@ConditionalOnProperty(prefix = "my.redis", name = "enabled", havingValue = "true")  // 需要配置为 true
public class MyRedisAutoConfiguration {
    // ...
}
```

**调试方法**：启动时加参数，打印自动配置报告：
```
--debug
```
或在 `application.yml` 中：
```yaml
debug: true
```

然后查看 `CONDITIONS EVALUATION REPORT`，会显示每个配置类为什么生效/不生效。

### 7.5 多模块项目放错模块

**症状**：spring.factories 放在 A 模块，但启动类在 B 模块，A 模块没有被 B 模块依赖。

**原因**：如果 A 不在 B 的 classpath 上，`SpringFactoriesLoader` 就扫描不到。

**解决**：
1. 确保 B 模块的 pom.xml 依赖了 A 模块
2. 或者把 spring.factories 移到启动类所在的模块

### 7.6 版本机制不一致

**症状**：升级到 Spring Boot 3.x 后，原有的自动配置不生效。

**原因**：3.x 移除了 spring.factories 中 `EnableAutoConfiguration` key 的支持。

**解决**：迁移到新机制：

```
旧文件：META-INF/spring.factories
新文件：META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

---

## 8. 最小示例：自定义 Starter 的 spring.factories

### 8.1 场景说明

假设你要写一个 `my-greeting-starter`，功能是自动注入一个 `GreetingService` Bean。

### 8.2 项目结构

```
my-greeting-starter/
├── pom.xml
└── src/main/
    ├── java/com/mycompany/greeting/
    │   ├── GreetingService.java
    │   ├── GreetingProperties.java
    │   └── GreetingAutoConfiguration.java
    └── resources/META-INF/
        └── spring.factories   ← 关键文件
```

### 8.3 spring.factories 内容（旧机制，2.6 及之前）

```properties
# 告诉 Spring Boot：GreetingAutoConfiguration 是一个自动配置类
# 当别人引入这个 starter 时，会自动加载这个配置
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.mycompany.greeting.GreetingAutoConfiguration
```

**每部分解释**：
- **Key**：`org.springframework.boot.autoconfigure.EnableAutoConfiguration` 是固定的，表示"这是自动配置类列表"
- **Value**：你的自动配置类的**全限定名**，必须完整写出包路径
- **反斜杠 `\`**：用于换行，如果有多个类可以继续追加

### 8.4 AutoConfiguration.imports 内容（新机制，2.7+ 推荐）

```
com.mycompany.greeting.GreetingAutoConfiguration
```

**对比**：
- 不需要写 key，每行就是一个类名
- 更简洁，更易读
- 文件路径变了：`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

### 8.5 如果不配置 spring.factories 会怎样？

**结果**：用户引入你的 starter 后，`GreetingAutoConfiguration` 不会被加载，`GreetingService` Bean 不会被注册。

**用户会看到**：
```
NoSuchBeanDefinitionException: No qualifying bean of type 'GreetingService' available
```

**根本原因**：`com.mycompany.greeting` 包不在用户项目的 `@ComponentScan` 范围内，而你又没有通过 spring.factories 声明，Spring Boot 就无从得知这个配置类的存在。

---

## 9. 快速定位流程图

当你需要排查 spring.factories 相关问题时，按以下流程操作：

```
┌─────────────────────────────────────────────────────────────┐
│                     定位 spring.factories                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
              ┌───────────────────────────────┐
              │ 1. 确认 Spring Boot 版本       │
              │    (pom.xml / 启动日志)        │
              └───────────────────────────────┘
                              │
              ┌───────────────┴───────────────┐
              ▼                               ▼
    ┌─────────────────┐             ┌─────────────────┐
    │  版本 < 2.7     │             │  版本 >= 2.7    │
    │  只找 spring.   │             │  两种文件都找   │
    │  factories      │             │                 │
    └─────────────────┘             └─────────────────┘
              │                               │
              └───────────────┬───────────────┘
                              ▼
              ┌───────────────────────────────┐
              │ 2. 确认是找"自己的"还是"依赖的" │
              └───────────────────────────────┘
                              │
              ┌───────────────┴───────────────┐
              ▼                               ▼
    ┌─────────────────┐             ┌─────────────────┐
    │  找自己写的      │             │  找依赖里的     │
    │  搜索项目源码    │             │  搜索 .m2 仓库  │
    │  src/main/      │             │  或 IDE External│
    │  resources/     │             │  Libraries      │
    └─────────────────┘             └─────────────────┘
                              │
                              ▼
              ┌───────────────────────────────┐
              │ 3. IDE 搜索 (Ctrl+Shift+N)    │
              │    输入: spring.factories     │
              │    或: AutoConfiguration.     │
              │        imports                │
              └───────────────────────────────┘
                              │
                              ▼
              ┌───────────────────────────────┐
              │ 4. 命令行验证                  │
              │    Get-ChildItem -Recurse     │
              │    -Filter "spring.factories" │
              └───────────────────────────────┘
                              │
                              ▼
              ┌───────────────────────────────┐
              │ 5. 构建产物验证                │
              │    jar tf xxx.jar |           │
              │    Select-String "META-INF"   │
              └───────────────────────────────┘
                              │
                              ▼
              ┌───────────────────────────────┐
              │ 6. 还是找不到？                │
              │    → 业务项目通常不需要它！    │
              │    → 检查是否真的需要          │
              └───────────────────────────────┘
```

---

## 附录：常用命令速查

```powershell
# 搜索项目中的 spring.factories
Get-ChildItem -Path "项目路径" -Recurse -Filter "spring.factories"

# 搜索 Maven 仓库中的 spring.factories
Get-ChildItem -Path "$env:USERPROFILE\.m2\repository\org\springframework\boot" -Recurse -Filter "spring.factories"

# 查看 JAR 中是否包含 spring.factories
jar tf target\xxx.jar | Select-String "spring.factories"

# 查看 Spring Boot 3.x 的新机制文件
jar tf target\xxx.jar | Select-String "AutoConfiguration.imports"

# 启动时打印自动配置报告
java -jar xxx.jar --debug
```

---

*最后更新：2026-01*
