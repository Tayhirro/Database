---
title: Spring Boot 初始化过程源码分析
date: "2026-01-07"
categories:
  - study
description: "本文档基于 mini-spring 框架源码，深入分析了 Spring Boot 应用从启动入口（main方法）到完成整个应用上下文刷新的完整初始化流程。通过对源码的详细解读，我们将了解 Spring Boot 是如何从程序启动到Bean容器准备就绪的全过程。"
---
# Spring Boot 初始化过程源码分析

## 概述

本文档基于 mini-spring 框架源码，深入分析了 Spring Boot 应用从启动入口（main方法）到完成整个应用上下文刷新的完整初始化流程。通过对源码的详细解读，我们将了解 Spring Boot 是如何从程序启动到Bean容器准备就绪的全过程。

## 完整的启动流程

### 1. 应用程序启动入口

在典型的Spring Boot应用中，入口通常是这样的：

```java
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

### 2. SpringApplication.run() 方法的执行流程

SpringApplication.run()方法执行以下关键步骤：

1. **创建SpringApplication实例**
2. **执行初始化器（ApplicationContextInitializer）**
3. **加载应用监听器（ApplicationListener）**
4. **创建和刷新ApplicationContext**

## ApplicationContext创建流程详细分析

### ClassPathXmlApplicationContext - ApplicationContext的创建入口

在mini-spring框架中，ApplicationContext的创建过程如下：

```java
public class ClassPathXmlApplicationContext extends AbstractXmlApplicationContext {
    
