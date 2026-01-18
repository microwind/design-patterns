# 从零手搓一个类Spring框架，彻底搞懂Spring核心原理

> 作者：JarryLi
> 适合读者：Java开发工程师、架构师、对框架原理感兴趣的同学

## 前言

还记得很多年前刚接触`Spring`的时候，我觉得很神奇，通过xml就可以自动注入对象，甚至一个`@Autowired`注解就可以？AOP 还能在不侵入业务代码的情况下统一加日志和事务？
那时主流还是 EJB 体系，使用原生Servlet、JSP以及Struts框架。面对 IoC、DI 这些看似“魔法”的能力，总想一探究竟：它们到底是怎么实现的？

也正是从那时起，一直在学习`Spring`源码。真正看进去才发现，这些并不是魔法，而是大量工程化设计的结果：层层抽象、接口解耦、经典设计模式反复出现。源码规模不小，很难一口气看完，只能带着问题一点点啃。

看书百遍，不如自己动手实践，于是参照Spring框架写一个简化版的MVC框架。**用最简洁的代码实现 Spring 的核心功能，让每个想深入理解框架原理的同学都能看懂。**。

在这个过程中，我不仅搞懂了IoC、DI、AOP这些核心概念以及Bean生命周期、DispatcherServlet与 ApplicationContext容器体系，还深刻理解了为什么Spring要这样设计。现在，我把这些心得分享给你，希望能帮你少走弯路。

## 目录

