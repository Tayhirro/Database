# 真实Spring Boot 初始化过程源码分析

## 概述

本文档基于真实的Spring Boot源码，深入分析了Spring Boot应用从启动入口（main方法）到完成整个应用上下文刷新的完整初始化流程。通过对源码的详细解读，我们将了解Spring Boot是如何从程序启动到Bean容器准备就绪的全过程。

## 相关概念速查

- Spring Boot 总览（早期笔记）：[study/springboot_intro.md](springboot_intro.md)
- 运行全链路（从启动到结束，主线入口）：[springboot/flows/运行全链路.md](../springboot/flows/运行全链路.md)

## 典型的Spring Boot启动类

在HMDP-Redis项目中，我们看到了一个典型的Spring Boot应用启动类：

```java
@EnableAspectJAutoProxy(exposeProxy = true)//暴露代理对象
@MapperScan("com.hmdp.mapper")
@SpringBootApplication
public class HmDianPingApplication {

    public static void main(String[] args) {
        SpringApplication.run(HmDianPingApplication.class, args);
    }
}
```

## 完整的Spring Boot启动流程

### 1. main方法入口

每个Spring Boot应用都以标准的Java main方法作为入口：

```java
public static void main(String[] args) {
    SpringApplication.run(HmDianPingApplication.class, args);
}
```

### 2. SpringApplication.run()的执行流程

`SpringApplication.run()`方法执行以下关键步骤：

#### 2.1 创建SpringApplication实例

```java
public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
    return run(new Class<?>[] { primarySource }, args);
}

public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
    // 1. 创建SpringApplication实例
    SpringApplication application = new SpringApplication(primarySources);
    // 2. 运行应用
    return application.run(args);
}
```

#### 2.2 SpringApplication构造过程

```java
public SpringApplication(Class<?>... primarySources) {
    this(null, primarySources);
}

public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
    // 记录主配置源
    this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));

    // 推断应用类型（REACTIVE、 SERVLET、 NONE）
    this.webApplicationType = WebApplicationType.deduceFromClasspath();
    // 设置启动器
    setInitializers(getSpringFactoriesInstances(ApplicationContextInitializer.class));
    
    // 设置监听器
    setListeners(getSpringFactoriesInstances(ApplicationListener.class));
    
    // 推断主方法
    this.mainApplicationClass = deduceMainApplicationClass();
}
```

**关键特性**：
- **应用类型推断**: 通过classpath推断是Web应用、响应式应用还是普通应用
- **自动配置加载**: 从META-INF/spring.factories加载初始化器和监听器
- **主方法推断**: 自动找到包含main方法的类

#### 2.3 应用运行过程

```java
public ConfigurableApplicationContext run(String... args) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    
    ConfigurableApplicationContext context = null;
    Collection<SpringBootExceptionReporter> exceptionReporters = null;
    
    // 1. 配置启动器
    configureHeadlessProperty();
    
    // 2. 获取SpringApplicationRunListeners
    SpringApplicationRunListeners listeners = getRunListeners(args);
    
    // 3. 启动监听器
    listeners.starting();
    
    try {
        // 4. 创建ApplicationArguments
        ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
        
        // 5. 准备环境
        ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
        configureIgnoreBeanInfo(environment);
        
        // 6. 打印Banner
        Banner printedBanner = printBanner(environment);
        
        // 7. 创建应用上下文
        context = createApplicationContext();
        
        // 8. 准备报告
        exceptionReporters = getSpringFactoriesInstances(SpringBootExceptionReporter.class,
                new Class<?>[] { ConfigurableApplicationContext.class }, context);
        
        // 9. 准备应用上下文
        prepareContext(context, environment, listeners, applicationArguments, printedBanner);
        
        // 10. 刷新应用上下文
        refreshContext(context);
        
        // 11. 刷新后的处理
        afterRefresh(context, applicationArguments);
        
        // 12. 停止监听器
        stopWatch.stop();
        
        // 13. 记录启动时间
        if (this.logStartupInfo) {
            new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), stopWatch);
        }
        
        // 14. 发布应用已启动事件
        listeners.started(context);
        
        // 15. 调用运行器
        callRunners(context, applicationArguments);
    }
    catch (Throwable ex) {
        handleRunFailure(context, listeners, exceptionReporters, ex);
        throw new IllegalStateException(ex);
    }
    
    try {
        // 16. 发布应用运行中事件
        listeners.running(context);
    }
    catch (Throwable ex) {
        handleRunFailure(context, listeners, exceptionReporters, ex);
        throw new IllegalStateException(ex);
    }
    
    return context;
}
```

