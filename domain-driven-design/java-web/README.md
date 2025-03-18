## Java语言`DDD`目录结构
```bash
java-web/
│── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com
│   │   │   │   └── microwind                      # 组织名
│   │   │   │   │   └── javaweborder               # 项目名
│   │   │   │   │        ├── application/          # 应用层（协调领域逻辑，处理业务用例）
│   │   │   │   │        │   ├── services/         # 服务层，业务逻辑目录
│   │   │   │   │        │   │   └── OrderService.java   # 订单应用服务
│   │   │   │   │        │   └── dto/              # 数据传输对象（DTO）
│   │   │   │   │        │   │   └── OrderDTO.java # 订单数据交换对象
│   │   │   │   │        ├── domain/               # 领域层（核心业务逻辑和接口定义）
│   │   │   │   │        │   └── order/            # 订单聚合（聚合根和业务逻辑）
│   │   │   │   │        │   │   ├── Order.java    # 订单实体（聚合根），包含核心业务逻辑
│   │   │   │   |        │   │   ├── OrderItem.java       # [可选]订单项实体（子聚合）
│   │   │   │   │        │   │   └── OrderEvent.java   # 订单领域事件
│   │   │   │   │        │   └── repository/           # 仓库接口
│   │   │   │   │        │   │   ├── Repository.java    # 仓库通用接口
│   │   │   │   │        │   │   ├── OrderRepository.java # 订单仓储接口，定义对订单数据的操作
│   │   │   │   │        ├── infrastructure/       # 基础设施层（实现领域层定义的接口）
│   │   │   │   │        │   ├── repository/       # 仓储实现
│   │   │   │   │        │   │   ├── OrderRepositoryImpl.java # 订单仓储实现
│   │   │   │   │        │   ├── messaging/        # 消息队列实现
│   │   │   │   │        │   └── configuration/    # 基础配置（与外部系统相关）
│   │   │   │   │        │   │   ├── DatabaseConfig.java  # 数据库配置
│   │   │   │   │        │   │   ├── JWTConfig.java       # JWT 配置
│   │   │   │   │        ├── interfaces/           # 接口层（处理外部请求，如HTTP接口）
│   │   │   │   │        │   ├── controllers/      # RESTful API接口
│   │   │   │   │        │   │   ├── OrderController.java  # 订单相关的HTTP接口
│   │   │   │   │        │   └── routes/           # 路由设置
│   │   │   │   │        │       ├── Router.java   # 基础路由工具设置
│   │   │   │   │        │       ├── OrderRoutes.java  # 订单路由配置
│   │   │   │   │        ├── middleware/           # 中间件（例如：鉴权、日志、拦截等）
│   │   │   │   │        │   └── LoggingFilter.java # 日志中间件，java通常使用Filter
│   │   │   │   │        └── config/               # 应用层配置（管理服务器和应用信息）
│   │   │   │   │        │   └── ServiceConfig.java  # 服务器与环境配置
│   │   │   │   │        └── utils/                # 实用工具
│   │   │   │   │        │   └── ResponseUtils.java # 响应包装处理工具
│   │   │   │   │        └── Application.java      # 应用启动类
│   │   │   │   │        └── TomcatServer.java     # [可选]嵌入式服务器
│   │   │   └── resources/
│   │   │   │   └── application.properties         # 配置文件
│   │   │   └── webapp/                            # [可选]web运行目录，可模拟
│   │   └── test/
│   │        └── java/
│   │            ├── com
│   │            │   └── javaweborder
│   │            │        ├── application/       # 应用层的测试
│   │            │        ├── interfaces/        # 接口层的测试
│── pom.xml                      # Maven 配置文件（如果使用 Maven）
│── build.gradle                 # Gradle 配置文件（如果使用 Gradle）
```

## 目录结构说明

### application
- service：封装应用业务逻辑，如订单创建、处理。
- dto：数据传输对象（DTO），用于应用层与外部传递数据。

### domain
- order：订单聚合（包含聚合根、实体、领域事件等）。
- repository：领域仓储接口，定义持久化操作。

### infrastructure
- repository：基础设施层的仓储实现。
- messaging：消息队列或事件流的集成。
- configuration：基础设施配置，如数据库连接、API 密钥等。

### interfaces
- controllers：RESTful API 层，用于处理外部请求。
- routes：路由配置，映射 API 路径和控制器。

## 运行
```bash
# 安装
$ mvn clean install -U
# 打包
$ mvn clean package -P prod -DskipTests
# 执行
$ java -jar target/java-web-order-1.0.0.jar
# 看到最后出现成功提示
The Server has started. please visit localhost:8080.
# 通过 http://localhost:8080 访问系统

# 测试用例
$ mvn test
# 单个测试
$ mvn surefire:test -Dtest=com.microwind.javaweborder.TomcatServerTest 
$ mvn surefire:test -Dtest=com.microwind.javaweborder.interfaces.routes.OrderRoutesTest
```

## Java 语言 DDD（领域驱动设计）特点
### 1. 关注领域模型
DDD 强调通过聚合（Aggregate）、实体（Entity）和值对象（Value Object）组织业务逻辑。