- [关于 SpringWind 框架](#关于-springwind-框架)
- [Spring的设计理念](#spring的设计理念)
- [Spring的核心特性](#spring的核心特性)
- [深入理解Spring框架](#深入理解spring框架)
- [参照Spring框架设计Springwind的架构](#参照spring框架设计springwind的架构)
- [SpringWind 与 Spring MVC 的对比](#springwind-与-spring-mvc-的对比)
- [Springwind框架有什么用](#springwind框架有什么用)
- [Springwind的项目结构](#springwind的项目结构)
- [Springwind使用实践](#springwind使用实践)
- [快速开始](#快速开始)
- [Springwind改进点](#springwind改进点)

---

## 关于 SpringWind 框架

SpringWind 是一个**教学性质的轻量级 Java Web 框架**，旨在帮助开发者深入理解 Spring 框架的核心原理。通过从零开始实现 Spring 的核心机制，让框架不再是"黑盒子"。

### 框架定位

SpringWind **不是**为了替代 Spring Framework，而是作为：
- **学习工具**：理解 Spring 核心原理的最佳实践
- **教学框架**：清晰的代码结构，适合教学和分享
- **快速原型**：小型项目的轻量级选择
- **探索平台**：验证新想法和设计方案的试验田

### 核心特性

SpringWind 实现了 Spring Framework 的以下核心功能：

| 特性 | 说明 | 对应 Spring 功能 |
|-----|------|-----------------|
| **IoC 容器** | 自动扫描、注册和管理 Bean 的生命周期 | ApplicationContext |
| **依赖注入** | 通过 `@Autowired` 实现自动装配 | Dependency Injection |
| **组件注解** | `@Component`、`@Service`、`@Controller`、`@Repository` | Stereotype Annotations |
| **MVC 模式** | 模拟 Spring MVC 的请求处理机制 | Spring MVC |
| **请求映射** | `@RequestMapping` 实现 URL 到方法的映射 | Request Mapping |
| **参数绑定** | `@PathVariable`、`@RequestParam`、`@RequestBody` | Parameter Binding |
| **AOP 支持** | 基于 JDK/CGLIB 的动态代理实现切面编程 | Spring AOP |
| **JDBC 模板** | 简化数据库操作的模板类 | JdbcTemplate |
| **JSON 响应** | 自动序列化对象为 JSON | @ResponseBody |

### 与 Spring Framework 的关系

SpringWind 参考了 Spring Framework 的设计思想，但做了大量简化：

**相同点：**
- 核心设计理念（IoC、DI、AOP）
- 注解驱动开发
- 分层架构（Controller-Service-Dao）
- 三级缓存解决循环依赖
- 模板方法封装样板代码

**不同点：**
- **代码规模**：SpringWind 核心代码不到 2000 行，Spring Framework 超过 50 万行
- **功能范围**：SpringWind 聚焦核心功能，Spring 提供全面的企业级特性
- **复杂度**：SpringWind 去除了大量抽象层次，更易理解
- **生产就绪**：Spring 经过大规模验证，SpringWind 主要用于学习

### 技术栈

```
核心技术：
├── Java 17+                    # 现代 Java 特性
├── Jakarta Servlet API 6.1     # Web 容器标准
├── CGLIB 3.3.0                 # 字节码增强（AOP）
├── SLF4J + Logback            # 日志框架
└── Jackson 2.18.2             # JSON 序列化

构建工具：
└── Maven 3.6+

运行环境：
├── Embedded Tomcat 11         # 嵌入式 Web 服务器
└── H2 / MySQL                 # 数据库（可选）
```

### SpringWind 与 Spring MVC 注解对照表

为了降低学习成本，SpringWind 的注解与 Spring MVC 保持一致：

| SpringWind 注解 | Spring MVC 注解 | 作用 | 示例 |
|----------------|----------------|------|------|
| `@Component` | `@Component` | 标记通用组件 | `@Component` |
| `@Service` | `@Service` | 标记服务层组件 | `@Service` |
| `@Controller` | `@Controller` | 标记控制器组件 | `@Controller` |
| `@Repository` | `@Repository` | 标记数据访问层组件 | `@Repository` |
| `@Autowired` | `@Autowired` | 自动注入依赖 | `@Autowired private UserDao dao;` |
| `@RequestMapping` | `@RequestMapping` | 映射 HTTP 请求 | `@RequestMapping("/user")` |
| `@PathVariable` | `@PathVariable` | 绑定路径变量 | `@PathVariable("id") Long id` |
| `@RequestParam` | `@RequestParam` | 绑定请求参数 | `@RequestParam("name") String name` |
| `@RequestBody` | `@RequestBody` | 绑定请求体（JSON） | `@RequestBody User user` |
| `@ResponseBody` | `@ResponseBody` | 返回 JSON 响应 | `@ResponseBody` |
| `@Aspect` | `@Aspect` | 标记切面类 | `@Aspect` |
| `@Before` | `@Before` | 前置通知 | `@Before("com.example.*")` |
| `@After` | `@After` | 后置通知 | `@After("com.example.*")` |
| `@Around` | `@Around` | 环绕通知 | `@Around("com.example.*")` |

**使用示例：**

```java
// 定义 Controller
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;  // 自动注入

    @RequestMapping("/get")
    @ResponseBody
    public User getUser(@RequestParam("id") Long id) {
        return userService.getUserById(id);
    }

    @RequestMapping("/detail/{id}")
    @ResponseBody
    public User getUserDetail(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @RequestMapping("/create")
    @ResponseBody
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
}
```

如果你熟悉 Spring MVC，就能**零学习成本**使用 SpringWind！

### 学习路径建议

**第一步：快速体验（10分钟）**
1. 运行示例项目，看看效果
2. 访问几个 API 接口，理解请求流程

**第二步：理解核心原理（1-2小时）**
1. 阅读本文档的"Spring的设计理念"和"Spring的核心特性"
2. 打断点调试，观察 Bean 的创建和依赖注入过程
3. 理解三级缓存如何解决循环依赖

**第三步：深入源码（3-5小时）**
1. 阅读 `SpringWindApplicationContext.java`（IoC 容器核心）
2. 阅读 `AspectProcessor.java`（AOP 代理创建）
3. 阅读 `DispatcherServlet.java`（MVC 请求分发）
4. 阅读 `JdbcTemplate.java`（JDBC 模板方法）

**第四步：动手实践（1-2天）**
1. 基于 SpringWind 写一个小项目
2. 尝试添加新功能（如事务支持）
3. 对比 Spring Framework 源码，理解设计差异

**第五步：给 Spring 贡献代码（进阶）**
1. 理解 Spring 的设计思想后，可以参与 Spring 社区
2. 提交 Bug 修复或新功能的 Pull Request

---

## Spring的设计理念

在开始造轮子之前，我们得先理解Spring为什么要这样设计。很多人觉得Spring框架复杂，但其实它的核心理念非常简单。

### 1. 控制反转（IoC）- 别自己new对象了

**传统做法的痛点：**

```java
// 传统方式：自己管理对象
public class UserService {
    private UserDao userDao = new UserDaoImpl();  // 硬编码依赖
    private EmailService emailService = new EmailServiceImpl();

    public void registerUser(User user) {
        userDao.save(user);
        emailService.sendWelcomeEmail(user);
    }
}
```

这样写有什么问题？

1. **耦合度高**：UserService强依赖具体的实现类
2. **难以测试**：无法替换成Mock对象做单元测试
3. **配置混乱**：数据库连接、邮件配置都硬编码在代码里

**Spring的解决方案：**

```java
// Spring方式：依赖注入
@Service
public class UserService {
    @Autowired
    private UserDao userDao;  // 由容器注入
    @Autowired
    private EmailService emailService;

    public void registerUser(User user) {
        userDao.save(user);
        emailService.sendWelcomeEmail(user);
    }
}
```

现在，对象的创建和管理交给了IoC容器，业务代码只需要声明"我需要什么"，而不用关心"怎么创建"。这就是**控制反转**的核心思想。

### 2. 面向切面编程（AOP）- 横切关注点分离

假设你要给所有Service方法加日志，传统做法是这样：

```java
public class UserService {
    public void registerUser(User user) {
        System.out.println("开始执行registerUser");  // 日志代码
        long start = System.currentTimeMillis();      // 性能监控代码

        userDao.save(user);  // 真正的业务逻辑

        long end = System.currentTimeMillis();
        System.out.println("执行完成，耗时: " + (end - start) + "ms");
    }
}
```

业务代码和日志、监控代码混在一起，非常臃肿。而且如果有100个方法，就得重复100遍这些代码。

**AOP的解决方案：**

```java
@Aspect
public class LoggingAspect {
    @Before("com.example.service.*.*")
    public void logBefore(Method method) {
        System.out.println("开始执行: " + method.getName());
    }

    @After("com.example.service.*.*")
    public void logAfter(Method method) {
        System.out.println("执行完成: " + method.getName());
    }
}

// 业务代码保持纯净
@Service
public class UserService {
    public void registerUser(User user) {
        userDao.save(user);  // 只关注业务逻辑
    }
}
```

日志、事务、权限校验这些**横切关注点**被抽离出来，业务代码变得清爽多了。

### 3. 约定优于配置 - 少写XML，多用注解

早期的Spring需要写大量XML配置：

```xml
<!-- 古老的Spring配置 -->
<bean id="userService" class="com.example.UserService">
    <property name="userDao" ref="userDao"/>
</bean>
<bean id="userDao" class="com.example.UserDaoImpl"/>
```

现在只需要：

```java
@Service
public class UserService {
    @Autowired
    private UserDao userDao;
}
```

一个注解就搞定了。这就是**约定优于配置**的威力。

### 4. 模板方法模式 - 封装样板代码

JDBC操作有大量的样板代码：

```java
// 传统JDBC：50多行代码
Connection conn = null;
PreparedStatement stmt = null;
ResultSet rs = null;
try {
    conn = dataSource.getConnection();
    stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
    stmt.setLong(1, userId);
    rs = stmt.executeQuery();
    if (rs.next()) {
        return new User(rs);
    }
} catch (SQLException e) {
    throw new RuntimeException(e);
} finally {
    if (rs != null) try { rs.close(); } catch (SQLException e) {}
    if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
    if (conn != null) try { conn.close(); } catch (SQLException e) {}
}
```

**Spring JdbcTemplate：**

```java
// 简洁的JdbcTemplate：2行代码
User user = jdbcTemplate.queryForObject(
    "SELECT * FROM users WHERE id = ?",
    (rs, rowNum) -> new User(rs),
    userId
);
```

获取连接、关闭资源这些固定步骤被封装了，你只需要专注于SQL和结果映射。

---

## Spring的核心特性

在理解了Spring的设计理念后，我们来看看它的核心特性是如何实现的。

### IoC容器 - 对象的大管家

IoC容器就像一个对象工厂，负责：

1. **扫描组件**：找到所有带`@Component`、`@Service`等注解的类
2. **注册Bean定义**：把类的元信息存起来
3. **实例化Bean**：通过反射创建对象
4. **依赖注入**：把依赖的对象注入进去
5. **生命周期管理**：管理Bean的创建和销毁

**Bean的生命周期：**

```
扫描 → 注册 → 实例化 → 依赖注入 → 初始化 → 就绪 → 销毁
```

在SpringWind中，我们用一个核心类`SpringWindApplicationContext`来实现这些功能：

```java
public class SpringWindApplicationContext {
    // 一级缓存：完整的Bean
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    // 二级缓存：早期Bean引用（解决循环依赖）
    private Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();
    // 正在创建的Bean集合
    private Set<String> singletonsCurrentlyInCreation = new HashSet<>();

    public SpringWindApplicationContext(Class<?> configClass) {
        scanComponents(configClass);      // 1. 扫描组件
        createSingletonBeans();           // 2. 创建Bean
        dependencyInjection();            // 3. 依赖注入
        invokeInitMethods();              // 4. 执行初始化方法
    }
}
```

### 循环依赖问题 - 三级缓存的精妙设计

这是面试常考的问题。假设有这样的循环依赖：

```java
@Service
public class ServiceA {
    @Autowired
    private ServiceB serviceB;  // A依赖B
}

@Service
public class ServiceB {
    @Autowired
    private ServiceA serviceA;  // B依赖A，形成循环
}
```

如果不处理，会陷入死循环：创建A → 需要B → 创建B → 需要A → 创建A → ...

**Spring的解决方案：三级缓存**

```java
// 创建A
1. 实例化A（调用构造器，此时A还没注入依赖）
2. 把A的早期引用放入二级缓存
3. 注入A的依赖（发现需要B）

   // 创建B
   4. 实例化B
   5. 把B的早期引用放入二级缓存
   6. 注入B的依赖（发现需要A）
   7. 从二级缓存获取A的早期引用 ✓
   8. 完成B的创建，移到一级缓存

9. 完成A的创建，移到一级缓存
```

关键是**允许Bean在未完全初始化时就暴露引用**。这样，循环依赖的双方都能拿到对方的引用，只是暂时还不是完整的对象。

### 依赖注入 - 自动装配的实现

依赖注入的核心是反射。SpringWind的实现：

```java
private void doDependencyInjection(Object bean) {
    Class<?> clazz = bean.getClass();

    // 扫描所有字段
    for (Field field : clazz.getDeclaredFields()) {
        if (!field.isAnnotationPresent(Autowired.class)) {
            continue;
        }

        // 根据类型从容器中查找Bean
        Object dependency = getBean(field.getType());
        if (dependency == null) {
            throw new BeanNotFoundException("找不到依赖: " + field.getType());
        }

        // 通过反射设置字段值
        field.setAccessible(true);
        field.set(bean, dependency);
    }
}
```

流程很简单：
1. 找到所有带`@Autowired`的字段
2. 根据字段类型从容器中查找对应的Bean
3. 通过反射把Bean注入进去

### AOP - 动态代理的艺术

AOP的核心是**动态代理**。SpringWind支持两种代理方式：

**1. JDK动态代理（基于接口）**

```java
// 如果目标类实现了接口
if (interfaces.length > 0) {
    return Proxy.newProxyInstance(
        classLoader,
        interfaces,
        new AopInvocationHandler(target, aspects)
    );
}
```

**2. CGLIB代理（基于继承）**

```java
// 如果目标类没有接口
Enhancer enhancer = new Enhancer();
enhancer.setSuperclass(targetClass);
enhancer.setCallback(new AopMethodInterceptor(target, aspects));
return enhancer.create();
```

**代理的工作流程：**

```
客户端调用方法
    ↓
代理对象拦截
    ↓
执行@Before通知（前置增强）
    ↓
调用目标方法（真正的业务逻辑）
    ↓
执行@After通知（后置增强）
    ↓
返回结果
```

### Web MVC - 前端控制器模式

SpringWind的Web MVC实现了经典的前端控制器模式：

```java
@WebServlet("/*")
public class DispatcherServlet extends HttpServlet {
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // 1. 获取请求路径
        String uri = req.getRequestURI();

        // 2. 查找处理器
        HandlerMethod handler = getHandler(uri);

        // 3. 解析参数
        Object[] args = resolveArguments(handler, req, resp);

        // 4. 调用Controller方法
        Object result = handler.invoke(args);

        // 5. 处理响应
        handleResult(result, resp);
    }
}
```

**请求处理流程：**

```
HTTP请求 → DispatcherServlet → URL映射 → 参数解析 → 调用Controller → 视图解析 → HTTP响应
```

### JDBC模板 - 样板代码的终结者

JdbcTemplate把JDBC操作固定的步骤封装起来：

```java
public <T> T queryForObject(String sql, RowMapper<T> mapper, Object... args) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
        conn = getConnection();        // 1. 获取连接
        stmt = prepareStatement(sql);  // 2. 创建语句
        setParameters(stmt, args);     // 3. 设置参数
        rs = stmt.executeQuery();      // 4. 执行查询
        return mapper.mapRow(rs, 0);   // 5. 映射结果（你只需要关心这一步）
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } finally {
        closeResources(rs, stmt, conn);  // 6. 关闭资源
    }
}
```

你只需要提供SQL和结果映射逻辑，其他的都由框架处理。

---

## 深入理解Spring框架

### 为什么要用注解？

注解本质上是**元数据**，用来描述代码的特性。Java通过反射API可以在运行时读取这些注解：

```java
// 扫描类上的注解
Class<?> clazz = Class.forName("com.example.UserService");
if (clazz.isAnnotationPresent(Service.class)) {
    // 这是一个Service，注册为Bean
}

// 扫描字段上的注解
for (Field field : clazz.getDeclaredFields()) {
    if (field.isAnnotationPresent(Autowired.class)) {
        // 这个字段需要依赖注入
    }
}
```

注解相比XML配置的优势：
1. **就近原则**：配置和代码在一起，更直观
2. **类型安全**：编译期检查，避免拼写错误
3. **重构友好**：IDE可以自动重构

### 反射 - Spring的基石

Spring大量使用反射来实现框架功能：

```java
// 1. 通过反射创建对象
Constructor<?> constructor = clazz.getDeclaredConstructor();
Object bean = constructor.newInstance();

// 2. 通过反射设置字段值
Field field = clazz.getDeclaredField("userDao");
field.setAccessible(true);
field.set(bean, userDaoBean);

// 3. 通过反射调用方法
Method method = clazz.getDeclaredMethod("registerUser", User.class);
method.invoke(bean, user);
```

反射的性能确实比直接调用慢，但Spring通过**缓存**来优化：

```java
// 构造器缓存
private Map<Class<?>, Constructor<?>> constructorCache = new ConcurrentHashMap<>();

public Object createBean(Class<?> clazz) {
    Constructor<?> constructor = constructorCache.computeIfAbsent(clazz, c -> {
        return c.getDeclaredConstructor();
    });
    return constructor.newInstance();
}
```

### 设计模式 - Spring是设计模式的教科书

Spring框架中应用了大量的设计模式：

| 设计模式 | 应用场景 | 举例 |
|---------|---------|------|
| **工厂模式** | Bean的创建 | ApplicationContext就是Bean工厂 |
| **单例模式** | Bean的作用域 | 默认的单例Bean |
| **代理模式** | AOP实现 | JDK动态代理、CGLIB代理 |
| **模板方法模式** | 固定流程封装 | JdbcTemplate、RestTemplate |
| **策略模式** | 不同的实现策略 | RowMapper结果映射 |
| **观察者模式** | 事件机制 | BeanPostProcessor |
| **前端控制器模式** | Web MVC | DispatcherServlet |
| **注册表模式** | Bean定义管理 | BeanDefinitionRegistry |

学习Spring，也是在学习如何优雅地应用设计模式。

### 并发安全 - ConcurrentHashMap的选择

Spring容器是线程安全的，SpringWind也要保证这一点。我们使用`ConcurrentHashMap`而不是`Hashtable`或加锁的`HashMap`：

```java
// ✓ 推荐：ConcurrentHashMap
private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

// ✗ 不推荐：Hashtable（方法级锁，性能差）
private Map<String, Object> singletonObjects = new Hashtable<>();

// ✗ 不推荐：synchronized包装（同样是粗粒度锁）
private Map<String, Object> singletonObjects =
    Collections.synchronizedMap(new HashMap<>());
```

`ConcurrentHashMap`使用分段锁，并发性能远高于`Hashtable`。

---

## 参照Spring框架设计Springwind的架构

### 整体架构设计

SpringWind采用分层架构，从下到上分为四层：

```
┌─────────────────────────────────────────────────────────────┐
│                    应用层 (Application Layer)                │
│  开发者编写的业务代码：Controller、Service、Repository       │
└─────────────────────────────────────────────────────────────┘
                             ↓
┌─────────────────────────────────────────────────────────────┐
│                    框架层 (Framework Layer)                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Web MVC   │  │     AOP     │  │   JDBC Template     │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                             ↓
┌─────────────────────────────────────────────────────────────┐
│                 核心容器层 (Core Container Layer)            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ Bean 定义   │  │ 依赖注入    │  │ 生命周期管理         │  │
│  │ 管理        │  │             │  │                     │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ 单例池      │  │ 循环依赖    │  │ BeanPost            │  │
│  │             │  │ 解决        │  │ Processor           │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                             ↓
┌─────────────────────────────────────────────────────────────┐
│                   基础设施层 (Infrastructure Layer)          │
│  反射 API | 类加载器 | 动态代理 | 注解处理 | 并发工具         │
└─────────────────────────────────────────────────────────────┘
```

### 核心模块设计

#### 1. IoC容器模块

**核心类：** `SpringWindApplicationContext`

**主要职责：**
- 组件扫描和注册
- Bean的创建和缓存
- 依赖注入
- 生命周期管理

**关键代码（SpringWindApplicationContext.java:35-64）：**

```java
public class SpringWindApplicationContext {
    // 一级缓存：完整的Bean
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    // 二级缓存：早期Bean引用（解决循环依赖）
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();
    // 正在创建的Bean集合
    private final Set<String> singletonsCurrentlyInCreation = new HashSet<>();
    // 构造器缓存（性能优化）
    private final Map<Class<?>, Constructor<?>> constructorCache = new ConcurrentHashMap<>();

    public SpringWindApplicationContext(Class<?> configClass) {
        scanComponents(configClass);      // 扫描组件
        createSingletonBeans();           // 创建单例Bean
        dependencyInjection();            // 依赖注入
        invokeInitMethods();              // 执行初始化方法
    }
}
```

#### 2. AOP模块

**核心类：** `AspectProcessor`、`AopInvocationHandler`

**主要职责：**
- 切面注册和管理
- 代理对象创建（JDK/CGLIB）
- 切点匹配
- 通知执行

**代理创建逻辑（AspectProcessor.java:59-80）：**

```java
public Object createProxy(Object target) {
    List<AspectInfo> matchedAspects = findMatchedAspects(target.getClass());
    if (matchedAspects.isEmpty()) {
        return target;  // 没有切面，返回原始对象
    }

    // 如果实现了接口，使用JDK动态代理
    Class<?>[] interfaces = target.getClass().getInterfaces();
    if (interfaces.length > 0) {
        return Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            interfaces,
            new AopInvocationHandler(target, matchedAspects)
        );
    }

    // 否则使用CGLIB代理
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(target.getClass());
    enhancer.setCallback(new AopMethodInterceptor(target, matchedAspects));
    return enhancer.create();
}
```

#### 3. Web MVC模块

**核心类：** `DispatcherServlet`、`HandlerMapping`、`PathMatcher`

**主要职责：**
- 请求路由
- 参数解析和绑定
- Controller调用
- 视图解析

**请求处理流程：**

```
HTTP请求
  ↓
DispatcherServlet（统一入口）
  ↓
HandlerMapping（查找处理器）
  ↓
PathMatcher（路径匹配）
  ↓
参数解析（@PathVariable, @RequestParam, @RequestBody）
  ↓
Controller方法调用
  ↓
结果处理（JSON/视图）
  ↓
HTTP响应
```

#### 4. JDBC模块

**核心类：** `JdbcTemplate`、`RowMapper`

**主要职责：**
- 连接管理
- SQL执行
- 结果集映射
- 资源关闭

**模板方法实现：**

```java
public <T> List<T> query(String sql, RowMapper<T> mapper, Object... args) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
        conn = getConnection();        // 固定步骤1
        stmt = prepareStatement(sql);  // 固定步骤2
        setParameters(stmt, args);     // 固定步骤3
        rs = stmt.executeQuery();      // 固定步骤4
        return mapResults(rs, mapper); // 可变步骤（回调）
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } finally {
        closeResources(rs, stmt, conn); // 固定步骤5
    }
}
```

### 注解体系设计

SpringWind定义了一套完整的注解体系：

| 注解 | 作用 | 元注解 |
|-----|------|--------|
| `@Component` | 通用组件 | - |
| `@Service` | 服务层组件 | `@Component` |
| `@Controller` | 控制器组件 | `@Component` |
| `@Repository` | 数据访问层组件 | `@Component` |
| `@Autowired` | 依赖注入 | - |
| `@RequestMapping` | URL映射 | - |
| `@PathVariable` | 路径变量 | - |
| `@RequestParam` | 请求参数 | - |
| `@RequestBody` | 请求体 | - |
| `@Aspect` | 切面 | - |
| `@Before` | 前置通知 | - |
| `@After` | 后置通知 | - |
| `@Around` | 环绕通知 | - |

### 性能优化策略

#### 1. 构造器缓存

反射获取构造器的成本较高，缓存起来：

```java
private final Map<Class<?>, Constructor<?>> constructorCache = new ConcurrentHashMap<>();

public Object createBean(Class<?> clazz) {
    Constructor<?> constructor = constructorCache.computeIfAbsent(clazz, c -> {
        try {
            return c.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new BeanCreationException("找不到无参构造器", e);
        }
    });
    return constructor.newInstance();
}
```

#### 2. 切点表达式缓存

正则表达式编译很慢，缓存编译后的Pattern：

```java
private final Map<String, Pattern> patternCache = new ConcurrentHashMap<>();

public boolean matches(String pointcut, String methodName) {
    Pattern pattern = patternCache.computeIfAbsent(pointcut, p -> {
        String regex = pointcut.replace("*", ".*").replace(".", "\\.");
        return Pattern.compile(regex);
    });
    return pattern.matcher(methodName).matches();
}
```

#### 3. 使用ConcurrentHashMap

所有需要并发访问的Map都使用`ConcurrentHashMap`，避免锁竞争。

---

## SpringWind 与 Spring MVC 的对比

了解 SpringWind 与 Spring MVC 的异同，可以帮助你更好地理解框架的设计取舍。

### 功能对比表

| 功能特性 | SpringWind | Spring MVC | 说明 |
|---------|-----------|------------|------|
| **IoC 容器** | ✅ 基本实现 | ✅ 完整实现 + 复杂场景 | SpringWind 实现了单例 Bean 管理、依赖注入、生命周期管理 |
| **依赖注入** | ✅ 字段注入、方法注入 | ✅ 字段、方法、构造器注入 | SpringWind 暂不支持构造器注入 |
| **循环依赖解决** | ✅ 三级缓存 | ✅ 三级缓存 + 多种策略 | 核心机制相同，Spring 支持更多边缘场景 |
| **Bean 作用域** | ⚠️ 仅单例（Singleton） | ✅ Singleton、Prototype、Request、Session 等 | SpringWind 只支持单例模式 |
| **AOP 支持** | ✅ JDK/CGLIB 代理 | ✅ AspectJ 集成 + 更强大的切点表达式 | SpringWind 实现了基本的 AOP 功能 |
| **切面类型** | ✅ @Before、@After、@Around | ✅ @Before、@After、@Around、@AfterReturning、@AfterThrowing | SpringWind 支持三种基本通知类型 |
| **Web MVC** | ✅ 基本实现 | ✅ 完整的 MVC 栈 + 异步支持 | SpringWind 实现了核心的请求映射和参数绑定 |
| **请求映射** | ✅ @RequestMapping | ✅ @RequestMapping、@GetMapping、@PostMapping 等 | SpringWind 使用统一的 @RequestMapping |
| **参数绑定** | ✅ @PathVariable、@RequestParam、@RequestBody | ✅ 更多参数类型和转换器 | SpringWind 支持基本的参数绑定 |
| **JSON 响应** | ✅ 自动序列化 | ✅ 多种视图解析器 | SpringWind 使用 Jackson 进行 JSON 序列化 |
| **JDBC 支持** | ✅ JdbcTemplate | ✅ JdbcTemplate + JPA/Hibernate 集成 | SpringWind 实现了 JdbcTemplate 的核心功能 |
| **事务管理** | ❌ 需自行实现 | ✅ 声明式和编程式事务 | SpringWind 暂不支持，可通过 AOP 自己实现 |
| **配置方式** | ✅ 注解驱动 | ✅ 注解 + XML + Java Config | SpringWind 主要使用注解配置 |
| **国际化（i18n）** | ❌ 不支持 | ✅ MessageSource | Spring 提供完整的国际化支持 |
| **事件机制** | ❌ 不支持 | ✅ ApplicationEvent | Spring 提供事件发布和监听机制 |
| **SpEL 表达式** | ❌ 不支持 | ✅ 完整的表达式语言 | Spring 支持强大的 SpEL 表达式 |
| **代码量** | ~2000 行 | ~50 万行 | SpringWind 代码量极小，易于理解 |
| **学习难度** | ⭐⭐ 容易 | ⭐⭐⭐⭐ 较难 | SpringWind 适合初学者理解原理 |
| **生产就绪** | ❌ 教学用途 | ✅ 企业级应用 | Spring 经过大规模生产验证 |

### 使用场景对比

| 场景 | 推荐使用 | 原因 |
|-----|---------|------|
| **学习 Spring 原理** | SpringWind | 代码简洁，易于理解核心机制 |
| **企业级应用** | Spring MVC | 功能完整，生产就绪 |
| **小型工具项目** | SpringWind | 轻量级，快速启动 |
| **Demo/原型开发** | SpringWind | 简单直接，无需复杂配置 |
| **大型分布式系统** | Spring Boot/Cloud | 完整的生态系统和中间件支持 |
| **需要事务支持** | Spring MVC | SpringWind 需要自己实现事务 |
| **需要复杂 AOP** | Spring MVC | 支持 AspectJ 和复杂切点表达式 |
| **快速原型验证** | SpringWind | 启动快，依赖少 |

### 代码对比示例

**定义一个简单的 Controller：**

```java
// SpringWind 和 Spring MVC 的代码几乎完全相同
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/list")
    @ResponseBody
    public List<User> list() {
        return userService.getAllUsers();
    }

    @RequestMapping("/get")
    @ResponseBody
    public User getUser(@RequestParam("id") Long id) {
        return userService.getUserById(id);
    }

    @RequestMapping("/create")
    @ResponseBody
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
}
```

**启动应用：**

```java
// SpringWind
public class Application {
    public static void main(String[] args) {
        SpringWindApplicationContext context =
            new SpringWindApplicationContext(Application.class);
        // 启动 Tomcat
        TomcatServer.start(context);
    }
}

// Spring Boot
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 迁移成本

**从 SpringWind 迁移到 Spring MVC：**
- **难度**：⭐⭐ 容易
- **工作量**：主要是替换依赖和配置
- **代码改动**：业务代码几乎不需要改动
- **注意事项**：
  - 添加 Spring Boot Starter 依赖
  - 调整配置文件（application.yml/properties）
  - 构造器注入需要添加构造器
  - 事务注解需要启用 @EnableTransactionManagement

**从 Spring MVC 迁移到 SpringWind：**
- **难度**：⭐⭐⭐ 中等
- **工作量**：需要调整部分高级特性
- **代码改动**：
  - 去除复杂的 SpEL 表达式
  - 事务代码改为手动管理或 AOP 实现
  - 复杂切点表达式需要简化
  - 构造器注入改为字段注入
- **适用场景**：小型项目降低复杂度

### 性能对比

| 指标 | SpringWind | Spring MVC |
|-----|-----------|------------|
| **启动时间** | ~200ms | ~2-5s（Spring Boot） |
| **内存占用** | ~50MB | ~150-300MB |
| **依赖数量** | ~5 个 | ~50-100 个 |
| **jar 包大小** | ~2MB | ~20-50MB |
| **并发性能** | 良好 | 优秀 |

**注意**：SpringWind 的性能优势主要体现在轻量级场景，对于大型应用，Spring 的优化更加全面。

### 学习建议

1. **先学 SpringWind，再学 Spring**
   - SpringWind 代码量小，容易看懂核心原理
   - 理解了原理后，再看 Spring 源码会轻松很多

2. **对比学习**
   - 用 SpringWind 写一个小项目
   - 用 Spring Boot 实现同样的功能
   - 对比两者的实现差异

3. **渐进式学习**
   - IoC/DI → AOP → MVC → JDBC → 事务
   - 每个模块都先在 SpringWind 中理解，再看 Spring 的实现

---

## Springwind框架有什么用

说实话，SpringWind不是为了替代Spring，而是为了**学习Spring**。但它也有自己的价值：

### 1. 彻底搞懂Spring原理和运行机制

**面试常问的问题，SpringWind都有答案：**

- **IoC容器是怎么工作的？**
  看`SpringWindApplicationContext`的实现，200多行代码就能看懂。

- **循环依赖怎么解决的？**
  看三级缓存的实现，代码简洁明了。

- **AOP是怎么实现的？**
  看`AspectProcessor`如何创建代理对象。

- **@Autowired是怎么注入的？**
  看`dependencyInjection`方法，反射设置字段值。

- **DispatcherServlet是怎么分发请求的？**
  看Web MVC模块，前端控制器模式的经典实现。

**学习建议：**

1. 先跑一遍示例项目，看看效果
2. 打断点调试，看Bean是怎么创建的
3. 对照Spring源码，理解设计思想
4. 自己动手改代码，加功能

### 2. 用在小项目上快速开发

Spring确实有点重，一个简单的Web项目，引入Spring Boot全家桶可能有上百个依赖。

SpringWind只有几个核心依赖：
```xml
<dependencies>
    <dependency>
        <groupId>jakarta.servlet</groupId>
        <artifactId>jakarta.servlet-api</artifactId>
    </dependency>
    <dependency>
        <groupId>cglib</groupId>
        <artifactId>cglib</artifactId>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
    </dependency>
</dependencies>
```

**适用场景：**
- 小型Web应用
- 内部工具系统
- 学习项目
- Demo演示

### 3. 探索新功能，给Spring提建议

SpringWind是一个试验田，你可以：
- 尝试新的设计思路
- 验证性能优化方案
- 实现Spring没有的功能
- 给Spring社区提PR

比如，你可以尝试：
- 实现响应式编程支持
- 优化Bean创建性能
- 简化配置方式
- 增强AOP功能

---

## Springwind的项目结构

SpringWind的代码组织清晰，模块划分明确：

```
springwind/
├── pom.xml                                      # Maven配置
├── README.md                                    # 项目文档
├── src/
│   ├── main/
│   │   └── java/com/github/microwind/springwind/
│   │       ├── SpringWindApplication.java      # 框架启动类
│   │       │
│   │       ├── annotation/                     # 注解定义（13个核心注解）
│   │       │   ├── Component.java              # 通用组件
│   │       │   ├── Service.java                # 服务层
│   │       │   ├── Controller.java             # 控制器
│   │       │   ├── Repository.java             # 数据访问层
│   │       │   ├── Autowired.java              # 依赖注入
│   │       │   ├── RequestMapping.java         # URL映射
│   │       │   ├── PathVariable.java           # 路径变量
│   │       │   ├── RequestParam.java           # 请求参数
│   │       │   ├── RequestBody.java            # 请求体
│   │       │   ├── Aspect.java                 # 切面
│   │       │   ├── Before.java                 # 前置通知
│   │       │   ├── After.java                  # 后置通知
│   │       │   └── Around.java                 # 环绕通知
│   │       │
│   │       ├── core/                           # 核心IoC容器
│   │       │   ├── SpringWindApplicationContext.java  # IoC容器核心
│   │       │   ├── BeanDefinition.java         # Bean定义
│   │       │   ├── BeanPostProcessor.java      # Bean后处理器
│   │       │   └── PropertyValue.java          # 属性值
│   │       │
│   │       ├── aop/                            # AOP实现
│   │       │   ├── AspectProcessor.java        # 切面处理器
│   │       │   ├── AspectInfo.java             # 切面信息
│   │       │   ├── AspectType.java             # 通知类型
│   │       │   └── AopInvocationHandler.java   # AOP调用处理器
│   │       │
│   │       ├── jdbc/                           # JDBC模板
│   │       │   ├── JdbcTemplate.java           # JDBC模板类
│   │       │   └── RowMapper.java              # 结果集映射
│   │       │
│   │       ├── web/                            # Web MVC
│   │       │   ├── DispatcherServlet.java      # 前端控制器
│   │       │   ├── HandlerMapping.java         # 处理器映射
│   │       │   ├── PathMatcher.java            # 路径匹配器
│   │       │   ├── ViewResult.java             # 视图结果
│   │       │   ├── HttpRequestUtil.java        # HTTP工具
│   │       │   └── JsonUtil.java               # JSON工具
│   │       │
│   │       ├── exception/                      # 异常定义
│   │       │   ├── BeanCreationException.java
│   │       │   ├── BeanNotFoundException.java
│   │       │   └── CircularDependencyException.java
│   │       │
│   │       └── util/                           # 工具类
│   │           ├── ClassScanner.java           # 类扫描器
│   │           └── StringUtils.java            # 字符串工具
│   │
│   └── test/                                   # 测试代码
│       ├── java/
│       │   ├── IoCTest.java                    # IoC容器测试
│       │   ├── AopTest.java                    # AOP测试
│       │   ├── MvcTest.java                    # Web MVC测试
│       │   └── JdbcTest.java                   # JDBC测试
│       └── resources/
│           └── test-data.sql
│
└── examples/                                    # 示例项目
    ├── user-demo/                               # 用户管理示例
    │   ├── src/
    │   │   └── main/java/com/github/microwind/userdemo/
    │   │       ├── UserDemoApplication.java    # 启动类
    │   │       ├── config/                     # 配置层
    │   │       │   └── DataSourceConfig.java   # 数据源配置
    │   │       ├── controller/                 # 控制器层
    │   │       │   ├── UserController.java     # 用户CRUD接口
    │   │       │   ├── StudentController.java  # 学生管理
    │   │       │   └── ClassController.java    # 班级管理
    │   │       ├── service/                    # 服务层
    │   │       │   ├── UserService.java
    │   │       │   ├── StudentService.java
    │   │       │   └── ClassService.java
    │   │       ├── dao/                        # 数据访问层
    │   │       │   ├── UserDao.java           # JdbcTemplate CRUD
    │   │       │   ├── StudentDao.java
    │   │       │   └── ClassDao.java
    │   │       ├── model/                      # 数据模型
    │   │       │   ├── User.java
    │   │       │   ├── Student.java
    │   │       │   └── ClassInfo.java
    │   │       └── utils/                      # 工具类
    │   │           ├── ApiResponse.java        # API响应封装
    │   │           ├── PageResult.java         # 分页结果
    │   │           └── JsonUtil.java
    │   ├── init-db.sql                         # 数据库初始化脚本
    │   └── test-user-api.sh                    # API测试脚本
    │
    └── web-demo/                                # Web应用示例
        ├── src/
        │   └── main/java/com/github/microwind/webdemo/
        │       ├── WebDemoApplication.java     # 启动类
        │       ├── controller/                 # 控制器层
        │       │   ├── HomeController.java     # 首页
        │       │   ├── ProductController.java  # 产品中心
        │       │   ├── NewsController.java     # 新闻资讯
        │       │   ├── AdminController.java    # 后台管理
        │       │   └── ArticleController.java  # 文章CRUD
        │       ├── service/                    # 服务层
        │       │   ├── ColumnService.java
        │       │   └── ArticleService.java
        │       ├── dao/                        # 数据访问层
        │       │   ├── ColumnDao.java
        │       │   └── ArticleDao.java
        │       ├── model/                      # 数据模型
        │       │   ├── Column.java             # 栏目
        │       │   └── Article.java            # 文章
        │       └── utils/                      # 工具类
        │           ├── ResponseUtils.java
        │           ├── RequestUtils.java
        │           └── ResponseBody.java
        └── README.md
```

**核心模块说明：**

| 模块 | 包名 | 核心类 | 行数 | 说明 |
|-----|------|--------|------|------|
| 注解 | annotation | 13个注解类 | ~200 | 定义框架的注解体系 |
| IoC容器 | core | SpringWindApplicationContext | ~400 | 容器核心，Bean管理 |
| AOP | aop | AspectProcessor | ~300 | 切面处理，代理创建 |
| Web MVC | web | DispatcherServlet | ~500 | 请求分发，参数绑定 |
| JDBC | jdbc | JdbcTemplate | ~300 | 数据库操作模板 |
| 异常 | exception | 3个异常类 | ~100 | 框架异常定义 |
| 工具 | util | ClassScanner等 | ~200 | 辅助工具类 |

整个框架核心代码不到2000行，但实现了Spring的核心功能。

---

## Springwind使用实践

光说不练假把式，下面我们通过两个实际示例来看看SpringWind怎么用。

### 示例1：User Demo - 完整的用户管理系统

这是一个基于数据库的用户管理系统，展示了SpringWind的IoC、DI、Web MVC和JDBC功能。

#### 项目结构

典型的三层架构：Controller → Service → Dao

```
user-demo/
├── controller/
│   └── UserController.java      # 处理HTTP请求
├── service/
│   └── UserService.java          # 业务逻辑
├── dao/
│   └── UserDao.java              # 数据访问（使用JdbcTemplate）
├── model/
│   └── User.java                 # 实体类
├── config/
│   └── DataSourceConfig.java    # 数据源配置
└── utils/
    ├── ApiResponse.java          # 统一响应封装
    └── PageResult.java           # 分页结果
```

#### 1. 定义实体类

```java
public class User {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Long createdTime;
    private Long updatedTime;

    // getter/setter省略
}
```

#### 2. 数据访问层 - 使用JdbcTemplate

```java
@Repository
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 查询所有用户
    public List<User> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM users ORDER BY id DESC",
            this::mapRow
        );
    }

    // 根据ID查询
    public User findById(Long id) {
        return jdbcTemplate.queryForObject(
            "SELECT * FROM users WHERE id = ?",
            this::mapRow,
            id
        );
    }

    // 分页查询
    public List<User> findByPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return jdbcTemplate.query(
            "SELECT * FROM users ORDER BY id DESC LIMIT ? OFFSET ?",
            this::mapRow,
            pageSize, offset
        );
    }

    // 创建用户
    public int insert(User user) {
        return jdbcTemplate.update(
            "INSERT INTO users (name, email, phone, created_time, updated_time) " +
            "VALUES (?, ?, ?, ?, ?)",
            user.getName(), user.getEmail(), user.getPhone(),
            System.currentTimeMillis(), System.currentTimeMillis()
        );
    }

    // 更新用户
    public int update(User user) {
        return jdbcTemplate.update(
            "UPDATE users SET name = ?, email = ?, phone = ?, " +
            "updated_time = ? WHERE id = ?",
            user.getName(), user.getEmail(), user.getPhone(),
            System.currentTimeMillis(), user.getId()
        );
    }

    // 删除用户
    public int delete(Long id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }

    // 结果集映射
    private User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setCreatedTime(rs.getLong("created_time"));
        user.setUpdatedTime(rs.getLong("updated_time"));
        return user;
    }
}
```

**看到了吗？** 原本50多行的JDBC代码，现在只需要2行。`JdbcTemplate`自动处理了连接管理、资源关闭、异常处理。

#### 3. 服务层 - 业务逻辑

```java
@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User getUserById(Long id) {
        return userDao.findById(id);
    }

    public List<User> getUsersByPage(int page, int pageSize) {
        return userDao.findByPage(page, pageSize);
    }

    public Long getUserCount() {
        return userDao.count();
    }

    public boolean createUser(User user) {
        return userDao.insert(user) > 0;
    }

    public boolean updateUser(User user) {
        return userDao.update(user) > 0;
    }

    public boolean deleteUser(Long id) {
        return userDao.delete(id) > 0;
    }
}
```

服务层很薄，主要是调用Dao层。如果有复杂的业务逻辑（比如用户注册需要发送邮件），就写在这里。

#### 4. 控制器层 - RESTful API

```java
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    // 获取用户列表（支持分页）
    @RequestMapping("/list")
    @ResponseBody
    public ViewResult list(HttpServletRequest request) {
        try {
            String pageStr = request.getParameter("page");
            String pageSizeStr = request.getParameter("pageSize");

            if (pageStr != null && pageSizeStr != null) {
                // 分页查询
                int page = Integer.parseInt(pageStr);
                int pageSize = Integer.parseInt(pageSizeStr);
                List<User> users = userService.getUsersByPage(page, pageSize);
                Long total = userService.getUserCount();

                // 使用PageResult和ApiResponse简化响应构建
                return new JsonResult(
                    ApiResponse.page(users, page, pageSize, total).toMap()
                );
            } else {
                // 查询所有
                List<User> users = userService.getAllUsers();
                return new JsonResult(
                    ApiResponse.success(users, "获取用户列表成功").toMap()
                );
            }
        } catch (Exception e) {
            return new JsonResult(
                ApiResponse.failure("获取用户列表失败: " + e.getMessage()).toMap()
            );
        }
    }

    // 获取单个用户
    @RequestMapping("/get")
    @ResponseBody
    public ViewResult getById(HttpServletRequest request) {
        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.isEmpty()) {
                return new JsonResult(
                    ApiResponse.badRequest("用户ID不能为空").toMap()
                );
            }

            Long id = Long.parseLong(idStr);
            User user = userService.getUserById(id);

            if (user == null) {
                return new JsonResult(
                    ApiResponse.notFound("用户不存在").toMap()
                );
            }

            return new JsonResult(
                ApiResponse.success(user, "获取用户详情成功").toMap()
            );
        } catch (Exception e) {
            return new JsonResult(
                ApiResponse.failure("获取用户失败: " + e.getMessage()).toMap()
            );
        }
    }

    // 创建用户
    @RequestMapping("/create")
    @ResponseBody
    public ViewResult create(@RequestBody User user) {
        try {
            boolean success = userService.createUser(user);
            if (success) {
                return new JsonResult(
                    ApiResponse.success(user, "创建用户成功").toMap()
                );
            } else {
                return new JsonResult(
                    ApiResponse.failure("创建用户失败").toMap()
                );
            }
        } catch (Exception e) {
            return new JsonResult(
                ApiResponse.failure("创建用户失败: " + e.getMessage()).toMap()
            );
        }
    }

    // 更新用户
    @RequestMapping("/update")
    @ResponseBody
    public ViewResult update(@RequestBody User user) {
        try {
            boolean success = userService.updateUser(user);
            if (success) {
                return new JsonResult(
                    ApiResponse.success(user, "更新用户成功").toMap()
                );
            } else {
                return new JsonResult(
                    ApiResponse.failure("更新用户失败").toMap()
                );
            }
        } catch (Exception e) {
            return new JsonResult(
                ApiResponse.failure("更新用户失败: " + e.getMessage()).toMap()
            );
        }
    }

    // 删除用户
    @RequestMapping("/delete")
    @ResponseBody
    public ViewResult delete(HttpServletRequest request) {
        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.isEmpty()) {
                return new JsonResult(
                    ApiResponse.badRequest("用户ID不能为空").toMap()
                );
            }

            Long id = Long.parseLong(idStr);
            boolean success = userService.deleteUser(id);

            if (success) {
                return new JsonResult(
                    ApiResponse.success(null, "删除用户成功").toMap()
                );
            } else {
                return new JsonResult(
                    ApiResponse.failure("删除用户失败").toMap()
                );
            }
        } catch (Exception e) {
            return new JsonResult(
                ApiResponse.failure("删除用户失败: " + e.getMessage()).toMap()
            );
        }
    }
}
```

#### 5. 统一响应封装 - ApiResponse

为了简化Controller代码，我们提供了`ApiResponse`工具类：

```java
public class ApiResponse {
    private Integer code;
    private String message;
    private Object data;

