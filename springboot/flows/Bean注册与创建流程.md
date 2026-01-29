---
type: flow
tags:
  - springboot/flow
  - beans
---

# Bean 注册与创建流程（从 BeanDefinition 到可用实例）

## 一句话

Bean 注册与创建流程描述了 `ApplicationContext.refresh()` 期间 BeanDefinition 的发现/注册、容器设施装配，以及运行时按 Scope 解析并通过 Bean 生命周期创建可用实例的阶段化链路。

## 严格定义

给定配置来源集合 $S$（配置类、扫描结果、导入结果、XML 等）与环境 $E$（profiles 与属性源），在一次容器刷新过程中：

1. **准备阶段**：构造 BeanDefinition 集合 $D$ 并写入注册表 $R$，完成 BeanFactory 后处理与设施装配
2. **创建阶段**：对 $d \in D$，按 [Bean 生命周期](modules/core/beans/mechanism/BeanLifecycle.md) 执行实例化 → 属性填充 → 初始化
3. **存取阶段**：按 $d.scopeName$ 解析实例来源（singleton 缓存 / prototype 新建 / 自定义 Scope 委托）

即：准备阶段解决"有哪些 Bean"，创建阶段解决"如何创建 Bean"，存取阶段解决"从哪获取 Bean"。

## 接口：数据 + 约束

### 输入
- 配置来源（sources / 扫描 / 导入 / 自动配置导入等产生的 BeanDefinition）
- `Environment`（profiles 与属性源）
- 后处理器集合（`BeanFactoryPostProcessor`、`BeanPostProcessor` 等）
- Scope 注册设施（singleton / prototype 内置，request / session 等需注册）

### 输出
- `BeanDefinitionRegistry` 中已注册的 BeanDefinition 集合
- 处于可用态的 `BeanFactory`（可解析依赖并按 scope 返回实例）

### 约束
- BeanFactoryPostProcessor 需在单例实例化之前执行（保证 BeanDefinition 变换生效）
- 非 singleton/prototype 的 scope 必须在 BeanFactory 中注册同名 Scope
- Web 相关 scope 的可用性依赖请求上下文的绑定

## 阶段（Phase）

### Phase 1：BeanDefinition 发现与注册

| 子阶段 | 说明 |
|--------|------|
| 配置解析 | `@Configuration` / `@ComponentScan` / `@Import` / XML → BeanDefinition |
| 条件过滤 | `@ConditionalOnClass` / `@ConditionalOnProperty` 等条件判断 |
| 注册写入 | `BeanDefinitionRegistry.registerBeanDefinition(...)` |

相关条目：
- [BeanDefinition](modules/core/beans/interface/BeanDefinition.md)
- [BeanDefinitionRegistry](modules/core/beans/interface/BeanDefinitionRegistry.md)

### Phase 2：BeanFactory 准备

| 子阶段 | 说明 |
|--------|------|
| 工厂创建 | `DefaultListableBeanFactory` 作为默认实现 |
| 基础设施装配 | ClassLoader、EL 表达式解析器、嵌套等级解析器等 |
| 可枚举能力 | `ConfigurableListableBeanFactory` 接口组合 |

相关条目：
- [BeanFactory](modules/core/beans/interface/BeanFactory.md)
- [ConfigurableListableBeanFactory](modules/core/beans/interface/ConfigurableListableBeanFactory.md)
- [DefaultListableBeanFactory](modules/core/beans/class/DefaultListableBeanFactory.md)

### Phase 3：BeanFactoryPostProcessor 执行

| 类型 | 说明 |
|------|------|
| `BeanDefinitionRegistryPostProcessor` | 可新增/删除 BeanDefinition（优先级高于普通 BFPP） |
| `BeanFactoryPostProcessor` | 可修改 BeanDefinition 属性值、替换占位符等 |

执行顺序：先按优先级执行 BDRPP，再执行普通 BFPP。

相关条目：
- [BeanFactoryPostProcessorExecution](modules/core/beans/mechanism/BeanFactoryPostProcessorExecution.md)

### Phase 4：BeanPostProcessor 注册

