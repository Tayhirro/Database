# Builder Pattern 具体例子

## 场景：组装电脑

假设你要组装一台电脑，有很多可选配置：CPU、内存、硬盘、显卡等。如果没有Builder模式，代码会很丑陋。

---

## ❌ 不用Builder的痛苦写法

### 1. 构造函数地狱
```java
public class Computer {
    private String cpu;
    private int ram;      // GB
    private int ssd;      // GB
    private String gpu;
    private boolean hasBluetooth;
    private boolean hasWifi;
    
    // 构造函数1：只传必传参数
    public Computer(String cpu, int ram) {
        this(cpu, ram, 256, null, false, false);
    }
    
    // 构造函数2：加上硬盘
    public Computer(String cpu, int ram, int ssd) {
        this(cpu, ram, ssd, null, false, false);
    }
    
    // 构造函数3：加上显卡
    public Computer(String cpu, int ram, int ssd, String gpu) {
        this(cpu, ram, ssd, gpu, false, false);
    }
    
    // 构造函数4：全参数（噩梦！）
    public Computer(String cpu, int ram, int ssd, String gpu, 
                    boolean hasBluetooth, boolean hasWifi) {
        this.cpu = cpu;
        this.ram = ram;
        this.ssd = ssd;
        this.gpu = gpu;
        this.hasBluetooth = hasBluetooth;
        this.hasWifi = hasWifi;
    }
}

// 使用时：
// 我只想要CPU+内存+硬盘+蓝牙，不要显卡和WiFi
Computer pc = new Computer("i7", 16, 512, null, true, false);
// 问题1：第4个参数null是什么意思？容易看错位置！
// 问题2：true和false哪个是蓝牙哪个是WiFi？容易写反！
// 问题3：如果新增一个参数（比如机箱颜色），所有构造函数都要改！
```

### 2. JavaBean模式（也有问题）
```java
Computer pc = new Computer();
pc.setCpu("i7");
pc.setRam(16);
pc.setSsd(512);
pc.setHasBluetooth(true);
// 问题：对象可以处于"半成品"状态，用了未设置的字段会报错！
```

---

## ✅ 使用Builder的优雅写法

### 完整代码
```java
public class Computer {
    // 必需参数
    private final String cpu;
    private final int ram;
    
    // 可选参数（有默认值）
    private final int ssd;
    private final String gpu;
    private final boolean hasBluetooth;
    private final boolean hasWifi;
    
    // 私有构造函数，只能通过Builder创建
    private Computer(Builder builder) {
        this.cpu = builder.cpu;
        this.ram = builder.ram;
        this.ssd = builder.ssd;
        this.gpu = builder.gpu;
        this.hasBluetooth = builder.hasBluetooth;
        this.hasWifi = builder.hasWifi;
    }
    
    // 静态内部类：Builder
    public static class Builder {
        // 必需参数
        private final String cpu;
        private final int ram;
        
        // 可选参数（设置默认值）
        private int ssd = 256;          // 默认256GB
        private String gpu = null;      // 默认无独显
        private boolean hasBluetooth = false;
        private boolean hasWifi = true; // 默认有WiFi
        
        // 构造函数：只传必需参数
        public Builder(String cpu, int ram) {
            this.cpu = cpu;
            this.ram = ram;
        }
        
        // 链式调用：设置SSD
        public Builder setSsd(int ssd) {
            this.ssd = ssd;
            return this;  // 返回自己，支持链式调用
        }
        
        // 链式调用：设置显卡
        public Builder setGpu(String gpu) {
            this.gpu = gpu;
            return this;
        }
        
        // 链式调用：设置蓝牙
        public Builder setBluetooth(boolean hasBluetooth) {
            this.hasBluetooth = hasBluetooth;
            return this;
        }
        
        // 链式调用：设置WiFi
        public Builder setWifi(boolean hasWifi) {
            this.hasWifi = hasWifi;
            return this;
        }
        
        // 收口方法：构建最终对象
        public Computer build() {
            return new Computer(this);
        }
    }
    
    @Override
    public String toString() {
        return "Computer{" +
                "cpu='" + cpu + '\'' +
                ", ram=" + ram + "GB" +
                ", ssd=" + ssd + "GB" +
                ", gpu=" + (gpu != null ? gpu : "集成显卡") +
                ", 蓝牙=" + hasBluetooth +
                ", WiFi=" + hasWifi +
                '}';
    }
}
```

### 使用方式
```java
public class Main {
    public static void main(String[] args) {
        // 示例1：办公电脑（简单配置）
        Computer officePC = new Computer.Builder("i5", 8)
                .setSsd(512)
                .build();
        System.out.println(officePC);
        // 输出：Computer{cpu='i5', ram=8GB, ssd=512GB, gpu=集成显卡, 蓝牙=false, WiFi=true}
        
        // 示例2：游戏电脑（高性能配置）
        Computer gamingPC = new Computer.Builder("i9", 32)
                .setSsd(2048)                    // 2TB硬盘
                .setGpu("RTX 4090")              // 高端显卡
                .setBluetooth(true)              // 要蓝牙
                .build();
        System.out.println(gamingPC);
        // 输出：Computer{cpu='i9', ram=32GB, ssd=2048GB, gpu=RTX 4090, 蓝牙=true, WiFi=true}
        
        // 示例3：服务器（特殊配置）
        Computer server = new Computer.Builder("Xeon", 64)
                .setSsd(4000)
                .setWifi(false)                  // 服务器不需要WiFi
                .build();
        System.out.println(server);
    }
}
```

---

## Builder vs 普通写法的对比

| 维度 | 普通构造函数 | Builder模式 |
|------|-------------|-------------|
| **可读性** | ❌ 差，容易看错参数位置 | ✅ 好，方法名自解释 |
| **可选参数** | ❌ 需要传null或默认值 | ✅ 不写就行，有默认值 |
| **扩展性** | ❌ 新增参数要改所有构造函数 | ✅ 新增一个链式方法即可 |
| **安全性** | ❌ 运行时才可能发现参数错误 | ✅ 编译期就能检查必需参数 |
| **代码量** | ✅ 少 | ❌ 多（但这是值得的） |

---

## 什么时候用Builder？

### ✅ 适合用Builder
- 参数很多（≥3个）
- 大部分参数是可选的
- 参数之间有多种组合方式
- 需要保证对象创建后不可变（immutable）

### ❌ 不适合用Builder
- 参数很少（1-2个）
- 所有参数都是必需的
- 简单工具类（如 `trimToNull`）

---

## 现实中的例子

### 1. Java的StringBuilder（虽然不是严格Builder模式，但思想类似）
```java
StringBuilder sb = new StringBuilder()
    .append("Hello")
    .append(" ")
    .append("World");
String result = sb.toString();
```

### 2. Lombok的@Builder注解（自动生成）
```java
import lombok.Builder;

@Builder
public class User {
    private String name;
    private int age;
    private String email;
}

// 自动生成Builder，直接使用
User user = User.builder()
    .name("张三")
    .age(25)
    .email("zhangsan@example.com")
    .build();
```

### 3. Spring的RestTemplate配置
```java
RestTemplate restTemplate = new RestTemplateBuilder()
    .setConnectTimeout(Duration.ofSeconds(5))
    .setReadTimeout(Duration.ofSeconds(10))
    .additionalMessageConverters(new MappingJackson2HttpMessageConverter())
    .build();
```

---

## 一句话总结

**Builder模式就是把"一团糟的构造函数参数"变成"一步一步的链式配置"，让代码像自然语言一样好读，还能防止出错！**