    // 成功响应
    public static ApiResponse success(Object data, String message) {
        return new ApiResponse(200, message, data);
    }

    // 分页响应
    public static ApiResponse page(List<?> list, int page, int pageSize, long total) {
        PageResult<?> pageResult = PageResult.of(list, page, pageSize, total);
        return success(pageResult, "获取列表成功");
    }

    // 失败响应
    public static ApiResponse failure(String message) {
        return new ApiResponse(500, message, null);
    }

    public static ApiResponse badRequest(String message) {
        return new ApiResponse(400, message, null);
    }

    public static ApiResponse notFound(String message) {
        return new ApiResponse(404, message, null);
    }
}
```

**对比一下：**

传统方式（手动构建Map）：
```java
Map<String, Object> result = new HashMap<>();
result.put("code", 200);
result.put("message", "获取用户成功");
result.put("data", user);
return new JsonResult(result);
```

使用ApiResponse（一行搞定）：
```java
return new JsonResult(ApiResponse.success(user, "获取用户成功").toMap());
```

#### 6. 分页结果 - PageResult

分页是Web应用的常见需求，SpringWind提供了`PageResult`：

```java
public class PageResult<T> {
    private List<T> list;          // 数据列表
    private Integer page;          // 当前页码
    private Integer pageSize;      // 每页大小
    private Long total;            // 总记录数
    private Integer totalPages;    // 总页数
    private Boolean hasPrevious;   // 是否有上一页
    private Boolean hasNext;       // 是否有下一页
    private Boolean first;         // 是否是第一页
    private Boolean last;          // 是否是最后一页

