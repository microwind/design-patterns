## Java SpringBoot `MVC`目录结构
```bash
spring-mvc/
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── microwind                # 组织名
│   │   │           └── springbootorder      # 项目名
│   │   │               ├── controllers      # 控制器层（HTTP请求入口）
│   │   │               |   └── order        # 模块目录
│   │   │               |       ├── OrderController.java  
│   │   │               ├── services         # 服务层（业务逻辑处理）
│   │   │               |   └── order       
│   │   │               |       ├── OrderService.java     
│   │   │               ├── repository       # 数据访问层（数据库操作）
│   │   │               |   └── order       
│   │   │               |       ├── OrderRepository.java  # 数据访问接口
│   │   │               |       ├── OrderRepositoryImpl.java # 数据访问实现
│   │   │               ├── models           # 模型层（数据对象定义）
│   │   │               |   └── order       
│   │   │               |       ├── Order.java       
│   │   │               ├── middleware/             # 中间件（例如：鉴权、日志、拦截等）
│   │   │               │   ├── LoggingFilter.java  # 日志中间件，java通常使用Filter
│   │   │               │   └── AuthorizationFilter.java  # 鉴权中间件
│   │   │               └── config/                 # 配置类（如数据库、Web配置）
│   │   │               │   └── ServiceConfig.java  # 服务器与环境配置
│   │   │               └── utils/                  # （通用工具方法）
│   │   │               │   └── ResponseUtils.java  # 响应包装处理工具
│   │   │               └── Application.java        # 启动类
│   │   ├── resources
│   │   │   ├── static                      # 静态资源（CSS、JS、图片）
│   │   │   ├── templates                   # 模板文件（Thymeleaf/HTML）
│   │   │   ├── application.properties      # 配置文件（端口、数据源等）
│   │   ├── webapp                          # [可选] Web资源目录,可以模拟生成
│   │   │   └── WEB-INF/                    # [可选] 嵌入式服务器模拟生成
│   ├── test                                # 单元测试目录
│   │   ├── java
│   │   │   └── com
│   │   │       └── microwind
│   │   │           └── springbootorder
│   │   │               ├── controllers      # 控制器层测试
│   │   │               |   └── order       
│   │   │               |       ├── OrderControllerTest.java  
│   │   │               └── ApplicationTest.java  # 集成测试
├── pom.xml                                  # Maven 构建文件
```

## 目录结构说明
### controllers
控制器层，负责处理 HTTP 请求和响应。按照业务模块划分不同的子目录，每个子目录下的控制器类处理该模块相关的 HTTP 接口。

### services
服务层，负责处理具体的业务逻辑。同样按照业务模块划分，每个服务类处理该模块的业务操作。

### repository
仓储层，主要负责与数据库进行交互，执行数据的增删改查操作。按业务模块划分，每个仓储类处理该模块的数据持久化。

### models
模型层，用于定义数据模型，通常对应数据库中的表结构。按业务模块划分，每个模型类代表该模块的数据结构。

### resources
- static
用于存放静态资源，如 CSS、JavaScript 文件和图片等。这些资源一般会放到CDN或专门的静态服务机，很少跟Java部署在一起。

- templates
这是传统意义上的视图层，用于存放模板文件，如 Thymeleaf、JSP、Velocity、Freemarker等。模板文件用于生成动态的 HTML 页面。在前后端分离下，很少由Java渲染页面了。

- application.properties
说明：Spring Boot 的配置文件，用于配置应用的各种属性，如数据库连接信息、服务器端口等。

## 运行
创建db.sql，导入数据库结构。
```sql
CREATE DATABASE order_db;
CREATE TABLE `orders` (
  `order_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `order_name` VARCHAR(255) NOT NULL COMMENT '订单名称',
  `amount` DECIMAL(10, 2) NOT NULL COMMENT '订单金额',
  `status` VARCHAR(50) NOT NULL COMMENT '订单状态',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
```
```bash
# 编译打包
$ mvn clean install -U
# 启动应用
$ java -jar target/spring-boot-order-1.0.0.jar
# 直接启动
$ mvn spring-boot:run
Starting server on http://localhost:8080 successfully.

# 访问验证
$ curl -X GET http://localhost:8080/api/orders
$ curl -X GET http://localhost:8080/api/orders/订单ID

