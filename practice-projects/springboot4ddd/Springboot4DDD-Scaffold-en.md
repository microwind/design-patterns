# An out-of-the-box Spring Boot 4 DDD project scaffold

> An out-of-the-box DDD (Domain-Driven Design) engineering scaffold based on Spring Boot 4.0.1 and Java 21

## 🎯 What is this?

**Springboot4DDD** is an out-of-the-box Java DDD engineering scaffold that helps developers quickly build Web applications that conform to Domain-Driven Design principles. The structure is simple and clear, helping you get started with Java development quickly.
Source Code: https://github.com/microwind/design-patterns/tree/main/practice-projects/springboot4ddd

### Core Features
✅ **Strict DDD Four-Layer Architecture** - Clear separation of domain layer, application layer, infrastructure layer, and interface layer<br>
✅ **Event-Driven Architecture** - Integrated RocketMQ, supports domain event publishing and consumption<br>
✅ **Multi-Datasource Support** - Out-of-the-box support for MySQL + PostgreSQL dual datasources<br>
✅ **Dual Persistence Solutions** - Two options available: JdbcTemplate and Spring Data JDBC<br>
✅ **API Signature Verification** - Built-in complete interface security authentication mechanism<br>
✅ **Unified Response Format** - Standardized API response structure<br>
✅ **Global Exception Handling** - Elegant error capture and response<br>
✅ **Parameter Validation** - Data validation based on Jakarta Validation<br>
✅ **Production Ready** - Complete logging, configuration, transaction management

### Tech Stack

| Technology | Version | Description |
|-----------|---------|-------------|
| Spring Boot | 4.0.1 | Latest stable version |
| Java | 21 | LTS version |
| MySQL | 8.0+ | User data storage |
| PostgreSQL | 14+ | Order data storage |
| Redis | 6.0+ | Cache (optional) |
| RocketMQ | 5.3+ | Message Queue (event-driven) |
| Lombok | - | Simplify code |
| Maven | 3.8+ | Build tool |

---

## 💡 Why Choose This Scaffold?

### 1. Save Time

No need to build project architecture from scratch. Clone and use immediately, focus on business development.

**Comparison**:
- ❌ Traditional approach: 1-2 weeks to build basic architecture
- ✅ Using scaffold: 10 minutes to complete initialization

### 2. Architecture Standards

Strictly follow DDD layering principles to avoid code chaos.

**Benefits**:
- Business logic cohesion in domain objects
- Clear responsibilities for each layer, easy to maintain
- Support unit testing and integration testing

### 3. Best Practices

Integrated enterprise-level development best practices.

**Included**:
- Unified response format
- Global exception handling
- API signature verification
- Multi-datasource management
- Cross-database queries

### 4. Easy to Extend

Modular design, easily add new features.

**Examples**:
- 5 minutes to add new business entity
- 10 minutes to integrate Redis cache
- 15 minutes to integrate RocketMQ message queue

---

## 📁 Project Structure Details

### DDD structure
```text
┌─────────────────────────────────────────────────┐
│            Interfaces Layer                     │
│  - REST Controllers                             │
│  - Request/Response Objects (VO)                │
│  - API router                                   │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│          Application Layer                      │
│  - Application Services                         │
│  - DTO (Data Transfer Objects)                  │
│  - Use case orchestration                       │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│            Domain Layer                         │
│  - Entities                                     │
│  - Value Objects                                │
│  - Aggregates                                   │
│  - Domain Services                              │
│  - Repository Interfaces                        │
│  - Core business logic                          │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│         Infrastructure Layer                    │
│  - Repository Implementations                   │
│  - Database Access                              │
│  - External Services                            │
│  - Configuration                                │
│  - Utilities                                    │
└─────────────────────────────────────────────────┘
```

