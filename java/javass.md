---
title: JAVA基础
date: "2026-01-30"
categories:
  - java
description: "注解 @interface 接口  interface"
---
# JAVA基础

## 编译

```
java源码--javac（编译器）---.class文件  
```

## 代理

### 接口实现代理

```java
Hello target = new HelloImpl();
Hello proxy = (Hello)Proxy.newProxyInstance(
	target.getClass().getClassLoader(),	//返回target同一个类型
    target.getClass().getInterfaces(),	//提供接口  生成proxy类
    new MyInvocationHandler(target)		//提供解决办法
)
    
    

--例子：     
//定义解决办法
InvocationHandler h = new MyInvocationHandler(target);
-可以定义不同解决办法
//生成代理类
Hello proxy = (Hello) Proxy.newProxyInstance(classloader,interface, invocationHandler);
---$Proxy0
//运行 
$Proxy0.hi 转发方法
return (String) h.invoke(this /*就是 proxy*/, method, new Object[]{"Bob"});
//对于method：反射---class定义的method
Method method = Hello.class.getMethod("sayHello");
method.invoke(Object obj1,Object[] args); 


---proxy类
-缓存===为了：校验接口列表-拼接代理类字节码-生成类
WeakCache<ClassLoader, InterfaceArrayKey, Class<?>>
-生成byte[]字节码
sun.misc.ProxyGenerator.generateProxyClass(...)
-定义类
defineClass0(loader, name, bytes, ...)
// 1. 它继承了 Proxy 类（所以它自动拥有了 handler 这个成员变量）
// 2. 它实现了你的 UserService 接口（所以它有 saveUser 方法）
public final class $Proxy0 extends Proxy implements UserService {

    // 这一步是在类加载时，通过反射把 saveUser 方法对象拿出来，存成静态变量 m3
    // m3 = UserService.class.getMethod("saveUser", String.class);
    --反射 拿取 方法
    private static Method m3; 

    // 构造函数：把你传入的 handler 存给父类
    public $Proxy0(InvocationHandler h) {
        super(h);
    }

    // --- 重点来了：这就是生成的 saveUser 方法 ---
    @Override
    public void saveUser(String name) {
        try {
            // 它是怎么走的？看这行：
            // super.h 就是你传入的 LogHandler
            // 它直接调用了 handler 的 invoke 方法！
            // 把 "自己(this)", "方法对象(m3)", "参数(name)" 全传过去了
            super.h.invoke(this, m3, new Object[] { name });
            
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
    }
}

```

#### 具体实现

```java
//解决办法
class MyInvocationHandler implements InvocationHandler{
    //实现 final object target代理对象
    private final Object target;
    public MyinvocationHandler(Object target){
    	this.target = target;
    }
    
    //定义invoke 实现转发
    @Override
    public Object invoke(Object proxy,Method method,Object[] args) throws Throwable{
        System.out.println("Before call");
      	Object result = method.invoke(target,args);
        System.out.println("After method call");
        return result;
    }
}      
//代理类
public final class  $proxy0 extends Proxy implements Hello{
    
   	public InvocationHandler h;   
    public $Proxy0(InvocationHandler h) {
        super(h);
        this.h = h;
    }
    public void sayHello(){
        try{
            Method m3 = Hello.class.getMethod("sayhello")    
            h.invoke(this,m3,null);
        }catch(Throwable e){
        }
    }  
}
```

### 类代理

```java
//强制使用CGLIB代理，开启spring AOP
@EnableAspectJAutoProxy(proxyTargetClass = true)  //为 false（默认），并且目标实现了接口，则使用 JDK 动态代理（只代理接口方法）。
```

### CGLIB实现代理

```java
//通过生成目标类的子类做代理，不要求实现接口
A proxy = (A) enhancer.create();                  // 生成的是 A 的子类
System.out.println(proxy.hello("Alice"));         // 会被拦截
```





## 类

- 一般类
- 注解 @interface 
- 接口  interface 
- 枚举  

```java
//Class<?>
类、接口、枚举、注解、数组、基本类型、void
//void 只用于反射   有一个类描述符
Class<?> c = void.class;
//基本类型 有一个类描述符
Class<?> c = int.class; Integer.Type
    
```



### 多态

-  Override   覆写父类方法
-  **向上转型**   向下转型
   - **解耦 & 接口编程**（依赖倒置）
   - 隐藏实现细节，收敛能力边界
   - 统一容器/参数类型

### 协变返回

```
-覆写父类方法，返回一个更具体的子类    
```

### 匿名类   匿名内部类  

```java
//匿名类
new 父类名或接口名(构造参数) {
    // 重写父类方法 或 实现接口方法
};
Map<Person,Integer> map = new TreeMap<>(); //此处定义内部类 直接定义，然后使用对应的实例
//new 接口 (){}
new Comparator<Person>(){
    public int compare(Person p1, Person p2){
        return p1.name.compareTo(p2.name);
    }
}
```

### java接口

- 继承Object类

```java
public interface Log{
	void info(String msg); //默认实现 public abstract 方法
	void warn(String msg);
    public static final variable;
}  	
-------------------------------
-多继承其余接口
public interface Log extends extends A,B {
    ...
}
------------------------------
-默认实现-存储在接口的metaspace中
-自动生成匿名类class$1    
public interface Log{
	default void info(String msg){
        System.out.println("Default method2()");
    }
	void warn(String msg);
    public static final variable;
} 
```

#### @FunctionalInterface

- 位置：`interface` 声明处的类型注解（type annotation）
- 定义：`java.lang.FunctionalInterface` 是用于标记“函数式接口（Functional Interface）”的注解；函数式接口满足 **SAM（Single Abstract Method）** 约束：在排除 `Object` 继承方法与 `default/static` 方法后，接口中抽象方法数量为 1。
- 作用：触发编译期校验（不满足 SAM 会编译失败），用于把“该接口用于 lambda/方法引用”的意图固定到类型定义上。
- 约束：
  - 目标类型必须是 `interface`；标注在 `class/enum` 上通常会报编译错误。
  - `default` / `static` 方法不计入抽象方法数量。
  - `Object` 的方法签名（如 `toString/equals/hashCode`）不计入抽象方法数量。

```java
@FunctionalInterface
public interface IntPredicate {
    boolean test(int value); // 唯一抽象方法（SAM）
}

IntPredicate p = v -> v > 0; // lambda 以 SAM 进行适配
boolean ok = p.test(10);
```

```java
@FunctionalInterface
public interface Bad {
    void a();
    void b(); // 两个抽象方法 → 不满足 SAM（编译期报错）
}
```

关系（名词对齐）：
- 上级：Java 注解（`@interface`）/ 接口（`interface`）
- 相关：lambda 表达式、方法引用（method reference）、SAM 转换、`java.util.function.*`（标准函数式接口族）

### java注解

- 继承接口

```java
public @interface MyAnnotation{
    String value();
}
//java.lang.annotation.Annotation 继承的接口 等价于
public interface MyAnnotation extends Annotation {
    String value();
}
//语法糖+作用
1：@interface == extends java.lang.annotation.Annotation
2： 检查 @interface 的定义，并强制你遵守注解接口的特殊规则
  
    
```

- 使用注解

```java
@MyAnnotation(value ="somevalue")
public class MyClass{
    
}
//在编译时 在 MyClass.class 文件中，添加一个 RuntimeVisibleAnnotations 属性
//记录这个类上有一个 MyAnnotation 注解，它的 value 配置项的值是 "someValue"

```





存储在`.class`的attribute中   常量池会有符号引用

```
RuntimeVisibleAnnotations_attribute {
    u2 attribute_name_index;          // 属性名索引（如 "RuntimeVisibleAnnotations"）
    u4 attribute_length;              // 属性长度
    u2 num_annotations;               // 注解数量
    annotation annotations[num_annotations]; // 注解数组
   .... 注解类型
}
```

#### 定义注解

```java
public @interface Report{
	int value() default 0;
	String valueContext() default "";
}
```

#### 元注解

有一些**注解**可以修饰其他注解，这些注解就称为元注解（meta annotation）。Java标准库已经定义了一些元注解，我们只需要使用元注解，通常不需要自己去编写元注解。

##### @Target 

- 类或接口：`ElementType.TYPE`；
- 字段：`ElementType.FIELD`；
- 方法：`ElementType.METHOD`；
- 构造方法：`ElementType.CONSTRUCTOR`；
- 方法参数：`ElementType.PARAMETER`。

- 可以使用数组

  ```java
  @Target({
  	ElementType.TYPE,
  	ElementType.METHOD
  })
  ```

##### @Retention 声明周期

- 仅编译期：`RetentionPolicy.SOURCE`；
- 仅class文件：`RetentionPolicy.CLASS`；
- 运行期：`RetentionPolicy.RUNTIME`。

##### @Repeatable

使用`@Repeatable`这个元注解可以定义`Annotation`是否可重复。这个注解应用不是特别广泛。

##### @Inherited   

使用`@Inherited`定义子类是否可继承父类定义的`Annotation`。`@Inherited`仅针对`@Target(ElementType.TYPE)`类型的`annotation`有效，并且仅针对`class`的继承，对`interface`的继承无效：

- 对类继承
- 对接口无效

```
isAnnotationPresent()
```

#### 使用反射api来读取注解

```java
getAnnotation()

Class cls=Example.class;
Report report =cls.getAnnotation(Report.class);


```

获取所有注解

```java
Annotation[] anns =example1.class.getAnnotations();

//类的所有注解

Method exampe1_method=example1.class.getMethod("example1_methods",Class<?>[]{});
Annotation[][] annsP =example1_method.getParameterAnnotations();
//方法的所有注解


```





#### 使用

在某个JavaBean中，我们可以使用该注解：

```java
public class Person {
    @Range(min=1, max=20)
    public String name;

    @Range(max=10)
    public String city;
}
```

但是，定义了注解，本身对程序逻辑没有任何影响。我们必须自己编写代码来使用注解。这里，我们编写一个`Person`实例的检查方法，它可以检查`Person`实例的`String`字段长度是否满足`@Range`的定义：

```java
void check(Person person) throws IllegalArgumentException, ReflectiveOperationException {
    // 遍历所有Field:
    for (Field field : person.getClass().getFields()) {
        // 获取Field定义的@Range:
        field.setAccessible(true); // 允许访问protected字段
        Range range = field.getAnnotation(Range.class);
        // 如果@Range存在:
        if (range != null) {
            // 获取Field的值:
            Object value = field.get(person);
            // 如果值是String:
            if (value instanceof String s) {
                // 判断值是否满足@Range的min/max:
                if (s.length() < range.min() || s.length() > range.max()) {
                    throw new IllegalArgumentException("Invalid field: " + field.getName());
                }
            }
        }
    }
}
```

这样一来，我们通过`@Range`注解，配合`check()`方法，就可以完成`Person`实例的检查。注意检查逻辑完全是我们自己编写的，JVM不会自动给注解添加任何额外的逻辑。









