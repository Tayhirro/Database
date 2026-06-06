---
title: MAS Orchestration Framework
date: "2026-04-16"
categories:
  - multi-agent
  - orchestration
description: "The Orchestration of Multi-Agent Systems 论文核心框架总结"
---

导航：[README.md](../README.md) | [概念图.md](../概念图.md) | [索引.md](../索引.md)

---

# The Orchestration of Multi-Agent Systems

> **论文信息**
> - 标题：The Orchestration of Multi-Agent Systems: Architectures, Protocols, and Enterprise Adoption
> - 作者：Apoorva Adimulam, Rajesh Gupta, Sumit Kumar（Skan AI）
> - 领域：Multi-Agent Systems / Agent Orchestration

---

## 一、为什么需要统一的 MAS 编排框架

本文关注的问题不是单个 Agent 如何调用更多工具，而是当 Agent 系统进入复杂任务、复杂工具和企业级部署场景后，如何从**单 Agent 工具增强**转向**多 Agent 协作编排**。

早期 Agent 系统通常通过给一个 Agent 配置更多工具、更长上下文和更复杂的提示词来提升能力。但当任务复杂度继续提高时，单 Agent 模式会逐渐暴露出三个结构性问题：

1. **上下文窗口有限**：单个 Agent 需要同时保存任务目标、历史轨迹、工具说明、中间结果和环境反馈。交互轮次越多，上下文越容易膨胀，关键信息也越容易被稀释。
2. **工具过多导致 prompt 膨胀**：当所有工具都暴露给同一个 Agent 时，工具说明会占用大量上下文空间，并增加工具选择、参数填写和错误恢复的难度。
3. **缺乏全局协调和企业级治理**：单 Agent 模式难以稳定处理跨角色协作、任务分解、权限控制、调用审计、运行观测和失败恢复。

因此，MAS 的核心转变不是继续给一个 Agent 叠加更多工具，而是：

> 从“给单个 Agent 配置更多工具”，转向“由多个专用 Agent 在编排层协调下协作完成任务”。

本节的动机链条可以概括为：

```text
单 Agent + 大量工具
  -> 上下文膨胀
  -> 工具选择困难
  -> 缺乏全局协调
  -> 缺乏治理与观测
        ↓
引入 MAS 编排层
        ↓
多个 Specialized Agents + Orchestration Layer
        ↓
MCP 连接工具 / A2A 连接 Agent / Governance 支撑企业落地
```

基于这一背景，论文提出一个统一的 MAS 编排框架，将多 Agent 系统整合为三层核心内容：

1. **MAS 架构组成**：Specialized Agents（Worker / Service / Support）+ Orchestration Layer。
2. **通信协议体系**：MCP 处理 Agent 与工具、资源之间的连接，A2A 处理 Agent 与 Agent 之间的通信与协作。
3. **企业落地机制**：将治理、观测、权限、审计、运维和失败恢复纳入编排框架。

因此，本文的核心贡献不只是提出一个多 Agent 架构，而是将 **Agent 角色分工、编排层控制、工具协议、Agent 间通信协议和企业治理机制** 放入同一个分析框架中，解释 MAS 为什么需要从单 Agent 工具调用系统演化为可编排、可观测、可治理的协作系统。

---

## 二、架构总览

```
┌─────────────────────────────────────────────────────────────┐
│                    Orchestration Layer                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ Planning │  │  Policy  │  │ Execution│  │  State & │   │
│  │   Unit   │  │   Unit   │  │& Control │  │ Knowledge│   │
│  │  (规划)  │  │  (策略)  │  │  (执行)  │  │ (状态)   │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
│                   ┌──────────────────┐                    │
│                   │Quality & Operations│                   │
│                   │      (质量运维)     │                    │
│                   └──────────────────┘                    │
├─────────────────────────────────────────────────────────────┤
│                   Communication Protocols                   │
│            MCP (Agent ↔ 工具/数据)                          │
│            A2A  (Agent ↔ Agent)                            │
├─────────────────────────────────────────────────────────────┤
│                  Specialized Agents                        │
│  Worker Agents + Service Agents + Support Agents          │
│ (执行层/任务专用) (共享服务/治理) (监督分析/系统支撑)        │
└─────────────────────────────────────────────────────────────┘
```

