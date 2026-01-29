---
type: flow
tags:
  - springboot/flow
---

# Bean 注册与创建流程（从 BeanDefinition 到 Bean 实例与 Scope）

## 一句话
Bean 注册与创建流程描述了 `ApplicationContext.refresh()` 期间 BeanDefinition 的发现/注册、BeanFactory 的后处理与设施装配，以及运行时按 Scope 解析与获取 Bean 实例的阶段化链路。

## 严格定义
给定配置来源集合 $S$（配置类、扫描结果、导入结果、XML 等）与环境 $E$（profiles 与属性源），在一次容器刷新过程中构造出 BeanDefinition 集合 $D$ 并写入注册表 $R$，随后通过后处理器集合 $\Pi$ 对 $D$ 与 BeanFactory 进行变换，最终得到可对外提供 `getBean(...)` 的容器状态。

对任一 BeanDefinition $d \in D$，其运行时实例的获取由两部分共同决定：
1. **存取策略**：由 $d.scope$ 与 BeanFactory 中同名的 Scope 注册项决定（singleton 缓存 / prototype 新建 / 自定义 Scope 容器）
2. **创建链路**：由 Bean 生命周期定义的阶段化处理序列决定（实例化 → 属性填充 → Aware → BPP.before → 初始化 → BPP.after）

即：scope 决定"从哪取/存到哪"，生命周期决定"如何创建与装配"。

## 接口：数据 + 约束
- 输入：
  - 配置来源（sources/扫描/导入/自动配置导入等产生的 BeanDefinition）
  - `Environment`（profiles 与属性源）
  - 后处理器集合（`BeanFactoryPostProcessor`、`BeanPostProcessor` 等）
  - 作用域设施（`Scope` 的注册，Web 场景下包括 `request`/`session`）
- 输出：
  - `BeanDefinitionRegistry` 中已注册的 BeanDefinition 集合
  - 处于可用态的 `BeanFactory`（可解析依赖并按 scope 返回实例）
- 约束：
  - BeanFactory 的后处理（BeanFactoryPostProcessor）需发生在单例实例化之前，以保证对 BeanDefinition 的变换在创建前生效。
  - 非 singleton/prototype 的 scope 名称必须在 BeanFactory 中注册同名 `Scope`；否则在实例解析时产生未注册错误（见 [modules/core/beans/mechanism/ScopeResolution.md](../modules/core/beans/mechanism/ScopeResolution.md)）。
  - Web 相关 scope 的可用性依赖请求上下文的绑定（见 [modules/web/mechanism/WebScopes.md](../modules/web/mechanism/WebScopes.md)）。

## 阶段（Phase）

### Phase 1：BeanDefinition 发现与注册（Definition → Registry）
- 配置来源被解析为 BeanDefinition（来源可能包含自动配置导入与条件过滤的结果）。
- BeanDefinition 写入注册表：
  - `BeanDefinition`：见 [modules/core/beans/interface/BeanDefinition.md](../modules/core/beans/interface/BeanDefinition.md)
  - `BeanDefinitionRegistry`：见 [modules/core/beans/interface/BeanDefinitionRegistry.md](../modules/core/beans/interface/BeanDefinitionRegistry.md)

### Phase 2：获得并准备 BeanFactory（Registry → Factory）
- Context 获取/创建内部 BeanFactory（常见为 `DefaultListableBeanFactory`），并将基础设施（classloader、resolvable dependencies 等）写入。
  - `BeanFactory`：见 [modules/core/beans/interface/BeanFactory.md](../modules/core/beans/interface/BeanFactory.md)
  - `ConfigurableListableBeanFactory`：见 [modules/core/beans/interface/ConfigurableListableBeanFactory.md](../modules/core/beans/interface/ConfigurableListableBeanFactory.md)
  - `DefaultListableBeanFactory`：见 [modules/core/beans/class/DefaultListableBeanFactory.md](../modules/core/beans/class/DefaultListableBeanFactory.md)

