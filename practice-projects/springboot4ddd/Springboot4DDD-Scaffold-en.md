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

## 📚 Related Documentation

- **README.md** - Project overview and quick start
- **TUTORIAL.md** - Complete practical tutorial (30,000+ words detailed explanation)
- **DATABASE.md** - Multi-datasource configuration details
- **SIGN_GUIDE.md** - API signature verification usage guide

---

## 🤝 Get Help

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
