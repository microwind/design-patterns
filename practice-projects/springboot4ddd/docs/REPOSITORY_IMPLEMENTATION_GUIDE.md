# 订单数据访问实现方式切换指南

## 概述

本项目实现了订单数据访问的灵活切换机制，支持在 **Spring Data JDBC** 和 **MyBatis Plus** 两种实现方式之间随意切换，无需修改任何业务代码。

## 架构设计

### 适配器模式应用

- **OrderRepository**：领域层仓储接口，定义所有数据访问契约
- **OrderRepositoryImpl**：基础设施层的适配器实现，统一封装两种数据访问方式
- **OrderJdbcRepository**：Spring Data JDBC 的底层实现
- **OrderMybatisPlusRepository**：MyBatis Plus 的底层实现

```
OrderRepository (领域层接口)
       ↑
       |
OrderRepositoryImpl (适配器 - 基础设施层)
       |
   ┌───┴───┐
   |       |
JDBC   MyBatis Plus
```

## 切换方法

### 方式一：配置文件切换（推荐）

编辑 `application.yaml` 文件：

```yaml
order:
  repository:
    implementation: jdbc  # 使用 JDBC（默认）
    # 或改为：implementation: mybatis-plus  # 使用 MyBatis Plus
```

### 方式二：环境变量切换

```bash
export ORDER_REPOSITORY_IMPLEMENTATION=mybatis-plus
java -jar app.jar
```

### 方式三：启动参数切换

```bash
java -jar app.jar --order.repository.implementation=mybatis-plus
```

### 方式四：不同环境配置

创建不同的 Spring Profile 配置：

**application-dev.yaml**（开发环境用 JDBC）：
```yaml
order:
  repository:
    implementation: jdbc
```

**application-prod.yaml**（生产环境用 MyBatis Plus）：
```yaml
order:
  repository:
    implementation: mybatis-plus
```

启动时指定：
```bash
java -jar app.jar --spring.profiles.active=prod
```

## 实现细节

### OrderRepositoryImpl 的核心逻辑

```java
private boolean isUsingMybatisPlus() {
    return "mybatis-plus".equalsIgnoreCase(implementationType);
}

@Override
public Order save(Order order) {
    if (isUsingMybatisPlus()) {
        orderMybatisPlusRepository.insert(order);
        return order;
    }
    return orderJdbcRepository.save(order);
}
```

每个方法都根据配置动态选择使用哪种实现：

| 方法 | JDBC | MyBatis Plus |
|------|------|-------------|
| `save()` | `orderJdbcRepository.save()` | `orderMybatisPlusRepository.insert()` |
| `findById()` | `orderJdbcRepository.findById()` | `orderMybatisPlusRepository.selectById()` |
| `findByOrderNo()` | `orderJdbcRepository.findByOrderNo()` | `orderMybatisPlusRepository.findByOrderNo()` |
| `findByUserId()` | `orderJdbcRepository.findByUserId()` | `orderMybatisPlusRepository.findByUserId()` |
| `findAll()` | `orderJdbcRepository.findAll()` | `orderMybatisPlusRepository.selectList()` |
| `deleteById()` | `orderJdbcRepository.deleteById()` | `orderMybatisPlusRepository.deleteById()` |

## 依赖配置

### pom.xml 中添加了

```xml
<!-- MyBatis Plus -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.7</version>
</dependency>
```

### MyBatis Plus 配置

在 `MybatisPlusConfig` 中配置了分页插件等支持：

```java
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

### 启动类配置

在 `Application.java` 中添加了 Mapper 扫描：

```java
@MapperScan("com.github.microwind.springboot4ddd.infrastructure.repository.mybatisplus")
public class Application { }
```

## 优势

1. **灵活切换**：无需修改任何业务代码，仅修改配置即可切换数据访问方式
2. **适配器模式**：完全解耦业务层与基础设施层的依赖
3. **渐进式迁移**：支持在两种实现间逐步迁移
4. **易于测试**：可以为不同实现方式编写专门的单元测试
5. **性能优化**：可根据不同场景选择最优的数据访问方式

## 使用建议

- **开发阶段**：使用 JDBC，更直观、调试更容易
- **生产环境**：根据性能测试结果选择，MyBatis Plus 有更多缓存和优化选项
- **并发高频场景**：MyBatis Plus 的缓存机制可能更优
- **简单查询场景**：JDBC 的开销更小

## 注意事项

1. 两种实现都应该遵循同样的数据库 Schema
2. 自定义查询时，需要在两个 Repository 中都实现
3. 切换实现方式时，建议进行充分的测试
4. 事务管理由 Service 层负责，Repository 层只做数据访问