## 方法引用  + 匿名

- 对应一个轻量对象，实现了目标式函数接口，同等于对应方法

```java
ClassName::staticMethod  //引用静态方法
-----------------------------------------------------------------------------------------    
object::instanceMethod //实例方法
------------------------------------------------------------------------------------------
ClassName::instanceMethod //实例方法
//example
Function<String, Integer> f = String::length;     //接收一个String实例   f.apply("hello"); 
--------------------------------------------------------------------------------------------    
ClassName::new
Supplier<string> s1 = String::new
```

### lambda 函数式接口

- Lambda 表达式本质上就是一个匿名的、轻量级的“接口实现对象”
- 函数式接口约束 对象行为准则

```java
//用函数直接实现接口
@FunctionalInterface
public interface Comparator<T>{
	int compare(T o1 ,T o2);
}
//等价于   comp是一个轻量级类
Comparator<PCB> comp = (a, b) -> Integer.compare(a.getBurstTime(), b.getBurstTime());
Comparator<PCB> comp = new Comparator<PCB>() {
    @Override
    public int compare(PCB a, PCB b) {
        return Integer.compare(a.getBurstTime(), b.getBurstTime());
    }
};
```

## 基本类型

```
-byte
-short 2字节
-int   4字节    Integer 包装类
-long 8字节
-float 4字节
-double 8字节   Double 包装类
-char 2字节
-boolean 1bit  (JVM优化)
```

## 引用类型

```java
//class
//interface 
//内建[] array
int[] arr = new int[5];	
//enum  
enum Color {RED,GREEN,BLUE}
Color c = Color.RED;
//Annotation
@Override
public void run(){}
List  
-(使用equal()进行比较)
-contains  //boolean
-indexOf  //返回索引 
Map
-
```



## Stream

```java
//字节流
-OutputStream   输
//  输出到 类内 （外2类内---以便输出）
--ByteArrayOutputStream -//实现 OutputStream 接口，作为“字节接收器    //byte[] buf 存储 
//toByteArray()
//write 

    
-InputStream     
//输入到 程序内部的变量 （类内2外）
    
-------------------------
Writer  Reader    
  
```

### 序列化

```java
// 序列化：对象 → 字节流
// 反序列化：字节流 → 对象
public class Result implements Serializable{
}
//outputstream.writeObject
os.writeObject(result);
//inputstream.readObject()
ois.readObject();
//serializeToFile deserializeFromFile
serializeToFile(user, "user.dat");
User restoredUser = deserializeFromFile("user.dat");
//或者中间桥接 
```



## 数组

```java
List<E> sss = ArrayList<>();
sss.add  remove 
sss.get //获取指定索引元素   set(index,E e)
sss.size
```

## 字典

### HashMap

```java
Map<String,Integer> map = new HashMap<>();

map.put("apple",10);
map.get(Object key);

map.remove(key);  //删除键值对
map.containsKey(key)	

map.values();		//获取all values
map.keySet();		//获取all key
map.entrySet();		//获取all  <>


Map<String, Integer> map = new HashMap<>(10000); 
```

- 对于自己定义的key中的类 

```java
public class Person{
	String firstName;
	String lastName;
	int age;
	
	@Override
	int hashCode(){
		int h =0;
		h = 31*h + firstName.hashCode();
		h = 31 * h + lastName.hashCode();
		h = 31 * h + age;
        return h;
	}


}
//覆写 equal方法
@Override
public boolean equal(Object o){
    ...
}
//覆写  hashcode
//or
    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, age);
    }


```

- `equals()`用到的用于比较的每一个字段，都必须在`hashCode()`中用于计算；`equals()`中没有使用到的字段，绝不可放在`hashCode()`中计算。

#### 原理

```java
int index = key.hashCode() & 0xf;
|会自动扩容
int index = key.hashCode() & 0x1f;
...
```

### EnumMap

```
map.put(DayOfWeek.MONDAY,"星期一");
```

### TreeMap

```
-Comparable 接口
Map<Person,Integer> map = new TreeMap<>(new Comparator<Person>() {
            public int compare(Person p1, Person p2) {
                return p1.name.compareTo(p2.name);
            }
});
-new一个Comparator<Person>()

```



## 集合

### HashSet

```
-add
-contains
-remove
-size
```

### TreeSet

```
-实现SortedSet接口
```

## 队列

```java
queue .add 		 //offer
queue .remove	// poll
queue .element  //取出队首元素 不删除	//peek
Queue<String> q = new PriorityQueue<>();  //优先队列

#双端队列
Deque 
-添加  尾 首 addLast addFirst /offer...
-获取  尾 首 getFirst getLast // peek ...
- removeFirst removeLast //poll...

```



## 位操作

```java
>>> 无符号右移

static final int hash(Object key){
	int h;
	return (key == null) ? 0:(h = key.hashCode())^(h>>>16);
}
index = h & (table.length - 1);
>> 带符号右移

<< 左移运算符
    
    
```

## 包装  缓存机制

```java
private static class CharacterCache {
    private CharacterCache(){}
    static final Character cache[] = new Character[127 + 1];
    static {
        for (int i = 0; i < cache.length; i++)
            cache[i] = new Character((char)i);
    }

}
```

## 配置

```java
Properties props = new Properties();
//接收流式
props.load(new java.io.FileInputStream(f));
String filepath = props.getProperty("last_open_file");
```



## 迭代器

```java
for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
     String s = it.next();
     System.out.println(s);
}//搜索下一个

```

## Collections工具类

```
-sort shuffle
-变为不可变list 
List<T> unmodifiableList(List<? extends T> list)
-变为线程安全list
List<T> synchronizedList(List<T> list)
```

## Stream

- stream框架

```
-转换为一个惰性计算的流水线-


```











## 总结

### 数

```java
-注意对包装内容的使用
//应该使用long
Long sum = 0L;
for(long i = 0;i<= Integer.MAX_VALUE;i++)

-浮点数运算丢失精度    
BigDecimal包装
BigDecimal a = new BigDecimal("1.0");
BigDecimal b = new BigDecimal("1.00");
System.out.println(0 == x.compareTo(y));

BigInteger ...
```

### 变量

```java
//类变量 
-存储在方法区中 -可以在运行时赋值
//静态变量 final修饰为常量
-共享一份静态变量
//局部变量 
--------------------------------
//常量 
//char常量 -2字节
//string常量 
```

### 访问关系

```java
//静态方法 
-访问静态成员


//重载
-方法名必须相同
-遇到变长参数匹配的时候
-会优先匹配定长参数函数
    
    
//重写 
- 方法名 参数列表相同 
-子类返回类型 必须更小或者相等
-异常小于父类  访问修饰符 大于等于父类
-父类修饰符 private final static 子类不能重写
class Parent {
    final void show() {
        System.out.println("Parent show");
    }
}

class Child extends Parent {
    // ❌ 编译错误：无法重写 final 方法
    // void show() {  
    //     System.out.println("Child show");
    // }
}   
```

### 特征

```bash
#封装
对象的状态信息（也就是属性）隐藏在对象内部，不允许外部对象直接访问对象的内部信息，但可以提供操作
#继承
子类拥有父类对象所有的属性和方法，私有也是拥有，只是不能用
子类可以拥有自己属性和方法，即子类可以对父类进行扩展。 -扩展
可以override 重写
```





## 反射

```java
public final class Class {
    private Class() {}
}
```

```java
Class cls = String.class;
```

```java
String s = "Hello";
Class cls = s.getClass();
```

```java
Class cls = Class.forName("java.lang.String");
```
### 动态加载

- 选择性加载各种类

```java
// Commons Logging优先使用Log4j:
LogFactory factory = null;
if (isClassPresent("org.apache.logging.log4j.Logger")) {
    factory = createLog4j();
} else {
    factory = createJdkLog();
}

boolean isClassPresent(String name) {
    try {
        Class.forName(name);
        return true;
    } catch (Exception e) {
        return false;
    }
}
```







### Field

```java
Field getField(name)：根据字段名获取某个public的field（包括父类）
Field getDeclaredField(name)：根据字段名获取当前类的某个field（不包括父类）
Field[] getFields()：获取所有public的field（包括父类）
Field[] getDeclaredFields()：获取当前类的所有field（不包括父类）
    
    
    
    
class Person{
    public String name ="s";
}    
public class re{
    public static void main(String[] args){
        try{
            Person p=new Person();
            Class<?> pC=person.getClass();
            Filed nameField =pC.getField("name");
        }
    }
}
    
```



#### Field类

```java
一个Field对象包含了一个字段的所有信息：

    
    
    
    
field.get(Object obj) 	//可以获取obj这个实例的field所有内容
//通过反射获取field
		|
    	|
// 通过field.get(obj)   获取obj-Field 的值
    
    
getName()：返回字段名称，例如，"name"；
getType()：返回字段类型，也是一个Class实例，例如，String.class；
getModifiers()：返回字段的修饰符，它是一个int，不同的bit表示不同的含义。
    
    
set()
```



```java
f.setAccessible(true);
```

调用`Field.setAccessible(true)`的意思是，别管这个字段是不是`public`，一律允许访问。

设置字段--创建

```java
Class c =String.class;
Field f=getDeclaredField("size");
f.setAccessible(true);
f.set(p, "Xiao Hong"); 
```

- 可以直接修改内容

### method

```java
Method getMethod(name, Class...)：获取某个public的Method（包括父类）
Method getDeclaredMethod(name, Class...)：获取当前类的某个Method（不包括父类）
Method[] getMethods()：获取所有public的Method（包括父类）
Method[] getDeclaredMethods()：获取当前类的所有Method（不包括父类）
```



- class表示name方法的参数

```java
String S="shshs";
Method m =String.class.getMethod("substring",int.class);
String r =(String)m.invoke(s,6);

```

#### invoke

```java
public Object invoke(Object proxy,Method,Objecy[] args) throws IllegalAccessException,IllegalArgumentException,InvocationTargetException  {
    
    MethodInvocation invocation = new MethodInvocation(method,args,target);  
    //生成 ProceedingJoinPoint ... 
    
    //开始执行拦截器链 
    return invocation.proceed();   
}
//proceed 是递归调用     
```

#### 反射调用的底层路径（`Method.invoke` → MethodAccessor → `invoke0/native` → target）

`Method.invoke(obj, args...)` 的语义是：在运行时对 `obj` 上的某个目标方法执行一次调用，并把返回值/异常回传给调用方。实现层面通常包含“Java 层参数整理 + 访问器分发 + JVM 反射入口 + 目标方法执行”的分层。

##### 1) `MethodAccessor` / `NativeMethodAccessorImpl` / `invoke0` 的分层（JDK 实现口径）

反射调用通常会经由一层“方法访问器（accessor）”对象分发。常见类名会出现在 `jdk.internal.reflect.*`（或旧版本的 `sun.reflect.*`）包下：

