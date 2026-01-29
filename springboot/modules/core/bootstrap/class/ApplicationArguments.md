---
type: class
tags:
  - springboot/class
  - bootstrap
---

# ApplicationArguments（应用参数封装）

## 一句话
`ApplicationArguments` 是 Spring Boot 对命令行参数的结构化封装，将 `String[] args` 解析为 option arguments（`--key=value`）和 non-option arguments（positional args）两类。

## 严格定义
在 Spring Boot 中，`org.springframework.boot.ApplicationArguments` 是参数封装接口，由 `DefaultApplicationArguments` 实现；在 `SpringApplication.run()` 开始时，原始命令行参数 `String[] args` 被解析为该结构，供 `ApplicationRunner` 和后续流程使用。

## 接口：数据 + 约束

### 数据
- `Source`：原始参数源（`String[]`）
- `OptionNames`：`--key=value` 形式的所有 key 集合
- `NonOptionArgs`：不以 `--` 开头的 positional 参数列表

### 操作
| 方法 | 说明 |
|------|------|
| `String[] getSourceArgs()` | 获取原始参数数组 |
| `Set<String> getOptionNames()` | 获取所有 option key |
| `boolean containsOption(String name)` | 判断是否包含指定 option |
| `List<String> getOptionValues(String name)` | 获取指定 option 的所有值（支持重复） |
| `List<String> getNonOptionArgs()` | 获取 positional 参数列表 |

### 约束
- **解析规则**：以 `--` 开头视为 option arg，格式为 `--key=value` 或 `--key`；其余为 non-option arg
- **重复 key**：同一 key 出现多次，`getOptionValues()` 返回列表
- **boolean flag**：`--enable` 形式的 flag，存在即视为 true

## 示例

```bash
java -jar app.jar --server.port=8080 --debug profile1 profile2
```

解析结果：
| 方法调用 | 返回值 |
|----------|--------|
| `getOptionNames()` | `["server.port", "debug"]` |
| `getOptionValues("server.port")` | `["8080"]` |
| `containsOption("debug")` | `true` |
| `getNonOptionArgs()` | `["profile1", "profile2"]` |

## 关系：上级/下级/等价/特例/推广

- 上级：启动流程（见 [flows/启动流程.md](../../../flows/启动流程.md)）的 Phase 0-2（参数准备阶段）
- 使用者：`ApplicationRunner.run(ApplicationArguments args)`（见 [ApplicationRunner.md](./ApplicationRunner.md)）
- 对比：原始 `String[] args`（`CommandLineRunner` 使用）

## 把新概念挂回框架（多级索引轨迹）

springboot → modules → core → bootstrap → class → ApplicationArguments → interface/ApplicationRunner
