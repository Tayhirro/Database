---
title: L2L与强化学习-像RL，但不是RL
aliases:
  - Learned Optimizer 与强化学习
  - L2L 像 RL 但不是 RL
date: 2026-03-14 00:00:00
categories:
  - 博客
tags:
  - ai/optimization
  - ai/meta-learning
  - ai/reinforcement-learning
description: 从强化学习类比切入，重新理解 learned optimizer 的训练本质。
---

[[博客索引|返回博客索引]]

> [!abstract]
> 读完本文，你会更清楚：
> - 为什么 learned optimizer 很容易让人联想到强化学习
> - 这个类比到底对在哪里，又错在哪里
> - 为什么它没有 value function，依然能做 credit assignment
> - 更准确的说法为什么是「可微轨迹上的元优化」

读完本文，你会更清楚：

- 为什么 learned optimizer 很容易让人联想到强化学习
- 这个类比到底对在哪里，又错在哪里
- 为什么它没有 value function，依然能做 credit assignment
- 更准确的说法为什么是「可微轨迹上的元优化」

我最近重新想了一遍 L2L（Learning to Learn）里 learned optimizer 的训练方式，最大的收获不是“它像 RL”，而是：

> **它确实长得像强化学习，但训练它的方法，往往并不是标准强化学习。**

这件事我一开始也绕了半圈。

因为只要把 learned optimizer 按时间展开，它的外形真的太像了：每一步看当前状态，输出一个更新动作，这个动作会改变后面的轨迹，而我们关心的又恰好是整条轨迹最终好不好。

于是很自然就会问：这不就是 RL 吗？

我的答案现在变成了：**像，但不能直接等号。**

## 它为什么这么像 RL

如果硬要用 RL 语言翻译 learned optimizer，其实非常顺手。

- **state**：当前优化状态，比如参数位置、当前梯度、历史梯度统计、 hidden state，甚至 preconditioner
- **action**：优化器这一步输出的更新量，也就是 $\Delta x_t$
- **transition**：做完更新以后，从 $x_t$ 走到 $x_{t+1}$
- **return**：不是单步 reward，而是整条优化轨迹累计起来的效果

很多论文写法本身就已经有这个味道了。比如 Optimus 这种 learned optimizer，会把更新写成：

$$
x^{k+1} = x^k - \Delta x^k, \qquad \Delta x^k = B^k s^k.
$$

如果再把元目标写成沿轨迹累加的 loss：

$$
\sum_k L(x^k),
$$

那它看起来就更像“策略沿着一条轨迹行动，并为累计后果负责”了。

所以从直觉上说，把 learned optimizer 理解成一种“在优化轨迹上做决策的 agent”，这个类比完全没问题。

> **问题不在于这个类比错了，而在于它还不够精确。**

> [!note]
> 如果只停留在“它看起来像 RL”，很容易自然滑向 actor-critic、value function、Bellman backup 那套熟悉语言；真正要分清的是：**外形像，不代表训练机制也一样。**

## 真正关键的一步：它通常不按标准 RL 的方式训练

我现在觉得，这里最容易混淆的点，是“看起来像 RL”与“训练机制就是 RL”之间其实隔着一层。

标准 RL 里，我们熟悉的是这些东西：

- value function / Q function
- Bellman backup
- actor-critic
- policy gradient 配合环境采样

但 learned optimizer 的很多做法，并不是这套。

它更常见的训练方式是：**把整个优化过程 unroll 成一条可微轨迹，然后直接优化这条轨迹诱导出的 meta-objective。**

也就是直接去最小化：

$$
\min_\phi \sum_t w_t L(x_t(\phi)).
$$

这里的 $\phi$ 是 optimizer 自己的参数，$x_t(\phi)$ 表示“在这个 optimizer 控制下，任务参数沿着轨迹走到第 $t$ 步时的位置”。

注意这个目标长得像累计 return，但它不是先学一个 value，再通过 value 间接更新 policy；它是**直接拿轨迹总代价来训练 optimizer 本身**。

> [!tip]
> 记忆这件事最简单的方法是：
> `像 return，不等于通过 value 学出来的 return。`

## 和标准 RL 真正不同的 4 个地方

### 1. 它通常没有显式 value function

这是我觉得最本质的一刀。

在标准 RL 语境里，我们习惯于先问：“它的 value 在哪里？Q 在哪里？critic 在哪里？”

但在很多 learned optimizer 里，答案是：**没有。**

它不去拟合一个 $V(s)$，也不去拟合一个 $Q(s, a)$。它只是把整条 rollout 展开，然后直接优化这条 rollout 对应的总代价。

所以更准确地说，它做的是：

- 不是 value learning
- 不是 Bellman-style bootstrapping
- 而是 trajectory-level meta-optimization

这一步一想通，脑子里的雾会散很多。

### 2. 它的“环境”往往是已知且可微的

这也是它和标准 RL 差别特别大的地方。

RL 里的环境通常是未知的、黑盒的，甚至不可微。你可以 rollout，但很难指望把梯度直接穿过环境反传回来。

可 learned optimizer 这边，状态转移本身就是优化更新：