    // 创建分页结果
    public static <T> PageResult<T> of(List<T> list, int page, int pageSize, long total) {
        PageResult<T> result = new PageResult<>();
        result.setList(list);
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setTotal(total);
        result.setTotalPages((int) Math.ceil((double) total / pageSize));
        result.setHasPrevious(page > 1);
        result.setHasNext(page < result.getTotalPages());
        result.setFirst(page == 1);
        result.setLast(page == result.getTotalPages());
        return result;
    }
}
```

**分页响应示例：**

```json
{
  "code": 200,
  "message": "获取列表成功",
  "data": {
    "list": [
      {"id": 1, "name": "admin", "email": "admin@example.com"},
      {"id": 2, "name": "test", "email": "test@example.com"}
    ],
    "page": 2,
    "pageSize": 10,
    "total": 100,
    "totalPages": 10,
    "hasPrevious": true,
    "hasNext": true,
    "first": false,
    "last": false
  }
}
```

前端可以根据`hasPrevious`和`hasNext`来禁用/启用分页按钮，非常方便。

#### 7. 启动应用

```java
public class UserDemoApplication {
    public static void main(String[] args) {
        if (args.length > 0 && "--web".equals(args[0])) {
            // Web模式：启动嵌入式Tomcat
            startWebServer();
        } else {
            // 控制台模式：直接运行
            runConsoleDemo();
        }
    }

