---
title: "图注意力网络（Graph Attention Network, GAT）"
date: "2026-01-26"
categories:
  - 神经网络
description: GAT 是把 attention 引入 图神经网络（GNN）消息传递 的一层：对每个节点，从邻居里“注意力加权”汇聚信息，从而在不固定邻居权重的情况下学习图上的表示。
---
# 图注意力网络（Graph Attention Network, GAT）

## 1. 一句话
- GAT 是把 **attention** 引入 **图神经网络（GNN）消息传递** 的一层：对每个节点，从邻居里“注意力加权”汇聚信息，从而在不固定邻居权重的情况下学习图上的表示。

## 2. 定义 / 公式（尽量给最常用的那版）
给定图 `G=(V,E)`，节点特征 `h_i∈R^F`，一层 GAT 的典型写法：

### 2.1 线性变换
- `z_i = W h_i`（`W∈R^{F'×F}`）

### 2.2 边注意力打分（邻居对）
- 对 `j∈N(i)`（常把自环也并入：`j∈N(i)∪{i}`），打分：
  $$
  e_{ij} = \mathrm{LeakyReLU}\big(a^\mathsf{T}[z_i \Vert z_j]\big)
  $$
  其中 `a` 是可学习向量，`[·||·]` 是拼接。

### 2.3 softmax 归一化（只在邻居集合内）
  $$
  \alpha_{ij} = \mathrm{softmax}_j(e_{ij})=\frac{\exp(e_{ij})}{\sum_{k\in N(i)\cup\{i\}}\exp(e_{ik})}
  $$

### 2.4 聚合更新
  $$
  h'_i = \sigma\Big(\sum_{j\in N(i)\cup\{i\}} \alpha_{ij}\, z_j\Big)
  $$

### 2.5 Multi-head（常用）
- `K` 个头并行，各自一套 `(W^k,a^k)`：
  - **concat**：`h'_i = ||_{k=1}^K h_i^{\prime(k)}`（中间层常用）
  - **average**：`h'_i = (1/K) Σ_k h_i^{\prime(k)}`（输出层常用）

### 2.6 Tensor 级 Toy Example（N=3）
下面给一个“张量怎么流”的例子（不纠结具体数值，先把 shape 和计算路径捋清楚）。设：
- 节点数 `N=3`，输入维 `F=2`，单头输出维 `F'=4`，头数 `K=2`
- 输入节点特征 `H ∈ R^{N×F}`（一行一个节点）：
  ```
  H: (3,2)
  h0=[...], h1=[...], h2=[...]
  ```
- 图用边表表示（消息方向 `j → i`），用 PyG 风格 `edge_index ∈ N^{2×E}`：
  ```
  # edge_index[0] = src(j), edge_index[1] = dst(i)
  edge_index: (2,E)
  src = edge_index[0]   # (E,)
  dst = edge_index[1]   # (E,)
  ```
  例如把 self-loop 也加上（实际很常见），取：
  ```
  src: [0,1,0,1,2,1,2]
  dst: [0,0,1,1,1,2,2]
  # 表示：0→0,1→0,0→1,1→1,2→1,1→2,2→2
  # 所以 N(0)={0,1}，N(1)={0,1,2}，N(2)={1,2}
  ```

**单头（k=1）**：
- 线性变换：`Z = H (W^T)`（等价于逐点 `z_i=W h_i`）
  ```
  W: (4,2)
  Z: (3,4)
  ```
- 取出每条边两端的特征：
  ```
  Zi = Z[dst]   # (E,4)  每条边的目标节点 i
  Zj = Z[src]   # (E,4)  每条边的源节点 j
  ```
- 拼接后打分（每条边一个标量）：
  ```
  concat = [Zi || Zj]      # (E,8)
  a: (8,)
  e = LeakyReLU(concat @ a)  # (E,)
  ```
- 在“同一个目标节点 i 的入边集合”内做 softmax（局部归一化）：
  ```
  α = softmax(e, group_by=dst)   # (E,)  每条边一个权重 α_{ij}
  ```
- 加权聚合（把邻居的 value 汇到目标节点上）：
  ```
  msg = α[:,None] * Zj          # (E,4)
  H1 = scatter_sum(msg, dst)    # (N,4)  对同一 dst 的边求和
  H1 = σ(H1)                    # (N,4)
  ```

**多头（K=2）**：
- 对每个头重复上面的流程得到 `H1, H2 ∈ R^{N×F'}`，最后
  - concat：`H' = [H1 || H2] ∈ R^{N×(K·F')}`（这里是 `(3,8)`）
  - average：`H' = (H1+H2)/2 ∈ R^{N×F'}`

## 3. 直觉（为什么这么设计）
- GCN/GraphSAGE 这类层本质是“邻居求和/平均 + 线性变换”，而 GAT 让模型学会：**不同邻居对我重要程度不同**，并用 `softmax` 做局部归一化，得到可解释但不必强解释的加权汇聚。
- 从注意力视角：GAT 就是 **带邻接掩码（mask）的 self-attention**，只不过注意力打分函数更“轻量”，并且操作对象是图的邻居集而不是全连接 token。

## 4. 常用变体 / 记号差异
- **GATv2**：改写打分函数，让注意力对 query/key 的交互更灵活（缓解原版的表达限制）；你会看到形如 `e_{ij}=a^T σ(W [h_i||h_j])` 的写法。
- **Edge-aware / EGAT**：把边特征 `e_{ij}`（距离/关系类型/时空边等）也输入打分或 value：`e_{ij}=f(h_i,h_j,e_{ij})`。
- **Residual / Norm / Dropout**：工程上常配残差、LayerNorm/BatchNorm、对 `α_{ij}` 做 dropout。
- **Graph Transformer**：更接近标准 Transformer（QK^T 点积、多层 FFN 等），但用图稀疏性做 mask；可把 GAT 看作其中一类轻量实现。

## 5. 在哪些模型里出现
- 引文网络节点分类、知识图谱/异构图、分子图表征、交通路网/社交网络建模等。
- 轨迹/时空任务里常见套路是：先做时间编码（RNN/TCN/Transformer），再用 GAT 在交互图上做空间消息传递（或反过来做时空交替）。

## 6. 速查
- 关键词：message passing、neighbor attention、multi-head、self-loop、mask、edge features、oversmoothing。
- 常见坑：
  - 忘记加 self-loop 会让节点“丢失自身特征”或训练不稳。
  - 大图上 `softmax` attention 的显存/时间成本来自边数 `|E|`：常用采样邻居、稀疏实现或分层图。
  - `α_{ij}` 的可视化不等于因果解释：它是模型内部权重，不必然对应“真实重要性”。
