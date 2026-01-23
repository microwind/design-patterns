# SpringWind Framework

[![Java Version](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

> 一个轻量级的类Spring框架实现，深入剖析并实践现代Java企业级框架的核心原理

SpringWind 是一个轻量级Web框架，它从零开始实现了 Spring Framework 的核心机制，包括 IoC 容器、依赖注入、AOP、Web MVC 和 JDBC 模板。通过阅读和学习 SpringWind 的源码，开发者可以深入理解 Spring 框架的设计思想和实现原理。

## 目录

- [设计理念](#设计理念)
- [核心特性](#核心特性)
- [架构设计](#架构设计)
- [快速开始](#快速开始)
- [深入理解](#深入理解)
- [设计模式](#设计模式)
- [项目结构](#项目结构)

---

## 设计理念

SpringWind 的设计遵循以下核心理念：

### 1. 控制反转 (IoC) - 依赖管理的哲学

传统的程序设计中，对象自己负责创建和管理它的依赖对象。这导致了代码的高度耦合和难以测试。SpringWind 通过 IoC 容器接管了对象的创建和生命周期管理，实现了：

- **依赖关系的外部化**：对象不再主动创建依赖，而是声明需要什么依赖
- **单一职责原则**：业务对象专注于业务逻辑，不关心依赖的创建
- **可测试性**：依赖可以被轻松替换为 Mock 对象

```java
// 传统方式 - 紧耦合
public class UserService {
    private UserRepository repository = new UserRepositoryImpl(); // 硬编码依赖
}

// SpringWind 方式 - 松耦合
@Service
public class UserService {
    @Autowired
    private UserRepository repository; // 依赖注入，可替换
}
```

### 2. 约定优于配置 (Convention over Configuration)

SpringWind 采用注解驱动的开发模式，最小化配置文件的使用：

- 使用 `@Component`、`@Service`、`@Controller` 等注解自动注册 Bean
- 使用 `@Autowired` 自动装配依赖
- 使用 `@RequestMapping` 自动映射 URL 到处理方法

这种方式大大减少了样板代码，让开发者专注于业务逻辑。

### 3. 面向切面编程 (AOP) - 关注点分离

横切关注点（如日志、事务、安全）不应该与业务逻辑混合在一起。SpringWind 的 AOP 实现通过动态代理技术，将这些关注点从业务代码中分离出来：

```java
@Aspect
public class LoggingAspect {
    @Before("com.example.service.*.*")
    public void logBefore(Method method) {
        System.out.println("执行方法: " + method.getName());
    }
}
```

### 4. 模板方法模式 - 封装最佳实践

JDBC 操作涉及大量的样板代码（获取连接、创建语句、处理结果、关闭资源）。SpringWind 的 JdbcTemplate 封装了这些固定流程，让开发者只需要关心 SQL 语句和结果映射：

```java
// 传统 JDBC - 50+ 行代码
Connection conn = null;
PreparedStatement stmt = null;
ResultSet rs = null;
try {
    conn = dataSource.getConnection();
    stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
    stmt.setInt(1, userId);
    rs = stmt.executeQuery();
    // ... 处理结果
} catch (SQLException e) {
    // ... 异常处理
} finally {
    // ... 关闭资源（3个try-catch块）
}

// SpringWind JdbcTemplate - 2 行代码
User user = jdbcTemplate.queryForObject(
    "SELECT * FROM users WHERE id = ?",
    (rs, rowNum) -> new User(rs),
    userId
);
```

### 5. 分层架构 - 关注点分离

SpringWind 将框架功能清晰地分为四个层次：

```
┌─────────────────────────────────────────────────────────────┐
│                    应用层 (Application)                      │
│  开发者编写的业务代码：Controller、Service、Repository        │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    框架层 (Framework)                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Web MVC   │  │     AOP     │  │   JDBC Template     │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                 核心容器层 (IoC Container)                   │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ Bean 定义   │  │ 依赖注入    │  │ 生命周期管理         │  │
│  │ 管理        │  │             │  │                     │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                   基础设施层 (Infrastructure)                │
│  反射、类加载器、动态代理、注解处理                           │
└─────────────────────────────────────────────────────────────┘
```

---

## 核心特性

### ✅ IoC 容器 - 对象生命周期的守护者

**核心类**: `SpringWindApplicationContext.java:35`

SpringWind 的 IoC 容器是框架的心脏，负责管理应用中所有对象（Bean）的创建、配置和生命周期。

#### 三级缓存机制解决循环依赖

SpringWind 实现了类似 Spring 的三级缓存策略来处理循环依赖问题：

```java
// SpringWindApplicationContext.java:40-47
private final Map<String, Object> singletonObjects;            // 一级缓存：完整初始化的Bean
private final Map<String, Object> earlySingletonObjects;       // 二级缓存：早期Bean引用
private final Map<String, ObjectFactory<?>> singletonFactories; // 三级缓存：ObjectFactory工厂
private final Set<String> singletonsCurrentlyInCreation;       // 正在创建的Bean集合
```

**工作原理**:
1. 创建 Bean A 时，先实例化并将ObjectFactory放入三级缓存（关键！）
2. 如果 Bean A 依赖 Bean B，开始创建 Bean B
3. 如果 Bean B 又依赖 Bean A，从三级缓存取出ObjectFactory并调用getObject()
4. getObject()内部调用getEarlyBeanReference，由SmartInstantiationAwareBeanPostProcessor决定返回原始对象还是代理对象
5. 将获取的早期引用放入二级缓存，从三级缓存移除
6. 完成 Bean B 的创建后，继续完成 Bean A 的创建

**为什么需要三级缓存？**
- 如果Bean需要AOP代理，最终注入的应该是代理对象而非原始对象
- ObjectFactory的延迟特性允许在真正需要时才创建代理，保证循环依赖的所有Bean拿到同一个代理对象

这种机制允许单例Bean的属性注入循环依赖得到解决（不支持构造器循环依赖）。

#### Bean 生命周期管理

```
┌──────────────┐
│ 1. 扫描组件   │ → 识别 @Component、@Service 等注解的类
└──────────────┘
       ↓
┌──────────────┐
│ 2. 注册定义   │ → 创建 BeanDefinition 并存入容器
└──────────────┘
       ↓
┌──────────────┐
│ 3. 实例化     │ → 通过反射创建对象实例
└──────────────┘
       ↓
┌──────────────┐
│ 4. 依赖注入   │ → 扫描 @Autowired 字段并注入依赖
└──────────────┘
       ↓
┌──────────────┐
│ 5. 初始化     │ → 调用 @PostConstruct 标注的方法
└──────────────┘
       ↓
┌──────────────┐
│ 6. 就绪使用   │ → Bean 可被应用程序使用
└──────────────┘
       ↓
┌──────────────┐
│ 7. 销毁      │ → 容器关闭时调用 @PreDestroy 方法
└──────────────┘
```

#### 扩展点：BeanPostProcessor

SpringWind 提供了 `BeanPostProcessor` 接口，允许在 Bean 初始化前后进行自定义处理：

```java
public interface BeanPostProcessor {
    // 初始化前的处理
    Object postProcessBeforeInitialization(Object bean, String beanName);

    // 初始化后的处理（可用于创建代理对象）
    Object postProcessAfterInitialization(Object bean, String beanName);
}
```

### ✅ 依赖注入 (DI) - 自动装配的魔法

**核心实现**: `SpringWindApplicationContext.java` 的 `doDependencyInjection()` 方法

SpringWind 支持字段注入和方法注入两种方式：

```java
@Service
public class UserService {
    // 字段注入
    @Autowired
    private UserRepository userRepository;

    // 方法注入
    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
```

**注入流程**:
1. 扫描类的所有字段和方法
2. 识别带有 `@Autowired` 注解的成员
3. 根据类型从容器中查找匹配的 Bean
4. 通过反射设置字段值或调用方法

### ✅ 面向切面编程 (AOP) - 代理的艺术

**核心类**: `AspectProcessor.java`、`AopInvocationHandler.java`

SpringWind 的 AOP 实现基于动态代理技术，支持两种代理策略：

#### 双代理策略

```java
// AspectProcessor.java 中的代理选择逻辑
if (target.getClass().getInterfaces().length > 0) {
    // 如果目标类实现了接口，使用 JDK 动态代理
    return Proxy.newProxyInstance(...);
} else {
    // 否则使用 CGLIB 字节码增强
    return Enhancer.create(...);
}
```

**JDK 动态代理**: 基于接口，通过 `java.lang.reflect.Proxy` 创建代理对象
**CGLIB 代理**: 基于继承，通过字节码生成子类实现代理

#### 支持的通知类型

```java
@Aspect
public class TransactionAspect {
    @Before("com.example.service.*.*")
    public void beginTransaction() { /* ... */ }

    @After("com.example.service.*.*")
    public void commitTransaction() { /* ... */ }

    @Around("com.example.service.*.*")
    public Object aroundAdvice(Method method, Object[] args) { /* ... */ }
}
```

#### 切点表达式

SpringWind 使用正则表达式实现切点匹配：

```java
// 模式: "com.example.service.*.*"
// 转换为正则: "com\\.example\\.service\\..*\\..*"
// 匹配: com.example.service.UserService.getUser()
```

**性能优化**: 切点正则表达式会被编译并缓存，避免重复编译。

### ✅ Web MVC - 前端控制器模式

**核心类**: `DispatcherServlet.java`

SpringWind 的 Web MVC 实现了经典的前端控制器（Front Controller）模式。

#### 请求处理流程

```
HTTP 请求
    ↓
┌─────────────────────┐
│ DispatcherServlet   │ → Servlet 容器的统一入口
└─────────────────────┘
    ↓
┌─────────────────────┐
│ URL 映射查找         │ → 根据请求路径和 HTTP 方法查找处理器
└─────────────────────┘
    ↓
┌─────────────────────┐
│ 参数解析            │ → 解析 @RequestParam、@PathVariable、@RequestBody
└─────────────────────┘
    ↓
┌─────────────────────┐
│ 调用 Controller     │ → 通过反射调用控制器方法
└─────────────────────┘
    ↓
┌─────────────────────┐
│ 视图解析            │ → 处理返回值（String/Map/Object）
└─────────────────────┘
    ↓
HTTP 响应
```

#### 路径变量与参数绑定

```java
@Controller
public class UserController {
    @RequestMapping("/user/{id}")
    public User getUser(
        @PathVariable("id") Long userId,          // 路径变量
        @RequestParam("format") String format,    // 查询参数
        @RequestBody UserDTO dto,                 // 请求体（JSON）
        HttpServletRequest request                // Servlet 对象
    ) {
        return userService.getUser(userId);
    }
}
```

**路径匹配机制** (`PathMatcher.java`):
- 将 `/user/{id}` 转换为正则表达式 `^/user/([^/]+)$`
- 使用捕获组提取路径变量
- 支持多个路径变量：`/order/{orderId}/item/{itemId}`

#### 响应处理策略

| 返回类型 | 处理方式 |
|---------|---------|
| `String` (以 `redirect:` 开头) | HTTP 重定向 |
| `String` (以 `forward:` 开头) | 服务器端转发 |
| `String` (以 `html:` 开头) | 返回 HTML 内容 |
| `String` (其他) | 视图名称，转发到 JSP |
| `Map` 或 `Object` | 转换为 JSON 响应 |
| `ViewResult` | 自定义视图结果对象 |

### ✅ JDBC 模板 - 简化数据访问

**核心类**: `JdbcTemplate.java`

SpringWind 的 JDBC 模板封装了 JDBC 的样板代码，提供了简洁的数据库操作 API。

#### 核心方法

```java
// 查询列表
List<User> users = jdbcTemplate.query(
    "SELECT * FROM users WHERE age > ?",
    (rs, rowNum) -> new User(
        rs.getLong("id"),
        rs.getString("name"),
        rs.getInt("age")
    ),
    18
);

// 查询单个对象
User user = jdbcTemplate.queryForObject(
    "SELECT * FROM users WHERE id = ?",
    (rs, rowNum) -> new User(rs),
    userId
);

// 查询标量值（自动类型转换）
Integer count = jdbcTemplate.queryForScalar(
    "SELECT COUNT(*) FROM users",
    Integer.class
);

// 更新操作
int rows = jdbcTemplate.update(
    "UPDATE users SET name = ? WHERE id = ?",
    "新名称", userId
);

// 批量操作
jdbcTemplate.batchUpdate(
    "INSERT INTO users (name, age) VALUES (?, ?)",
    Arrays.asList(
        new Object[]{"张三", 25},
        new Object[]{"李四", 30}
    )
);
```

#### 模板方法模式的实现

JdbcTemplate 将 JDBC 操作分解为固定的步骤：

```java
1. 获取数据库连接 (getConnection)
2. 创建 PreparedStatement
3. 设置参数 (setParameters)
4. 执行 SQL
5. 处理结果集 (RowMapper 回调)
6. 关闭资源 (closeResources)
```

开发者只需要提供 SQL 语句和结果映射逻辑（RowMapper），其他都由框架处理。

#### 资源管理

JdbcTemplate 确保数据库资源的正确关闭，即使发生异常：

```java
Connection conn = null;
PreparedStatement stmt = null;
ResultSet rs = null;
try {
    // ... 执行 SQL
} catch (SQLException e) {
    throw new RuntimeException(e);
} finally {
    closeResources(rs, stmt, conn);  // 独立的 try-catch，避免资源泄漏
}
```

---

## 架构设计

### 整体架构图

```
┌─────────────────────────────────────────────────────────────┐
│                    SpringWind Framework                     │
├─────────────────────────────────────────────────────────────┤
│  Application Layer (应用层)                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐    │
│  │Controller│  │ Service  │  │Repository│  │  Aspect  │    │
│  │  层      │  │   层     │  │   层     │  │   层     │    │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘    │
├─────────────────────────────────────────────────────────────┤
│  Framework Layer (框架层)                                    │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Web MVC   │  │     AOP     │  │   JDBC Template     │  │
│  │   Layer     │  │   Proxy     │  │      Layer          │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│  Core Container Layer (核心容器层)                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ Bean Def    │  │ Dependency  │  │ Lifecycle           │  │
│  │ Registry    │  │ Injection   │  │ Management          │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ Singleton   │  │ Circular    │  │ BeanPost            │  │
│  │ Pool        │  │ Dependency  │  │ Processor           │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│  Infrastructure Layer (基础设施层)                           │
│  反射 API | 类加载器 | 动态代理 | 注解处理 | 并发工具          │
└─────────────────────────────────────────────────────────────┘
```

### Bean 创建和依赖注入流程

```
客户端代码          IoC 容器            BeanA           BeanB
   |                  |                 |               |
   | getBean(BeanA)   |                 |               |
   |----------------->|                 |               |
   |                  | 检查一级缓存      |               |
   |                  | (未命中)         |               |
   |                  |                 |               |
   |                  | 标记为创建中      |               |
   |                  | (循环依赖检测)    |               |
   |                  |                 |               |
   |                  | 实例化 BeanA     |               |
   |                  |---------------->|               |
   |                  |                 |               |
   |                  | 放入二级缓存      |               |
   |                  | (早期引用)       |               |
   |                  |                 |               |
   |                  | 注入 BeanB       |               |
   |                  |-------------------------------->|
   |                  |                 |               |
   |                  |                 | 设置 BeanB    |
   |                  |                 | 到 BeanA      |
   |                  |<--------------------------------|
   |                  |                 |               |
   |                  | 调用 init 方法   |               |
   |                  |---------------->|               |
   |                  |                 |               |
   |                  | 移至一级缓存      |               |
   |                  | (完整 Bean)      |               |
   |                  |                 |               |
   |<-----------------|                 |               |
   | 返回 BeanA        |                 |               |
```

### AOP 代理流程

```
客户端调用
   ↓
┌─────────────────────┐
│   代理对象           │
│  (Proxy Object)     │
└─────────────────────┘
   ↓
┌─────────────────────┐
│ InvocationHandler   │
│ 拦截方法调用         │
└─────────────────────┘
   ↓
┌─────────────────────┐
│ 执行 @Before 通知   │ → 前置增强逻辑
└─────────────────────┘
   ↓
┌─────────────────────┐
│ 调用目标方法         │ → 实际的业务逻辑
└─────────────────────┘
   ↓
┌─────────────────────┐
│ 执行 @After 通知    │ → 后置增强逻辑
└─────────────────────┘
   ↓
返回结果
```

---

## 快速开始

### 环境要求

- Java 17 或更高版本
- Maven 3.6+

### 添加依赖

```xml
<dependency>
    <groupId>microwind.github.com</groupId>
    <artifactId>springwind</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 基本使用示例

#### 1. 定义服务类

```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id);
    }
}
```

#### 2. 定义数据访问层

```java
@Repository
public class UserRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User findById(Long id) {
        return jdbcTemplate.queryForObject(
            "SELECT * FROM users WHERE id = ?",
            (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email")
            ),
            id
        );
    }
}
```

#### 3. 定义控制器

```java
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/user/{id}")
    public User getUser(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }
}
```

#### 4. 定义切面

```java
@Aspect
public class LoggingAspect {
    @Before("com.example.service.*.*")
    public void logBefore(Method method) {
        System.out.println("开始执行方法: " + method.getName());
    }

    @After("com.example.service.*.*")
    public void logAfter(Method method) {
        System.out.println("方法执行完成: " + method.getName());
    }
}
```

#### 5. 启动应用

```java
@Configuration
public class AppConfig {
    // 配置类
}

public class Application {
    public static void main(String[] args) {
        SpringWindApplication.run(AppConfig.class);
    }
}
```

### 使用命令

```bash
# 编译项目
mvn clean compile -DskipTests=true

# 运行测试
mvn clean test

# 打包
mvn clean package

# 安装到本地仓库
mvn clean install
```

---

## 深入理解

### 循环依赖的解决方案

SpringWind 使用三级缓存机制解决单例 Bean 的循环依赖问题。

#### 问题场景

```java
@Service
public class ServiceA {
    @Autowired
    private ServiceB serviceB;  // A 依赖 B
}

@Service
public class ServiceB {
    @Autowired
    private ServiceA serviceA;  // B 依赖 A (循环依赖!)
}
```

#### 解决流程

```
1. 开始创建 ServiceA (getBean("serviceA"))
   ├─ 检查一级缓存 → 未找到
   ├─ 标记为正在创建：singletonsCurrentlyInCreation.add("serviceA")
   ├─ 实例化 ServiceA（调用构造器，得到原始对象）
   ├─ **关键：将 ObjectFactory 放入三级缓存**
   │   singletonFactories.put("serviceA", () -> getEarlyBeanReference("serviceA", rawA))
   ├─ 开始为 ServiceA 注入依赖 → 发现需要 ServiceB
   │
   └─ 2. 开始创建 ServiceB (getBean("serviceB"))
      ├─ 检查一级缓存 → 未找到
      ├─ 标记为正在创建：singletonsCurrentlyInCreation.add("serviceB")
      ├─ 实例化 ServiceB（得到原始对象）
      ├─ **将 ObjectFactory 放入三级缓存**
      │   singletonFactories.put("serviceB", () -> getEarlyBeanReference("serviceB", rawB))
      ├─ 开始为 ServiceB 注入依赖 → 发现需要 ServiceA (循环依赖！)
      │
      ├─ 3. 再次调用 getBean("serviceA")
      │   ├─ 检查一级缓存 → 未找到
      │   ├─ 发现 serviceA 在正在创建集合中 → 进入循环依赖处理逻辑
      │   ├─ 检查二级缓存 → 未找到
      │   ├─ **从三级缓存取出 ObjectFactory**
      │   ├─ **调用 ObjectFactory.getObject()**
      │   │   → 内部调用 getEarlyBeanReference("serviceA", rawA)
      │   │   → SmartInstantiationAwareBeanPostProcessor 可能在此创建 AOP 代理
      │   │   → 返回 ServiceA 的早期引用（可能是代理对象）
      │   ├─ 将早期引用放入二级缓存：earlySingletonObjects.put("serviceA", earlyA)
      │   ├─ 从三级缓存移除：singletonFactories.remove("serviceA")
      │   └─ 返回 ServiceA 的早期引用给 ServiceB ✓
      │
      ├─ ServiceB 成功注入 ServiceA（早期引用）
      ├─ 执行 ServiceB 的前置处理器
      ├─ 执行 @PostConstruct 初始化方法
      ├─ 执行后置处理器（AOP 代理可能在此创建）
      ├─ **完全初始化好的 ServiceB 放入一级缓存**
      │   singletonObjects.put("serviceB", serviceB)
      ├─ 清理二、三级缓存
      └─ 从正在创建集合移除：singletonsCurrentlyInCreation.remove("serviceB")

3. 继续完成 ServiceA
   ├─ ServiceA 成功注入 ServiceB（从一级缓存获取）
   ├─ 执行 ServiceA 的前置处理器
   ├─ 执行 @PostConstruct 初始化方法
   ├─ 执行后置处理器（如果已在 getEarlyBeanReference 创建代理，则跳过）
   ├─ **完全初始化好的 ServiceA 放入一级缓存**
   │   singletonObjects.put("serviceA", serviceA)
   ├─ 清理二、三级缓存
   └─ 从正在创建集合移除：singletonsCurrentlyInCreation.remove("serviceA")
```

**关键点**:
- **一级缓存** (`singletonObjects`): 存储完全初始化好的单例 Bean（依赖注入、初始化都完成）
- **二级缓存** (`earlySingletonObjects`): 存储早期 Bean 引用（已实例化，可能是代理对象）
- **三级缓存** (`singletonFactories`): 存储 ObjectFactory 工厂对象（**不是Bean本身！**）
- **限制**: 仅支持单例Bean的属性/字段注入，不支持构造器循环依赖

**为什么需要三级缓存（singletonFactories）？**

这是最精妙的设计！如果只有二级缓存会有问题：
- 当Bean需要AOP代理时，最终注入的应该是**代理对象**，而非原始对象
- 在循环依赖场景下，Bean创建时不知道是否会被其他Bean引用，不知道何时创建代理
- 如果直接在二级缓存放原始对象，后续创建代理后，其他Bean拿到的仍是原始对象，导致不一致

**ObjectFactory的作用**：
1. **延迟特性**：只有在真正被依赖时才调用getObject()，不浪费资源
2. **灵活性**：在getObject()中调用getEarlyBeanReference()，由SmartInstantiationAwareBeanPostProcessor决定返回原始对象还是代理对象
3. **一致性**：保证所有引用拿到的是同一个对象（早期引用一旦生成，就缓存在二级缓存中）

**核心代码**（SpringWindApplicationContext.java:615-621, 669-684）:
```java
// 1. 放入三级缓存
singletonFactories.put(beanName, new ObjectFactory<Object>() {
    @Override
    public Object getObject() {
        return getEarlyBeanReference(beanName, rawBean); // 延迟调用
    }
});

// 2. 获取早期引用
protected Object getEarlyBeanReference(String beanName, Object bean) {
    Object exposedObject = bean;
    for (BeanPostProcessor bp : beanPostProcessors) {
        if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
            // AOP在这里提前创建代理
            exposedObject = ((SmartInstantiationAwareBeanPostProcessor) bp)
                .getEarlyBeanReference(exposedObject, beanName);
        }
    }
    return exposedObject; // 可能是原始对象，也可能是代理对象
}
```

### 注解的工作原理

Java 注解本质上是特殊的接口，继承自 java.lang.annotation.Annotation。SpringWind仿照Spring 在启动时通过反射或字节码扫描读取注解信息。

**创建注解**
```java
// 注解的元注解定义示例
@Target(ElementType.TYPE)      // 注解作用目标：类、接口、枚举
@Retention(RetentionPolicy.RUNTIME)  // 注解保留策略：运行时可见
@Documented                    // 包含在Javadoc中
@Component                     // 标记为Spring组件
public @interface Service {
    String value() default "";  // 注解属性
}
```

**注解执行**
```java
// 1. 扫描类上的注解
Class<?> clazz = Class.forName("com.example.UserService");
if (clazz.isAnnotationPresent(Service.class)) {
    // 这是一个服务类，需要注册为Bean
}

// 2. 扫描字段上的注解  
Object bean = createBeanInstance(clazz);  // 创建Bean实例
for (Field field : clazz.getDeclaredFields()) {
    if (field.isAnnotationPresent(Autowired.class)) {
        // 需要依赖注入
        Class<?> fieldType = field.getType();
        Object dependency = getBean(fieldType);  // 获取依赖的Bean
        
        // 设置字段值
        field.setAccessible(true);
        field.set(bean, dependency);
    }
}
```

### 性能优化策略

#### 1. 构造器缓存

```java
// SpringWindApplicationContext.java:49
private final Map<Class<?>, Constructor<?>> constructorCache = new ConcurrentHashMap<>();
```

反射获取构造器的成本较高，SpringWind 会缓存每个类的构造器，避免重复查找。

#### 2. 切点表达式编译缓存

```java
// 正则表达式编译是昂贵的操作
private final Map<String, Pattern> patternCache = new ConcurrentHashMap<>();

public boolean matches(String pointcut, String methodName) {
    Pattern pattern = patternCache.computeIfAbsent(pointcut, Pattern::compile);
    return pattern.matcher(methodName).matches();
}
```

#### 3. ConcurrentHashMap 避免锁竞争

SpringWind 在所有并发场景中使用 `ConcurrentHashMap` 而不是 `Hashtable` 或 `synchronized` 包装的 `HashMap`，提供更好的并发性能。

---

## 设计模式

SpringWind 是学习设计模式的绝佳案例，框架中应用了多种经典设计模式：

### 1. 工厂模式 (Factory Pattern)

**应用场景**: IoC 容器创建 Bean

```java
// SpringWindApplicationContext 就是一个 Bean 工厂
public class SpringWindApplicationContext {
    public <T> T getBean(Class<T> requiredType) {
        // 工厂方法：根据类型创建或返回 Bean
    }
}
```

### 2. 单例模式 (Singleton Pattern)

**应用场景**: 单例 Bean 的管理

```java
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

public Object getSingleton(String beanName) {
    // 确保每个 Bean 在容器中只有一个实例
    return singletonObjects.computeIfAbsent(beanName, this::createBean);
}
```

### 3. 代理模式 (Proxy Pattern)

**应用场景**: AOP 动态代理

```java
// JDK 动态代理
Object proxy = Proxy.newProxyInstance(
    classLoader,
    interfaces,
    new AopInvocationHandler(target, aspects)
);

// CGLIB 代理
Enhancer enhancer = new Enhancer();
enhancer.setSuperclass(targetClass);
enhancer.setCallback(new AopMethodInterceptor(target, aspects));
Object proxy = enhancer.create();
```

### 4. 模板方法模式 (Template Method Pattern)

**应用场景**: JdbcTemplate 的固定流程

```java
public <T> List<T> query(String sql, RowMapper<T> mapper, Object... args) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
        conn = getConnection();        // 步骤1: 获取连接
        stmt = prepareStatement(sql);  // 步骤2: 创建语句
        setParameters(stmt, args);     // 步骤3: 设置参数
        rs = stmt.executeQuery();      // 步骤4: 执行查询
        return mapResults(rs, mapper); // 步骤5: 映射结果 (回调)
    } finally {
        closeResources(rs, stmt, conn); // 步骤6: 关闭资源
    }
}
```

### 5. 策略模式 (Strategy Pattern)

**应用场景**: RowMapper 结果映射策略

```java
// 不同的映射策略
RowMapper<User> userMapper = (rs, rowNum) -> new User(rs);
RowMapper<Order> orderMapper = (rs, rowNum) -> new Order(rs);

// 统一的执行接口
List<User> users = jdbcTemplate.query(sql, userMapper);
List<Order> orders = jdbcTemplate.query(sql, orderMapper);
```

### 6. 观察者模式 (Observer Pattern)

**应用场景**: Bean 生命周期事件

```java
// BeanPostProcessor 观察 Bean 的创建过程
public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(Object bean, String beanName);
    Object postProcessAfterInitialization(Object bean, String beanName);
}
```

### 7. 前端控制器模式 (Front Controller Pattern)

**应用场景**: DispatcherServlet 统一处理 HTTP 请求

```java
public class DispatcherServlet extends HttpServlet {
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // 所有请求的统一入口
        String path = req.getRequestURI();
        HandlerMethod handler = getHandler(path);
        handler.invoke(req, resp);
    }
}
```

### 8. 注册表模式 (Registry Pattern)

**应用场景**: Bean 定义的注册和查找

```java
private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

