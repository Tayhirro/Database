---
title: 时序差分（Temporal Difference）
date: "2026-05-14"
categories:
  - 神经网络
  - 强化学习
description: 用一步奖励和下一个状态的估计更新价值函数，有偏但方差小。
---

# 时序差分（Temporal Difference）

## 1. 一句话
- 用一步奖励 $r_{t+1}$ 和下一个状态的估计 $\gamma V(s_{t+1})$ 更新价值函数。

## 2. 先明确：我们在估计什么

$G_t$ 是一条轨迹的累积回报，它是**随机变量**——每次采出来的值都不同，没法直接"估计"一个随机变量本身。

我们能估计的是它的**期望**：$V(s) = \mathbb{E}[G_t \mid s_t = s]$。期望是一个确定的函数，不是随机变量。MC 和 TD 都是估计这个期望的方法，只是逼近方式不同。0

## 3. 定义

**TD目标**：
$$\hat{v}_t = r_{t+1} + \gamma V(s_{t+1})$$

**TD误差**：
$$\delta_t = r_{t+1} + \gamma V(s_{t+1}) - V(s_t)$$

**更新规则**：
$$V(s_t) \leftarrow V(s_t) + \alpha \delta_t$$

## 4. 分类：critic 学 V 还是 Q

TD 只是更新规则，真正决定算法形态的是**价值函数学 V 还是 Q**。整棵分类树（括号里标注 actor 有无）：

```
critic 学什么？
├─ V 路线：学 V(s)
│    └─ V-critic actor-critic：A2C / A3C / PPO（有 actor，全 on-policy）
└─ Q 路线：学 Q(s,a)
     ├─ on-policy：SARSA（无 actor，target 用行为策略实际选的 a'）
     └─ off-policy：target 用 max（学 Q*）—— max 怎么算？
          ├─ 离散动作：直接枚举 → Q-learning / DQN（无 actor）
          └─ 连续动作：max 枚举不了，两条路
               ├─ actor 网络逼近 argmax → DDPG / TD3 / SAC（有 actor）
               └─ 不用 actor，用 V(s') 绕开 → AFU / MODIP
```

### 4.1 前置：贝尔曼方程的两个版本

贝尔曼期望方程有两个版本，都是**精确等式**：

**V 版本**（对状态价值）：
$$V(s) = \mathbb{E}_\pi[r_{t+1} + \gamma V(s_{t+1}) \mid s_t = s]$$

**Q 版本**（对动作价值）：
$$Q(s,a) = \mathbb{E}[r_{t+1} + \gamma Q(s_{t+1}, a_{t+1}) \mid s_t=s, a_t=a]$$

两者的关系：$$V(s) = \sum_a \pi(a|s)\,Q(s,a)$$，即 V 是 Q 对动作取策略期望。

TD 的核心思想相同——用单次采样代替期望，用当前估计值 bootstrap——选哪个版本的 Bellman 方程，就走 V 路线还是 Q 路线。

---

### 4.2 V 路线：critic 学 V(s) —— A2C / A3C / PPO

定位：这条线**全是 actor-critic + on-policy**，和连续/离散无关；actor 是策略本身，critic 学 V 只为算 advantage。

用 V 版本的 Bellman 方程，critic 只学 $$V(s)$$，全程不涉及 Q。

**TD target**：
$$\hat{v}_t = r_{t+1} + \gamma V(s_{t+1})$$

**TD 误差**：
$$\delta_t = r_{t+1} + \gamma V(s_{t+1}) - V(s_t)$$

**critic 更新**：$$V(s_t) \leftarrow V(s_t) + \alpha\,\delta_t$$

**actor 更新**：用 $$\delta_t$$ 当 advantage（不需要单独学 Q）

推导：$$A(s_t, a_t) = Q(s_t, a_t) - V(s_t)$$，而 Bellman 告诉我们 $$Q(s_t, a_t) = \mathbb{E}[r_{t+1} + \gamma V(s_{t+1}) \mid s_t, a_t]$$，代入得：

$$A(s_t, a_t) = \underbrace{r_{t+1} + \gamma V(s_{t+1})}_{Q \text{ 的单步近似}} - \underbrace{V(s_t)}_{\text{基线}} = \delta_t$$

- $$\delta_t > 0$$：这步比预期好，actor 增大 $$a_t$$ 的概率
- $$\delta_t < 0$$：这步比预期差，actor 减小 $$a_t$$ 的概率
- 一个 V 网络同时服务 critic 和 actor

---

### 4.3 Q 路线：critic 学 Q(s,a)

Q 路线依次问两个问题：

