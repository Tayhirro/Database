


##  qwen3
目标1：把 **thinking / non-thinking** 两种模式融合到同一个模型里，而且还能用 **thinking budget** 控制“想多久”
目标2：用 **strong-to-weak distillation** 把大模型的能力高效迁移给小模型，避免每个小模型都完整走一遍昂贵的四阶段训练

### Stage 1 Long-CoT Cold Start
他们先构造一个以 **数学、代码、逻辑推理、STEM** 为主的数据集，每个题目都要配有可验证答案或测试用例
- **冷启动**：在正式进入更强的优化阶段（这里主要是 Reasoning RL）之前，先用一小批经过严格筛选、可验证、高质量的长链推理样本，对模型做一次准备性初始化，使模型先获得“基础推理模式（foundational reasoning patterns）”
#### query filtering
用 Qwen2.5-72B-Instruct 去删掉不易验证的问题、多子问题、纯开放式生成题，不需要 CoT 也能答对”的简单题
#### response filtering
让 QwQ-32B 对每个 query 生成多个候选答案，再删掉错误答案、明显猜测、重复啰嗦、thinking 和 summary 不一致、语言风格混乱等样本

目的：**基础 reasoning pattern 注进去，给后面的 RL 留出提升空间**，将模型推理能力对齐到一个初始化好的空间


### Stage 2：Reasoning RL
使用的是一组专门挑选出来的 **query-verifier pairs**，没在冷启动用过、对冷启动模型是“可学的”、尽量难、而且要覆盖广泛子领域。使用 **GRPO** 来更新模型参数

- 引入 **off-policy training** 提高样本效率
- 用 **大 batch** 和 **每个 query 更多 rollout**
- 通过控制 **entropy** 去平衡 exploration 和 exploitation，避免 RL 过程不稳定