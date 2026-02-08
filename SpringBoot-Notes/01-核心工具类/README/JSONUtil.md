# JSONUtil - JSON 处理工具类

> **来源**: `cn.hutool.json.JSONUtil` (Hutool) / `com.fasterxml.jackson.databind.ObjectMapper` (Jackson)  
> **作用**: 对象与 JSON 字符串之间的相互转换

---

## 1. 是什么？

**JSONUtil** 是 Hutool 提供的 JSON 处理工具类，封装了对象转 JSON、JSON 转对象等常用操作。Spring Boot 默认使用 **Jackson** 作为 JSON 处理器。

### 1.1 两种常用方案

| 方案 | 类 | 来源 | 特点 |
|------|-----|------|------|
| **Hutool** | `JSONUtil` | Hutool 工具包 | 简单、链式调用 |
| **Spring 官方** | `ObjectMapper` | Jackson | Spring 默认、功能强大 |

---

## 2. Hutool - JSONUtil

### 2.1 引入依赖

```xml
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.8.20</version>
</dependency>
```

### 2.2 对象转 JSON 字符串

```java
User user = new User(1L, "张三", 20);
String jsonStr = JSONUtil.toJsonStr(user);
// 结果：{"id":1,"name":"张三","age":20}

// List 转 JSON
List<User> users = Arrays.asList(user1, user2);
String jsonArray = JSONUtil.toJsonStr(users);
// 结果：[{"id":1,"name":"张三"}, {"id":2,"name":"李四"}]
```

### 2.3 JSON 字符串转对象

```java
String jsonStr = "{\"id\":1,\"name\":\"张三\",\"age\":20}";

// 转对象
User user = JSONUtil.toBean(jsonStr, User.class);

// 转 List
List<User> users = JSONUtil.toList(jsonArray, User.class);

// 转 Map
Map<String, Object> map = JSONUtil.toBean(jsonStr, Map.class);
```

### 2.4 美化输出

```java
String prettyJson = JSONUtil.formatJsonStr(jsonStr);
```

### 2.5 常用配置

```java
// 忽略 null 值字段
JSONConfig config = JSONConfig.create()
    .setIgnoreNullValue(true);
String jsonStr = JSONUtil.toJsonStr(user, config);
```

---

## 3. Spring 官方 - ObjectMapper

### 3.1 获取 ObjectMapper

```java
@Autowired
private ObjectMapper objectMapper;
```

### 3.2 对象转 JSON 字符串

```java
User user = new User(1L, "张三", 20);
String jsonStr = objectMapper.writeValueAsString(user);

// 美化输出
String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
    .writeValueAsString(user);
```

### 3.3 JSON 字符串转对象

```java
// 转对象
User user = objectMapper.readValue(jsonStr, User.class);

// 转 List
List<User> users = objectMapper.readValue(jsonArray, 
    new TypeReference<List<User>>() {});
```

### 3.4 常用配置

```java
@Bean
public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    
    // 日期格式
    mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    
    // 忽略 null 值字段
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    
    // 忽略未知字段
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
    return mapper;
}
```

---

## 4. 实际应用场景

### 4.1 Redis 缓存存取对象

```java
// 存：对象转 JSON
User user = userService.getById(1L);
redisTemplate.opsForValue().set("user:1", JSONUtil.toJsonStr(user));

// 取：JSON 转对象
String json = redisTemplate.opsForValue().get("user:1");
User user = JSONUtil.toBean(json, User.class);
```

### 4.2 接口返回 JSON（Spring Boot 自动处理）

```java
@GetMapping("/user/{id}")
public Result getUser(@PathVariable Long id) {
    User user = userService.getById(id);
    return Result.ok(user);
    // Spring Boot 自动用 ObjectMapper 把 Result 对象转成 JSON
}
```

### 4.3 接收前端 JSON 参数

```java
@PostMapping("/user")
public Result saveUser(@RequestBody User user) {
    // Spring Boot 自动把前端传来的 JSON 转成 User 对象
    userService.save(user);
    return Result.ok();
}
```

---

## 5. 总结

| 功能 | Hutool JSONUtil | Jackson ObjectMapper |
|------|----------------|---------------------|
| **对象 → JSON** | `JSONUtil.toJsonStr(obj)` | `objectMapper.writeValueAsString(obj)` |
| **JSON → 对象** | `JSONUtil.toBean(json, User.class)` | `objectMapper.readValue(json, User.class)` |
| **JSON → List** | `JSONUtil.toList(json, User.class)` | `objectMapper.readValue(json, new TypeReference<List<User>>() {})` |
| **美化输出** | `JSONUtil.formatJsonStr(json)` | `objectMapper.writerWithDefaultPrettyPrinter()` |

**建议**：
- **简单项目**：用 Hutool JSONUtil，API 简洁
- **Spring Boot 项目**：主要用 Jackson，Hutool 作为辅助

**一句话**：JSON 工具类是 Java 对象和 JSON 字符串之间的**转换桥梁**，让你轻松处理前后端数据交互。

---

**参考**：
- Hutool JSONUtil：https://hutool.cn/docs/#/json/JSONUtil
- Jackson ObjectMapper：https://github.com/FasterXML/jackson-databind/