public void registerBeanDefinition(String beanName, BeanDefinition definition) {
    beanDefinitionMap.put(beanName, definition);
}

public BeanDefinition getBeanDefinition(String beanName) {
    return beanDefinitionMap.get(beanName);
}
```

---

## 项目结构

```
springwind/
├── pom.xml                                      # Maven 项目配置
├── README.md                                     # 本文档
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/github/microwind/springwind/
│   │   │       ├── SpringWindApplication.java   # 框架启动类
│   │   │       │
│   │   │       ├── annotation/                  # 注解定义
│   │   │       │   ├── Aspect.java              # AOP 切面注解
│   │   │       │   ├── After.java               # 后置通知
│   │   │       │   ├── Around.java              # 环绕通知
│   │   │       │   ├── Autowired.java           # 依赖注入注解
│   │   │       │   ├── Before.java              # 前置通知
│   │   │       │   ├── Component.java           # 组件注解
│   │   │       │   ├── Controller.java          # 控制器注解
│   │   │       │   ├── Repository.java          # 数据访问层注解
│   │   │       │   ├── RequestMapping.java      # URL 映射注解
│   │   │       │   ├── Service.java             # 服务层注解
│   │   │       │   ├── Transactional.java       # 事务注解
│   │   │       │   ├── RequestParam.java        # 请求参数注解
│   │   │       │   ├── PathVariable.java        # 路径变量注解
│   │   │       │   └── RequestBody.java         # 请求体注解
│   │   │       │
│   │   │       ├── core/                        # 核心 IoC 容器
│   │   │       │   ├── SpringWindApplicationContext.java  # IoC 容器核心类
│   │   │       │   ├── BeanDefinition.java      # Bean 定义
│   │   │       │   ├── BeanPostProcessor.java   # Bean 后处理器接口
│   │   │       │   └── PropertyValue.java       # 属性值
│   │   │       │
│   │   │       ├── aop/                         # AOP 实现
│   │   │       │   ├── AspectProcessor.java     # 切面处理器 (代理创建)
│   │   │       │   ├── AspectInfo.java          # 切面信息
│   │   │       │   ├── AspectType.java          # 通知类型枚举
│   │   │       │   └── AopInvocationHandler.java # AOP 调用处理器
│   │   │       │
│   │   │       ├── jdbc/                        # JDBC 模板
│   │   │       │   ├── JdbcTemplate.java        # JDBC 模板类
│   │   │       │   └── RowMapper.java           # 结果集映射接口
│   │   │       │
│   │   │       ├── web/                         # Web MVC
│   │   │       │   ├── DispatcherServlet.java   # 前端控制器
│   │   │       │   ├── HandlerMapping.java      # 处理器映射
│   │   │       │   ├── PathMatcher.java         # 路径匹配器
│   │   │       │   ├── ViewResult.java          # 视图结果
│   │   │       │   ├── HttpRequestUtil.java     # HTTP 请求工具
│   │   │       │   └── JsonUtil.java            # JSON 工具
│   │   │       │
│   │   │       ├── exception/                   # 异常定义
│   │   │       │   ├── BeanCreationException.java
│   │   │       │   ├── BeanNotFoundException.java
│   │   │       │   └── CircularDependencyException.java
│   │   │       │
│   │   │       └── util/                        # 工具类
│   │   │           ├── ClassScanner.java        # 类扫描器
│   │   │           └── StringUtils.java         # 字符串工具
│   │   │
│   │   └── resources/
│   │       └── META-INF/
│   │           └── MANIFEST.MF
│   │
│   └── test/
│       ├── java/                                # 测试代码
│       │   └── microwind/github/com/springwind/
│       │       ├── IoCTest.java                 # IoC 容器测试
│       │       ├── AopTest.java                 # AOP 测试
│       │       ├── MvcTest.java                 # Web MVC 测试
│       │       └── JdbcTest.java                # JDBC 模板测试
│       └── resources/
│           ├── application.properties
│           └── test-data.sql
│
└── examples/                                     # 示例项目
    ├── user-demo/                                # 用户管理示例
    │   ├── src/
    │   └── pom.xml
    └── web-demo/                                 # Web 应用示例
        ├── src/
        └── pom.xml