### Director structure
```
springboot4ddd/
├── src/main/java/com/github/microwind/springboot4ddd/
│   │
│   ├── Application.java                              # Spring Boot startup class
│   │
│   ├── domain/                                       # 【Domain Layer】Core business logic
│   │   ├── model/                                    # Domain models
│   │   │   ├── order/
│   │   │   │   └── Order.java                        # Order aggregate root (contains business methods)
│   │   │   └── user/
│   │   │       └── User.java                         # User entity
│   │   │
│   │   ├── repository/                               # Repository interfaces (implemented by infrastructure layer)
│   │   │   ├── order/
│   │   │   │   └── OrderRepository.java              # Order repository interface
│   │   │   └── user/
│   │   │       └── UserRepository.java               # User repository interface
│   │   │
│   │   ├── event/                                    # 🆕 Domain event definition
│   │   │   ├── DomainEvent.java                      # Domain event interface
│   │   │   ├── OrderEvent.java                       # Order event
│   │   │   ├── UserEvent.java                        # User event
│   │   │   └── EventPublisher.java                   # Event publisher interface
│   │   │
│   │   └── service/                                  # Domain service (business logic across aggregates)
│   │       └── [Domain service classes]
│   │
│   ├── application/                                  # 【Application Layer】Business orchestration
│   │   ├── dto/                                      # Data transfer objects
│   │   │   └── order/
│   │   │       ├── OrderDTO.java                     # Order DTO
│   │   │       └── OrderMapper.java                  # Entity and DTO conversion
│   │   │
│   │   └── service/                                  # Application service (orchestrate domain objects)
│   │       ├── order/
│   │       │   └── OrderService.java                 # Order application service
│   │       └── user/
│   │           └── UserService.java                  # User application service
│   │
│   ├── infrastructure/                               # 【Infrastructure Layer】Technical support
│   │   ├── common/                                   # Common components
│   │   │   ├── ApiResponse.java                      # Unified API response
│   │   │   └── GlobalExceptionHandler.java           # Global exception handling
│   │   │
│   │   ├── config/                                   # Configuration classes
│   │   │   ├── DataSourceConfig.java                 # Multi-datasource configuration
│   │   │   ├── OrderJdbcConfig.java                  # Spring Data JDBC configuration
│   │   │   ├── SignConfig.java                       # Signature configuration
│   │   │   ├── WebConfig.java                        # Web configuration
│   │   │   ├── JacksonConfig.java                    # JSON serialization configuration
│   │   │   └── ApiAuthConfig.java                    # API authentication configuration
│   │   │
│   │   ├── constants/                                # Constant definition
│   │   │   └── ErrorCode.java                        # Error code definition
│   │   │
│   │   ├── exception/                                # Custom exceptions
│   │   │   ├── BusinessException.java                # Business exception
│   │   │   └── ResourceNotFoundException.java        # Resource not found exception
│   │   │
│   │   ├── middleware/                               # Middleware
│   │   │   ├── SignatureInterceptor.java             # Signature interceptor
│   │   │   ├── CachedBodyFilter.java                 # Request body cache filter
│   │   │   └── CachedBodyHttpServletRequest.java     # Request body cache wrapper
│   │   │
│   │   ├── repository/                               # Repository implementation
│   │   │   ├── order/
│   │   │   │   └── OrderRepositoryImpl.java          # Order repository implementation
│   │   │   ├── user/
│   │   │   │   └── UserRepositoryImpl.java           # User repository implementation
│   │   │   └── jdbc/
│   │   │       └── OrderJdbcRepository.java          # Spring Data JDBC interface
│   │   │
│   │   ├── messaging/                              # 🆕 RocketMQ message handling
│   │   │   ├── config/
│   │   │   │   └── RocketMQConfig.java             # RocketMQ configuration
│   │   │   ├── producer/
│   │   │   │   └── OrderEventProducer.java         # Order event producer
│   │   │   ├── consumer/
│   │   │   │   └── OrderEventConsumer.java         # Order event consumer
│   │   │   ├── message/
│   │   │   │   ├── OrderCreatedMessage.java        # Order created message
│   │   │   │   ├── OrderPaidMessage.java           # Order paid message
│   │   │   │   ├── OrderCancelledMessage.java      # Order cancelled message
│   │   │   │   └── OrderCompletedMessage.java      # Order completed message
│   │   │   └── converter/
│   │   │       └── OrderEventMessageMapper.java    # Event to message converter
│   │   │
│   │   └── util/                                     # Utility classes
│   │       └── SignatureUtil.java                    # Signature utility
│   │
│   └── interfaces/                                   # 【Interface Layer】External exposure
│       ├── annotation/                               # Custom annotations
│       │   ├── RequireSign.java                      # Require signature verification annotation
│       │   ├── IgnoreSignHeader.java                 # Ignore signature verification annotation
│       │   └── WithParams.java                       # Signature parameter option
│       │
│       ├── controller/                               # REST controllers
│       │   ├── HealthController.java                 # Health check
│       │   ├── order/
│       │   │   └── OrderController.java              # Order interface
│       │   └── user/
│       │       └── UserController.java               # User interface
│       │
│       └── vo/                                       # View objects (request/response)
│           ├── order/
│           │   ├── CreateOrderRequest.java           # Create order request
│           │   ├── UpdateOrderRequest.java           # Update order request
│           │   ├── OrderResponse.java                # Order response
│           │   └── OrderListResponse.java            # Order list response
│           └── user/
│               ├── CreateUserRequest.java            # Create user request
│               ├── UpdateUserRequest.java            # Update user request
│               └── UserResponse.java                 # User response
│
├── src/main/resources/
│   ├── application.yml                               # Main configuration file
│   ├── application-dev.yml                           # Development environment configuration
│   ├── application-prod.yml                          # Production environment configuration
│   ├── apiauth-config.yaml                           # API authentication configuration
│   │
│   └── db/                                           # Database scripts
│       ├── mysql/
│       │   └── init_users.sql                        # MySQL initialization script
│       └── postgresql/
│           └── init_orders.sql                       # PostgreSQL initialization script
│
└── src/test/java/                                    # Test code
    └── com/github/microwind/springboot4ddd/
        └── ApplicationTests.java                     # Application test
```

