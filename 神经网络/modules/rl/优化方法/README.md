# 优化方法

RL 的参数优化方法分为两类：策略优化和值优化。它们要解决同一个问题——最大化期望回报——但选择了不同的参数化对象和优化途径。这个文件说明两者的区别以及各自对应的算法。

## 策略优化

**学什么**：直接参数化策略 $\pi_\theta(a|s)$，用梯度上升最大化 $J(\theta) = \mathbb{E}_{\pi_\theta}[G]$。

**策略形式**：概率分布（离散用 softmax，连续用高斯），对 $\theta$ 可微。

**梯度依据**：策略梯度定理

$$
\nabla J(\theta) = \mathbb{E} \left[ G(\tau) \nabla_\theta \log \pi_\theta(\tau) \right]
$$

由于 $\pi_\theta$ 可微，$\nabla_\theta \log \pi_\theta$ 存在且非零，梯度正常传播。

**对应文件**：[策略优化/PolicyGradient.md](策略优化/PolicyGradient.md)、[策略优化/ActorCritic.md](策略优化/ActorCritic.md)

## 值优化

**学什么**：逼近最优动作价值 $Q^*(s,a)$，策略由 $\arg\max_a Q(s,a)$ 隐含给出。

**策略形式**：$\pi(s) = \arg\max_a Q_w(s,a)$，等价于指示函数 $\mathbf{1}[a = \arg\max_a Q_w(s,a)]$，对 $w$ 不可微。

**梯度情况**：指示函数几乎处处梯度为 0，跳变点不可导。代入策略梯度公式得 $\nabla J(w) = \mathbb{E}[G \cdot 0] = 0$（几乎处处），但 $J(w)$ 实际在动作切换点有跳变——推导不成立。

**实际优化方式**：绕过 $\arg\max$，改用贝尔曼误差作为优化目标（光滑的 MSE）：

$$
L(w) = \mathbb{E} \left[ \left( r + \gamma \max_{a'} Q_w(s',a') - Q_w(s,a) \right)^2 \right]
$$

**对应文件**：[值优化/Q-learning.md](值优化/Q-learning.md)、[值优化/DQN.md](值优化/DQN.md)

## 对比

| | 策略优化 | 值优化 |
|--|---------|-------|
| 优化对象 | $\pi_\theta(a\|s)$ | $Q_w(s,a)$ |
| 策略来源 | softmax / 高斯输出 | $\arg\max Q$ |
| $\pi$ 是否可微 | 可微 | 不可微（指示函数） |
| 优化目标 | $J(\theta) = \mathbb{E}[G]$（策略梯度） | $L(w)$ = 贝尔曼误差 MSE |
| 典型算法 | REINFORCE、A2C、PPO | Q-learning、DQN、SAC* |

*SAC 同时学 Q 和 $\pi$，是两类方法的混合。

## 优化方法推导

梯度上升和 Q-learning 本质上都是不动点迭代：

$$
\theta_{k+1} = g(\theta_k)
$$

区别在于迭代算子 $g$ 的来源不同：

- **梯度上升**：$g(\theta) = \theta + \eta \nabla J(\theta)$，迭代算子来自某个标量函数 $J$ 的梯度。这种迭代一定能写成"沿着某个势函数最陡的方向走一步"。

- **Q-learning**：$g(Q) = Q + \alpha (\mathcal{T}^* Q - Q)$，迭代算子是 Bellman 最优算子 $\mathcal{T}^*$ 的压缩映射变形。这个映射**不是**任何标量函数的梯度。

纯数学例子：考虑线性迭代

$$
\begin{cases}
x_{k+1} = 0.5 y_k \\
y_{k+1} = -0.2 x_k
\end{cases}
$$

如果存在标量函数 $L(x,y)$ 使得该迭代等价于梯度下降 $(x_{k+1}, y_{k+1}) = (x_k, y_k) - \eta \nabla L(x_k, y_k)$，则必须有：

$$
\frac{\partial L}{\partial x} = \frac{x - 0.5y}{\eta}, \quad \frac{\partial L}{\partial y} = \frac{y + 0.2x}{\eta}
$$

计算混合偏导：

$$
\frac{\partial^2 L}{\partial y \partial x} = -\frac{0.5}{\eta}, \quad \frac{\partial^2 L}{\partial x \partial y} = \frac{0.2}{\eta}
$$

二者不相等，这样的 $L$ 不存在。这个迭代收敛到不动点 $(0,0)$，但它不是任何函数的梯度下降。Q-learning 的 Bellman 算子 $\mathcal{T}^*$ 同理——它是压缩映射，能收敛到 $Q^*$，但一般不是某个势函数的梯度。

所以包含关系是：

```
迭代方法 x_{k+1} = g(x_k)
  ├── g 是某个 J 的梯度上升 → 策略梯度（可微策略 → ∇J 存在）
  └── g 不是任何 J 的梯度  → Q-learning（压缩映射，不依赖梯度）
```

为什么理解这个区别重要，答案就在迭代算子的性质里：

| | 梯度上升 | 不动点迭代（非梯度） |
|--|---------|-------------------|
| 需要什么 | 目标函数 $J$ 可微，才能算 $\nabla J$ | 映射 $g$ 是压缩映射，保证收敛 |
| 策略形式要求 | $\pi_\theta$ 必须可微（softmax/高斯） | $\pi$ 可以是 argmax（指示函数），因为不参与梯度计算 |
| 收敛保证 | 凸 $J$ + 适当步长 | 压缩映射（Banach 不动点定理） |
| 典型例子 | $\theta_{k+1} = \theta_k + \eta \nabla J(\theta_k)$ | $Q_{k+1} = Q_k + \alpha(r + \gamma \max Q_k - Q_k)$ |

两者都能收敛到最优策略，但走的路径完全不同：一个沿着梯度爬坡，一个通过压缩映射迭代逼近 $Q^*$ 的不动点。它们在收敛性分析、对策略形式的要求、以及对问题的建模方式上都有根本区别。
