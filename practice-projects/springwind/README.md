# SpringWind Framework

一个轻量级的Spring-like框架实现，模拟Spring MVC核心原理，包括IoC容器、依赖注入、AOP、JDBC模板等。下面我将用文字和图表来描述。

## 整体架构图
```
┌─────────────────────────────────────────────────────────────┐
│                    SpringWind Framework                     │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Web MVC   │  │     AOP     │  │   JDBC Template     │  │
│  │   Layer     │  │   Layer     │  │      Layer          │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│                 IoC Container Core                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ Bean Def    │  │ Dependency  │  │ Lifecycle           │  │
│  │ Management  │  │ Injection   │  │ Management          │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 功能介绍

- ✅ IoC容器（控制反转 Inversion of Control)

  IoC容器是框架的核心，负责管理应用中的对象（称为Bean）的生命周期和配置。在SpringWind中，IoC容器通过SpringWindApplicationContext类实现。它根据注解（如@Component, @Service, @Controller等）扫描并注册Bean定义，然后实例化Bean，并完成依赖注入。

- ✅ DI依赖注入 (Dependency Injection)

  依赖注入是IoC的一种实现方式。SpringWind通过@Autowired注解实现依赖注入。当容器创建Bean时，它会检查Bean中的字段是否带有@Autowired注解，然后自动将所需的依赖注入到该字段。
```java
// 依赖注入的核心逻辑
private void doDependencyInjection(Object bean) {
    Class<?> clazz = bean.getClass();
    for (Field field : clazz.getDeclaredFields()) {
        if (field.isAnnotationPresent(Autowired.class)) {
            // 1. 获取依赖类型
            Class<?> fieldType = field.getType();
            
            // 2. 从容器获取依赖Bean
            Object dependency = getBean(fieldType);
            
            // 3. 注入依赖
            field.setAccessible(true);
            field.set(bean, dependency);
        }
    }
}
```

- ✅ 注解驱动开发

  SpringWind支持使用注解来配置Bean和依赖注入。开发人员可以在类上添加@Component, @Service, @Controller等注解，将其标识为Bean。在字段上添加@Autowired注解，实现自动注入依赖。
  1. **组件注册注解**：用于标记类为SpringWind容器管理的Bean
     - `@Component`：通用组件注解
     - `@Service`：服务层组件注解（继承自@Component）
     - `@Controller`：控制器组件注解（继承自@Component）
     - `@Repository`：数据访问层组件注解（继承自@Component）
     
  2. **依赖注入注解**：
     - `@Autowired`：自动注入依赖的Bean，可以用于字段和方法
     
  3. **Web MVC注解**：
     - `@RequestMapping`：映射HTTP请求到控制器方法
     
  4. **AOP相关注解**：
     - `@Aspect`：标记一个类为切面（继承自@Component）
     - `@Before`：前置通知，在目标方法执行前执行
     - `@After`：后置通知，在目标方法执行后执行
     - `@Around`：环绕通知，围绕目标方法执行
     
  5. **事务管理注解**：
     - `@Transactional`：标记方法需要事务支持

- ✅ Bean生命周期管理

  1. 注册：通过扫描类路径下的类，识别带有组件注解的类，并将其注册为Bean定义。
  2. 实例化：容器根据Bean定义创建实例。对于单例Bean，容器在启动时就会创建；对于原型Bean，每次请求时创建。
  3. 依赖注入：容器注入Bean所依赖的其他Bean。
  4. 初始化：如果Bean有初始化方法（使用@PostConstruct注解），则调用该方法。
  5. 使用：Bean可以被应用程序使用。
  6. 销毁：如果Bean有销毁方法（使用@PreDestroy注解），在容器关闭时调用。

- ✅ AOP面向切面编程 (Aspect-Oriented Programming)

  AOP允许将横切关注点（如日志、事务等）模块化。SpringWind通过动态代理实现AOP。它定义切面（使用@Aspect注解），并在目标方法执行前后执行通知（使用@Before, @After, @Around注解）。

- ✅ MVC Web框架

  SpringWind提供了一个简单的MVC实现。它使用DispatcherServlet作为前端控制器，根据请求URL映射到对应的控制器方法，并调用该方法处理请求。

- ✅ JDBC模板

  JDBC模板简化了数据库操作，封装了JDBC的样板代码，如连接管理、异常处理等。它提供了JdbcTemplate类，支持执行SQL查询和更新。
```text
JdbcTemplate
├── update(String sql, Object... args)
├── query(String sql, RowMapper<T> mapper, Object... args)
└── queryForObject(String sql, RowMapper<T> mapper, Object... args)

执行流程:
1. 获取连接
2. 创建PreparedStatement
3. 设置参数
4. 执行SQL
5. 处理结果集
6. 关闭资源
```

## SpringWind框架组件图
```
+-------------------+      +-----------------------+
|                   |      |                       |
|  Client Code      |      |   SpringWind Framework|
|                   |      |                       |
+-------------------+      +-----------------------+
         |                            |
         | 使用Bean                    | 管理Bean生命周期
         |                            |
         v                            |
+-------------------+                 |
|   Bean容器         |                 |
|   (IoC Container) |                 |
|                   |                 |
|  - Bean定义映射    |                 |
|  - 单例池          |                 |
|  - 依赖注入        |                 |
|  - AOP代理        |                  |
+-------------------+                 |
         |                            |
         | 依赖                        |
         v                            |
