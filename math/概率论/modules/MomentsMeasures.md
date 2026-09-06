---
title: 矩与数值特征（Moments and Numerical Measures）
date: "2026-01-17"
categories:
  - math
description: 期望、方差、协方差等数值特征用于量化随机变量的"中心位置"、"离散程度"和"相关性"
---
# 矩与数值特征（Moments and Numerical Measures）

## 1. 一句话
- 期望、方差、协方差等数值特征用于量化随机变量的"中心位置"、"离散程度"和"相关性"

---

## 2. 期望（Expectation）

**定义**：随机变量的加权平均

$$E[X] = \int x \cdot p(x) \, dx$$

**详见**：[Expectation.md](Expectation.md)

---

## 3. 方差（Variance）

### 定义
$$\text{Var}(X) = E[(X - E[X])^2]$$

**计算公式**（更常用）：
$$\text{Var}(X) = E[X^2] - (E[X])^2$$

**直觉**：衡量 $X$ 相对于期望 $E[X]$ 的"离散程度"

---

### 标准差（Standard Deviation）
$$\sigma(X) = \sqrt{\text{Var}(X)}$$

与 $X$ 同单位，更直观。

---

### 条件方差（Conditional Variance）

已知 $X$ 后，$Y$ 还剩多少不确定性：

$$\text{Var}(Y|X) = E[(Y - E[Y|X])^2 | X]$$

等价计算公式：

$$\text{Var}(Y|X) = E[Y^2|X] - (E[Y|X])^2$$

给定具体取值 $X=x$ 时：

$$\text{Var}(Y|X=x) = E[Y^2|X=x] - (E[Y|X=x])^2$$

**类型区别**：
- $\text{Var}(Y|X)$ 是随机变量，本质是 $X$ 的函数
- $\text{Var}(Y|X=x)$ 是一个数

**独立性简化**：
若 $X, Y$ 独立，则：
$$\text{Var}(Y|X) = \text{Var}(Y)$$

---

### 方差分解（全方差公式）

$$\text{Var}(Y) = E[\text{Var}(Y|X)] + \text{Var}(E[Y|X])$$

**记忆**：总方差 = 组内方差的平均 + 组间均值的方差

**直觉**：
- $E[\text{Var}(Y|X)]$：固定每个 $X$ 后，$Y$ 在组内还会波动多少
- $\text{Var}(E[Y|X])$：不同 $X$ 对应的条件均值之间差多少

**推导思路**：
$$Y - E[Y] = (Y - E[Y|X]) + (E[Y|X] - E[Y])$$

平方后取期望，交叉项为 0，因此得到全方差公式。

---

### 性质

**常数不影响方差**：
$$\text{Var}(X + c) = \text{Var}(X)$$

**系数平方**：
$$\text{Var}(aX) = a^2 \text{Var}(X)$$

**独立和的方差**：
若 $X, Y$ 独立，则：
$$\text{Var}(X + Y) = \text{Var}(X) + \text{Var}(Y)$$

**一般情形**（不要求独立）：
$$\text{Var}(X + Y) = \text{Var}(X) + \text{Var}(Y) + 2\text{Cov}(X, Y)$$

**线性组合的方差**：
$$\text{Var}(aX + bY + c) = a^2\text{Var}(X) + b^2\text{Var}(Y) + 2ab\text{Cov}(X,Y)$$

推广到多变量：
$$\text{Var}\left(\sum_{i=1}^n a_iX_i\right)
= \sum_{i=1}^n a_i^2\text{Var}(X_i)
+ 2\sum_{i<j} a_i a_j \text{Cov}(X_i,X_j)$$

若 $X_1,\ldots,X_n$ 两两不相关，则协方差项为 0。

---

### 总体方差与样本方差

**总体方差**是分布本身的数值特征：
$$\text{Var}(X) = E[(X-\mu)^2]$$

**样本内方差**（描述当前样本的离散程度）：
$$S_n^2 = \frac{1}{n}\sum_{i=1}^n (X_i-\bar{X})^2$$