    private static void startWebServer() {
        // 创建SpringWind容器
        SpringWindApplicationContext context =
            new SpringWindApplicationContext(UserDemoApplication.class);

        // 启动Tomcat
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        // ... 配置Servlet
        tomcat.start();
    }
}
```

#### 8. API测试

```bash
# 获取用户列表（分页）
curl "http://localhost:8080/user/list?page=1&pageSize=10"

# 获取单个用户
curl "http://localhost:8080/user/get?id=1"

# 创建用户
curl -X POST http://localhost:8080/user/create \
  -H "Content-Type: application/json" \
  -d '{"name":"张三","email":"zhangsan@example.com","phone":"13800138000"}'

# 更新用户
curl -X POST http://localhost:8080/user/update \
  -H "Content-Type: application/json" \
  -d '{"id":1,"name":"李四","email":"lisi@example.com","phone":"13900139000"}'

# 删除用户
curl -X POST "http://localhost:8080/user/delete?id=1"
```

### 示例2：Web Demo - 企业网站内容管理系统

这是一个企业网站的CMS系统，展示了SpringWind的Web MVC功能。

#### 业务场景

模拟"春风公司"的企业网站，包含：
- 前台展示：首页、产品中心、新闻资讯
- 后台管理：栏目管理、文章发布
- RESTful API：完整的CRUD操作

#### 数据模型

**栏目（Column）：**
```java
public class Column {
    private Long id;
    private String name;           // 栏目名称
    private String description;    // 栏目描述
    private Integer sort;          // 排序
    private LocalDateTime createTime;
}
```

**文章（Article）：**
```java
public class Article {
    private Long id;
    private String title;          // 标题
    private String content;        // 内容
    private Long columnId;         // 所属栏目
    private String author;         // 作者
    private LocalDateTime publishTime;
}
```

#### RESTful API设计

| HTTP方法 | URL | 说明 |
|---------|-----|------|
| GET | /article/list | 获取所有文章 |
| GET | /article/detail/{id} | 获取文章详情 |
| GET | /article/column/{columnId} | 获取栏目下的文章 |
| POST | /article/create | 创建文章 |
| PUT | /article/update | 更新文章 |
| DELETE | /article/delete/{id} | 删除文章 |

#### 实现代码

**1. 数据访问层（内存存储）：**

```java
@Repository
public class ArticleDao {
    private List<Article> articles = new ArrayList<>();
    private AtomicLong idGenerator = new AtomicLong(1);

