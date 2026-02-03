# 类加载（Class Loading）

导航：[jvm/README.md](../README.md) | [索引.md](索引.md)

本目录包含 JVM 类加载机制与类加载器体系。

---

## 子目录

| 目录                           | 说明    |
| ---------------------------- | ----- |
| [mechanism/](mechanism/)     | 类加载过程 |
| [classloader/](classloader/) | 类加载器  |

---

## 条目列表

### 加载机制（mechanism/）
- [ClassLoadingProcess](mechanism/ClassLoadingProcess.md)：类加载全流程
- [Loading](mechanism/Loading.md)：加载阶段
- [Linking](mechanism/Linking.md)：链接阶段（验证-准备-解析）
- [Initialization](mechanism/Initialization.md)：初始化阶段

### 类加载器（classloader/）
- [ClassLoader](classloader/ClassLoader.md)：类加载器概述
- [BootstrapClassLoader](classloader/BootstrapClassLoader.md)：启动类加载器
- [ExtensionClassLoader](classloader/ExtensionClassLoader.md)：扩展类加载器
- [AppClassLoader](classloader/AppClassLoader.md)：应用类加载器
- [ParentDelegation](classloader/ParentDelegation.md)：双亲委派模型
- [ContextClassLoader](classloader/ContextClassLoader.md)：线程上下文类加载器

---

## 关系

- 上级：[JVM](../README.md)
- 下游：[Runtime](../runtime/README.md)（类加载后进入运行时）
- 相关：[MethodArea](../runtime/structure/MethodArea.md)（类元信息存储）