### Layer Responsibility Description

| Layer | Location | Responsibility | Key Principle |
|------|---------|----------------|--------|
| **Domain Layer** | `domain/` | Core business logic, domain models | Independent of framework, business rules cohesion |
| **Application Layer** | `application/` | Orchestrate domain objects, manage transactions | Thin layer, no business logic |
| **Infrastructure Layer** | `infrastructure/` | Technical implementation, persistence, configuration | Implement technical details, transparent to upper layers |
| **Interface Layer** | `interfaces/` | REST API, request/response handling | Handle external interaction, no business logic |

---

## 🚀 Quick Start

### 1. Environment Preparation

Ensure you have installed:
- JDK 21
- Maven 3.8+
- MySQL 8.0+
- PostgreSQL 14+
- RocketMQ 5.3+ (for event-driven)
- Git

### 2. Clone Project

```bash
git clone https://github.com/microwind/design-patterns.git
cd design-patterns/practice-projects/springboot4ddd
```

### 3. Start RocketMQ (Optional, for event-driven)

```bash
# Start NameServer
cd rocketmq-all-5.3.2-bin-release
sh bin/mqnamesrv

# Start Broker
sh bin/mqbroker -n localhost:9876

# Check cluster status
sh bin/mqadmin clusterList -n localhost:9876
```

### 4. Initialize Database

**MySQL (User data):**
```bash
mysql -u root -p
CREATE DATABASE frog CHARACTER SET utf8mb4;
CREATE USER 'frog_admin'@'localhost' IDENTIFIED BY 'frog_password';
GRANT ALL PRIVILEGES ON frog.* TO 'frog_admin'@'localhost';
USE frog;
source src/main/resources/db/mysql/init_users.sql;
```

**PostgreSQL (Order data):**
```bash
psql -U postgres
CREATE DATABASE seed ENCODING 'UTF8';
\c seed
\i src/main/resources/db/postgresql/init_orders.sql
```

### 5. Configure Application

Edit `src/main/resources/application-dev.yml`:

```yaml
spring:
  datasource:
    user:
      jdbc-url: jdbc:mysql://localhost:3306/frog
      username: frog_admin
      password: frog_password  # Change to your password

    order:
      jdbc-url: jdbc:postgresql://localhost:5432/seed
      username: postgres
      password: postgres_password  # Change to your password

# RocketMQ Configuration (Optional)
rocketmq:
  name-server: localhost:9876
  producer:
    group: springboot4ddd-producer
    send-message-timeout: 3000
    retry-times-when-send-failed: 2
```

### 6. Start Application

```bash
# Compile
./mvnw clean compile

# Run
./mvnw spring-boot:run
```

### 7. Verify Installation

```bash
# Health check
curl http://localhost:8080/api/health

# Query user list
curl http://localhost:8080/api/users

# Query order list
curl http://localhost:8080/api/orders/list
```

---

## 📝 How to Develop New Features Based on the Scaffold

### Scenario: Add a "Product Management" Feature

#### Step 1: Create Domain Model

Create `Product.java` in `domain/model/product/`:

```java
package com.github.microwind.springboot4ddd.domain.model.product;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table("products")
public class Product {

    @Id
    private Long id;

    @Column("product_name")
    private String productName;

    private BigDecimal price;

    private Integer stock;

    @Column("created_at")
    private LocalDateTime createdAt;

    // Domain behavior: decrease stock
    public void decreaseStock(Integer quantity) {
        if (this.stock < quantity) {
            throw new BusinessException("Insufficient stock");
        }
        this.stock -= quantity;
    }

    // Domain behavior: increase stock
    public void increaseStock(Integer quantity) {
        this.stock += quantity;
    }
}
```

#### Step 2: Define Repository Interface

Create `ProductRepository.java` in `domain/repository/product/`:

```java
package com.github.microwind.springboot4ddd.domain.repository.product;

import com.github.microwind.springboot4ddd.domain.model.product.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    void deleteById(Long id);
}
```

