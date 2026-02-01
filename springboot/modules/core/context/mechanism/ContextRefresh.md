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

| 阶段                                             | 功能                                                              |
| ---------------------------------------------- | --------------------------------------------------------------- |
| `prepareRefresh()`                             | 初始化上下文状态，准备 Environment 与早期事件存储                                 |
| `obtainFreshBeanFactory()`                     | 获取或创建内部 BeanFactory，加载 BeanDefinition                           |
| `prepareBeanFactory(beanFactory)`              | 配置 BeanFactory 标准特性（类加载器、SpEL 解析器等）                             |
| `postProcessBeanFactory(beanFactory)`（子类 hook） | 子类自定义 BeanFactory 后处理（如注册 Web 相关 Scope）                         |
| `invokeBeanFactoryPostProcessors(beanFactory)` | 执行 BFPP/BDRPP，修改 BeanDefinition                                 |
| `registerBeanPostProcessors(beanFactory)`      | 注册 BPP，准备实例创建拦截链                                                |
| `initMessageSource()`                          | 初始化国际化消息源（读取 messages*.properties）                              |
| `initApplicationEventMulticaster()`            | 初始化事件广播器，建立事件分发机制                                               |
| `onRefresh()`（子类 hook）                         | 子类扩展点；Web 场景中常用于 `createWebServer()` 启动内嵌 Web 服务器（Tomcat/Jetty） |
| `registerListeners()`                          | 将监听器注册到事件广播器                                                    |
| `finishBeanFactoryInitialization(beanFactory)` | 预实例化非 lazy Bean（Service、Controller 等）                           |
| `finishRefresh()`                              | 发布 ContextRefreshedEvent，标记容器就绪                                 |

### Boot 触发位置（Boot 2.3.x）
`SpringApplication.refreshContext(context)` → `SpringApplication.refresh(context)` → `context.refresh()`。

## invokeBeanFactoryPostProcessors 的典型产物（注解配置上下文）
在注解驱动的 `ApplicationContext` 中（例如 `AnnotationConfig...ApplicationContext` 及其 Web 变体），`invokeBeanFactoryPostProcessors(beanFactory)` 的典型效果之一是触发配置类解析与派生注册：
- `ConfigurationClassPostProcessor`（BDRPP/BFPP）解析配置类候选并派生注册更多 `BeanDefinition`（见 [../../beans/mechanism/ConfigurationClassPostProcessor.md](../../beans/mechanism/ConfigurationClassPostProcessor.md)）。
- 该解析过程通常包含：
  - `@ComponentScan`：扫描 stereotype 组件并注册其 `BeanDefinition`（扫描器见 [../../beans/mechanism/BeanRegistrationMethods.md](../../beans/mechanism/BeanRegistrationMethods.md)）。
  - `@Bean`：为 `@Bean` 方法派生注册 `BeanDefinition`（factory method 语义见 [../../beans/mechanism/BeanRegistrationMethods.md](../../beans/mechanism/BeanRegistrationMethods.md)）。
  - `@EnableAutoConfiguration`（Boot）：导入自动配置候选（见 [../../../config/mechanism/AutoConfiguration.md](../../../config/mechanism/AutoConfiguration.md)）。

## 阶段顺序原理（依赖方向与 Hook 时机）

阶段顺序由**设施依赖方向**决定：下游设施依赖上游设施，故上游必先初始化。

### 依赖链与 Hook 位置

阶段按**依赖关系**顺序执行：下游依赖上游，故上游先初始化。

