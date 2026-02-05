# Redis 配置详解

本文介绍 Spring Boot 中 Redis 的配置方法和最佳实践。

## 1. Maven 依赖

```xml
<dependencies>
    <!-- Spring Data Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    
    <!-- 连接池 -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-pool2</artifactId>
    </dependency>
    
    <!-- JSON 序列化 -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
</dependencies>
```

## 2. 基础配置（application.yml）

```yaml
spring:
  redis:
    # 基础连接配置
    host: localhost              # Redis服务器地址
    port: 6379                  # Redis端口
    password:                   # 密码，没有则留空
    database: 0                 # 数据库索引(0-15)
    timeout: 5000ms             # 操作超时时间
    
    # Lettuce 连接池配置（Spring Boot 2.x 默认）
    lettuce:
      pool:
        max-active: 8           # 最大活跃连接数
        max-idle: 8             # 最大空闲连接数
        min-idle: 0             # 最小空闲连接数
        max-wait: 1000ms        # 获取连接最大等待时间
```

## 3. 配置项详解

| 配置项 | 含义 | 建议值 |
|--------|------|--------|
| `host` | Redis服务器IP | localhost/实际IP |
| `port` | Redis端口 | 6379 |
| `password` | 认证密码 | 有就填，没有留空 |
| `database` | 数据库编号 | 0-15，默认0 |
| `timeout` | 命令执行超时 | 5000ms |
| `max-active` | 最大连接数 | 8-20 |
| `max-idle` | 最大空闲连接 | 同max-active |
| `min-idle` | 最小空闲连接 | 0-5 |
| `max-wait` | 等待连接超时 | 1000-5000ms |

## 4. 生产环境配置示例

```yaml
# 高并发生产环境
spring:
  redis:
    cluster:                    # 集群配置
      nodes: 
        - 192.168.1.10:6379
        - 192.168.1.11:6379
        - 192.168.1.12:6379
    password: your-password
    lettuce:
      pool:
        max-active: 50          # 根据并发量调整
        max-idle: 50
        min-idle: 10            # 预热连接
        max-wait: 3000ms
        time-between-eviction-runs: 60000ms
      shutdown-timeout: 100ms
```

## 5. 常见问题排查

### 问题1：Could not get a resource from the pool
**原因**：连接池满了或 Redis 服务器连不上  
**解决**：
1. 检查 Redis 服务器是否启动
2. 增加 max-active: 8 -> 20
3. 增加 max-wait: 1000ms -> 5000ms

### 问题2：Redis command timed out
**原因**：命令执行超过 timeout  
**解决**：
```yaml
spring:
  redis:
    timeout: 10000ms  # 增加到10秒
```

### 问题3：密码认证失败
```yaml
spring:
  redis:
    password: "123456"  # 确保和 redis.conf 里 requirepass 一致
```

## 6. 序列化配置

```java
@Configuration
public class RedisConfig {
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        
        // Key 使用 String 序列化
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        
        // Value 使用 JSON 序列化
        Jackson2JsonRedisSerializer<Object> jsonSerializer = 
            new Jackson2JsonRedisSerializer<>(Object.class);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        template.afterPropertiesSet();
        return template;
    }
}
```

## 7. Lettuce vs Jedis

| 特性 | Lettuce（推荐） | Jedis |
|------|----------------|-------|
| 线程安全 | ✅ 线程安全 | ❌ 非线程安全 |
| 异步支持 | ✅ 支持 Reactive | ❌ 阻塞式 |
| 自动重连 | ✅ 支持 | ❌ 需手动管理 |
| 性能 | 高（Netty） | 中等（BIO） |
| 包大小 | 较大（依赖Netty） | 较小 |

完整操作指南请查看：[Redis 完整指南](../02-数据访问/README/Redis-Complete-Guide.md)