---

## 三、Specialized Agents（专用智能体）

### 3.1 Worker Agents（执行层智能体）

**定义**：最基础的任务执行单元，每个负责一个明确范围的子任务。

**特点**：
- 无状态的：每个请求独立处理，不保留上下文
- 有状态的：跟踪跨多步骤的工作流进度
- 通常并行运行，每个专注于狭窄的子领域

**典型场景**：
- 金融信贷工作流中：提取申请数据 → 计算信用评分 → 生成初步风险评估 → 下游 agent 验证

**对应 Claude Code 的实现**：
- Explore Agent（只读探索）
- Plan Agent（只读规划）
- GeneralPurpose Agent（全工具执行）
- 这三种都属于 Worker Agent 的范畴

### 3.2 Service Agents（服务型智能体）

**定义**：为其他 agent 提供共享服务能力的专用单元，通常按需插入主执行链路。

**特点**：
- 可复用，作为系统公共能力存在
- 不直接承担业务目标，但直接参与质量、诊断、恢复等执行环节

**典型能力**：
- **Quality Assurance**：验证数据、交叉检查合规性
- **Diagnostic**：检测不一致或缺失，追溯责任模块，生成结构化错误报告
- **Healing**：自动重试失败操作或重置工作流

**对应 Claude Code 的实现**：
- Verification Agent（验证执行结果是否正确）
- Coordinator Mode 中按需触发的验证/修复型辅助 agent

### 3.3 Support Agents（支撑型智能体）

**定义**：为 MAS 提供持续监督、分析和系统支撑的辅助单元，作为 Service Agents 的补充存在。

**特点**：
- 更偏旁路支撑，而不是直接插入业务主流程
- 关注观测、分析、数据新鲜度和系统级保障

**典型能力**：
- **Monitoring**：监测 agent 健康度、延迟、吞吐、SLA
- **Analytics**：分析跨 agent 表现、瓶颈、异常趋势
- **Data Refresh**：刷新知识源、缓存或上下文依赖，保持状态有效

**对应 Claude Code 的实现**：
- 没有严格等价的独立 Support Agent
- 更接近 telemetry、日志观测、上下文维护等分散式系统能力

### 3.4 Worker vs Service vs Support 对比

| 维度 | Worker Agent | Service Agent | Support Agent |
| --------- | ------------ | ------------- | ------------- |
| **职责** | 执行具体业务子任务 | 提供质量/诊断/修复等共享服务 | 提供监控/分析/数据刷新等系统支撑 |
| **触发方式** | 由编排层分配任务 | 被动响应验证、异常或治理需求 | 周期性运行或按观测事件触发 |
| **是否在主流程中** | 是 | 通常按需插入 | 通常不直接在主流程中 |
| **是否可并行** | 通常并行 | 通常按需调用 | 常驻或异步运行 |
| **类比** | 工厂流水线上的操作工 | 工厂里的质检员/维修工 | 监控室里的运维分析员 |

---

## 四、Orchestration Layer（编排层）

编排层是 MAS 的"中枢神经系统"，包含五个组件：

### 4.1 Planning Unit（规划单元）

**职责**：任务分解 + 执行顺序编排

**功能**：
- 将复杂目标拆解为可协调的子任务
- 决定子任务的依赖关系、先后顺序与分派对象
- 根据全局任务状态动态调整流程，并识别并行机会

**说明**：
- 论文强调把"系统级规划"和"agent 级执行"分层：specialized agents 仍在各自职责边界内自主感知、推理和行动
- 因此 Planning Unit 关注的是"整体下一步谁做什么"，而不是某个 agent 内部"下一步调哪个工具"
- 在实现上，Planning Unit 可以是显式计划器（如任务图 / DAG），也可以是 coordinator 驱动的动态分派逻辑

**Claude Code 对应**：
- `coordinator/coordinatorMode.ts`：Coordinator 负责跨 worker 的任务拆分、分派和结果汇总
- `AgentTool` / worker delegation 机制：把全局目标拆成子任务并交给不同 worker

