---
title: Q-learning
date: "2026-05-18"
categories:
  - 神经网络
  - 强化学习
note_type: algorithm
data_regime:
  - online
policy_relation:
  - off-policy
optimization_object:
  - value
algorithm_family:
  - value-based
description: 基于贝尔曼最优方程的无模型值迭代算法，用 TD 更新 Q 并隐含优化策略。
---
# Q-learning  DQN

## 1. 要点
### 动作-梯度回传失败的推导
如果直接maxG（theta）
$$  
G(\theta)=r(s,a_\theta(s))  
$$

$$
a_\theta(s)=\arg\max_a Q_\theta(s,a)  
$$
$$  
z_\theta(s)=Q_\theta(s,\cdot)  
$$
链式展开
$$
\frac{\partial G}{\partial a_\theta}  
\frac{\partial a_\theta}{\partial z_\theta}  
\frac{\partial z_\theta}{\partial \theta}  
$$
- 环境-动作梯度 
	- 没有联系
- 动作-选择梯度
	- Q-learning 选择函数：argmax(Q(s,a)) ---a 不是通过policy-gredient采样
	- 导致断开



Q-learning 用 TD 误差更新 Q，逼近最优 Q*，策略直接取 argmax Q。


### 经验回放（Replay Buffer）
- 把转移 (s, a, r, s') 存进 buffer，训练时随机采样
- 打破样本间的时序相关性，稳定训练

### 目标网络（Target Network）
- 维护一份冻结的 Q_target，定期从 Q_current 复制
- TD target 用 Q_target 算，减少 bootstrap 的目标抖动
DQN 有两个 Q 网络：Qθ−​(s,a) Qθ​(s,a)
DQN 的 TD target 是：
$$  
Q_{\theta^-}(s,a)  
$$
其中，$\theta$ 是当前正在被梯度更新的参数，$\theta^-$ 是目标网络的参数。目标网络不会每一步都更新，而是每隔一段时间从当前网络复制一次：  
  
$$  
\theta^- \leftarrow \theta  
$$  
  
DQN 的 TD target 写成：  
  
$$  
y = r + \gamma \max_{a'} Q_{\theta^-}(s', a')  
$$
- 隔一段时间更新Qtheta-


## 2. 更新规则

$$
Q(s_t, a_t) \leftarrow Q(s_t, a_t) + \alpha \left[ r_t + \gamma \max_a Q(s_{t+1}, a) - Q(s_t, a_t) \right]
$$

其中 r + γ max Q(s', a') 是 TD target，与 target 的差是 TD 误差。

## 3. 设计逻辑

Q-learning 是在无模型条件下近似 Bellman 最优方程：
$$
Q^{*}(s,a) = \mathbb{E}_{s'} \left[ r + \gamma \max_{a'} Q^{*}(s',a') \mid s,a \right]
$$

- 右边用单步采样 r + γ max Q(s',a') 代替期望 → 有偏但方差小
- off-policy策略 
- 策略梯度不行：隐式策略由 Q 定义：$\pi_{\theta}(a|s) = \mathbb{1}[a = \arg\max_b Q_{\theta}(s,b)]$ 指示函数输入为Q
	- argmaxbQ(s,b) / Q(s,b) 无法求解

## 4. 关键点
- 行为策略和目标策略分离 → off-policy
- 更新目标有 max → 会高估 Q（overestimation bias）
- 表格型，状态动作空间有限时保证收敛


## 5. 离线数据下的额外风险

标准 Q-learning 假设能够持续与环境交互。若只在固定数据集 $\mathcal D$ 上训练，target 中的

$$
\max_{a'}Q(s',a')
$$

可能选中数据集从未覆盖的 $a'_{\mathrm{OOD}}$。神经网络对该动作的 Q 没有监督，虚假高值会被 $\max$ 选中并通过 bootstrap 传播。

| 问题 | 原因 | 常见处理 |
|---|---|---|
| Maximization bias | 对带噪 Q 估计取最大值 | Double DQN、双 Q |
| Offline extrapolation error | 查询数据支持域外的 $(s,a)$ | CQL、IQL、behavior regularization |

> [!warning]
> “更新目标有 max，所以会高估 Q”不能完整概括 offline RL 的 OOD 问题。前者不要求动作在数据集外；后者的核心是 learned policy 与 dataset support 发生分布偏移。

详见 [[ActionOODAndExtrapolationError|Action OOD 与价值外推误差]]、[[CQL]] 和 [[IQL]]。


## 6. 参考
- [[BellmanEquation]]：Q* 最优方程
- [[TemporalDifference]]：TD 更新
- [[神经网络/modules/rl/优化方法/训练范式/OfflineRL|离线强化学习总览]]
