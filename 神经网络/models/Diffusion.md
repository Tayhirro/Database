# Diffusion（Diffusion Models / DDPM）

## 1. 一句话
- 通过“前向逐步加噪 + 学习反向去噪”来建模数据分布的生成模型族。

## 2. 核心设定（够用版）
- 前向过程（固定）：`q(x_t | x_{t-1})` 逐步往高斯噪声靠近
- 反向过程（学习）：`p_θ(x_{t-1} | x_t)` 或等价地学习 `ε_θ(x_t,t)` / `score`

## 3. 训练目标（常见写法）
- 预测噪声 `ε`：最常见、实现也最直观
- 也会看到：预测 `x_0`、预测 `v`（不同参数化）

## 4. 采样/推理
- 从 `x_T ~ N(0,I)` 开始，逐步反推到 `x_0`
- 常见加速：DDIM、ODE/SDE 视角

## 5. 扩展
- 条件扩散：classifier guidance / classifier-free guidance
- Latent Diffusion（先到潜空间再扩散）

