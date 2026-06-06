---
title: "The Seven Tools of Causal Inference, with Reflections on Machine Learning"
date: "2026-03-30"
categories:
  - 因果推断
description: Judea Pearl 在 CACM 2019 综述中用“三层因果层级 + 七类工具”概括了 SCM 框架能处理的问题边界，以及它与现代机器学习的关键差异。
---
# The Seven Tools of Causal Inference, with Reflections on Machine Learning

## 1. 论文信息

- 作者：Judea Pearl
- 载体：*Communications of the ACM*
- 时间：2019-03
- PDF： [r475-cacm-reprint.pdf](r475-cacm-reprint.pdf)

## 2. 核心主张

这篇文章不是在介绍某一个具体算法，而是在给出一个总框架性的判断：

- 因果推断的关键不在于“再做一种统计拟合”
- 而在于引入 `SCM` 这类可以统一表达观测、干预与反事实语义的结构模型
- 仅停留在观测关联层的学习系统，无法回答行动、解释、责任归因与环境迁移中的核心问题

换言之，文章的重点是：

- 先用三层因果层级说明“因果问题与纯统计问题的边界”
- 再用七类工具说明“SCM 体系具体能完成哪些任务”

## 3. SCM 作为推理引擎

在文章正式进入“三层层级”之前，Pearl 先给出了一段非常关键的总括：`SCM` 由图模型、结构方程以及干预与反事实逻辑三部分组成，并可被视为一个因果推理引擎。

这一推理引擎接收三类输入：

- Assumptions：研究者对因果结构的建模假设
- Queries：待回答的因果问题
- Data：观测数据或实验数据

并输出三类结果：

- Estimand：在既定假设下回答该查询的数学表达式
- Estimate：用具体数据对该表达式进行估计后得到的数值结果
- Fit indices：衡量数据与模型假设相容性的指标

这一定义的意义在于，它把因果推断拆成三个层次清晰的环节：

- 识别：查询是否可由假设与数据类型推出
- 估计：若可识别，如何从有限样本得到数值答案
- 检验：数据是否与建模假设相容

文章中的药物示例说明了这一点。若查询是 `Q = P(Y|do(X))`，假设中 `Z` 同时影响 `X` 与 `Y`，数据来自 `P(X,Y,Z)`，那么推理引擎会先导出：

$$
E = \sum_z P(Y \mid X, Z=z)P(Z=z).
$$

这个 `Estimand` 不是最终答案，而是一个估计处方。后续才是数值 `Estimate` 的问题，而 `Fit indices` 则用于检查图结构和数据是否相容。

这也是文章与很多机器学习表述不同的地方：`SCM` 不是直接从数据里“猜答案”，而是先建立 `Assumptions -> Query -> Estimand -> Estimate` 的形式化映射。

## 4. 三层因果层级

Pearl 在文中把可回答的问题组织成一个严格分层的 hierarchy。

### 4.1 Association（关联）

- 典型形式：`P(y \mid x)`
- 问题类型：观测到 `X=x` 后，`Y` 的分布如何变化
- 数据基础：纯观测数据
- 能力边界：只能回答 seeing 问题，不能回答 doing 与 retrospective questions

### 4.2 Intervention（干预）

- 典型形式：`P(y \mid do(x), z)`
- 问题类型：若外部强行把 `X` 设为 `x`，结果会如何
- 语义关键：`do(x)` 不是条件化，而是结构方程替换
- 数据 / 模型要求：需要实验数据或因果模型支持

### 4.3 Counterfactual（反事实）

- 典型形式：`P(y_x \mid x', y')`
- 问题类型：在已知事实世界里，如果同一个体当时采取了不同动作，会怎样
- 语义关键：需要比较事实世界与替代世界
- 模型要求：需要完整 `SCM`，而不只是相关分布或平均实验效应

## 5. 层级的方向性

- 反事实层能够覆盖干预层与关联层
- 干预层能够覆盖关联层
- 反方向不成立

因此：

- 只掌握 `P(y \mid x)` 的系统，无法自动上升到 `P(y \mid do(x))`
- 只掌握实验平均效应，也无法自动推出个体层反事实判断

这是文章对现代机器学习最核心的批评之一：多数系统停留在 Association 层，而真正的因果分析主要发生在 Intervention 与 Counterfactual 层。

## 6. 七个工具

### Tool 1. Encoding causal assumptions

- 任务：把因果假设以紧凑、透明、可检验的形式编码出来
- 关键价值：
  - transparency：研究者能明确看到自己假设了什么
  - testability：可检查这些假设是否与数据冲突
- 核心工具：
  - 因果图
  - `d-separation`

对应知识库：

- [../structures/StructuralCausalModel.md](../structures/StructuralCausalModel.md)
- [../modules/DSeparation.md](../modules/DSeparation.md)

