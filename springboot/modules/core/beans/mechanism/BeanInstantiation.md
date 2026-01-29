---
type: mechanism
tags:
  - springboot/mechanism
  - beans
---

# Bean 实例化策略（Bean Instantiation Strategy）

## 一句话

Bean 实例化策略定义了 Spring 根据 BeanDefinition 选择何种构造机制来创建 Bean 原始实例。

## 严格定义

给定 BeanDefinition $d$，实例化阶段通过以下优先级顺序选择构造方式：

$$
\text{InstantiationStrategy}(d) = \begin{cases}
\text{Supplier} & \text{if } d.instanceSupplier \neq \text{null} \\
\text{FactoryMethod} & \text{if } d.factoryMethodName \neq \text{null} \\
\text{Constructor} & \text{otherwise}
\end{cases}
$$

其中 FactoryMethod 又分为：
- **静态工厂方法**：$d.factoryMethodName + d.factoryBeanName = \text{null}$
- **实例工厂方法**：$d.factoryMethodName + d.factoryBeanName \neq \text{null}$

## 接口：数据 + 约束

### 输入
- `BeanDefinition`：包含 className、constructorArgumentValues、propertyValues、factoryMethodName、factoryBeanName、instanceSupplier
- `BeanFactory`：提供 ClassLoader、ObjectProvider 解析能力

### 输出
- 实例化的原始对象（尚未注入属性）

### 约束
- `Supplier` 优先级最高，直接调用 `supplier.get()`
- 构造函数选择需处理 `@Autowired(required=false)` 与多构造函数场景
- 工厂方法需处理参数解析与类型匹配
- `BeanDefinition.autowireMode` 决定构造函数注入策略

## 构造方式详述

### 1. Supplier 回调

| 字段 | 说明 |
|------|------|
| `BeanDefinition.instanceSupplier` | `Supplier<T>` 函数式接口 |
| 调用时机 | `AbstractAutowireCapableBeanFactory.createBean(...)` |
| 返回值 | 直接作为原始实例 |

### 2. 工厂方法

| 类型 | 条件 | 调用方式 |
|------|------|----------|
| 静态工厂方法 | `factoryMethodName != null` + `factoryBeanName == null` | `Class.forName(className).getMethod(factoryMethodName, args...).invoke(null, args)` |
| 实例工厂方法 | `factoryMethodName != null` + `factoryBeanName != null` | `beanFactory.getBean(factoryBeanName).getClass().getMethod(factoryMethodName, args...).invoke(bean, args)` |
| 泛型工厂方法 | `GenericTypeAwareAutowireBeanFactory` | 处理泛型返回类型解析 |

### 3. 构造函数

| 策略 | 说明 |
|------|------|
| `AUTOWIRE_NO` | 默认，使用无参或唯一匹配构造函数 |
| `AUTOWIRE_CONSTRUCTOR` | 选择匹配参数类型的构造函数 |
| `@Autowired` 构造器 | 优先使用标注 `@Autowired(required=true)` 的构造函数 |
| CGLIB 代理构造 | 子类继承场景需调用父类构造函数 |

## 常用构造/操作

| 操作 | 接口/类 |
|------|---------|
| 实例化入口 | `AbstractAutowireCapableBeanFactory.createBean(...)` |
| 策略选择 | `AbstractAutowireCapableBeanFactory.createBeanInstance(...)` |
| Supplier 调用 | `Supplier.get()` |
| 工厂方法调用 | `ConstructorResolver.invokeFactoryMethod(...)` |
| 构造函数解析 | `ConstructorResolver.autowireConstructor(...)` |

## 关系：上级/下级/等价/特例/推广

- 上级：[Bean 生命周期](BeanLifecycle.md)（Instantiation 阶段）
- 相关：[BeanDefinition](../interface/BeanDefinition.md)（包含实例化元数据）
- 相关：[FactoryBean](../interface/FactoryBean.md)（工厂方法特例）
- 特例： `@Bean` 方法 → 工厂方法实例化

## 把新概念挂回框架（多级索引轨迹）

springboot → modules → core → beans → mechanism → BeanInstantiation
