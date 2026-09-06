---
title: 强化学习优化方法索引
date: "2026-08-09"
categories:
  - 神经网络
  - 强化学习
aliases:
  - RL 优化方法
note_type: index
description: 用数据范式、策略关系和优化对象三条独立标签组织强化学习算法。
---

# 优化方法

## 这个文件夹里有什么

| 子目录 / 文件 | 装什么 |
|---|---|
| `算法/` | 具体算法笔记:目标函数、训练流程、结论 |
| `训练范式/` | 组织训练数据的讨论:在线 vs 离线、on-policy vs off-policy |
| `问题与约束/` | 训练中遇到的问题,目前是 OOD(数据外偏移) |
| `README.md` | 索引:算法怎么分类、从哪查起 |

## 最常见的疑问:为什么不按"在线 / 离线"分文件夹?

因为算法身上的属性不是一条线,而是三个互相独立的维度:

1. **数据范式** — 训练时还能不能继续从环境拿数据?
   `online`(能) / `offline`(固定数据集) / `offline-to-online`(先离线后在线)
2. **策略关系** — behavior policy 和目标 policy 是不是同一个?
   `on-policy`(是) / `off-policy`(不是)
3. **优化对象** — 直接更新的是哪个,策略 π、价值 Q/V,还是两者?

同一篇算法通常同时占好几个属性。举例:

- **IQL**:`offline` + `off-policy` + 同时优化价值和策略。
- **CQL**:`offline` + `off-policy` + 只优化价值(策略靠 Q 隐式决定)。
- **Actor-Critic**:在线离线、on/off-policy 都行,价值和策略都更新。

如果按"离线算法"建一个文件夹,那 Actor-Critic 这种哪边都沾的放哪?按"值优化"建文件夹,IQL 既是值优化又是策略优化,又该放哪?强行分文件夹的结果只能是同一篇笔记复制几份,或者放一堆互相跳转的链接。所以——**这三个维度不做成文件夹层级,而是做成贴在每个算法笔记上的标签。**

## 标签记在哪里

每篇算法笔记开头都有 frontmatter,就是开头那两行 `---` 之间的内容。以 IQL 为例:

```yaml
---
data_regime: offline          # 数据范式
policy_relation: off-policy   # 策略关系
optimization_object:          # 优化对象
  - value
  - policy
---
```

`算法/` 下每篇笔记都有这套标签,取值是统一的(比如 `offline-to-online`、`conservative-value-learning` 这种固定写法)。在 Obsidian 里直接用属性面板按这些字段筛选,或写一行 Dataview 查询就能列出"所有离线算法""所有 on-policy 算法",不用挪文件。

## 目录结构

```text
优化方法/
├── README.md                 # 本文件:分类索引
├── 算法/                     # 每个算法只保存一份
│   ├── PolicyGradient.md
│   ├── ActorCritic.md
│   ├── A2C.md
│   ├── Q-learning DQN.md
│   ├── CQL.md
│   ├── IQL.md
│   └── Cal-QL.md
├── 训练范式/                 # 分类规则,不存算法副本
│   ├── OnlineVsOffline.md
│   ├── OnPolicyVsOffPolicy.md
│   └── OfflineRL.md
└── 问题与约束/
    └── OOD/
        ├── OODTaxonomy.md
        ├── 按偏移位置/
        │   ├── ObservationOOD.md
        │   ├── StateVisitationOOD.md
        │   ├── ActionOODAndExtrapolationError.md
        │   ├── LanguageGoalTaskOOD.md
        │   └── TransitionRewardOOD.md
        └── 按变化来源/
            ├── EnvironmentShift.md
            └── EmbodimentShift.md
```

`训练范式/` 和 `问题与约束/` 只讨论问题本身,不放算法副本;算法都收敛在 `算法/` 里,一篇一份。

## 当前算法一览

| 算法                                   | 数据范式                        | 策略关系                   | 优化对象           | 核心机制                     |
| ------------------------------------ | --------------------------- | ---------------------- | -------------- | ------------------------ |
| [[PolicyGradient\|Policy Gradient]]  | Online                      | On-policy(当前笔记范围)      | Policy         | 直接最大化期望回报                |
| [[ActorCritic\|Actor-Critic]]        | Online / Offline            | On-policy / Off-policy | Policy + Value | Actor 决策、Critic 评价       |
| [[A2C]]                              | Online                      | On-policy              | Policy + Value | 同步采样后统一更新                |
| [[Q-learning DQN\|Q-learning / DQN]] | 通常 Online                   | Off-policy             | Value          | Bellman 最优方程 + $\max Q$  |
| [[CQL]]                              | Offline                     | Off-policy             | Value          | 保守正则,压低 OOD-action 的 Q   |
| [[IQL]]                              | Offline                     | Off-policy             | Value + Policy | expectile $V$ + 优势加权 BC  |
| [[Cal-QL]]                           | Offline / Offline-to-Online | Off-policy             | Value + Policy | CQL + reference-value 校准 |

## 从哪个问题查起

