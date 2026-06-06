---
title: 干预（Intervention / do-operator）
date: "2026-03-26"
categories:
  - 因果推断
description: 干预不是条件化；它是在 SCM 里把某个结构方程替换为常量赋值，得到新模型。
---
# 干预（Intervention / do-operator）

## 1. 一句话

- 干预的本质不是“看见 `X=x`”，而是“把 `X` 强行设成 `x`”。

## 2. 记号

- 观测：`P(Y|X=x)`
- 干预：`P(Y|do(X=x))`

这两个量一般不相等。

## 3. 在 SCM 里的定义

设原模型为：

$$
M = (U, V, F, P(U))
$$

其中某个变量 `X` 的结构方程是：

$$
X = f_X(PA_X, U_X)
$$

做干预 `do(X=x)` 后，得到新模型：

$$
M_x = (U, V, F_x, P(U))
$$

其中 `F_x` 把原来生成 `X` 的那条方程替换为常量赋值：

$$
X := x
$$

直觉上等价于：切断所有指向 `X` 的入边，再把 `X` 钉死为 `x`。

## 4. 为什么它不等于条件化

- `P(Y|X=x)`：只是在自然世界里挑出“碰巧 `X=x` 的样本”
- `P(Y|do(X=x))`：是把世界机制改掉后，再看 `Y`

如果 `X` 受到混杂因素影响，那么条件化会把混杂也一起带进去；干预则不会。

## 5. 一个最小例子

设：

- `Z`：病情严重程度
- `X`：是否吃药
- `Y`：是否恢复

图结构：

- `Z -> X`
- `Z -> Y`
- `X -> Y`

此时：

- `P(Y|X=1)` 混进了“病重的人更可能吃药”这件事
- `P(Y|do(X=1))` 才更接近“强行给药会怎样”

## 6. 和相关页面的关系

- `SCM` 定义世界对象本身：见 [../structures/StructuralCausalModel.md](../structures/StructuralCausalModel.md)
- 图上判断怎样调整：见 [BackdoorCriterion.md](BackdoorCriterion.md)
- 更强的问题“同一个体如果没吃药会怎样”：见 [Counterfactual.md](Counterfactual.md)

## 7. 速查

- 干预 = 改方程，不是加条件
- `do(X=x)` = 切断 `X` 的入边 + 固定 `X=x`
- 因果推断最核心的分界线就是：`see` 不等于 `do`