- `MethodAccessor`：方法访问器接口（将反射调用抽象为 `invoke(obj, args)`）。
- `DelegatingMethodAccessorImpl`：委托型访问器（内部持有一个真实访问器并转发，用于热切换实现）。
- `NativeMethodAccessorImpl`：基于 native 入口的访问器（通过 `invoke0(...)` 让 JVM 执行一次目标方法调用）。
- `GeneratedMethodAccessorN`：运行时生成的访问器（JVM/反射工厂生成字节码类，用于减少后续反射调用开销）。

概念级调用链（示意）：

```
调用方A
  -> java.lang.reflect.Method.invoke(obj, args)
       -> MethodAccessor.invoke(obj, args)
            -> DelegatingMethodAccessorImpl.invoke
                 -> NativeMethodAccessorImpl.invoke
                      -> invoke0(...)               // native 入口（JVM）
                          -> target(...)            // 真实目标方法（解释/JIT）
```

在某些 JDK/配置下，反射调用会发生“inflation（膨胀）”优化：前几次先走 native 慢路径；达到阈值后切换到 `GeneratedMethodAccessorN` 之类的字节码访问器，后续路径变为：

```
Method.invoke
  -> MethodAccessor.invoke
       -> GeneratedMethodAccessorN.invoke
            -> target(...)
```

##### 2) “执行 native 时，线程有没有栈帧？”（Java 栈 vs 本地栈）

- 从 Java 代码视角：调用 `native` 方法时，当前线程会从“执行 Java 方法”进入“执行本地实现”；在 `Throwable.getStackTrace()` 里通常会看到一帧标记为 `Native Method`（例如 `Method.invoke0(Native Method)`），但不会展开 native 内部的 C/C++ 调用栈细节。
- 从 JVM/运行时视角：native 代码主要运行在 **本地方法栈（native stack）** 上；当 JVM 通过反射入口去调用目标 Java 方法时，会为 **target 方法建立新的 Java 栈帧**（就像普通方法调用一样），执行完再逐层返回。

因此可按“分界点”记：
- 进入 `invoke0/native` 时：形成 native 执行段（Java 栈追踪上常表现为 `Native Method`）。
- 进入 `target` 方法入口时：建立/进入 target 的 Java 栈帧（解释执行或执行 JIT 编译后的机器码）。

```java
Method.setAccessible(true)
```

### Class

```
Class<?> cls = Class.forName("类名");

```







#### newInstance

```java
Class<?> clazz = Class.forName("com.example.User");
Object obj = clazz.getDeclaredConstructor().newInstance();
```















#### Constructor

```java
 Constructor cons1 = Integer.class.getConstructor(int.class...);
 Integer n1 =(Integer) cons1.newInstance(123);

        // 获取构造方法Integer(String)
        Constructor cons2 = Integer.class.getConstructor(String.class);
        Integer n2 = (Integer) cons2.newInstance("456");

```

//或者直接new





### 获取继承关系

#### getSuperclass获取父类

```java
Class i=Int.class;
Class n =i.getSuperclass();
```

#### 获取interface

```java
     Class s = Integer.class;
        Class[] is = s.getInterfaces();
```

#### 继承关系

判断继承关系

```java
Object n =Integer.valueOf(123);
boolean isDouble =n instanceof Double;
```

如果是两个`Class`实例，要判断一个向上转型是否成立，可以调用`isAssignableFrom()`：

```java
// Integer i = ?
Integer.class.isAssignableFrom(Integer.class); // true，因为Integer可以赋值给Integer
// Number n = ?
Number.class.isAssignableFrom(Integer.class); // true，因为Integer可以赋值给Number
// Object o = ?
Object.class.isAssignableFrom(Integer.class); // true，因为Integer可以赋值给Object
// Integer i = ?
Integer.class.isAssignableFrom(Number.class); // false，因为Number不能赋值给Integer
```

对于所有的Interface的变量总是通过某个实例向上转型赋值给接口类型的

```java
CharSequence ss =new StringBuilder();
```



一种方式是动态代码，我们仍然先定义了接口`Hello`，但是我们并不去编写实现类，而是直接通过JDK提供的一个`Proxy.newProxyInstance()`创建了一个`Hello`接口对象。这种没有实现类但是在运行期动态创建了一个接口对象的方式，我们称为动态代码。JDK提供的动态创建接口对象的方式，就叫动态代理。

```java
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Main {
    public static void main(String[] args) {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println(method);
                if (method.getName().equals("morning")) {
                    System.out.println("Good morning, " + args[0]);
                }
                return null;
            }
        };
      Hello ss= (Hello) Proxy.newProxyInstance(
      	Hello.class.getClassLoader,
        new Class[] {Hello.class},
         handler
      );
      ss.morning("Bob");
    }
    	
}

interface Hello {
    void morning(String name);
}
```

### ClassLoader

```java
     // 获取 Main 类的类加载器
ClassLoader classLoader = Main.class.getClassLoader();
```

```java
Class<?> loadedClass =classLoader.loadClass();
```

#### 可以使用当前线程进行类的加载

```java
ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
```

#### JVM 中的 ClassLoader 层次与加载方式（概念模型）

JVM 对“类从哪里来、由谁来定义（define）”的组织方式可以抽象为：**一组有父子关系的 ClassLoader + 双亲委派（parent delegation）**。

##### 1) 层次结构（JDK 8 vs JDK 9+）

- JDK 8 常见层次（概念口径）：
  - Bootstrap ClassLoader（启动类加载器）：加载 `rt.jar` 等核心类库；在 Java 代码里表现为 `null`（不是 `ClassLoader` 的 Java 对象）。
  - Extension ClassLoader（扩展类加载器）：加载 `$JAVA_HOME/jre/lib/ext` 等扩展目录（JDK 9+ 后不再以此模型为主）。
  - Application ClassLoader（应用类加载器）：加载应用 classpath（`-classpath/-cp`）中的类。
- JDK 9+ 常见层次（模块化后的口径）：
  - Bootstrap ClassLoader：加载 `java.base` 等引导模块。
  - Platform ClassLoader：加载平台模块（替代 JDK 8 的 “Ext” 概念位置）。
  - Application ClassLoader：加载 classpath 与应用模块等。

代码观测（打印层级）：

```java
ClassLoader app = Main.class.getClassLoader();
ClassLoader parent = app.getParent();
ClassLoader bootstrap = (parent == null) ? null : parent.getParent();
```

##### 2) 双亲委派（parent delegation）的“加载法”

当调用 `ClassLoader.loadClass(name)` 时，典型策略是：
1. 先检查该 `ClassLoader` 是否已经加载过该类（缓存/已定义类集合）。
2. 将加载请求委派给 parent（一直到 Bootstrap）。
3. parent 无法完成时，当前 `ClassLoader` 才尝试自行查找并定义该类（通常落在 `findClass`/`defineClass`）。

这一策略使得：
- 核心类（如 `java.lang.String`）倾向由上层（Bootstrap）统一定义，避免同名类被应用侧“覆盖定义”。
- 同名类是否被视为“同一个类”，取决于 **(class name, defining ClassLoader)** 这对二元组。

##### 3) 类的来源：classpath / module path / 自定义来源

- Application ClassLoader 的常见来源是 classpath：Jar/目录（`-cp`）。
- 模块化后还存在 module path（`--module-path`）与模块解析规则。
- 自定义 ClassLoader 可以把“类的字节流来源”改为：网络、数据库、加密文件、内存字节数组等；最终仍需调用 `defineClass(...)` 把字节码定义为 JVM 内的 `Class<?>`。

##### 4) ContextClassLoader（线程上下文类加载器）

`Thread.currentThread().getContextClassLoader()` 通常用于“框架代码加载应用代码”的场景：
- 调用点位于框架/容器线程中（框架类由 platform/app loader 加载），但需要加载应用侧实现类（由 app loader 或自定义 loader 定义）。
- 因此，框架在运行时可以通过线程上下文类加载器获取“应当用哪个 loader 去找实现类”的线索。

常见用法：SPI（`ServiceLoader`）、容器插件体系、应用服务器/嵌入式容器集成等。

#### 容器类加载器（Tomcat 的 WebappClassLoader / TomcatWebClassLoader）

Tomcat 这类 Servlet 容器会在同一个 JVM 进程里托管多个 Web 应用（webapp），因此会额外引入“每个 webapp 一套类空间”的类加载器体系，用于隔离不同应用的依赖与静态状态。

##### 1) 目的：每个 webapp 一个 defining ClassLoader

对容器而言，类的身份仍然是 **(class name, defining ClassLoader)**：
- webapp1 的 `com.example.A` 由 `WebappClassLoader#1` 定义
- webapp2 的 `com.example.A` 由 `WebappClassLoader#2` 定义
即使类名相同，JVM 也认为它们是不同类型，互相不能强转，也不共享静态字段。

##### 2) 典型来源：`WEB-INF/classes` 与 `WEB-INF/lib/*.jar`

Tomcat 的 webapp 类加载器（常见实现名：`WebappClassLoaderBase` / `ParallelWebappClassLoader` 等）通常以 URL 集合的方式组织来源：
- `WEB-INF/classes/`（编译输出的 `.class`）
- `WEB-INF/lib/*.jar`（应用私有依赖）

容器级/共享库（例如 Tomcat 自身的实现类、Servlet/JSP API 等）通常由更上层的 loader 提供，而不是由 webapp loader 从 `WEB-INF/lib` 定义。

##### 3) 加载顺序：委派模型 + 容器的过滤/例外规则

Tomcat 的 webapp loader 仍然会进行“委派”：
1. 已加载检查（缓存）
2. 向 parent 委派（直到 bootstrap/platform/app/容器公共 loader）
3. parent 找不到时，尝试从 webapp 自己的 URL（`WEB-INF/classes`、`WEB-INF/lib`）里查找并 `defineClass`

但容器通常还会对某些包名做过滤或例外规则，用来约束“哪些类必须来自容器侧，哪些类允许由 webapp 定义”，以避免关键 API/实现被应用覆盖定义（这是一类容器策略，不是 JVM 强制规则）。

##### 4) ContextClassLoader 在容器中的使用

Tomcat 在处理请求时会把当前线程的 ContextClassLoader 设为“当前 webapp 的 classloader”，使得框架代码（或 SPI）在运行时能从正确的 webapp 类空间加载实现类。

##### 5) 代码示例：在 Servlet 中打印类加载器

```java
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoaderDumpServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    ClassLoader thisClassLoader = this.getClass().getClassLoader();
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    ClassLoader stringLoader = String.class.getClassLoader(); // 通常为 null（bootstrap）
    resp.getWriter().println("this.getClass().getClassLoader() = " + thisClassLoader);
    resp.getWriter().println("Thread.contextClassLoader = " + contextClassLoader);
    resp.getWriter().println("String.class.getClassLoader() = " + stringLoader);
  }
}
```




