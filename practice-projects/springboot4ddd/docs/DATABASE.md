# 数据库配置说明

## 数据库架构

本项目使用**多数据源架构**，分别使用 MySQL 和 PostgreSQL：

### 1. MySQL - 用户数据库 (users)
- **数据库名称**: `frog`
- **表**: `users`
- **端口**: `3306`
- **用途**: 存储用户信息
- **访问方式**: JdbcTemplate (`userJdbcTemplate`)

### 2. PostgreSQL - 订单数据库 (orders)
- **数据库名称**: `seed`
- **表**: `orders`
- **端口**: `5432`
- **用途**: 存储订单信息
- **访问方式**: Spring Data JDBC (`OrderJdbcRepository`)

## 多数据源配置

### DataSourceConfig.java
配置两个独立的数据源及其对应的 JdbcTemplate 和事务管理器：

```java
// MySQL 数据源 - users
@Bean(name = "userDataSource")
public DataSource userDataSource() { ... }

@Bean(name = "userJdbcTemplate")
public JdbcTemplate userJdbcTemplate(@Qualifier("userDataSource") DataSource dataSource) { ... }

@Bean(name = "userTransactionManager")
public PlatformTransactionManager userTransactionManager(@Qualifier("userDataSource") DataSource dataSource) { ... }

// PostgreSQL 数据源 - orders
@Bean(name = "orderDataSource")
@Primary  // Spring Data JDBC 使用此数据源
public DataSource orderDataSource() { ... }

@Bean(name = "orderTransactionManager")
@Primary
public PlatformTransactionManager orderTransactionManager(@Qualifier("orderDataSource") DataSource dataSource) { ... }
```

### OrderJdbcConfig.java
配置 Spring Data JDBC 使用 PostgreSQL 数据源：

```java
@Configuration
@EnableJdbcRepositories(
    basePackages = "com.github.microwind.springboot4ddd.infrastructure.repository.jdbc"
)
public class OrderJdbcConfig extends AbstractJdbcConfiguration {

    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations() {
        return new NamedParameterJdbcTemplate(orderDataSource);
    }
}
```

**关键点**: 只扫描 `infrastructure.repository.jdbc` 包，不包含 `user` 和 `order` 子包，避免误扫描。

## 字段命名规范

为了保持代码的一致性和可读性，项目采用统一的命名规范：

### Java 代码 (驼峰命名)
```java
// User 模型
private LocalDateTime createdTime;
private LocalDateTime updatedTime;

// Order 模型
private LocalDateTime createdTime;  // 映射到 created_at
private LocalDateTime updatedTime;  // 映射到 updated_time
```

### 数据库字段 (下划线命名)
```sql
-- MySQL users 表
created_time TIMESTAMP
updated_time TIMESTAMP

-- PostgreSQL orders 表 (注意: created_at 不是 created_time)
created_at TIMESTAMP
updated_time TIMESTAMP
```

**注意**: orders 表使用 `created_at` 而不是 `created_time`，这与 users 表不同。

## Order 模型的映射配置

Order 模型使用 **Spring Data JDBC**，通过 `@Column` 注解实现字段映射：

```java
@Data
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

    private String status;

    @Column("created_at")        // 注意: created_at
    private LocalDateTime createdTime;

    @Column("updated_time")
    private LocalDateTime updatedTime;
}
```

## User 模型的映射配置

User 模型使用 **JdbcTemplate**，通过 `RowMapper` 手动映射：

```java
private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> User.builder()
    .id(rs.getLong("id"))
    .username(rs.getString("username"))
    .email(rs.getString("email"))
    .phone(rs.getString("phone"))
    .nickname(rs.getString("nickname"))
    .status(rs.getInt("status"))
    .createdTime(rs.getTimestamp("created_time").toLocalDateTime())
    .updatedTime(rs.getTimestamp("updated_time").toLocalDateTime())
    .build();
```

## 数据库初始化

### MySQL 初始化
```bash
# 执行初始化脚本
mysql -u root -p < src/main/resources/db/mysql/init_users.sql
```

### PostgreSQL 初始化
```bash
# 创建数据库
psql -U postgres -c "CREATE DATABASE seed;"

# 执行初始化脚本
psql -U postgres -d seed -f src/main/resources/db/postgresql/init_orders.sql
```

## 配置文件

### application-dev.yaml
```yaml
# MySQL - User database
user:
  datasource:
    jdbc-url: jdbc:mysql://localhost:3306/frog?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
    username: frog_admin
    password: frog798

# PostgreSQL - Order database
order:
  datasource:
    jdbc-url: jdbc:postgresql://localhost:5432/seed
    username: postgres
    password: lego798
```

## 注意事项

1. **数据源路由关键配置**:
   - **User 相关操作**: 使用 `@Qualifier("userJdbcTemplate")` 注入的 JdbcTemplate，连接到 MySQL
   - **Order 相关操作**: 通过 Spring Data JDBC 自动路由到 `@Primary` 标记的 `orderDataSource`（PostgreSQL）
   - **重要**: `orderDataSource` 必须标记 `@Primary`，Spring Data JDBC 才能正确使用 PostgreSQL

2. **时间字段映射差异**:
   - **users 表 (MySQL)**: `created_time`, `updated_time`
   - **orders 表 (PostgreSQL)**: `created_at`, `updated_time`
   - Java 代码统一使用: `createdTime`, `updatedTime`

3. **字段映射总结**:
   ```
   Java            MySQL (users)      PostgreSQL (orders)
   ─────────────   ────────────────   ──────────────────
   createdTime  →  created_time       created_at
   updatedTime  →  updated_time       updated_time
   ```

4. **数据库字段映射方式**:
   - **User** (JdbcTemplate):
     - 使用 `RowMapper` 手动映射
     - 通过 `@Qualifier("userJdbcTemplate")` 指定数据源
   - **Order** (Spring Data JDBC):
     - 使用 `@Column` 注解自动映射
     - 通过 `OrderJdbcConfig` 配置使用 PostgreSQL

5. **数据库驱动**:
   - MySQL: `com.mysql.cj.jdbc.Driver`
   - PostgreSQL: `org.postgresql.Driver`

6. **事务管理**:
   - User: 使用 `@Transactional(transactionManager = "userTransactionManager")`
   - Order: Spring Data JDBC 自动使用 `orderTransactionManager`

## 测试数据

### users 表
- user1 (启用)
- user2 (启用)
- user3 (禁用)

### orders 表
- ORD1000000001 (PENDING)
- ORD1000000002 (PAID)
- ORD1000000003 (COMPLETED)
