---
title: Bean 注册方式（Bean Registration Methods）
date: "2026-02-01"
categories:
  - springboot
tags:
  - springboot/mechanism
  - beans
  - annotation
description: 'Spring 提供多种将类或对象注册为 Bean 的方式，按注册对象可分为\"类注册\"（Stereotype 注解）与\"实例注册\"（@Bean 方法），以及框架专用的扫描注册链路。'
type: mechanism
---
# Bean 注册方式（Bean Registration Methods）

## 一句话

Spring 提供多种将类或对象注册为 Bean 的方式，按注册对象可分为"类注册"（Stereotype 注解）与"实例注册"（@Bean 方法），以及框架专用的扫描注册链路。

## 严格定义

Bean 注册方式定义了从"候选来源"到 `BeanDefinitionRegistry.registerBeanDefinition(...)` 的映射路径。设：

- $C$：候选类集合
- $M$：配置类中的方法集合
- $R$：`BeanDefinitionRegistry`

则注册方式可形式化为：

| 方式 | 映射 | 注册对象 |
|------|------|----------|
| Stereotype 扫描 | $f: C \to R$ | 类本身 |
| @Bean 方法 | $g: M \to R$ | 方法返回值 |
| 框架专用扫描 | $h: C \to R$ | 类本身（走专用注册链路） |

## 接口：数据 + 约束

### 输入
- 扫描基包（basePackages）
- 配置类（@Configuration）
- 框架专用注解（如 @MapperScan）

### 输出
- `BeanDefinition` 写入 `BeanDefinitionRegistry`
- 后续由容器按 [Bean 生命周期](BeanLifecycle.md) 实例化

### 约束
- Stereotype 扫描依赖 @ComponentScan 或 @SpringBootApplication 的默认扫描
- @Bean 方法必须位于 @Configuration 或 @Component 类中
- 框架专用扫描（如 MyBatis @MapperScan）走独立注册链路，不依赖 @Component

---

## 方式一：Stereotype 注解（类注册）

### 定义

Stereotype 注解标记"这个类是一个 @Component 候选"，扫描后类本身被注册为 Bean。

### 注解层次

```
@Component
├── @Service          // 业务层
├── @Repository       // 持久层
├── @Controller       // MVC 控制器
│   └── @RestController   // = @Controller + @ResponseBody
└── @Configuration    // 配置类（特殊处理：CGLIB 增强）
```

### 注册流程

```
@ComponentScan(basePackages)
       ↓
ClassPathBeanDefinitionScanner.scan()
       ↓
扫描带有 @Component 及其派生注解的类
       ↓
BeanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition)
       ↓
容器实例化该类 → 可注入、可 AOP
```

### 示例

```java
// 类本身注册为 Bean，beanName 默认 "userServiceImpl"
@Service
public class UserServiceImpl implements UserService {
    // ...
}

// 类本身注册为 Bean，beanName 默认 "userController"
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
}
```

### BeanDefinition 特征

| 属性 | 值 |
|------|-----|
| beanClass | 被标注的类（如 `UserServiceImpl.class`） |
| beanName | 类名首字母小写（或 @Component("name") 指定） |
| scope | 默认 singleton |

---

## 方式二：@Bean 方法（实例注册）

### 定义

@Bean 标记在方法上，方法的返回值作为 Bean 被注册。

### 注册流程

```
@Configuration 类被解析
       ↓
ConfigurationClassPostProcessor 处理配置类并派生注册（见 [ConfigurationClassPostProcessor.md](ConfigurationClassPostProcessor.md)）
       ↓
为每个 @Bean 方法生成 BeanDefinition
       ↓
BeanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition)
       ↓
调用方法获取返回值 → 返回值作为 Bean 实例
```

### 示例

```java
@Configuration
public class MybatisConfig {
    
    // 方法返回值注册为 Bean，beanName 默认 "mybatisPlusInterceptor"
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}
```

### BeanDefinition 特征

| 属性 | 值 |
|------|-----|
| beanClass | 方法返回类型（如 `MybatisPlusInterceptor.class`） |
| beanName | 方法名（或 @Bean("name") 指定） |
| factoryBeanName | 所在配置类的 beanName |
| factoryMethodName | 方法名 |

### @Configuration vs @Component 中的 @Bean

| 场景 | 行为 |
|------|------|
| @Configuration + @Bean | Full 模式：配置类被 CGLIB 代理，@Bean 方法间调用返回同一实例 |
| @Component + @Bean | Lite 模式：无代理，方法间调用会创建新实例 |

---

## 方式三：框架专用扫描（如 @MapperScan）

### 定义

某些框架提供专用扫描注解，走独立的注册链路，不依赖 @Component 体系。

### 示例：MyBatis @MapperScan

```java
@SpringBootApplication
@MapperScan("com.hmdp.mapper")  // 扫描 Mapper 接口
public class HmDianPingApplication {
    public static void main(String[] args) {
        SpringApplication.run(HmDianPingApplication.class, args);
    }
}
```

### 注册流程

```
@MapperScan(basePackages)
       ↓
MapperScannerRegistrar implements ImportBeanDefinitionRegistrar
       ↓
ClassPathMapperScanner.scan()
       ↓
扫描接口 + 生成 MapperFactoryBean 的 BeanDefinition
       ↓
BeanDefinitionRegistry.registerBeanDefinition(...)
       ↓
容器实例化时通过 FactoryBean.getObject() 返回代理
```

### 特征

| 属性 | 值 |
|------|-----|
| 扫描目标 | 接口（不是类） |
| beanClass | `MapperFactoryBean`（FactoryBean） |
| 实际 Bean | 动态代理实例 |
| 不需要 | @Component、@Repository 等注解 |

---

## 对比总结

| 维度 | Stereotype (@Service 等) | @Bean 方法 | 框架专用 (@MapperScan) |
|------|--------------------------|-----------|----------------------|
| 注册对象 | 类本身 | 方法返回值 | 接口的代理实例 |
| 标注位置 | 类上 | 方法上 | 启动类/配置类上 |
| 扫描器 | ClassPathBeanDefinitionScanner | ConfigurationClassPostProcessor | 框架自定义 Scanner |
| BeanDefinition.beanClass | 被标注的类 | 返回类型 | FactoryBean |
| 典型场景 | 业务组件 | 第三方库对象、复杂构造 | ORM Mapper、RPC 接口 |

---

## 关系

- 上级：[Bean 注册与创建流程](../../../../flows/Bean注册与创建流程.md)
- 下游：[BeanDefinition](../interface/BeanDefinition.md)、[BeanDefinitionRegistry](../interface/BeanDefinitionRegistry.md)
- 相关：[Bean 生命周期](BeanLifecycle.md)（注册后的实例化流程）
- 相关：[FactoryBean](../interface/FactoryBean.md)（@MapperScan 使用的工厂模式）

## 把新概念挂回框架（多级索引轨迹）

springboot → modules → core → beans → mechanism → BeanRegistrationMethods