#### Step 3: Implement Repository

Create `ProductRepositoryImpl.java` in `infrastructure/repository/product/`:

```java
package com.github.microwind.springboot4ddd.infrastructure.repository.product;

import com.github.microwind.springboot4ddd.domain.model.product.Product;
import com.github.microwind.springboot4ddd.domain.repository.product.ProductRepository;
import com.github.microwind.springboot4ddd.infrastructure.repository.jdbc.ProductJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJdbcRepository jdbcRepository;

    @Override
    public Product save(Product product) {
        return jdbcRepository.save(product);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jdbcRepository.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return (List<Product>) jdbcRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        jdbcRepository.deleteById(id);
    }
}
```

Create `ProductJdbcRepository.java` in `infrastructure/repository/jdbc/`:

```java
package com.github.microwind.springboot4ddd.infrastructure.repository.jdbc;

import com.github.microwind.springboot4ddd.domain.model.product.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJdbcRepository extends CrudRepository<Product, Long> {
}
```

#### Step 4: Create Application Service

Create `ProductService.java` in `application/service/product/`:

```java
package com.github.microwind.springboot4ddd.application.service.product;

import com.github.microwind.springboot4ddd.domain.model.product.Product;
import com.github.microwind.springboot4ddd.domain.repository.product.ProductRepository;
import com.github.microwind.springboot4ddd.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(String name, BigDecimal price, Integer stock) {
        Product product = new Product();
        product.setProductName(name);
        product.setPrice(price);
        product.setStock(stock);
        product.setCreatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void decreaseStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        product.decreaseStock(quantity);  // Call domain behavior
        productRepository.save(product);
    }
}
```

#### Step 5: Create REST Interface

Create `ProductController.java` in `interfaces/controller/product/`:

```java
package com.github.microwind.springboot4ddd.interfaces.controller.product;

import com.github.microwind.springboot4ddd.application.service.product.ProductService;
import com.github.microwind.springboot4ddd.domain.model.product.Product;
import com.github.microwind.springboot4ddd.infrastructure.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ApiResponse<Product> createProduct(@RequestBody CreateProductRequest request) {
        Product product = productService.createProduct(
                request.getName(),
                request.getPrice(),
                request.getStock()
        );
        return ApiResponse.success(product);
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> getProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ApiResponse.success(product);
    }

    @GetMapping
    public ApiResponse<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ApiResponse.success(products);
    }
}
```

#### Step 6: Create Database Table

Add to `src/main/resources/db/postgresql/`:

```sql
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert test data
INSERT INTO products (product_name, price, stock) VALUES
    ('iPhone 15 Pro', 7999.00, 100),
    ('MacBook Pro', 15999.00, 50),
    ('AirPods Pro', 1999.00, 200);
```

#### Step 7: Test Interface

```bash
# Query all products
curl http://localhost:8080/api/products

# Query single product
curl http://localhost:8080/api/products/1

# Create product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"iPad Pro","price":6999.00,"stock":80}'
```

---

## 🎨 Development Standards

### Naming Conventions

```java
// Domain objects: nouns, using business language
public class Product { }

// Application service: XxxService
public class ProductService { }

// Repository interface: XxxRepository
public interface ProductRepository { }

// Repository implementation: XxxRepositoryImpl
public class ProductRepositoryImpl implements ProductRepository { }

// DTO: XxxDTO
public class ProductDTO { }

// Request object: XxxRequest
public class CreateProductRequest { }

// Response object: XxxResponse
public class ProductResponse { }

// Controller: XxxController
public class ProductController { }
```

### Layering Principles

| Principle | Description |
|-----------|-------------|
| **Domain Layer Purity** | No Spring annotations except persistence mapping |
| **Business Logic Cohesion** | Implement business rules in domain objects |
| **Correct Dependency Direction** | Interface Layer → Application Layer → Domain Layer ← Infrastructure Layer |
| **Single Responsibility** | Each class is responsible for one thing |
| **Interface Isolation** | Domain layer defines interfaces, infrastructure layer implements |

### Code Style

```java
// ✅ Recommended: Business logic in domain objects
@Data
public class Order {
    public void cancel() {
        if (this.status != OrderStatus.PENDING) {
            throw new BusinessException("Only pending orders can be cancelled");
        }
        this.status = OrderStatus.CANCELLED;
    }
}

// ✅ Recommended: Application service only orchestrates
@Service
public class OrderService {
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.cancel();  // Call domain behavior
        orderRepository.save(order);
    }
}

// ❌ Not recommended: Business logic in application service
@Service
public class OrderService {
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        // Business logic should not be here
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Only pending orders can be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}
```

---

## � Common Configuration

