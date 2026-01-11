# 练习（按模块/模型）

## 1. ELBO / KL
- 推导：`log p(x) = ELBO(x) + KL(q(z|x)||p(z|x))`
- 证明：`KL(q||p) >= 0`

## 2. VAE
- 写出 VAE 的计算图，并标注哪些地方需要重参数化

## 3. Attention
- 推导 self-attention 的矩阵形式，并解释 `sqrt(d_k)` 缩放的作用

## 4. CLIP / InfoNCE
- 写出 CLIP 的双向对比损失，并解释温度 `τ` 的影响