    public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
        this(new String[]{configLocation});
    }
    
    public ClassPathXmlApplicationContext(String[] configLocations) throws BeansException {
        this.configLocations = configLocations;
        refresh(); // 核心：调用refresh()方法开始初始化
    }
}
```

**关键点**：
- 构造函数中直接调用`refresh()`方法
- 这是整个Spring容器初始化的起点
- 完成了从"创建Context"到"初始化Context"的无缝衔接

### AbstractRefreshableApplicationContext - BeanFactory的创建

```java
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {
    
    private DefaultListableBeanFactory beanFactory;
    
    @Override
    protected final ConfigurableListableBeanFactory obtainFreshBeanFactory() {
        refreshBeanFactory(); // 创建新的BeanFactory
        return getBeanFactory();
    }
    
    protected void refreshBeanFactory() throws BeansException {
        // 如果已存在BeanFactory，则销毁所有单例Bean并关闭工厂
        if (this.beanFactory != null) {
            this.beanFactory.destroySingletons();
            this.beanFactory = null;
        }
        
        // 创建新的BeanFactory
        DefaultListableBeanFactory beanFactory = createBeanFactory();
        
        // 加载Bean定义
        loadBeanDefinitions(beanFactory);
        
        this.beanFactory = beanFactory;
    }
    
    protected DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory();
    }
}
```

**关键点**：
- `obtainFreshBeanFactory()`是refresh()方法中的第2步
- 每次刷新都会创建新的BeanFactory
- 支持重复刷新，确保每次都是干净的状态

## 核心组件架构

### 1. ApplicationContext（应用上下文）
```java
public interface ApplicationContext extends ListableBeanFactory, ResourceLoader {
    String getId();
    String getDisplayName();
    long getStartupDate();
    ApplicationContext getParent();
}
```
- **作用**: 作为中央接口，扩展了 ListableBeanFactory 和 ResourceLoader
- **职责**: 提供应用层特性，管理bean生命周期和资源加载

### 2. AbstractApplicationContext（抽象应用上下文）
```java
public abstract class AbstractApplicationContext extends DefaultResourceLoader 
        implements ApplicationContext {
    private final long startupDate;
    private final AtomicBoolean active = new AtomicBoolean();
    private final AtomicBoolean closed = new AtomicBoolean();
    private ApplicationContext parent;
    private String id;
    private String displayName;
}
```
- **作用**: ApplicationContext 的抽象实现，提供基础功能
- **核心方法**: `refresh()` - 定义了完整的初始化流程

### 3. DefaultListableBeanFactory（默认列表化Bean工厂）
```java
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory
        implements ConfigurableListableBeanFactory, BeanDefinitionRegistry {
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);
    private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>(16);
}
```
- **作用**: 核心Bean工厂实现，负责Bean的注册、创建和管理
- **特点**: 支持单例缓存、三级缓存机制、Bean定义管理

## 初始化流程详细分析

### refresh() 方法 - 核心初始化模板

AbstractApplicationContext 中的 `refresh()` 方法定义了 Spring Boot 初始化的完整模板：

```java
public void refresh() throws Exception {
    synchronized (this) {
        // 1. 准备刷新上下文
        prepareRefresh();
        
        // 2. 获取bean工厂
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
        
        // 3. 准备bean工厂
        prepareBeanFactory(beanFactory);
        
        try {
            // 4. 允许在上下文子类中对bean工厂进行后处理
            postProcessBeanFactory(beanFactory);
            
            // 5. 调用BeanFactoryPostProcessor
            invokeBeanFactoryPostProcessors(beanFactory);
            
            // 6. 注册BeanPostProcessor
            registerBeanPostProcessors(beanFactory);
            
            // 7. 初始化消息源
            initMessageSource();
            
            // 8. 初始化事件多播器
            initApplicationEventMulticaster();
            
            // 9. 初始化其他特殊bean
            onRefresh();
            
            // 10. 注册监听器
            registerListeners();
            
            // 11. 完成bean工厂的初始化
            finishBeanFactoryInitialization(beanFactory);
            
            // 12. 完成刷新
            finishRefresh();
        } catch (Exception ex) {
            logger.error("Context refresh failed", ex);
            throw ex;
        }
    }
}
```

## 各阶段详细解析

### 1. prepareRefresh() - 准备刷新
```java
protected void prepareRefresh() {
    this.active.set(true);
    this.closed.set(false);
    logger.info("Refreshing " + getDisplayName());
}
```
- **功能**: 设置上下文状态为活跃，标记开始刷新过程
- **状态管理**: 使用 AtomicBoolean 确保线程安全的状态切换

### 2. obtainFreshBeanFactory() - 获取Bean工厂
```java
protected abstract ConfigurableListableBeanFactory obtainFreshBeanFactory();
```
- **功能**: 抽象方法，由子类实现获取新的BeanFactory
- **意义**: 允许不同类型的上下文提供不同的BeanFactory实现
- **实现**: 在AbstractRefreshableApplicationContext中创建新的DefaultListableBeanFactory

### 3. prepareBeanFactory() - 准备Bean工厂
```java
protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    // 设置类加载器
    beanFactory.setBeanClassLoader(getClassLoader());
}
```
- **功能**: 配置Bean工厂的基础属性
- **扩展点**: 子类可以重写此方法进行更多配置

### 4. postProcessBeanFactory() - 后处理Bean工厂
```java
protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    // 默认实现为空，留给子类扩展
}
```
- **功能**: 钩子方法，允许子类在Bean工厂准备好后进行后处理
- **应用**: Spring Boot 中的自动配置经常在此阶段执行

### 5. invokeBeanFactoryPostProcessors() - 调用Bean工厂后处理器
```java
protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    // 执行BeanFactoryPostProcessor
}
```
- **功能**: 执行所有BeanFactoryPostProcessor
- **作用**: 修改Bean定义，实现配置文件的解析和转换

### 6. registerBeanPostProcessors() - 注册Bean后处理器
```java
protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    // 注册BeanPostProcessor
}
```
- **功能**: 注册所有BeanPostProcessor
- **作用**: 为Bean的初始化过程提供扩展点

### 7. initMessageSource() - 初始化消息源
```java
protected void initMessageSource() {
    // 初始化消息源
}
```
- **功能**: 初始化国际化消息源
- **用途**: 支持多语言应用

### 8. initApplicationEventMulticaster() - 初始化事件多播器
```java
protected void initApplicationEventMulticaster() {
    ConfigurableListableBeanFactory beanFactory = getBeanFactory();
    applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
}
```
- **功能**: 初始化事件发布和监听机制
- **作用**: 支持Spring的事件驱动编程模型

### 9. onRefresh() - 刷新特殊Bean
```java
protected void onRefresh() {
    // 留给子类实现特殊bean的初始化
}
```
- **功能**: 钩子方法，子类可以初始化特殊用途的Bean
- **应用**: Web上下文会初始化Web相关组件

### 10. registerListeners() - 注册监听器
```java
protected void registerListeners() {
    // 获取所有ApplicationListener类型的bean
    Collection<ApplicationListener> listeners = getBeansOfType(ApplicationListener.class).values();
    for (ApplicationListener listener : listeners) {
        applicationEventMulticaster.addApplicationListener(listener);
    }
}
```
- **功能**: 注册所有ApplicationListener
- **作用**: 建立事件监听机制

### 11. finishBeanFactoryInitialization() - 完成Bean工厂初始化
```java
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // 初始化所有剩余的单例bean
}
```
- **功能**: 预实例化所有单例Bean
- **核心流程**: 详见下面的Bean创建流程分析

### 12. finishRefresh() - 完成刷新
```java
protected void finishRefresh() {
    // 完成刷新，发布上下文刷新事件
    publishEvent(new ContextRefreshedEvent(this));
}
```
- **功能**: 完成整个刷新过程，发布ContextRefreshedEvent事件
- **标志**: 应用上下文完全初始化完成

## Bean创建流程详细分析

### DefaultListableBeanFactory 中的 Bean 创建核心方法

#### 1. createBean() - 创建Bean实例
```java
@Override
protected Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException {
    try {
        if (beanDefinition.isSingleton() && beanDefinition.getConstructorArgumentValues() != null 
            && beanDefinition.getConstructorArgumentValues().size() > 0) {
            beforeSingletonCreation(beanName);
            try {
                // 创建bean实例
                final Object bean = createBeanInstance(beanDefinition);
                
                // 填充属性
                populateBean(beanName, bean, beanDefinition);
                
                // 初始化bean
                Object exposedObject = initializeBean(beanName, bean, beanDefinition);
                
                // 将完整的bean加入到单例缓存
                addSingleton(beanName, exposedObject);
                
                return exposedObject;
            } finally {
                afterSingletonCreation(beanName);
            }
        }
        // ... 其他处理逻辑
    } catch (Exception e) {
        throw new BeansException("Error creating bean with name '" + beanName + "'", e);
    }
}
```

#### 2. Bean创建的四个核心步骤

**步骤1: createBeanInstance() - 创建Bean实例**
```java
protected Object createBeanInstance(BeanDefinition beanDefinition) throws BeansException {
    Class<?> beanClass = beanDefinition.getBeanClass();
    if (beanClass == null) {
        throw new BeansException("Bean class is not set for bean definition");
    }
    
    try {
        if (beanDefinition.hasConstructorArgumentValues()) {
            return autowireConstructor(beanDefinition);
        }
        return beanClass.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
        throw new BeansException("Error creating bean instance for " + beanClass, e);
    }
}
```

**步骤2: populateBean() - 填充属性**
```java
protected void populateBean(String beanName, Object bean, BeanDefinition beanDefinition) throws BeansException {
    PropertyValues propertyValues = beanDefinition.getPropertyValues();
    if (propertyValues != null) {
        for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
            String propertyName = propertyValue.getName();
            Object value = propertyValue.getValue();
            Class<?> type = propertyValue.getType();
            
            try {
                // 处理依赖注入
                if (value instanceof String && type != String.class) {
                    String refBeanName = (String) value;
                    value = getBean(refBeanName);
                }
                
                String methodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
                Method setter = bean.getClass().getMethod(methodName, type);
                setter.setAccessible(true);
                setter.invoke(bean, value);
            } catch (Exception e) {
                throw new BeansException("Error setting property '" + propertyName + "' for bean '" + beanName + "'", e);
            }
        }
    }
}
```

**步骤3: initializeBean() - 初始化Bean**
```java
protected Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {
    // 执行Aware方法
    if (bean instanceof Aware) {
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(this);
        }
        if (bean instanceof BeanNameAware) {
            ((BeanNameAware) bean).setBeanName(beanName);
        }
    }

    // 执行BeanPostProcessor的前置处理
    Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

    // 执行初始化方法
    try {
        invokeInitMethods(beanName, wrappedBean, beanDefinition);
    } catch (Exception e) {
        throw new BeansException("Invocation of init method failed", e);
    }

    // 执行BeanPostProcessor的后置处理
    wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
    return wrappedBean;
}
```

**步骤4: addSingleton() - 添加到单例缓存**
```java
public void addSingleton(String beanName, Object singletonObject) {
    synchronized (this.singletonObjects) {
        this.singletonObjects.put(beanName, singletonObject);
        // 从二级和三级缓存中移除
        this.earlySingletonObjects.remove(beanName);
        this.singletonFactories.remove(beanName);
    }
}
```

## 三级缓存机制

### 缓存结构
```java
/** 一级缓存：完整的单例对象 */
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