### Multi-Datasource Configuration

To add a third datasource:

```java
@Configuration
public class DataSourceConfig {

    @Bean(name = "thirdDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.third")
    public DataSource thirdDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "thirdJdbcTemplate")
    public JdbcTemplate thirdJdbcTemplate(
            @Qualifier("thirdDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
```

### API Signature Configuration

Edit `apiauth-config.yaml`:

```yaml
api-auth:
  apps:
    - app-code: my-app
      secret-key: my_secret_key_123
      permissions:
        - /api/products/**
        - /api/orders/**
```

Add annotation on controller method:

```java
@PostMapping
@RequireSign(withParams = WithParams.TRUE)  // Require signature verification
public ApiResponse<?> createProduct(@RequestBody CreateProductRequest request) {
    // ...
}
```

### Redis Cache Configuration

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

Use cache:

```java
@Service
public class ProductService {

    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow();
    }

    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
```

---

## 🚀 RocketMQ Event-Driven Architecture

### Why Event-Driven?

In traditional synchronous call mode, order creation, payment and other operations need to synchronously call multiple services (inventory, notification, points, etc.), leading to:
- **High coupling** - Order service depends on all other services
- **Poor performance** - Synchronously waiting for multiple service responses
- **Low availability** - Any service failure affects the main process

**Event-driven architecture achieves decoupling through asynchronous messages**:
- ✅ Service decoupling - Order service only needs to publish events, doesn't care who consumes
- ✅ High performance - Asynchronous processing, doesn't block main process
- ✅ High availability - Consumer failure doesn't affect producer

### Message Processing Architecture

This scaffold adopts **clear responsibility separation** design:

```
infrastructure/messaging/
├── config/                    # Configuration management
│   └── RocketMQConfig.java   # Topic, Tag, group name configuration
├── producer/                  # Producer (send messages)
│   └── OrderEventProducer.java
├── consumer/                  # Consumer (receive messages)
│   └── OrderEventConsumer.java
├── message/                   # Message objects (transport carrier)
│   ├── OrderCreatedMessage.java
│   ├── OrderPaidMessage.java
│   ├── OrderCancelledMessage.java
│   └── OrderCompletedMessage.java
└── converter/                 # Converter (event→message)
    └── OrderEventMessageMapper.java
```

**Design principles**:
- Domain events (DomainEvent) are defined in domain layer
- Message objects (Message) are defined in infrastructure layer
- Decouple domain layer and infrastructure layer through Converter

### Message Flow

```
┌─────────────┐      ┌──────────────┐      ┌─────────────┐
│  HTTP Request│ ───> │  OrderService │ ───> │  Order Model │
└─────────────┘      └──────────────┘      └─────────────┘
                            │                      │
                            │ 1.Persist           │ 2.Record event
                            ▼                      ▼
                     ┌──────────────┐      ┌─────────────────┐
                     │   Database   │      │ DomainEvent List │
                     └──────────────┘      └─────────────────┘
                                                   │
                                     3.Get event and publish
                                                   ▼
                                          ┌───────────────────┐
                                          │ OrderEventProducer │
                                          └───────────────────┘
                                                   │
                                     4.Convert to Message object
                                                   ▼
                                          ┌───────────────────┐
                                          │    RocketMQ       │
                                          │ Topic: order-events│
                                          │ Tag: OrderCreated  │
                                          └───────────────────┘
                                                   │
                                     5.Consume message
                                                   ▼
                                          ┌───────────────────┐
                                          │ OrderEventConsumer│
                                          └───────────────────┘
                                                   │
                                     6.Business processing
                                                   ▼
                                     ┌───────────────────────────┐
                                     │ • Deduct inventory         │
                                     │ • Send notification        │
                                     │ • Update points           │
                                     │ • Generate report         │
                                     └───────────────────────────┘
```

### Complete Implementation Example

### Step 1: Define Domain Event (Domain Layer)

Create base class and order events in `domain/event/`:

```java
// DomainEvent.java - Domain event base class
package com.github.microwind.springboot4ddd.domain.event;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public abstract class DomainEvent implements Serializable {
    private String eventId;
    private Long aggregateId;
    private String aggregateType;
    private LocalDateTime occurredAt;

    protected DomainEvent(Long aggregateId, String aggregateType) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.occurredAt = LocalDateTime.now();
    }

    public String getEventType() {
        return this.getClass().getSimpleName();
    }
}

// OrderCreatedEvent.java - Order creation event
package com.github.microwind.springboot4ddd.domain.event.order;

import com.github.microwind.springboot4ddd.domain.event.DomainEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderCreatedEvent extends DomainEvent {
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;

    public OrderCreatedEvent(Long orderId, String orderNo, Long userId,
                             BigDecimal totalAmount, String status) {
        super(orderId, "Order");
        this.orderNo = orderNo;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
    }
}
```

