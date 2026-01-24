# ApplicationListener（事件监听器）

> **类型**：标准接口（Standard Interface）

## 一句话
`ApplicationListener` 是 Spring 框架标准的事件观察者接口，用于接收并处理 `ApplicationEvent`（包括 Boot 的启动事件和 Context 的运行事件）。

## 严格定义
### 1. 接口：`ApplicationListener<E>`
它是基于观察者模式（Observer Pattern）的消费者接口，定义了单一的事件处理方法。

```java
@FunctionalInterface
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
    void onApplicationEvent(E event);
}
```

### 2. 组合机制：`ApplicationEventMulticaster`
与 `RunListener` 不同，`ApplicationListener` 没有对应的“复数类”（如 ~~ApplicationListeners~~）。它的组合与执行是由 **`ApplicationEventMulticaster`**（事件多播器）负责的。
- **机制**：多播器维护一个监听器注册表，当事件发布时，它会遍历注册表，筛选出对该事件感兴趣的监听器并调用。

## 核心特性
- **泛型过滤**：通过 `<E>` 指定感兴趣的事件类型（如 `ApplicationListener<ApplicationStartedEvent>`），Spring 会自动过滤。
- **排序支持**：实现 `Ordered` 接口或使用 `@Order` 注解，决定同一事件下不同监听器的执行顺序（Order 值越小越先执行）。
- **智能监听**：`SmartApplicationListener`（旧版）或 `GenericApplicationListener`（新版）支持更细粒度的事件类型和源对象（Source）匹配。

## 常用构造/操作
- **注册方式**：
  1. **`spring.factories`**：用于监听 Boot **启动早期**事件（Context 创建前）。
  2. **`SpringApplication.addListeners(...)`**：编程式添加。
  3. **`@Component` / `@EventListener`**：用于监听 **Context 创建后**的事件（启动早期事件无法通过这种方式捕获，因为 Bean 还没扫描）。

## 关系：上级/下级/等价/特例/推广
- **上级**：`java.util.EventListener`。
- **对应**：`ApplicationEvent`（被监听的事件对象，见 [ApplicationLifecycleEvents.md](ApplicationLifecycleEvents.md)）。
- **管理者**：`ApplicationEventMulticaster`（负责广播）。
- **特例**：`SmartApplicationListener`（支持更复杂的匹配逻辑）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → events → ApplicationListener → （Multicaster / SmartListener）。
