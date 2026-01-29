---
type: interface
tags:
  - springboot/interface
  - bootstrap
---

# ApplicationRunner（应用启动器接口）

## 一句话
`ApplicationRunner` 定义了 Spring Boot 启动完成后、容器就绪但尚未对外服务前执行的回调契约，用于在 `ApplicationRunner.run()` 中封装应用启动后的初始化逻辑。

## 严格定义
在 Spring Boot 中，`org.springframework.boot.ApplicationRunner` 是函数式接口，其实现类作为标准 Bean 注册在容器中；在 `SpringApplication.run()` 的 `callRunners` 阶段，容器通过类型查找获取所有 `ApplicationRunner` Bean，按 `@Order` 或 `Ordered` 排序后依次调用 `run(ApplicationArguments args)`。

## 接口：数据 + 约束

### 输入
- `ApplicationArguments args`：封装后的命令行参数（区分 option args 和 non-option args）

### 输出
- 无返回值（void）
- 副作用：执行用户自定义启动逻辑

### 约束
- **执行时机**：在 `context.refresh()` 完成（所有非 lazy Bean 已实例化）后，在 `ApplicationReadyEvent` 发布前
- **异常处理**：`run()` 抛出异常会中止启动流程，触发 `ApplicationFailedEvent`
- **排序**：多个 Runner 通过 `@Order` 或实现 `Ordered` 接口控制执行顺序（默认 `Ordered.LOWEST_PRECEDENCE`）
- **Bean 生命周期**：Runner 就是普通 Bean，在 `finishBeanFactoryInitialization()` 阶段创建，不是单独实例化

## 常用构造/操作

| 操作 | 说明 |
|------|------|
| `void run(ApplicationArguments args)` | 执行启动后逻辑 |
| `@Order(int)` / `Ordered` | 控制多个 Runner 的执行顺序 |

## 与 CommandLineRunner 的区别

| 维度 | ApplicationRunner | CommandLineRunner |
|------|-------------------|-------------------|
| 参数类型 | `ApplicationArguments`（结构化） | `String... args`（原始字符串数组） |
| 参数解析 | 自动解析 `--key=value` 和 positional args | 需手动解析 |
| 使用场景 | 需要访问解析后的参数 | 只需原始命令行参数 |

## 关系：上级/下级/等价/特例/推广

- 上级：启动流程（见 [flows/启动流程.md](../../../flows/启动流程.md)）的 Phase 5b（Runner 执行阶段）
- 等价：`CommandLineRunner`（功能等价，参数形式不同）
- 相关：`SpringApplication`（调用方，见 [SpringApplication.md](../class/SpringApplication.md)）
- 相关：`ApplicationArguments`（参数封装，见 [ApplicationArguments.md](./ApplicationArguments.md)）

## 把新概念挂回框架（多级索引轨迹）

springboot → modules → core → bootstrap → interface → ApplicationRunner → flows/启动流程/Phase 5b
