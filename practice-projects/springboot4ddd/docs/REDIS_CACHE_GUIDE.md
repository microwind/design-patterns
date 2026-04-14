# Redis 缓存使用指南

## 概述

本示例演示 Spring Boot + DDD 项目中 Redis 缓存的典型用法：**查询优先走缓存，未命中则穿透到数据库，写入后清除缓存**。

## 配置

### 1. application.yml

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
      timeout: 3000ms
```

### 2. RedisConfig

配置 `RedisTemplate`，Key 用 String 序列化，Value 用 JSON 序列化，并集中定义缓存常量：

```java
@Configuration
public class RedisConfig {

    public static final String USER_CACHE_PREFIX = "user:";
    public static final String ORDER_CACHE_PREFIX = "order:";
    public static final long USER_CACHE_TTL = 1800;  // 30分钟
    public static final long ORDER_CACHE_TTL = 900;   // 15分钟

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}
```

## 核心：SimpleCacheService

只有两个方法，覆盖读写场景：

```java
@Service
public class SimpleCacheService {

    // 查缓存 → 未命中则调 supplier 加载 → 回填缓存
    public <T> T getOrSet(String key, Class<T> clazz, Duration ttl, Supplier<T> supplier);

    // 删除缓存（写入/更新后调用）
    public void delete(String key);
}
```

Redis 故障时自动降级，直接走数据源，不影响业务。

## 业务集成

### UserService — 用户模块

```java
// 查询：优先走缓存
public UserResponse getUserById(Long id) {
    return simpleCacheService.getOrSet(
        RedisConfig.USER_CACHE_PREFIX + id,
        UserResponse.class,
        Duration.ofSeconds(RedisConfig.USER_CACHE_TTL),
        () -> {
            User user = userRepository.findById(id).orElseThrow();
            return toResponse(user);
        }
    );
}

// 更新：写后清缓存
public UserResponse updateUser(Long id, UpdateUserRequest request) {
    // ... 更新逻辑 ...
    simpleCacheService.delete(RedisConfig.USER_CACHE_PREFIX + id);
    return toResponse(updatedUser);
}
```

### OrderService — 订单模块

```java
// 按ID查询：优先走缓存
public OrderResponse getOrderDetail(Long id) {
    return simpleCacheService.getOrSet(
        RedisConfig.ORDER_CACHE_PREFIX + "detail:" + id,
        OrderResponse.class,
        Duration.ofSeconds(RedisConfig.ORDER_CACHE_TTL),
        () -> {
            Order order = orderRepository.findById(id).orElseThrow();
            return orderMapper.toOrderResponse(order);
        }
    );
}

// 按订单号查询：优先走缓存
public OrderDTO getOrderByNo(String orderNo) {
    return simpleCacheService.getOrSet(
        RedisConfig.ORDER_CACHE_PREFIX + "no:" + orderNo,
        OrderDTO.class,
        Duration.ofSeconds(RedisConfig.ORDER_CACHE_TTL),
        () -> orderRepository.findByOrderNo(orderNo).map(orderMapper::toDTO).orElseThrow()
    );
}

// 状态变更（取消/支付/完成）：写后清缓存
simpleCacheService.delete(RedisConfig.ORDER_CACHE_PREFIX + "id:" + order.getId());
simpleCacheService.delete(RedisConfig.ORDER_CACHE_PREFIX + "detail:" + order.getId());
simpleCacheService.delete(RedisConfig.ORDER_CACHE_PREFIX + "no:" + order.getOrderNo());
```

## 调用链路

```
Controller                    Service                     SimpleCacheService       Redis / DB
GET /api/users/{id}    →  UserService.getUserById()   →  getOrSet("user:1")  →  命中返回 / 未命中查DB回填
PUT /api/users/{id}    →  UserService.updateUser()    →  delete("user:1")    →  清除缓存
GET /api/orders/{id}   →  OrderService.getOrderDetail() → getOrSet("order:detail:1") → 命中返回 / 未命中查DB回填
POST /api/orders/{id}/pay → OrderService.payOrder()   →  delete("order:*")  →  清除缓存
```

## 文件清单

```
infrastructure/config/RedisConfig.java        # Redis 配置 + 缓存常量
infrastructure/cache/SimpleCacheService.java   # 缓存服务（getOrSet + delete）
application/service/user/UserService.java      # 用户服务（集成缓存）
application/service/order/OrderService.java    # 订单服务（集成缓存）
interfaces/controller/user/UserController.java # 用户 API 入口
interfaces/controller/order/OrderController.java # 订单 API 入口
```

## 测试

```bash
mvn test -Dtest=SimpleCacheServiceTest
```

测试覆盖：缓存命中、缓存未命中回填、数据源返回null不缓存、Redis故障降级。