**无偏样本方差**（估计总体方差）：
$$S^2 = \frac{1}{n-1}\sum_{i=1}^n (X_i-\bar{X})^2$$

**关键区别**：$\frac{1}{n-1}$ 是为了让 $E[S^2]=\text{Var}(X)$，不是总体方差定义的一部分。

---

## 4. 协方差（Covariance）

### 定义
$$\text{Cov}(X, Y) = E[(X - E[X])(Y - E[Y])]$$

**计算公式**：
$$\text{Cov}(X, Y) = E[XY] - E[X]E[Y]$$

---

### 直觉

- **$\text{Cov}(X, Y) > 0$**：$X$ 大时 $Y$ 倾向于大（正相关）
- **$\text{Cov}(X, Y) < 0$**：$X$ 大时 $Y$ 倾向于小（负相关）
- **$\text{Cov}(X, Y) = 0$**：不相关（但不一定独立）

**注意**：协方差为 0 不能推出独立，但独立能推出协方差为 0。

---

### 性质

**对称性**：
$$\text{Cov}(X, Y) = \text{Cov}(Y, X)$$

**与自身的协方差是方差**：
$$\text{Cov}(X, X) = \text{Var}(X)$$

**线性性**：
$$\text{Cov}(aX + b, cY + d) = ac \cdot \text{Cov}(X, Y)$$

**求和展开**（重要）：
$$\text{Var}(X + Y) = \text{Var}(X) + \text{Var}(Y) + 2\text{Cov}(X, Y)$$

推广到多变量：
$$\text{Var}\left(\sum_{i=1}^n X_i\right) = \sum_{i=1}^n \text{Var}(X_i) + 2\sum_{i<j} \text{Cov}(X_i, X_j)$$


**例子**：
- $\text{Cov}(X, 2X+Y) = 2\text{Var}(X) + \text{Cov}(X, Y)$
- $\text{Cov}(X-Y, X+Y) = \text{Var}(X) - \text{Var}(Y)$

---

### 协方差矩阵（多元情形）

对随机向量 $\mathbf{X} = (X_1, \ldots, X_n)^T$，协方差矩阵：

$$\Sigma = \text{Cov}(\mathbf{X}) = E[(\mathbf{X} - E[\mathbf{X}])(\mathbf{X} - E[\mathbf{X}])^T]$$

**矩阵形式**：
$$\Sigma_{ij} = \text{Cov}(X_i, X_j)$$

**性质**：
- 对称矩阵：$\Sigma = \Sigma^T$
- 半正定：$\mathbf{a}^T \Sigma \mathbf{a} \geq 0$ 对所有 $\mathbf{a}$
- 对角线元素是方差：$\Sigma_{ii} = \text{Var}(X_i)$

---

### 矩阵/向量的整体方差与元素方差

**问题**：若随机矩阵 $W$ 有 $N$ 个元素，每个元素 $W_{ji}$ 独立同分布，$E[W_{ji}] = 0$，$Var(W_{ji}) = \sigma^2$，则把所有元素看作一个整体，其方差怎么求？

**推导**：

样本均值：
$$\bar{W} = \frac{1}{N} \sum_{j,i} W_{ji}$$

样本方差：
$$S^2 = \frac{1}{N-1} \sum_{j,i} (W_{ji} - \bar{W})^2$$

展开平方和：
$$\sum_{j,i} (W_{ji} - \bar{W})^2 = \sum_{j,i} W_{ji}^2 - N \bar{W}^2$$

取期望：

1. $\sum_{j,i} W_{ji}^2 = N\sigma^2$（因为 $E[W_{ji}^2] = Var(W_{ji}) + (E[W_{ji}])^2 = \sigma^2$）

