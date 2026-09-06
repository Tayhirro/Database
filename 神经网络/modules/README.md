---
title: Modules 组织说明
date: "2026-03-25"
categories:
  - 神经网络
description: modules 用来放会被多个模型复用的概念，但在 modules 内按主题域拆二级目录，避免平铺。
---
# Modules 组织说明

`modules/` 主要放那些会被多个模型反复用到的共用概念。

所以这几类都可以放在 `modules/`：
- 训练目标与训练机制：`Loss`、`Optimizer`
- 概率与变分推断底座：`ELBO`、`KL`、`Reparameterization Trick`
- 表示学习范式：`Contrastive Learning`、`JEPA`、`Metric Learning`
- 强化学习框架：`MDP`、`Policy Gradient`、`Actor-Critic`、`GAE`

这些模块再按主题域分到二级目录里：
- `foundations/`：归纳偏置、参数共享、局部性、等变性/不变性等上位概念
- `training/`：训练目标、初始化、优化器
- `probabilistic/`：潜变量、ELBO、KL、重参数化、GMM/HMM/FA
- `transformer/`：Attention、Tokenization
- `representation/`：对比学习、JEPA、度量学习、原型学习
- `sequence/`：RNN 概念入口
- `graph/`：图神经网络基础
- `rl/`：强化学习底座与框架

简单判断规则：
- 如果它是一张“某个具体模型/算法怎么工作的卡片”，放 `models/`
- 如果它是“多个模型都会依赖的共用概念”，放 `modules/<topic>/`
- 如果它更像“定义一个因果世界、干预语义、反事实语义的上位建模框架”（例如 `SCM`），优先单独建分支：`../../因果推断/README.md`
- 如果你拿不准，就问一句：以后会不会有 3 篇以上笔记都要链到它？如果会，优先放 `modules/`
