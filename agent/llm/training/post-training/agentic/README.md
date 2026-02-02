# Agent 能力训练（Agentic Training）

导航：[post-training/README.md](../README.md) | [索引.md](索引.md)

训练模型获得 Agent 能力（工具使用、环境交互）的方法。

---

## 定义

Agentic Training：通过与工具/环境的交互训练，使模型学会调用工具、执行多步任务的能力。

---

## 与 Tool Use 的区别

| 概念 | 阶段 | 说明 |
|------|------|------|
| **Agentic Training** | 训练时 | 教模型学会使用工具 |
| **Tool Use** | 推理时 | 已训练好的模型调用工具 |

---

## 条目列表

### 方法
- [ToolRL](ToolRL.md)：工具使用强化学习
- [EnvironmentRL](EnvironmentRL.md)：环境交互 RL
- [ImitationLearning](ImitationLearning.md)：模仿学习

### 代表工作
- [Search-R1](Search-R1.md)：搜索增强推理训练
- [Toolformer](Toolformer.md)：自监督工具学习
- [WebGPT](WebGPT.md)：网页浏览 + RLHF
- [ToolkenGPT](ToolkenGPT.md)：工具 token 嵌入

---

## 训练范式

Agentic Training 可使用多种范式：

| 范式 | 说明 | 例子 |
|------|------|------|
| SFT | 用工具调用数据微调 | Toolformer (部分) |
| RL | 环境奖励信号训练 | Search-R1, WebGPT |
| Imitation | 模仿专家轨迹 | 行为克隆 |

---

## 关系

- 上级：[Post-training](../README.md)
- 范式：[RL](../../paradigms/ReinforcementLearning.md)、[SL](../../paradigms/SupervisedLearning.md)
- 交叉引用：[agent/tool/](../../../../tool/)（工具使用模块）
