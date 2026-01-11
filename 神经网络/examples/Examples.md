# 例子库（最小工作例子）

## AE / VAE
- 用一个小数据集（MNIST/CIFAR 子集）跑通 AE：重构能收敛、潜空间可视化（t-SNE/2D）
- 跑通 VAE：能看到 KL 项与重构项的 trade-off（可记录 β-VAE）
- VAE tensor 级 walkthrough（RGB 图片 H×W×3）：[VAE_TensorLevelExample.md](VAE_TensorLevelExample.md)

## Diffusion
- 跑通一个最小 DDPM：能从噪声采样出结构化图像（哪怕很糊）

## BERT / CLIP
- BERT：做一次分类/NER 微调，记录 tokenizer 与 mask 处理
- CLIP：做一次 zero-shot 分类（prompt 模板对比）