    // 初始化测试数据
    public ArticleDao() {
        Article article1 = new Article();
        article1.setId(idGenerator.getAndIncrement());
        article1.setTitle("春风公司2024年度总结");
        article1.setContent("回顾2024年，我们取得了丰硕的成果...");
        article1.setColumnId(2L);  // 新闻栏目
        article1.setAuthor("张三");
        article1.setPublishTime(LocalDateTime.now());
        articles.add(article1);
    }

    public List<Article> findAll() {
        return new ArrayList<>(articles);
    }

    public Article findById(Long id) {
        return articles.stream()
            .filter(a -> a.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public List<Article> findByColumnId(Long columnId) {
        return articles.stream()
            .filter(a -> a.getColumnId().equals(columnId))
            .collect(Collectors.toList());
    }

    public Article save(Article article) {
        article.setId(idGenerator.getAndIncrement());
        article.setPublishTime(LocalDateTime.now());
        articles.add(article);
        return article;
    }

    public Article update(Article article) {
        Article existing = findById(article.getId());
        if (existing != null) {
            existing.setTitle(article.getTitle());
            existing.setContent(article.getContent());
            existing.setColumnId(article.getColumnId());
            existing.setAuthor(article.getAuthor());
            return existing;
        }
        return null;
    }

    public boolean delete(Long id) {
        return articles.removeIf(a -> a.getId().equals(id));
    }
}
```

**2. 服务层：**

```java
@Service
public class ArticleService {
    @Autowired
    private ArticleDao articleDao;

    public List<Article> getAllArticles() {
        return articleDao.findAll();
    }

    public Article getArticleById(Long id) {
        return articleDao.findById(id);
    }

    public List<Article> getArticlesByColumnId(Long columnId) {
        return articleDao.findByColumnId(columnId);
    }

    public Article createArticle(Article article) {
        return articleDao.save(article);
    }

    public Article updateArticle(Article article) {
        return articleDao.update(article);
    }

    public boolean deleteArticle(Long id) {
        return articleDao.delete(id);
    }
}
```

**3. 控制器层：**

```java
@Controller
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    // 获取文章列表
    @RequestMapping("/list")
    public void list(HttpServletRequest request, HttpServletResponse response) {
        List<Article> articles = articleService.getAllArticles();
        ResponseUtils.sendJsonResponse(response, 200, "获取成功", articles, null);
    }

    // 获取文章详情
    @RequestMapping("/detail/{id}")
    public void detail(@PathVariable("id") String idStr,
                      HttpServletRequest request,
                      HttpServletResponse response) {
        try {
            Long id = Long.parseLong(idStr);
            Article article = articleService.getArticleById(id);

            if (article == null) {
                ResponseUtils.sendJsonError(response, 404, "文章不存在", null);
            } else {
                ResponseUtils.sendJsonResponse(response, 200, "获取成功", article, null);
            }
        } catch (NumberFormatException e) {
            ResponseUtils.sendJsonError(response, 400, "无效的文章ID", null);
        }
    }

    // 创建文章
    @RequestMapping("/create")
    public void create(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 解析请求体
            Article article = HttpRequestUtil.parseRequestBody(request, Article.class);
            Article created = articleService.createArticle(article);
            ResponseUtils.sendJsonResponse(response, 200, "创建成功", created, null);
        } catch (Exception e) {
            ResponseUtils.sendJsonError(response, 500, "创建失败: " + e.getMessage(), null);
        }
    }