### 4.2 Policy Unit（策略单元）

**职责**：治理约束 + 合规性检查

**功能**：
- 预定义的策略规则在任务执行前生效
- 数据进入共享状态前验证 schema，防止无效数据传播
- 检测不一致时更新状态，并调用 service agent 执行诊断或修复；support agent 持续提供监测与分析支撑

**Claude Code 对应**：
- Tool Permission 机制（危险操作需用户确认）
- `Tool.ts` 的 `checkPermissions()` 方法
### 4.3 Execution & Control Unit（执行控制单元）

**职责**：运行时调度 + 并发管理

**功能**：
- 管理任务并发执行
- 收集遥测数据（telemetry）：延迟、吞吐、成功率
- 异常检测 → 触发预防性干预
- 支持新组件的受控部署、测试、沙箱验证

**Claude Code 对应**：
- `toolOrchestration.ts` 的并发/串行执行编排
- `StreamingToolExecutor.ts` 的流式工具执行

### 4.4 State & Knowledge Management（状态与知识管理）

**职责**：维护系统状态 + 提供上下文

分为两个子单元：

**State Unit（状态单元）**：
- 管理检查点（checkpoint）
- 跟踪工作流进度、agent 状态、活动日志
- 支持 agent 从检查点恢复，保证工作流完整性

**Knowledge Unit（知识单元）**：
- 管理领域上下文信息
- 连接外部数据源，将数据暴露为可检索上下文
- 保证 worker agent 和编排组件操作的信息一致性

**Claude Code 对应**：
- 对话历史管理（`messages` 数组）
- 上下文压缩机制（Snip → Microcompact → Autocompact）
- `context.ts` 加载 CLAUDE.md、git 状态等上下文

### 4.5 Quality & Operations Management（质量与运维管理）

**职责**：验证结果 + 性能优化

**功能**：
- 基于遥测数据、状态更新、上下文数据评估系统性能
- 验证聚合输出是否符合策略标准
- 应用性能洞察优化未来工作流

**Claude Code 对应**：
- Verification Agent 的结果验证逻辑
- 模型回退机制（API 超时 → 降级到轻量模型）

### 4.6 编排层组件与 Claude Code 对照表

| 编排层组件 | 职责 | Claude Code 对应实现 |
|---|---|---|
| Planning Unit | 任务分解、执行顺序 | Coordinator Mode、AgentTool 分派逻辑 |
| Policy Unit | 治理约束、合规检查 | Tool permissions、checkPermissions() |
| Execution & Control | 运行时调度、并发管理 | queryLoop、toolOrchestration、StreamingToolExecutor |
| State & Knowledge | 状态维护、上下文供给 | messages 历史、context.ts、上下文压缩 |
| Quality & Operations | 结果验证、性能优化 | Verification Agent、模型回退 |

---

## 五、通信协议

### 5.1 MCP（Model Context Protocol）

**定位**：agent 与外部工具/数据源之间的接口标准

**解决的问题**：
- 传统方式：每个 agent 自己定义工具调用格式，难以复用
- MCP：将工具和数据源标准化接入，任何 agent 都能调用

**类比**：USB 接口——设备（工具）和电脑（agent）之间的标准协议

**Claude Code 对应**：
- `assembleToolPool()` 整合 MCP 工具
- 内部 30+ 工具通过统一的 `Tool` 接口注册

### 5.2 A2A（Agent-to-Agent Protocol）

**定位**：agent 之间的协作协议

**解决的问题**：
- MCP 只能 agent→工具，不能 agent↔agent
- 多 agent 需要协商、委托、状态同步

**核心能力**：
- **Task Delegation**：主 agent 将子任务委托给其他 agent
- **State Synchronization**：多 agent 共享工作流状态
- **Negotiation**：agent 之间协商任务分配

**Claude Code 对应**：
- 子 agent 机制（Explore/Plan/Verification/GeneralPurpose/Fork）
- Coordinator Mode 的 agent 间通信
- Fork 机制的 prompt cache 共享

### 5.3 MCP vs A2A 对比