## 详细步骤解析

### 步骤1: 配置Headless属性

```java
private void configureHeadlessProperty() {
    System.setProperty("java.awt.headless", 
        System.getProperty("java.awt.headless", "true"));
}
```
- **作用**: 配置Headless模式，即使在没有GUI的环境中也能运行
- **应用**: 适用于服务器环境

### 步骤2: 获取SpringApplicationRunListeners

```java
private SpringApplicationRunListeners getRunListeners(String[] args) {
    Class<?>[] types = new Class<?>[] { SpringApplication.class, String[].class };
    return getSpringFactoriesInstances(SpringApplicationRunListeners.class, types, this, args);
}
```
- **机制**: 通过SpringFactoriesLoader从META-INF/spring.factories加载
- **作用**: 管理启动过程中的事件发布

### 步骤3: 准备环境

```java
private ConfigurableEnvironment prepareEnvironment(SpringApplicationRunListeners listeners,
        ApplicationArguments applicationArguments) {
    // 创建环境
    ConfigurableEnvironment environment = getOrCreateEnvironment();
    
    // 配置环境
    configureEnvironment(environment, applicationArguments.getSourceArgs());
    
    // 绑定到SpringApplication
    bindToSpringApplication(environment, this.webApplicationType);
    
    // 配置PropertySource
    if (!this.recommendedLogLevel.isEmpty()) {
        this.logStartUpInfo = true;
        LogConfigurer.updateLogLevels(this.logStartUpInfo, this.recommendedLogLevel);
    }
    
    // 触发环境准备事件
    listeners.environmentPrepared(environment);
    
    // 将环境迁移到SpringApplication
    attach(environment);
    
    return environment;
}
```

### 步骤4: 创建应用上下文

```java
protected ConfigurableApplicationContext createApplicationContext() {
    return this.applicationContextFactory.create(this.webApplicationType);
}

private ConfigurableApplicationContextFactory applicationContextFactory = DefaultApplicationContextFactory.get();

public ConfigurableApplicationContext create(WebApplicationType webApplicationType) {
    try {
        switch (webApplicationType) {
            case SERVLET:
                return new AnnotationConfigServletWebServerApplicationContext();
            case REACTIVE:
                return new AnnotationConfigReactiveWebServerApplicationContext();
            case NONE:
                return new AnnotationConfigApplicationContext();
        }
    }
    catch (Exception ex) {
        throw new IllegalStateException("Unable create a default ApplicationContext instance", ex);
    }
}
```

**上下文类型**：
- **SERVLET**: `AnnotationConfigServletWebServerApplicationContext` (Spring MVC)
- **REACTIVE**: `AnnotationConfigReactiveWebServerApplicationContext` (Spring WebFlux) 
- **NONE**: `AnnotationConfigApplicationContext` (普通应用)

### 步骤5: 准备应用上下文

```java
private void prepareContext(ConfigurableApplicationContext context, ConfigurableEnvironment environment,
        SpringApplicationRunListeners listeners, ApplicationArguments applicationArguments, Banner printedBanner) {
    // 设置环境
    context.setEnvironment(environment);
    
    // 设置应用名称
    postProcessApplicationContext(context);
    
    // 应用初始化器
    applyInitializers(context);
    
    // 触发上下文准备事件
    listeners.contextPrepared(context);
    
    // 记录启动信息
    if (this.logStartupInfo) {
        logStartupInfo(context.getParent() == null);
        logStartupProfileInfo(context);
    }
    
    // 注册SpringApplication参数
    ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
    beanFactory.registerSingleton("springApplicationArguments", applicationArguments);
    
    // 注册Spring Boot Banner
    if (printedBanner != null) {
        beanFactory.registerSingleton("springBootBanner", printedBanner);
    }
    
    // 设置是否有懒加载启动器
    if (beanFactory instanceof DefaultListableBeanFactory) {
        ((DefaultListableBeanFactory) beanFactory).setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
    }
    
    // 懒加载启动器
    if (this.lazyInitialization) {
        context.addBeanFactoryPostProcessor(new LazyInitializationBeanFactoryPostProcessor());
    }
}
```