2. 求 $E[\bar{W}^2]$：
   - $E[\bar{W}] = 0$，故 $Var(\bar{W}) = E[\bar{W}^2]$
   - $Var(\bar{W}) = Var\left(\frac{1}{N}\sum W_{ji}\right) = \frac{1}{N^2} \sum Var(W_{ji}) = \frac{N\sigma^2}{N^2} = \frac{\sigma^2}{N}$
   - 所以 $E[\bar{W}^2] = \frac{\sigma^2}{N}$

3. 因此：
$$E\left[\sum(W_{ji} - \bar{W})^2\right] = N\sigma^2 - \sigma^2 = (N-1)\sigma^2$$

最终：
$$E[S^2] = \frac{(N-1)\sigma^2}{N-1} = \sigma^2$$

**结论**：整体方差的期望值等于单个元素的方差 $\sigma^2$。

---

## 5. 相关系数（Correlation Coefficient）

### 定义（Pearson 相关系数）
$$\rho(X, Y) = \frac{\text{Cov}(X, Y)}{\sqrt{\text{Var}(X) \text{Var}(Y)}} = \frac{\text{Cov}(X, Y)}{\sigma_X \sigma_Y}$$

**归一化的协方差**，消除了量纲影响。

---

### 性质

**取值范围**（Cauchy-Schwarz 不等式）：
$$-1 \leq \rho(X, Y) \leq 1$$

**极端情况**：
- $\rho = 1$：完全正线性相关（$Y = aX + b$，$a > 0$）
- $\rho = -1$：完全负线性相关（$Y = aX + b$，$a < 0$）
- $\rho = 0$：不相关（但可能非线性相关）

**不变性**：
$$\rho(aX + b, cY + d) = \text{sign}(ac) \cdot \rho(X, Y)$$

---

### 独立 vs 不相关

| 关系 | 含义 |
|------|------|
| $X, Y$ 独立 | $p(x,y) = p(x)p(y)$ |
| $X, Y$ 不相关 | $\text{Cov}(X,Y) = 0$ 或 $\rho(X,Y) = 0$ |

**逻辑关系**：
- 独立 → 不相关（总成立）
- 不相关 ↛ 独立（反例：$Y = X^2$，$X \sim \mathcal{N}(0,1)$）

---

## 6. 高阶矩

### 原点矩（Raw Moment）
$$\mu_k = E[X^k]$$

- $\mu_1 = E[X]$：均值
- $\mu_2 = E[X^2]$

---

### 中心矩（Central Moment）
$$\nu_k = E[(X - E[X])^k]$$

- $\nu_1 = 0$（恒为0）
- $\nu_2 = \text{Var}(X)$：方差
- $\nu_3$：用于定义偏度
- $\nu_4$：用于定义峰度

---

### 偏度（Skewness）
$$\gamma_1 = \frac{E[(X - \mu)^3]}{\sigma^3} = \frac{\nu_3}{\sigma^3}$$

**含义**：分布的对称性
- $\gamma_1 = 0$：对称
- $\gamma_1 > 0$：右偏（右尾长）
- $\gamma_1 < 0$：左偏（左尾长）

---

### 峰度（Kurtosis）
$$\gamma_2 = \frac{E[(X - \mu)^4]}{\sigma^4} = \frac{\nu_4}{\sigma^4}$$

**超额峰度**（相对于正态分布）：
$$\text{Excess Kurtosis} = \gamma_2 - 3$$

- 正态分布：$\gamma_2 = 3$
- 厚尾分布（如 $t$ 分布）：$\gamma_2 > 3$
- 薄尾分布（如均匀分布）：$\gamma_2 < 3$

---

## 7. 常用不等式

### Cauchy-Schwarz 不等式
$$|E[XY]| \leq \sqrt{E[X^2] E[Y^2]}$$

**推论**：
$$|\text{Cov}(X,Y)| \leq \sqrt{\text{Var}(X) \text{Var}(Y)}$$

因此：
$$|\rho(X,Y)| \leq 1$$

---

### Markov 不等式

若 $X \geq 0$，且 $a>0$，则：
$$P(X \geq a) \leq \frac{E[X]}{a}$$

