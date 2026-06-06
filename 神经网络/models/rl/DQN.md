---
title: DQN（Deep Q-Network）
date: "2026-03-25"
categories:
  - 神经网络
description: 用神经网络逼近动作价值函数 Q(s,a)，配合经验回放与稳定化技巧，让 Q-learning 能处理高维输入。
---
# DQN（Deep Q-Network）

## 1. 一句话
- DQN 就是用神经网络近似动作价值函数 $Q(s,a)$，再用 Q-learning 的 TD 目标更新它，从而在离散动作空间里学会“当前状态下该做哪个动作”。

## 2. 目标（解决什么问题）
- 传统表格型 Q-learning 只能处理小状态空间。
- 当状态是图像、传感器、多维特征时，没法给每个 $(s,a)$ 单独存一张表。
- DQN 的思路是：
  - 用网络 $Q_\theta(s,a)$ 代替 Q 表
  - 让网络自己从状态里提特征
  - 继续沿用 Q-learning 的“bootstrap + TD target”思路

## 3. 核心结构

### 3.1 网络输出什么
- 输入：状态 `s`
- 输出：当前状态下每个离散动作的 Q 值

```text
state s
  -> Q-network
  -> [Q(s,a1), Q(s,a2), ..., Q(s,aK)]
```

- 然后选动作：

$$
a_t = \arg\max_a Q_\theta(s_t,a)
$$

- 训练时一般还会配 $\epsilon$-greedy：
  - 以概率 $\epsilon$ 随机探索
  - 以概率 $1-\epsilon$ 选当前最大 Q 的动作

### 3.2 两个最关键的工程件
- **Experience Replay**：
  - 把转移 `(s,a,r,s')` 存到 replay buffer
  - 每次随机采样 mini-batch 来训练
  - 作用：打散时序相关性，提升样本利用率
- **Target Network**：
  - 维护一个较慢更新的目标网络 $Q_{\theta^{-}}$
  - 计算 TD target 时用它，而不是直接用当前在线网络
  - 作用：降低“目标也在动”导致的不稳定

## 4. 损失 / 训练目标

### 4.1 标准 TD target
- 对非终止状态，目标通常写成：

$$
y = r + \gamma \max_{a'} Q_{\theta^{-}}(s', a')
$$

- 如果 $s'$ 是终止状态，则常见写法是：

$$
y = r
$$

### 4.2 DQN 的回归损失

$$
L(\theta)=\mathbb{E}\left[\left(Q_\theta(s,a)-y\right)^2\right]
$$

- 本质上它在做一件事：
  - 当前网络对 $Q(s,a)$ 的预测
  - 要去逼近“奖励 + 下一步最优价值”的 bootstrap 目标

### 4.3 为什么它不是监督学习里的固定标签
- $y$ 不是人工标注，而是由另一个 Q 估计拼出来的
- 所以 DQN 是典型的 **bootstrapping**
- 这也是 RL 难训的原因之一：你在追一个由自己估出来的目标

## 5. 训练流程（伪代码）

```python
initialize online_q(theta)
initialize target_q(theta_target = theta)
initialize replay_buffer

for each step:
    with probability eps:
        a = random_action()
    else:
        a = argmax_a online_q(s, a)

    s_next, r, done = env.step(a)
    replay_buffer.add(s, a, r, s_next, done)
    s = s_next

    batch = replay_buffer.sample()

    y = r + gamma * (1 - done) * max_a' target_q(s_next, a')
    loss = mse(online_q(s, a), y)
    update theta

    every C steps:
        theta_target <- theta
```

## 6. 为什么有效（直觉）
- $Q(s,a)$ 学的是：现在做这个动作，未来总回报大概有多少
- 一旦 Q 学准了，策略就很简单：
  - 在每个状态选 Q 最大的动作就行
- DQN 的关键不是“网络很深”，而是把：
  - 值函数近似
  - off-policy Q-learning
  - replay buffer
  - target network
 组合到了一起，才让高维输入下的 Q-learning 勉强稳定起来

## 7. 常见坑 & Debug 清单
- **Q 值爆炸 / 发散**：学习率过大、target 更新太快、奖励尺度失衡
- **过估计偏差**：$\max_a Q(s',a)$ 容易高估，后续常用 Double DQN 缓解
- **探索不足**：$\epsilon$ 衰减太快，前期就陷入局部最优
- **replay buffer 太小**：样本相关性强，训练不稳
- **只适合离散动作**：
  - 如果动作是连续的，$\arg\max_a Q(s,a)$ 很难直接做
  - 这也是很多连续控制算法转向 actor-critic 或 diffusion policy 的原因

## 8. 它和 Guided Diffusion 的关系
- DQN 是 **value-based RL**：先学 $Q(s,a)$，再直接挑最大值动作
- guided diffusion 是 **generative + guidance**：先生成动作 / 轨迹，再用 reward 或 $Q$ 去引导采样
- 你可以把两者关系先理解成：
  - DQN 提供“哪个动作更好”的评价信号
  - diffusion 提供“如何表示复杂动作分布”的生成机制

> [!note]
> 在离散动作问题里，DQN 直接做 $\arg\max$ 很自然；在连续动作问题里，更常见的是“DQN 风格的 Q-learning / critic”去引导 diffusion，而不是把经典 DQN 原样套上去。

## 9. 关联笔记
- MDP：[../../modules/rl/MarkovDecisionProcess.md](../../modules/rl/MarkovDecisionProcess.md)
- 价值函数：[../../modules/rl/ValueFunction.md](../../modules/rl/ValueFunction.md)
- Guided Diffusion：[../GuidedDiffusion.md](../GuidedDiffusion.md)
- PPO：[PPO.md](PPO.md)

## 10. 参考
- Mnih et al., 2013. *Playing Atari with Deep Reinforcement Learning*
- Mnih et al., 2015. *Human-level Control through Deep Reinforcement Learning*
- Hasselt et al., 2016. *Deep Reinforcement Learning with Double Q-learning*