+-------------------+                 |
|   Bean            |                 |
|                   |                 |
|  - 业务逻辑        |                 |
|  - 依赖其他Bean    |                 |
+-------------------+                 |
         |                            |
         | 调用                        |
         v                            |
+-------------------+                 |
|   AOP切面          |                 |
|                   |                 |
|  - 前置通知        |                 |
|  - 后置通知        |                 |
|  - 环绕通知        |                 |
+-------------------+                 |
         |                            |
         | 数据库操作                   |
         v                            |
+-------------------+                 |
|   JDBC模板         |                 |
|                   |                 |
|  - 数据源          |                 |
|  - 查询/更新       |                 |
+-------------------+                 |
         |                            |
         | SQL执行                     |
         v                            |
+-------------------+                 |
|   数据库           |                 |
+-------------------+                 |
```

## Bean的创建和依赖注入
```
Client          SpringWindContainer      BeanA         BeanB
  |                   |                   |             |
  |   getBean(BeanA)  |                   |             |
  | ----------------> |                   |             |
  |                   |   create BeanA    |             |
  |                   | ----------------->|             |
  |                   |   inject BeanB    |             |
  |                   | ------------------------------->|
  |                   |   set BeanB to BeanA            |
  |                   | ------------------------------->|
  |                   |   call init method|             |
  |                   | ----------------->|             |
  |                   |   return BeanA    |             |
  | <---------------- |                   |             |
```

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>microwind.github.com</groupId>
    <artifactId>springwind</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## 使用说明

1. **编译项目**：
```bash
$ mvn clean compile -DskipTests=true
```

2. **运行测试**：
```shell
# $ mvn test
$ mvn clean test -DskipTests=false
```

3. **打包发布**:
```shell
mvn clean package
```

4. **安装到本地仓库**：
```shell
mvn clean install
```


## 项目结构
```
src/main/java/com/github/microwind/springwind/
├── annotation/     # 注解定义
├── core/          # IoC核心容器
├── aop/           # AOP实现
├── jdbc/          # JDBC模板
├── web/           # Web MVC
└── util/          # 工具类
```

## 目录详细
```
springwind/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── microwind/
│   │   │               └── springwind/
│   │   │                   ├── SpringWindApplication.java
│   │   │                   ├── annotation/
│   │   │                   │   ├── Aspect.java
│   │   │                   │   ├── After.java
│   │   │                   │   ├── Around.java
│   │   │                   │   ├── Autowired.java
│   │   │                   │   ├── Before.java
│   │   │                   │   ├── Component.java
│   │   │                   │   ├── Controller.java
│   │   │                   │   ├── Repository.java
│   │   │                   │   ├── RequestMapping.java
│   │   │                   │   ├── Service.java
│   │   │                   │   └── Transactional.java
│   │   │                   ├── core/
│   │   │                   │   ├── SpringWindApplicationContext.java
│   │   │                   │   ├── BeanDefinition.java
│   │   │                   │   ├── BeanPostProcessor.java
│   │   │                   │   └── PropertyValue.java
│   │   │                   ├── example/  # 测试例子
│   │   │                   │   ├── aop/
│   │   │                   │   ├── controller/
│   │   │                   │   ├── dao/
│   │   │                   │   ├── model/
│   │   │                   │   └── service/
│   │   │                   ├── aop/
│   │   │                   │   ├── AspectProcessor.java
│   │   │                   │   ├── AspectInfo.java
│   │   │                   │   ├── AspectType.java
│   │   │                   │   └── AopInvocationHandler.java
│   │   │                   ├── jdbc/
│   │   │                   │   ├── JdbcTemplate.java
│   │   │                   │   └── RowMapper.java
│   │   │                   ├── web/
│   │   │                   │   ├── DispatcherServlet.java
│   │   │                   │   └── HandlerMapping.java
│   │   │                   └── util/
│   │   │                       ├── ClassScanner.java
│   │   │                       └── StringUtils.java
│   │   └── resources/
│   │       └── META-INF/
│   │           └── MANIFEST.MF
│   └── test/
│       ├── java/
│       │   └── microwind/
│       │       └── github/
│       │           └── com/
│       │               └── springwind/
│       │                   ├── IoCTest.java
│       │                   ├── AopTest.java
│       │                   ├── MvcTest.java
│       │                   └── JdbcTest.java
│       └── resources/
│           ├── application.properties
│           └── test-data.sql
└── examples/
|   ├── user-demo/
|   │   ├── src/
|   │   └── pom.xml
|   |── web-demo/
|   |   ├── src/
|   |   └── pom.xml
```

## 基本用法
```java
@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @RequestMapping("/user")
    public String getUser() {
        return userService.getUserInfo();
    }
}

@Service
public class UserService {
    // 业务逻辑
}

// 启动应用
SpringWindApplication.run(AppConfig.class);
```

## 在SpringWind中使用的设计模式

框架的核心原理体现了Spring中控制反转(IoC)、依赖注入(DI)、面向切面编程(AOP)等现代Java框架的核心思想，通过注解驱动和约定优于配置的方式，简化企业级应用的开发。

1. 工厂模式 - SpringWindApplicationContext 作为Bean工厂
2. 单例模式 - 单例Bean的管理
3. 模板方法模式 - JdbcTemplate 的固定流程
4. 代理模式 - AOP的动态代理
5. 观察者模式 - Bean生命周期事件
6. 策略模式 - RowMapper 结果映射策略