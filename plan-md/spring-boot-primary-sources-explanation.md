# Spring Boot primarySources 配置详解

## 代码片段分析

```java
this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
```

## 核心概念解释

### 1. 什么是 primarySources？

`primarySources` 是 SpringApplication 类中的一个重要字段，它存储了 Spring Boot 应用的主配置源。

### 2. 传入 HmDianPingApplication.class 的含义

当您看到这样的代码调用时：

```java
SpringApplication.run(HmDianPingApplication.class, args);
```

这里的 `HmDianPingApplication.class` 确实作为主配置源传入，其作用包括：

## 主配置源的作用

### 1. **组件扫描起始点**
- Spring Boot 会从 `@SpringBootApplication` 注解标注的类开始扫描
- 该注解包含 `@ComponentScan`，会自动扫描同包及子包下的组件
- `HmDianPingApplication.class` 告诉 Spring 在哪里开始扫描

### 2. **配置类标识**
- 主配置类上的 `@SpringBootApplication` 注解标识了应用配置
- Spring Boot 会从这个类中读取各种配置信息
- 包括自动配置、Bean 定义等

### 3. **应用上下文创建**
- 作为创建 ApplicationContext 的基础
- Spring 使用这个类来建立应用的基础包结构
- 决定哪些配置文件需要被加载

## 具体作用机制

### 1. **包扫描范围**
```java
@SpringBootApplication
public class HmDianPingApplication {
    // 这会扫描 com.hmdp 包及其子包
}
```
- 扫描范围：`com.hmdp` 及其所有子包
- 包含的组件：`@Component`, `@Service`, `@Repository`, `@Controller` 等

### 2. **自动配置触发**
- `@SpringBootApplication` 包含 `@EnableAutoConfiguration`
- 自动配置会根据 classpath 中的依赖来加载相关配置
- 主配置类作为配置的起点

### 3. **配置属性绑定**
- 主配置类上的 `@ConfigurationProperties` 注解
- 环境变量和配置文件与 Java 对象的绑定
- 外部配置的加载和解析

## LinkedHashSet 的优势

```java
new LinkedHashSet<>(Arrays.asList(primarySources))
```

### 1. **去重性**
- Set 确保没有重复的配置源
- 避免重复加载相同的配置类

### 2. **有序性**
- LinkedHashSet 保持插入顺序
- 配置源按照重要性排序

### 3. **性能优化**
- HashSet 提供了 O(1) 的查找性能
- LinkedHashSet 在保持顺序的同时提供快速查找

## 实际应用示例

### 1. **单配置类应用**
```java
@SpringBootApplication
public class HmDianPingApplication {
    public static void main(String[] args) {
        SpringApplication.run(HmDianPingApplication.class, args);
    }
}
```

### 2. **多配置源应用**
```java
public static void main(String[] args) {
    // 可以传入多个配置类
    SpringApplication.run(
        new Class<?>[]{
            HmDianPingApplication.class,
            AdditionalConfig.class
        }, 
        args
    );
}
```

### 3. **模块化配置**
```java
@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
@EnableAsync
public class HmDianPingApplication {
    // 组合多个 Enable 注解启用不同功能
}
```

## Spring Boot 完整启动流程

### 1. 入口方法调用链

当你调用 `SpringApplication.run()` 时，实际执行流程：

```java
// 你的代码
SpringApplication.run(HmDianPingApplication.class, args);

// 内部调用链
public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
    return run(new Class<?>[] { primarySource }, args);
}

public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
    return (new SpringApplication(primarySources)).run(args);  // 关键！
}
```

1. `new SpringApplication(primarySources)` - 创建 SpringApplication 实例
2. `.run(args)` - 启动应用并返回 `ConfigurableApplicationContext`

### 2. SpringApplication 构造过程

```java
public SpringApplication(Class<?>... primarySources) {
    this(null, primarySources);
}

public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
    // 1. 保存主配置源
    this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
    
    // 2. 推断应用类型（SERVLET、REACTIVE、NONE）
    this.webApplicationType = WebApplicationType.deduceFromClasspath();
    
    // 3. 加载初始化器（从 META-INF/spring.factories）
    setInitializers(getSpringFactoriesInstances(ApplicationContextInitializer.class));
    
    // 4. 加载监听器
    setListeners(getSpringFactoriesInstances(ApplicationListener.class));
    
    // 5. 推断主方法所在类
    this.mainApplicationClass = deduceMainApplicationClass();
}
```

### 3. run() 方法 - 核心启动流程

```java
public ConfigurableApplicationContext run(String... args) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    
    ConfigurableApplicationContext context = null;
    
    // 1. 配置 Headless 属性
    configureHeadlessProperty();
    
    // 2. 获取运行监听器
    SpringApplicationRunListeners listeners = getRunListeners(args);
    listeners.starting();  // 发布 ApplicationStartingEvent
    
    try {
        // 3. 封装命令行参数
        ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
        
        // 4. 准备环境（加载 application.yml 等配置）
        ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
        
        // 5. 打印 Banner
        Banner printedBanner = printBanner(environment);
        
        // 6. 创建 ApplicationContext（关键！）
        context = createApplicationContext();
        
        // 7. 准备上下文
        prepareContext(context, environment, listeners, applicationArguments, printedBanner);
        
        // 8. 刷新上下文（核心！调用 refresh()）
        refreshContext(context);
        
        // 9. 刷新后处理
        afterRefresh(context, applicationArguments);
        
        stopWatch.stop();
        
        // 10. 发布 ApplicationStartedEvent
        listeners.started(context);
        
        // 11. 调用 Runner（CommandLineRunner、ApplicationRunner）
        callRunners(context, applicationArguments);
    }
    catch (Throwable ex) {
        handleRunFailure(context, listeners, ex);
        throw new IllegalStateException(ex);
    }
    
    // 12. 发布 ApplicationReadyEvent
    listeners.running(context);
    
    return context;  // 返回创建好的上下文
}
```

