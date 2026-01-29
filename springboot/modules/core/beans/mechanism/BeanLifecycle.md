---
type: mechanism
tags:
  - springboot/mechanism
  - beans
---

# Bean 生命周期（Bean Lifecycle）

## 一句话

Bean 生命周期描述了单个 Bean 实例从实例化到销毁的阶段化处理链路。

## 严格定义

给定 BeanDefinition $d$ 与 BeanFactory $F$，Bean 生命周期定义为以下有序阶段的执行序列：

$$
\text{Lifecycle}(d, F) = \text{Instantiation} \to \text{Population} \to \text{Aware} \to \text{BPP}_{\text{before}} \to \text{Initialization} \to \text{BPP}_{\text{after}} \to \text{Ready} \to \text{Destruction}
$$

其中：
- $\text{Instantiation}$：根据 $d$ 的实例化策略构造原始对象
- $\text{Population}$：依赖注入与属性填充
- $\text{Aware}$：容器感知回调
- $\text{BPP}_{\text{before}}$：`BeanPostProcessor.postProcessBeforeInitialization`
- $\text{Initialization}$：初始化回调
- $\text{BPP}_{\text{after}}$：`BeanPostProcessor.postProcessAfterInitialization`
- $\text{Ready}$：实例进入可用态
- $\text{Destruction}$：容器关闭时的销毁回调

## 接口：数据 + 约束

### 输入
- `BeanDefinition`：元数据（class、scope、构造参数、属性值、init-method、destroy-method 等）
- `BeanFactory`：提供依赖解析、BeanPostProcessor 注册表、Scope 注册表
- `BeanPostProcessor` 集合：已注册的实例级处理器

### 输出
- 完成生命周期各阶段后的 Bean 实例（可能被代理包装）

### 约束
- Aware 回调的执行顺序：`BeanNameAware` → `BeanClassLoaderAware` → `BeanFactoryAware`（由 `ApplicationContextAwareProcessor` 额外添加 `EnvironmentAware`、`ApplicationContextAware` 等）
- 初始化回调执行顺序：`@PostConstruct` → `InitializingBean.afterPropertiesSet()` → 自定义 `init-method`
- 销毁回调执行顺序：`@PreDestroy` → `DisposableBean.destroy()` → 自定义 `destroy-method`
- `BeanPostProcessor.postProcessAfterInitialization` 可返回代理对象替换原始实例

## 阶段详述

### 1. 实例化（Instantiation）

根据 BeanDefinition 选择实例化策略：

| 策略 | 条件 | 说明 |
|------|------|------|
| 构造函数 | 默认 | 选择匹配的构造函数（可结合 `@Autowired` 构造器注入） |
| `Supplier` | `BeanDefinition.instanceSupplier != null` | 调用 `Supplier.get()` |
| 工厂方法 | `BeanDefinition.factoryMethodName != null` | 调用静态或实例工厂方法 |
| `FactoryBean` | Bean 类型实现 `FactoryBean` 接口 | 调用 `FactoryBean.getObject()` 返回目标实例 |

相关类：`AbstractAutowireCapableBeanFactory.createBeanInstance(...)`

### 2. 属性填充（Population / Dependency Injection）

将依赖注入到实例中：

| 注入方式 | 触发条件 |
|----------|----------|
| `@Autowired` / `@Value` | 字段或 setter 标注 |
| `@Resource` / `@Inject` | JSR-250 / JSR-330 标注 |
| XML `<property>` | BeanDefinition 中的 PropertyValues |
| 构造器注入 | 构造函数参数（已在实例化阶段完成） |

相关类：`AbstractAutowireCapableBeanFactory.populateBean(...)`

### 3. Aware 回调

容器向 Bean 注入基础设施引用：

| 接口 | 注入内容 |
|------|----------|
| `BeanNameAware` | Bean 名称 |
| `BeanClassLoaderAware` | ClassLoader |
| `BeanFactoryAware` | BeanFactory 引用 |
| `EnvironmentAware` | Environment（需 `ApplicationContextAwareProcessor`） |
| `ApplicationContextAware` | ApplicationContext（需 `ApplicationContextAwareProcessor`） |

相关类：`AbstractAutowireCapableBeanFactory.invokeAwareMethods(...)`

### 4. BeanPostProcessor.postProcessBeforeInitialization

遍历已注册的 `BeanPostProcessor`，依次调用 `postProcessBeforeInitialization(bean, beanName)`。

典型处理器：
- `CommonAnnotationBeanPostProcessor`：处理 `@PostConstruct`
- `ApplicationContextAwareProcessor`：处理 `ApplicationContextAware` 等

### 5. 初始化回调（Initialization）

按以下顺序执行：

1. `@PostConstruct` 标注方法（由 `CommonAnnotationBeanPostProcessor` 在 BPP.before 阶段触发）
2. `InitializingBean.afterPropertiesSet()`
3. 自定义 `init-method`（BeanDefinition 中配置）

相关类：`AbstractAutowireCapableBeanFactory.invokeInitMethods(...)`

### 6. BeanPostProcessor.postProcessAfterInitialization

遍历已注册的 `BeanPostProcessor`，依次调用 `postProcessAfterInitialization(bean, beanName)`。

典型处理器：
- `AbstractAutoProxyCreator`：AOP 代理生成（`@Transactional`、`@Async` 等）

此阶段可返回代理对象替换原始实例。

### 7. Ready

实例进入可用态：
- singleton：缓存至单例注册表
- prototype：直接返回调用方
- 其他 scope：存入对应 Scope 容器

### 8. 销毁（Destruction）

容器关闭时（`context.close()`），对 singleton 执行销毁回调：

1. `@PreDestroy` 标注方法
2. `DisposableBean.destroy()`
3. 自定义 `destroy-method`

prototype 不由容器管理销毁。

## 常用构造/操作

| 操作 | 接口/类 |
|------|---------|
| 实例化 | `AbstractAutowireCapableBeanFactory.createBeanInstance(...)` |
| 属性填充 | `AbstractAutowireCapableBeanFactory.populateBean(...)` |
| Aware 回调 | `AbstractAutowireCapableBeanFactory.invokeAwareMethods(...)` |
| 初始化 | `AbstractAutowireCapableBeanFactory.invokeInitMethods(...)` |
| BPP 调用 | `AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsBeforeInitialization(...)` / `...AfterInitialization(...)` |
| 销毁 | `DisposableBeanAdapter.destroy()` |

## 关系：上级/下级/等价/特例/推广

- 上级：[Bean 注册与创建流程](../../../flows/Bean注册与创建流程.md)（Phase 6 触发生命周期）
- 上级：[Context refresh](../../context/mechanism/ContextRefresh.md)（`finishBeanFactoryInitialization` 阶段创建 singleton）
- 下级：[BeanInstantiation](BeanInstantiation.md)（Instantiation 阶段的构造策略详情）
- 相关：[BeanPostProcessor](../interface/BeanPostProcessor.md)（实例级拦截）
- 相关：[Scope 注册与解析](ScopeResolution.md)（决定实例存取策略）
- 相关：[FactoryBean](../interface/FactoryBean.md)（特殊实例化策略）

## 把新概念挂回框架（多级索引轨迹）

springboot → modules → core → beans → mechanism → BeanLifecycle