| 维度 | MCP | A2A |
|---|---|---|
| **通信方向** | agent → 工具/数据 | agent ↔ agent |
| **协议层级** | 基础设施层 | 编排协调层 |
| **类比** | USB 协议 | HTTP/WebSocket |
| **Claude Code** | 工具注册系统 | 子 agent 通信机制 |

---

## 六、完整工作流示例：金融风控

论文以金融机构信贷风险与欺诈检测工作流为例，完整展示了编排层如何协同：

```
用户提交贷款申请
        │
        ▼
┌──────────────────────────────────────────────┐
│           Orchestration Layer                 │
│                                              │
│  Planning Unit：分解子任务                    │
│   - 数据提取                                 │
│   - 风险评估                                 │
│   - 合规审查                                 │
│   - 欺诈筛查                                 │
│        │                                    │
│  Policy Unit：嵌入治理约束                   │
│   - 贷款法规                                 │
│   - 机构风险阈值                             │
│        │                                    │
│  Execution & Control：并发执行 + 遥测        │
│   - 并行执行四个子任务                       │
│   - 收集延迟/吞吐/成功率                     │
│        │                                    │
│  State & Knowledge：维护状态                 │
│   - 申请人状态                               │
│   - 历史记录                                 │
│   - 监管参考                                 │
│        │                                    │
│  Quality & Operations：验证结果              │
│   - 对照策略标准验证                         │
│   - 性能洞察优化未来工作流                   │
│                                              │
└──────────────────────────────────────────────┘
        │
        ▼
最终审批决策
```

---

## 七、论文核心结论

> **可靠性不只来自智能体本身，还来自编排层对规划、执行和验证的治理。**

MAS 的核心洞察：
1. **专业化胜于通用**：多个专用 agent 协作 > 单个全能 agent
2. **编排层是灵魂**：编排层（而非 agent 本身）决定了系统的可靠性、可审计性和策略合规性
3. **协议标准化是规模化前提**：MCP + A2A 建立了互操作基础
4. **观测与治理不可分割**：没有可观测性就没有可问责性

---

## 八、与 Claude Code 的关系

| 论文框架 | Claude Code 实现 | 对应文件 |
|---|---|---|
| Worker Agent | Explore/Plan/GeneralPurpose/Fork | runAgent.ts, forkSubagent.ts |
| Service Agent | Verification Agent | runAgent.ts |
| Support Agent | 无独立一等 agent；更接近 telemetry / context maintenance / observability 能力 | query.ts, context.ts, 遥测与状态维护逻辑 |
| Planning Unit | Coordinator Mode + AgentTool 分派逻辑 | coordinatorMode.ts, AgentTool/ |
| Execution & Control | queryLoop + toolOrchestration | query.ts, toolOrchestration.ts |
| State & Knowledge | messages 管理 + context.ts | context.ts, 上下文压缩 |
| Quality & Operations | Verification + 模型回退 | runAgent.ts, claude.ts |
| MCP | MCP 工具注册 | tools.ts, assembleToolPool() |
| A2A | 子 agent 通信 | AgentTool/, coordinatorMode.ts |

---

## 九、术语对照

| 英文术语 | 中文翻译 | 说明 |
|---|---|---|
| Orchestration Layer | 编排层 | MAS 的中央协调组件 |
| Specialized Agent | 专用智能体 | 承担特定角色的 agent |
| Worker Agent | 执行层智能体 | 直接执行任务的 agent |
| Service Agent | 服务型智能体 | 提供质量/诊断/修复等共享服务的 agent |
| Support Agent | 支撑型智能体 | 提供监控/分析/数据刷新等系统支撑的 agent |
| Task Delegation | 任务委托 | 主 agent 向子 agent 分配任务 |
| State Management | 状态管理 | 维护工作流执行状态 |
| Policy Enforcement | 策略执行 | 保证操作符合治理规则 |
| Telemetry | 遥测数据 | 性能/延迟/成功率等监控数据 |
| Checkpoint | 检查点 | 工作流的快照，支持故障恢复 |

---

## References

- Adimulam, A., Gupta, R., & Kumar, S. (2026). *The Orchestration of Multi-Agent Systems: Architectures, Protocols, and Enterprise Adoption*. Skan AI.
