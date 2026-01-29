# ExtensionExecutionModels（Boot 启动扩展点的执行模型）

> **类型**：机制（Mechanism）

## 一句话
Boot 启动扩展点的执行模型描述了通过 `SpringFactoriesLoader` 等发现的 SPI 实现类，在启动阶段被回调时的组织方式：要么按顺序批量遍历调用（ordered iteration），要么在解析/加载流程的分派点按“是否支持”选择实现执行（dispatch by support）。

## 严格定义
给定扩展点接口类型 $T$ 与其实现集合 $I(T)=\\{i_1,\\dots,i_n\\}$（由 classpath 资源声明并被发现），Boot 在启动生命周期中对 $I(T)$ 的回调可按以下两类语义组织：

1. **有序批量遍历（Ordered Batch Invocation）**：将 $I(T)$ 按排序规则 $\\prec$ 排序后，对每个阶段/触发点 $p$ 以 for-loop 形式依次调用 $i_k(p)$。
2. **分派点选择（Dispatch at Resolution Points）**：在流程遇到输入对象 $x$ 时，通过谓词 $supports(i, x)$ 或等价匹配规则，从 $I(T)$ 中选择一个或多个实现执行；对未匹配者不调用。

两类模型均可使用“实现集合 + 遍历”的基本实现方式，但其语义分别对应“全体叠加/逐个生效”与“按类型路由/按输入匹配”。

## 接口：数据 + 约束
- 输入：
  - SPI 接口类型 $T$
  - 实现集合 $I(T)$（来自 `spring.factories` / `imports` 等资源）
  - 触发点 $p$（启动阶段回调）或输入 $x$（待解析/待加载对象）
- 输出：
  - 对启动期对象的副作用（例如修改 `Environment`、注册 bootstrap 对象、发布事件、解析配置资源）
- 约束：
  - 排序：有序批量遍历通常接受 `Ordered`/`@Order` 等排序约定；排序是实现集合到执行序列的确定性映射。
  - 作用域：部分扩展点的实例需与一次 `run(args)` 绑定（例如需要 `args` 的构造签名或持有 per-run 状态）。
  - 版本边界：某些扩展点仅在特定 Boot 版本存在（例如 `ConfigData*` 在 Boot 2.4+）。

## 常用构造/操作（仅列出接口与符号）
### A. 有序批量遍历（Ordered Batch Invocation）
- 收集：`loadFactories(T)` → `List<T>`
- 排序：`sort(list)`（按 `Ordered/@Order`）
- 执行：`for (T t : list) t.callback(...)`

典型接口（示例）：
- `EnvironmentPostProcessor`：在环境准备阶段逐个回调以修改 `Environment`
- `BootstrapRegistryInitializer`：在 bootstrap registry 使用前逐个回调以注册对象/工厂
- `SpringApplicationRunListener`：在 `run()` 的各阶段逐个回调（见 [../../core/bootstrap/interface/SpringApplicationRunListener.md](../../core/bootstrap/interface/SpringApplicationRunListener.md)）

### B. 分派点选择（Dispatch at Resolution Points）
- 触发：遇到输入 $x$（例如某类 location/resource）
- 匹配：遍历实现集合以判断 `supports(i, x)` 或等价匹配
- 执行：仅对匹配者执行解析/加载

典型接口（示例，Boot 2.4+）：
- `ConfigDataLocationResolver`：将 location 字符串解析为资源描述
- `ConfigDataLoader`：将资源描述加载为配置数据

## 关系：上级/下级/等价/特例/推广
- 上级：扩展点发现（见 [SpringFactoriesLoader.md](SpringFactoriesLoader.md)）。
- 相关：
  - 扩展点清单：见 [ExtensionPoints.md](ExtensionPoints.md)
  - 启动流程触发点：见 [springboot/flows/启动流程.md](../../../flows/启动流程.md)
  - 容器侧链（对照语义）：BeanFactory 后处理器与实例后处理器（见 [springboot/flows/Bean注册与创建流程.md](../../../flows/Bean注册与创建流程.md)）

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → extension → mechanism → ExtensionExecutionModels → flows/启动流程。