### Phase 3：BeanFactoryPostProcessor（Definition 级变换）
- 执行 `BeanFactoryPostProcessor` / `BeanDefinitionRegistryPostProcessor`：允许对 BeanDefinition 进行增删改与派生注册。
- 该阶段通常发生在单例创建之前。
- 执行顺序与断环窗口：见 [modules/core/beans/mechanism/BeanFactoryPostProcessorExecution.md](../modules/core/beans/mechanism/BeanFactoryPostProcessorExecution.md)。

### Phase 4：BeanPostProcessor（实例级拦截链装配）
- 注册 `BeanPostProcessor`：使后续的 Bean 创建过程可在初始化前后进行实例级变换（例如代理生成、注入增强等）。

### Phase 5：Scope 注册与可用性边界（Scope 设施就位）
- BeanFactory 内部维护 scope 注册表（`scopeName -> Scope`），并提供 `registerScope(name, scope)` 入口：
  - `ConfigurableBeanFactory`：见 [modules/core/beans/interface/ConfigurableBeanFactory.md](../modules/core/beans/interface/ConfigurableBeanFactory.md)
  - `Scope`：见 [modules/core/beans/interface/Scope.md](../modules/core/beans/interface/Scope.md)
  - scope 解析机制：见 [modules/core/beans/mechanism/ScopeResolution.md](../modules/core/beans/mechanism/ScopeResolution.md)
- Web 场景下，WebApplicationContext 的实现类可在其 `postProcessBeanFactory(...)` hook 中注册 `request`/`session` 等 scope（见 [modules/web/mechanism/WebScopes.md](../modules/web/mechanism/WebScopes.md)）。

### Phase 6：单例实例化与运行态按 scope 取用（Factory → Instances）

#### 6.1 Scope 解析（存取策略）
- refresh 期间：创建非 lazy 的 singleton，并完成依赖注入与初始化回调。
- 运行时：按 BeanDefinition.scopeName 解析实例来源：
  - `singleton`：来自容器全局单例缓存
  - `prototype`：每次请求创建新实例
  - 其他 scope（如 `request`/`session`）：委托给同名 `Scope.get(...)` 从对应上下文取/存

#### 6.2 Bean 生命周期（创建链路）
当需要创建新实例时（singleton 首次获取、prototype 每次获取、Scope 缓存未命中），执行以下阶段化链路：

| 阶段 | 说明 |
|------|------|
| 实例化（Instantiation） | 根据 BeanDefinition 选择构造函数 / Supplier / 工厂方法 / FactoryBean |
| 属性填充（Population） | `@Autowired` / `@Value` / setter 注入 / XML 属性 |
| Aware 回调 | `BeanNameAware`、`BeanFactoryAware`、`ApplicationContextAware` 等 |
| BPP.postProcessBeforeInitialization | 遍历 BeanPostProcessor，初始化前拦截 |
| 初始化回调 | `@PostConstruct` → `InitializingBean.afterPropertiesSet()` → `init-method` |
| BPP.postProcessAfterInitialization | 遍历 BeanPostProcessor，初始化后拦截（可返回代理） |

详见：[Bean 生命周期](../modules/core/beans/mechanism/BeanLifecycle.md)

#### 6.3 FactoryBean 场景
当 BeanDefinition 对应的类实现 `FactoryBean<T>` 接口时：
- `getBean("beanName")` 返回 `FactoryBean.getObject()` 的结果（类型为 `T`）
- `getBean("&beanName")` 返回 FactoryBean 实例本身

## 关系：上级/下级/等价/特例/推广
- 上级：`ApplicationContext.refresh()` 的阶段化模板流程（见 [modules/core/context/mechanism/ContextRefresh.md](../modules/core/context/mechanism/ContextRefresh.md)）。
- 下级：[Bean 生命周期](../modules/core/beans/mechanism/BeanLifecycle.md)（Phase 6 触发的实例创建链路）。
- 相关：外部化配置与条件装配（影响 BeanDefinition 的产生与过滤）。
- 特例：Web 应用包含 request/session scope 的注册与请求上下文绑定（见 [modules/web/mechanism/WebScopes.md](../modules/web/mechanism/WebScopes.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → flows → Bean 注册与创建流程 →（BeanDefinition / BeanFactory / PostProcessor / Scope / BeanLifecycle）。
