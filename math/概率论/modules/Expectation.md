# 期望（Expectation）

## 1. 一句话
- 期望是随机变量的"加权平均"，权重由概率分布给出；是概率论中最核心的数值特征

---

## 2. 定义的三个层次

### 层次1：离散随机变量
$$E[X] = \sum_{x} x \cdot P(X=x)$$

**直觉**：每个可能值 × 它出现的概率，然后全部相加

**例子**：掷骰子 $X \in \{1,2,3,4,5,6\}$，$P(X=k) = 1/6$
$$E[X] = 1 \cdot \frac{1}{6} + 2 \cdot \frac{1}{6} + \cdots + 6 \cdot \frac{1}{6} = 3.5$$

---

### 层次2：连续随机变量
$$E[X] = \int_{-\infty}^{\infty} x \cdot p(x) \, dx$$

其中 $p(x)$ 是概率密度函数（PDF）。

**直觉**：把求和换成积分，密度函数 $p(x)$ 替代了离散概率

**例子**：标准正态分布 $X \sim \mathcal{N}(0,1)$
$$E[X] = \int_{-\infty}^{\infty} x \cdot \frac{1}{\sqrt{2\pi}} e^{-x^2/2} dx = 0$$
（对称性导致正负抵消）

---

### 层次3：一般定义（测度论）
$$E[X] = \int_{\Omega} X(\omega) \, dP(\omega)$$

其中：
- $\Omega$：样本空间
- $X: \Omega \to \mathbb{R}$：随机变量（可测函数）
- $P$：概率测度
- 积分是关于测度 $P$ 的 Lebesgue 积分

**为什么需要这个定义**：
- 统一离散和连续情形
- 严格定义"期望存在"的条件（绝对可积）
- 为测度论概率论提供基础

---

## 3. 函数的期望

给定随机变量 $X$ 和函数 $g: \mathbb{R} \to \mathbb{R}$，则：

$$E[g(X)] = \begin{cases}
\sum_x g(x) \cdot P(X=x) & \text{离散} \\
\int g(x) \cdot p(x) \, dx & \text{连续}
\end{cases}$$

**关键点**：不需要先求 $Y = g(X)$ 的分布，直接用 $X$ 的分布计算

**例子**：$X \sim \text{Uniform}(0,1)$，求 $E[X^2]$
$$E[X^2] = \int_0^1 x^2 \cdot 1 \, dx = \frac{1}{3}$$

---

## 4. 多元情形：联合期望

给定联合分布 $p(x,z)$，函数 $g(x,z)$ 的期望：

$$E[g(X,Z)] = \iint g(x,z) \cdot p(x,z) \, dx \, dz$$

**链式分解**（使用链式法则）：
$$E[g(X,Z)] = \iint g(x,z) \cdot p(x) \cdot p(z|x) \, dz \, dx$$

这可以写成**嵌套期望**：
$$E[g(X,Z)] = E_{x \sim p(x)} \left[ E_{z \sim p(z|x)} [g(x,z)] \right]$$

**理解**：
1. 内层期望：固定 $x$，对 $z$ 求期望
2. 外层期望：对结果再对 $x$ 求期望

---

## 5. 记号约定

| 记号 | 含义 |
|------|------|
| $E[X]$ | 对 $X$ 的分布求期望（分布通常从上下文推断） |
| $E_{x \sim p(x)}[h(x)]$ | 明确指出以 $p(x)$ 为权重求期望 |
| $E_{p(x)}[h(x)]$ | 同上（简写） |
| $E[Y\|X]$ | 条件期望（是关于 $X$ 的随机变量） |
| $E[Y\|X=x]$ | 给定 $X=x$ 时 $Y$ 的期望（是数值） |

**常见困惑**：
- $E_{x \sim p(x)}$ 中的 $x \sim p(x)$ **不是假设**，而是"对 $p(x)$ 这个分布求积分"的简写
- 期望本质是**积分**，不是"对某个变量"求

---

## 6. 期望的性质

### 线性性（最重要）
$$E[aX + bY] = aE[X] + bE[Y]$$
**无需独立性假设**

### 单调性
若 $X \leq Y$（几乎处处），则 $E[X] \leq E[Y]$

### 独立变量的乘积
若 $X, Y$ 独立，则：
$$E[XY] = E[X] \cdot E[Y]$$

---

## 7. 期望存在性

期望不一定存在！需要：
$$E[|X|] = \int |x| \, p(x) \, dx < \infty$$

**反例**：Cauchy 分布
$$p(x) = \frac{1}{\pi(1+x^2)}$$
$$\int_{-\infty}^{\infty} |x| \cdot \frac{1}{\pi(1+x^2)} dx = \infty$$

期望不存在。

---

## 8. 在机器学习中的应用

### 训练目标通常是期望
$$\min_\theta \, E_{x \sim p_{data}(x)} [\ell(\theta; x)]$$

**实际计算**：用样本平均近似
$$E[\ell] \approx \frac{1}{n} \sum_{i=1}^n \ell(\theta; x_i)$$

### VAE 的重建项
$$E_{x \sim p_{data}} E_{z \sim q_\phi(z|x)} [\|x - f_\theta(z)\|^2]$$

展开成联合期望：
$$E_{(x,z) \sim q_\phi(x,z)} [\|x - f_\theta(z)\|^2]$$

其中 $q_\phi(x,z) = p_{data}(x) \cdot q_\phi(z|x)$

---

## 9. 与积分的关系

**期望 = 概率加权积分**

| 类比 | 积分 | 期望 |
|------|------|------|
| 普通积分 | $\int f(x) \, dx$ | 无权重 |
| 加权积分 | $\int f(x) w(x) \, dx$ | 权重 $w(x)$ |
| 期望 | $\int f(x) p(x) \, dx$ | 权重是概率密度 |

**归一化条件**：$\int p(x) dx = 1$

---

## 10. 相关模块

- [条件期望](ConditionalExpectation.md)：$E[Y|X]$ 的严格定义
- [嵌套期望](IteratedExpectation.md)：$E[E[Y|X]] = E[Y]$
- [方差](Variance.md)：$\text{Var}(X) = E[X^2] - (E[X])^2$

---

## 11. 速查

| 场景 | 公式 |
|------|------|
| 离散 | $E[X] = \sum_x x P(X=x)$ |
| 连续 | $E[X] = \int x p(x) dx$ |
| 函数 | $E[g(X)] = \int g(x) p(x) dx$ |
| 联合 | $E[g(X,Z)] = \iint g(x,z) p(x,z) dxdz$ |
| 线性性 | $E[aX+bY] = aE[X] + bE[Y]$ |

---

## 12. 常见误区

- **误区1**：认为 $E[XY] = E[X]E[Y]$ 总成立（需要独立性）
- **误区2**：认为 $E[1/X] = 1/E[X]$（Jensen不等式表明通常不等）
- **误区3**：忽略期望可能不存在的情况
- **误区4**：混淆 $E[Y|X]$（随机变量）和 $E[Y|X=x]$（数值）
