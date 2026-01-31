# 推理（Inference）

导航：[llm/README.md](../README.md) | [索引.md](索引.md)

本目录包含 LLM 的推理策略与加速技术。

---

## 子目录

| 目录 | 说明 |
|------|------|
| [decoding/](decoding/) | 解码策略 |
| [acceleration/](acceleration/) | 推理加速 |
| [quantization/](quantization/) | 模型量化 |

---

## 条目列表

### 解码策略
- [Sampling](decoding/Sampling.md)（温度采样、Top-k、Top-p）
- [BeamSearch](decoding/BeamSearch.md)
- [SpeculativeDecoding](decoding/SpeculativeDecoding.md)

### 加速
- [KVCache](acceleration/KVCache.md)
- [FlashAttention](acceleration/FlashAttention.md)
- [PagedAttention](acceleration/PagedAttention.md)
- [ContinuousBatching](acceleration/ContinuousBatching.md)

### 量化
- [Quantization](quantization/Quantization.md)
- [INT8](quantization/INT8.md)
- [GPTQ](quantization/GPTQ.md)
- [AWQ](quantization/AWQ.md)