### 步骤6: 刷新应用上下文

```java
private void refreshContext(ConfigurableApplicationContext context) {
    refresh(context);
}

protected void refresh(ConfigurableApplicationContext applicationContext) {
    applicationContext.refresh();
}
```

这里调用的是Spring Framework的refresh()方法，即我们熟悉的AbstractApplicationContext.refresh()方法：

```java
public void refresh() throws BeansException, IllegalStateException {
    synchronized (this.startupShutdownMonitor) {
        // 1. 准备刷新
        prepareRefresh();
        
        // 2. 获取BeanFactory
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
        
        // 3. 准备BeanFactory
        prepareBeanFactory(beanFactory);
        
        try {
            // 4. BeanFactory后处理
            postProcessBeanFactory(beanFactory);
            
            // 5. 执行BeanFactoryPostProcessor
            invokeBeanFactoryPostProcessors(beanFactory);
            
            // 6. 注册BeanPostProcessor
            registerBeanPostProcessors(beanFactory);
            
            // 7. 初始化MessageSource
            initMessageSource();
            
            // 8. 初始化事件多播器
            initApplicationEventMulticaster();
            
            // 9. 初始化特殊Bean
            onRefresh();
            
            // 10. 注册监听器
            registerListeners();
            
            // 11. 完成BeanFactory初始化
            finishBeanFactoryInitialization(beanFactory);
            
            // 12. 完成刷新
            finishRefresh();
        }
        
        catch (BeansException ex) {
            // 销毁已创建的Bean
            destroyBeans();
            // 重置活跃标志
            cancelRefresh(ex);
            // 抛出异常
            throw ex;
        }
        
        finally {
            // 重置缓存
            resetCommonCaches();
        }
    }
}
```

## Spring Boot特有特性分析

### 1. 自动配置机制

#### @SpringBootApplication注解

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
        @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {
    // ...
}
```

**三个核心注解**：
- `@SpringBootConfiguration`: 标记为配置类
- `@EnableAutoConfiguration`: 启用自动配置
- `@ComponentScan`: 组件扫描

#### 自动配置加载过程

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {
    // ...
}

public class AutoConfigurationImportSelector implements DeferredImportSelector {
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return StringUtils.toStringArray(importCandidates);
    }
}
```

**自动配置流程**：
1. 从META-INF/spring.factories加载配置类
2. 通过条件注解筛选适用配置
3. 创建配置Bean

### 2. 条件化配置

Spring Boot使用条件化配置控制Bean的创建：

```java
@ConditionalOnClass(DataSource.class)
@ConditionalOnMissingBean
public class DataSourceConfiguration {
    // 配置DataSource Bean
}

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(DispatcherServlet.class)
public class DispatcherServletAutoConfiguration {
    // 配置DispatcherServlet
}
```

**常用条件注解**：
- `@ConditionalOnClass`: classpath中包含指定类时
- `@ConditionalOnMissingBean`: 不存在指定Bean时
- `@ConditionalOnWebApplication`: 是Web应用时
- `@ConditionalOnProperty`: 配置属性满足条件时

### 3. 配置属性绑定

#### application.properties/application.yml

Spring Boot支持多种配置文件格式：

**application.properties**:
```properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/db
```

**application.yml**:
```yaml
server:
  port: 8080
  
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db
```

#### 配置属性绑定机制

```java
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties {
    private String url;
    private String username;
    // getter/setter
}

@Bean
@ConditionalOnMissingBean
@ConfigurationProperties(prefix = "spring.datasource")
public DataSource dataSource(DataSourceProperties properties) {
    // 根据配置创建DataSource
}
```

