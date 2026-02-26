---
title: Builder（建造者模式）
date: "2026-01-29"
categories:
  - springboot
description: 一句话：把复杂对象的构建过程拆成多个步骤，Director按固定顺序指挥，ConcreteBuilder负责具体实现，创建出不同的Product。
---
# Builder（建造者模式）

**一句话**：把复杂对象的构建过程拆成多个步骤，Director按固定顺序指挥，ConcreteBuilder负责具体实现，创建出不同的Product。

---

## 四个核心角色（详细解释！）

### 1. Product（产品）
**是什么**：最终要创建的复杂对象  
**干什么**：持有所有属性，最终被返回给客户端使用  
**类比**：汉堡包本身

### 2. Builder（抽象建造者）
**是什么**：接口或抽象类  
**干什么**：定义创建Product需要的所有步骤（有什么步骤）  
**类比**：汉堡制作说明书（规定了要做面包、加肉、加酱这些步骤）

### 3. ConcreteBuilder（具体建造者）
**是什么**：Builder的具体实现类（可以有多个！）  
**干什么**：实现每个步骤的具体逻辑（怎么做）  
**类比**：
- 鸡肉汉堡制作流程（芝麻面包+炸鸡+蜂蜜芥末酱）
- 牛肉汉堡制作流程（全麦面包+牛肉饼+黑胡椒酱+芝士）

### 4. Director（指挥者）
**是什么**：指挥建造的类（可选，但常用）  
**干什么**：定义步骤的执行顺序（先做什么，后做什么）  
**类比**：店长，规定"先做面包→再放肉→最后加酱"的标准流程

---

## 实际例子：汉堡店制作系统

### 场景说明
一家汉堡店要制作两种汉堡，制作流程一样，但每个步骤的具体内容不同：
- 鸡肉汉堡（芝麻面包 + 炸鸡 + 蜂蜜芥末酱）
- 牛肉汉堡（全麦面包 + 牛肉饼 + 黑胡椒酱 + 芝士）

---

## 完整代码实现（四个角色都有！）

```java
// ============================================
// 1. Product（产品）
// 最终要创建的汉堡对象
// ============================================
public class Burger {
    private String bun;        // 面包
    private String meat;       // 肉
    private String sauce;      // 酱
    private boolean hasCheese; // 是否有芝士
    
    // 包访问权限的构造方法（只能通过Builder包内访问）
    Burger() {}
    
    // 包访问权限的setter
    void setBun(String bun) { this.bun = bun; }
    void setMeat(String meat) { this.meat = meat; }
    void setSauce(String sauce) { this.sauce = sauce; }
    void setHasCheese(boolean hasCheese) { this.hasCheese = hasCheese; }
    
    @Override
    public String toString() {
        return "Burger{" + bun + "面包 + " + meat + " + " + sauce + 
               (hasCheese ? " + 芝士" : "") + "}";
    }
}

// ============================================
// 2. Builder（抽象建造者接口）
// 定义了制作汉堡需要的所有步骤（有什么步骤）
// ============================================
public interface BurgerBuilder {
    void buildBun();      // 步骤1：准备面包
    void buildMeat();     // 步骤2：加肉
    void buildSauce();    // 步骤3：加酱
    void addCheese();     // 步骤4：加芝士（可选步骤）
    Burger getResult();   // 获取最终汉堡
}

// ============================================
// 3. ConcreteBuilder（具体建造者1：鸡肉汉堡）
// 实现Builder接口，定义鸡肉汉堡每个步骤怎么做
// ============================================
public class ChickenBurgerBuilder implements BurgerBuilder {
    private Burger burger = new Burger();  // 创建一个空汉堡
    
    @Override
    public void buildBun() {
        burger.setBun("芝麻");  // 鸡肉汉堡用芝麻面包
        System.out.println("  → 铺上芝麻面包");
    }
    
    @Override
    public void buildMeat() {
        burger.setMeat("炸鸡排"); // 鸡肉汉堡用炸鸡
        System.out.println("  → 放上炸鸡排");
    }
    
    @Override
    public void buildSauce() {
        burger.setSauce("蜂蜜芥末酱"); // 鸡肉汉堡配蜂蜜芥末
        System.out.println("  → 挤上蜂蜜芥末酱");
    }
    
    @Override
    public void addCheese() {
        burger.setHasCheese(false); // 鸡肉汉堡不加芝士
        System.out.println("  → 不加芝士");
    }
    
    @Override
    public Burger getResult() {
        return burger;  // 返回做好的鸡肉汉堡
    }
}

// ============================================
// 3. ConcreteBuilder（具体建造者2：牛肉汉堡）
// 实现Builder接口，定义牛肉汉堡每个步骤怎么做
// ============================================
public class BeefBurgerBuilder implements BurgerBuilder {
    private Burger burger = new Burger();  // 创建一个空汉堡
    
    @Override
    public void buildBun() {
        burger.setBun("全麦");  // 牛肉汉堡用全麦面包
        System.out.println("  → 铺上全麦面包");
    }
    
    @Override
    public void buildMeat() {
        burger.setMeat("煎牛肉饼"); // 牛肉汉堡用牛肉饼
        System.out.println("  → 放上煎牛肉饼");
    }
    
    @Override
    public void buildSauce() {
        burger.setSauce("黑胡椒酱"); // 牛肉汉堡配黑胡椒
        System.out.println("  → 挤上黑胡椒酱");
    }
    
    @Override
    public void addCheese() {
        burger.setHasCheese(true); // 牛肉汉堡加芝士
        System.out.println("  → 盖上一片芝士");
    }
    
    @Override
    public Burger getResult() {
        return burger;  // 返回做好的牛肉汉堡
    }
}

// ============================================
// 4. Director（指挥者/店长）
// 定义制作流程的顺序（先做什么，后做什么）
// 不 care 具体用什么材料，只 care 顺序！
// ============================================
public class BurgerDirector {
    private BurgerBuilder builder;  // 当前使用的建造者
    
    // 设置使用哪个具体建造者（鸡肉还是牛肉）
    public void setBuilder(BurgerBuilder builder) {
        this.builder = builder;
    }
    
    // 定义标准制作流程（这就是Director的核心价值！）
    public void constructBurger() {
        System.out.println("开始制作汉堡...");
        builder.buildBun();     // 第1步：面包
        builder.buildMeat();    // 第2步：肉
        builder.buildSauce();   // 第3步：酱
        builder.addCheese();    // 第4步：芝士
        System.out.println("制作完成！\n");
    }
}

// ============================================
// 客户端使用代码
// ============================================
public class Main {
    public static void main(String[] args) {
        // 创建店长（Director）
        BurgerDirector director = new BurgerDirector();
        
        // ========== 制作鸡肉汉堡 ==========
        System.out.println("=== 顾客点单：鸡肉汉堡 ===");
        ChickenBurgerBuilder chickenBuilder = new ChickenBurgerBuilder();
        director.setBuilder(chickenBuilder);    // 告诉店长：用鸡肉汉堡流程
        director.constructBurger();              // 店长按标准流程指挥制作
        Burger chickenBurger = chickenBuilder.getResult();
        System.out.println("出品：" + chickenBurger);
        
        // ========== 制作牛肉汉堡 ==========
        System.out.println("\n=== 顾客点单：牛肉汉堡 ===");
        BeefBurgerBuilder beefBuilder = new BeefBurgerBuilder();
        director.setBuilder(beefBuilder);        // 告诉店长：用牛肉汉堡流程
        director.constructBurger();              // 同样的constructBurger方法！
        Burger beefBurger = beefBuilder.getResult();
        System.out.println("出品：" + beefBurger);
    }
}

// ============================================
// 运行输出：
// === 顾客点单：鸡肉汉堡 ===
// 开始制作汉堡...
//   → 铺上芝麻面包
//   → 放上炸鸡排
//   → 挤上蜂蜜芥末酱
//   → 不加芝士
// 制作完成！
// 出品：Burger{芝麻面包 + 炸鸡排 + 蜂蜜芥末酱}
//
// === 顾客点单：牛肉汉堡 ===
// 开始制作汉堡...
//   → 铺上全麦面包
//   → 放上煎牛肉饼
//   → 挤上黑胡椒酱
//   → 盖上一片芝士
// 制作完成！
// 出品：Burger{全麦面包 + 煎牛肉饼 + 黑胡椒酱 + 芝士}
// ============================================
```