```
阶段 1-3：基础层（必须最先）
┌─────────────────┐    ┌──────────────────┐    ┌──────────────────────┐
│ prepareRefresh()│───▶│obtainFreshBean   │───▶│ prepareBeanFactory() │
│ (Environment)   │    │Factory           │    │ (配置 BeanFactory)   │
└─────────────────┘    └──────────────────┘    └──────────┬───────────┘
                                                          │
                    ┌─────────────────────────────────────┘
                    ▼
            ┌──────────────────────┐
            │[Hook 1]              │
            │postProcessBeanFactory│───▶ 子类扩展点：可修改 BeanFactory
            │(BeanFactory 就绪)    │      或注册自定义 Scope
            └──────────────────────┘
                    │
                    ▼
阶段 4-5：后处理层（依赖 BeanFactory）
┌──────────────────────────┐    ┌──────────────────────────┐
│invokeBeanFactoryPostP... │───▶│registerBeanPostP...      │
│(BFPP 修改 BeanDefinition)│    │(BPP 注册拦截链)          │
└──────────────────────────┘    └──────────────────────────┘
                    │
                    ▼
阶段 6-7：设施层（被后续使用）
┌──────────────────┐    ┌──────────────────────────┐
│ initMessageSource│───▶│initApplicationEvent      │
│ (国际化消息源)   │    │Multicaster (事件广播器)  │
└──────────────────┘    └──────────────┬───────────┘
                                         │
                    ┌────────────────────┘
                    ▼
            ┌──────────────────────┐
            │[Hook 2]              │
            │onRefresh()           │───▶ 子类扩展点：基础设施全部就绪
            │(EventMulticaster 就绪)│      但业务 Bean 未创建；可启动
            └──────────────────────┘      Web 服务器等自定义设施
                    │
                    ▼
阶段 8-10：运行时层（依赖全部设施）
┌────────────────────────┐    ┌──────────────────────────────────┐
│ registerListeners()    │───▶│ finishBeanFactoryInitialization()│
│ (监听器注册到广播器)   │    │ (预实例化非 lazy Bean)           │
└────────────────────────┘    └──────────────────────────────────┘
```

**箭头含义**：`───▶` 表示执行顺序（从左到右，从上到下）；`│` 和 `▶` 表示阶段调用关系。

### Hook 时机约束

| Hook | 前置条件 | 后置限制 | 典型用途 |
|------|----------|----------|----------|
| `postProcessBeanFactory()` | BeanFactory 已创建，标准特性已配置 | BFPP 即将执行，BeanDefinition 将被变换 | 注册 Web Scope、修改 BeanFactory 默认行为 |
| `onRefresh()` | MessageSource、EventMulticaster 已就绪 | 业务 Bean 即将实例化，监听器待注册 | 初始化“特定场景基础设施”（WebServer、ReactiveServer、自定义运行时设施） |

## 精确定位：`refresh()` 的整体结构（不是“只创建 WebServer”）

把 `refresh()` 看成“构建容器运行时”的过程更准确：它不是做某一个具体设施（例如 WebServer），而是在**统一的启动模板**里，按依赖顺序搭建整套运行时能力。

`AbstractApplicationContext.refresh()` 是典型的**模板方法**：
- 主流程负责“通用框架设施”的搭建（BeanFactory、后处理器链、事件系统等）。
- 两个关键 hook（`postProcessBeanFactory` / `onRefresh`）给子类把“场景化设施”挂上来。

### 以“构建运行时”的视角重述 refresh 分层
- **元数据层（定义阶段）**：`obtainFreshBeanFactory()` 加载/刷新 `BeanDefinition`，相当于把“要装配的对象模型”准备好。
- **变换层（装配阶段）**：`invokeBeanFactoryPostProcessors()` 对 `BeanDefinition` 做派生与改写（注解解析、自动配置导入等）。
- **拦截层（实例化前准备）**：`registerBeanPostProcessors()` 把对象创建/初始化的拦截链装上（后续每个 Bean 创建都会经过它）。
- **基础设施层（通用设施）**：`initMessageSource()`、`initApplicationEventMulticaster()` 等把“容器级能力”就位。
- **场景设施层（hook 扩展）**：`postProcessBeanFactory()` / `onRefresh()` 由子类按场景补齐设施（例如 Web 环境的 `ServletContext`、WebServer；或你自定义的运行时设施）。
- **运行时层（实例化与就绪）**：`registerListeners()` → `finishBeanFactoryInitialization()` → `finishRefresh()`，完成监听器就位、单例预实例化、发布就绪事件。

### 类比（为什么你会觉得像“构建数据库”）
- `BeanDefinition` 更像 schema/元数据：先把“结构”定义清楚，后面才谈实例化与运行。
- BFPP/BDRPP 与 BPP 更像编译/拦截机制：前者改写定义，后者围绕实例生命周期织入拦截逻辑。
- `onRefresh()` 更像“补齐运行时设施”：在主流程给定的时机，把某个场景所需的外部设施（WebServer、资源、上下文对象）接到容器里。