```

---

## 核心注解说明

| 注解 | 作用范围 | 说明 |
|-----|---------|------|
| `@Component` | 类 | 通用组件注解，标记为 Spring 管理的 Bean |
| `@Service` | 类 | 服务层组件，继承自 @Component |
| `@Controller` | 类 | 控制器组件，处理 HTTP 请求 |
| `@Repository` | 类 | 数据访问层组件 |
| `@Autowired` | 字段/方法 | 自动注入依赖 |
| `@RequestMapping` | 方法 | 映射 HTTP 请求到控制器方法 |
| `@RequestParam` | 参数 | 绑定 HTTP 请求参数 |
| `@PathVariable` | 参数 | 绑定 URL 路径变量 |
| `@RequestBody` | 参数 | 绑定 HTTP 请求体（JSON） |
| `@Aspect` | 类 | 标记为 AOP 切面 |
| `@Before` | 方法 | 前置通知 |
| `@After` | 方法 | 后置通知 |
| `@Around` | 方法 | 环绕通知 |
| `@Transactional` | 方法 | 事务管理（需配合 AOP） |

---

## 学习路线图

### 初级：理解基本概念

1. **IoC 和 DI 的概念**
   - 阅读 `SpringWindApplicationContext.java` 的 `scanComponents()` 方法
   - 理解如何扫描类路径并注册 Bean 定义
   - 理解 `@Autowired` 如何实现依赖注入

2. **注解的使用**
   - 学习如何使用 `@Component`、`@Service`、`@Controller`
   - 理解注解的元注解和继承关系

3. **Bean 生命周期**
   - 理解从扫描到实例化到初始化的完整流程
   - 学习 `@PostConstruct` 和 `@PreDestroy` 的使用

### 中级：深入核心机制

1. **循环依赖的解决**
   - 研究三级缓存的实现原理
   - 理解早期 Bean 引用的作用
   - 尝试创建循环依赖的示例并观察框架行为

2. **AOP 的实现**
   - 理解 JDK 动态代理和 CGLIB 代理的区别
   - 学习切点表达式的匹配机制
   - 实现自定义切面（如日志、性能监控）

3. **Web MVC 的请求处理**
   - 理解前端控制器模式
   - 学习参数解析和类型转换
   - 实现自定义的参数解析器

### 高级：架构设计和性能优化

1. **设计模式的应用**
   - 识别框架中使用的各种设计模式
   - 理解为什么在特定场景使用特定模式
   - 尝试重构代码并应用其他模式

2. **性能优化**
   - 研究构造器缓存、正则表达式缓存等优化策略
   - 理解 ConcurrentHashMap 的并发优势
   - 尝试添加性能监控和分析

3. **扩展框架**
   - 实现自定义的 BeanPostProcessor
   - 添加新的注解类型
   - 实现事务管理功能

---

## 与 Spring Framework 的对比

| 特性 | SpringWind | Spring Framework |
|-----|-----------|-----------------|
| IoC 容器 | ✅ 基本实现 | ✅ 完整实现 + 复杂场景 |
| 依赖注入 | ✅ 字段注入、方法注入 | ✅ 字段、方法、构造器注入 |
| 循环依赖 | ✅ 三级缓存 | ✅ 三级缓存 + 多种策略 |
| AOP | ✅ JDK/CGLIB 代理 | ✅ AspectJ 集成 + 更强大的切点表达式 |
| Web MVC | ✅ 基本实现 | ✅ 完整的 MVC 栈 + 异步支持 |
| JDBC | ✅ JdbcTemplate | ✅ JdbcTemplate + JPA/Hibernate 集成 |
| 事务管理 | ❌ 需自行实现 | ✅ 声明式和编程式事务 |
| 配置方式 | ✅ 注解驱动 | ✅ 注解 + XML + Java Config |
| Bean 作用域 | ⚠️ 仅单例 | ✅ Singleton、Prototype、Request、Session 等 |
| 国际化 | ❌ | ✅ MessageSource |
| 事件机制 | ❌ | ✅ ApplicationEvent |
| SpEL 表达式 | ❌ | ✅ |

**SpringWind 的定位**：教育型框架，专注于核心原理的清晰实现，而非生产环境的完整功能。

---

## FAQ

### Q: SpringWind 可以用于生产环境吗？

A: SpringWind 是一个教育型框架，主要用于学习和理解 Spring 的核心原理。它缺少生产环境所需的许多特性（如完整的事务管理、安全性、性能调优等）。如果需要在生产环境使用，建议选择 Spring Framework。

### Q: 为什么选择 Java 17？

A: Java 17 是长期支持版本（LTS），提供了许多现代 Java 特性（如 Record、Pattern Matching、Sealed Classes 等），同时保持了稳定性。这些特性让代码更简洁、更安全。

### Q: SpringWind 支持 Spring Boot 吗？

A: SpringWind 是一个独立的框架，不依赖也不兼容 Spring Boot。它的目标是从零实现 Spring 的核心机制，而不是基于 Spring 构建。

### Q: 如何调试 SpringWind 的源码？

A: 1. Clone 项目到本地
2. 在 IDE 中导入为 Maven 项目
3. 在关键类（如 `SpringWindApplicationContext`）设置断点
4. 运行测试用例或示例项目
5. 观察 Bean 的创建、依赖注入、AOP 代理等流程

### Q: 是否支持构造器注入？

A: 当前版本主要支持字段注入和方法注入。构造器注入需要更复杂的参数解析逻辑，可以作为学习项目自行实现。

### Q: 如何贡献代码？

A: 欢迎提交 Pull Request！建议的贡献方向：
- 添加单元测试
- 改进文档和注释
- 实现新特性（如事务管理、更多注解支持）
- 性能优化
- Bug 修复

---

## 技术栈

- **核心语言**: Java 17
- **构建工具**: Maven
- **日志框架**: SLF4J + Logback
- **AOP 代理**: JDK Dynamic Proxy + CGLIB
- **Web 容器**: Jakarta Servlet API
- **JSON 处理**: Jackson
- **测试框架**: JUnit 4 + Mockito
- **数据库**: H2 (测试), MySQL (生产)

---

## 致谢

SpringWind 的设计深受 Spring Framework 的启发，感谢 Spring 团队为 Java 社区贡献了如此优秀的框架。通过深入学习Spring框架，我理解了其核心原理和设计模式，为SpringWind的实现提供了重要的参考。

---

## 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

---

## 联系方式

- **项目主页**: https://github.com/microwind/design-patterns/tree/main/practice-projects/springwind
- **问题反馈**: https://github.com/microwind/design-patterns/issues

---

**愿你在学习 SpringWind 的过程中，觉得开心愉悦！** 
