---
type: concept
tags:
  - java/jvm
  - jvm
  - runtime
  - structure
  - method-area
---

# MethodArea（方法区）

## 一句话
方法区（Method Area）是 JVM 运行时数据区中用于承载“类级元信息与相关运行时结构”的抽象区域。

## 严格定义
方法区是 JVM 规范层面的逻辑概念，用于描述类元信息（例如运行时常量池、字段/方法描述、方法字节码等）及其相关结构的存放边界；其具体物理实现由 JVM 决定（例如在 HotSpot JDK 8+ 中由元空间 Metaspace 承载主要的类元数据）。

## 接口：数据 + 约束
- 数据：
  - 类元信息（类、字段、方法、常量池、注解等）
  -（实现相关）类加载器维度的元数据组织与缓存结构
- 输入：
  - 类加载/链接/初始化过程产生的元信息写入
- 输出：
  - 运行态对类元信息的读取视图（反射、解析、调用分派等）
- 约束：
  - 方法区的逻辑边界由 JVM 规范定义；容量、布局与回收语义由具体实现与参数决定（实现相关）。

## 常用构造/操作（仅列出接口与符号）
- 物理实现（JDK 8+）：Metaspace（见 [Metaspace.md](Metaspace.md)）

## 关系：上级/下级/等价/特例/推广
- 上级：Runtime / structure（运行时数据区结构）。
- 特例（实现）：Metaspace（见 [Metaspace.md](Metaspace.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → runtime → structure → MethodArea