| 类型 | 说明 |
|------|------|
| 实例级拦截 | `BeanPostProcessor.postProcessBeforeInitialization` / `postProcessAfterInitialization` |
| 典型处理器 | `AutowiredAnnotationBeanPostProcessor`、`CommonAnnotationBeanPostProcessor`、`AbstractAutoProxyCreator` |

### Phase 5：Scope 设施注册

| Scope | 说明 |
|-------|------|
| `singleton` | 默认，内置实现（容器级单例缓存） |
| `prototype` | 默认，内置实现（每次新建） |
| `request` | Web 场景，请求级别缓存 |
| `session` | Web 场景，会话级别缓存 |
| 自定义 | 通过 `ConfigurableBeanFactory.registerScope(name, Scope)` 注册 |

相关条目：
- [Scope](modules/core/beans/interface/Scope.md)
- [ScopeResolution](modules/core/beans/mechanism/ScopeResolution.md)
- [WebScopes](../web/mechanism/WebScopes.md)

### Phase 6：单例实例化（refresh 期间）

| 子阶段 | 说明 |
|--------|------|
| 遍历 BeanDefinition | `beanFactory.preInstantiateSingletons()` |
| 排除 lazy | 跳过 `BeanDefinition.isLazyInit()` 为 true 的 Bean |
| 依赖排序 | 按 `dependsOn` 与 `@Order` 确定创建顺序 |

### Phase 7：运行时按 Scope 获取（getBean 调用）

| Scope | 行为 |
|-------|------|
| `singleton` | 从 `singletonObjects` 缓存获取，未命中则触发生命周期创建 |
| `prototype` | 每次触发完整生命周期创建 |
| `request` | 从 `RequestContextHolder` 对应请求的缓存获取 |
| `session` | 从 `HttpSession` 属性获取 |
| 自定义 | 委托给 `Scope.get(name, ObjectFactory)` |

## 完整创建链路（Bean 生命周期）

当需要创建新实例时（singleton 首次获取、prototype 每次获取、Scope 缓存未命中），执行以下阶段：

| 阶段 | 说明 | 关键组件 |
|------|------|----------|
| 实例化 | 选择构造策略创建原始对象 | [BeanInstantiation](modules/core/beans/mechanism/BeanInstantiation.md) |
| 属性填充 | 依赖注入（@Autowired / @Value / XML） | `AutowiredAnnotationBeanPostProcessor` |
| Aware 回调 | 注入容器感知接口 | `ApplicationContextAwareProcessor` |
| BPP.before | 初始化前拦截 | 全部 BeanPostProcessor |
| 初始化 | @PostConstruct / InitializingBean / init-method | `invokeInitMethods` |
| BPP.after | 初始化后拦截（可返回代理） | 全部 BeanPostProcessor（如 AOP） |
| 缓存/返回 | 根据 scope 策略返回实例 | `ObjectFactory` / `Scope` |

详见：[Bean 生命周期](modules/core/beans/mechanism/BeanLifecycle.md)

## FactoryBean 场景

当 Bean 类型实现 `FactoryBean<T>` 接口时：

| 调用方式 | 返回值 |
|----------|--------|
| `getBean("beanName")` | `FactoryBean.getObject()` 的结果（类型为 `T`） |
| `getBean("&beanName")` | FactoryBean 实例本身 |

相关条目：[FactoryBean](modules/core/beans/interface/FactoryBean.md)

## 关系：上级/下级/等价/特例/推广

- 上级：[Context refresh](modules/core/context/mechanism/ContextRefresh.md)（`refresh()` 模板流程的子流程）
- 下级：[Bean 生命周期](modules/core/beans/mechanism/BeanLifecycle.md)（创建阶段的具体阶段序列）
- 下级：[BeanInstantiation](modules/core/beans/mechanism/BeanInstantiation.md)（实例化阶段的构造策略）
- 下级：[ScopeResolution](modules/core/beans/mechanism/ScopeResolution.md)（存取阶段的 scope 解析机制）
- 相关：外部化配置（影响 BeanDefinition 的产生与条件过滤）
- 特例：Web 应用包含 request/session scope（见 [WebScopes](../web/mechanism/WebScopes.md)）

## 把新概念挂回框架（多级索引轨迹）

springboot → flows → Bean 注册与创建流程 →（BeanDefinition / BeanFactory / PostProcessor / Scope / BeanLifecycle / BeanInstantiation）