在 Java 中，使用 `class` 定义实体和值对象：
```java
// 实体（Entity）
public class Order {
    private int id;
    private String name;
    // getters and setters
}
```


### 2. 分层架构
DDD 通常采用 **分层架构**，通用项目结构如下：
- **领域层（Domain Layer）**：核心业务逻辑，如 `domain` 目录下的实体和聚合。
```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ElementCollection
    @CollectionTable(name = "order_items")
    private List<OrderItem> items; // 值对象
    
    // 核心业务方法
    public void addItem(Product product, int quantity) {
        this.items.add(new OrderItem(product, quantity));
    }
}
```
- **应用层（Application Layer）**：用例（Use Cases）和业务流程编排。
```java
@Service
@Transactional
@RequiredArgsConstructor
public class OrderAppService {
    private final OrderRepository orderRepository;
    
    public OrderDTO createOrder(OrderCreateCommand command) {
        Order order = new Order();
        command.getItems().forEach(item -> 
            order.addItem(item.getProduct(), item.getQuantity()));
        order = orderRepository.save(order);
        return OrderDTO.fromDomain(order);
    }
}
```
- **基础设施层（Infrastructure Layer）**：数据库、缓存、外部 API 适配等。
```java
// JPA 仓储实现（依赖 Spring Data JPA）
@Repository
@RequiredArgsConstructor
public class JpaOrderRepository implements OrderRepository {
    private final OrderJpaRepository jpaRepository;
    private final DomainEventPublisher eventPublisher;

    @Override
    public Order save(Order order) {
        Order savedOrder = jpaRepository.save(order);
        order.domainEvents().forEach(eventPublisher::publish);
        return savedOrder;
    }
}
```
- **接口层（Interface Layer）**：提供 HTTP、RPC 或 CLI 接口。
```java
// REST 控制器
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderAppService orderAppService;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderDTO dto = orderAppService.createOrder(request.toCommand());
        return ResponseEntity.created(URI.create("/orders/" + dto.getId())).body(dto);
    }
}
```

### 3. 依赖倒置（Dependency Inversion）
领域层不应直接依赖基础设施，而是通过 **接口（Interface）** 进行依赖倒置。例如：
OrderRepository 是领域层定义的接口，OrderRepositoryImpl 是基础设施层的具体实现。
```java
// 领域层接口
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(int id);
}
```

```java
// 基础设施层实现
@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final JpaRepository<Order, Integer> jpaRepository;

    @Override
    public Order save(Order order) {
        return jpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(int id) {
        return jpaRepository.findById(id);
    }
}
```

### 4. 聚合（Aggregate）管理

聚合根（Aggregate Root）管理整个聚合的生命周期：

例如，Order 是聚合根，OrderItem 是子聚合。Order 聚合控制子聚合 OrderItem 的生命周期并确保业务一致性。
```java
// 订单实体（聚合根）
public class Order {
    private int id;
    private List<OrderItem> items;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Order(int id, List<OrderItem> items) {
        this.id = id;
        this.items = items;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    // 其他业务逻辑方法
}
```

订单项是 OrderItem，可以视为子聚合：
```java
public class OrderItem {
    private int id;
    private String productName;
    private int quantity;
    private BigDecimal price;

    // 构造方法和其他方法
}
```

### 5. 应用服务（Application Service）
应用服务封装领域逻辑，避免外部直接操作领域对象：

```java
// 业务流程处理
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(int orderId, List<OrderItem> items) {
        Order order = new Order(orderId, items, "Pending");
        return orderRepository.save(order);
    }
}
```

### 6. 事件驱动（Event-Driven）
使用 **领域事件（Domain Events）** 进行解耦，在 Java 语言中可通过 **Channel** 或 **Pub/Sub** 实现：

```java
public class OrderCreatedEvent {
    private final int orderId;

    public OrderCreatedEvent(int orderId) {
        this.orderId = orderId;
    }

    // getters
}

@Service
public class EventPublisher {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void publish(OrderCreatedEvent event) {
        eventPublisher.publishEvent(event);
    }
}
```

### 7. 结合 CQRS（命令查询职责分离）
DDD 可结合 CQRS（Command Query Responsibility Segregation），在 Java 中可用 **命令（Command）** 处理变更操作，用 **查询（Query）** 处理数据读取：

```java
public class CreateOrderCommand {
    private final int orderId;
    private final List<OrderItem> items;

    public CreateOrderCommand(int orderId, List<OrderItem> items) {
        this.orderId = orderId;
        this.items = items;
    }

    // getters
}
```

### 总结

Java DDD 实践强调：

1. 使用 `class` 作为领域模型（Entity、Value Object、Aggregate）
2. 依赖倒置，通过接口定义领域层，不直接依赖基础设施
3. 使用应用服务`（Service）`封装业务逻辑，避免外部直接操作领域对象
4. 事件驱动，通过 `ApplicationEventPublisher` 进行解耦
5. 结合 `CQRS`，实现命令和查询分离，提高可扩展性