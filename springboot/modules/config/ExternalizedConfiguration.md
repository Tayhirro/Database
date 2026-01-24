# 外部化配置（Externalized Configuration）

> **类型**：机制（Mechanism）

## 一句话
外部化配置是将来自多个配置源的配置项按优先级合并到 `Environment`，并供容器与自动配置在启动/运行阶段查询的机制集合。

## 严格定义
对一次启动过程，外部化配置可抽象为从“配置源集合”到“可查询配置视图”的映射：
- 配置源：命令行参数、系统属性、环境变量、配置文件等
- 配置视图：`Environment`（包含 profiles 与 `PropertySource` 序列）

该机制为后续的条件判断（`@Conditional...`）与属性绑定（`@ConfigurationProperties`）提供输入。

## 接口：数据 + 约束
- 数据：
  - 配置源集合（sources）
  - profiles（激活/默认）
  - 属性解析与占位符规则（placeholder resolution）
- 输出：
  - `Environment`（可按 key 查询属性值）
- 约束：
  - 配置源的种类、加载顺序与覆盖规则属于版本相关行为，具体以对应版本的参考实现为准。

## 常用构造/操作（仅列出接口与符号）
- 配置查询：`Environment.getProperty(key)`（抽象接口）
- profile：`spring.profiles.active` / `spring.profiles.include`
- 属性绑定：`@ConfigurationProperties`（见 [springboot/modules/config/ConfigurationProperties.md](ConfigurationProperties.md)）

## 关系：上级/下级/等价/特例/推广
- 上级：配置管理（configuration management）。
- 下级：`Environment`、profiles、属性绑定。
- 相关：自动配置条件（见 [springboot/modules/config/AutoConfiguration.md](AutoConfiguration.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → 外部化配置 →（Environment / profiles / 属性绑定）→ 启动流程。
