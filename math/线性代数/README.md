# 线性代数（Linear Algebra）笔记组织说明（可扩展 & 速查）

这部分建议按三条主线组织：  
1) **对象**：向量空间、线性映射、矩阵  
2) **结构**：内积/正交、二次型/半正定（PSD）  
3) **工具**：分解（特征分解/谱定理/SVD/QR）与应用（最小二乘、PCA、核方法）

---

## 1. 放在哪里最好（先做规划）
你提到的“核方法/核函数判定链路”，本质依赖的是 **Gram 矩阵** 与 **半正定矩阵（PSD）**：
- 建议放在 `math/线性代数/modules/KernelMethods.md`（讲 kernel trick、Gram 矩阵、判定套路）
- 其底座概念独立成一页：`math/线性代数/modules/PSD.md`（PSD/二次型/分解/等价刻画）

如果后续要深入到 RKHS（再生核希尔伯特空间），可以再：
- 在 `math/泛函/` 里加一页（更“泛函分析味”）
- 或留在本目录扩展为“核方法（线性代数视角）→ RKHS（泛函视角）”两段

---

## 2. 目录结构（入口 → 索引 → 概念图 → 模块）
- `math/线性代数/README.md`：入口与组织方式（本页）
- `math/线性代数/索引.md`：术语索引（中文｜英文｜一句话｜链接）
- `math/线性代数/概念图.md`：概念关系图（依赖链/常用路线）

横切模块（解题与应用工具箱）：
- `math/线性代数/modules/Linearity.md`
- `math/线性代数/modules/PSD.md`
- `math/线性代数/modules/KernelMethods.md`

例子与练习（最小工作例子/自测）：
- `math/线性代数/examples/Examples.md`
- `math/线性代数/exercises/Exercises.md`

---

## 3. 建议学习路线（按“你现在缺哪块就读哪块”）
- 基础：向量空间、线性无关/基/维数、线性映射与矩阵表示
- 线性方程组：秩、消元、可解性、最小二乘
- 内积与正交：Gram–Schmidt、正交投影、QR
- 分解：特征值/对角化（对称矩阵→谱定理）、SVD（最通用）
- PSD 与二次型：`x^T A x`、Cholesky、Gram 矩阵
- 核方法：把“高维内积”换成“核函数”，核心判定链路见 `modules/KernelMethods.md`
