---
type: interface
tags:
  - springboot/interface
  - bootstrap
---

# CommandLineRunner（命令行启动器接口）

## 一句话
`CommandLineRunner` 定义了 Spring Boot 启动完成后执行的回调契约，接收原始命令行参数数组，用于在应用就绪前执行初始化逻辑。

## 严格定义
在 Spring Boot 中，`org.springframework.boot.CommandLineRunner` 是函数式接口，其实现类作为标准 Bean 注册在容器中；在 `SpringApplication.run()` 的 `callRunners` 阶段，容器通过类型查找获取所有 `CommandLineRunner` Bean，与 `ApplicationRunner` 合并后按 `@Order` 或 `Ordered` 排序，依次调用 `run(String... args)`。

## 接口：数据 + 约束

### 输入
- `String... args`：原始命令行参数数组（未经解析的 `main(String[] args)` 入参）

### 输出
- 无返回值（void）
- 副作用：执行用户自定义启动逻辑

### 约束
- **执行时机**：在 `context.refresh()` 完成后，`ApplicationReadyEvent` 发布前；与 `ApplicationRunner` 混合排序执行
- **异常处理**：`run()` 抛出异常会中止启动流程，触发 `ApplicationFailedEvent`
- **排序**：通过 `@Order` 或 `Ordered` 接口控制执行顺序；与 `ApplicationRunner` 统一排序
- **参数形式**：接收原始字符串数组，如需解析需自行处理或使用 `ApplicationRunner`

## 常用构造/操作

| 操作 | 说明 |
|------|------|
| `void run(String... args)` | 执行启动后逻辑 |
| `@Order(int)` / `Ordered` | 控制多个 Runner 的执行顺序 |

## 与 ApplicationRunner 的区别

| 维度 | CommandLineRunner | ApplicationRunner |
|------|-------------------|-------------------|
| 参数类型 | `String... args`（原始字符串数组） | `ApplicationArguments`（结构化封装） |
| 参数解析 | 需手动解析命令行参数 | Spring Boot 自动解析 `--key=value` 和 positional args |
| 使用场景 | 简单脚本、不需要参数解析 | 需要结构化访问启动参数 |

## 关系：上级/下级/等价/特例/推广

- 上级：启动流程（见 [flows/启动流程.md](../../../../flows/启动流程.md)）的 Phase 5b（Runner 执行阶段）
- 等价：`ApplicationRunner`（功能等价，参数形式不同）
- 相关：`SpringApplication`（调用方，见 [SpringApplication.md](../class/SpringApplication.md)）

## 把新概念挂回框架（多级索引轨迹）

springboot → modules → core → bootstrap → interface → CommandLineRunner → flows/启动流程/Phase 5b
