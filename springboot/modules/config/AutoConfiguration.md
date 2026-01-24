# 自动配置（Auto-Configuration）

> **类型**：机制（Mechanism）

## 一句话
自动配置是基于 classpath、`Environment` 与容器状态等条件，自动导入一组配置类以构建默认 Bean 图的机制（由 `@EnableAutoConfiguration` 触发）。

## 严格定义
在 Spring Boot 中，自动配置可抽象为一个“候选配置类集合”的导入过程：
1) 发现候选 auto-config 列表（来源为 classpath 上的声明资源）  
2) 对候选项应用过滤规则（exclusions、条件注解等）  
3) 将剩余配置类作为 `@Configuration` 类导入到容器的配置类解析流程中

## 接口：数据 + 约束
- 输入（条件的常见来源）：
  - classpath（某类/资源是否存在）
  - `Environment`（属性值、profiles）
  - `ApplicationContext`（某 Bean 是否存在/缺失）
- 输出：
  - 被导入的 auto-config 配置类集合
- 约束：
  - “候选列表如何声明”在 Boot 2.x 与 3.x 存在差异（资源路径与聚合方式不同）。

## 常用构造/操作（仅列出接口与符号）
- 触发注解：`@EnableAutoConfiguration`（通常由 `@SpringBootApplication` 间接引入）
- 条件注解族：`@ConditionalOnClass` / `@ConditionalOnMissingBean` / `@ConditionalOnProperty` 等
- 候选列表资源：
  - Boot 2.x：`META-INF/spring.factories` 中的 `EnableAutoConfiguration` 键
  - Boot 3.x：`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`（以及相关元数据）
- 关联：候选列表的聚合机制见 [springboot/modules/extension/SpringFactories.md](../extension/SpringFactories.md)

## 关系：上级/下级/等价/特例/推广
- 上级：配置类导入（Spring 配置类解析阶段的一类输入）。
- 相关：外部化配置（条件判断输入）、配置属性绑定、starter 依赖组织（见 [springboot/modules/extension/Starters.md](../extension/Starters.md)）。
- 发生位置：通常在 `ApplicationContext.refresh()` 期间的配置类解析与 BeanDefinition 注册阶段（见 [springboot/flows/启动流程.md](../../flows/启动流程.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → 自动配置 →（候选发现/条件过滤/导入）→ 启动流程。
