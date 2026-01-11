# Springboot4DDD：基于Spring Boot 4和Java 21的领域驱动设计实践脚手架

## 一、项目概述

在现代软件开发中，如何构建一个既符合业务领域特性，又易于维护和扩展的企业级应用，是每个开发者必须面对的挑战。领域驱动设计（Domain-Driven Design，简称DDD）作为一种软件设计方法论，为我们提供了一套系统化的解决方案。

**Springboot4DDD** 是一个完整的、生产就绪的DDD脚手架项目，基于最新的Spring Boot 4.0.1和Java 21构建。它不仅实现了严格的DDD四层架构，还集成了多数据源管理、API安全认证、统一异常处理等企业级特性，可以帮助开发者快速搭建高质量的Java Web应用。

### 1.1 核心特性

本脚手架具有以下核心特性：

- **严格的DDD分层架构**：领域层、应用层、基础设施层、接口层四层清晰分离
- **多数据源架构**：支持MySQL + PostgreSQL双数据源，演示跨数据库查询
- **双持久化策略**：同时展示JdbcTemplate和Spring Data JDBC两种数据访问方式
- **API签名验证**：基于SHA-256的接口安全认证机制
- **统一响应格式**：标准化的API响应结构
- **全局异常处理**：优雅的异常捕获和错误响应
- **请求参数校验**：基于Jakarta Validation的数据验证
- **跨数据库查询**：应用层实现多数据源数据整合
- **事务管理**：多数据源独立事务管理
- **生产就绪**：完整的日志、配置、监控支持

### 1.2 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Spring Boot** | 4.0.1 | 最新的Spring Boot版本 |
| **Java** | 21 | LTS版本，支持现代Java特性 |
| **MySQL** | 8.0+ | 用户数据存储 |
| **PostgreSQL** | 14+ | 订单数据存储 |
| **Redis** | 6.0+ | 缓存支持（可选） |
| **RocketMQ** | 4.9+ | 消息队列（可选） |
| **Spring Data JDBC** | 3.x | 订单数据持久化 |
| **JdbcTemplate** | - | 用户数据持久化 |
| **Lombok** | - | 简化代码 |
| **Maven** | 3.8+ | 构建工具 |

### 1.3 适用场景

本脚手架适用于以下场景：

- **学习DDD**：完整展示DDD四层架构的实现方式
- **企业级应用开发**：具备生产环境所需的各种特性
- **多数据源项目**：需要同时访问多个数据库的场景
- **API安全认证**：需要对外提供安全的REST API
- **微服务架构**：可作为单个微服务的基础框架
- **快速原型开发**：基于本脚手架快速搭建新项目

---

## 二、DDD架构详解

### 2.1 什么是DDD？

领域驱动设计（Domain-Driven Design）是一种软件开发方法论，强调将业务领域的概念和逻辑作为软件设计的核心。DDD通过分层架构将业务逻辑与技术实现分离，使代码更加清晰、可维护。

### 2.2 四层架构

Springboot4DDD严格遵循DDD的四层架构模式：

```
┌─────────────────────────────────────────────────┐
│           接口层 (Interfaces Layer)              │
│  - REST Controllers                              │
│  - Request/Response Objects (VO)                 │
│  - API路由和请求转换                              │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│          应用层 (Application Layer)              │
│  - Application Services                          │
│  - DTO (Data Transfer Objects)                   │
│  - 用例编排、跨聚合协调                           │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│            领域层 (Domain Layer)                 │
│  - Entities (实体)                               │
│  - Value Objects (值对象)                        │
│  - Aggregates (聚合根)                           │
│  - Domain Services (领域服务)                    │
│  - Repository Interfaces (仓储接口)              │
│  - 核心业务逻辑                                   │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│        基础设施层 (Infrastructure Layer)          │
│  - Repository Implementations                    │
│  - Database Access                               │
│  - External Services                             │
│  - Configuration                                 │
│  - Utilities                                     │
└─────────────────────────────────────────────────┘
```

### 2.3 各层职责

#### 领域层 (Domain Layer)

**位置**：`src/main/java/com/github/microwind/springboot4ddd/domain/`

**职责**：
- 包含核心业务逻辑和领域模型
- 定义实体（Entity）、值对象（Value Object）、聚合根（Aggregate Root）
- 实现领域服务（Domain Service）
- 定义仓储接口（Repository Interface）
- **独立于框架和技术实现**

**关键原则**：
- 业务规则必须在领域层实现
- 不依赖其他层，保持纯净
- 使用领域语言命名

**示例**：
```java
// Order.java - 订单聚合根
@Table("orders")
public class Order {
    @Id
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private OrderStatus status;

    // 业务方法：取消订单
    public void cancel() {
        if (this.status != OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL);
        }
        this.status = OrderStatus.CANCELLED;
    }

    // 业务方法：支付订单
    public void pay() {
        if (this.status != OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_PAY);
        }
        this.status = OrderStatus.PAID;
    }
}
```

#### 应用层 (Application Layer)

**位置**：`src/main/java/com/github/microwind/springboot4ddd/application/`

**职责**：
- 编排领域对象完成用户用例
- 管理事务边界
- 实现DTO和领域对象的转换
- 协调多个聚合根的操作
- **不包含业务逻辑**，只负责编排

**关键原则**：
- 薄薄的一层，不实现业务规则
- 调用领域层的业务方法
- 管理事务
- 处理DTO转换

**示例**：
```java
// OrderService.java - 订单应用服务
@Service
@Transactional(transactionManager = "orderTransactionManager")
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    // 编排：创建订单
    public OrderDTO createOrder(CreateOrderRequest request) {
        // 调用领域对象的工厂方法
        Order order = Order.create(request.getUserId(), request.getTotalAmount());

        // 保存
        Order savedOrder = orderRepository.save(order);

        // 转换为DTO
        return OrderMapper.toDTO(savedOrder);
    }

    // 编排：跨数据库查询（订单 + 用户信息）
    public List<OrderDTO> getUserOrderList(Long userId) {
        // 从PostgreSQL查询订单
        List<Order> orders = orderRepository.findByUserId(userId);

        // 从MySQL查询用户
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        // 在应用层组合数据
        return orders.stream()
            .map(order -> OrderMapper.toDTO(order, user))
            .collect(Collectors.toList());
    }
}
```

#### 基础设施层 (Infrastructure Layer)

**位置**：`src/main/java/com/github/microwind/springboot4ddd/infrastructure/`

**职责**：
- 实现领域层定义的仓储接口
- 提供数据持久化能力
- 提供配置管理
- 提供工具类和通用组件
- 实现外部服务调用

**关键原则**：
- 实现技术细节
- 为其他层提供技术支撑
- 可以依赖第三方框架

**示例**：
```java
// OrderRepositoryImpl.java - 订单仓储实现
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJdbcRepository jdbcRepository;

    @Override
    public Order save(Order order) {
        return jdbcRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jdbcRepository.findById(id);
    }
}

// DataSourceConfig.java - 多数据源配置
@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.user")
    public DataSource userDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.order")
    public DataSource orderDataSource() {
        return DataSourceBuilder.create().build();
    }
}
```

#### 接口层 (Interfaces Layer)

**位置**：`src/main/java/com/github/microwind/springboot4ddd/interfaces/`

**职责**：
- 对外暴露REST API
- 处理HTTP请求和响应
- 参数校验
- 请求对象（Request）和响应对象（Response）定义
- 调用应用层服务

**关键原则**：
- 处理外部交互
- 不包含业务逻辑
- 依赖应用层

**示例**：
```java
// OrderController.java - 订单控制器
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    @RequireSign(withParams = WithParams.TRUE)
    public ApiResponse<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        OrderDTO orderDTO = orderService.createOrder(request);
        return ApiResponse.success(OrderResponse.from(orderDTO));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderListResponse>> getUserOrders(
            @PathVariable Long userId) {
        List<OrderDTO> orders = orderService.getUserOrderList(userId);
        return ApiResponse.success(OrderListResponse.fromList(orders));
    }
}
```

### 2.4 依赖方向

DDD架构的关键原则是**依赖方向自上而下，领域层不依赖任何层**：

```
接口层 (Interfaces)
    ↓ 依赖
应用层 (Application)
    ↓ 依赖
领域层 (Domain) ←─────────── 基础设施层实现领域层接口
    ↑ 接口定义
基础设施层 (Infrastructure)
```

**关键点**：
- 领域层定义仓储接口，基础设施层实现
- 应用层通过接口调用仓储，不直接依赖实现
- 接口层只依赖应用层，不越过应用层直接访问领域层

---

## 三、快速开始：10分钟搭建你的第一个DDD项目

### 3.1 环境准备

在开始之前，请确保你的开发环境已安装：

| 软件 | 最低版本 | 推荐版本 | 检查命令 |
|------|----------|----------|----------|
| **JDK** | 21 | 21 | `java -version` |
| **Maven** | 3.8+ | 3.9+ | `mvn -version` |
| **MySQL** | 8.0+ | 8.0+ | `mysql --version` |
| **PostgreSQL** | 14+ | 15+ | `psql --version` |
| **Git** | 2.0+ | 最新 | `git --version` |

**可选组件**（用于完整功能）：
- Redis 6.0+：用于缓存
- RocketMQ 4.9+：用于消息队列

