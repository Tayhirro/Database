# 嵌套期望与全期望公式（Iterated Expectation & Law of Total Expectation）

## 1. 一句话
- 嵌套期望是"先对一部分变量求期望，再对剩余变量求期望"的操作；全期望公式说明这个过程等价于直接对所有变量求期望

---

## 2. 全期望公式（核心定理）

### 基本形式
$$E[Y] = E[E[Y|X]]$$

**读法**：
- 内层：$E[Y|X]$ 是关于 $X$ 的随机变量
- 外层：再对 $X$ 求期望
- 结果：等于直接对 $Y$ 求期望

**直觉**：
- 把全部概率分解到各个 $X=x$ 的情况
- 在每个 $X=x$ 下算 $Y$ 的平均
- 再把所有情况加权平均

---

### 证明（连续情形）

$$E[E[Y|X]] = \int E[Y|X=x] \cdot p(x) \, dx$$

展开内层期望：
$$= \int \left( \int y \cdot p(y|x) \, dy \right) p(x) \, dx$$

交换积分顺序（Fubini定理）：
$$= \iint y \cdot p(y|x) p(x) \, dy \, dx$$

使用链式法则 $p(y|x)p(x) = p(x,y)$：
$$= \iint y \cdot p(x,y) \, dy \, dx$$

对 $x$ 积分（边缘化）：
$$= \int y \left( \int p(x,y) \, dx \right) dy = \int y \cdot p(y) \, dy = E[Y]$$

---

## 3. 多层嵌套

### 三变量情形
$$E[Y] = E[E[E[Y|X,Z] | X]]$$

**操作顺序**：
1. 知道 $(X,Z)$ → $E[Y|X,Z]$
2. "忘记" $Z$，只保留 $X$ → $E[E[Y|X,Z] | X]$
3. 再"忘记" $X$ → 最终得到 $E[Y]$

**简化**：
$$E[E[Y|X,Z] | X] = E[Y|X]$$

因此：
$$E[Y] = E[E[Y|X]]$$

---

### 任意顺序
$$E[E[Y|X,Z] | X] = E[Y|X]$$
$$E[E[Y|X,Z] | Z] = E[Y|Z]$$

**原则**：逐步"丢弃"条件变量。

---

## 4. 你在VAE推导中看到的形式

### 训练目标展开

训练时计算：
$$E_{x \sim p_{data}(x)} E_{z \sim q_\phi(z|x)} [g(x,z)]$$

**第一步：嵌套期望的定义展开**
$$= E_{x \sim p_{data}(x)} \left[ \int g(x,z) q_\phi(z|x) \, dz \right]$$

**第二步：外层期望展开**
$$= \int \left( \int g(x,z) q_\phi(z|x) \, dz \right) p_{data}(x) \, dx$$

**第三步：合并成二重积分**
$$= \iint g(x,z) q_\phi(z|x) p_{data}(x) \, dz \, dx$$

**第四步：定义联合分布**

定义 $q_\phi(x,z) := p_{data}(x) q_\phi(z|x)$，则：
$$= \iint g(x,z) q_\phi(x,z) \, dz \, dx = E_{(x,z) \sim q_\phi(x,z)} [g(x,z)]$$

**为什么这样做**：
- 从嵌套期望变成单层期望
- 更方便后续推导（如求最优解）

---

## 5. 关键工具：Fubini定理

### Fubini定理（交换积分顺序）

若 $\iint |f(x,y)| \, dx \, dy < \infty$，则：
$$\int \left( \int f(x,y) \, dy \right) dx = \int \left( \int f(x,y) \, dx \right) dy$$

**应用到概率**：
$$E_X[E_{Y|X}[g(X,Y)]] = E_Y[E_{X|Y}[g(X,Y)]]$$

（只要期望存在）

**实际意义**：
- 可以先对 $X$ 求期望再对 $Y$
- 也可以先对 $Y$ 求期望再对 $X$
- 结果相同

---

## 6. 从嵌套期望到联合分布

### 一般模式

给定采样过程：
1. $X \sim p(x)$
2. $Z \sim p(z|x)$

则嵌套期望：
$$E_{x \sim p(x)} E_{z \sim p(z|x)} [g(x,z)]$$