### Step 2: Record Events in Order Aggregate Root

```java
// Order.java
@Data
@Table("orders")
public class Order {
    @Id
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;

    // Domain event list (not persisted)
    @Transient
    private List<DomainEvent> domainEvents = new ArrayList<>();

    // Create order
    public static Order create(Long userId, BigDecimal totalAmount) {
        Order order = new Order();
        order.setOrderNo("ORD" + System.currentTimeMillis());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        return order;
    }

    // Record order creation event (call after save)
    public void recordCreatedEvent() {
        this.domainEvents.add(new OrderCreatedEvent(
                this.id, this.orderNo, this.userId,
                this.totalAmount, this.status
        ));
    }

    public List<DomainEvent> getDomainEvents() {
        return new ArrayList<>(this.domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
```

### Step 3: Create Message Object (Infrastructure Layer)

```java
// OrderCreatedMessage.java
package com.github.microwind.springboot4ddd.infrastructure.messaging.order.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedMessage implements Serializable {
    private String eventId;
    private Long orderId;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime occurredAt;
}
```

### Step 4: Create Event to Message Converter

```java
// OrderEventMessageMapper.java
package com.github.microwind.springboot4ddd.infrastructure.messaging.order.converter;

import com.github.microwind.springboot4ddd.domain.event.order.*;
import com.github.microwind.springboot4ddd.infrastructure.messaging.order.message.*;
import org.springframework.stereotype.Component;

@Component
public class OrderEventMessageMapper {

    public OrderCreatedMessage toMessage(OrderCreatedEvent event) {
        return OrderCreatedMessage.builder()
                .eventId(event.getEventId())
                .orderId(event.getAggregateId())
                .orderNo(event.getOrderNo())
                .userId(event.getUserId())
                .totalAmount(event.getTotalAmount())
                .status(event.getStatus())
                .occurredAt(event.getOccurredAt())
                .build();
    }

    // Other event conversion methods...
}
```

### Step 5: Implement Message Producer

```java
// OrderEventProducer.java
package com.github.microwind.springboot4ddd.infrastructure.messaging.order.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.microwind.springboot4ddd.domain.event.DomainEvent;
import com.github.microwind.springboot4ddd.domain.event.order.*;
import com.github.microwind.springboot4ddd.infrastructure.messaging.order.converter.OrderEventMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final RocketMQTemplate rocketMQTemplate;
    private final OrderEventMessageMapper messageMapper;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "order-events";

    public void publishEvent(DomainEvent event) {
        try {
            // 1. Convert event to message object
            Object message = convertToMessage(event);

            // 2. Serialize to JSON
            String messageBody = objectMapper.writeValueAsString(message);

            // 3. Send to RocketMQ
            String destination = TOPIC + ":" + event.getEventType();
            SendResult result = rocketMQTemplate.syncSend(destination, messageBody);

            log.info("Order event sent, eventId={}, eventType={}, msgId={}",
                    event.getEventId(), event.getEventType(), result.getMsgId());
        } catch (Exception e) {
            log.error("Failed to send order event, eventId={}", event.getEventId(), e);
            throw new RuntimeException("Failed to publish order event", e);
        }
    }

    public void publishEvents(List<DomainEvent> events) {
        for (DomainEvent event : events) {
            publishEvent(event);
        }
    }

    private Object convertToMessage(DomainEvent event) {
        if (event instanceof OrderCreatedEvent) {
            return messageMapper.toMessage((OrderCreatedEvent) event);
        } else if (event instanceof OrderPaidEvent) {
            return messageMapper.toMessage((OrderPaidEvent) event);
        }
        // Other event types...
        throw new IllegalArgumentException("Unsupported event type: " + event.getClass());
    }
}
```

### Step 6: Implement Message Consumer

```java
// OrderEventConsumer.java
package com.github.microwind.springboot4ddd.infrastructure.messaging.order.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.microwind.springboot4ddd.infrastructure.messaging.order.message.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventConsumer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    // Order creation event consumer
    @Slf4j
    @Component
    @RocketMQMessageListener(
            topic = "order-events",
            consumerGroup = "springboot4ddd-order-created-consumer",
            selectorExpression = "OrderCreatedEvent"
    )
    public static class OrderCreatedConsumer implements RocketMQListener<String> {

        @Override
        public void onMessage(String message) {
            try {
                log.info("Received order creation message: {}", message);

                OrderCreatedMessage msg = OBJECT_MAPPER.readValue(
                        message, OrderCreatedMessage.class);

                // Business processing: send notification
                sendCreationNotification(msg);

                log.info("Order creation message processed, eventId={}", msg.getEventId());
            } catch (Exception e) {
                log.error("Failed to process order creation message", e);
                throw new RuntimeException("Order creation message processing failed", e);
            }
        }

        private void sendCreationNotification(OrderCreatedMessage msg) {
            log.info("【Simulated notification】Send order creation notification to user {}, order number: {}",
                    msg.getUserId(), msg.getOrderNo());
        }
    }
}
```

