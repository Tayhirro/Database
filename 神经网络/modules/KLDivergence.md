# KL 散度（Kullback–Leibler Divergence）

## 1. 一句话
- 衡量两个分布差异的量：`KL(q||p)=E_q[log q - log p]`，非对称、非距离。

## 2. 定义
- `KL(q||p) = \\int q(z) \\log \\frac{q(z)}{p(z)} dz`
- 离散形式：`KL(q||p)=\\sum_z q(z)\\log\\frac{q(z)}{p(z)}`

## 3. 常用性质（够用版）
- 非负性：`KL(q||p) >= 0`，且当且仅当 `q=p`（几乎处处）取等
- 非对称：一般 `KL(q||p) != KL(p||q)`
- 与交叉熵/极大似然关系：最小化 `KL(q||p_θ)` 等价于最大化 `E_q[log p_θ]`（差一个与 θ 无关的常数）

## 4. 在 VAE/ELBO 里出现的位置
- ELBO 的正则项就是 `KL(q(z|x)||p(z))`：`modules/ELBO.md`