/** 二级缓存：早期的单例对象 */
private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);

/** 三级缓存：单例工厂 */
private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>(16);
```

### 缓存级别说明

**一级缓存 (singletonObjects)**
- 存储完全初始化好的单例Bean
- 其他组件可以直接使用的完整对象
- 线程安全的ConcurrentHashMap

**二级缓存 (earlySingletonObjects)**  
- 存储早期暴露的单例对象
- 用于解决循环依赖问题
- 存储的是未完全初始化的Bean对象

**三级缓存 (singletonFactories)**
- 存储Bean工厂ObjectFactory
- 用于创建早期Bean引用
- 支持AOP代理的延迟创建

### 循环依赖解决机制

```java
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
    // 首先检查一级缓存
    Object singletonObject = this.singletonObjects.get(beanName);
    
    if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
        synchronized (this.singletonObjects) {
            // 检查二级缓存
            singletonObject = this.earlySingletonObjects.get(beanName);
            
            if (singletonObject == null && allowEarlyReference) {
                // 检查三级缓存
                ObjectFactory<?> factory = this.singletonFactories.get(beanName);
                if (factory != null) {
                    // 从工厂获取对象
                    singletonObject = factory.getObject();
                    // 放入二级缓存
                    this.earlySingletonObjects.put(beanName, singletonObject);
                    // 从三级缓存移除
                    this.singletonFactories.remove(beanName);
                }
            }
        }
    }
    return singletonObject;
}
```

## 关键设计模式

### 1. 模板方法模式
- **应用**: AbstractApplicationContext.refresh() 方法定义了初始化流程模板
- **优势**: 提供稳定的算法骨架，允许子类重写特定步骤

### 2. 工厂模式
- **应用**: BeanFactory 体系管理Bean的创建和生命周期
- **优势**: 分离对象的创建和使用，提高代码可维护性

### 3. 单例模式
- **应用**: 单例Bean的缓存机制
- **优势**: 保证对象的唯一性，减少内存开销

### 4. 观察者模式
- **应用**: ApplicationEvent 和 ApplicationListener
- **优势**: 实现松耦合的事件驱动机制

### 5. 策略模式
- **应用**: 不同类型的BeanPostProcessor和BeanFactoryPostProcessor
- **优势**: 可以灵活地替换和扩展处理策略

## 扩展点和钩子方法

### 主要扩展点

1. **postProcessBeanFactory()**: Bean工厂后处理
2. **onRefresh()**: 特殊Bean初始化
3. **initMessageSource()**: 消息源初始化
4. **initApplicationEventMulticaster()**: 事件多播器初始化
5. **registerListeners()**: 监听器注册

### BeanPostProcessor 接口
```java
public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;
}
```

### BeanFactoryPostProcessor 接口
```java
public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
}
```

## 性能优化点

### 1. 并发缓存
- 使用ConcurrentHashMap确保线程安全
- 减少锁竞争，提高并发性能

### 2. 延迟初始化
- 采用延迟加载策略，只有在需要时才创建Bean
- 减少启动时间和内存占用

### 3. 循环依赖检测
- 三级缓存机制高效解决循环依赖问题
- 避免无限递归创建

### 4. 批量处理
- 预实例化单例Bean，提高后续访问性能
- 减少运行时创建Bean的开销

## 完整流程总结

### 从启动到Context创建完成的完整时序

1. **应用启动**: `public static void main(String[] args)`
2. **SpringApplication.run()**: 创建SpringApplication实例并启动
3. **创建ApplicationContext**: 触发ClassPathXmlApplicationContext构造函数
4. **refresh()调用**: 在构造函数中直接调用refresh()方法
5. **obtainFreshBeanFactory()**: 创建新的DefaultListableBeanFactory
6. **12步初始化流程**: 执行完整的容器初始化流程
7. **Bean创建**: 通过四级流程创建所有单例Bean
8. **容器就绪**: 发布ContextRefreshedEvent，应用完全启动

### Context创建的核心特点

1. **无缝衔接**: 从构造函数直接进入refresh()，无需额外调用
2. **模板方法**: refresh()方法定义了标准的12步初始化流程
3. **可重复刷新**: 每次刷新都会创建新的BeanFactory
4. **完整生命周期**: 从Bean定义加载到Bean实例化的完整管理
5. **事件驱动**: 通过事件机制提供扩展点

## 总结

Spring Boot 的初始化过程是一个复杂而精密的工程，从程序启动入口main方法开始，经历了SpringApplication.run()、ApplicationContext创建、refresh()方法执行、BeanFactory初始化、Bean创建等多个阶段。

从源码分析可以看出：

1. **完整流程**: 从启动入口到容器就绪的完整时序
2. **分层设计**: ApplicationContext → BeanFactory → 具体实现的分层架构
3. **模板方法**: 通过refresh()方法定义稳定的初始化流程
4. **扩展机制**: 提供丰富的钩子方法和扩展接口
5. **缓存优化**: 三级缓存机制既保证了性能又解决了循环依赖
6. **生命周期管理**: 完善的Bean生命周期管理机制

这种设计体现了Spring框架的优秀架构思想：稳定性、可扩展性、可维护性和高性能。对于理解Spring Boot的工作原理和进行深度定制具有重要意义。

---

*本文档基于mini-spring项目源码分析，展示了从Spring Boot启动入口到ApplicationContext创建完成的完整初始化流程。*
