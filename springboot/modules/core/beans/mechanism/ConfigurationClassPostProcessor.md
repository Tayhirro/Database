---
type: mechanism
tags:
  - springboot/core
  - beans
  - annotation
---

# ConfigurationClassPostProcessor（配置类解析与派生注册）

## 一句话
`ConfigurationClassPostProcessor` 是 Spring Framework 在 `refresh()` 早期执行的 `BeanDefinitionRegistryPostProcessor`：解析配置类（含复合注解/`@Import`/`@ComponentScan`/`@Bean` 等）并向注册表派生注册更多 `BeanDefinition`。

## 严格定义
在 `AbstractApplicationContext.refresh()` 的 `invokeBeanFactoryPostProcessors(beanFactory)` 阶段，`ConfigurationClassPostProcessor` 作为 BDRPP/BFPP 被实例化并回调；它以 `BeanDefinitionRegistry` 为输入，识别候选配置类（通常是 `@Configuration` 语义或等价的“配置类候选”），并通过配置类解析器对其注解元数据进行解析，进而触发：
- `@ComponentScan`：对指定 base package 扫描 stereotype 组件并注册其 `BeanDefinition`；
- `@Import`：导入配置类、`ImportSelector`、`ImportBeanDefinitionRegistrar` 等扩展点并派生注册；
- `@Bean`：为配置类中的 `@Bean` 方法派生注册对应的 `BeanDefinition`（factoryBeanName/factoryMethodName 语义）。

在 Spring Boot 中，`prepareContext()` 将 primary source（常为 `@SpringBootApplication` 标注的主类）注册为“配置类候选”的 `BeanDefinition`；随后 `ConfigurationClassPostProcessor` 在 `refresh()` 内解析该 `BeanDefinition` 的注解元数据，间接触发组件扫描与自动配置候选的导入链路。

## 接口：数据 + 约束
- 数据（输入）：
  - `BeanDefinitionRegistry`（已有的 BeanDefinition 集合，含主类对应的配置类 BeanDefinition）
  - 配置类注解元数据（由 `BeanDefinition` 携带/可解析）
- 输出：
  - 对注册表的变换：新增/修改/派生注册 `BeanDefinition`
- 约束：
  - 运行时点：发生于 `invokeBeanFactoryPostProcessors(beanFactory)`，早于 `registerBeanPostProcessors(beanFactory)` 与普通单例的大规模实例化；
  - 解析与派生注册的结果取决于 classpath、属性（例如 profiles/条件注解）、以及导入选择器的实现。

## 常用构造/操作（仅列出接口与符号）
- 执行入口（refresh 内）：`invokeBeanFactoryPostProcessors(beanFactory)`
- 解析主题：配置类候选（`@Configuration` 语义、或包含 `@ComponentScan/@Import/@Bean` 等的候选类）
- 触发产物：
  - `@ComponentScan` → `ClassPathBeanDefinitionScanner.scan()`
  - `@Bean` → 派生 `BeanDefinition`（`factoryBeanName`/`factoryMethodName`）
  - `@EnableAutoConfiguration`（Boot）→ 自动配置候选导入（机制见 [../../../config/mechanism/AutoConfiguration.md](../../../config/mechanism/AutoConfiguration.md)）

## 关系：上级/下级/等价/特例/推广
- 上级：
  - `ApplicationContext.refresh()` 模板流程（见 [../../context/mechanism/ContextRefresh.md](../../context/mechanism/ContextRefresh.md)）
  - BFPP/BDRPP 执行与断环（见 [BeanFactoryPostProcessorExecution.md](BeanFactoryPostProcessorExecution.md)）
- 下游：
  - Bean 注册方式（见 [BeanRegistrationMethods.md](BeanRegistrationMethods.md)）
- 相关：
  - 自动配置（见 [../../../config/mechanism/AutoConfiguration.md](../../../config/mechanism/AutoConfiguration.md)）
  - 扩展点发现（见 [../../../extension/mechanism/SpringFactoriesLoader.md](../../../extension/mechanism/SpringFactoriesLoader.md)）

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → beans → mechanism → ConfigurationClassPostProcessor。

