---
title: AnnotationProcessing（注解处理机制）
date: "2026-02-03"
categories:
  - springboot
tags:
  - springboot/beans
  - annotation
  - mechanism
description: 类型：机制（Mechanism）
type: mechanism
---
# AnnotationProcessing（注解处理机制）

> **类型**：机制（Mechanism）

## 一句话

Spring 生态系统中的注解按**处理时机**可分为**编译时处理**（APT）与**运行时处理**（反射），二者在处理器实现、处理目标和技术本质上完全不同。

## 严格定义

在 Spring 技术栈中，注解（Annotation）的处理并非单一机制，而是根据 `@Retention` 元注解和框架设计，在不同生命周期阶段由不同处理器完成：

- **编译时注解处理**（Compile-time）：`RetentionPolicy.SOURCE` 或 `CLASS`，由 `javax.annotation.processing.Processor` 在 javac 阶段处理，生成或修改代码
- **运行时注解处理**（Runtime）：`RetentionPolicy.RUNTIME`，由 Spring 容器通过反射在 JVM 运行时处理，完成配置、依赖注入、AOP 等

## 两种注解处理的根本区别

### 对比维度

| 维度          | 编译时注解处理             | 运行时注解处理            |
| ----------- | ------------------- | ------------------ |
| **处理时机**    | javac 编译阶段          | JVM 运行期间           |
| **注解保留策略**  | `SOURCE` / `CLASS`  | `RUNTIME`          |
| **处理器类型**   | `AbstractProcessor` | 反射 API + Spring 容器 |
| **处理目标**    | 生成/修改源代码或字节码        | 创建 Bean、配置映射、依赖注入  |
| **代表框架**    | Lombok、MapStruct    | Spring、MyBatis、JPA |
| **性能影响**    | 增加编译时间              | 增加启动时间和运行时开销       |
| **是否能修改源码** | 是（修改 AST）           | 否（只能读取）            |

### 流程对比

```
【编译时注解处理】
User.java (源码) 
    ↓ javac
Annotation Processor (Lombok/MapStruct)
    ↓ 修改 AST / 生成新代码
User.class (字节码已修改)
    ↓ JVM 加载
直接使用（方法已存在）

【运行时注解处理】
User.java (源码，带 @Component)
    ↓ javac
User.class (字节码保留注解)
    ↓ JVM 加载
    ↓ Spring 启动
ConfigurationClassPostProcessor / ComponentScan
    ↓ 反射读取注解
注册 BeanDefinition → 实例化 → 依赖注入
```

## 编译时注解处理详解

### 触发时机
- **Maven/Gradle 编译阶段**：执行 `mvn compile` 或 `gradle build` 时
- **IDE 自动编译**：保存文件时自动触发
- **处理器发现**：通过 `META-INF/services/javax.annotation.processing.Processor` 注册

### 典型框架与注解

| 框架 | 注解 | 处理效果 |
|------|------|---------|
| **Lombok** | `@Data` | 生成 getter/setter/equals/hashCode/toString |
| **Lombok** | `@Builder` | 生成 Builder 模式代码 |
| **Lombok** | `@Slf4j` | 生成日志变量 `log` |
| **MapStruct** | `@Mapper` | 生成类型转换实现类 |
| **QueryDSL** | `@Entity` + APT | 生成 Q 类（查询元模型） |

### 处理示例（Lombok @Data）

```java
// 开发者编写的源码
@Data
public class User {
    private Long id;
    private String name;
}

// 编译后生成的字节码（实际效果）
public class User {
    private Long id;
    private String name;
    
    // Lombok 在编译期插入的方法
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    public boolean equals(Object o) { ... }
    public int hashCode() { ... }
    public String toString() { ... }
}
```

### 技术实现要点

```java
// 1. 定义注解（RetentionPolicy.SOURCE）
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)  // 编译后丢弃
public @interface GenerateBuilder {
}

// 2. 实现处理器
@SupportedAnnotationTypes("com.example.GenerateBuilder")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class GenerateBuilderProcessor extends AbstractProcessor {
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, 
                          RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(GenerateBuilder.class)) {
            // 使用 JavaPoet 等库生成代码
            generateBuilderClass(element);
        }
        return true;
    }
}

// 3. 注册处理器（META-INF/services/javax.annotation.processing.Processor）
// 文件内容：com.example.GenerateBuilderProcessor
```

## 运行时注解处理详解

### Spring Boot 启动过程中的注解处理时间线

