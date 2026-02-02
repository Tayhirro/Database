# 强化学习（Reinforcement Learning）

导航：[paradigms/README.md](README.md)

---

## 一句话

通过与环境交互获取奖励信号来优化策略的学习范式。

---

## 严格定义

强化学习 (RL)：智能体在环境中采取行动，根据奖励信号调整策略以最大化累积回报的学习框架。

$$
\max_\theta \mathbb{E}_{\tau \sim \pi_\theta} \left[ \sum_{t=0}^{T} \gamma^t r_t \right]
$$

其中：
- $\pi_\theta$：参数为 $\theta$ 的策略
- $\tau$：轨迹 $(s_0, a_0, r_0, s_1, \ldots)$
- $r_t$：时刻 $t$ 的奖励
- $\gamma$：折扣因子

---

## 接口

**输入**：
- 状态空间 $\mathcal{S}$
- 动作空间 $\mathcal{A}$
- 奖励函数 $R: \mathcal{S} \times \mathcal{A} \to \mathbb{R}$

**输出**：
- 策略 $\pi: \mathcal{S} \to \mathcal{A}$

---

## 在 LLM 中的应用

| 应用场景 | 状态 | 动作 | 奖励 |
|----------|------|------|------|
| RLHF | 对话历史 | 生成 token | Reward Model 评分 |
| Reasoning | 推理过程 | 下一步推理 | 结果正确性 |
| Agentic | 任务状态 | 工具调用 | 任务完成度 |

---

## 常用算法

- [PPO](../post-training/alignment/PPO.md)：Proximal Policy Optimization
- [GRPO](GRPO.md)：Group Relative Policy Optimization
- [REINFORCE](REINFORCE.md)：基础策略梯度
- [A2C](A2C.md)：Advantage Actor-Critic

---

## 关系

- 上级：[Paradigms](README.md)
- 应用于：[Alignment](../post-training/alignment/)、[Reasoning](../post-training/reasoning/)、[Agentic](../post-training/agentic/)