## 泛型与特化

**java 中泛型标记符：**

- **E** - Element (在集合中使用，因为集合中存放的是元素)
- **T** - Type（Java 类）
- **K** - Key（键）
- **V** - Value（值）
- **N** - Number（数值类型）
- **？** - 表示不确定的 java 类型

```java
//上下界定义
public class example{
    //表示接收example2的子类  定义上界 
	public void extract(List<? extends example2> array){}   
    //表示接收example2的父类  定义下界
	public void extract(List<? super example2> array){}
}

//接口泛型 --实现泛型/具体类型
public class Box<T> implements Container<T> {
    private T item;

    @Override
    public void set(T item) {
        this.item = item;
    }

    @Override
    public T get() {
        return item;
    }
}
--泛型接口的具体使用 --钻石操作符
 //子类继承父类的 类型
Container<String> example = Box<>(); 

--特化
public class StringContainer implements Container<String> {
    private String value;
    @Override
    public void set(String item) {
        this.value = item;
    }
    @Override
    public String get() {
        return value;
    }
}

```

## 泛型擦除（Type Erasure）

**一句话**：Java 泛型在**编译后**会被"擦掉"，变成原始类型，JVM 运行时**看不到**泛型类型参数。

### 为什么需要擦除？

1. **兼容性**：Java 5 引入泛型，要兼容 Java 5 之前的 JVM（不支持泛型）
2. **字节码统一**：JVM 规范不感知泛型，只认识原始类型

### 擦除规则

| 泛型类型 | 编译后擦除为 |
|---------|-------------|
| `List<T>` | `List`（原始类型） |
| `List<String>` | `List` |
| `T`（无边界） | `Object` |
| `<T extends Number>` | `Number`（上界） |
| `<T extends Comparable<T>>` | `Comparable`（第一个上界） |

### 擦除示例

```java
// 源代码（你写的）
public class GenericExample {
    public void test() {
        List<String> strList = new ArrayList<>();
        strList.add("hello");
        String s = strList.get(0);
        
        Map<String, Integer> map = new HashMap<>();
        map.put("age", 25);
    }
}

// 编译后的字节码（实际运行的）- 通过 javap -c 查看
public class GenericExample {
    public void test() {
        List strList = new ArrayList();  // <String> 被擦除！
        strList.add("hello");
        String s = (String) strList.get(0);  // 编译器自动插入强制类型转换
        
        Map map = new HashMap();  // <String, Integer> 被擦除
        map.put("age", 25);  // 编译器检查类型，但字节码没有泛型信息
    }
}
```

### 擦除带来的影响

#### 1. 运行时无法获取泛型类型

```java
List<String> strList = new ArrayList<>();
List<Integer> intList = new ArrayList<>();

System.out.println(strList.getClass());  // class java.util.ArrayList
System.out.println(intList.getClass());  // class java.util.ArrayList
System.out.println(strList.getClass() == intList.getClass());  // true！

// 无法区分 List<String> 和 List<Integer>
// 因为擦除后都是 List
```

#### 2. 泛型数组不能创建

```java
// 错误！无法创建泛型数组
List<String>[] array = new List<String>[10];  // 编译错误

// 原因：擦除后变成 List[]，如果允许创建，可以放入 List<Integer>
// 破坏类型安全
List<String>[] array = (List<String>[]) new List[10];  // 只能这样，但有警告
```

#### 3. 基本类型不能直接作为泛型参数

```java
// 错误！
List<int> intList;  // 编译错误

// 正确做法：使用包装类
List<Integer> intList;  // 擦除后是 List（存 Integer 对象）
```

### 桥接方法（Bridge Method）

擦除可能导致方法签名冲突，编译器会生成桥接方法：

```java
// 父类 - 擦除后是原始类型
class Node<T> {
    public void setData(T data) {
        // ...
    }
}

// 子类 - 指定了具体类型
class MyNode extends Node<Integer> {
    @Override
    public void setData(Integer data) {  // 参数类型是 Integer
        // ...
    }
}

// 编译器生成的桥接方法（在字节码中可见）
class MyNode extends Node {
    // 桥接方法 - 保持多态性
    public void setData(Object data) {  // 擦除后的签名
        setData((Integer) data);  // 调用实际方法并强制转换
    }
    
    // 实际方法
    public void setData(Integer data) {
        // ...
    }
}
```

### 获取泛型信息的方法

虽然运行时擦除了，但编译器在字节码中保留了**签名信息**（Signature attribute），可以通过反射获取：

```java
// 1. 获取方法返回值的泛型类型
Method method = MyClass.class.getMethod("getList");
Type returnType = method.getGenericReturnType();
// 可能得到：java.util.List<java.lang.String>

// 2. 获取字段的泛型类型
Field field = MyClass.class.getDeclaredField("list");
Type fieldType = field.getGenericType();
// 可能得到：java.util.List<java.lang.String>

// 3. 获取父类的泛型参数
Type genericSuperclass = MyClass.class.getGenericSuperclass();
if (genericSuperclass instanceof ParameterizedType) {
    ParameterizedType pt = (ParameterizedType) genericSuperclass;
    Type[] actualTypeArgs = pt.getActualTypeArguments();
    // 可能得到：[class java.lang.String]
}
```

### 总结

```
源代码泛型
    ↓ javac 编译
类型检查（编译期） + 插入强制类型转换 + 生成桥接方法
    ↓ 擦除
字节码（无泛型）
    ↓ JVM 运行
原始类型 + 自动类型转换
```

**关键点**：泛型是**编译期**的类型安全检查机制，运行时不存在，通过**擦除**保持与旧版本 JVM 的兼容性。

# Java[类加载](https://so.csdn.net/so/search?q=类加载&spm=1001.2101.3001.7020)器

- 加载、验证、准备、解析和初始化

```java
//加载
ClassLoader::load_class
SystemDictionary::resolve_or_fail (C++)
ClassFileParser::parse_stream //读取字节码转换为InstanceKlass
 
```

1、**启动类**加载器 （Bootstrap Class Loader）

2、**扩展类**加载器（Extension Class Loader）

3、**应用程序类**加载器（Application Class Loader）

（1）检查 new 指令的参数能否在**常量池**中定位到一个**类的符号引用**；

（2）如果没有，则**加载、验证、准备、解析和初始化**

（3）如果有，JVM 将在**堆**中为新生对象分配内存。分配内存方式有：**指针碰撞**和**空闲列表**，具体选择哪种分配方式取决于堆是否规整，而堆是否规整又取决于**垃圾收集器**是否带有**压缩整理**功能。

- 指针碰撞：如果Java堆是绝对规整的，所有用过的内存都放在一边，所有没用过的内存存放在另一边，中间存放一个指针作为分界点指示器。分配内存时，将指针从用过的内存区域向空闲内存区域移动等距离区域。
- 空闲列表：如果Java不是规整的，这时，JVM就必须维护一张列表记录可用的内存块，在分配内存时，从列表上找到一个足够大的连续内存块分配给对象，并更新列表上的记录。

在分配对象内存空间的过程中，需要考虑线程安全的问题。因为在虚拟机中创建对象是非常频繁的行为，可能出现正在给对象A分配内存，指针还没来得及修改，对象B又同时使用了原来的指针来分配内存的情况。解决这个问题有两种方案：CAS 和 TLAB：

- **CAS**以及失败重试（比较和交换机制）：对分配内存空间的操作进行同步处理，实际上虚拟机采用CAS配上失败重试的方式保证更新操作的原子性。CAS操作需要输入两个数值，一个**旧值（操作前期望的值）**和一个新值，在操作期间先比较旧值有没有发送变化，如果**没有变化**，才交换成新值，否则不进行交换。
- **TLAB**（**Thread Local Allocation Buffer**，本地线程分配缓存区）：在堆中为每个线程预先分配一块私有内存，从而把内存分配的动作划分在不同的空间进行，保证线程安全，并减少同步开销。

（4）内存分配完成后，虚拟机需要将分配到的内存空间都初始化为零值，保证了对象实例的字段在 Java 代码中可以不赋初始值就可以直接使用；

（5）对对象进行必要的设置，例如是哪个对象类型的实例、如何才能找到类元信息、对象的哈希码、GC 分代年龄等信息，这些信息存放在对象头中。

（6）执行 init 方法，把对象按照程序员意愿进行初始化。

## JVM运行

- 运行保证程序运行的**基础类**

## 双亲委派模型

1、当需要加载类时，会优先委派当前所在的类的加载器的父加载器去加载这个类。

2、如果父加载器无法加载到这个类时，再尝试在当前所在的类的加载器中加载这个类。

- 类是动态加载 

- 会网上委派，然后一直到appLoader，如果都没有，则重写findClass进行类加载









### 类加载（Class Loading）

**自定义**

```java
@Override
protected Class<?> findClass(String name){
    	String fileName = classPath + name.replace('.', '/') + ".class";		//识别一个.class的路径，并从这个路径开始读取class里面的具体内容
     	
    FileInputStream fis = new FileInputStream(fileName);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b;
		while ((b = fis.read()) != -1) {
   		 	baos.write(b);
		}
		fis.close();
    	byte[] classData = baos.toByteArray();
    	return defineClass(name, classData, 0, classData.length);
} 

```







Java 的运行环境通过**类加载器（ClassLoader）**将 `.class` 文件加载到 JVM 中：

1. **类加载器**：Java 有不同的类加载器（引导类加载器、扩展类加载器、应用程序类加载器等），它们负责将编译后的 `.class` 文件加载到 JVM 中，并通过**双亲委派模型**来避免类的重复加载和确保核心类的安全性。
2. **字节码验证**：类加载过程中，字节码会被验证，确保其符合 JVM 规范，防止恶意代码运行。
3. **解析和准备**：类加载后，JVM 会解析符号引用，将其替换为直接引用，并为类的静态变量分配内存。

### 4. 类的链接与初始化

- 链接
  - **验证**：验证字节码的正确性。
  - **准备**：为类的静态字段分配内存并设置默认初始值。
  - **解析**：将符号引用解析为直接引用。

- **初始化**：执行类的静态初始化代码（静态块），为静态变量赋初始值。

### 5. 实例化对象

- 对象创建
  - 通过 `new` 关键字或其他方式（如反射），创建类的实例。JVM 会为对象在堆内存中分配空间，并调用构造函数对对象进行初始化。

- 内存布局
  - JVM 在堆中分配内存用于存储对象的实例数据，如成员变量。
  - 在对象实例中，包含一个指针用于指向方法区中的类元数据，以便于了解类的结构和方法。

### 6. 方法调用