```
SpringApplication.run()
    │
    ├─ 阶段 1：编译期（已发生）
    │   ├─ Lombok 处理 @Data → 生成方法
    │   └─ MapStruct 处理 @Mapper → 生成实现类
    │
    ├─ 阶段 2：Spring 启动期（运行时处理）
    │   │
    │   ├─ 2.1 prepareContext()
    │   │   └─ 注册 primary source（主类）为 BeanDefinition
    │   │
    │   ├─ 2.2 refreshContext() → context.refresh()
    │   │   │
    │   │   ├─ invokeBeanFactoryPostProcessors() 【关键阶段】
    │   │   │   └─ ConfigurationClassPostProcessor 工作
    │   │   │       ├─ 解析 @SpringBootApplication
    │   │   │       ├─ @ComponentScan → 扫描 @Component/@Service/@Controller
    │   │   │       ├─ @Bean 方法 → 注册 factory method BeanDefinition
    │   │   │       └─ @Import → 导入配置类
    │   │   │
    │   │   ├─ registerBeanPostProcessors()
    │   │   │   └─ 注册 AutowiredAnnotationBeanPostProcessor 等
    │   │   │
    │   │   ├─ onRefresh() 【子类扩展点】
    │   │   │   └─ WebServer 创建（ServletWebServerApplicationContext）
    │   │   │
    │   │   └─ finishBeanFactoryInitialization()
    │   │       ├─ 实例化非 lazy Bean
    │   │       ├─ 触发 BeanPostProcessor
    │   │       │   └─ AutowiredAnnotationBeanPostProcessor 处理 @Autowired/@Value
    │   │       └─ CommonAnnotationBeanPostProcessor 处理 @PostConstruct
    │   │
    │   └─ 2.3 运行时（请求处理期）
    │       ├─ HandlerMapping 处理 @RequestMapping（首次请求或启动时）
    │       ├─ MyBatis Plus 扫描 @TableName/@TableId（Mapper 初始化时）
    │       └─ ...
    │
    └─ 阶段 3：运行期持续处理
        ├─ AOP 代理创建（含 @Transactional/@Cacheable 等）
        ├─ @Scheduled 定时任务调度
        └─ @EventListener 事件监听
```

### 按阶段分类的运行时注解

#### 阶段 A：BeanDefinition 扫描与注册（BFPP 阶段）

**触发时机**：`invokeBeanFactoryPostProcessors()`
**核心处理器**：`ConfigurationClassPostProcessor`

| 注解 | 处理动作 | 输出结果 |
|------|---------|---------|
| `@SpringBootApplication` | 解析复合注解 | 触发 @Configuration + @ComponentScan + @EnableAutoConfiguration |
| `@ComponentScan` | 扫描 basePackages | 发现 @Component/@Service/@Controller/@Repository 并注册 BeanDefinition |
| `@Component` 及其派生 | 标记类为 Bean 候选 | 生成 ScannedGenericBeanDefinition |
| `@Bean` | 解析方法 | 生成 ConfigurationClassBeanDefinition（factoryMethod 语义） |
| `@Import` | 导入配置类 | 注册导入类的 BeanDefinition |
| `@Conditional` 族 | 条件判断 | 决定是否注册（@ConditionalOnClass 等） |

```java
// 处理示例
@Configuration
@ComponentScan("com.example")
public class AppConfig {
    @Bean
    public DataSource dataSource() { ... }
}

// ConfigurationClassPostProcessor 处理结果：
// 1. 扫描 com.example 包，发现 @Service/@Controller 等，注册 BeanDefinition
// 2. 解析 @Bean 方法，注册 dataSource 的 BeanDefinition（factoryMethodName="dataSource"）
```

#### 阶段 B：Bean 实例化与依赖注入（BPP 阶段）

**触发时机**：`finishBeanFactoryInitialization()` 中的 Bean 创建流程
**核心处理器**：各类 `BeanPostProcessor`

| 注解 | 处理器 | 处理时机 | 动作 |
|------|--------|---------|------|
| `@Autowired` / `@Value` | `AutowiredAnnotationBeanPostProcessor` | Bean 实例化后，初始化前 | 依赖注入：按类型/名称查找 Bean，注入字段/方法 |
| `@Resource` | `CommonAnnotationBeanPostProcessor` | Bean 实例化后 | JNDI 或 Spring Bean 注入 |
| `@Inject` (JSR-330) | `AutowiredAnnotationBeanPostProcessor` | Bean 实例化后 | 同 @Autowired |
| `@PostConstruct` | `CommonAnnotationBeanPostProcessor` | 依赖注入完成后 | 调用初始化方法 |
| `@PreDestroy` | `CommonAnnotationBeanPostProcessor` | 容器关闭时 | 调用销毁方法 |