1. **target 里保不保留行为策略的实际选择？** → 决定 on / off-policy
2. **若是 off-policy：$$\max_{a'} Q(s',a')$$ 怎么算？** → 离散直接枚举；连续只能逼近或绕开

#### 4.3.1 先明确：on-policy vs off-policy

强化学习里有两个角色：

- **行为策略**（behavior policy）：实际用来探索、收集数据的策略，比如 epsilon-greedy
- **目标策略**（target policy）：你在学它的价值函数的那个策略

**on-policy** = 这两个是同一个。你用什么策略探索，就学什么策略的价值。

**off-policy** = 这两个可以不一样。你一边用 epsilon-greedy 瞎探索，一边学另一个策略（比如 greedy 最优策略）的价值。

这和估 V 还是估 Q **无关**——V 和 Q 都可以是 on 或 off：

|     | on-policy                           | off-policy |
| --- | ----------------------------------- | ---------- |
| 估 V | A2C/A3C（用当前 $$\pi$$ 的数据学 $$V^\pi$$） | 较少见，但理论上可以 |
| 估 Q | SARSA（用当前 $$\pi$$ 的数据学 $$Q^\pi$$）   | Q-learning 系（见下） |

#### 4.3.2 on-policy：SARSA

target 里**保留行为策略的实际选择**：

$$y_t = r + \gamma Q(s', a'), \quad a' \text{ 由行为策略（如 } \epsilon\text{-greedy）实际选出}$$

学的是当前策略的 $$Q^\pi$$：ε-greedy 瞎选了差动作，target 就老实反映差结果（数值例子见 4.6）。

#### 4.3.3 off-policy：target 用 max —— 但 max 怎么算？

$$y_t = r + \gamma \max_{a'} Q(s', a')$$

max 一律看最好的，把行为策略从 target 里彻底去掉：用 ε-greedy 乱探索，学的却是 greedy 最优策略 $$Q^*$$ → **天生 off-policy**。

于是剩下唯一的问题：**$$\max_{a'} Q(s',a')$$ 怎么算？** 按动作空间分叉：

**A. 离散动作：直接枚举 —— Q-learning / DQN**

critic 学 $$Q(s,a)$$，target 里用 $$\max$$ 选最优动作。

**TD target**：
$$y_t = r_{t+1} + \gamma \max_{a'} Q_{\bar{\omega}}(s_{t+1}, a')$$

**更新**：
$$Q_\omega(s_t, a_t) \leftarrow Q_\omega(s_t, a_t) + \alpha\left[y_t - Q_\omega(s_t, a_t)\right]$$

- 没有 actor，直接用 $$\arg\max_a Q(s,a)$$ 作为策略
- replay buffer 里随便拿一条 $$(s, a, r, s')$$，不管行为策略是什么，都能更新 $$Q(s,a)$$——这就是 Q-learning 天生 off-policy 的原因

**B. 连续动作：max 枚举不了 —— 两条路**

Q-learning 的最优 Q 满足：

$$Q^*(s,a) = r(s,a) + \gamma\,\mathbb{E}_{s'}\!\left[\max_{a'} Q^*(s', a')\right]$$

**问题只在：连续动作空间里 $$\max_{a'} Q(s', a')$$ 算不了**（无穷多个动作，没法枚举）。

**路 1：actor 网络逼近 argmax —— DDPG / TD3 / SAC（Q-critic actor-critic）**

DDPG 的解法：$$\max$$ 不好直接算？那训练一个 actor $$\mu_\theta(s)$$ 让它逼近 $$\arg\max_a Q(s,a)$$：

$$\max_a Q(s', a) \approx Q(s',\; \mu_\theta(s'))$$

所以 DDPG 本质上就是 **Q-learning 思想扩展到连续动作域** + **deterministic policy gradient 的 actor-critic 结构**。actor 存在的意义就是替 critic 解决那个 max。

SAC 也是类似思路，保留 $$Q(s,a)$$（因为 actor 需要知道具体哪个 action 好），但用 stochastic policy + entropy regularization 做 soft policy improvement，本质上是 off-policy stochastic actor-critic。

**具体更新流程**：

拿一条 transition $$(s_t, a_t, r_t, s_{t+1})$$：

1. 当前 Q 网络算：$$Q_\omega(s_t, a_t)$$
2. actor 给出下一步动作：$$a_{t+1} = \pi_\theta(s_{t+1})$$
3. target Q 网络（冻结参数 $$\bar{\omega}$$）算：$$Q_{\bar{\omega}}(s_{t+1}, a_{t+1})$$
4. target：$$y_t = r_t + \gamma\, Q_{\bar{\omega}}(s_{t+1}, \pi_\theta(s_{t+1}))$$
5. critic 更新：最小化 $$(Q_\omega(s_t, a_t) - y_t)^2$$
6. actor 更新：直接最大化 $$Q_\omega(s, \pi_\theta(s))$$，梯度从 Q 回传给 actor

和 V 路线（4.2）的区别：这里**需要 $$a_{t+1}$$**（由 actor 给出），全程都是 Q，不需要 V。

**代价：actor 和 critic 耦合成闭环**

**方向一：Actor 依赖 Critic（$$Q \to \mu$$）**

actor 怎么知道动作好不好？靠 critic。假设当前 actor 输出 $$a=0.2$$，critic 告诉它 $$\frac{\partial Q}{\partial a} > 0$$（"动作增大一点，价值更高"），actor 就往 $$0.2 \to 0.3$$ 更新。actor 的梯度是：

$$\nabla_\theta J \approx \mathbb{E}_s\left[\nabla_a Q_\omega(s,a)\big|_{a=\mu_\theta(s)} \cdot \nabla_\theta \mu_\theta(s)\right]$$

**方向二：Critic 依赖 Actor（$$\mu \to Q$$）**

critic 的 TD target 里明确调用了 actor：

$$y_t = r_t + \gamma\, Q_{\bar{\omega}}(s_{t+1},\; \mu_{\bar{\theta}}(s_{t+1}))$$

于是完整闭环：$$Q \to \mu \to Q \to \mu \to \cdots$$

**耦合为什么危险？——两个方向的误差传播**

actor 没学好时：假设 $$s'$$ 下真实最优动作是"变道"（$$Q^*=10$$），但 actor 还只会输出"直行"（$$Q=3$$）。critic target 变成 $$y = r + \gamma \times 3$$，而理想应该是 $$r + \gamma \times 10$$。**actor error → critic target error**。

critic 学歪时：真实 $$Q^*(s, 0.4)=10,\; Q^*(s, 0.8)=2$$，但 critic 错误预测 $$Q_\omega(s, 0.8)=20$$。actor 最大化 critic，被吸向 $$a=0.8$$（利用 critic 漏洞）。critic 下一轮 target 又调用这个跑偏的 actor，错误继续 bootstrap。**critic error → actor 跑偏 → critic 继续错**。

**路 2：不用 actor，用 V(s') 绕开 —— AFU / MODIP**

DDPG 的 critic target 需要：$$s' \to \pi(s') \to a' \to Q(s', a')$$，每一步构造 target 都要问 actor。如果 actor 是 Diffusion Policy（MODIP 场景），还要完整跑多步去噪 $$x_K \to x_{K-1} \to \cdots \to x_0 = a'$$，又贵又容易传错。

AFU / MODIP 改成直接用 V：

$$y_t = r_t + \gamma\, V(s')$$

把依赖链从 $$s' \to \pi \to a' \to Q(s',a')$$ 缩短为 $$s' \to V(s')$$，**不再查询 actor**，critic 的 bootstrap 和 actor 彻底解耦。

**off-policy 估 Q 三代做法小结**：

| 方法               | target 写法                            | 需要 actor 给 $$a_{t+1}$$？ | 适用场景                   |
| ---------------- | ------------------------------------ | ----------------------- | ---------------------- |
| Q-learning / DQN | $$r + \gamma \max_{a'} Q(s', a')$$   | 不需要（离散动作直接枚举 max）       | 离散动作空间                 |
| DDPG / TD3 / SAC | $$r + \gamma Q(s', \pi_\theta(s'))$$ | **需要**（actor 近似 max）    | 连续动作空间，critic-actor 耦合 |
| AFU              | Q-critic 但不依赖 actor                  | 不需要（回归+条件梯度缩放）          | 连续动作空间，critic-actor 解耦 |

一句话：离散动作能直接 max 所以不需要 actor（DQN）；连续动作没法枚举，DDPG 系只能让 actor 代劳，AFU 绕开了这个限制。

---

### 4.4 同一步对比：V-critic vs Q-critic

transition：$$(s_3,\; \text{右},\; r=1,\; s_4)$$，$$\gamma = 0.9$$

| | V-critic（A2C） | Q-critic（DDPG） |
|---|---|---|
| critic 当前输出 | $$V(s_3)=5,\; V(s_4)=8$$ | $$Q(s_3, \text{右})=5$$ |
| target 怎么算 | $$1 + 0.9 \times V(s_4) = 8.2$$ | actor 给 $$a'=\pi(s_4)=\text{上}$$，$$1 + 0.9 \times Q(s_4, \text{上}) = 8.2$$ |
| critic 更新 | $$V(s_3) \leftarrow 5 + \alpha \times 3.2$$ | 最小化 $$(5 - 8.2)^2$$ |
| actor 更新 | $$\delta_t = 3.2 > 0$$，增大"右"概率 | 最大化 $$Q(s, \pi(s))$$，梯度回传 |

数值上 target 碰巧一样（因为这个例子里 $$Q(s_4, \text{上}) = V(s_4) = 8$$），但**计算路径不同**：左边直接查 V，右边要先问 actor 下一步干什么、再查 Q。

---

### 4.5 总表：算法挂在哪

| 算法 | actor | critic | TD target | on/off | max 怎么算 |
| --- | --- | --- | --- | --- | --- |
| A2C / A3C | ✓ | V | $$r + \gamma V(s')$$ | on | —（V 没有 max） |
| PPO | ✓ | V | $$r + \gamma V(s')$$（GAE 多步） | on | — |
| SARSA | ✗ | Q | $$r + \gamma Q(s', a')$$（$$a'$$ 来自行为策略） | on | 不用 max |
| Q-learning / DQN | ✗ | Q | $$r + \gamma \max_{a'} Q(s', a')$$ | off | 离散：枚举 |
| DDPG | ✓ | Q | $$r + \gamma Q(s', \pi(s'))$$ | off | 连续：actor 逼近 |
| TD3 | ✓ | Q×2 | $$r + \gamma \min(Q_1, Q_2)(s', \pi(s'))$$ | off | 连续：actor 逼近 |
| SAC | ✓ | Q×2 | $$r + \gamma[\min(Q_1, Q_2)(s', a') - \alpha \log \pi(a' \| s')]$$ | off | 连续：actor 逼近（soft） |

---

### 4.6 具体例子：Q-learning vs SARSA

状态 $$s_3$$，三个动作，当前 Q 表：

| $$Q(s_3, \cdot)$$ | 值 |
|---|---|
| 上 | 3 |
| 左 | 7 |
| 右 | 2 |

行为策略 epsilon-greedy 选了**左**，得到 $$r=1$$，转移到 $$s_4$$。$$s_4$$ 的 Q 表：

| $$Q(s_4, \cdot)$$ | 值 |
|---|---|
| 上 | 8 |
| 左 | 4 |
| 右 | 6 |

$$\gamma = 0.9$$。

**Q-learning（off-policy）**：

$$y_t = r + \gamma \max_{a'} Q(s_4, a') = 1 + 0.9 \times \max\{8, 4, 6\} = 1 + 0.9 \times 8 = 8.2$$

不管行为策略选了什么，$$\max$$ 永远看最好的。它在学"如果以后都用最优策略，能拿多少"。

**SARSA（on-policy）**：

行为策略到 $$s_4$$ 后，如果 epsilon-greedy 选到了"上"（和 greedy 一样）：

$$y_t = 1 + 0.9 \times Q(s_4, \text{上}) = 8.2$$

碰巧一样。但如果 epsilon 随机选到了"左"：

$$y_t = 1 + 0.9 \times Q(s_4, \text{左}) = 1 + 0.9 \times 4 = 4.6$$

差很多。SARSA 老实反映行为策略的实际选择——你瞎选了差动作，target 就体现这个差结果。

**核心区别**：到了 $$s_4$$ 后，Q-learning 一律用 $$\max$$ 看最好的（学最优策略 $$Q^*$$），SARSA 用行为策略实际选的（学当前策略 $$Q^\pi$$）。

Q-learning 能 off-policy 就是因为 $$\max$$ 把行为策略从 target 里彻底去掉了——你在用 epsilon-greedy 乱探索，但 target 假装你在用 greedy 最优策略。


## 5. 特点

- 有偏估计：因为 $V(s_{t+1})$ 本身还没学准（bootstrap），target 有系统偏差
- 方差小：只用一步奖励，比 MC（整条轨迹累积）噪声小得多
- 不需要等 episode 结束就能更新（online learning）

## 6. 固定离线数据下的局限

TD 的 bootstrap 本身不是 OOD 问题，但它会放大错误。如果 target 查询的数据外动作被高估：

$$
Q(s',a_{\mathrm{OOD}})\text{ 偏高}
\rightarrow
y=r+\gamma Q(s',a_{\mathrm{OOD}})\text{ 偏高}
\rightarrow
Q(s,a)\text{ 被带偏}.
$$

在线训练可以执行该动作并获得新反馈；离线训练无法验证它，因此需要 CQL、IQL 或策略分布约束。详见 [[ActionOODAndExtrapolationError|Action OOD 与价值外推误差]]。

## 7. 变体

- TD(0)：只看一步
- TD($\lambda$)：看多步，用 $\lambda$ 控制权重
- Q-learning：用TD更新Q

## 8. 相关模块

- [ValueFunction.md](../价值函数/ValueFunction.md)：价值函数定义
- [GeneralizedAdvantageEstimation.md](GeneralizedAdvantageEstimation.md)：GAE
- [[神经网络/modules/rl/优化方法/训练范式/OfflineRL|离线强化学习总览]]：固定数据下的分布偏移与处理方法