- 方法调用
  - 方法调用分为静态绑定（编译期决定）和动态绑定（运行时决定）。Java 支持多态性，对于实例方法调用，JVM 会在运行时根据对象的实际类型来确定调用哪个方法。
  - **调用栈**：每当调用一个方法时，JVM 会在**栈内存**中为该方法分配一个栈帧，栈帧中存储方法的局部变量表、操作数栈、返回地址等。
  - 局部变量表中可以存储基本类型和对象引用（reference），这些引用指向堆中的对象实例。





### 7. 内存管理

- 堆内存和栈内存

  - **堆**：存储对象实例和数组，由垃圾回收器管理。
  - **栈**：存储方法的栈帧，包括局部变量和方法调用状态。

- 垃圾回收

  - Java 使用**垃圾回收器（Garbage Collector, GC）**来管理堆内存。垃圾回收器自动查找和销毁不再被引用的对象，释放内存空间。
  - 当对象没有任何引用指向它时，就会被标记为可回收对象，垃圾回收器会在合适的时候回收它们。





# JAVAbean

**JavaBean** 是一种特殊的类，由Spring框架管理创建配置

### JavaBean 的特征

- 无参构造函数

- getter setter修改私有属性

- 可序列化 Serializerable

# JAVA垃圾回收







# Lombok

```java
- @Data - 自动生成所有字段的getter/setter、equals()、hashCode()和toString()方法
- @AllArgsConstructor - 自动生成包含所有字段的构造函数
- @NoArgsConstructor= - 自动生成无参数构造函数
- @Getter - 为类的所有字段自动生成getter方法
- @Setter - 为类的所有字段自动生成setter方法
- @ToString - 自动生成toString()方法
- @EqualsAndHashCode - 自动生成equals()和hashCode()方法
- @Builder - 实现建造者模式，提供流式API创建对象
- @RequiredArgsConstructor - 为所有必需的字段生成构造函数(final字段或被@NonNull标注的字段)   
- 日志功能 @Slf4j - 自动生成该类的Logger静态字段
```





# JAVASPRING

- 把对象创建、依赖装配、横切能力（事务/日志）和 Web 请求处理

```java
//主要目标是减少 开发，单元测试和 集成测试时间
提供有目的的开发方法
避免定义更多的注释配置
避免编写大量导入语句
避免XML配置。
-----------------------------
提供了production-ready功能，例如metrics, health checks和externalized configuration    
-spring-boot-starter-actuator
///actuator/metrics 等数据
-Health Checks 
//在微服务架构和容器化（如K8s）部署中至关重要
-Externalized Configuration    
//将应用的所有配置（数据库URL、密码、服务器端口、功能开关等）从打包好的代码中剥离出来，放到代码外部进行管理。 
    
提供了web内嵌，starterPOM    

    
无需定义大量XML数据内容（包含Bean，AOP，事务配置）   
-注解启动  //动态代理+静态代理
-配置类
-属性文件
    
    
```





## Tomcat

```
TCP流--->Tomcat NIO 接收线程（Acceptor读取字节流）--->
Tomcat Coyote 连接器(解析 HTTP 协议，生成)--->
解析为HttpServletRequest / HttpServletResponse 对象
DispatcherServlet--->Tomcat回调Servlet的service方法 ---
```

## Spring

```
拦截器---InterceptorRegistry registry
//拦截器注册表  --InterceptorRegistry
//HTTP请求 → DispatcherServlet → 查询InterceptorRegistry → 匹配拦截器 → 执行拦截器链 → Controller 
addPathPatterns
exeludePathPatterns()    
addInterceptor().exeludePathPatterns().order(1); //执行顺序
```





## 数据映射标准 & 数据类分类定义

```py
JPA（Java Persistence API） 
Hibernate  #ORM框架
-实体类（Entity） 定义数据库表
-注解驱动 @Entity @Id  @Column
-自动生成查询sql
```



```
DAO
```

## Mybatis

```java
//编写mapper层  
@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE user_id = #{id}")
    @Results({
        @Result(property = "id", column = "user_id"),
        @Result(property = "name", column = "user_name"),
        @Result(property = "email", column = "email_address")
    })
    User selectById(Long id);
}
//启动后
@MapperScan  为每个接口生成一个 动态代理类
```





## java扫描过程

- 创建Spring容器

- 扫描和注册Bean 

  ```
  @SpringBootApplication
  |
  |--执行 @SpringBootCongiguration
         @EnableAutoConfiguration
         @ComponentScan
  @Constructor
  @NoArgsConstructor  //无参构造 -提供
  @AllArgsConstructor	//全参数构造 -提供
  ```

## Bean

- 对于bean注册，天然单例

```
-原型作用域
@Scope("prototype")
public class ...
TestService service1 = context.getBean(TestService.class);

-单例作用域
-Singleton 
@Scope("singleton")
    
```

- 类

```java
------------------------------------------
@ComponentScan   //扫描@Component
    
@Component   -识别为Spring Bean

@Service   -服务层   业务逻辑

@Repository    -数据访问层

@Controller     -web控制器 

@RestController   -restful web控制器
------------------------自动扫描
-@Resource	    
/*
*class注释  
*默认按名字注入 
*/    
@Component
public class MyService {
    @Resource(name = "dataSourceA") // 指定注入名为 "dataSourceA" 的 Bean
    private DataSource dataSource;
}
    
@Resource(lookup = "java:comp/DefaultDataSource")
private DataSource dataSource;

    
    
-@Primary  //优先注册bean
-----------------------------
-@Repository    
    
    
-----------------------------
-@Autowired
/*
*类变量层级注释
*/    
    
 //单注入 ---Primary
@Autowired(required = false)  //没找到就忽略
@Qualifier("z")  //根据bean名字
    
    
 //多注入 ---注入List<?>   
 @Autowired   
 List<Validator> validators; 



-----------------------------------------
 //工厂模式  
 @Component
 public class ZoneIdFactoryBean implements FactoryBean<ZoneId> {
     
 	@Override
    public ZoneId getObject() throws Exception {
        return ZoneId.of(zone);
    }
 }
```

- 方法

```java
-@Configuration  
-@bean(name = "")  
/* 方法层面注释
*	定义bean的标识
*/    
@Bean("z")		//命名
ZoneId createZoneOfZ() {
    return ZoneId.of("Z");
}    
// or  Bean Qualifier
@Bean
@Qualifier("utc8")
ZoneId createZoneOfUTC8() {
    return ZoneId.of("UTC+08:00");
}
    
//bean层面
------------------------------流程
//   创建   -   使用  - 销毁
    
-@PostConstruct
-@PreDestory
@Component
public class MailService {
    @Autowired(required = false)
    ZoneId zoneId = ZoneId.systemDefault();

    @PostConstruct
    public void init() {
        System.out.println("Init mail service with zoneId = " + this.zoneId);
    }
    @PreDestroy
    public void shutdown() {
        System.out.println("Shutdown mail service");
    }
}  
------------------------------------------------------------
    
   
```

- 资源注入

```java
-@Value("classpath:/logo.txt")
    
@Component
public class AppService{
    @Value("classpath:/logo.txt")		//注入资源
    private Resource resource;

    private String logo;

    @PostConstruct
    public void init() throws IOException {
        try (var reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            this.logo = reader.lines().collect(Collectors.joining("\n"));
        }
    }
    
}

```

### 条件装配

```java
native
test
production
    
-@Profile    
@Profile("test")
@Profile({"test","master"})
    
    
@Configuration
@ComponentScan
public class AppConfig {
    @Bean
    @Profile("!test")
    ZoneId createZoneId() {
        return ZoneId.systemDefault();
    }

    @Bean
    @Profile("test")
    ZoneId createZoneIdForTest() {
        return ZoneId.of("America/New_York");
    }
}

-@Conditional
//根据条件情况来建立bean    
    
@Component
@Conditional(OnSmtpEnvCondition.class)
public class SmtpMailService implements MailService {
    ...
}    
-@ConditionalOnProperty(name="app.smtp", havingValue="true")
-@ConditionalOnClass(name = "javax.mail.Transport")//当前classpath
-

```

### 配置

```java
@PropertySource("app.properties")
{
    app.name=MyApplication
    @Autowired
    private Environment env;  	//注入app.properties
    String appName = env.getProperty("app.name")
    ---------------------------------------
    @Value("${app.name}")    
    private String appName;
}



//Spring 配置类 ，Spring 容器会处理它，并根据其中的 @Bean 方法创建和管理 Bean
@Configuration
public class AppConfig {
    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }		//注册MyService bean 创建一个service bean
}
```





## IoC

- Inversion of Control  控制反转   IoC容器

- 注入机制 DI





## AOP

```java
//joinpoint  连接点
---------------    
@Before("execution(* com.example.service.*.*(..))")  //方法
@After
@AfterReturning  
@AfterThrowing(pointcut = "execution(* com.example.service.*.*(..))", throwing= "ex")
@Around//ProceedingJoinPoint 动态代理+静态代理
Before → @Around → 目标方法 → @After → @AfterReturning / @AfterThrowing    
---------------      
@Pointcut("execution(* com.xxx.service..*(..))") 	//直接定义pointcut
--对被拦截的方法定义别名--直接替换@Around(realMethod(){})
	

    
 pjp.proceed()     
 pjp.getArgs()
 pjp.getSignature()    //方法名，参数
 pjp.getTarget()       //方法所属类
 pjp.getThis()		  //AOP对象
	     
---------------------------------------------------    
//@args     匹配方法参数中有使用了 @Valid 注解
@Around("@args(javax.validation.Valid)")//匹配

    
//execution(...)  匹配某类方法
//@annotation @within（...） 匹配注解
//set() 匹配字段
@Before("set(* com.example.model.*.*)")    
//bean(...)
//引用切点方法  声明PointCut
    
//target(...)  目标类
//this(...)   接口 
    
-------------------------------------------
 List<MethodInterceptor>：拦截链
 MethodInterceptor: 反射最终调用定义的切面方法
 //用methodinvocation构造一个 ProceedingJoinPoint pjp
 methodinvocation：记录器，在拦截链传递
 -------------------------------------------
 methodinvocation.proceed()----methodinterceptor.invoke----around.invoke()
    
```





```java
//aspect 切面类  定义增强
@Aspect
@Component

public class ValidateAspect{   
	@Around("@annotation(arange)")
    public Object validateParameters(ProceedingJoinPoint pjp,arange arange){
        Object[] args = pjp.getArgs();
        for(Object arg: args){
            if(arg instanceof Integer){
                int value = (int ) arg;
                if(value<arange.min()||value >arange.max()){
                    throw new IllegalArgumentException("参数不在指定范围内")
                }
            }
        }
        return pjp.proceed();
    }
    @PointCut("execution(public * com.bupt.....*(..))")
    //.*(..) 任意方法 任意参数
    //..子包
 	//*匹配任意内容
    public void controllerPointcut(){}
}
-----------------------------------------------------------------------
```

### spring配置aop

