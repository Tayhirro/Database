# CLIP（Contrastive Language–Image Pretraining）

## 1. 一句话
- 图像编码器 + 文本编码器的双塔模型，通过对比学习把图文表征对齐。

## 2. 结构
- Image Encoder：ResNet/ViT（看具体实现）
- Text Encoder：Transformer
- 相似度：通常是归一化后的 dot-product / cosine

## 3. 训练目标（对比学习）
- 入口：`modules/ContrastiveLearning.md`
- batch 内构造 `N×N` 相似度矩阵，正例在对角线，做双向交叉熵

## 4. 推理（zero-shot）
- 把类别写成 prompt（多模板可集成），用文本编码器得到类向量
- 用图像向量与各类向量相似度做分类

## 5. 常见坑
- prompt 选择影响很大（尤其是零样本）
- 归一化与温度 `τ` 的处理要和训练一致