```java
// 处理示例
@Service
public class UserService {
    @Autowired  // 运行时反射处理
    private UserMapper userMapper;
    
    @Value("${app.name}")  // 运行时处理
    private String appName;
    
    @PostConstruct  // 运行时处理
    public void init() {
        // 依赖注入完成后执行
    }
}

// AutowiredAnnotationBeanPostProcessor 处理：
// 1. 反射查找 @Autowired 字段
// 2. 从 BeanFactory 查找 UserMapper Bean
// 3. 反射设置字段值：field.set(userService, userMapperInstance)
```

#### 阶段 C：Spring MVC 请求处理（Servlet 初始化后）

**触发时机**：首次请求或容器刷新时（Servlet 初始化）
**核心处理器**：`RequestMappingHandlerMapping`, `RequestMappingHandlerAdapter`

| 注解 | 处理器 | 处理动作 |
|------|--------|---------|
| `@Controller` / `@RestController` | `RequestMappingHandlerMapping` | 识别为 handler 候选 |
| `@RequestMapping` 族 | `RequestMappingHandlerMapping` | 建立 URL → HandlerMethod 映射 |
| `@PathVariable` | `PathVariableMethodArgumentResolver` | 从 URL 路径提取参数值 |
| `@RequestParam` | `RequestParamMethodArgumentResolver` | 从 QueryString 提取参数 |
| `@RequestBody` | `RequestResponseBodyMethodProcessor` | HTTP Body → Java 对象（反序列化） |
| `@ResponseBody` | `RequestResponseBodyMethodProcessor` | Java 对象 → HTTP Body（序列化） |
| `@ModelAttribute` | `ModelAttributeMethodProcessor` | 绑定请求参数到对象 |

```java
// 处理示例
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    @ResponseBody
    public UserDTO getUser(@PathVariable Long id) {  // 运行时解析 URL 提取 id
        return userService.findById(id);
    }
}

// RequestMappingHandlerMapping 处理：
// 1. 扫描 @Controller 类
// 2. 解析 @RequestMapping("/{id}") → 注册映射：GET /api/users/{id} → UserController.getUser(Long)
// 
// 请求处理时：
// 1. PathVariableMethodArgumentResolver 从 URL /api/users/123 提取 id=123
// 2. RequestResponseBodyMethodProcessor 将返回的 UserDTO 序列化为 JSON
```

#### 阶段 D：持久层框架初始化（Bean 初始化后）

**触发时机**：Mapper Bean 初始化时（通常在第一次使用或启动时）
**核心处理器**：框架特定的扫描器

| 框架 | 注解 | 处理器 | 处理动作 |
|------|------|--------|---------|
| **MyBatis Plus** | `@TableName`, `@TableId`, `@TableField` | `TableInfoHelper` | 解析实体类与表映射，构建 TableInfo 缓存 |
| **MyBatis** | `@Mapper` | `MapperScannerConfigurer` | 扫描并注册 Mapper 接口 |
| **JPA/Hibernate** | `@Entity`, `@Table`, `@Column` | `MetadataSources` | 构建实体元数据，生成 SQL 映射 |
| **Spring Data** | `@Repository` | 内置扫描器 | 生成动态代理实现 |

```java
// MyBatis Plus 处理示例
@TableName("t_user")  // 运行时处理
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_name")
    private String name;
}

// TableInfoHelper 处理：
// 1. 反射读取 @TableName → 表名 = "t_user"
// 2. 反射读取 @TableId → 主键字段 = "id"
// 3. 反射读取 @TableField 映射关系
// 4. 构建 TableInfo 对象缓存，用于后续 SQL 生成
```

#### 阶段 E：AOP 与事务（Bean 初始化后期）

**触发时机**：`BeanPostProcessor.postProcessAfterInitialization()`
**核心处理器**：`AnnotationAwareAspectJAutoProxyCreator`, `InfrastructureAdvisorAutoProxyCreator`

| 注解 | 处理器 | 处理动作 |
|------|--------|---------|
| `@Aspect` | `AnnotationAwareAspectJAutoProxyCreator` | 解析切面定义，创建 AOP 代理 |
| `@Transactional` | `InfrastructureAdvisorAutoProxyCreator` | 创建事务代理，编织事务逻辑 |
| `@Cacheable` | `CacheInterceptor` | 创建缓存代理，编织缓存逻辑 |
| `@Async` | `AsyncAnnotationBeanPostProcessor` | 创建异步执行代理 |

```java
// AOP 处理示例
@Aspect
@Component
public class LoggingAspect {
    @Around("@annotation(Loggable)")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        // ...
    }
}

// AnnotationAwareAspectJAutoProxyCreator 处理：
// 1. 扫描 @Aspect 类
// 2. 解析 @Pointcut 和 @Around/@Before 等通知
// 3. 为目标 Bean 创建代理（JDK 动态代理或 CGLIB）
// 4. 代理拦截方法调用，执行切面逻辑
```