### Step 7: Publish Events in OrderService

```java
// OrderService.java
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "orderTransactionManager", readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    @Transactional(transactionManager = "orderTransactionManager")
    public OrderDTO createOrder(CreateOrderRequest request) {
        // 1. Create order
        Order order = Order.create(request.getUserId(), request.getTotalAmount());

        // 2. Persist
        Order savedOrder = orderRepository.save(order);

        // 3. Record event
        savedOrder.recordCreatedEvent();

        // 4. Publish event to RocketMQ
        publishDomainEvents(savedOrder);

        return orderMapper.toDTO(savedOrder);
    }

    private void publishDomainEvents(Order order) {
        List<DomainEvent> events = order.getDomainEvents();
        if (!events.isEmpty()) {
            orderEventProducer.publishEvents(events);
            order.clearDomainEvents();
        }
    }
}
```

### Step 8: Configure RocketMQ

**pom.xml dependency** (already included):
```xml
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-spring-boot-starter</artifactId>
    <version>2.3.0</version>
</dependency>
```

**application.yaml configuration**:
```yaml
rocketmq:
  name-server: localhost:9876
  producer:
    group: springboot4ddd-producer  # Producer group
    send-message-timeout: 3000      # Send timeout (ms)
    compress-message-body-threshold: 4096  # Compression threshold
  consumer:
    group: springboot4ddd-consumer  # Default consumer group
```

### Step 9: Start RocketMQ

```bash
# Start NameServer
cd rocketmq-all-5.3.2-bin-release
nohup sh bin/mqnamesrv &

# Start Broker
nohup sh bin/mqbroker -n localhost:9876 &

# Check processes
jps -l | grep rocketmq
```

### Step 10: Test Message Sending and Receiving

```bash
# 1. Start Spring Boot application
./mvnw spring-boot:run

# 2. Create order (will automatically publish OrderCreatedEvent message)
curl -X POST http://localhost:8080/api/orders/create \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "totalAmount": 999.99}'

# 3. View application logs to see:
# - Order creation success log
# - Order event sent, eventId=xxx, eventType=OrderCreatedEvent, msgId=xxx
# - Received order creation message: {"eventId":"xxx",...}
# - 【Simulated notification】Send order creation notification to user 1, order number: ORDxxx
# - Order creation message processed, eventId=xxx
```

### Architecture Advantages Summary

| Feature | Traditional Synchronous Call | Event-Driven Architecture |
|---------|-----------------------------|---------------------------|
| **Coupling** | High - Order service depends on inventory, notification services | Low - Only need to publish events |
| **Performance** | Poor - Synchronously wait for all service responses | Good - Asynchronous processing, second-level response |
| **Availability** | Low - Any service failure affects main process | High - Consumer failure doesn't affect order creation |
| **Scalability** | Difficult - Adding new features requires modifying order service | Easy - Just add new consumers |
| **Maintainability** | Complex - Unclear responsibilities | Simple - Clear responsibility separation |

---

## 🔧 RocketMQ Configuration Details

### Basic Configuration

```yaml
rocketmq:
  name-server: localhost:9876          # NameServer address
  producer:
    group: springboot4ddd-producer     # Producer group name
    send-message-timeout: 3000         # Send timeout (ms)
    compress-message-body-threshold: 4096  # Message body compression threshold (bytes)
    retry-times-when-send-failed: 2    # Retry count on send failure
    max-message-size: 4194304          # Maximum message size (4MB)
  consumer:
    group: springboot4ddd-consumer     # Default consumer group name
```

### ACL Permission Configuration (Optional)

If RocketMQ has ACL enabled, configure account and password:

```yaml
rocketmq:
  producer:
    access-key: RocketMQ
    secret-key: 12345678
  consumer:
    access-key: RocketMQ
    secret-key: 12345678
```

**RocketMQ Broker side configuration** (`conf/plain_acl.yml`):

```yaml
accounts:
  - accessKey: RocketMQ
    secretKey: 12345678
    whiteRemoteAddress:
    admin: false
    defaultTopicPerm: DENY
    defaultGroupPerm: SUB
    topicPerms:
      - order-events=PUB|SUB
    groupPerms:
      - springboot4ddd-producer=PUB
      - springboot4ddd-consumer=SUB
      - springboot4ddd-order-created-consumer=SUB
      - springboot4ddd-order-paid-consumer=SUB
```

