# 多数据源配置故障排查

## 问题描述
```
Caused by: org.postgresql.util.PSQLException: ERROR: relation "users" does not exist
```

## 问题原因

在多数据源环境下，Spring Data JDBC 会扫描指定包下的所有 Repository。关键问题：

1. **错误的扫描范围**: `@EnableJdbcRepositories` 扫描了 `infrastructure.repository`，包括所有子包
2. **路由混乱**: User 相关操作被 Spring Data JDBC 扫描，错误地连接到了 PostgreSQL
3. **PostgreSQL 中没有 users 表**，导致报错

## 解决方案

### 1. 调整包结构 ✅

将 `OrderJdbcRepository` 移到独立的包中，避免扫描到其他 Repository：

```
infrastructure/repository/
├── jdbc/
│   └── OrderJdbcRepository.java    ← Spring Data JDBC (PostgreSQL)
├── order/
│   └── OrderRepositoryImpl.java    ← 适配器
└── user/
    └── UserRepositoryImpl.java     ← JdbcTemplate (MySQL)
```

### 2. 更新 OrderJdbcConfig 扫描路径 ✅

**文件**: `infrastructure/config/OrderJdbcConfig.java`

```java
@Configuration
@EnableJdbcRepositories(
    // 关键：只扫描 jdbc 包，不包含 user 和 order 子包
    basePackages = "com.github.microwind.springboot4ddd.infrastructure.repository.jdbc"
)
public class OrderJdbcConfig extends AbstractJdbcConfiguration {

    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations() {
        return new NamedParameterJdbcTemplate(orderDataSource);
    }
}
```

### 3. 更新 import 引用 ✅

**文件**: `infrastructure/repository/order/OrderRepositoryImpl.java`

```java
import com.github.microwind.springboot4ddd.infrastructure.repository.jdbc.OrderJdbcRepository;
```

```java
@Bean(name = "orderDataSource")
@Primary  // ← 关键：Spring Data JDBC 需要这个标记
public DataSource orderDataSource() {
    return DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .build();
}
```

### 3. UserRepositoryImpl 使用 @Qualifier 注入

```java
@Repository
public class UserRepositoryImpl implements UserRepository {

    @Qualifier("userJdbcTemplate")  // ← 关键：明确指定使用 MySQL
    private final JdbcTemplate jdbcTemplate;

    // ...
}
```

## 数据源路由总结

| 组件 | 数据库 | 路由方式 |
|-----|-------|---------|
| UserRepositoryImpl | MySQL | `@Qualifier("userJdbcTemplate")` 明确指定 |
| OrderJdbcRepository | PostgreSQL | Spring Data JDBC 自动使用 `@Primary` 数据源 |

## 配置检查清单

- [x] `orderDataSource` 标记 `@Primary`
- [x] `orderTransactionManager` 标记 `@Primary`
- [x] 创建 `OrderJdbcConfig` 配置 Spring Data JDBC
- [x] `UserRepositoryImpl` 使用 `@Qualifier("userJdbcTemplate")`
- [x] `UserRepositoryImpl` 事务使用 `@Transactional(transactionManager = "userTransactionManager")`

## 验证步骤

1. **编译检查**
   ```bash
   ./mvnw clean compile -DskipTests
   ```

2. **启动应用检查日志**
   ```bash
   ./mvnw spring-boot:run
   ```

   应该看到：
   ```
   HikariPool-1 - Starting... (userDataSource - MySQL)
   HikariPool-2 - Starting... (orderDataSource - PostgreSQL)
   ```

3. **测试 API**
   ```bash
   # 测试 User API（应该访问 MySQL）
   curl http://localhost:8080/api/users

   # 测试 Order API（应该访问 PostgreSQL）
   curl http://localhost:8080/api/order/list
   ```

## 常见错误

### 错误 1: 找不到 users 表（在 PostgreSQL 中）
**原因**: Spring Data JDBC 使用了错误的数据源
**解决**: 确保 `orderDataSource` 标记 `@Primary`

### 错误 2: 找不到 orders 表（在 MySQL 中）
**原因**: User Repository 错误使用了 orderDataSource
**解决**: 确保 UserRepositoryImpl 使用 `@Qualifier("userJdbcTemplate")`

### 错误 3: 循环依赖
**原因**: 多个 @Primary 配置冲突
**解决**: 只在 orderDataSource 和 orderTransactionManager 上使用 @Primary

## 架构图

```
┌─────────────────────────────────────────────────────────┐
│                    Application                          │
└─────────────────────────────────────────────────────────┘
                          │
        ┌─────────────────┴─────────────────┐
        │                                   │
        ▼                                   ▼
┌───────────────────┐              ┌───────────────────┐
│ UserRepositoryImpl│              │OrderJdbcRepository│
│  (JdbcTemplate)   │              │ (Spring Data JDBC)│
└───────────────────┘              └───────────────────┘
        │                                   │
        │ @Qualifier                        │ @Primary
        │ ("userJdbcTemplate")              │
        ▼                                   ▼
┌───────────────────┐              ┌───────────────────┐
│  userDataSource   │              │ orderDataSource   │
│     (MySQL)       │              │   (PostgreSQL)    │
└───────────────────┘              └───────────────────┘
        │                                   │
        ▼                                   ▼
┌───────────────────┐              ┌───────────────────┐
│   MySQL:3306      │              │ PostgreSQL:5432   │
│   Database: frog  │              │  Database: seed   │
│   Table: users    │              │  Table: orders    │
└───────────────────┘              └───────────────────┘
```