**用途**：只知道均值时，粗略控制右尾概率。

---

### Chebyshev 不等式

若 $E[X]=\mu$，$\text{Var}(X)=\sigma^2$，则：
$$P(|X-\mu| \geq a) \leq \frac{\sigma^2}{a^2}$$

等价地：
$$P(|X-\mu| \geq k\sigma) \leq \frac{1}{k^2}$$

**用途**：只知道均值和方差时，控制偏离均值的概率。

---

## 8. 在机器学习中的应用

### 标准化（Standardization）
$$Z = \frac{X - E[X]}{\sqrt{\text{Var}(X)}}$$

使 $E[Z] = 0$，$\text{Var}(Z) = 1$

---

### 主成分分析（PCA）
对协方差矩阵 $\Sigma$ 做特征值分解：
$$\Sigma = Q \Lambda Q^T$$

主成分是 $Q$ 的列向量。

---

### 线性回归的 MSE
$$\min_{\beta} E[(Y - X^T\beta)^2]$$

最优解涉及协方差矩阵：
$$\beta^* = \Sigma_{XX}^{-1} \Sigma_{XY}$$

---

### VAE 的高斯假设
假设 $X|Z \sim \mathcal{N}(\mu_\theta(z), \Sigma_\theta(z))$

Decoder 输出均值和协方差。

---

## 9. 速查表

| 概念 | 公式 | 含义 |
|------|------|------|
| 期望 | $E[X] = \int x p(x) dx$ | 中心位置 |
| 方差 | $\text{Var}(X) = E[X^2] - (E[X])^2$ | 离散程度 |
| 标准差 | $\sigma = \sqrt{\text{Var}(X)}$ | 与 $X$ 同单位 |
| 条件方差 | $\text{Var}(Y\|X)=E[Y^2\|X]-(E[Y\|X])^2$ | 已知 $X$ 后的剩余离散程度 |
| 全方差公式 | $\text{Var}(Y)=E[\text{Var}(Y\|X)]+\text{Var}(E[Y\|X])$ | 组内 + 组间 |
| 协方差 | $\text{Cov}(X,Y) = E[XY] - E[X]E[Y]$ | 线性相关性 |
| 相关系数 | $\rho = \frac{\text{Cov}(X,Y)}{\sigma_X \sigma_Y}$ | 归一化协方差 |
| 独立和方差 | $\text{Var}(X+Y) = \text{Var}(X) + \text{Var}(Y)$ | 仅独立时 |
| 一般和方差 | $\text{Var}(X+Y) = \text{Var}(X) + \text{Var}(Y) + 2\text{Cov}(X,Y)$ | 总成立 |
| 无偏样本方差 | $S^2=\frac{1}{n-1}\sum_i(X_i-\bar{X})^2$ | 估计总体方差 |

---

## 10. 相关模块

- [期望](Expectation.md)：期望的详细定义
- [条件期望](ConditionalExpectation.md)：$E[Y|X]$
- 本文第 7 节：Cauchy-Schwarz、Markov、Chebyshev 等常用不等式

---

## 11. 常见误区

- **误区1**：认为 $\text{Cov}(X,Y) = 0$ 意味着独立（只能说不相关）
- **误区2**：忘记方差对系数是平方：$\text{Var}(aX) = a^2 \text{Var}(X)$
- **误区3**：混淆协方差和相关系数（后者是归一化的）
- **误区4**：认为 $\text{Var}(X+Y) = \text{Var}(X) + \text{Var}(Y)$ 总成立（需要独立或不相关）
- **误区5**：用 Pearson 相关系数衡量非线性关系（它只能捕获线性相关）
- **误区6**：认为 $\text{Var}(Y|X)$ 是常数（它通常是关于 $X$ 的随机变量）
- **误区7**：把全方差公式写成 $\text{Var}(Y)=\text{Var}(Y|X)+\text{Var}(E[Y|X])$（第一项外面还要取期望）
