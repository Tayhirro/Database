# 拉普拉斯分布 / 双指数分布（Laplace / Double Exponential）

导航：[math/概率论/distributions/README.md](README.md) ｜[math/概率论/索引.md](../索引.md)

## 1. 一句话
- 一个“尖峰 + 厚尾”的连续分布；其负对数似然对应 `L1` 损失，常用于鲁棒建模（噪声、先验）。

## 2. 定义（PDF/CDF）
- 记号：`X ~ Laplace(μ, b)`，其中 `μ ∈ R`（位置），`b > 0`（尺度）。
- 支持集：`x ∈ R`。
- 概率密度（PDF）：
  - `f(x) = (1/(2b)) · exp(-|x-μ|/b)`
- 分布函数（CDF）：
  - 若 `x < μ`：`F(x) = (1/2) · exp((x-μ)/b)`
  - 若 `x ≥ μ`：`F(x) = 1 - (1/2) · exp(-(x-μ)/b)`

## 3. 参数与直觉
- `μ`：峰的位置（也是中位数、众数、均值）。
- `b`：尺度；`b` 越大，尾部越厚、方差越大。

## 4. 常用性质
- `E[X] = μ`
- `Var(X) = 2 b^2`
- MGF：`M_X(t) = E[e^{tX}] = exp(μ t) / (1 - b^2 t^2)`，定义域 `|t| < 1/b`

## 5. 构造与关系
- 差分构造：若 `U, V` 独立且 `U,V ~ Exp(rate = 1/b)`，则 `U - V ~ Laplace(0, b)`，从而 `μ + U - V ~ Laplace(μ, b)`。
- `L1` / 稀疏先验：若把误差建模为拉普拉斯噪声，则极大似然等价于最小化 `Σ |x_i - μ|`（对应中位数）；在回归里对应 `L1` 代价（鲁棒/稀疏相关）。

## 6. 采样（常用写法）
- 若 `U ~ Uniform(-1/2, 1/2)`，则
  - `X = μ - b · sgn(U) · ln(1 - 2|U|)` 服从 `Laplace(μ, b)`。

## 7. 速查
- 关键词：Laplace、Double Exponential、`L1`、median、厚尾（heavy-tailed）