### 4. 嵌入式容器

Spring Boot提供嵌入式容器支持：

#### Servlet容器

```java
@AutoConfiguration
@ConditionalOnClass(ServletRequest.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class TomcatAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }
}
```

#### 嵌入式Tomcat

```java
public class TomcatServletWebServerFactory implements WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory> {
    
    public WebServer getWebServer(ServletContextInitializer... initializers) {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        // 配置tomcat
        return new TomcatWebServer(tomcat, this.getPort() >= 0);
    }
}
```

## Spring Boot启动事件流程

Spring Boot在整个启动过程中发布多种事件：

### 1. ApplicationStartingEvent

```java
listeners.starting();
```
- **时机**: SpringApplication开始启动时
- **用途**: 应用即将开始启动

### 2. ApplicationEnvironmentPreparedEvent

```java
listeners.environmentPrepared(environment);
```
- **时机**: Environment准备完成时
- **用途**: 环境配置已完成

### 3. ApplicationContextInitializedEvent

```java
listeners.contextPrepared(context);
```
- **时机**: ApplicationContext初始化完成时
- **用途**: 上下文已准备，但尚未刷新

### 4. ApplicationStartedEvent

```java
listeners.started(context);
```
- **时机**: ApplicationContext刷新完成时
- **用途**: 应用已完全启动

### 5. ApplicationReadyEvent

```java
listeners.running(context);
```
- **时机**: 应用准备处理请求时
- **用途**: 应用已准备就绪

## SpringApplicationRunListeners

Spring Boot通过SpringApplicationRunListeners管理启动过程：

```java
public class SpringApplicationRunListeners {
    
    private final List<SpringApplicationRunListener> listeners;
    
    public void starting() {
        for (SpringApplicationRunListener listener : this.listeners) {
            listener.starting();
        }
    }
    
    public void environmentPrepared(ConfigurableEnvironment environment) {
        for (SpringApplicationRunListener listener : this.listeners) {
            listener.environmentPrepared(environment);
        }
    }
}
```

默认实现为`EventPublishingRunListener`：

```java
public class EventPublishingRunListener implements SpringApplicationRunListener {
    
    private final SpringApplication application;
    private final String[] args;
    private final SimpleApplicationEventMulticaster multicaster;
    
    public void starting() {
        this.multicaster.multicastEvent(new ApplicationStartingEvent(this.application, this.args));
    }
    
    public void environmentPrepared(ConfigurableEnvironment environment) {
        this.multicaster.multicastEvent(new ApplicationEnvironmentPreparedEvent(this.application, this.args, environment));
    }
}
```

## 与传统Spring应用的区别

### 1. 启动方式

**传统Spring应用**:
```java
public static void main(String[] args) {
    ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    // 手动启动
}
```

**Spring Boot应用**:
```java
public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
    // 自动配置和启动
}
```

### 2. 配置方式

**传统Spring**: XML配置文件
**Spring Boot**: 约定优于配置，注解配置

### 3. 依赖管理

**传统Spring**: 手动管理依赖版本
**Spring Boot**: 依赖管理POM，通过starters简化依赖

### 4. 嵌入式容器

**传统Spring**: 需要外部Web容器
**Spring Boot**: 提供嵌入式容器

### 5. 监控和健康检查

**传统Spring**: 需要额外配置
**Spring Boot**: 内置Actuator监控

## 总结

Spring Boot的初始化过程是一个高度自动化和智能化的过程，从main方法开始，经过SpringApplication创建、环境准备、上下文创建和刷新，最终到应用完全启动。

**关键特点**：

1. **零配置启动**: 约定优于配置，自动推断和配置
2. **自动配置**: 基于条件化配置的智能Bean创建
3. **嵌入式容器**: 内置Web服务器，简化部署
4. **事件驱动**: 完整的启动事件机制
5. **分层架构**: Spring Framework + Spring Boot特有增强

这种设计极大地简化了Spring应用的开发和部署，同时保持了Spring框架的强大功能和灵活性。

---

*本文档基于真实Spring Boot源码分析，展示了从启动入口到应用完全启动的完整流程。*
