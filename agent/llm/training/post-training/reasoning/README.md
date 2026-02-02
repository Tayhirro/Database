# 推理训练（Reasoning）

导航：[post-training/README.md](../README.md) | [索引.md](索引.md)

增强模型推理能力的训练方法。

---

## 定义

Reasoning Training：通过特定数据或强化学习训练模型进行多步推理、自我验证的能力。

---

## 条目列表

### 方法
- [ReasoningRL](ReasoningRL.md)：推理强化学习
- [ProcessRewardModel](ProcessRewardModel.md)：过程奖励模型 (PRM)
- [OutcomeRewardModel](OutcomeRewardModel.md)：结果奖励模型 (ORM)
- [SelfPlay](SelfPlay.md)：自博弈
- [MCTS-RL](MCTS-RL.md)：蒙特卡洛树搜索 + RL

### 代表工作
- [DeepSeek-R1](DeepSeek-R1.md)
- [OpenAI-o1](OpenAI-o1.md)
- [Qwen-QwQ](Qwen-QwQ.md)

---

## 关系

- 上级：[Post-training](../README.md)
- 前置：[SFT](../sft/)、[Alignment](../alignment/)（可选）
- 范式：[Reinforcement Learning](../../paradigms/ReinforcementLearning.md)
- 相关：[Planning/CoT](../../../../planning/decomposition/ChainOfThought.md)