### 3.2 克隆项目

```bash
# 克隆仓库
git clone https://github.com/microwind/design-patterns.git

# 进入项目目录
cd design-patterns/practice-projects/springboot4ddd

# 查看项目结构
tree -L 3 src/
```

### 3.3 数据库初始化

#### 步骤1：创建MySQL数据库（用户数据）

```bash
# 登录MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE frog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 创建用户并授权
CREATE USER 'frog_admin'@'localhost' IDENTIFIED BY 'frog_password';
GRANT ALL PRIVILEGES ON frog.* TO 'frog_admin'@'localhost';
FLUSH PRIVILEGES;

# 使用数据库
USE frog;

# 执行初始化脚本
source src/main/resources/db/mysql/init_users.sql;

# 验证数据
SELECT * FROM users;
```

**输出示例**：
```
+----+----------+-------------------+-------------+----------+--------+---------------------+---------------------+
| id | username | email             | phone       | nickname | status | created_time        | updated_time        |
+----+----------+-------------------+-------------+----------+--------+---------------------+---------------------+
|  1 | user1    | user1@example.com | 13800000001 | 用户1    |      1 | 2024-01-01 10:00:00 | 2024-01-01 10:00:00 |
|  2 | user2    | user2@example.com | 13800000002 | 用户2    |      1 | 2024-01-01 11:00:00 | 2024-01-01 11:00:00 |
|  3 | user3    | user3@example.com | 13800000003 | 用户3    |      1 | 2024-01-01 12:00:00 | 2024-01-01 12:00:00 |
+----+----------+-------------------+-------------+----------+--------+---------------------+---------------------+
```

#### 步骤2：创建PostgreSQL数据库（订单数据）

```bash
# 登录PostgreSQL
psql -U postgres

# 创建数据库
CREATE DATABASE seed ENCODING 'UTF8';

# 切换数据库
\c seed

# 执行初始化脚本
\i src/main/resources/db/postgresql/init_orders.sql

# 验证数据
SELECT * FROM orders;
```

**输出示例**：
```
 id |   order_no    | user_id | total_amount | status  |      created_at
----+---------------+---------+--------------+---------+----------------------
  1 | ORD1000000001 |       1 |       100.00 | PENDING | 2024-01-01 10:00:00
  2 | ORD1000000002 |       1 |       200.00 | PAID    | 2024-01-01 11:00:00
  3 | ORD1000000003 |       2 |       150.00 | COMPLETED | 2024-01-01 12:00:00
```

### 3.4 配置应用

编辑配置文件 `src/main/resources/application-dev.yaml`：

```yaml
spring:
  # MySQL数据源（用户数据）
  datasource:
    user:
      jdbc-url: jdbc:mysql://localhost:3306/frog?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
      username: frog_admin
      password: frog_password  # 修改为你的密码
      driver-class-name: com.mysql.cj.jdbc.Driver

    # PostgreSQL数据源（订单数据）
    order:
      jdbc-url: jdbc:postgresql://localhost:5432/seed
      username: postgres
      password: postgres_password  # 修改为你的密码
      driver-class-name: org.postgresql.Driver

  # Redis配置（可选）
  data:
    redis:
      host: localhost
      port: 6379
      # password: your_redis_password  # 如果有密码

# RocketMQ配置（可选）
rocketmq:
  name-server: localhost:9876
```

**重要提示**：
- MySQL使用 `jdbc-url` 而不是 `url`
- 两个数据源的字段名有细微差异（MySQL: `created_time`, PostgreSQL: `created_at`）
- 生产环境务必修改为强密码

### 3.5 编译项目

```bash
# 清理并编译
./mvnw clean compile

# 或使用Maven命令
mvn clean compile
```

**输出示例**：
```
[INFO] BUILD SUCCESS
[INFO] Total time:  8.521 s
[INFO] Finished at: 2024-01-09T10:00:00+08:00
```

### 3.6 运行测试

```bash
# 运行所有测试
./mvnw test

# 或运行单个测试
./mvnw test -Dtest=ApplicationTests
```

### 3.7 启动应用

```bash
# 方式1：使用Maven插件
./mvnw spring-boot:run

# 方式2：打包后运行
./mvnw clean package
java -jar target/springboot4ddd-0.0.1-SNAPSHOT.jar

# 方式3：在IDE中运行
# 直接运行 Application.java 的 main 方法
```

**启动成功标志**：
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v4.0.1)

[main] c.g.m.s.Application  : Started Application in 3.245 seconds
```

### 3.8 验证安装

#### 健康检查

```bash
curl http://localhost:8080/api/health
```

**预期响应**：
```json
{
  "code": 200,
  "message": "Service is healthy",
  "data": {
    "status": "UP",
    "timestamp": "2024-01-09T10:00:00"
  },
  "timestamp": "2024-01-09T10:00:00"
}
```

#### 查询用户列表

```bash
curl http://localhost:8080/api/users
```

**预期响应**：
```json
{
  "code": 200,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "username": "user1",
      "email": "user1@example.com",
      "phone": "13800000001",
      "nickname": "用户1",
      "status": 1,
      "createdTime": "2024-01-01T10:00:00",
      "updatedTime": "2024-01-01T10:00:00"
    }
  ],
  "timestamp": "2024-01-09T10:00:00"
}
```

#### 查询订单列表

```bash
curl http://localhost:8080/api/orders/list
```

**预期响应**：
```json
{
  "code": 200,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "orderNo": "ORD1000000001",
      "userId": 1,
      "totalAmount": 100.00,
      "status": "PENDING",
      "statusDesc": "待支付",
      "username": "user1",
      "email": "user1@example.com",
      "phone": "13800000001",
      "createdAt": "2024-01-01T10:00:00"
    }
  ],
  "timestamp": "2024-01-09T10:00:00"
}
```

**注意**：这个接口展示了跨数据库查询能力，订单数据来自PostgreSQL，用户数据来自MySQL。

恭喜！你已经成功搭建了第一个DDD项目。接下来我们将深入探讨各个核心特性。

---

## 四、多数据源架构：跨数据库的艺术

### 4.1 为什么需要多数据源？

在实际项目中，我们经常遇到需要访问多个数据库的场景：

| 场景 | 说明 | 示例 |
|------|------|------|
| **业务隔离** | 不同业务使用不同数据库 | 用户系统用MySQL，订单系统用PostgreSQL |
| **读写分离** | 主库写入，从库读取 | 主库MySQL，从库MySQL Slave |
| **数据迁移** | 旧系统和新系统并存 | 旧Oracle数据库 + 新PostgreSQL数据库 |
| **性能优化** | 不同数据特性使用不同数据库 | 关系数据用PostgreSQL，时序数据用InfluxDB |
| **微服务拆分** | 不同服务访问不同数据库 | 订单服务访问订单库，库存服务访问库存库 |

Springboot4DDD采用**MySQL（用户数据） + PostgreSQL（订单数据）**的双数据源架构，完整展示了多数据源的实现方式。

### 4.2 多数据源配置

#### 配置文件（application-dev.yaml）

```yaml
spring:
  datasource:
    # MySQL数据源
    user:
      jdbc-url: jdbc:mysql://localhost:3306/frog
      username: frog_admin
      password: frog_password
      driver-class-name: com.mysql.cj.jdbc.Driver
      hikari:
        maximum-pool-size: 10
        minimum-idle: 5
        connection-timeout: 30000
        idle-timeout: 600000
        max-lifetime: 1800000

    # PostgreSQL数据源
    order:
      jdbc-url: jdbc:postgresql://localhost:5432/seed
      username: postgres
      password: postgres_password
      driver-class-name: org.postgresql.Driver
      hikari:
        maximum-pool-size: 10
        minimum-idle: 5
        connection-timeout: 30000
        idle-timeout: 600000
        max-lifetime: 1800000
```

**关键点**：
- 使用 `jdbc-url` 而不是 `url`（多数据源必须）
- 每个数据源独立配置连接池参数
- 使用不同的配置前缀（user / order）

#### 数据源配置类（DataSourceConfig.java）

```java
@Configuration
public class DataSourceConfig {

    // MySQL数据源（用户数据）
    @Bean(name = "userDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.user")
    public DataSource userDataSource() {
        return DataSourceBuilder.create().build();
    }

    // PostgreSQL数据源（订单数据）- 标记为Primary
    @Bean(name = "orderDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.order")
    public DataSource orderDataSource() {
        return DataSourceBuilder.create().build();
    }

