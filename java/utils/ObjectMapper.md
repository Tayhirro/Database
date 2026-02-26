---
title: ObjectMapper（对象映射器）
date: "2026-02-06"
categories:
  - java
description: ObjectMapper 是 Jackson 库的核心类，用于 Java 对象 ↔ JSON 字符串 的互相转换（序列化/反序列化）。
---
# ObjectMapper（对象映射器）

## 一句话

ObjectMapper 是 Jackson 库的核心类，用于 **Java 对象 ↔ JSON 字符串** 的互相转换（序列化/反序列化）。

## 它是什么？

**拆开名字理解：**
- **Object** = Java 对象
- **Mapper** = 映射器（把 A 转换成 B）

```
┌─────────────────────────────────────────────────────────────────┐
│                      ObjectMapper = 翻译官                       │
│                                                                 │
│     Java 对象  ←──────────────────────────────→  JSON 字符串     │
│                       互相翻译                                   │
└─────────────────────────────────────────────────────────────────┘
```

## 核心功能

```
                    ObjectMapper
                         │
         ┌───────────────┴───────────────┐
         │                               │
         ▼                               ▼
   writeValueAsString()            readValue()
   对象 → JSON 字符串              JSON 字符串 → 对象
   （序列化）                       （反序列化）


具体例子：

┌──────────────────┐                      ┌──────────────────────────────┐
│ User 对象         │   ───────────→      │ JSON 字符串                   │
│                  │  writeValueAsString  │                              │
│  id = 1          │                      │ {"id":1,"name":"张三","age":25}│
│  name = "张三"    │                      │                              │
│  age = 25        │   ←───────────       │                              │
│                  │    readValue         │                              │
└──────────────────┘                      └──────────────────────────────┘
```

---

## 基础用法

### 1. 引入依赖

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.2</version>
</dependency>
```

### 2. 创建 ObjectMapper

```java
// 方式1：直接 new（每次 new 性能差，不推荐）
ObjectMapper mapper = new ObjectMapper();

// 方式2：单例模式（推荐）
public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    public static ObjectMapper getInstance() {
        return MAPPER;
    }
}

// 方式3：Spring 注入（SpringBoot 自动配置）
@Autowired
private ObjectMapper objectMapper;
```

### 3. 对象 → JSON（序列化）

```java
ObjectMapper mapper = new ObjectMapper();

// 对象转 JSON 字符串
User user = new User(1L, "张三", 25);
String json = mapper.writeValueAsString(user);
// 结果: {"id":1,"name":"张三","age":25}

// 对象转 byte[]
byte[] bytes = mapper.writeValueAsBytes(user);

// 对象写入文件
mapper.writeValue(new File("user.json"), user);

// 对象写入 OutputStream
mapper.writeValue(outputStream, user);
```

### 4. JSON → 对象（反序列化）

```java
ObjectMapper mapper = new ObjectMapper();

// JSON 字符串转对象
String json = "{\"id\":1,\"name\":\"张三\",\"age\":25}";
User user = mapper.readValue(json, User.class);

// 从文件读取
User user2 = mapper.readValue(new File("user.json"), User.class);

// 从 InputStream 读取
User user3 = mapper.readValue(inputStream, User.class);

// 从 byte[] 读取
User user4 = mapper.readValue(bytes, User.class);
```

### 5. 处理集合类型

```java
ObjectMapper mapper = new ObjectMapper();

// List 序列化
List<User> users = Arrays.asList(
    new User(1L, "张三", 25),
    new User(2L, "李四", 30)
);
String json = mapper.writeValueAsString(users);
// [{"id":1,"name":"张三","age":25},{"id":2,"name":"李四","age":30}]

// List 反序列化（需要 TypeReference）
String json2 = "[{\"id\":1,\"name\":\"张三\"},{\"id\":2,\"name\":\"李四\"}]";
List<User> userList = mapper.readValue(json2, new TypeReference<List<User>>() {});

