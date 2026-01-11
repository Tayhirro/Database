# AE（Autoencoder）

## 1. 一句话
- 用编码器把输入压缩成潜表示 `z`，再用解码器重构回 `x̂`，通过重构误差学习表示。

## 2. 目标
- 表征学习（压缩/去噪/特征提取）
- 数据重构（有损压缩的视角）

## 3. 结构（最常见）
- `z = Encoder(x)`
- `x̂ = Decoder(z)`

## 4. 损失
- 常见：`L = ||x - x̂||`（MSE）或 `-log p(x|z)`（把解码器当作概率模型）

## 5. 常见坑
- 过完备（overcomplete）AE 容易学到恒等映射：通常需要正则（稀疏/去噪/瓶颈/约束）

## 6. 扩展
- Denoising AE、Sparse AE、Contractive AE
- 概率化：VAE（见 `models/VAE.md`）