    // 更新文章
    @RequestMapping("/update")
    public void update(HttpServletRequest request, HttpServletResponse response) {
        try {
            Article article = HttpRequestUtil.parseRequestBody(request, Article.class);
            Article updated = articleService.updateArticle(article);

            if (updated == null) {
                ResponseUtils.sendJsonError(response, 404, "文章不存在", null);
            } else {
                ResponseUtils.sendJsonResponse(response, 200, "更新成功", updated, null);
            }
        } catch (Exception e) {
            ResponseUtils.sendJsonError(response, 500, "更新失败: " + e.getMessage(), null);
        }
    }

    // 删除文章
    @RequestMapping("/delete/{id}")
    public void delete(@PathVariable("id") String idStr,
                      HttpServletRequest request,
                      HttpServletResponse response) {
        try {
            Long id = Long.parseLong(idStr);
            boolean success = articleService.deleteArticle(id);

            if (success) {
                ResponseUtils.sendJsonResponse(response, 200, "删除成功", null, null);
            } else {
                ResponseUtils.sendJsonError(response, 404, "文章不存在", null);
            }
        } catch (NumberFormatException e) {
            ResponseUtils.sendJsonError(response, 400, "无效的文章ID", null);
        }
    }
}
```

**4. 首页控制器：**

```java
@Controller
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private ArticleService articleService;

    @RequestMapping("/index")
    public void index(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> data = new HashMap<>();
        data.put("companyName", "春风公司");
        data.put("description", "春风公司成立于2010年，是一家专注于云计算和大数据领域的创新型企业。");

        // 获取最新文章
        List<Article> recentArticles = articleService.getAllArticles().stream()
            .sorted((a, b) -> b.getPublishTime().compareTo(a.getPublishTime()))
            .limit(5)
            .collect(Collectors.toList());
        data.put("recentArticles", recentArticles);

        ResponseUtils.sendJsonResponse(response, 200, "首页数据获取成功", data, null);
    }
}
```

### 两个示例的对比

| 特性 | User Demo | Web Demo |
|-----|-----------|----------|
| 数据存储 | MySQL数据库 | 内存存储 |
| 数据访问 | JdbcTemplate | List集合 |
| 业务复杂度 | CRUD + 分页 | CRUD + 内容管理 |
| 响应封装 | ApiResponse + PageResult | ResponseUtils |
| 适用场景 | 需要持久化的业务系统 | 快速原型、Demo展示 |

**学习建议：**

1. 先跑User Demo，理解JdbcTemplate的使用
2. 再跑Web Demo，理解内存存储的便捷性
3. 对比两个项目，理解不同的设计选择
4. 尝试自己写一个项目，综合运用所学知识

---

## Springwind改进点

SpringWind目前实现了Spring的核心功能，但还有很多可以改进的地方。

### 已实现的功能 ✅

- [x] IoC容器（组件扫描、Bean管理）
- [x] 依赖注入（字段注入、方法注入）
- [x] 循环依赖解决（三级缓存）
- [x] AOP（JDK动态代理、CGLIB代理）
- [x] Web MVC（请求映射、参数绑定、JSON响应）
- [x] JDBC模板（查询、更新、批量操作）
- [x] 分页支持（PageResult）
- [x] 统一响应封装（ApiResponse）

### 待改进的功能 ⏳

#### 1. 事务管理

**当前状态：** 没有事务支持，需要手动管理

**改进方案：**

```java
// 声明式事务
@Service
public class UserService {
    @Transactional
    public void registerUser(User user) {
        userDao.insert(user);
        emailService.sendWelcomeEmail(user);  // 如果发邮件失败，用户创建也回滚
    }
}