### 4. createApplicationContext() - 创建上下文

```java
protected ConfigurableApplicationContext createApplicationContext() {
    return this.applicationContextFactory.create(this.webApplicationType);
}

// 根据应用类型创建不同的上下文实现
public ConfigurableApplicationContext create(WebApplicationType webApplicationType) {
    switch (webApplicationType) {
        case SERVLET:
            return new AnnotationConfigServletWebServerApplicationContext();  // Web应用
        case REACTIVE:
            return new AnnotationConfigReactiveWebServerApplicationContext(); // 响应式应用
        case NONE:
            return new AnnotationConfigApplicationContext();  // 普通应用
    }
}
```

**注意**：
- 返回类型是**接口** `ConfigurableApplicationContext`
- 实际创建的是**具体实现类**（如 `AnnotationConfigServletWebServerApplicationContext`）
- 这就是多态的体现！

### 5. refreshContext() - 刷新上下文

```java
private void refreshContext(ConfigurableApplicationContext context) {
    refresh(context);
}

protected void refresh(ConfigurableApplicationContext applicationContext) {
    applicationContext.refresh();  // 调用 Spring 的核心 refresh() 方法
}
```

`refresh()` 是 Spring 容器初始化的核心，执行 12 个步骤：

```java
public void refresh() {
    // 1. prepareRefresh()          - 准备刷新
    // 2. obtainFreshBeanFactory()  - 获取 BeanFactory
    // 3. prepareBeanFactory()      - 准备 BeanFactory
    // 4. postProcessBeanFactory()  - BeanFactory 后处理
    // 5. invokeBeanFactoryPostProcessors() - 执行 BeanFactoryPostProcessor
    // 6. registerBeanPostProcessors()      - 注册 BeanPostProcessor
    // 7. initMessageSource()       - 初始化消息源
    // 8. initApplicationEventMulticaster() - 初始化事件多播器
    // 9. onRefresh()               - 刷新特殊 Bean（如启动内嵌 Tomcat）
    // 10. registerListeners()      - 注册监听器
    // 11. finishBeanFactoryInitialization() - 实例化所有单例 Bean
    // 12. finishRefresh()          - 完成刷新，发布事件
}
```

### 6. 完整流程图

```
main()
  │
  ▼
SpringApplication.run(HmDianPingApplication.class, args)
  │
  ├─► new SpringApplication(primarySources)
  │     ├── 保存 primarySources
  │     ├── 推断应用类型 (SERVLET/REACTIVE/NONE)
  │     ├── 加载 ApplicationContextInitializer
  │     ├── 加载 ApplicationListener
  │     └── 推断主类
  │
  └─► application.run(args)
        │
        ├── 1. 获取 RunListeners
        ├── 2. 发布 ApplicationStartingEvent
        ├── 3. 准备 Environment
        ├── 4. 打印 Banner
        ├── 5. createApplicationContext() ──────► AnnotationConfigServletWebServerApplicationContext
        ├── 6. prepareContext()
        │       ├── 设置 Environment
        │       ├── 应用 Initializers
        │       └── 注册 primarySources 为 BeanDefinition
        ├── 7. refreshContext() ──────────────► refresh() 12步初始化
        │       └── onRefresh() 启动内嵌 Tomcat
        ├── 8. 发布 ApplicationStartedEvent
        ├── 9. 调用 CommandLineRunner / ApplicationRunner
        ├── 10. 发布 ApplicationReadyEvent
        │
        └──► return ConfigurableApplicationContext (应用启动完成！)
```

### 7. primarySources 在流程中的使用

`primarySources`（如 `HmDianPingApplication.class`）在启动过程中被使用的关键位置：

```java
// prepareContext() 中
private void prepareContext(...) {
    // ...
    // 将 primarySources 加载为 BeanDefinition
    Set<Object> sources = getAllSources();  // 包含 primarySources
    load(context, sources.toArray(new Object[0]));
    // ...
}
```

这意味着：
1. `HmDianPingApplication.class` 会被注册为一个 Bean
2. 它上面的 `@SpringBootApplication` 注解会被解析
3. 触发组件扫描和自动配置

## 总结

**`HmDianPingApplication.class` 作为主配置源的意义：**

1. **标识应用入口**：告诉 Spring 这是应用的根配置类
2. **定义扫描边界**：确定组件扫描的起始包
3. **触发自动配置**：启动 Spring Boot 的自动配置机制
4. **加载应用配置**：包括配置文件、环境变量等
5. **创建应用上下文**：建立完整的 Spring 应用环境

**配置源的工作流程：**
```
启动类 → 组件扫描 → 自动配置 → 上下文创建 → 应用启动
```

这个机制确保了 Spring Boot 应用能够从一个统一的入口开始，完成所有必要的配置和初始化工作。