```java
@EnableAspectJAutoProxy(exposeProxy = true) //配置spring aop
//扫描 @Aspect  并基于代理来进行AOP增强
AutoProxyCreator（本质是 BeanPostProcessor） 常见是 AnnotationAwareAspectJAutoProxyCreator 

```

## spring流程

```java
//建立生命周期
1：阶段发通知，不同模块在时机工作
2：三方库直接插入启动流程
	-SPI插件机制
    
    

-----------------------
new SpringApplication(primarySources)
-return Context    
ConfigurableApplicationContext //默认是AnnotationConfigServletWebServerApplicationContext
确定web类型建立 applicationcontext        
建立监听器-初始化器
 -springboot自带（spring.factories）
 -starter自带
 -SpringApplication.addInitializers/addListeners 手动加
//ApplicationContextInitializer 初始化springbean的一些配置
//ApplicationListener 监听 Spring 事件
//SpringApplicationRunListener     （Boot 启动阶段监听器）
-不是监听 Spring 的事件接口
-创建EventPublishingRunListener     
-把 Boot 启动阶段回调转成 Event，再广播给ApplicationListener
    
    
容器层 
ApplicationContext.refresh    
    
  
```

### spring消息通知

```java


ApplicationContext.publishEvent(event)//发布事件
//event --包装对象为payload
PayloadApplicationEvent 
//各种event    
//Spring Boot 启动生命周期事件（按触发顺序）
ApplicationStartingEvent              // 应用开始启动，Environment/Context 还未创建
ApplicationEnvironmentPreparedEvent   // Environment 已准备好，但 Context 未创建
ApplicationContextInitializedEvent    // Context 已创建且已初始化，尚未加载 Bean 定义
ApplicationPreparedEvent              // Context 已准备好，Bean 定义已加载但未 refresh
ApplicationStartedEvent               // Context 已 refresh，Runner 还未执行
ApplicationReadyEvent                 // 应用已就绪，Runner 执行完毕，可对外提供服务
ApplicationFailedEvent                // 启动失败（发生异常）时触发
```





## SpringTest

### Junit  测试

```java
@Test
@BeforeEach		//在test前都会执行一段
@AfterEach

@BeforeAll   //只执行一次
@AfterAll

@ExtensionWith(MockitoExtension.class)  
class ServiaceTest{
        @Test(可以执行)
}    
```



### Mockito 伪造对象

```java
//让 JUnit5 支持 Mockito 功能
@ExtensionWith(MockitoExtension.class)  
@Mock

--------------------------------
when(repository.findNameById(1l)).thenReturn("");	//指定mock内容


assertEquals("",);	//结果
verify(repository).findNameById();  //方法调用
```



### SpringTest  提供容器支持

```java
MockMvc 模拟http
@WebMvcTest(UserController.class)  // 只加载 Controller 和相关 Bean
@Autowired
private MockMvc mockMvc;
@MockBean
private UserService userService;  // 用 @MockBean 注入到容器


---------------------------------------
//发起请求  
mockMvc.perform()
```



### 单元测试

```
-加载类方法 不依赖容器
```





### 集成测试









## Lombok

```
@Data 
-Getter 
-Setter
-toString
-equals/hashCode


```



## 拦截器

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()) // 注册拦截器
                .addPathPatterns("/**")  // 需要拦截的路径（/** 表示拦截所有请求）
                .excludePathPatterns("/login", "/register"); // 不拦截的路径
    }
}
```



## JAVAMapper

```java
@Mapper
public interface UserMapper extends BaseMapper<User>{
	@Select("SELECT * FROM user WHERE id = #{id}")
}


```

```
Spring 启动时，扫描 @Mapper，
-用 MapperFactoryBean 创建代理对象
-当 userMapper.findUserById(1L) 被调用时，代理对象拦截调用
-MyBatis 解析 @Select 里的 SQL，生成 PreparedStatement
-MyBatis 通过 JDBC 执行 SQL
-查询结果封装成 User 实体对象
-返回 User
```

### DAO  

- data access object

```java
public class UserDao{
	@Autowired
    JdbcTemplate jdbcTemplate;	//注入jdbc
    User getById(Long id){
        String sql = ""
        return jdbcTemplate.querryForObject(sql,
           new Object[]{id}, 
           new BeanPropertyRowMapper<>(User.class))
    }	//由mapper的@注释做
    List<User> getUsers(int page){}

}
```





# JAVA异常

- 基类是 Throwable 
  - error
  - exception  
    - 一般性异常
    - runtimeException



```java
public byte[] getBytes(String charsetName) throws UnsupportedEncodingException{
    throw new ...
    
}//函数定义抛出异常   ---上层需要catch异常


public void example(){
    try {
    	// 可能抛出异常的代码
        if (someCondition) {
            throw new RuntimeException("An error occurred");
        }
    } catch (RuntimeException e) {
        // 处理异常
        System.out.println("Caught exception: " + e.getMessage());
    }
}
catch (...,e)-//可以捕获 Exception Throable Error
```

## 自定义异常

```java
class BaseException extends RuntimeException{

}
```

## assert

```java
-AssertionError //抛出AssertionError

$ java -ea Main.java
```









# JAVA结构

```java
JDT：javadoc（文档生成器）、jdb（调试器）、jconsole（监控工具）、javap（反编译工具）
JRE   ---jvm,java class library
----------------------------------------------    
JDK ---->重组为94模块：JRE+JDT
jlink ---->用于连接JDK的不同模块
   
javac Example.java    
编译后：解释方式：   
ASM- Java源代码 → javac编译 → .class文件 → ASM处理 → 修改后的.class文件 → JVM执行
    
（Ahead-Of-Time Compilation：如GraalVM Native Image）    
AOT- Java源代码 → javac编译 → .class文件 → AOT编译器 → 本地机器码 → 直接执行
JIT just in time compilation   运行时编译
    
ASM     
//ClassVisitor MethodVisitor API编写转换器
import org.objectweb.asm.*;
//methodvisitor
public class CalculatorMethodAaptor extends MethodVisitor{
    private String methodName;
    public CalculatorMethodAdapter(MethodVisitor mv, String methodName){
        super(Opcodes.ASM);
        this.methodName = methodName;
    }
    // 这是方法开始时插入代码的地方
    @Override
    public void visitCode() {
        super.visitCode(); // 先执行原有代码
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"); ...
     }
     // 这是在方法返回前插入代码的地方
    @Override
    public void visitInsn(int opcode) {
    }
}
//ClassVisitor
public class LoggingClassTransformer extends ClassVisitor {
	public LoggingClassTransformer(ClassVisitor cv) {
        super(Opcodes.ASM9, cv);
    }
   	//访问method时调用
     @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        // 只对我们感兴趣的 'add' 方法进行增强
        if ("add".equals(name) && "(II)I".equals(descriptor)) {
            return new CalculatorMethodAdapter(mv, name);//返回MethodVisitor 
        }
        return mv;
    }
}


//启动 reader
ClassReader reader = new ClassReader(originalClassBytes);
ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
LoggingClassTransformer transformer = new LoggingClassTransformer(writer); 
reader.accept(transformer, ClassReader.EXPAND_FRAMES); //启动 visitor
//触发classvisitor
...
//触发Methodvisitor
...
    

    // 访问类的每个方法
```





## Class结构

```java
//classFile   ---Klass 对象 包含这些信息
ClassFile {
    u4             magic; //`0xCAFEBABE` 组成，长度为 4 字节。
    u2             minor_version;//版本号
    u2             major_version;//版本号
    u2             constant_pool_count; //constant pool ：类，字段名，描述符int等，属性名，字面量常量
    cp_info        constant_pool[constant_pool_count-1];
    u2             access_flags;
    u2             this_class;
    u2             super_class;
    u2             interfaces_count;
    u2             interfaces[interfaces_count];
    //fields
    u2             fields_count; //fields table  访问标志+名称索引+描述符索引+属性表索引
    //static final 指向常量池的索引 （属性表索引指向constant pool）
    field_info     fields[fields_count];
    //method
    u2             methods_count;
    method_info    methods[methods_count];
    //anno
    u2             attributes_count;
    attribute_info attributes[attributes_count];
}

   ////method_info
    method_info {
        u2 access_flags;          // public/private/static ...
        u2 name_index;            // 常量池里的方法名索引
        u2 descriptor_index;      // 常量池里的方法签名描述符
        u2 attributes_count;
        attribute_info attributes[attributes_count]; // 关键在这里
    }
//bytecode 方法表里某些方法的 Code 属性里的 指令流
```





- **interface   table 实现的类的接口**

- methods table  方法表



```
+------------------+ 
| Magic Number     | 4 bytes (0xCAFEBABE)
+------------------+
| Version          | 4 bytes (major_version, minor_version)
+------------------+
| Constant Pool    | Variable size (depending on the number of constants)
+------------------+
| Access Flags     | 2 bytes
+------------------+
| This Class Index | 2 bytes (constant pool index for the class name)
+------------------+
| Superclass Index | 2 bytes (constant pool index for the superclass name)
+------------------+


+------------------+
| Interfaces+ count   | Variable size (list of interfaces implemented by the class)
+------------------+
| Fields   +count   | Variable size (list of fields)
|   (存储class)     |
+------------------+
| Methods          | Variable size (list of methods)
+------------------+
|
|
|
---->  descriptor:
	   flags:
	   Code:  字节码,操作数栈大小，局部变量表大小，异常表
	   

+------------------+
| Attributes       | Variable size (extra metadata like LineNumberTable)   
+------------------+---
					   |
					   | 
					   --> #存储注解
```

- **字节码**  用于指定jvm执行，jvm再对字节码进行机器特异  -->底层机器语言

### 字节码

```
getstatic   到运行时常量池解析静态字段引用   压入操作数栈
```









## 类加载流程

```java
//Classloader 流程
ClassLoader---加载类---链接（验证（字节码是否合法、安全）-准备（为静态字段分配内存并设默认值）-解析（把常量池里的符号引用解析为直接引用）（类，方法，字段））---初始化---使用与实例化--卸载
    
//<clinit>    
--按源文件顺序对静态变量赋初值并执行静态块；先父类后子类。
--只一个thread执行
static void <clinit>()   classinitializer类初始化

    

所有 静态字段的初始化赋值（比如 static int a = 10;）
所有 static {} 静态代码块中的内容     
```

### static 加载本地库





## ASM

```java
ClassA.class  	//指向Class<?> 
Class<?> clazz = Class.forName("com.example.Calculator");


//ASM 流程    
ClassReader--启动--ClassVisitor-浏览method--MethodVisitor--visitCode--visitInsn-
写入ClassVisitor的ByteVector缓冲区 ----visitXXX ---缓冲区复制写回     
    //ClassVisitor 
    ClassWriter  ---继承ClassVisitor
    //通过defineClass 修改classLoader进行动态修改加载
    class MyLoader extends ClassLoader {
            public Class<?> define(byte[] code) {
                return defineClass(null, code, 0, code.length);
            }
    }
    
