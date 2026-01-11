# 神经网络（Neural Networks）知识库组织说明（可扩展 & 速查）

这部分建议按两层来组织：  
1) **模型族（models）**：AE / VAE / CVAE / Diffusion / BERT / CLIP ……  
2) **横切模块（modules）**：概率视角（KL/ELBO）、Attention、对比学习、训练技巧……

---

## 1. 目录结构（入口 → 索引 → 概念图 → 模块 → 模型 → 论文/实现/实验）
- `神经网络/README.md`：入口与组织方式（本页）
- `神经网络/索引.md`：层次化索引（中文｜英文｜一句话｜链接）
- `神经网络/概念图.md`：概念依赖图（“先学什么 → 再学什么”）

核心内容：
- `神经网络/modules/`：共用概念与工具箱（ELBO/KL/Attention/InfoNCE/Tokenization…）
- `神经网络/models/`：模型卡片（目标、结构、损失、训练流程、坑点、扩展）

资料与落地：
- `神经网络/papers/`：论文笔记（建议命名：`YYYY - FirstAuthor - Title.md`）
- `神经网络/implementations/`：代码阅读/复现记录（仓库结构、关键模块、调用链）
- `神经网络/experiments/`：实验日志（一次一个问题，记录配置与结论）
- `神经网络/examples/`：最小工作例子（能跑通就行）
- `神经网络/exercises/`：自测题（按模块/模型组织）
- `神经网络/_assets/`：图片与附件

---

## 2. 你提到的主题放哪里
- AE / VAE / CVAE：`models/AE.md`、`models/VAE.md`、`models/CVAE.md`
- Diffusion：`models/Diffusion.md`
- BERT：`models/BERT.md`
- CLIP：`models/CLIP.md`

它们常用的底座模块：
- VAE 系：`modules/ELBO.md`、`modules/KLDivergence.md`、`modules/ReparameterizationTrick.md`
- Transformer/BERT：`modules/Attention.md`、`modules/Tokenization.md`
- CLIP：`modules/ContrastiveLearning.md`
- few-shot / 度量学习：`modules/PrototypicalLearning.md`

---

## 3. 建议填坑路线（按“依赖链最短”）
- 先把 `索引.md` 补齐：每次遇到新术语加一行
- AE → VAE（ELBO + KL + 重参数化）→ CVAE（条件变量怎么进模型）
- Attention → Transformer Encoder → BERT（预训练目标与微调套路）
- 对比学习（InfoNCE）→ CLIP（双塔 + 相似度矩阵 + 温度系数）
- Diffusion：先抓住“前向加噪 + 反向去噪 + 预测噪声/score”三件事，再补采样细节

相关数学底座如果需要单独记：
- 线性代数：`../math/线性代数/README.md`
- 概率/信息论：你也可以在 `math/` 下新建一个概率目录，再从这里链接过去
