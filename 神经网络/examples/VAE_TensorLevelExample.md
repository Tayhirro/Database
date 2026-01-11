---
type: example
tags:
  - nn/vae
  - nn/example
  - nn/tensor
---

# VAE Tensor 级例子：RGB 图片（H, W, 3）

目标：给一个 batch 的图片张量（“图片 = H×W×3”），走一遍 VAE 的前向与 loss，把每一步 tensor 的 shape 对齐。

---

## 0. 约定（Notation）
- `B`：batch size
- `H,W`：图像高宽
- `C=3`：RGB 通道数
- `L`：latent dim
- `x`：输入图像张量
- `mu, logvar`：编码器输出（`logvar = log(sigma^2)`）
## 1. 输入（图片 H×W×3）
很多数据源是 **channels-last**：
- `x_hw3`：`(B, H, W, 3)`，值域常见 `[0,1]`（float）或 `[0,255]`（uint8）

PyTorch 卷积默认 **channels-first**：
- `x = x_hw3.permute(0, 3, 1, 2)` → `(B, 3, H, W)`
> 关键点：后面所有 loss 的 `sum(dim=...)` 都依赖这个约定，先统一通道顺序。

## 2. Encoder：`x → (mu, logvar)`
编码器最终输出两个向量（逐样本一行）：
- `h = Enc(x)` → `(B, hidden)`
- `mu = FC_mu(h)` → `(B, L)`
- `logvar = FC_logvar(h)` → `(B, L)`

## 3. 重参数化：`z = mu + std * eps`
- `std = exp(0.5 * logvar)` → `(B, L)`
- `eps ~ N(0, I)`：`eps = randn_like(std)` → `(B, L)`
- `z = mu + std * eps` → `(B, L)`

## 4. Decoder：`z → x_hat`
解码器输出与输入同 shape 的“重构图像”：
- `x_hat = Dec(z)` → `(B, 3, H, W)`

### 4.1 `z (B,L)` 怎么“解码”成图片
`B` 始终是 batch 维；关键在于把每个样本的 latent 向量 `z_i ∈ R^L` 映射回一张图像张量。

两种最常见的 shape 走法：
1) **MLP decoder（最直观）**
   - `z: (B, L) -> Linear -> (B, 3*H*W) -> view -> (B, 3, H, W)`
2) **Conv decoder（图像更常用）**
   - `z: (B, L) -> Linear -> (B, C0*H0*W0) -> view -> (B, C0, H0, W0)`
   - 再用 `ConvTranspose2d` 或 `Upsample + Conv2d` 逐步上采样到 `(B, 3, H, W)`

### 4.2 一个常见配置例子（目标 `64×64`）
如果目标输出是 `H=W=64`，常见会选：
- `H0=W0=4`，每次上采样 `×2`，做 4 次：`4 → 8 → 16 → 32 → 64`
- `C0` 取一个较大的通道数（如 128/256），最后一层映射到 `3` 通道

输出形式常见两种：
1) `x_hat` 已经被 `sigmoid`/`tanh` 约束到范围（如 `[0,1]`）  
2) `x_logits` 未过激活，后面用 `BCEWithLogits`（更数值稳定）

## 5. Loss（逐样本）

### 5.1 重构项（reconstruction）
如果你把像素当作连续值（更常见于自然图像）：
- `recon_i = ||x_hat - x||^2`（对 `C,H,W` 求和）→ `(B,)`

如果你把像素当作 Bernoulli（更常见于 MNIST 一类二值/近似二值数据）：
- 用 `BCEWithLogits(x_logits, x)`（对 `C,H,W` 求和）→ `(B,)`

### 5.2 KL 项：`KL(q(z|x) || N(0,I))`
对角高斯到标准正态的 KL（逐样本）：
- `kl_i = 0.5 * sum_k (exp(logvar_k) + mu_k^2 - 1 - logvar_k)`（对 `L` 求和）→ `(B,)`

### 5.3 总损失
- `loss = (recon + beta * kl).mean()`（`beta=1` 就是标准 VAE）

## 6. 形状示例（“图片 = H×W×3”）
取 `B=4, H=W=64, C=3, L=128`：
- `x_hw3`：`(4, 64, 64, 3)`
- `x`：`(4, 3, 64, 64)`
- `mu, logvar`：`(4, 128)`
- `z`：`(4, 128)`
- `x_hat`：`(4, 3, 64, 64)`
- `recon, kl`：`(4,)`

## 7. 最小 PyTorch 伪代码（带 shape）
```python
# x_hw3: (B,H,W,3) in [0,1]
x = x_hw3.permute(0, 3, 1, 2).contiguous()  # (B,3,H,W)

h = enc(x)                  # (B,hidden)
mu = fc_mu(h)               # (B,L)
logvar = fc_logvar(h)       # (B,L)

std = torch.exp(0.5 * logvar)     # (B,L)
eps = torch.randn_like(std)       # (B,L)
z = mu + std * eps                # (B,L)

x_hat = dec(z)               # (B,3,H,W)  (or x_logits)

recon = F.mse_loss(x_hat, x, reduction="none").sum(dim=(1,2,3))  # (B,)
kl = 0.5 * (logvar.exp() + mu.pow(2) - 1 - logvar).sum(dim=1)    # (B,)

loss = (recon + kl).mean()
```

## 8. Debug 清单（最常见的坑）
- `x` 的通道顺序（`(B,H,W,3)` vs `(B,3,H,W)`）是否统一
- `recon` 的 reduce 维度是否正确（`sum(dim=(1,2,3))` 对应 `C,H,W`）
- `logvar` 是否真的是 `log(sigma^2)`（别写成 `log(sigma)`）
- decoder 输出范围与重构 loss 是否匹配（`sigmoid` vs logits）
