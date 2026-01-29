# ContextRefresh（ApplicationContext.refresh 模板流程）

> **类型**：机制（Mechanism）

## 一句话
`ApplicationContext.refresh()` 将上下文从“已配置（configuration phase）”推进到“可用（runtime phase）”，并以固定阶段顺序完成容器内部组件初始化、监听器注册、单例 Bean 创建与就绪事件发布。

## 严格定义
在 Spring Framework 中，`AbstractApplicationContext.refresh()` 是容器刷新模板方法：给定一个已完成基础准备的 `ConfigurableApplicationContext`（Environment 已就位、BeanDefinition 来源已加载、各类后处理器/监听器已可发现），`refresh()` 以阶段化流程执行容器初始化与 Bean 创建，使上下文进入可对外提供 `getBean/publishEvent` 等能力的活动状态。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `BeanFactory`（BeanDefinition 注册表与实例化能力）
  - `Environment`（属性源与 profiles）
  - `ApplicationEventMulticaster`（事件分发组件）
  - 监听器来源（Context 的 listener 对象集合与 listener beanName 集合）
- 输入：
  - `BeanDefinition` 集合（由 sources/扫描/导入等产生）
  - 后处理器与监听器（如 `BeanFactoryPostProcessor`、`BeanPostProcessor`、`ApplicationListener`）
- 输出：
  - 单例 Bean 实例化完成（在 `finishBeanFactoryInitialization` 阶段）
  - 容器事件：`ContextRefreshedEvent`（在 `finishRefresh` 附近发布）
- 约束：
  - `refresh()` 是阶段化模板方法：阶段边界与内部 hook 方法名由 Framework 定义；具体行为取决于 `ApplicationContext` 实现与所注册的后处理器/监听器。
  - 事件多播器与监听器注册通常在 refresh 早期完成，以保证 refresh 过程中产生的事件可被分发（见 [../../events/mechanism/ApplicationListenerRegistration.md](../../events/mechanism/ApplicationListenerRegistration.md)）。

## 常用构造/操作（仅列出接口与符号）
### 阶段化顺序（Framework 视角）
- `prepareRefresh()`
- `obtainFreshBeanFactory()`
- `prepareBeanFactory(beanFactory)`
- `postProcessBeanFactory(beanFactory)`（子类 hook）
- `invokeBeanFactoryPostProcessors(beanFactory)`
- `registerBeanPostProcessors(beanFactory)`
- `initMessageSource()`
- `initApplicationEventMulticaster()`
- `onRefresh()`（子类 hook）
- `registerListeners()`
- `finishBeanFactoryInitialization(beanFactory)`
- `finishRefresh()`

### Boot 触发位置（Boot 2.3.x）
`SpringApplication.refreshContext(context)` → `SpringApplication.refresh(context)` → `context.refresh()`。

## 关系：上级/下级/等价/特例/推广
- 上级：
  - 显式生命周期与状态机：见 [../../../patterns/concept/LifecycleStateMachine.md](../../../patterns/concept/LifecycleStateMachine.md)
  - 两阶段初始化：见 [../../../patterns/pattern/TwoPhaseInitialization.md](../../../patterns/pattern/TwoPhaseInitialization.md)
  - 模板方法：见 [../../../patterns/pattern/TemplateMethod.md](../../../patterns/pattern/TemplateMethod.md)
  - 装配与运行分离：见 [../../../patterns/principle/SeparationOfWiringAndRunning.md](../../../patterns/principle/SeparationOfWiringAndRunning.md)
- 相关：
  - `ApplicationContext`（见 [../interface/ApplicationContext.md](../interface/ApplicationContext.md)）
  - Bean 注册与创建流程（见 [springboot/flows/Bean注册与创建流程.md](../../../../flows/Bean注册与创建流程.md)）
  - 监听器注册与装配（见 [../../events/mechanism/ApplicationListenerRegistration.md](../../events/mechanism/ApplicationListenerRegistration.md)）
  - 生命周期事件（见 [../../events/mechanism/ApplicationLifecycleEvents.md](../../events/mechanism/ApplicationLifecycleEvents.md)）
  - 启动流程（见 [springboot/flows/启动流程.md](../../../../flows/启动流程.md)）

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → context → mechanism → ContextRefresh → flows/启动流程。
