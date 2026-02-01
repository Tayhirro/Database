# 执行引擎（Execution Engine）

导航：[jvm/README.md](../README.md) | [索引.md](索引.md)

本目录包含 JVM 字节码执行相关知识。

---

## 子目录

| 目录 | 说明 |
|------|------|
| [interpreter/](interpreter/) | 解释器 |
| [jit/](jit/) | 即时编译 |
| [bytecode/](bytecode/) | 字节码与栈帧 |

---

## 条目列表

### 解释器（interpreter/）
- [Interpreter](interpreter/Interpreter.md)：字节码解释器

### 即时编译（jit/）
- [JIT](jit/JIT.md)：即时编译概述
- [C1Compiler](jit/C1Compiler.md)：Client 编译器（快速编译）
- [C2Compiler](jit/C2Compiler.md)：Server 编译器（深度优化）
- [TieredCompilation](jit/TieredCompilation.md)：分层编译
- [HotSpotDetection](jit/HotSpotDetection.md)：热点探测
- [Optimization](jit/Optimization.md)：编译优化技术

### 字节码（bytecode/）
- [Bytecode](bytecode/Bytecode.md)：字节码指令集
- [StackFrame](bytecode/StackFrame.md)：栈帧结构
- [LocalVariableTable](bytecode/LocalVariableTable.md)：局部变量表
- [OperandStack](bytecode/OperandStack.md)：操作数栈

---

## 关系

- 上级：[JVM](../README.md)
- 相关：[CompilerThreads](../runtime/threading/CompilerThreads.md)（JIT 编译线程）
- 相关：[JVMStack](../runtime/structure/JVMStack.md)（栈帧存储位置）