```

## AOT



### 注册表

### 对比

#### CPP

```py
#CPP为虚函数的多态服务
-类名，field，method        #x
-函数（字段，名）  			#x
#AOT
元数据注册与序列化（Metadata Registration and Serialization）
-执行全程序静态可达性分析（whole-program static analysis） 返回元数据
java.lang.Class 对象的静态实例化（Static Instantiation of Class Objects）
-类镜像构建（class mirror construction）存储在 native image 的 堆初始化镜像（heap snapshot）在构建时即被填充


```







## JVM

![68747470733a2f2f6f73732e6a61766167756964652e636e2f6769746875622f6a61766167756964652f6a6176612f62617369732f6a766d2d726f7567682d7374727563747572652d6d6f64656c2e706e67](C:\Users\Tayhirro\Pictures\68747470733a2f2f6f73732e6a61766167756964652e636e2f6769746875622f6a61766167756964652f6a6176612f62617369732f6a766d2d726f7567682d7374727563747572652d6d6f64656c2e706e67.png)

- 加载阶段

  ```java
  -类加载子系统
  Bootstrap Class Loader -//加载Java核心库  rt.jar
  Extension Class Loader  -//扩展库jar/lib/ext
  Application Class Loader -//加载应用程序类路径(CLASSPATH)类
  ```
  
- 链接

  ```py
  -验证 是否jvm规范
  -准备  prepare  
  #类的静态变量分配内存  设置初始值
  -解析   resolve
  #符号引用转换为直接引用     -确定类，接口，字段和方法的内存布局
  1.Klass*的指针 HotSpot JVM中的对元数据对象的指针
  2.指向方法或字段元数据的指针   Method* FieldInfo* 
  -方法入口地址 字段在对象的偏移地址
  3.直接地址索引
  
  ----------------------------------------------
  解析类的字段   ---转换为  静态变量的内存地址
  
  
  
  ----------------------------------------------
  #特点
  1.运行时进行链接
  ```
  
- 运行时数据区 

  ```
  -方法区 method area
  -堆  heap
  #线程共享
  -栈 stack
  #每个线程有一个独立的栈
  TLS
  -本地方法栈   native method stack  
  #类似于栈帧
  ```
  
- 执行引擎

  ```py
  -Execution Engine 
  解释器 interpreter
  #逐条解释字节码
  
  JIT 编译器   Just-in-time
  #热点代码编译为本地机器码
  
  垃圾回收器  Garbage Collector
  #自动管理堆内存 回收对象
  # 标记-清除、复制、标记-整理
  ```



### Method area -Metaspace

#### Klass 

- HotSpot JVM 内部用于表示 Java 类的 C++ 对象

- ```
  MetaspaceObj
    └─ Klass
        └─ InstanceKlass  (普通类、注解、枚举)
        └─ InstanceMirrorKlass (Class对象的Klass)
        └─ InstanceClassLoaderKlass (类加载器的Klass)
        └─ TypeArrayKlass (基本类型数组)
        └─ ObjArrayKlass (对象数组)
  ```



- 具体内容

```java
Klass
类名、父类名、接口名 //Symbol* 指针
访问权限 access_flags // u4 整型   
field：
字段信息表	//Array<FieldInfo>*  to heap
methods：
方法表 _methods    //Array<Method*>*  to method
方法元数据 (名、返参、参数) //指针/索引  to Constantmethod
方法字节码    //u1 数组     to Constantmethod
虚函数表 _vtable    //Array<Method*>*  
数据：
常量池 ConstantPool //ConstantPool*  to metaspace ConstMethod (C++ 对象，独立分配) 
注解数据： //Annotations*
```





```java
类的基本信息   
 //类的元数据： 字段 方法   
-类名  父类名 接口 -访问权限 access_flags
-字段信息 
    
-方法表 -包含虚函数表vtable 用于override -方法信息   方法名  返回类型  参数类型 -方法字节码   -访问权限 access_flags
_methods
_vtable: Array<Method*> //JVM 在类链接阶段（linking）动态构建的 
 //静态
-由_static_field_data 指向   
    
 //常量
 通过 native 方法（如 getConstantPool()）间接访问 Metaspace 中的 ConstantPool
```

#### 运行时常量池

```
-运行时常量池 
tags[], operands[], resolved_klasses[]... 
```



#### Method and constantMethod区

```
_constMethod: ConstMethod* 
_access_flags, _vtable_index...  

//
_code: u1[] (字节码指令)  
_exception_table, _line_number_table... 
```



#### 独立静态区

```
-静态变量   
由_static_field_data 指向
```

### OOP

- **Ordinary object pointer**（普通对象指针）

```java
typedef class oopDesc*                            oop;
typedef class   instanceOopDesc*            instanceOop;  //普通对象
typedef class   arrayOopDesc*                    arraysOop;  //数组对象
typedef class     objArrayOopDesc*            objArrayOop;
typedef class     typeArrayOopDesc*            typeArrayOop;
```







### heap

#### oop

- 存储实际对象 

  | 内存区域             | 存储内容                                                     |
  | -------------------- | ------------------------------------------------------------ |
  | **对象头（Header）** | 包含哈希码、GC 信息、类型指针（指向 `String.class`）  指向method area 的类元数据 |
  | **实例字段**         | 存储 `value`（字符数组 `char[]`），存储 `hash`（用于缓存哈希值） |



```java
//oop

header + instance data
|--------------------------------------------|(8byte)
|           Mark Word（64位 JVM）           | → 存储锁信息、线程ID、哈希码等    Monitor（锁）
[hashcode | age | 0b01] //无锁
[ptr_to_monitor | 0b10] // 指向堆外的 ObjectMonitor    
|--------------------------------------------|	(8byte)
|         Klass Word（指向类元数据）         |
|--------------------------------------------|
|        Instance Data (实例数据)           |
|   int field1       |  4 bytes          | → 字段值直接存这里
|   Object* field2   |  4/8 bytes        | → 引用类型存指针
|   long field3      |  8 bytes          | → long/double 占8字节
|------------------------------------------|
|        Padding (可选对齐填充)             |
+------------------------------------------+    
//mark word
无锁 (Normal)    
 |   CAS    
 | ID+epoch+age+0b101 
偏向锁 (Biased)        ---可重入锁
轻量级锁 (Lightweight)    
重量级锁 (Heavyweight)
GC标记 (Marked)     //仅标记位，用于 GC 复制
    
//锁
当对象未被锁定时，Monitor 不存在
当对象被重量级锁（synchronized）锁定时，JVM 在 堆外（C Heap） ，//分配一个独立的 ObjectMonitor C++ 对象
Mark Word 中的锁指针指向它。    
```

### Monitor区域

```java
       +-----------------------+
       | ObjectMonitor (C Heap)|
       |  - _owner (线程ID)    |
       |  - _recursions (重入) |
       |  - _EntryList (等待队列)|
       +-----------------------+
  //
```



### 线程进程控制

##### 线程

```java
-thread     轻量级进程  LWP
-jvm 内部数据结构存储   
```

##### 进程

```
-单进程
```









### 栈帧

- 局部变量表   形参  局部变量  this引用
- 操作数栈          code的入栈出栈区域
- 动态连接区域
- 返回地址













### GC  垃圾回收





### class

- 通过Class类进行**实例**和**类静态变量**修改

```ja0va
// 使用 Unsafe 修改字段（底层机制）
        Unsafe unsafe = getUnsafe();
        long offset = unsafe.objectFieldOffset(field);
        unsafe.putObject(p, offset, "unsafeName");
//获取类中的field的偏移量修改


//接收 p offset
//返回一个Unsafe 修改
private static Unsafe getUnsafe() throws Exception {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }
```

### field

```java
class Field {
    private Class<?> clazz;  // 记录该 Field 属于哪个类
    private int fieldOffset; // 记录该字段在实例中的偏移量
}
```







## JNI  

- java native interface









## MVNW

- 用于启动maven wrapper 
- mvnw ,mvnw.cmd





















## 文件

### class

- 元数据区域





# JAVA日志

## Log4j











# JAVA指令

```makefile
javap -v   (显示更多细节)    //反编译

javac Example.java //生成字节码



```

# JAVA并发

## Runnable

```java
-实现run函数的类  
-可以提交给thread然后执行 

@FunctionalInterface
public interface Runnable {
    void run();
}
```



## Thread

```java
Thread t = new Thread();  
-Thread(Runnable target); Thread(Runnable target, String name)
-Thread(ThreadGroup group, Runnable target)
-Thread(ThreadGroup group, Runnable target, String name, long stackSize)
    
//创建单线程
class MyThread extends Thread {
    Runnable runnables;
    @Override
    public void run() {
       // runnables.run();
        System.out.println("start new thread!");
    }
}
Thread t = new MyThread();
    
//Thread implements Runnable
class MyRunnable implements Runnable{
    ...
}
Thread t = new Thread(new MyRunnable());

```

### 状态

```
new---running----blocked(被挂起(在外存))---waiting(运行中，但是在等待)---timed waiting(sleep()  等待)  --- terminated(线程终止) 

-----线程内部
run()

```





```
t.start()  //开始执行线程

t.stop()   //强制结束线程运行
t.running = false  //是否继续运行 


object.wait() //等待某个对象的通知
t.sleep() //休眠一段时间
t.join()  //等待t结束


```



### 中断

```java
-协作式 非抢占式 
#外部进程无法抢占目标进程

public static void main(String[] args){
	Thread worker = new Thread(()->{});
	worker.start();
	try{
		worker.join();
	}catch(InterruptedException e){
		Thread.currentThread().interrupt();
		worker.interrupt();
	}
}    
```

- 捕获对wait/wait timer 的中断 









### 线程间共享

- **volatile** 关键字

  ```
  -访问变量时，从主内存获取最新值
  -修改变量后，立刻写回主内存
  ```

  











### 守护线程

- 管理某些无法自己结束的线程

```
Thread t = new MyThread();
t.setDaemon(true);
t.start();
```







## Executor

```java
public interface Executor{
	void execute(Runnable command);
}   //执行一个runnable的对象

-ExecutorService 继承接口Executor
  
---------------------------------    
        
//pool   --线程池
    
ExecutorService executor;
//限制动态调整范围
public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(
            0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());
}


executor.submit(task1);  
shutdown();

----------------------------------
    
    
    
//schedule 
```

- **Thread**

  ```
  -Thread.currentThread()
  ```










## 线程池

```java

-SingleThreadExecutor   //单线程执行的线程池
-FixedThreadPool
ExecutorService s1 = Executors.newFixedThreadPoll(1);

s1.submit(new Task());  //runnable    
   
-CachedThreadPool

-ScheduledThreadPool
ses.schedule(new Task("one-time"), 1, TimeUnit.SECONDS);
ses.scheduleAtFixedRate(new Task("fixed-rate"), 2, 3, TimeUnit.SECONDS);
ses.scheduleWithFixedDelay(new Task("fixed-delay"), 2, 3, TimeUnit.SECONDS);