可以改写为联合期望：
$$E_{(x,z) \sim p(x,z)} [g(x,z)]$$

其中 $p(x,z) = p(x) p(z|x)$（链式法则）。

---

### 带条件的情形

给定条件 $C$：
1. $Z \sim p(z|C)$
2. $X \sim p(x|z, C)$

则：
$$E_{z \sim p(z|C)} E_{x \sim p(x|z,C)} [g(x,z)]$$
$$= E_{(x,z) \sim p(x,z|C)} [g(x,z)]$$

其中：
$$p(x,z|C) = p(x|z,C) p(z|C)$$

**你的例子**：
$$P(X_{t+1}, Z | C) = P(X_{t+1} | Z, C) P(Z | C)$$

这就是链式法则的条件版本。

---

## 7. 最优化问题中的应用

### 问题：最小化嵌套期望

$$\min_f E_{x \sim p(x)} E_{z \sim q(z|x)} [(y - f(z))^2]$$

其中 $y = h(x,z)$ 是某个函数。

**步骤1：固定 $q$，对 $f$ 优化**

$$\min_f \iint (h(x,z) - f(z))^2 q(z|x) p(x) \, dz \, dx$$

**步骤2：改写成对 $z$ 的期望**

$$= \min_f \int \left( \int (h(x,z) - f(z))^2 q(z|x) p(x) \, dx \right) dz$$

定义 $q(x,z) = p(x)q(z|x)$，边缘化得：
$$q(z) = \int q(z|x) p(x) \, dx$$

条件分布：
$$q(x|z) = \frac{q(z|x) p(x)}{q(z)}$$

**步骤3：逐点优化**

对每个固定的 $z$，最小化：
$$\min_{f(z)} E_{x \sim q(x|z)} [(h(x,z) - f(z))^2]$$

最优解：
$$f^*(z) = E_{x \sim q(x|z)} [h(x,z)]$$

这就是 VAE 中"最优 decoder 是条件期望"的来源。

---

## 8. 实际计算：Monte Carlo估计

### 嵌套采样

算法：
1. 从 $p(x)$ 采样 $x_1, \ldots, x_n$
2. 对每个 $x_i$，从 $p(z|x_i)$ 采样 $z_{i1}, \ldots, z_{im}$
3. 估计：
$$E_x E_{z|x} [g(x,z)] \approx \frac{1}{n} \sum_{i=1}^n \frac{1}{m} \sum_{j=1}^m g(x_i, z_{ij})$$

---

### 联合采样（等价）

算法：
1. 从 $p(x,z)$ 采样 $(x_1,z_1), \ldots, (x_N, z_N)$
2. 估计：
$$E_{x,z} [g(x,z)] \approx \frac{1}{N} \sum_{k=1}^N g(x_k, z_k)$$

**实现**：先采样 $x_k \sim p(x)$，再采样 $z_k \sim p(z|x_k)$。

---

## 9. 相关模块

- [期望](Expectation.md)：基本定义
- [条件期望](ConditionalExpectation.md)：$E[Y|X]$ 的含义
- [链式法则](ChainRule.md)：$p(x,z) = p(x)p(z|x)$

---

## 10. 速查

| 公式 | 含义 |
|------|------|
| $E[Y] = E[E[Y\|X]]$ | 全期望公式 |
| $E_x E_{z\|x} [g] = E_{x,z} [g]$ | 嵌套期望 = 联合期望 |
| $\int \int f \, dy \, dx = \int \int f \, dx \, dy$ | Fubini定理（可交换） |
| $p(x,z) = p(x)p(z\|x)$ | 嵌套采样的联合分布 |
| $\min_f E_{z\|x}[(y-f(z))^2]$ 最优解 | $f^*(z) = E[y\|z]$ |

---

## 11. 常见误区

- **误区1**：认为嵌套期望无法化简（实际上可以变成联合期望）
- **误区2**：混淆采样顺序和积分顺序
- **误区3**：忘记 Fubini 定理的条件（需要绝对可积）
- **误区4**：认为 $E_x E_{z|x}$ 和 $E_z E_{x|z}$ 相同（需要对应的联合分布相同）
- **误区5**：最优化时忘记是"逐点优化"（对每个 $z$ 分别优化）