// Map 反序列化
String json3 = "{\"name\":\"张三\",\"age\":25}";
Map<String, Object> map = mapper.readValue(json3, new TypeReference<Map<String, Object>>() {});
```

---

## 常用配置

### 1. 忽略未知字段（最常用！）

```java
// 问题：JSON 中有字段，但 Java 类中没有 → 默认报错！
String json = "{\"id\":1,\"name\":\"张三\",\"extraField\":\"xxx\"}";
User user = mapper.readValue(json, User.class);  // 报错！User 没有 extraField

// 解决：忽略未知字段
ObjectMapper mapper = new ObjectMapper();
mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

// 或者在类上加注解
@JsonIgnoreProperties(ignoreUnknown = true)
public class User { ... }
```

### 2. 空值处理

```java
// 序列化时不输出 null 值的字段
mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

User user = new User();
user.setId(1L);
user.setName(null);  // null 值
String json = mapper.writeValueAsString(user);
// 结果: {"id":1}  ← name 字段不输出
```

### 3. 日期格式化

```java
// 默认：日期输出为时间戳
// {"createTime":1699012800000}

// 配置为格式化字符串
mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
// {"createTime":"2023-11-03 12:00:00"}

// 或者在字段上加注解
public class User {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
```

### 4. 驼峰 ↔ 下划线转换

```java
// Java 用驼峰：userName
// JSON 用下划线：user_name

mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

public class User {
    private String userName;  // Java 驼峰
}

String json = mapper.writeValueAsString(user);
// {"user_name":"张三"}  ← JSON 下划线
```

### 5. 格式化输出（美化 JSON）

```java
// 默认：紧凑输出
// {"id":1,"name":"张三","age":25}

// 格式化输出（便于阅读）
mapper.enable(SerializationFeature.INDENT_OUTPUT);
// {
//   "id" : 1,
//   "name" : "张三",
//   "age" : 25
// }
```

### 6. 允许序列化私有字段

```java
// 默认：只能序列化 public 字段或有 getter 的字段
// 配置后：private 字段也能序列化

mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
```

---

## 常用注解

```java
public class User {
    
    // 指定 JSON 字段名（字段名映射）
    @JsonProperty("user_id")
    private Long id;
    
    // 序列化时忽略该字段
    @JsonIgnore
    private String password;
    
    // 日期格式化
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;
    
    // 反序列化时的别名（多个名字都能识别）
    @JsonAlias({"name", "userName", "user_name"})
    private String name;
    
    // 空值时使用默认值
    @JsonSetter(nulls = Nulls.SKIP)
    private Integer age = 0;
}

// 类级别：忽略未知字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class User { ... }

// 类级别：只序列化指定字段
@JsonIgnoreProperties({"password", "salt"})
public class User { ... }
```

---

## 进阶配置：activateDefaultTyping

### 问题：反序列化时丢失类型信息

```java
// 存的时候
Object obj = new User(1L, "张三", 25);
String json = mapper.writeValueAsString(obj);
// {"id":1,"name":"张三","age":25}  ← 没有类型信息！

// 取的时候
Object result = mapper.readValue(json, Object.class);
// result 是 LinkedHashMap，不是 User！
// 因为 Jackson 不知道原来是什么类型
```

### 解决：序列化时带上类名

```java
ObjectMapper mapper = new ObjectMapper();
mapper.activateDefaultTyping(
    LaissezFaireSubTypeValidator.instance,   // 类型验证器
    ObjectMapper.DefaultTyping.NON_FINAL,    // 非 final 类都带类型
    JsonTypeInfo.As.PROPERTY                 // 类型信息作为 @class 属性
);

// 现在序列化会带上类名
Object obj = new User(1L, "张三", 25);
String json = mapper.writeValueAsString(obj);
// {"@class":"com.example.User","id":1,"name":"张三","age":25}
//    ↑ 类型信息

// 反序列化时能还原成正确类型
Object result = mapper.readValue(json, Object.class);
// result 是 User 类型！
```

### 在 Redis 中的应用

```java
@Bean
public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    
    // 配置 Jackson 序列化器
    Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
    