```

















## 原子操作

```java
public final int get() //获取当前的值
public final int getAndSet(int newValue)//获取当前的值，并设置新的值
public final int getAndIncrement()//获取当前的值，并自增
public final int getAndDecrement() //获取当前的值，并自减
public final int getAndAdd(int delta) //获取当前的值，并加上预期的值

boolean compareAndSet(int expect, int update) //如果输入的数值等于预期值，则以原子方式将该值设置为输入值（update）
public final void lazySet(int newValue)//最终设置为newValue, lazySet 提供了一种比 set 方法更弱的语义，可能导致其他线程在之后的一小段时间内还是可以读到旧的值，但可能更高效。
```

### Load/Store 

- 读写原子操作

### Disable Ints

- 关闭中断原子操作

### Test&Set

```
void lock(int *flag){
	while(test_and_set(flag) != 0){
	
	}
}
//读flag
//set flag=1
//return 旧值


```





### Compare&Swap

```
addr,expected,new_val
if (*addr == expected){
	 *addr = new_val;
        return true;
}
return false;
//读flag
//比较flag
//设置flag

```





## 线程安全问题





### Concurrent包  

```
//线程安全操作 -不需要加锁 


-BlockingQueue  
//支持阻塞  可以指定容量 
-put //队满等待
-take //队空等待
-poll }
-peek } //等待一段时间后放弃
-offer}  




-ConcurrentLinkedQueue 
-poll	//队头查看并删除
-peek   //队头查看

-offer  //队尾添加


```

### 信号量

```java
private Semaphore semaphore;
semaphore.acquire();  //信号量-1
semaphore.release(); //+1

```







### 锁

- 先放互斥锁，让其他先进入
- 再放资源锁  
- **嵌套释放   ---和获取锁对应** 

```Java
//是否可重入
-不可重入锁
-可重入锁  
- 同一个线程反复获取的锁
 
//-是否独占  非/独占锁      
//读写锁
    
    
//-分配方式：
-偏向锁
-当第一个线程尝试获取锁时，JVM 会将锁标记为偏向该线程（通过在对象头的 Mark Word 中记录线程 ID）。
-如果同一个线程再次尝试获取锁，无需进行 CAS 操作，直接检查 Mark Word 中的线程 ID 是否匹配即可。

-公平锁   
-所有尝试获取锁的线程都会加入一个 FIFO 队列。
    
//-是否悲观：
-悲观锁    乐观锁
 -悲观  获取锁
 -乐观  提交的时候再验证，然后是否回滚

    

-中断锁  

-超时机制
```









### synchronized

```java
synchronized(Object lock_en){	//锁对象  ,对Object进行加锁
	this.coun-= n;

}

//对方法中的代码块同步
public synchronized void add(int n){
    count += n;
}

//对静态方法同步  --对class类同步
public synchronized static void test(int n) {
    ...
}
object.wait   --指向的线程进行wait     

    
object.notifyAll   --通知所有线程
    
thread.run()    //启动线程 thread.start()   //启动线程   
thread.sleep() 
thread.join()  
thread.yield()  //让出cpu，但不会释放锁
    
```





- 获取单锁

```
-无锁
-偏向锁   ID标记对象头   无需CAS
-轻量级锁   CAS更新对象头  
-重量级锁  mutex
```

- 锁状态

  ```java
  //偏向锁 
  -线程ID  //如果无竞争，直接进入同步块。
      
  //轻量级锁
  -记录线程栈中锁记录
  class LockRecord{
   	ObjectMarkWord displaced_header; //指向旧mark word
      Object lock_object;			//指向锁对象 Object
      boolean locked;
  }
  //撤销锁对象头的偏向id-->添加指针-->在申请冲突的线程栈中添加锁记录对象
  
  重量级锁 
  -指向 monitor 对象
  //撤销锁对象头的轻量级锁id--->添加指向monitor对象
      
  //monitor对象
  class monitor{
     List<Thread> EntryList;//等待获取锁的线程队列
     List<Thread> WaitSet;//wait() 的线程队列。
  }    
   
  
  ```

- ```java
  -await()
  //当前线程释放锁对象的mark word ，thread加入WaitSet
  
  -signal()
  //线程b调用,将线程从 WaitSet 加入 EntryList
  
  
      
  ```

  

### Condition

```java
private final Condition condition = lock.newCondition();

    
condition.await();  //
condition.await(1, TimeUnit.SECOND)

condition.singalAll();  // 
condition.signal() //


```













### ReentrantLock

- 获取多重

- AQS（AbstractQueuedSynchronizer）

```
-基于concurrent.Lock 并发包实现

```



- 依赖 CAS 队列管理

```java
volatile int state //占用和重入次数

-读写锁   
ReentrantReadWriteLock  16位  16位
-读锁   写锁状态
ReentrantReadWriteLock rwLock = ...
Lock readLock = rwLock.readLock();
Lock writeLock = rwLock.writeLock();
//允许读
//单写 




-独占锁
ReentrantLock lock = new ReentrantLock();

lock.lock(); // state = 1（第一次获取锁）
lock.lock(); // state = 2（重入，第二次获取锁）
lock.unlock(); // state = 1（释放一次）
lock.unlock(); // state = 0（完全释放锁）
lock.tryLock(1, TimeUnit.SECONDS)
```





### StampedLock

```java
-读写锁
StampedLock stampedLock = new StampedLock();

//获取读锁
public double distanceFromOrigin(){
	long stamp = stampedLock.tryOptimisticRead();	
	//获取读锁
	
    if(!stampedLock.validate(stamp)){
        stamp = stampedLock.readLock(); //获取悲观读锁
        
        finally{
            stampedLock.unlockRead(stamp);
       
        }
    }

}


```

异步

## 消费者生产者

- 单向

```c
//一个函数模拟一个方面的内容


//生产消费
-单/多 生产者    <---->  单/多 消费者 //数量有限
-生产者生产特定内容

-方向
-顺序   指定执行顺序
    
    
ep:单向单缓冲区 严格顺序  多生产者 多消费者  
ep:单向单缓冲区  不严格顺序  多生产者 多消费者

mutex--->为同一个笼子同类别加锁
seamphore mutex = 1; 
semaphore tiger = 0,empty_tiger = 3,pig = 0,empty_pig = 5;
int tiger_num=0;
int pig_num=0;
semaphore tiger_num_mutex,pig_num_mutex;
void Producer_A(){
    do{
        wait(empty_tiger);
        wait(tiger_num_mutex);
        if(tiger_num==0) wait(mutex);
        tiger_num = (tiger_num+1)%3;
    	signal(tiger_num_mutex);
        signal(tiger);
    }while(TRUE);
}
void Consumer_Feed(){
    do{
        wait(tiger);
        wait(tiger_num_mutex);
        if(tiger_num ==0)signal(mutex);
        signal(tiger_num_mutex);
        signal(tiger);
    }while(TRUE);
}
------------------------------------------  
 //读者写者  
-单/多 写者-读者   单/多 写者-读者    
-顺序  指定顺序
-方向  单向双向
   
    
semaphore write_mutex;
semaphore reader_num_mutex;
int reader_num;

void writer(){
    
    wait(write_mutex);

    //操作
    signal(write_mutex);
}    

void reader(){
    if(reader_num==0){
        wait(write_mutex);
    }
    wait(reader_num_mutex);
    reader_num = reader_num+1;
    signal(reader_num_mutex);
    //读取时间
    wait(reader_num_mutex);
    reader_num = reader_num-1;
    if(reader_num==0){
        signal(write_mutex);
    }
    signal(reader_num_mutex);
}    

//---------------------------    
semaphore s_m mutex;
semaphore m_t mutex;
semaphore m mutex =2;
void S_to_P(){
    do{
        wait(s_m);
        wait(m);
        signal(s_m);
        wait(m_t);
        signal(m);
        signal(m_t);
    }while(TRUE);
}    ...
//有车就不能过 ---多车优化
//----------------------------
int waiting = 0;
const int n = 10;
semaphore set_waiting = 1;
semaphore cut = 1;
semaphore customers = 0;
void customer(){
    do{
        wait(set_waiting);
    	if(waiting<n){
            waiting++;
        	signal(set_waiting);
            signal(customers);   //看先增加谁都行
            wait(cut);

            //理发
        }else{
           signal(set_waiting); 
        }   
    }while(TRUE);
}
void cut(){
    do{
		wait(customers);
        
        wait(set_waiting);
        waiting--;
        signal(set_waiting);
        //开始理发
        signal(cut);
    }while(TRUE);
}

//------------------------------------


 
    
//资源
-单缓冲区      
-多缓冲区
int in =0;
int out = 0;
item buffer[n];
semaphore mutex = 1;
semaphore full = 0;
semaphore empty = n;
// semaphore   n  用于控制 
//int     n   用于判断 



void Producer(){
	do{
		wait(empty);
		wait(mutex);
		//放入
		signal(mutex);
		signal(full);
	
	}while(TRUE);

}

void Consumer(){
	do{
		wait(full);
		wait(mutex);
		//取出
		signal(mutex);
		signal(empty);
	}while(TRUE);

}

//------------------------3个水桶   10/水缸  1水井

semaphore mutex_1=1,mutex_2=1; //水桶  水缸
semaphore bucket_mutex =3;
semaphore empty_mutex =10,full_mutex = 0;

void Little_monk(){
	do(){
		wait(empty_mutex);
		wait(bucket_mutex);
		wait(mutex_2);
		//执行操作
		signal(mutex_2);
		wait(empty_mutex);
		wait(mutex_1);
		//放水
		signal(mutex_1);
		signal(bucket_mutex);
		signal(full_mutex); 
	}
}
void Old_monk(){
	do(){
		wait(full_mutex);
		wait(bucket_mutex);
		wait(mutex_1);
		//打水
		signal(mutex_1);
		signal(buckte_mutex);
		signal(signal_mutex);
	}
}
//----------------------------------


```



# Node

```bash
npm  #包管理器  Node Package Manager
npm install lodash  #-g
npm uninstall 
npm run dev  #运行脚本

npx  #包执行器

```

# Vue

```
main.js(router,app.vue,Vue runtime) , xxx.js
index.html
style-77bd9.css
--------------------------------------------------
Vue: <template>-->{render() setup()}  合并到js中  
	app.vue 合并到main.js中
html: index.html进入html
js:index.js进入main.js
```





## 状态管理器 Store

- 全局状态管理（Vuex）

  ```js
  Vue.use(Vuex)
  export default new Vuex.Store({
    state: {
      count: 0
    },
    mutations: {},
    actions: {},
    modules: {}
  })
  ```



## 根 Vue 实例

```js
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
```





# 代码开发

## 防御性编程

```

```
