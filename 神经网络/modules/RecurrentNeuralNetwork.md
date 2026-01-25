# RNN（Recurrent Neural Network）

## 1. 一句话
- 处理序列 `x_{1:T}` 的神经网络：用共享参数在时间上递推隐藏状态 `h_t`，把“历史信息”压进一个可更新的记忆里。

## 2. 定义 / 公式（最常用写法）

### 2.1 Vanilla RNN
- 给定输入 `x_t`、上一时刻隐藏状态 `h_{t-1}`：
  - $$h_t = \phi(W_x x_t + W_h h_{t-1} + b)$$
  - $$y_t = g(W_y h_t + b_y)\quad (\text{可选})$$
- 训练通常用 BPTT（Backprop Through Time）。长序列容易出现梯度消失/爆炸（常配 gradient clipping）。

### 2.2 LSTM（Long Short-Term Memory）
- 核心：引入 cell state `c_t` + 门控（忘记/写入/输出）来稳定长期信息传递。
- 常用写法：
  - $$f_t=\sigma(W_f[h_{t-1},x_t]+b_f),\ i_t=\sigma(W_i[h_{t-1},x_t]+b_i),\ o_t=\sigma(W_o[h_{t-1},x_t]+b_o)$$
  - $$g_t=\tanh(W_g[h_{t-1},x_t]+b_g)$$
  - $$c_t=f_t\odot c_{t-1}+i_t\odot g_t,\quad h_t=o_t\odot\tanh(c_t)$$

### 2.3 GRU（Gated Recurrent Unit）
- 你可以把 GRU 看作 LSTM 的轻量化门控 RNN：没有显式 `c_t`，用 update/reset 两个门来控制“保留旧记忆 vs 写入新信息”。
- 常用写法：
  - $$z_t=\sigma(W_z[h_{t-1},x_t]+b_z),\ r_t=\sigma(W_r[h_{t-1},x_t]+b_r)$$
  - $$\tilde{h}_t=\tanh(W_h[r_t\odot h_{t-1},x_t]+b_h)$$
  - $$h_t=(1-z_t)\odot h_{t-1}+z_t\odot\tilde{h}_t$$

## 3. 直觉（为什么能做序列）
- `h_t` 是“可学习的状态变量”：每步读入 `x_t`，用同一套参数更新一次，相当于在时间上做递归的特征提取。
- LSTM/GRU 的门控本质是“让网络学会什么时候该忘、什么时候该记”，从而更稳地建模长期依赖。

## 4. 常用变体 / 记号差异（GRU 放这里最自然）
- **LSTM vs GRU 怎么选**
  - LSTM 表达力更强、门更多，常在更长依赖/更复杂序列里更稳。
  - GRU 参数更少、计算更省，很多工程里作为默认 RNN 编码器（尤其是序列不长或数据量不大时）。
- **双向 RNN（BiRNN / BiLSTM / BiGRU）**：同时用正向与反向递推，适合离线编码（有全序列），不适合严格在线预测。
- **Stacked / Multi-layer RNN**：把 RNN 堆多层增强表达力（注意 hidden size 与过拟合）。
- **输出方式**：
  - many-to-one：用 `h_T` 当序列 embedding（分类/意图识别/轨迹编码常见）
  - many-to-many：输出每步 `y_t`（序列标注/逐步预测）

## 5. 在哪些模型里出现
- 经典 NLP：语言模型、Seq2Seq（Transformer 之前的主力）。
- 时间序列：预测/插补/异常检测。
- 轨迹预测：Social-LSTM、edgeRNN/nodeRNN（把历史轨迹编码成运动特征），以及“先用 GRU/LSTM 把时间压成向量，再做图/注意力交互”的 summary-level 方案。

## 6. 速查
- 关键词：BPTT、vanishing/exploding gradients、gradient clipping、teacher forcing、hidden state init、padding/mask、BiLSTM。
- 常见坑：
  - 用绝对坐标直接喂 RNN 容易学到坐标系偏置：很多任务更偏好输入位移 `Δx, Δy`/速度等相对量。
  - rollout 预测的 exposure bias：训练时喂 GT、推理时喂预测会有分布漂移（可用 scheduled sampling/非自回归方案缓解）。

