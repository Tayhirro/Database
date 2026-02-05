# BeanUtils - Bean 操作工具类

## 简介

`BeanUtils` 是 Spring Framework 提供的用于操作 Java Bean 的工具类，主要提供对象属性的拷贝、转换等功能。

## 核心方法

### 1. copyProperties - 属性拷贝

```java
// 基本用法：将 source 对象的属性拷贝到 target 对象
BeanUtils.copyProperties(source, target);

// 忽略某些属性
BeanUtils.copyProperties(source, target, "id", "createTime");
```

### 2. CopyOptions - 拷贝选项（Hutool 扩展）

```java
// 忽略 null 值拷贝
CopyOptions copyOptions = CopyOptions.create()
    .setIgnoreNullValue(true);
BeanUtil.copyProperties(source, target, copyOptions);

// 自定义字段映射()
CopyOptions copyOptions = CopyOptions.create()
    .setFieldMapping(MapUtil.of("userName", "name"));

// 忽略某些字段
CopyOptions copyOptions = CopyOptions.create()
    .setIgnoreProperties("password", "secretKey");
```

## 常见使用场景

### 场景 1：DTO 与 Entity 转换

```java
// Entity -> DTO
UserDTO dto = new UserDTO();
BeanUtils.copyProperties(userEntity, dto);

// DTO -> Entity
UserEntity entity = new UserEntity();
BeanUtils.copyProperties(userDTO, entity);
```

### 场景 2：忽略 null 值更新

```java
// 部分更新时，只更新非 null 字段
CopyOptions options = CopyOptions.create()
    .setIgnoreNullValue(true);
BeanUtil.copyProperties(updateDTO, existingEntity, options);
```

### 场景 3：批量拷贝

```java
// List<Entity> -> List<DTO>
List<UserDTO> dtoList = entityList.stream()
    .map(entity -> {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    })
    .collect(Collectors.toList());
```

## 注意事项

1. **属性名必须一致**：拷贝基于属性名匹配，大小写敏感
2. **类型必须兼容**：源对象和目标对象的属性类型需要兼容或可转换
3. **浅拷贝**：默认是浅拷贝，对象引用会被共享
4. **性能考虑**：大量数据拷贝时建议使用 MapStruct 等编译期生成工具

## 同类工具对比

| 工具 | 特点 | 推荐场景 |
|------|------|----------|
| Spring BeanUtils | 简单、轻量 | 简单属性拷贝 |
| Hutool BeanUtil | 功能丰富 | 需要更多自定义选项 |
| MapStruct | 编译期生成、高性能 | 大量数据转换 |
| ModelMapper | 灵活配置 | 复杂映射场景 |

## 依赖引入

### Spring 版本（内置）
无需额外依赖，Spring Framework 已包含

### Hutool 版本
```xml
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.8.20</version>
</dependency>
```

## 示例代码

见 [示例代码](./示例代码/) 目录下的 `BeanUtilsDemo.java`