# 运行测试
$ mvn test
```

## 分层特点
Spring支持经典MVC三层设计
```text
​Controller（接口层）​ → ​View（视图层）​ → Model（模型层）​
```
也支持新的接口与应用、数据分离式MVC设计
```text
​Controller（接口控制层）​ → ​Service（应用服务层）​ → ​Repository（数据模型层）​
```

### 2. 分层架构
- **控制器层（Controller）**
通过 `@RestController` 或 `@Controller` 注解定义 HTTP 接口，使用 `@RequestMapping` 映射路径。

```java
// OrderController.java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable int id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order savedOrder = orderService.createOrder(order);
        return ResponseEntity.created(URI.create("/orders/" + savedOrder.getId())).body(savedOrder);
    }
}
```

- **服务层（Service）**
通过 `@Service` 注解声明服务类，处理业务逻辑并调用数据访问层。
```java
// OrderService.java
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order getOrderById(int id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }

    public Order createOrder(Order order) {
        order.setCreatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
}
```

- **数据访问层（Repository）**
通过 `@Repository` 注解定义数据访问接口，继承 Spring Data JPA 接口简化操作。
```java
// OrderRepository.java
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    // 自定义查询方法
    List<Order> findByStatus(String status);
}
```

- **模型层（Model）**
使用 `@Entity` 注解定义数据模型，与数据库表映射。
```java
// Order.java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String customerName;
    private BigDecimal amount;
    private LocalDateTime createdAt;

    // Getters & Setters
}
```

### 3. 注解驱动与依赖注入（Dependency Injection）
通过注解实现请求映射、依赖注入等核心功能，采用构造函数注入确保依赖不可变。
```java
// OrderController.java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    // 显式构造函数注入（推荐方式）
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 请求映射
    @GetMapping("/{id}")
    public OrderDTO getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }
}
```

```java
// OrderService.java
@Service
public class OrderService {
    // OrderService 仅依赖接口 OrderRepository，不直接调用JPA实现类。
    private final OrderRepository repository;

    // 构造函数注入（Spring自动识别）
    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    // 业务方法示例
    public OrderDTO getOrderById(Long id) {
        OrderEntity entity = repository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
        return OrderMapper.toDTO(entity);
    }
}
```

### 4. 接口与实现分离
通过定义 `OrderRepository` 接口，隔离业务逻辑与数据访问细节。具体实现放在`OrderRepositoryImpl`类。
```java
// OrderRepository.java 访问接口（定义）
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(int id);
}
```

构造函数注入 JpaRepository，实现仓储层对ORM框架的依赖，而非直接编写SQL或JDBC代码。符合 ​控制反转（IoC）​ 原则，提升代码可测试性。
接口封装数据库操作方法（如 save、findById），隐藏底层数据存储细节（如表名、字段映射）。
```java
// OrderRepositoryImpl.java 访问实现（依赖JPA）
@Repository
public class OrderRepositoryImpl implements OrderRepository {
    // 依赖Spring自动注入JPA实现
    private final JpaRepository<Order, Integer> jpaRepository;

    public OrderRepositoryImpl(JpaRepository<Order, Integer> jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
}
```

### 5. 视图解析（View Resolution）
Spring MVC 支持多种视图技术，如 Thymeleaf、JSP 等。通过视图解析器将控制器返回的逻辑视图名解析为实际的视图文件。
```java
@Controller
@RequestMapping("/page")
public class ViewController {

    @GetMapping("/hello")
    public String showHelloPage(Model model) {
        model.addAttribute("message", "Hello from view!");
        return "hello"; // 逻辑视图名
    }
}
```

### 6. 拦截器（Interceptors）
Spring MVC 提供了拦截器机制，可以在请求处理前后进行一些额外的处理，如日志记录、权限验证等。
```java
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("Pre-handle request: " + request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("Post-handle request: " + request.getRequestURI());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("Request completed: " + request.getRequestURI());
    }
}
```

### 7. 数据绑定（Data Binding）
Spring MVC 可以自动将 HTTP 请求参数绑定到 Java 对象上，方便处理表单提交等操作。
```java
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @PostMapping("/submit")
    public String submitForm(@RequestBody Order order) {
        return "Received data: " + order.getName();
    }
}
```

### 8. 配置管理
通过 `@Configuration` 类集中管理配置（如数据库、安全）。
```java
// DatabaseConfig.java
@Configuration
public class DatabaseConfig {
    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:mysql://localhost:3306/order_db")
                .username("root")
                .password("password")
                .build();
    }
}
```

### 9. 异常处理
通过 `@ControllerAdvice` 全局处理异常，统一返回格式。

```java
// GlobalExceptionHandler.java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> handleOrderNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
```

### 总结
Java Spring MVC 的主要优势在于：
1. 分层清晰：控制器、应用服务、数据访问各司其职。
2. 快速开发：通过注解简化配置，利用拦截器和数据绑定等方便处理请求。
3. 灵活扩展：依赖注入和模块化设计便于功能扩展。
4. 集成便捷：与 Spring Data JPA、Thymeleaf 等组件无缝整合。