## 完整注解处理时间线总结

```
时间轴：从代码编写到运行
│
├─【编译期】（javac）
│   ├─ Lombok: @Data/@Builder → 生成代码
│   ├─ MapStruct: @Mapper → 生成转换实现
│   └─ QueryDSL: APT → 生成 Q 类
│
├─【Spring 启动期 - BFPP 阶段】
│   └─ ConfigurationClassPostProcessor:
│       ├─ @SpringBootApplication/@Configuration
│       ├─ @ComponentScan → @Component/@Service/@Controller/@Repository
│       ├─ @Bean
│       └─ @Import/@Conditional
│
├─【Spring 启动期 - BPP 阶段】
│   ├─ AutowiredAnnotationBeanPostProcessor:
│   │   └─ @Autowired/@Value/@Inject
│   └─ CommonAnnotationBeanPostProcessor:
│       ├─ @Resource
│       ├─ @PostConstruct
│       └─ @PreDestroy
│
├─【Spring 启动期 - 初始化后期】
│   ├─ AnnotationAwareAspectJAutoProxyCreator:
│   │   └─ @Aspect/@Transactional/@Cacheable/@Async
│   └─ 框架特定扫描:
│       ├─ MyBatis Plus: @TableName/@TableId/@TableField
│       ├─ JPA: @Entity/@Table/@Column
│       └─ Spring Data: @Repository
│
├─【Servlet 初始化期】（如果是 Web 应用）
│   └─ RequestMappingHandlerMapping:
│       ├─ @Controller/@RestController
│       └─ @RequestMapping/@GetMapping/... 建立 URL 映射
│
└─【请求处理期】（运行时持续）
    ├─ HandlerMethodArgumentResolver:
    │   └─ @PathVariable/@RequestParam/@RequestBody/@ModelAttribute
    ├─ HandlerMethodReturnValueHandler:
    │   └─ @ResponseBody 序列化
    └─ AOP 代理拦截:
        └─ @Transactional/@Cacheable/@Async 织入
```

## 处理机制对比表

| 阶段 | 处理器类型 | 核心技术 | 典型注解 | 处理目标 |
|------|-----------|---------|---------|---------|
| **编译期** | Annotation Processor | AST 修改/代码生成 | `@Data`, `@Builder`, `@Mapper` | 生成/修改源代码 |
| **启动期 BFPP** | BeanFactoryPostProcessor | 反射读取类元数据 | `@ComponentScan`, `@Bean`, `@Import` | 注册 BeanDefinition |
| **启动期 BPP** | BeanPostProcessor | 反射操作实例 | `@Autowired`, `@Value`, `@PostConstruct` | 依赖注入、初始化回调 |
| **框架初始化** | 框架特定扫描器 | 反射读取类/字段/方法 | `@TableName`, `@Entity` | 构建框架元数据缓存 |
| **MVC 初始化** | HandlerMapping | 反射读取方法注解 | `@RequestMapping`, `@Controller` | 建立 URL 到方法的映射 |
| **请求处理** | HandlerAdapter + Resolvers | 反射 + 数据绑定 | `@PathVariable`, `@RequestBody` | 参数解析、返回值处理 |
| **AOP 织入** | BeanPostProcessor (代理) | 动态代理（JDK/CGLIB） | `@Transactional`, `@Aspect` | 创建代理，织入横切逻辑 |

## 关系与索引

- **上级概念**：
  - Bean 生命周期：[BeanLifecycle.md](BeanLifecycle.md)
  - Bean 注册方式：[BeanRegistrationMethods.md](BeanRegistrationMethods.md)
  - Context 刷新流程：[../context/mechanism/ContextRefresh.md](../context/mechanism/ContextRefresh.md)
  
- **相关概念**：
  - 配置类解析：[ConfigurationClassPostProcessor.md](ConfigurationClassPostProcessor.md)
  - BFPP 执行：[BeanFactoryPostProcessorExecution.md](BeanFactoryPostProcessorExecution.md)
  - Spring MVC 参数解析：[../../web/interface/HandlerMethodArgumentResolver.md](../../web/interface/HandlerMethodArgumentResolver.md)

- **多级索引**：
  - `springboot → modules → core → beans → mechanism → AnnotationProcessing`
  - 关联：`flows/启动流程.md` 中各阶段的注解处理细节
  - 关联：`modules/core/context/mechanism/ContextRefresh.md` 的 invokeBeanFactoryPostProcessors 阶段