    // MySQL JdbcTemplate
    @Bean(name = "userJdbcTemplate")
    public JdbcTemplate userJdbcTemplate(
            @Qualifier("userDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    // PostgreSQL JdbcTemplate
    @Bean(name = "orderJdbcTemplate")
    @Primary
    public JdbcTemplate orderJdbcTemplate(
            @Qualifier("orderDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    // MySQL事务管理器
    @Bean(name = "userTransactionManager")
    public PlatformTransactionManager userTransactionManager(
            @Qualifier("userDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    // PostgreSQL事务管理器
    @Bean(name = "orderTransactionManager")
    @Primary
    public PlatformTransactionManager orderTransactionManager(
            @Qualifier("orderDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
```

**关键点**：
1. **@Qualifier**：通过Bean名称注入特定数据源
2. **@Primary**：标记默认数据源（订单数据源）
3. **独立事务管理器**：每个数据源有自己的事务管理器
4. **HikariCP**：Spring Boot默认使用HikariCP连接池

### 4.3 双持久化策略

Springboot4DDD同时展示了两种数据访问方式：

#### 策略1：JdbcTemplate（用户数据）

**适用场景**：
- 需要灵活控制SQL
- 复杂查询
- 动态SQL构建

**实现示例**（UserRepositoryImpl.java）：

```java
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(@Qualifier("userJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User save(User user) {
        String sql = """
            INSERT INTO users (username, email, phone, nickname, status, created_time, updated_time)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getNickname());
            ps.setInt(5, user.getStatus());
            ps.setTimestamp(6, Timestamp.valueOf(user.getCreatedTime()));
            ps.setTimestamp(7, Timestamp.valueOf(user.getUpdatedTime()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try {
            User user = jdbcTemplate.queryForObject(sql, this::mapRow, id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
            .id(rs.getLong("id"))
            .username(rs.getString("username"))
            .email(rs.getString("email"))
            .phone(rs.getString("phone"))
            .nickname(rs.getString("nickname"))
            .status(rs.getInt("status"))
            .createdTime(rs.getTimestamp("created_time").toLocalDateTime())
            .updatedTime(rs.getTimestamp("updated_time").toLocalDateTime())
            .build();
    }
}
```

**优点**：
- 完全控制SQL语句
- 灵活的字段映射
- 适合复杂查询

**缺点**：
- 代码量较大
- 需要手动处理字段映射
- 容易出错

#### 策略2：Spring Data JDBC（订单数据）

**适用场景**：
- CRUD操作为主
- 简单查询
- 快速开发

**实现步骤**：

**步骤1**：配置Spring Data JDBC（OrderJdbcConfig.java）

```java
@Configuration
@EnableJdbcRepositories(
    basePackages = "com.github.microwind.springboot4ddd.infrastructure.repository.jdbc",
    jdbcOperationsRef = "orderNamedParameterJdbcOperations"
)
public class OrderJdbcConfig {

    @Bean
    public NamedParameterJdbcOperations orderNamedParameterJdbcOperations(
            @Qualifier("orderDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
```

**步骤2**：定义实体（Order.java）

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")  // Spring Data JDBC注解
public class Order {

    @Id  // Spring Data JDBC注解
    private Long id;

    @Column("order_no")
    private String orderNo;

    @Column("user_id")
    private Long userId;

    @Column("total_amount")
    private BigDecimal totalAmount;

    @Column("status")
    private OrderStatus status;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_time")
    private LocalDateTime updatedTime;

    // 业务方法
    public void cancel() {
        if (this.status != OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL);
        }
        this.status = OrderStatus.CANCELLED;
    }
}
```

**步骤3**：定义仓储接口（OrderJdbcRepository.java）

```java
@Repository
public interface OrderJdbcRepository extends CrudRepository<Order, Long> {

    @Query("SELECT * FROM orders WHERE order_no = :orderNo")
    Optional<Order> findByOrderNo(@Param("orderNo") String orderNo);

    @Query("SELECT * FROM orders WHERE user_id = :userId ORDER BY created_at DESC")
    List<Order> findByUserId(@Param("userId") Long userId);
}
```

**步骤4**：实现领域仓储接口（OrderRepositoryImpl.java）

```java
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJdbcRepository jdbcRepository;

    @Override
    public Order save(Order order) {
        return jdbcRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jdbcRepository.findById(id);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return jdbcRepository.findByUserId(userId);
    }
}
```

**优点**：
- 代码简洁
- 自动映射
- 减少样板代码

**缺点**：
- 灵活性稍差
- 复杂查询支持有限

### 4.4 跨数据库查询

Springboot4DDD的一大亮点是**在应用层实现跨数据库数据整合**。

**场景**：查询用户的所有订单，同时展示用户信息和订单信息（用户在MySQL，订单在PostgreSQL）

**实现**（OrderService.java）：

```java
@Service
@Transactional(transactionManager = "orderTransactionManager")
public class OrderService {

    private final OrderRepository orderRepository;  // PostgreSQL
    private final UserRepository userRepository;    // MySQL

    /**
     * 跨数据库查询：获取用户订单列表（含用户信息）
     */
    public List<OrderDTO> getUserOrderList(Long userId) {
        // 步骤1：从PostgreSQL查询订单
        List<Order> orders = orderRepository.findByUserId(userId);

        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        // 步骤2：从MySQL查询用户
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + userId));

        // 步骤3：在应用层组合数据
        return orders.stream()
            .map(order -> OrderMapper.toDTO(order, user))
            .collect(Collectors.toList());
    }

    /**
     * 跨数据库查询：获取所有订单列表（含用户信息）
     */
    public List<OrderDTO> getAllOrdersWithUserInfo() {
        // 步骤1：从PostgreSQL查询所有订单
        List<Order> orders = orderRepository.findAll();

        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        // 步骤2：提取所有用户ID
        Set<Long> userIds = orders.stream()
            .map(Order::getUserId)
            .collect(Collectors.toSet());

        // 步骤3：批量查询MySQL用户数据
        List<User> users = userRepository.findAll().stream()
            .filter(user -> userIds.contains(user.getId()))
            .collect(Collectors.toList());

        // 步骤4：构建userId -> User映射
        Map<Long, User> userMap = users.stream()
            .collect(Collectors.toMap(User::getId, Function.identity()));

        // 步骤5：组合数据
        return orders.stream()
            .map(order -> {
                User user = userMap.get(order.getUserId());
                return OrderMapper.toDTO(order, user);
            })
            .collect(Collectors.toList());
    }
}
```

**关键点**：
1. **不使用分布式事务**：两个数据库的事务独立管理
2. **应用层组合**：在应用服务层完成数据整合
3. **性能优化**：批量查询用户，避免N+1问题
4. **异常处理**：用户不存在时抛出明确异常

**调用示例**：

```bash
# 查询用户1的所有订单（含用户信息）
curl http://localhost:8080/api/orders/user/1

# 查询所有订单（含用户信息）
curl http://localhost:8080/api/orders/list
```

**响应示例**：

```json
{
  "code": 200,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "orderNo": "ORD1000000001",
      "userId": 1,
      "totalAmount": 100.00,
      "status": "PENDING",
      "statusDesc": "待支付",
      "createdAt": "2024-01-01T10:00:00",
      "username": "user1",        // 来自MySQL
      "email": "user1@example.com", // 来自MySQL
      "phone": "13800000001"      // 来自MySQL
    }
  ]
}
```

### 4.5 事务管理

在多数据源环境中，每个数据源需要独立的事务管理器。

**使用方式**：

```java
// 用户服务：使用userTransactionManager
@Service
@Transactional(transactionManager = "userTransactionManager")
public class UserService {
    // 所有方法都在MySQL事务中

    public User createUser(CreateUserRequest request) {
        // 这里的操作在MySQL事务中
        return userRepository.save(user);
    }
}

// 订单服务：使用orderTransactionManager
@Service
@Transactional(transactionManager = "orderTransactionManager")
public class OrderService {
    // 所有方法都在PostgreSQL事务中

    public OrderDTO createOrder(CreateOrderRequest request) {
        // 这里的操作在PostgreSQL事务中
        return orderRepository.save(order);
    }
}
```

**跨数据库事务说明**：

⚠️ **重要**：Springboot4DDD不支持跨数据库的分布式事务（XA事务）。原因如下：

1. **性能考虑**：分布式事务性能较差
2. **复杂度**：增加系统复杂度
3. **实际需求**：大多数场景不需要强一致性

**替代方案**：
- **最终一致性**：通过消息队列（如RocketMQ）实现
- **补偿机制**：业务失败时执行补偿操作
- **Saga模式**：长事务拆分为多个本地事务

### 4.6 字段映射注意事项

在多数据源环境中，不同数据库可能有不同的命名约定：

| 层级 | MySQL | PostgreSQL | Java |
|------|-------|------------|------|
| **时间字段** | `created_time` | `created_at` | `createdTime` |
| **更新时间** | `updated_time` | `updated_time` | `updatedTime` |
| **命名风格** | snake_case | snake_case | camelCase |

**解决方案**：

1. **JdbcTemplate方式**：手动映射
```java
private User mapRow(ResultSet rs, int rowNum) throws SQLException {
    return User.builder()
        .createdTime(rs.getTimestamp("created_time").toLocalDateTime())  // 手动映射
        .build();
}
```

2. **Spring Data JDBC方式**：使用@Column注解
```java
@Column("created_at")  // 数据库字段名
private LocalDateTime createdAt;  // Java字段名
```

---

## 五、API签名验证：构建安全的接口体系

### 5.1 为什么需要API签名验证？

在Web应用中，REST API的安全性至关重要。常见的安全威胁包括：

| 威胁 | 说明 | 危害 |
|------|------|------|
| **未授权访问** | 任何人都可以调用接口 | 数据泄露、恶意操作 |
| **参数篡改** | 攻击者修改请求参数 | 业务逻辑被破坏 |
| **重放攻击** | 攻击者重复发送截获的请求 | 重复扣款、刷量 |
| **接口滥用** | 恶意调用消耗服务器资源 | 服务不可用 |

Springboot4DDD内置了基于**SHA-256的签名验证机制**，有效防止上述安全威胁。

### 5.2 签名机制原理

#### 签名生成算法

**不含参数的签名**（适用于GET请求）：

```
signature = SHA256(appCode + secretKey + apiPath + timestamp)
```

**含参数的签名**（适用于POST/PUT请求）：

```
paramsString = buildSignatureSource(params)  // 参数按ASCII排序
signature = SHA256(paramsString + appCode + secretKey + apiPath + timestamp)
```

**关键组成**：
- `appCode`：调用方标识（如 "ios1", "h5"）
- `secretKey`：密钥（服务端存储，客户端不可见）
- `apiPath`：接口路径（如 "/api/orders/create"）
- `timestamp`：时间戳（毫秒，用于防重放）
- `params`：请求参数（可选）

#### 签名验证流程

```
客户端                               服务端
   │                                  │
   │ 1. 准备请求参数                 │
   │    userId=1, amount=100          │
   │                                  │
   │ 2. 生成签名                     │
   │    sign = SHA256(...)            │
   │                                  │
   │ 3. 发送请求                     │
   ├──────────────────────────────────>│
   │    Headers:                      │
   │      Sign-appCode: ios1          │ 4. 提取签名信息
   │      Sign-sign: abc123...        │    从Header读取
   │      Sign-time: 1704787200000    │
   │      Sign-path: /api/orders/create│
   │    Body: {userId:1, amount:100}  │
   │                                  │
   │                                  │ 5. 验证时间戳
   │                                  │    是否在10分钟内
   │                                  │
   │                                  │ 6. 查询权限配置
   │                                  │    查询secretKey和权限
   │                                  │
   │                                  │ 7. 重新计算签名
   │                                  │    expected = SHA256(...)
   │                                  │
   │                                  │ 8. 比对签名
   │                                  │    expected == sign ?
   │                                  │
   │ 9. 返回结果                     │
   │<──────────────────────────────────┤
   │    {code:200, data:{...}}        │
   │                                  │
```

### 5.3 配置API认证

#### 步骤1：编辑apiauth-config.yaml

```yaml
api-auth:
  apps:
    # iOS应用
    - app-code: ios1
      secret-key: ios_secret_key_123456
      permissions:
        - /api/orders/**
        - /api/users/**
        - /api/payment/**

    # H5应用
    - app-code: h5
      secret-key: h5_secret_key_abcdef
      permissions:
        - /api/orders/create
        - /api/orders/*/pay
        - /api/users/*

    # 内部服务
    - app-code: internal
      secret-key: internal_super_secret_key_xyz
      permissions:
        - /**  # 完全权限

  interfaces:
    # 订单创建接口
    - api-path: /api/orders/create
      interface-salt: order_create_salt_2024
      with-params: true  # 需要参数签名

    # 订单支付接口
    - api-path: /api/orders/*/pay
      interface-salt: order_pay_salt_2024
      with-params: false  # 不需要参数签名

    # 用户查询接口
    - api-path: /api/users/*
      interface-salt: user_salt_2024
      with-params: false

# 签名配置
sign:
  signature:
    ttl: 600000  # 签名有效期：10分钟（毫秒）
    default-with-params: false  # 默认不含参数
```

**配置说明**：
- `app-code`：唯一标识调用方
- `secret-key`：密钥，**绝对不能泄露给客户端**
- `permissions`：该应用可访问的接口（支持Ant路径匹配）
- `interface-salt`：接口盐值，增加签名复杂度
- `with-params`：是否包含参数进行签名
- `ttl`：签名有效期，防止重放攻击

#### 步骤2：在控制器方法上添加注解

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    // 创建订单：需要签名验证，包含参数
    @PostMapping("/create")
    @RequireSign(withParams = WithParams.TRUE)
    public ApiResponse<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        OrderDTO orderDTO = orderService.createOrder(request);
        return ApiResponse.success(OrderResponse.from(orderDTO));
    }

    // 支付订单：需要签名验证，不包含参数
    @PostMapping("/{id}/pay")
    @RequireSign(withParams = WithParams.FALSE)
    public ApiResponse<OrderResponse> payOrder(@PathVariable Long id) {
        OrderDTO orderDTO = orderService.payOrder(id);
        return ApiResponse.success(OrderResponse.from(orderDTO));
    }

    // 查询订单：不需要签名验证
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long id) {
        OrderDTO orderDTO = orderService.getOrderById(id);
        return ApiResponse.success(OrderResponse.from(orderDTO));
    }

    // 健康检查：忽略签名验证
    @GetMapping("/health")
    @IgnoreSignHeader
    public ApiResponse<String> health() {
        return ApiResponse.success("OK");
    }
}
```

**注解说明**：
- `@RequireSign`：标记需要签名验证的方法
- `WithParams.TRUE`：签名包含请求参数
- `WithParams.FALSE`：签名不包含请求参数
- `@IgnoreSignHeader`：跳过签名验证

### 5.4 客户端调用示例

#### 不含参数的签名（GET/DELETE请求）

**Java客户端**：

```java
public class ApiClient {

    private static final String APP_CODE = "ios1";
    private static final String SECRET_KEY = "ios_secret_key_123456";
    private static final String BASE_URL = "http://localhost:8080";

    /**
     * 查询订单详情（不含参数签名）
     */
    public OrderResponse getOrder(Long orderId) throws Exception {
        String apiPath = "/api/orders/" + orderId;
        long timestamp = System.currentTimeMillis();

        // 生成签名
        String sign = generateSign(apiPath, timestamp, null);

        // 构建请求
        HttpURLConnection conn = (HttpURLConnection)
            new URL(BASE_URL + apiPath).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Sign-appCode", APP_CODE);
        conn.setRequestProperty("Sign-sign", sign);
        conn.setRequestProperty("Sign-time", String.valueOf(timestamp));
        conn.setRequestProperty("Sign-path", apiPath);

        // 发送请求并解析响应
        // ...
    }

    /**
     * 生成签名（不含参数）
     */
    private String generateSign(String apiPath, long timestamp,
                                Map<String, Object> params) {
        String signSource = APP_CODE + SECRET_KEY + apiPath + timestamp;
        return DigestUtils.sha256Hex(signSource);
    }
}
```

**JavaScript客户端**：

```javascript
// 引入crypto-js库
import CryptoJS from 'crypto-js';

const APP_CODE = 'h5';
const SECRET_KEY = 'h5_secret_key_abcdef';
const BASE_URL = 'http://localhost:8080';

/**
 * 查询订单详情（不含参数签名）
 */
async function getOrder(orderId) {
    const apiPath = `/api/orders/${orderId}`;
    const timestamp = Date.now();

    // 生成签名
    const signSource = `${APP_CODE}${SECRET_KEY}${apiPath}${timestamp}`;
    const sign = CryptoJS.SHA256(signSource).toString();

    // 发送请求
    const response = await fetch(`${BASE_URL}${apiPath}`, {
        method: 'GET',
        headers: {
            'Sign-appCode': APP_CODE,
            'Sign-sign': sign,
            'Sign-time': timestamp.toString(),
            'Sign-path': apiPath
        }
    });

    return await response.json();
}
```

#### 含参数的签名（POST/PUT请求）

**Java客户端**：

```java
/**
 * 创建订单（含参数签名）
 */
public OrderResponse createOrder(Long userId, BigDecimal amount) throws Exception {
    String apiPath = "/api/orders/create";
    long timestamp = System.currentTimeMillis();

    // 构建请求参数
    Map<String, Object> params = new LinkedHashMap<>();
    params.put("userId", userId);
    params.put("totalAmount", amount);

    // 生成签名
    String sign = generateSignWithParams(apiPath, timestamp, params);

    // 构建请求
    HttpURLConnection conn = (HttpURLConnection)
        new URL(BASE_URL + apiPath).openConnection();
    conn.setRequestMethod("POST");
    conn.setDoOutput(true);
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setRequestProperty("Sign-appCode", APP_CODE);
    conn.setRequestProperty("Sign-sign", sign);
    conn.setRequestProperty("Sign-time", String.valueOf(timestamp));
    conn.setRequestProperty("Sign-path", apiPath);

    // 写入请求体
    String jsonBody = new ObjectMapper().writeValueAsString(params);
    conn.getOutputStream().write(jsonBody.getBytes(StandardCharsets.UTF_8));

    // 发送请求并解析响应
    // ...
}

/**
 * 生成签名（含参数）
 */
private String generateSignWithParams(String apiPath, long timestamp,
                                     Map<String, Object> params) {
    // 步骤1：构建参数字符串（按ASCII排序）
    String paramsString = buildSignatureSource(params);

    // 步骤2：生成签名
    String signSource = paramsString + APP_CODE + SECRET_KEY + apiPath + timestamp;
    return DigestUtils.sha256Hex(signSource);
}

/**
 * 构建参数字符串（按键名ASCII排序）
 */
private String buildSignatureSource(Map<String, Object> params) {
    if (params == null || params.isEmpty()) {
        return "";
    }

    // 排序键名
    List<String> sortedKeys = new ArrayList<>(params.keySet());
    Collections.sort(sortedKeys);

    // 拼接参数
    StringBuilder builder = new StringBuilder();
    boolean first = true;

    for (String key : sortedKeys) {
        Object value = params.get(key);

        // 跳过null和空字符串
        if (value == null || "".equals(value)) {
            continue;
        }

        if (!first) {
            builder.append("&");
        }
        first = false;
        builder.append(key).append("=").append(value);
    }

    return builder.toString();
}
```

**JavaScript客户端**：

```javascript
/**
 * 创建订单（含参数签名）
 */
async function createOrder(userId, totalAmount) {
    const apiPath = '/api/orders/create';
    const timestamp = Date.now();

    // 构建请求参数
    const params = {
        userId: userId,
        totalAmount: totalAmount
    };

    // 生成签名
    const sign = generateSignWithParams(apiPath, timestamp, params);

    // 发送请求
    const response = await fetch(`${BASE_URL}${apiPath}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Sign-appCode': APP_CODE,
            'Sign-sign': sign,
            'Sign-time': timestamp.toString(),
            'Sign-path': apiPath
        },
        body: JSON.stringify(params)
    });

    return await response.json();
}

/**
 * 生成签名（含参数）
 */
function generateSignWithParams(apiPath, timestamp, params) {
    // 步骤1：构建参数字符串（按ASCII排序）
    const paramsString = buildSignatureSource(params);

    // 步骤2：生成签名
    const signSource = `${paramsString}${APP_CODE}${SECRET_KEY}${apiPath}${timestamp}`;
    return CryptoJS.SHA256(signSource).toString();
}

/**
 * 构建参数字符串（按键名ASCII排序）
 */
function buildSignatureSource(params) {
    if (!params || Object.keys(params).length === 0) {
        return '';
    }

    // 排序键名
    const sortedKeys = Object.keys(params).sort();

    // 拼接参数
    const parts = [];
    for (const key of sortedKeys) {
        const value = params[key];

        // 跳过null和空字符串
        if (value === null || value === undefined || value === '') {
            continue;
        }

        parts.push(`${key}=${value}`);
    }

    return parts.join('&');
}
```

### 5.5 测试签名验证

#### 使用curl测试

**不含参数的签名**：

```bash
#!/bin/bash

APP_CODE="ios1"
SECRET_KEY="ios_secret_key_123456"
API_PATH="/api/orders/1"
TIMESTAMP=$(date +%s%3N)  # 毫秒时间戳

# 生成签名
SIGN_SOURCE="${APP_CODE}${SECRET_KEY}${API_PATH}${TIMESTAMP}"
SIGN=$(echo -n "$SIGN_SOURCE" | openssl dgst -sha256 | awk '{print $2}')

# 发送请求
curl -X GET "http://localhost:8080${API_PATH}" \
  -H "Sign-appCode: ${APP_CODE}" \
  -H "Sign-sign: ${SIGN}" \
  -H "Sign-time: ${TIMESTAMP}" \
  -H "Sign-path: ${API_PATH}"
```

**含参数的签名**：

```bash
#!/bin/bash

APP_CODE="ios1"
SECRET_KEY="ios_secret_key_123456"
API_PATH="/api/orders/create"
TIMESTAMP=$(date +%s%3N)

# 请求参数（按ASCII排序）
PARAMS="totalAmount=100.00&userId=1"

# 生成签名
SIGN_SOURCE="${PARAMS}${APP_CODE}${SECRET_KEY}${API_PATH}${TIMESTAMP}"
SIGN=$(echo -n "$SIGN_SOURCE" | openssl dgst -sha256 | awk '{print $2}')

# 发送请求
curl -X POST "http://localhost:8080${API_PATH}" \
  -H "Content-Type: application/json" \
  -H "Sign-appCode: ${APP_CODE}" \
  -H "Sign-sign: ${SIGN}" \
  -H "Sign-time: ${TIMESTAMP}" \
  -H "Sign-path: ${API_PATH}" \
  -d '{"userId":1,"totalAmount":100.00}'
```

#### 常见错误及解决方案

| 错误信息 | 原因 | 解决方案 |
|---------|------|----------|
| `缺少签名头信息` | 未传递签名Header | 检查是否传递了4个必需的Header |
| `签名已过期` | 时间戳超过10分钟 | 使用当前时间戳 |
| `应用无权限访问该接口` | 权限配置不正确 | 检查apiauth-config.yaml的permissions |
| `签名验证失败` | 签名计算错误 | 检查签名算法、参数排序、时间戳 |
| `接口不存在` | apiPath错误 | 检查apiPath是否与实际路径一致 |

---

## 六、实战案例：订单系统完整实现

通过一个完整的订单系统案例，展示如何使用Springboot4DDD开发业务功能。

### 6.1 业务需求

开发一个订单管理系统，支持：

| 功能 | 说明 |
|------|------|
| **创建订单** | 用户下单，生成订单号 |
| **查询订单** | 查看订单详情 |
| **查询用户订单** | 查看某用户的所有订单（含用户信息） |
| **取消订单** | 取消待支付订单 |
| **支付订单** | 支付待支付订单 |
| **完成订单** | 完成已支付订单 |
| **删除订单** | 删除订单（仅测试环境） |

### 6.2 领域层实现

#### 步骤1：定义订单实体（Order.java）

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
public class Order {

    @Id
    private Long id;

    @Column("order_no")
    private String orderNo;

    @Column("user_id")
    private Long userId;

    @Column("total_amount")
    private BigDecimal totalAmount;

    @Column("status")
    private OrderStatus status;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_time")
    private LocalDateTime updatedTime;

    /**
     * 工厂方法：创建新订单
     */
    public static Order create(Long userId, BigDecimal totalAmount) {
        String orderNo = generateOrderNo();
        LocalDateTime now = LocalDateTime.now();

        return Order.builder()
                .orderNo(orderNo)
                .userId(userId)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .createdAt(now)
                .updatedTime(now)
                .build();
    }

    /**
     * 业务方法：取消订单
     */
    public void cancel() {
        if (this.status != OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL,
                "只有待支付订单才能取消");
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 业务方法：支付订单
     */
    public void pay() {
        if (this.status != OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_PAY,
                "只有待支付订单才能支付");
        }
        this.status = OrderStatus.PAID;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 业务方法：完成订单
     */
    public void complete() {
        if (this.status != OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_COMPLETE,
                "只有已支付订单才能完成");
        }
        this.status = OrderStatus.COMPLETED;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 生成订单号
     */
    private static String generateOrderNo() {
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 1000);
        return String.format("ORD%d%03d", timestamp, random);
    }
}
```

#### 步骤2：定义订单状态枚举（OrderStatus.java）

```java
public enum OrderStatus {
    PENDING("待支付"),
    PAID("已支付"),
    CANCELLED("已取消"),
    COMPLETED("已完成");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
```

#### 步骤3：定义仓储接口（OrderRepository.java）

```java
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    Optional<Order> findByOrderNo(String orderNo);

    List<Order> findByUserId(Long userId);

    List<Order> findAll();

    void deleteById(Long id);
}
```

### 6.3 基础设施层实现

#### 步骤1：定义Spring Data JDBC仓储（OrderJdbcRepository.java）

```java
@Repository
public interface OrderJdbcRepository extends CrudRepository<Order, Long> {

    @Query("SELECT * FROM orders WHERE order_no = :orderNo")
    Optional<Order> findByOrderNo(@Param("orderNo") String orderNo);

    @Query("SELECT * FROM orders WHERE user_id = :userId ORDER BY created_at DESC")
    List<Order> findByUserId(@Param("userId") Long userId);
}
```

#### 步骤2：实现仓储接口（OrderRepositoryImpl.java）

```java
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJdbcRepository jdbcRepository;

    public OrderRepositoryImpl(OrderJdbcRepository jdbcRepository) {
        this.jdbcRepository = jdbcRepository;
    }

    @Override
    public Order save(Order order) {
        return jdbcRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jdbcRepository.findById(id);
    }

    @Override
    public Optional<Order> findByOrderNo(String orderNo) {
        return jdbcRepository.findByOrderNo(orderNo);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return jdbcRepository.findByUserId(userId);
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        jdbcRepository.findAll().forEach(orders::add);
        return orders;
    }

    @Override
    public void deleteById(Long id) {
        jdbcRepository.deleteById(id);
    }
}
```

### 6.4 应用层实现

#### 步骤1：定义DTO（OrderDTO.java）

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;
    private String statusDesc;
    private LocalDateTime createdAt;

    // 用户信息（跨数据库查询时填充）
    private String username;
    private String email;
    private String phone;
}
```

#### 步骤2：定义Mapper（OrderMapper.java）

```java
@Component
public class OrderMapper {

    /**
     * 实体转DTO（不含用户信息）
     */
    public static OrderDTO toDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .statusDesc(order.getStatus().getDescription())
                .createdAt(order.getCreatedAt())
                .build();
    }

    /**
     * 实体转DTO（含用户信息）
     */
    public static OrderDTO toDTO(Order order, User user) {
        OrderDTO dto = toDTO(order);
        if (user != null) {
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhone());
        }
        return dto;
    }
}
```

#### 步骤3：实现应用服务（OrderService.java）

```java
@Service
@Transactional(transactionManager = "orderTransactionManager")
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                       UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    /**
     * 创建订单
     */
    public OrderDTO createOrder(CreateOrderRequest request) {
        // 验证用户是否存在
        userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "用户不存在: " + request.getUserId()));

        // 创建订单（调用领域对象的工厂方法）
        Order order = Order.create(request.getUserId(), request.getTotalAmount());

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        // 转换为DTO
        return OrderMapper.toDTO(savedOrder);
    }

    /**
     * 查询订单（不含用户信息）
     */
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("订单不存在: " + id));

        return OrderMapper.toDTO(order);
    }

    /**
     * 查询用户订单（含用户信息，跨数据库）
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> getUserOrderList(Long userId) {
        // 从PostgreSQL查询订单
        List<Order> orders = orderRepository.findByUserId(userId);

        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        // 从MySQL查询用户
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + userId));

        // 组合数据
        return orders.stream()
            .map(order -> OrderMapper.toDTO(order, user))
            .collect(Collectors.toList());
    }

    /**
     * 查询所有订单（含用户信息，跨数据库）
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrdersWithUserInfo() {
        // 从PostgreSQL查询所有订单
        List<Order> orders = orderRepository.findAll();

        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        // 提取用户ID
        Set<Long> userIds = orders.stream()
            .map(Order::getUserId)
            .collect(Collectors.toSet());

        // 批量查询用户
        List<User> users = userRepository.findAll().stream()
            .filter(user -> userIds.contains(user.getId()))
            .collect(Collectors.toList());

        // 构建用户映射
        Map<Long, User> userMap = users.stream()
            .collect(Collectors.toMap(User::getId, Function.identity()));

        // 组合数据
        return orders.stream()
            .map(order -> {
                User user = userMap.get(order.getUserId());
                return OrderMapper.toDTO(order, user);
            })
            .collect(Collectors.toList());
    }

    /**
     * 取消订单
     */
    public OrderDTO cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("订单不存在: " + id));

        // 调用领域对象的业务方法
        order.cancel();

        // 保存
        Order updatedOrder = orderRepository.save(order);

        return OrderMapper.toDTO(updatedOrder);
    }

    /**
     * 支付订单
     */
    public OrderDTO payOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("订单不存在: " + id));

        // 调用领域对象的业务方法
        order.pay();

        // 保存
        Order updatedOrder = orderRepository.save(order);

        return OrderMapper.toDTO(updatedOrder);
    }

    /**
     * 完成订单
     */
    public OrderDTO completeOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("订单不存在: " + id));

        // 调用领域对象的业务方法
        order.complete();

        // 保存
        Order updatedOrder = orderRepository.save(order);

        return OrderMapper.toDTO(updatedOrder);
    }

    /**
     * 删除订单
     */
    public void deleteOrder(Long id) {
        if (!orderRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("订单不存在: " + id);
        }

        orderRepository.deleteById(id);
    }
}
```

### 6.5 接口层实现

#### 步骤1：定义请求对象（CreateOrderRequest.java）

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "用户ID不能为空")
    @Min(value = 1, message = "用户ID必须大于0")
    private Long userId;

    @NotNull(message = "订单金额不能为空")
    @DecimalMin(value = "0.01", message = "订单金额必须大于0")
    private BigDecimal totalAmount;
}
```

#### 步骤2：定义响应对象（OrderResponse.java, OrderListResponse.java）

```java
@Data
@Builder
public class OrderResponse {

    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;
    private String statusDesc;
    private LocalDateTime createdAt;

    public static OrderResponse from(OrderDTO dto) {
        return OrderResponse.builder()
                .id(dto.getId())
                .orderNo(dto.getOrderNo())
                .userId(dto.getUserId())
                .totalAmount(dto.getTotalAmount())
                .status(dto.getStatus())
                .statusDesc(dto.getStatusDesc())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}

@Data
@Builder
public class OrderListResponse {

    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;
    private String statusDesc;
    private LocalDateTime createdAt;

    // 用户信息
    private String username;
    private String email;
    private String phone;

    public static OrderListResponse from(OrderDTO dto) {
        return OrderListResponse.builder()
                .id(dto.getId())
                .orderNo(dto.getOrderNo())
                .userId(dto.getUserId())
                .totalAmount(dto.getTotalAmount())
                .status(dto.getStatus())
                .statusDesc(dto.getStatusDesc())
                .createdAt(dto.getCreatedAt())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .build();
    }

    public static List<OrderListResponse> fromList(List<OrderDTO> dtoList) {
        return dtoList.stream()
                .map(OrderListResponse::from)
                .collect(Collectors.toList());
    }
}
```

#### 步骤3：实现控制器（OrderController.java）

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单（需要签名验证，包含参数）
     */
    @PostMapping("/create")
    @RequireSign(withParams = WithParams.TRUE)
    public ApiResponse<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        OrderDTO orderDTO = orderService.createOrder(request);
        return ApiResponse.success(OrderResponse.from(orderDTO));
    }

    /**
     * 查询订单详情（不含用户信息，无需签名）
     */
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long id) {
        OrderDTO orderDTO = orderService.getOrderById(id);
        return ApiResponse.success(OrderResponse.from(orderDTO));
    }

    /**
     * 查询用户订单列表（含用户信息，无需签名）
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderListResponse>> getUserOrders(
            @PathVariable Long userId) {
        List<OrderDTO> orders = orderService.getUserOrderList(userId);
        return ApiResponse.success(OrderListResponse.fromList(orders));
    }

    /**
     * 查询所有订单（含用户信息，无需签名）
     */
    @GetMapping("/list")
    public ApiResponse<List<OrderListResponse>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrdersWithUserInfo();
        return ApiResponse.success(OrderListResponse.fromList(orders));
    }

    /**
     * 取消订单（需要签名验证）
     */
    @PostMapping("/{id}/cancel")
    @RequireSign(withParams = WithParams.FALSE)
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable Long id) {
        OrderDTO orderDTO = orderService.cancelOrder(id);
        return ApiResponse.success(OrderResponse.from(orderDTO));
    }

    /**
     * 支付订单（需要签名验证）
     */
    @PostMapping("/{id}/pay")
    @RequireSign(withParams = WithParams.FALSE)
    public ApiResponse<OrderResponse> payOrder(@PathVariable Long id) {
        OrderDTO orderDTO = orderService.payOrder(id);
        return ApiResponse.success(OrderResponse.from(orderDTO));
    }

    /**
     * 完成订单（需要签名验证）
     */
    @PostMapping("/{id}/complete")
    @RequireSign(withParams = WithParams.FALSE)
    public ApiResponse<OrderResponse> completeOrder(@PathVariable Long id) {
        OrderDTO orderDTO = orderService.completeOrder(id);
        return ApiResponse.success(OrderResponse.from(orderDTO));
    }

    /**
     * 删除订单（需要签名验证）
     */
    @DeleteMapping("/{id}")
    @RequireSign(withParams = WithParams.FALSE)
    public ApiResponse<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ApiResponse.success(null);
    }
}
```

### 6.6 完整测试

```bash
# 1. 创建订单（需要签名）
curl -X POST "http://localhost:8080/api/orders/create" \
  -H "Content-Type: application/json" \
  -H "Sign-appCode: ios1" \
  -H "Sign-sign: <生成的签名>" \
  -H "Sign-time: <时间戳>" \
  -H "Sign-path: /api/orders/create" \
  -d '{"userId":1,"totalAmount":299.99}'

# 2. 查询订单详情
curl "http://localhost:8080/api/orders/1"

# 3. 查询用户订单列表（含用户信息，跨数据库）
curl "http://localhost:8080/api/orders/user/1"

# 4. 支付订单（需要签名）
curl -X POST "http://localhost:8080/api/orders/1/pay" \
  -H "Sign-appCode: ios1" \
  -H "Sign-sign: <生成的签名>" \
  -H "Sign-time: <时间戳>" \
  -H "Sign-path: /api/orders/1/pay"

# 5. 完成订单（需要签名）
curl -X POST "http://localhost:8080/api/orders/1/complete" \
  -H "Sign-appCode: ios1" \
  -H "Sign-sign: <生成的签名>" \
  -H "Sign-time: <时间戳>" \
  -H "Sign-path: /api/orders/1/complete"
```

---

## 七、最佳实践与开发建议

### 7.1 DDD设计原则

| 原则 | 说明 | 示例 |
|------|------|------|
| **领域层纯净** | 领域层不依赖框架 | Order实体不使用@Service、@Autowired |
| **业务逻辑内聚** | 业务规则在领域对象中 | Order.cancel()包含取消订单的业务规则 |
| **依赖倒置** | 高层不依赖低层 | 应用层依赖仓储接口，不依赖实现 |
| **单一职责** | 每个类只负责一件事 | OrderService只负责编排，不包含业务逻辑 |
| **开闭原则** | 对扩展开放，对修改关闭 | 新增支付方式通过新增类，不修改原有代码 |

### 7.2 代码规范

#### 命名规范

```java
// 领域对象：名词，业务语言
public class Order { }
public class User { }

// 应用服务：XxxService
public class OrderService { }

// 仓储接口：XxxRepository
public interface OrderRepository { }

// 仓储实现：XxxRepositoryImpl
public class OrderRepositoryImpl implements OrderRepository { }

// DTO：XxxDTO
public class OrderDTO { }

// 请求对象：XxxRequest
public class CreateOrderRequest { }

// 响应对象：XxxResponse
public class OrderResponse { }

// 控制器：XxxController
public class OrderController { }
```

#### 包结构规范

```
com.github.microwind.springboot4ddd/
├── domain/                  # 领域层
│   ├── model/              # 领域模型
│   │   ├── order/         # 订单聚合
│   │   └── user/          # 用户聚合
│   ├── repository/        # 仓储接口
│   └── service/           # 领域服务
├── application/            # 应用层
│   ├── dto/              # DTO
│   └── service/          # 应用服务
├── infrastructure/        # 基础设施层
│   ├── common/          # 通用组件
│   ├── config/          # 配置类
│   ├── repository/      # 仓储实现
│   └── util/            # 工具类
└── interfaces/           # 接口层
    ├── annotation/      # 自定义注解
    ├── controller/      # 控制器
    └── vo/             # 视图对象
```

### 7.3 事务管理建议

```java
// 推荐：应用服务层管理事务
@Service
@Transactional(transactionManager = "orderTransactionManager")
public class OrderService {

    // 默认事务（读写）
    public OrderDTO createOrder(CreateOrderRequest request) {
        // ...
    }

    // 只读事务（性能优化）
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        // ...
    }

    // 特定异常回滚
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO complexOperation() {
        // ...
    }
}

// 不推荐：控制器层管理事务
@RestController
@Transactional  // ❌ 控制器不应该管理事务
public class OrderController {
    // ...
}
```

### 7.4 异常处理建议

```java
// 定义业务异常
public class BusinessException extends RuntimeException {
    private final String code;
    private final String message;

    public BusinessException(ErrorCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.message = message;
    }
}

// 在领域对象中抛出业务异常
public class Order {
    public void cancel() {
        if (this.status != OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL,
                "只有待支付订单才能取消，当前状态：" + this.status.getDescription());
        }
        this.status = OrderStatus.CANCELLED;
    }
}

// 全局异常处理器统一捕获
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<?> handleBusinessException(BusinessException e) {
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiResponse<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ApiResponse.error("404", e.getMessage());
    }
}
```

### 7.5 测试建议

```java
// 单元测试：测试领域逻辑
@Test
public void testOrderCancel() {
    // Given
    Order order = Order.create(1L, new BigDecimal("100.00"));

    // When
    order.cancel();

    // Then
    assertEquals(OrderStatus.CANCELLED, order.getStatus());
}

// 集成测试：测试完整流程
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateOrder() throws Exception {
        mockMvc.perform(post("/api/orders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":1,\"totalAmount\":100.00}")
                .header("Sign-appCode", "ios1")
                .header("Sign-sign", "...")
                .header("Sign-time", "...")
                .header("Sign-path", "/api/orders/create"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }
}
```

---

## 八、常见问题与解决方案

### 8.1 多数据源相关问题

#### Q1：如何添加第三个数据源？

```java
@Configuration
public class DataSourceConfig {

    // 添加第三个数据源
    @Bean(name = "thirdDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.third")
    public DataSource thirdDataSource() {
        return DataSourceBuilder.create().build();
    }

    // 添加对应的JdbcTemplate
    @Bean(name = "thirdJdbcTemplate")
    public JdbcTemplate thirdJdbcTemplate(
            @Qualifier("thirdDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    // 添加对应的事务管理器
    @Bean(name = "thirdTransactionManager")
    public PlatformTransactionManager thirdTransactionManager(
            @Qualifier("thirdDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
```

#### Q2：如何解决字段名不一致问题？

使用@Column注解明确映射：

```java
@Table("users")
public class User {

    @Column("created_time")  // MySQL字段名
    private LocalDateTime createdTime;  // Java字段名

    @Column("user_name")  // 数据库使用user_name
    private String username;  // Java使用username
}
```

#### Q3：跨数据库事务如何处理？

Springboot4DDD不支持分布式事务，推荐使用最终一致性方案：

```java
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RocketMQTemplate rocketMQTemplate;

    // 方案1：消息队列实现最终一致性
    @Transactional(transactionManager = "orderTransactionManager")
    public OrderDTO createOrderWithEventDriven(CreateOrderRequest request) {
        // 步骤1：创建订单（PostgreSQL事务）
        Order order = Order.create(request.getUserId(), request.getTotalAmount());
        Order savedOrder = orderRepository.save(order);

        // 步骤2：发送消息（异步更新其他数据源）
        OrderCreatedEvent event = new OrderCreatedEvent(savedOrder.getId(),
                                                        savedOrder.getUserId());
        rocketMQTemplate.convertAndSend("order-created-topic", event);

        return OrderMapper.toDTO(savedOrder);
    }

    // 方案2：补偿机制
    @Transactional(transactionManager = "orderTransactionManager")
    public OrderDTO createOrderWithCompensation(CreateOrderRequest request) {
        try {
            // 步骤1：创建订单
            Order order = Order.create(request.getUserId(), request.getTotalAmount());
            Order savedOrder = orderRepository.save(order);

            // 步骤2：调用用户服务更新用户积分（可能失败）
            try {
                userService.updatePoints(request.getUserId(), 10);
            } catch (Exception e) {
                // 补偿：回滚订单
                orderRepository.deleteById(savedOrder.getId());
                throw new BusinessException(ErrorCode.ORDER_CREATE_FAILED,
                                          "更新用户积分失败");
            }

            return OrderMapper.toDTO(savedOrder);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ORDER_CREATE_FAILED,
                                      e.getMessage());
        }
    }
}
```

### 8.2 API签名相关问题

#### Q4：前端如何安全存储secretKey？

⚠️ **重要**：前端永远不应该存储secretKey！

正确方案：
1. **后端签名**：前端请求后端的"签名生成接口"，后端使用secretKey生成签名后返回
2. **临时Token**：后端颁发有时效的临时Token给前端使用

```java
// 后端提供签名生成接口
@RestController
@RequestMapping("/api/sign")
public class SignController {

    @PostMapping("/generate")
    public ApiResponse<SignResponse> generateSign(
            @RequestBody SignRequest request) {
        // 验证应用权限
        AppConfig app = apiAuthConfig.getApp(request.getAppCode());
        if (app == null) {
            throw new BusinessException(ErrorCode.INVALID_APP_CODE);
        }

        // 生成签名
        String sign = SignatureUtil.generateSign(
            request.getAppCode(),
            app.getSecretKey(),  // secretKey只在后端使用
            request.getApiPath(),
            System.currentTimeMillis()
        );

        return ApiResponse.success(new SignResponse(sign));
    }
}
```

#### Q5：签名验证失败如何调试？

开启调试日志：

```yaml
logging:
  level:
    com.github.microwind.springboot4ddd.infrastructure.middleware: DEBUG
```

检查签名源字符串：

```java
@Component
public class SignatureInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) {
        // 输出签名源字符串
        String signSource = buildSignSource(...);
        log.debug("签名源字符串: {}", signSource);

        String expectedSign = DigestUtils.sha256Hex(signSource);
        log.debug("期望签名: {}", expectedSign);
        log.debug("实际签名: {}", actualSign);

        // ...
    }
}
```

### 8.3 性能优化问题

#### Q6：如何优化跨数据库查询性能？

```java
// 方案1：批量查询，避免N+1问题
public List<OrderDTO> getAllOrdersOptimized() {
    // 一次查询所有订单
    List<Order> orders = orderRepository.findAll();

    // 提取所有用户ID
    Set<Long> userIds = orders.stream()
        .map(Order::getUserId)
        .collect(Collectors.toSet());

    // 一次查询所有用户（而不是循环查询）
    Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
        .collect(Collectors.toMap(User::getId, Function.identity()));

    // 组合数据
    return orders.stream()
        .map(order -> OrderMapper.toDTO(order, userMap.get(order.getUserId())))
        .collect(Collectors.toList());
}

// 方案2：使用Redis缓存用户数据
@Service
public class UserService {

    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
    }
}
```

#### Q7：如何配置数据库连接池？

```yaml
spring:
  datasource:
    user:
      hikari:
        maximum-pool-size: 20        # 最大连接数
        minimum-idle: 5              # 最小空闲连接数
        connection-timeout: 30000    # 连接超时时间（毫秒）
        idle-timeout: 600000         # 空闲超时时间（10分钟）
        max-lifetime: 1800000        # 连接最大生命周期（30分钟）
        connection-test-query: SELECT 1  # 连接测试查询
```

---

## 九、项目扩展指南

### 9.1 添加Redis缓存

```java
// 1. 添加依赖（已包含）
// spring-boot-starter-data-redis

// 2. 配置Redis
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))  // 10分钟过期
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .build();
    }
}

// 3. 使用缓存
@Service
public class OrderService {

    @Cacheable(value = "orders", key = "#id")
    public OrderDTO getOrderById(Long id) {
        // 方法执行结果会被缓存
        return orderRepository.findById(id)
            .map(OrderMapper::toDTO)
            .orElseThrow(() -> new ResourceNotFoundException("订单不存在"));
    }

    @CacheEvict(value = "orders", key = "#id")
    public void deleteOrder(Long id) {
        // 删除订单时清除缓存
        orderRepository.deleteById(id);
    }
}
```

### 9.2 集成RocketMQ消息队列

```java
// 1. 添加依赖（已包含）
// rocketmq-spring-boot-starter

// 2. 发送消息
@Service
public class OrderService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public OrderDTO createOrder(CreateOrderRequest request) {
        Order order = Order.create(request.getUserId(), request.getTotalAmount());
        Order savedOrder = orderRepository.save(order);

        // 发送订单创建事件
        OrderCreatedEvent event = new OrderCreatedEvent(
            savedOrder.getId(),
            savedOrder.getUserId(),
            savedOrder.getTotalAmount()
        );
        rocketMQTemplate.convertAndSend("order-topic:created", event);

        return OrderMapper.toDTO(savedOrder);
    }
}

// 3. 消费消息
@Component
@RocketMQMessageListener(
    topic = "order-topic",
    selectorExpression = "created",
    consumerGroup = "order-consumer-group"
)
public class OrderCreatedListener implements RocketMQListener<OrderCreatedEvent> {

    @Override
    public void onMessage(OrderCreatedEvent event) {
        log.info("收到订单创建事件: {}", event);

        // 处理业务逻辑（如更新库存、发送通知等）
        // ...
    }
}
```

### 9.3 添加定时任务

```java
@Configuration
@EnableScheduling
public class ScheduleConfig {
}

@Component
public class OrderScheduledTasks {

    @Autowired
    private OrderService orderService;

    // 每天凌晨1点执行
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoCompleteOrders() {
        log.info("开始自动完成已支付超过7天的订单");

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Order> orders = orderRepository.findPaidOrdersBefore(sevenDaysAgo);

        for (Order order : orders) {
            try {
                order.complete();
                orderRepository.save(order);
                log.info("自动完成订单: {}", order.getOrderNo());
            } catch (Exception e) {
                log.error("自动完成订单失败: {}", order.getOrderNo(), e);
            }
        }
    }

    // 每5分钟执行一次
    @Scheduled(fixedRate = 300000)
    public void cancelExpiredOrders() {
        log.info("开始取消超时未支付订单");

        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        List<Order> orders = orderRepository.findPendingOrdersBefore(thirtyMinutesAgo);

        for (Order order : orders) {
            try {
                order.cancel();
                orderRepository.save(order);
                log.info("自动取消订单: {}", order.getOrderNo());
            } catch (Exception e) {
                log.error("自动取消订单失败: {}", order.getOrderNo(), e);
            }
        }
    }
}
```

---

## 十、部署指南

### 10.1 打包应用

```bash
# 开发环境打包
./mvnw clean package -Pdev

# 生产环境打包
./mvnw clean package -Pprod

# 跳过测试打包
./mvnw clean package -DskipTests

# 生成的jar文件位置
ls -lh target/springboot4ddd-0.0.1-SNAPSHOT.jar
```

### 10.2 Docker部署

创建 `Dockerfile`：

```dockerfile
# 使用Java 21基础镜像
FROM eclipse-temurin:21-jre-alpine

# 设置工作目录
WORKDIR /app

# 复制jar文件
COPY target/springboot4ddd-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 设置JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

创建 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  # MySQL（用户数据）
  mysql:
    image: mysql:8.0
    container_name: springboot4ddd-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: frog
      MYSQL_USER: frog_admin
      MYSQL_PASSWORD: frog_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./src/main/resources/db/mysql/init_users.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - app-network

  # PostgreSQL（订单数据）
  postgres:
    image: postgres:15
    container_name: springboot4ddd-postgres
    environment:
      POSTGRES_PASSWORD: postgres_password
      POSTGRES_DB: seed
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./src/main/resources/db/postgresql/init_orders.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - app-network

  # Redis
  redis:
    image: redis:7-alpine
    container_name: springboot4ddd-redis
    ports:
      - "6379:6379"
    networks:
      - app-network

  # 应用
  app:
    build: .
    container_name: springboot4ddd-app
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_USER_JDBCURL: jdbc:mysql://mysql:3306/frog
      SPRING_DATASOURCE_USER_USERNAME: frog_admin
      SPRING_DATASOURCE_USER_PASSWORD: frog_password
      SPRING_DATASOURCE_ORDER_JDBCURL: jdbc:postgresql://postgres:5432/seed
      SPRING_DATASOURCE_ORDER_USERNAME: postgres
      SPRING_DATASOURCE_ORDER_PASSWORD: postgres_password
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PORT: 6379
    depends_on:
      - mysql
      - postgres
      - redis
    networks:
      - app-network

volumes:
  mysql_data:
  postgres_data:

networks:
  app-network:
    driver: bridge
```

部署命令：

```bash
# 构建镜像
docker-compose build

# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f app

# 停止服务
docker-compose down

# 停止并删除数据卷
docker-compose down -v
```

### 10.3 生产环境配置建议

```yaml
# application-prod.yaml
spring:
  datasource:
    user:
      jdbc-url: ${MYSQL_URL:jdbc:mysql://mysql-host:3306/frog}
      username: ${MYSQL_USER:frog_admin}
      password: ${MYSQL_PASSWORD}  # 从环境变量读取
      hikari:
        maximum-pool-size: 50
        minimum-idle: 10

    order:
      jdbc-url: ${POSTGRES_URL:jdbc:postgresql://postgres-host:5432/seed}
      username: ${POSTGRES_USER:postgres}
      password: ${POSTGRES_PASSWORD}  # 从环境变量读取
      hikari:
        maximum-pool-size: 50
        minimum-idle: 10

  data:
    redis:
      host: ${REDIS_HOST:redis-host}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD}  # 从环境变量读取

# 日志配置
logging:
  level:
    root: INFO
    com.github.microwind.springboot4ddd: INFO
  file:
    name: /var/log/springboot4ddd/app.log
    max-size: 100MB
    max-history: 30

# 签名配置
sign:
  signature:
    ttl: 300000  # 生产环境缩短为5分钟
```

启动命令：

```bash
# 使用环境变量启动
java -jar \
  -Dspring.profiles.active=prod \
  -DMYSQL_PASSWORD=secure_password \
  -DPOSTGRES_PASSWORD=secure_password \
  -DREDIS_PASSWORD=secure_password \
  target/springboot4ddd-0.0.1-SNAPSHOT.jar
```

---

## 十一、总结

Springboot4DDD是一个完整的、生产就绪的DDD脚手架，它具有以下核心价值：

### 11.1 核心亮点

1. **严格的DDD分层架构**
   - 清晰的四层架构：领域层、应用层、基础设施层、接口层
   - 业务逻辑内聚在领域对象中
   - 依赖方向正确，易于维护和测试

2. **多数据源架构**
   - 支持MySQL + PostgreSQL双数据源
   - 展示两种持久化策略（JdbcTemplate vs Spring Data JDBC）
   - 完整的跨数据库查询实现

3. **API安全认证**
   - 基于SHA-256的签名验证
   - 支持含参数/不含参数两种签名模式
   - 灵活的权限配置

4. **生产就绪特性**
   - 统一响应格式
   - 全局异常处理
   - 请求参数校验
   - 事务管理
   - 完善的日志记录

5. **现代技术栈**
   - Spring Boot 4.0.1
   - Java 21
   - 最新的开发实践

### 11.2 适用场景

| 场景 | 说明 |
|------|------|
| **DDD学习** | 完整展示DDD的实现方式 |
| **企业级项目** | 可直接用于生产环境 |
| **多数据源项目** | 提供多数据源最佳实践 |
| **API服务开发** | 内置安全认证机制 |
| **快速原型** | 基于脚手架快速开发 |

### 11.3 下一步学习

1. **深入DDD**
   - 学习聚合根设计
   - 理解限界上下文
   - 掌握领域事件

2. **性能优化**
   - 使用Redis缓存
   - 优化数据库查询
   - 异步处理

3. **微服务化**
   - 服务拆分策略
   - 服务间通信
   - 分布式事务

4. **监控告警**
   - 集成Spring Boot Actuator
   - 接入Prometheus + Grafana
   - 日志聚合（ELK）

### 11.4 获取源码

完整代码已开源，地址：

```
https://github.com/microwind/design-patterns/tree/main/practice-projects/springboot4ddd
```

**文档列表**：
- `README.md`：项目概述和快速开始
- `TUTORIAL.md`：本教程文档
- `DATABASE.md`：多数据源配置详解
- `SIGN_GUIDE.md`：API签名验证使用指南

### 11.5 社区与反馈

如果你在使用过程中遇到问题，或有任何建议：

- **GitHub Issues**：https://github.com/microwind/design-patterns/issues
- **wx**：springbuild

---

## 参考资料

- [Domain-Driven Design: Tackling Complexity in the Heart of Software](https://www.amazon.com/Domain-Driven-Design-Tackling-Complexity-Software/dp/0321125215) - Eric Evans
- [Implementing Domain-Driven Design](https://www.amazon.com/Implementing-Domain-Driven-Design-Vaughn-Vernon/dp/0321834577) - Vaughn Vernon
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JDBC Reference](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/)
- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)