- 想知道"Q-learning 为什么通常在线上跑,CQL 为什么是离线的" → [[OnlineVsOffline]]
- 想知道"off-policy 为什么不等于 offline"(两个词分开说) → [[OnPolicyVsOffPolicy]]
- 想了解离线 RL 完整方法族 → [[OfflineRL|离线强化学习]]
- 偏移发生在 Observation、State、Action、Task 还是 Transition,源头是环境还是本体 → [[OODTaxonomy|OOD 分类总览]]
- 想看某个算法的公式和训练流程 → 进 `算法/` 找对应笔记

## 三种优化对象

### 策略优化 Policy

直接参数化策略 $\pi_\theta(a\mid s)$,梯度上升最大化期望回报:

$$
J(\theta)=\mathbb E_{\tau\sim\pi_\theta}[G(\tau)],
\qquad
\nabla J(\theta)=
\mathbb E\left[G(\tau)\nabla_\theta\log\pi_\theta(\tau)\right].
$$

对应 [[PolicyGradient]];A2C、PPO 这类在此之上加了 critic 或约束更新。

### 值优化 Value

逼近 $Q^*(s,a)$,策略不显式建模,靠 $\arg\max_a Q(s,a)$ 隐式得到。训练通常最小化 Bellman error:

$$
L(w)=\mathbb E\left[
\left(r+\gamma\max_{a'}Q_w(s',a')-Q_w(s,a)\right)^2
\right].
$$

对应 [[Q-learning DQN|Q-learning / DQN]];CQL 是在这个基础上加了数据支持域约束。

### Actor-Critic

同时学 policy 和 value。它是一个框架,不是 on-policy 的同义词:A2C 是 on-policy 的 Actor-Critic,DDPG、TD3、SAC 是 off-policy 的 Actor-Critic。两者都叫 Actor-Critic,但数据用法完全不同。

## 两种迭代方式:爬坡 vs 压缩映射

梯度上升和 Q-learning 本质上都是不动点迭代:

$$
\theta_{k+1} = g(\theta_k)
$$

区别在迭代算子 $g$ 从哪来:

- **梯度上升**:$g(\theta) = \theta + \eta \nabla J(\theta)$,算子是某个标量函数 $J$ 的梯度,每一步都顺着"某个势函数最陡的方向"走。这是爬山。
- **Q-learning**:$g(Q) = Q + \alpha (\mathcal{T}^* Q - Q)$,算子是 Bellman 最优算子 $\mathcal{T}^*$ 的压缩映射变形。这个映射一般不是任何标量函数的梯度,它是靠压缩性收敛的。

为什么"不是任何函数的梯度"这个说法有意义?举个纯数学例子——线性迭代

$$
\begin{cases}
x_{k+1} = 0.5 y_k \\
y_{k+1} = -0.2 x_k
\end{cases}
$$

如果存在标量函数 $L(x,y)$ 让迭代等价于梯度下降 $(x_{k+1}, y_{k+1}) = (x_k, y_k) - \eta \nabla L(x_k, y_k)$,那必须有:

$$
\frac{\partial L}{\partial x} = \frac{x - 0.5y}{\eta}, \quad \frac{\partial L}{\partial y} = \frac{y + 0.2x}{\eta}
$$

算混合偏导:

$$
\frac{\partial^2 L}{\partial y \partial x} = -\frac{0.5}{\eta}, \quad \frac{\partial^2 L}{\partial x \partial y} = \frac{0.2}{\eta}
$$

两边不相等,这样的 $L$ 不存在。这个迭代照样收敛到不动点 $(0,0)$,但它不是任何函数的梯度下降。Q-learning 的 Bellman 算子 $\mathcal{T}^*$ 同理——它是压缩映射,能收敛到 $Q^*$,但不是某个势函数的梯度。

所以两种方法的包含关系是:

```
迭代方法 x_{k+1} = g(x_k)
  ├── g 是某个 J 的梯度上升 → 策略梯度(可微策略 → ∇J 存在)
  └── g 不是任何 J 的梯度  → Q-learning(压缩映射,不依赖梯度)
```

这个区别决定了它们各自需要什么条件:

| | 梯度上升 | 不动点迭代(非梯度) |
|--|---------|-------------------|
| 需要什么 | 目标函数 $J$ 可微,才能算 $\nabla J$ | 映射 $g$ 是压缩映射,保证收敛 |
| 策略形式要求 | $\pi_\theta$ 必须可微(softmax / 高斯) | $\pi$ 可以是 argmax(指示函数),不参与梯度计算 |
| 收敛保证 | 凸 $J$ + 合适步长 | 压缩映射(Banach 不动点定理) |
| 典型例子 | $\theta_{k+1} = \theta_k + \eta \nabla J(\theta_k)$ | $Q_{k+1} = Q_k + \alpha(r + \gamma \max Q_k - Q_k)$ |

两者都能收敛到最优策略,但路径不同:一个顺着梯度爬山,一个靠压缩映射逼近 $Q^*$ 的不动点。在收敛性分析、对策略形式的要求、对问题的建模方式上,它们有本质区别。