// 实现思路：利用AOP拦截@Transactional方法
@Aspect
public class TransactionAspect {
    @Around("@annotation(Transactional)")
    public Object aroundTransaction(Method method, Object[] args) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);  // 开启事务

            Object result = method.invoke(args);  // 执行业务方法

            conn.commit();  // 提交事务
            return result;
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();  // 回滚事务
            }
            throw e;
        }
    }
}
```

#### 2. 构造器注入

**当前状态：** 只支持字段注入和方法注入

**改进方案：**

```java
// 支持构造器注入
@Service
public class UserService {
    private final UserDao userDao;  // final保证不可变

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }
}

// 实现思路：
// 1. 扫描构造器上的@Autowired
// 2. 解析构造器参数类型
// 3. 从容器中查找对应的Bean
// 4. 调用构造器创建对象
```

#### 3. Bean作用域

**当前状态：** 只支持单例（Singleton）

**改进方案：**

```java
@Service
@Scope("prototype")  // 每次获取都创建新实例
public class PrototypeService {
    // ...
}

@Service
@Scope("request")  // 每个HTTP请求一个实例
public class RequestScopedService {
    // ...
}
```

#### 4. 更强大的AOP

**当前改进：**

1. **支持切点表达式**：
```java
@Before("execution(* com.example.service.*.*(..))") // 支持AspectJ表达式
public void logBefore(JoinPoint joinPoint) {
    // ...
}
```

2. **支持切点组合**：
```java
@Pointcut("execution(* com.example.service.*.*(..))")
public void serviceMethods() {}

@Pointcut("@annotation(Transactional)")
public void transactionalMethods() {}

@Before("serviceMethods() && transactionalMethods()")
public void beforeServiceTransaction() {
    // 同时满足两个切点
}
```

3. **支持@Around获取方法参数**：
```java
@Around("com.example.service.*.*")
public Object aroundAdvice(ProceedingJoinPoint pjp) {
    Object[] args = pjp.getArgs();  // 获取方法参数
    System.out.println("参数: " + Arrays.toString(args));
    return pjp.proceed();  // 继续执行
}
```

#### 5. 更轻量级化

**当前体积：** 核心代码约2000行，依赖较少

**进一步优化：**

1. **按需加载模块**：
```xml
<!-- 只引入需要的模块 -->
<dependency>
    <groupId>com.github.microwind</groupId>
    <artifactId>springwind-core</artifactId>  <!-- 仅IoC -->
</dependency>

<dependency>
    <groupId>com.github.microwind</groupId>
    <artifactId>springwind-web</artifactId>  <!-- 仅Web MVC -->
</dependency>
```

2. **移除不必要的依赖**：
```xml
<!-- 当前依赖：Servlet、CGLIB、SLF4J、Jackson -->
<!-- 优化后：可以选择性依赖 -->
<dependency>
    <groupId>cglib</groupId>
    <artifactId>cglib</artifactId>
    <optional>true</optional>  <!-- 如果不用AOP，可以不引入 -->
</dependency>
```

3. **GraalVM Native Image支持**：
编译成原生可执行文件，启动速度更快，内存占用更小。

#### 6. 配置文件支持

**当前状态：** 主要依赖注解

**改进方案：**

```yaml
# application.yml
springwind:
  datasource:
    url: jdbc:mysql://localhost:3306/test
    username: root
    password: 123456
  web:
    port: 8080
    context-path: /api
```

```java
// 读取配置
@Configuration
public class DataSourceConfig {
    @Value("${springwind.datasource.url}")
    private String url;

    @Bean
    public DataSource dataSource() {
        return new HikariDataSource(url, username, password);
    }
}
```

#### 7. 更好的异常处理

**当前状态：** 基本的异常抛出

**改进方案：**

```java
// 全局异常处理器
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BeanNotFoundException.class)
    public ViewResult handleBeanNotFound(BeanNotFoundException e) {
        return new JsonResult(ApiResponse.notFound(e.getMessage()).toMap());
    }

    @ExceptionHandler(Exception.class)
    public ViewResult handleGenericException(Exception e) {
        return new JsonResult(ApiResponse.failure("系统错误: " + e.getMessage()).toMap());
    }
}
```

#### 8. 性能监控和诊断

**改进方案：**

```java
// Bean创建性能监控
@Aspect
public class BeanCreationMonitor {
    @Around("@annotation(Component)")
    public Object monitorBeanCreation(ProceedingJoinPoint pjp) {
        long start = System.currentTimeMillis();
        Object bean = pjp.proceed();
        long end = System.currentTimeMillis();
        System.out.println("创建Bean " + bean.getClass().getSimpleName() +
                          " 耗时: " + (end - start) + "ms");
        return bean;
    }
}

// 内存占用监控
public class MemoryMonitor {
    public void printMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("内存占用: " + (usedMemory / 1024 / 1024) + " MB");
    }
}
```

### 贡献指南

欢迎大家为SpringWind贡献代码！

**贡献方向：**

1. **实现上述待改进功能**
2. **完善单元测试**（目前测试覆盖率还不够）
3. **优化性能**（减少反射调用、提升并发性能）
4. **改进文档**（添加更多示例、API文档）
5. **修复Bug**（提交Issue或PR）

**参与方式：**

1. Fork本项目
2. 创建功能分支：`git checkout -b feature/transaction-support`
3. 提交代码：`git commit -m "添加事务支持"`
4. 推送分支：`git push origin feature/transaction-support`
5. 创建Pull Request

---

## 总结

通过从零实现SpringWind框架，我们彻底搞懂了Spring的核心原理：

1. **IoC容器**：通过反射扫描组件、创建Bean、管理生命周期
2. **依赖注入**：通过反射设置字段值，实现自动装配
3. **循环依赖**：通过三级缓存，允许Bean提前暴露引用
4. **AOP**：通过动态代理，实现横切关注点的分离
5. **Web MVC**：通过前端控制器模式，统一处理HTTP请求
6. **JDBC模板**：通过模板方法模式，封装样板代码

SpringWind不是为了替代Spring，而是为了**学习Spring**。它用最简洁的代码实现了Spring的核心功能，让每个想深入理解框架原理的同学都能看懂。

**学习建议：**

1. **先跑起来**：运行两个示例项目，看看效果
2. **打断点调试**：看Bean是怎么创建的，依赖是怎么注入的
3. **阅读源码**：SpringWind代码量不大，完全可以通读
4. **对比Spring**：理解SpringWind的简化设计和Spring的完整实现
5. **动手改代码**：实现待改进功能，加深理解

**最后的话：**

框架不是黑魔法，它只是把常见的代码模式封装起来。当你理解了原理，就能从"会用框架"进阶到"精通框架"，甚至"设计框架"。

希望SpringWind能帮你打开Spring的大门，祝你学习愉快！🚀

---

## 附录

### 环境要求

- Java 17+
- Maven 3.6+
- MySQL 8.0+（如果使用User Demo）

### 快速开始

```bash
# 1. 克隆项目
git clone https://github.com/microwind/design-patterns.git
cd practice-projects/springwind

# 2. 编译安装
mvn clean install -DskipTests=true

# 3. 运行User Demo
cd examples/user-demo
mvn exec:java -Dexec.args="--web"

# 4. 运行Web Demo
cd examples/web-demo
mvn exec:java -Dexec.args="--web"
```

### 相关资源

- **项目主页**：https://github.com/microwind/design-patterns/tree/main/practice-projects/springwind
- **Spring官方文档**：https://docs.spring.io/spring-framework/docs/current/reference/html/
- **问题反馈**：https://github.com/microwind/design-patterns/issues

### 参考资料

1. Spring Framework官方文档
2. 《Spring源码深度解析》书及相关评论
3. 《设计模式：可复用面向对象软件的基础》
4. 《Effective Java》

---

**愿你在学习SpringWind的过程中，深入理解现代Java框架的设计精髓！** 🎉
