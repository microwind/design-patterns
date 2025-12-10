# 双数据源配置说明

本项目已成功配置双MySQL数据源，分别用于Order和User数据。

## 配置结构

### 1. 数据源配置类

#### OrderDataSourceConfig (主数据源)
- **位置**: `src/main/java/com/microwind/knife/infrastructure/configuration/OrderDataSourceConfig.java`
- **数据源Bean**: `orderDataSource` (@Primary)
- **EntityManagerFactory**: `orderEntityManagerFactory`
- **事务管理器**: `orderTransactionManager`
- **JdbcTemplate**: `orderJdbcTemplate` (@Primary)
- **管理实体**: `com.microwind.knife.domain.order` (Order, OrderItem)
- **JPA Repository包**: `com.microwind.knife.domain.repository.order`

#### UserDataSourceConfig (次数据源)
- **位置**: `src/main/java/com/microwind/knife/infrastructure/configuration/UserDataSourceConfig.java`
- **数据源Bean**: `userDataSource`
- **EntityManagerFactory**: `userEntityManagerFactory`
- **事务管理器**: `userTransactionManager`
- **JdbcTemplate**: `userJdbcTemplate`
- **管理实体**: `com.microwind.knife.domain.user` (User)
- **JPA Repository包**: `com.microwind.knife.domain.repository.user`

### 2. 配置文件

#### application-dev.yml
```yaml
# Order数据源配置
order:
  datasource:
    jdbc-url: jdbc:mysql://localhost:3306/order_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
    username: order_admin
    password: order798
    driver-class-name: com.mysql.cj.jdbc.Driver

# User数据源配置
user:
  datasource:
    jdbc-url: jdbc:mysql://localhost:3306/frog?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
    username: frog_admin
    password: lego798
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 3. Repository实现

#### JdbcTemplate方式
- **OrderRepositoryImpl**: 注入`@Qualifier("orderJdbcTemplate")` → 使用order_db
- **UserRepositoryImpl**: 注入`@Qualifier("userJdbcTemplate")` → 使用frog
- **AppAuthRepositoryImpl**: 注入`@Qualifier("userJdbcTemplate")` → 使用frog

#### JPA方式 (按包路径自动识别)
- **OrderJpaRepository** (在`domain.repository.order`包) → 使用order_db
- **UserJpaRepository** (在`domain.repository.user`包，将来创建) → 使用frog

## 数据源路由规则

```
数据库         数据源             实体                 Repository                           事务管理器
──────────────────────────────────────────────────────────────────────────────────────────────────────────
order_db  →  orderDataSource  →  Order/OrderItem  →  OrderRepositoryImpl (JdbcTemplate)  →  orderTransactionManager
                                                      OrderJpaRepository (JPA)

frog      →  userDataSource   →  User             →  UserRepositoryImpl (JdbcTemplate)   →  userTransactionManager
                                                      AppAuthRepositoryImpl (JdbcTemplate)
                                                      UserJpaRepository (JPA,将来)
```

## 启动日志验证

启动成功时会看到：
```
INFO  com.zaxxer.hikari.HikariDataSource - OrderDB-Pool - Starting...
INFO  com.zaxxer.hikari.HikariDataSource - OrderDB-Pool - Start completed.
INFO  com.zaxxer.hikari.HikariDataSource - UserDB-Pool - Starting...
INFO  com.zaxxer.hikari.HikariDataSource - UserDB-Pool - Start completed.
initialize OrderRepositoryImpl with orderDataSource: ...
initialize UserRepositoryImpl with userDataSource: ...
initialize AppAuthRepositoryImpl with userDataSource: ...
```

## 注意事项

1. **Application.java** 已禁用自动配置:
   - `DataSourceAutoConfiguration`
   - `HibernateJpaAutoConfiguration`
   - `SqlInitializationAutoConfiguration`

2. **JPA Repository位置**:
   - Order相关: 必须在 `com.microwind.knife.domain.repository.order` 包
   - User相关: 必须在 `com.microwind.knife.domain.repository.user` 包

3. **连接池配置**:
   - OrderDB-Pool: max=20, min=5
   - UserDB-Pool: max=10, min=3

4. **事务注解使用**:
   ```java
   // Order事务
   @Transactional("orderTransactionManager")
   public void someMethod() { }

   // User事务
   @Transactional("userTransactionManager")
   public void anotherMethod() { }
   ```

## 如何添加新的Repository

### JdbcTemplate方式
```java
@Repository
public class NewRepositoryImpl {
    private final JdbcTemplate jdbcTemplate;

    public NewRepositoryImpl(@Qualifier("orderJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
```

### JPA方式
```java
// 在 com.microwind.knife.domain.repository.order 包下创建
public interface NewJpaRepository extends JpaRepository<Order, Long> {
    // 自动使用 orderEntityManagerFactory 和 orderTransactionManager
}
```