---

## 为什么要这样设计？好处在哪？

### 好处1：同样的流程，不同的产品
```java
// 都是调用 director.constructBurger()
// 但传入不同的 ConcreteBuilder，得到不同的汉堡

director.setBuilder(new ChickenBurgerBuilder());  
director.constructBurger();  // 制作鸡肉汉堡

director.setBuilder(new BeefBurgerBuilder());     
director.constructBurger();  // 制作牛肉汉堡

// 如果新增鱼肉汉堡？只需要新增 FishBurgerBuilder implements BurgerBuilder
// Director 的代码一行不用改！
```

### 好处2：流程和实现分离
- **Director**：只管"按什么顺序做"（流程控制）
- **ConcreteBuilder**：只管"具体用什么材料做"（实现细节）
- 两者解耦，互不影响！

### 好处3：隐藏复杂创建过程
```java
// 客户端代码超简单，不用知道汉堡是怎么一步步做出来的
Burger burger = builder.getResult();
// 不用关心：是先放面包还是先放肉？芝士什么时候加？
```

---

## 对比：不用Builder会怎样？（反面教材）

```java
// 不用Builder的丑陋写法：一个巨大的构造函数
public class Burger {
    public Burger(String type) {
        if (type.equals("chicken")) {
            this.bun = "芝麻";
            this.meat = "炸鸡排";
            this.sauce = "蜂蜜芥末酱";
            this.hasCheese = false;
        } else if (type.equals("beef")) {
            this.bun = "全麦";
            this.meat = "煎牛肉饼";
            this.sauce = "黑胡椒酱";
            this.hasCheese = true;
        }
        // 新增鱼肉汉堡？又要加else if！违反开闭原则！
        // 而且所有逻辑都耦合在一个类里，难以维护！
    }
}
```

---

## 一句话总结

**Builder模式 = Director定流程 + ConcreteBuilder做实现 = 同样的步骤，做出不同的产品！**

| 角色 | 代码中对应 | 作用 |
|------|-----------|------|
| **Product** | `Burger` 类 | 最终产品 |
| **Builder** | `BurgerBuilder` 接口 | 定义步骤 |
| **ConcreteBuilder** | `ChickenBurgerBuilder` / `BeefBurgerBuilder` | 具体实现 |
| **Director** | `BurgerDirector` 类 | 指挥顺序 |

---

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → Builder → GoFDesignPatterns。
