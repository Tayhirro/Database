# Spring Boot 测试面试速记（JUnit5 + Mockito + MockMvc）

目标：用于面试和项目落地，快速回答“怎么测、测什么、用什么注解”。

---

## 1. 测试分层（面试高频）

### 1. 单元测试（Unit Test）
- 只测一个类的业务逻辑，不启动 Spring 容器。
- 依赖（DAO/Redis/HTTP）用 Mock 替代。
- 典型工具：`JUnit5` + `Mockito`。

### 2. 集成测试（Integration Test）
- 启动 Spring 容器，验证多组件协作是否正确。
- 可连接真实数据库/Redis（或 Testcontainers）。
- 典型注解：`@SpringBootTest`。

### 3. Web 层测试（Controller/MVC Test）
- 重点验证路由、参数校验、状态码、JSON 结构、拦截器行为。
- 不一定启动整个应用。
- 典型注解：`@WebMvcTest` + `MockMvc`。

面试答法：单元测试保证“逻辑正确且快速反馈”，集成测试保证“组件连接正确”，Web 测试保证“接口行为正确”。

---

## 2. JUnit5 基础（必须会）

### 2.1 常用注解
- `@Test`：测试方法。
- `@BeforeEach` / `@AfterEach`：每个测试前后执行。
- `@BeforeAll` / `@AfterAll`：整类前后执行（静态方法）。
- `@DisplayName("...")`：给测试起可读名称。
- `@Nested`：分组组织测试。

### 2.2 常用断言（高频）
- `assertEquals(expected, actual)`
- `assertNotEquals(unexpected, actual)`
- `assertTrue(condition)`
- `assertFalse(condition)`
- `assertNull(obj)` / `assertNotNull(obj)`
- `assertThrows(Exception.class, () -> {...})`
- `assertAll(...)`（多个断言一起校验）

### 2.3 参数化测试（非常加分）
- `@ParameterizedTest`
- `@ValueSource(strings = {...})`
- `@CsvSource({...})`
- `@MethodSource("methodName")`

适用：手机号、验证码、邮箱、金额边界值这类“同逻辑多输入”。

---

## 3. Mockito 基础（服务层面试高频）

### 3.1 常用注解
- `@ExtendWith(MockitoExtension.class)`
- `@Mock`：创建依赖的模拟对象。
- `@InjectMocks`：把 Mock 注入被测对象。

### 3.2 常用语法
- `when(mock.method(...)).thenReturn(...)`
- `when(...).thenThrow(...)`
- `verify(mock).method(...)`
- `verify(mock, times(2)).method(...)`
- `verifyNoMoreInteractions(mock)`

### 3.3 适用场景
- 测 Service，不希望真的连 MySQL/Redis/消息队列。
- 验证“调用了没、调用次数、参数是否正确”。

---

## 4. Spring Boot 常见测试注解对比

- `@SpringBootTest`
  - 启动完整 Spring 上下文。
  - 适合集成测试，最真实但较慢。

- `@WebMvcTest(Controller.class)`
  - 只加载 MVC 相关 Bean。
  - 搭配 `MockMvc` 测 Controller 快且清晰。

- `@DataJpaTest`
  - 只测 JPA 层（Repository）。

- `@MybatisTest`
  - 只测 MyBatis 层（Mapper）。

- `@MockBean`
  - 在 Spring 容器里替换某个 Bean 为 Mock。

---

## 5. MockMvc（接口测试核心）

常测点：
- 状态码：`200/400/401/403/404`
- 返回体字段是否存在/正确
- 参数缺失、参数格式错误
- 拦截器是否生效（如未登录返回 401）

常用结构：
1. `mockMvc.perform(...)`
2. `andExpect(status().is...)`
3. `andExpect(jsonPath("...").value(...))`

---

## 6. 可直接套用的测试设计方法

### 6.1 AAA 模型（面试必答）
1. Arrange：准备输入与前置数据。
2. Act：调用被测方法。
3. Assert：断言结果。

### 6.2 边界值法
- 长度、最小值、最大值、空值、null、非法字符。
- 例如验证码：`5位`、`6位`、`7位`、包含 `-`、包含空格。

### 6.3 等价类法
- 有效输入一类，无效输入多类（格式错、长度错、为空）。

---

## 7. 面试常见问答模板

### Q1：单元测试和集成测试区别？
- 单元测试关注单个方法逻辑，依赖都 Mock，快。
- 集成测试关注组件协作，真实 Spring 环境，慢但更接近生产。

### Q2：为什么要 Mock？
- 降低外部依赖不稳定性（DB/Redis/网络），让测试更快、更稳定、可重复。

### Q3：覆盖率多少才算好？
- 不迷信 100%，核心业务和高风险分支必须覆盖。
- 通常强调“关键路径 + 异常分支 + 边界值”。

### Q4：测试失败如何定位？
- 看断言失败位置、输入数据、Mock 行为、日志。
- 先确认“预期是否正确”，再检查被测逻辑。

---

## 8. 你当前项目（hmdp）建议优先写的测试

1. `RegexUtils`：验证码/手机号格式校验（单元测试 + 参数化）。
2. `UserServiceImpl.sendCode`：60 秒限流逻辑（集成或 Mock Redis）。
3. `RedisTokenAuthResolver`：token 解析 + TTL 续期。
4. 拦截器链：未登录返回 `401`，已登录放行（`MockMvc`）。

---

## 9. 常见踩坑（面试可加分）

- 把集成测试当单元测试写，速度慢且不稳定。
- 静态状态未清理（如 `ThreadLocal`）导致测试互相污染。
- 断言太弱（只断言不为 null），没有验证关键业务结果。
- 用了真实外部服务导致 CI 偶发失败。

---

## 10. 一句话记忆

“单测保逻辑、集成保协作、接口保行为；核心是可重复、可定位、能防回归。”