### Common RocketMQ Commands

```bash
# View cluster status
sh bin/mqadmin clusterList -n localhost:9876

# View all topics
sh bin/mqadmin topicList -n localhost:9876

# View topic details
sh bin/mqadmin topicStatus -n localhost:9876 -t order-events

# View consumer group status
sh bin/mqadmin consumerProgress -n localhost:9876 -g springboot4ddd-order-created-consumer

# Send test message
sh bin/mqadmin sendMessage \
  -n localhost:9876 \
  -t order-events \
  -p "test message"

# Consume message
sh bin/mqadmin consumeMessage \
  -n localhost:9876 \
  -t order-events \
  -g test-consumer
```

---

## � Related Documentation

- **README.md** - Project overview and quick start
- **docs/TUTORIAL.md** - Complete practical tutorial (30,000+ words detailed explanation)
- **docs/DATABASE.md** - Multi-datasource configuration details
- **docs/SIGN_GUIDE.md** - API signature verification usage guide

---

## 📋 Checklist

When developing new features using the scaffold, ensure you complete the following steps:

- [ ] Create domain model in `domain/model/`
- [ ] Implement business methods in domain model
- [ ] Define repository interface in `domain/repository/`
- [ ] Implement repository in `infrastructure/repository/`
- [ ] Create application service in `application/service/`
- [ ] Create REST controller in `interfaces/controller/`
- [ ] Create request/response objects in `interfaces/vo/`
- [ ] Write database initialization script
- [ ] Add unit tests and integration tests
- [ ] Update API documentation

---

## ⚡ Frequently Asked Questions

### Q: How to switch databases?

A: Modify the datasource configuration in `application.yml`, and adjust the corresponding initialization script.

### Q: How to disable API signature verification?

A: Add `@IgnoreSignHeader` annotation on the controller method.

### Q: How to add a new datasource?

A: Refer to `DataSourceConfig.java`, add a new DataSource Bean and corresponding JdbcTemplate.

### Q: Can the domain layer depend on Spring?

A: The domain layer should remain clean. Except for persistence mapping annotations (like `@Table`, `@Column`), try not to depend on Spring.

### Q: How to disable RocketMQ?

A: If event-driven functionality is not needed, you can remove RocketMQ related dependencies, or not inject `EventPublisher`. The system will still work normally.

---

**Author**: JarryLi

Source Code Download:
https://github.com/microwind/design-patterns/tree/main/practice-projects/springboot4ddd

- **GitHub Repository**: https://github.com/microwind/design-patterns/tree/main/practice-projects/springboot4ddd
- **Issue Feedback**: https://github.com/microwind/design-patterns/issues
- **Reference Materials**:
    - [Spring Boot Documentation](https://docs.spring.io/spring-boot/)
    - [Spring Data JDBC Documentation](https://docs.spring.io/spring-data/jdbc/)
    - [Domain-Driven Design](https://www.amazon.com/Domain-Driven-Design-Tackling-Complexity-Software/dp/0321125215)

---

## 📋 Checklist

When developing new features using the scaffold, ensure you complete the following steps:

- [ ] Create domain model in `domain/model/`
- [ ] Implement business methods in domain model
- [ ] Define repository interface in `domain/repository/`
- [ ] Implement repository in `infrastructure/repository/`
- [ ] Create application service in `application/service/`
- [ ] Create REST controller in `interfaces/controller/`
- [ ] Create request/response objects in `interfaces/vo/`
- [ ] Write database initialization script
- [ ] Add unit tests and integration tests
- [ ] Update API documentation

---

## ⚡ Frequently Asked Questions

### Q: How to switch databases?

A: Modify the datasource configuration in `application.yml`, and adjust the corresponding initialization script.

### Q: How to disable API signature verification?

A: Add `@IgnoreSignHeader` annotation on the controller method.

### Q: How to add a new datasource?

A: Refer to `DataSourceConfig.java`, add a new DataSource Bean and corresponding JdbcTemplate.

### Q: Can the domain layer depend on Spring?

A: The domain layer should remain clean. Except for persistence mapping annotations (like `@Table`, `@Column`), try not to depend on Spring.

### Q: How to disable RocketMQ?

A: If event-driven functionality is not needed, you can remove RocketMQ related dependencies, or not inject `EventPublisher`. The system will still work normally.

---

**Author**: JarryLi

Source Code Download:
https://github.com/microwind/design-patterns/tree/main/practice-projects/springboot4ddd