$$
x_{t+1} = x_t - U_\phi(z_t).
$$

而任务损失 $L(x)$ 通常又是可微的。于是只要把优化过程展开，就可以沿着整条轨迹把梯度一路传回 optimizer 参数 $\phi$。

这时候它更像什么？

> **更像在已知动力学系统上的可微控制，或者 model-based 的 policy search。**

也就是说，这里的“环境”不是一个神秘的外部世界，而是一个你能写出方程、还能对它反传的系统。

### 3. 轨迹权重 $w_t$ 不等于 RL 里的折扣因子 $\gamma^t$

这也是一个特别容易误判的地方。

有些 L2L 目标会写成：

$$
\sum_{t=1}^T w_t L_t.
$$

于是直觉上很容易把 $w_t$ 看成“是不是就相当于 discount factor？”

但很多时候，它们的语义并不一样。

RL 里的 $\gamma$ 来自 MDP 里的 discounted return 设定；而 learned optimizer 里的 $w_t$，更像是：

- 对训练过程的 shaping
- 对不同时间步的监督权重
- 为了缓解 short-horizon 问题的人为设计

甚至有的做法会让 $w_t$ 递增，而不是递减。它追求的不是“越远的未来越不重要”，而是“让优化器尽早学会在前几步就做出有效动作”。

所以它和 discounted return **形式相似，但语义不同**。

> [!warning]
> 这里最容易偷换概念的地方就是把 $w_t$ 直接看成 $\gamma^t$。两者都在“给不同时间步加权”，但一个主要是在做 trajectory shaping，另一个来自 discounted return 的定义，本质不是一回事。

### 4. PES 这种技巧也不是在做 Monte Carlo value estimation

这一点也值得单独记一下。

有些 learned optimizer 论文会提到用 PES（Persistent Evolution Strategies）来帮助训练，尤其是在长链 unroll、梯度噪声大、容易爆炸的时候。

但 PES 在这里扮演的角色，更接近：

- 对 outer objective 的梯度估计器
- 对 truncated backprop 偏差的修正工具
- 解决元训练稳定性的问题

它不是那种“完整 rollout 后估计 return，再去拟合 value”的 RL 评估流程。

所以如果只因为它用了 sampling 或 Monte Carlo 风格的估计，就把它归入标准 RL，也会偏掉一点。

## 一个更准确的定位

如果要分层理解，我现在会这样记。

### 直觉层

**它像 RL。**

因为优化器确实在一条时间展开的轨迹上连续做决策，而且早期动作会影响后期结果。

### 数学层

**它更像轨迹级别的元优化。**

因为目标不是 Bellman 方程，而是直接最小化轨迹总代价。

### 训练机制层

**它更像可微环境上的 policy search / optimal control。**

因为：

- transition 是已知的
- 轨迹可以显式 unroll
- loss 通常可微
- 梯度可以直接穿过整条优化过程

这和标准 model-free RL 的味道并不一样。

## 一张更顺手的对照表

| learned optimizer | RL 类比 | 真正需要提醒自己的地方 |
| --- | --- | --- |
| optimization features $z_t$ | state $s_t$ | 状态是优化过程内部状态 |
| update $\Delta x_t$ | action $a_t$ | 动作是参数更新，而不是环境行为 |
| $x_{t+1} = x_t - \Delta x_t$ | environment transition | transition 通常已知且可微 |
| $-\sum_t w_t L_t$ | return | 形式接近 return，但目标是 cost shaping |
| 直接优化 $\sum_t w_t L(x_t(\phi))$ | policy optimization | 常常没有 critic，也没有 value learning |

## 我现在最喜欢的一句总结

如果只留一句话，我会留这句：

> **它不是“用强化学习训练优化器”，而是“把优化过程看成一条可微轨迹，再对这条轨迹的总代价做元学习”。**

这句话的好处是，它保留了 RL 类比带来的直觉，又避免把训练机制说错。

前四行说明它为什么“像”，最后一行提醒我：**别在最关键的地方偷换概念。**

## 下一步我最想继续想明白的事

其实把上面这些想通以后，会自然冒出一个更有意思的问题：

> **既然它没有 value function，那最终 loss 到底是怎么给第 1 步 action 分配 credit 的？**

答案当然还是链式法则，只不过这里的 credit assignment 是沿着可微轨迹反传，而不是通过 value 去间接传播。

这可能才是 learned optimizer 和 RL 最值得并排看的地方：它们都在解决 long-term credit assignment，但用的是两套不同的工具箱。

如果后面我继续整理，我很想把这件事单独写成一篇：直接把

$$
\frac{d}{d\phi} \sum_t L(x_t(\phi))
$$

的链式展开，和 RL 里的 return credit assignment 放在一起看。那时很多“像，但不一样”的地方，应该会更一目了然。

> [!example]
> 如果后面继续写这一系列，我更想把它拆成两篇：
> 1. `[[L2L与强化学习-像RL，但不是RL]]` 负责讲清“为什么这个类比成立但不等价”；
> 2. 下一篇专门讲“没有 value function 时，credit assignment 是怎么沿着可微轨迹传播的”。