### Hook 的定位（抽象总结）
- `postProcessBeanFactory()`：在 BFPP 之前插入，适合注册/调整 BeanFactory 级别能力（Scope、解析器、属性编辑等）。
- `onRefresh()`：在通用基础设施（消息源/事件系统）就绪后、业务 Bean 大规模实例化前插入，适合启动“需要容器环境但又会影响后续 Bean 创建”的设施。

### Web 场景特例：Boot 在 `onRefresh()` 启动 WebServer（嵌入式 Tomcat）
当应用是 Web 类型时，Spring Boot 使用 `ServletWebServerApplicationContext`（`ApplicationContext` 的 Web 特例）覆盖 `onRefresh()`，把“创建内嵌 Web 服务器”的逻辑插入到 refresh 的标准流程里。

```
SpringApplication.run()
  -> AbstractApplicationContext.refresh()
       ...
       -> onRefresh()  // hook：留给子类扩展
            -> ServletWebServerApplicationContext.onRefresh()
                 -> createWebServer()
                      -> TomcatServletWebServerFactory.getWebServer(...)
                           -> new org.apache.catalina.startup.Tomcat()
                                - new StandardServer()
                                - new StandardService()
                                - (Engine/Host/Context/Connector 等继续组装与配置)
```

### Web 场景下：谁创建了什么？（对照用）
- `SpringApplication.run()`：负责启动 Spring 容器并触发 `refresh()`，本身不直接创建 Tomcat 组件。
- `ServletWebServerApplicationContext.onRefresh()`：通过 hook 调用 `createWebServer()`，把“启动 WebServer”挂载进 refresh 流程。
- `TomcatServletWebServerFactory.getWebServer(...)`：作为工厂，驱动创建并配置内嵌 Tomcat。
- `org.apache.catalina.startup.Tomcat`：其构造与组装过程创建/持有 `StandardServer`、`StandardService` 等 Tomcat 核心组件。

### 约束原理

- **EventMulticaster 必须在 `onRefresh()` 之前**：`onRefresh()` 可能产生事件（如 Web 服务器启动失败），需有广播器接收。
- **`onRefresh()` 必须在业务 Bean 之前**：业务 Bean（如 Controller）可能依赖 `onRefresh()` 创建的设施（如 `ServletContext`）。
- **监听器注册在 Bean 实例化之前**：确保 Bean 创建过程中产生的事件可被分发。

## 关系：上级/下级/等价/特例/推广
- 上级：
  - 显式生命周期与状态机：见 [../../../patterns/concept/LifecycleStateMachine.md](../../../patterns/concept/LifecycleStateMachine.md)
  - 两阶段初始化：见 [../../../patterns/pattern/TwoPhaseInitialization.md](../../../patterns/pattern/TwoPhaseInitialization.md)
  - 模板方法：见 [../../../patterns/pattern/TemplateMethod.md](../../../patterns/pattern/TemplateMethod.md)
  - 装配与运行分离：见 [../../../patterns/principle/SeparationOfWiringAndRunning.md](../../../patterns/principle/SeparationOfWiringAndRunning.md)
- 相关：
  - `ApplicationContext`（见 [../interface/ApplicationContext.md](../interface/ApplicationContext.md)）
  - Bean 注册与创建流程（见 [springboot/flows/Bean注册与创建流程.md](../../../../flows/Bean注册与创建流程.md)）
  - BFPP/BDRPP 执行与断环（见 [springboot/modules/core/beans/mechanism/BeanFactoryPostProcessorExecution.md](../../beans/mechanism/BeanFactoryPostProcessorExecution.md)）
  - 监听器注册与装配（见 [../../events/mechanism/ApplicationListenerRegistration.md](../../events/mechanism/ApplicationListenerRegistration.md)）
  - 生命周期事件（见 [../../events/mechanism/ApplicationLifecycleEvents.md](../../events/mechanism/ApplicationLifecycleEvents.md)）
  - 启动流程（见 [springboot/flows/启动流程.md](../../../../flows/启动流程.md)）

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → context → mechanism → ContextRefresh → flows/启动流程。