    ObjectMapper mapper = new ObjectMapper();
    mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    mapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        ObjectMapper.DefaultTyping.NON_FINAL,
        JsonTypeInfo.As.PROPERTY
    );
    serializer.setObjectMapper(mapper);
    
    template.setValueSerializer(serializer);
    template.setHashValueSerializer(serializer);
    
    return template;
}
```

---

## 工具类封装

```java
public class JsonUtils {
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    static {
        // 忽略未知字段
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许序列化空对象
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 日期格式
        MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 不输出 null 值
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
    
    /**
     * 对象转 JSON 字符串
     */
    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }
    
    /**
     * JSON 转对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }
    
    /**
     * JSON 转 List
     */
    public static <T> List<T> fromJsonList(String json, Class<T> clazz) {
        try {
            JavaType type = MAPPER.getTypeFactory()
                .constructCollectionType(List.class, clazz);
            return MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }
    
    /**
     * JSON 转 Map
     */
    public static Map<String, Object> fromJsonMap(String json) {
        try {
            return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }
    
    /**
     * 对象转 Map
     */
    public static Map<String, Object> objectToMap(Object obj) {
        return MAPPER.convertValue(obj, new TypeReference<Map<String, Object>>() {});
    }
    
    /**
     * Map 转对象
     */
    public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) {
        return MAPPER.convertValue(map, clazz);
    }
}
```

**使用示例：**

```java
// 对象 → JSON
User user = new User(1L, "张三", 25);
String json = JsonUtils.toJson(user);

// JSON → 对象
User user2 = JsonUtils.fromJson(json, User.class);

// JSON → List
String listJson = "[{\"id\":1},{\"id\":2}]";
List<User> users = JsonUtils.fromJsonList(listJson, User.class);

// 对象 → Map（存 Redis Hash 时很有用）
Map<String, Object> map = JsonUtils.objectToMap(user);
redisTemplate.opsForHash().putAll("user:1", map);
```

---

## 常见问题

### 1. 循环引用导致栈溢出

```java
// 问题：A 引用 B，B 又引用 A
public class Parent {
    private List<Child> children;
}
public class Child {
    private Parent parent;  // 循环引用！
}
// mapper.writeValueAsString(parent) → StackOverflowError

// 解决：用 @JsonIgnore 打断循环
public class Child {
    @JsonIgnore
    private Parent parent;
}

// 或者用 @JsonManagedReference + @JsonBackReference
public class Parent {
    @JsonManagedReference
    private List<Child> children;
}
public class Child {
    @JsonBackReference
    private Parent parent;
}
```

### 2. 泛型类型擦除

```java
// 错误：直接用 List.class
List<User> users = mapper.readValue(json, List.class);  
// 结果是 List<LinkedHashMap>，不是 List<User>！

// 正确：使用 TypeReference 保留泛型信息
List<User> users = mapper.readValue(json, new TypeReference<List<User>>() {});
```

### 3. 枚举序列化

```java
public enum Status {
    ACTIVE, INACTIVE
}

// 默认序列化为字符串名称
// {"status":"ACTIVE"}

// 如果想序列化为数字
public enum Status {
    ACTIVE(1), INACTIVE(0);
    
    @JsonValue  // 序列化时用这个值
    private int code;
}
// {"status":1}
```

---

## 与其他 JSON 库对比

| 特性 | Jackson | Gson | Fastjson |
|------|---------|------|----------|
| 性能 | 最快 | 较慢 | 快 |
| 功能 | 最丰富 | 够用 | 丰富 |
| Spring 默认 | ✓ | ✗ | ✗ |
| 注解支持 | 丰富 | 较少 | 丰富 |
| 安全性 | 好 | 好 | 曾有漏洞 |
| 推荐度 | 首选 | 备选 | 谨慎使用 |

---

## 把新概念挂回框架

```
Java
 └── utils（工具类）
      └── ObjectMapper（JSON 对象映射器）
           ├── 序列化：对象 → JSON
           ├── 反序列化：JSON → 对象
           └── 应用场景
                ├── Redis 存储
                ├── HTTP 接口
                └── 配置文件解析
```
