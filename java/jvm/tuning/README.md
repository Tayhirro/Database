# 性能调优（Tuning）

导航：[jvm/README.md](../README.md) | [索引.md](索引.md)

本目录包含 JVM 性能调优参数、工具与分析方法。

---

## 子目录

| 目录 | 说明 |
|------|------|
| [parameters/](parameters/) | JVM 参数 |
| [tools/](tools/) | 诊断工具 |
| [analysis/](analysis/) | 分析方法 |

---

## 条目列表

### 参数（parameters/）
- [HeapParameters](parameters/HeapParameters.md)：堆参数（-Xms、-Xmx、-Xmn）
- [GCParameters](parameters/GCParameters.md)：GC 参数
- [JITParameters](parameters/JITParameters.md)：JIT 参数

### 工具（tools/）
- [jps](tools/jps.md)：进程查看
- [jstat](tools/jstat.md)：统计监控
- [jmap](tools/jmap.md)：内存映射
- [jstack](tools/jstack.md)：线程转储
- [jcmd](tools/jcmd.md)：命令行诊断
- [jconsole](tools/jconsole.md)：可视化监控
- [VisualVM](tools/VisualVM.md)：综合分析工具

### 分析（analysis/）
- [GCLog](analysis/GCLog.md)：GC 日志分析
- [HeapDump](analysis/HeapDump.md)：堆转储分析
- [ThreadDump](analysis/ThreadDump.md)：线程转储分析

---

## 关系

- 上级：[JVM](../README.md)
- 相关：[GC](../gc/README.md)（GC 参数与日志）
- 相关：[Runtime](../runtime/README.md)（内存与线程分析）
