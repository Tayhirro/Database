---
title: "神经网络（Neural Networks）知识库组织说明（可扩展 & 速查）"
date: "2026-01-26"
categories:
  - 神经网络
description: 这部分建议按两层来组织：1) models 按“具体模型/算法卡片”组织；2) modules 按“可复用概念”组织，但在 modules 内再按主题域拆二级目录，避免平铺成大杂烩。
---
# 神经网络（Neural Networks）知识库组织说明（可扩展 & 速查）

这部分建议按两层来组织：  
1) **模型族（models）**：按“具体模型 / 算法卡片”组织，例如 AE / VAE / Diffusion / BERT / CLIP / PPO。  
2) **横切模块（modules）**：按“可复用概念”组织，例如 ELBO / Attention / Contrastive Learning / Actor-Critic。

注意：`modules` **不是按粒度分**，而是按“会不会被多个模型复用”来分。  
所以 `Contrastive Learning`、`Metric Learning`、`Actor-Critic` 这类东西继续留在 `modules` 是合理的；真正需要避免的是把它们全都平铺在同一层，导致目录像杂物间。

---

## 1. 目录结构（入口 → 索引 → 概念图 → 模块 → 模型 → 论文/实现/实验）
- `神经网络/README.md`：入口与组织方式（本页）
- `神经网络/神经网络索引.md`：层次化索引（中文｜英文｜一句话｜链接）
- `神经网络/概念图.md`：概念依赖图（“先学什么 → 再学什么”）

核心内容：
- `神经网络/modules/`：共用概念与工具箱，但按主题域拆二级目录，见 `神经网络/modules/README.md`
- `神经网络/models/`：模型卡片（目标、结构、损失、训练流程、坑点、扩展）
- `神经网络/models/rl/`：强化学习算法卡片（DQN / PPO / GRPO）

资料与落地：
- `神经网络/papers/`：论文笔记（建议命名：`YYYY - FirstAuthor - Title.md`）
- `神经网络/implementations/`：代码阅读/复现记录（仓库结构、关键模块、调用链）
- `神经网络/experiments/`：实验日志（一次一个问题，记录配置与结论）
- `神经网络/examples/`：最小工作例子（能跑通就行）
- `神经网络/exercises/`：自测题（按模块/模型组织）
- `神经网络/_assets/`：图片与附件

---

- AE / VAE / CVAE：`models/AE.md`、`models/VAE.md`、`models/CVAE.md`
- Diffusion：`models/Diffusion.md`、`models/GuidedDiffusion.md`
- BERT：`models/BERT.md`
- CLIP：`models/CLIP.md`
- RNN 变体（LSTM/GRU/BiRNN）：`models/rnn/README.md`

常用的底座模块：
- `modules/training/`：训练目标、初始化、优化器
- `modules/probabilistic/`：潜变量、ELBO、KL、重参数化
- `modules/transformer/`：Attention、Tokenization
- `modules/representation/`：对比学习、JEPA、度量学习、原型学习
- `modules/sequence/`：RNN 概念入口
- `modules/graph/`：图神经网络基础
- `modules/rl/`：MDP、价值函数、策略梯度、Actor-Critic、GAE
- VAE 系：`modules/probabilistic/ELBO.md`、`modules/probabilistic/KLDivergence.md`、`modules/probabilistic/ReparameterizationTrick.md`
- Transformer/BERT：`modules/transformer/Attention.md`、`modules/transformer/Tokenization.md`
- 序列建模：`modules/sequence/RecurrentNeuralNetwork.md`
- 图神经网络：`modules/graph/GraphAttentionNetwork.md`
- CLIP：`modules/representation/ContrastiveLearning.md`
- JEPA：`modules/representation/JEPA.md`
- few-shot / 度量学习：`modules/representation/MetricLearning.md`、`modules/representation/PrototypicalLearning.md`
- 训练优化：`modules/training/Initialization.md`、`modules/training/Loss.md`、`modules/training/Optimizer.md`
- 强化学习底座：`modules/rl/MarkovDecisionProcess.md`、`modules/rl/ValueFunction.md`、`modules/rl/PolicyGradient.md`、`modules/rl/ActorCritic.md`、`modules/rl/GeneralizedAdvantageEstimation.md`
- 强化学习算法：`models/rl/DQN.md`、`models/rl/PPO.md`、`models/rl/GRPO.md`

---

## 3. 建议填坑路线（按“依赖链最短”）
- 先把 `神经网络索引.md` 补齐：每次遇到新术语加一行
- 新增模块时，先决定它属于哪个主题域；不要再把所有模块平铺回 `modules/`
- AE → VAE（ELBO + KL + 重参数化）→ CVAE（条件变量怎么进模型）
- Attention → Transformer Encoder → BERT（预训练目标与微调套路）
- 对比学习（InfoNCE）→ CLIP（双塔 + 相似度矩阵 + 温度系数）
- JEPA：从“重建像素/对比样本”转向“用上下文预测目标表征”，适合作为非对比预测式自监督路线
- 度量学习：先补 `Metric Learning`，再看 Siamese / Triplet / 原型学习 / ArcFace 这些具体路线
- Diffusion：先抓住“前向加噪 + 反向去噪 + 预测噪声/score”三件事，再补 guided diffusion / Q-guidance

相关数学底座如果需要单独记：
- 线性代数：`../math/线性代数/README.md`
- 概率/信息论：你也可以在 `math/` 下新建一个概率目录，再从这里链接过去
- 因果推断：`../因果推断/README.md`（`SCM` / 干预 / 反事实 / backdoor）
