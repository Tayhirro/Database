# Starter 依赖（Spring Boot Starters）

## 一句话
starter 依赖是一种依赖组织方式：用一个“聚合依赖”表达某类能力的依赖集合，并通过约定与自动配置在运行时形成默认行为。

## 严格定义
在 Maven/Gradle 依赖图中，starter 通常是一个依赖坐标（artifact），其主要作用是引入一组传递依赖；这些依赖中可能包含：
- 运行库（例如日志、Web 栈、序列化等）
- 自动配置模块（autoconfigure）及其声明资源

starter 本身通常不要求提供代码入口，其作用是组织依赖与默认组合。

## 接口：数据 + 约束
- 输入：构建系统依赖声明（Maven/Gradle）。
- 输出：应用的 classpath 依赖闭包。
- 约束：依赖管理（BOM/parent）决定版本一致性策略；具体以项目构建配置为准。

## 常用构造/操作（仅列出接口与符号）
- 典型命名：`spring-boot-starter-*`
- 与自动配置的连接：见 [springboot/modules/AutoConfiguration.md](AutoConfiguration.md)

## 关系：上级/下级/等价/特例/推广
- 上级：依赖管理与模块化。
- 相关：自动配置（starter 引入的 autoconfigure 会提供候选配置类声明资源）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → Starters → 自动配置 → 启动流程。