### Tool 2. Do-calculus and the control of confounding

- 任务：控制混杂，并在更一般情况下识别干预效应
- 第一层工具：
  - `backdoor criterion`
- 更一般工具：
  - `do-calculus`
- 关键判断：
  - 若可识别，应能产出 estimand
  - 若不可识别，应明确返回 failure

对应知识库：

- [../modules/BackdoorCriterion.md](../modules/BackdoorCriterion.md)
- [../modules/DoCalculus.md](../modules/DoCalculus.md)

### Tool 3. The algorithmitization of counterfactuals

- 任务：把个体层“如果当时换个做法会怎样”形式化并算法化
- 核心点：
  - 反事实不是文学化想象，而是 `SCM` 内可计算的句子
  - 结构方程模型决定每个反事实语句的 truth value

对应知识库：

- [../modules/Counterfactual.md](../modules/Counterfactual.md)

### Tool 4. Mediation analysis

- 任务：分析因果效应通过哪条机制传递
- 典型目标：
  - direct effect
  - indirect effect
- 典型问题：
  - `X` 对 `Y` 的影响中，有多少是通过中介 `Z` 传递的

对应知识库：

- [../modules/MediationAnalysis.md](../modules/MediationAnalysis.md)

### Tool 5. Adaptability / external validity / sample selection bias

- 任务：研究结论能否跨环境迁移，以及环境变化、样本选择偏差如何修正
- 与机器学习的接口：
  - domain adaptation
  - transfer learning
  - robustness
  - policy transport
- 核心判断：
  - 仅看关联分布变化，不足以定位变化发生在哪个机制上
  - 需要因果模型来表达稳定机制与变化机制

对应知识库：

- [../modules/Transportability.md](../modules/Transportability.md)
- [../modules/DoCalculus.md](../modules/DoCalculus.md)

### Tool 6. Recovering from missing data

- 任务：在存在缺失的情况下，判断目标关系是否仍可恢复
- 关键转变：
  - 从“缺失类型标签”转向“缺失机制建模”
- 核心判断：
  - recoverability 是识别问题，不只是插补问题

对应知识库：

- [../modules/MissingDataRecovery.md](../modules/MissingDataRecovery.md)

### Tool 7. Causal discovery

- 任务：从数据反推出与数据兼容的因果结构或等价类
- 文中提到的三类代表思路：
  - 基于图模型可检验蕴含的结构筛选
  - 基于非高斯性的方向识别
  - 基于环境 shocks 的自然干预识别

对应知识库：

- [../modules/CausalDiscovery.md](../modules/CausalDiscovery.md)

## 7. 这篇文章在当前知识库中的意义

这篇文章最重要的作用，不是提供一个局部公式，而是提供一个“总视角”：

- `SCM` 是统一对象
- 三层层级定义了问题边界
- 七个工具定义了在该边界内可以系统完成的任务类型

因此，它特别适合作为：

- `SCM` 之后的总览文章
- 从“基础概念”过渡到“具体工具”的桥接节点
- 理解因果推断与现代机器学习差异的高层入口

## 8. 对机器学习的直接启示

- 当前机器学习系统大多擅长 Association 层
- 若目标涉及：
  - 干预决策
  - 机制解释
  - 反事实分析
  - 环境迁移
  - 缺失恢复
  - 结构发现
  则必须超出纯统计关联层

这也是为什么这篇文章经常被用作：

- 因果机器学习入门综述
- OOD 泛化 / robustness 研究的理论背景
- 反事实解释和机制解释工作的语义入口

## 9. 建议阅读顺序

- 先读 [../structures/StructuralCausalModel.md](../structures/StructuralCausalModel.md)，尤其是其中关于 `SCM` 作为推理引擎的部分
- 再读 [../modules/Intervention.md](../modules/Intervention.md)
- 然后读 [../modules/DSeparation.md](../modules/DSeparation.md) 与 [../modules/BackdoorCriterion.md](../modules/BackdoorCriterion.md)
- 再进入 [../modules/DoCalculus.md](../modules/DoCalculus.md) 与 [../modules/Counterfactual.md](../modules/Counterfactual.md)
- 最后扩展到：
  - [../modules/MediationAnalysis.md](../modules/MediationAnalysis.md)
  - [../modules/Transportability.md](../modules/Transportability.md)
  - [../modules/MissingDataRecovery.md](../modules/MissingDataRecovery.md)
  - [../modules/CausalDiscovery.md](../modules/CausalDiscovery.md)

## 10. 一句话总结

- 这篇文章的地位在于：它不仅用“三层因果层级 + 七类工具”概括了 `SCM` 的能力边界，还把 `SCM` 明确表述为一个把 `Assumptions`、`Queries`、`Data` 映射到 `Estimand`、`Estimate` 与 `Fit indices` 的因果推理引擎。
