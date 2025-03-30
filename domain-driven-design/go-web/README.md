## Go语言`DDD`目录结构
```bash
go-web/
│── cmd/
│   └── main.go               # 应用入口
│── internal/
│   ├── application/          # 应用层（协调领域逻辑，处理业务用例）
│   │   ├── services/         # 服务层，业务逻辑目录
│   │   │   └── order_service.go # 订单应用服务，调用领域层业务逻辑
│   ├── domain/               # 领域层（核心业务逻辑和接口定义）
│   │   ├── order/            # 订单聚合
│   │   │   ├── order.go      # 订单实体（聚合根），包含核心业务逻辑
│   │   ├── repository/       # 通用仓库接口
│   │   │   ├── repository.go # 通用仓库接口（通用 CRUD 操作）
│   │   │   └── order_repository.go # 订单仓储接口，定义对订单数据的操作
│   ├── infrastructure/       # 基础设施层（实现领域层定义的接口）
│   │   ├── repository/       # 仓储实现
│   │   │   └── order_repository_impl.go  # 订单仓储实现，具体的订单数据存储
│   └── interfaces/           # 接口层（处理外部请求，如HTTP接口）
│   │   ├── handlers/         # HTTP 处理器
│   │   │  └── order_handler.go # 订单相关的HTTP处理器
│   │   └── routes/
│   │   │   ├── router.go     # 基础路由工具设置
│   │   │   └── order-routes.go # 订单路由地址配置
│   │   │   └── order-routes-test.go # 订单路由测试
│   └── middleware/           # 中间件（例如：鉴权、拦截、认证等）
│   │   └── logging.go        # 日志中间件
│   ├── config/               # 服务相关配置
│   │   └── server_config.go  # 服务器配置（如端口、超时设置等）
│── pkg/                      # 可复用的公共库
│   └── utils/                # 工具类（例如：日志、日期处理等）
```

## 运行
```bash
$ go clean -modcache
$ go mod tidy
$ go run cmd/main.go
Starting server on :8080 successfully.
# 通过 http://localhost:8080 访问系统

# 测试用例
$ cd internal/interfaces/routes
$ % go test
# 展示测试结果
```

## Go 语言 DDD（领域驱动设计）特点

### 1. 关注领域模型
DDD 强调领域模型的构建，使用 **聚合（Aggregate）**、**实体（Entity）**、**值对象（Value Object）** 组织业务逻辑。

在 Go 语言中，通常使用 `struct` 定义实体和值对象：

```go
// 实体（Entity）
type User struct {
    ID   int
    Name string
}
```

### 2. 分层架构
DDD 通常采用 **分层架构**，Go 语言项目可以遵循如下结构：

- **领域层（Domain Layer）**：核心业务逻辑，如 `domain` 目录下的实体和聚合。
- **应用层（Application Layer）**：用例（Use Cases）和业务流程编排。
- **基础设施层（Infrastructure Layer）**：数据库、缓存、外部 API 适配等。
- **接口层（Interface Layer）**：提供 HTTP、gRPC 或 CLI 接口。

### 3. 依赖倒置（Dependency Inversion）
领域层不应直接依赖基础设施，而是通过 **接口（Interface）** 进行依赖倒置。例如：

```go
// 领域层：定义接口
type UserRepository interface {
    GetByID(id int) (*User, error)
}
```

```go
// 基础设施层：数据库实现
type userRepositoryImpl struct {
    db *sql.DB
}

func (r *userRepositoryImpl) GetByID(id int) (*User, error) {
    // 数据库查询逻辑
}
```

### 4. 聚合（Aggregate）管理

聚合根（Aggregate Root）管理整个聚合的生命周期：

```go
type Order struct {
    ID      int
    Items   []OrderItem
    Status  string
}

func (o *Order) AddItem(item OrderItem) {
    o.Items = append(o.Items, item)
}
```

### 5. 应用服务（Application Service）
应用服务封装领域逻辑，避免外部直接操作领域对象：

```go
type OrderService struct {
    repo OrderRepository
}

func (s *OrderService) CreateOrder(userID int, items []OrderItem) (*Order, error) {
    order := Order{UserID: userID, Items: items, Status: "Pending"}
    return s.repo.Save(order)
}
```

### 6. 事件驱动（Event-Driven）
使用 **领域事件（Domain Events）** 进行解耦，在 Go 语言中可通过 **Channel** 或 **Pub/Sub** 实现：

```go
type OrderCreatedEvent struct {
    OrderID int
}

def publishEvent(event OrderCreatedEvent) {
    go func() {
        eventChannel <- event
    }()
}
```

### 7. 结合 CQRS（命令查询职责分离）
DDD 可结合 CQRS（Command Query Responsibility Segregation），在 Go 语言中可用 **命令（Command）** 处理变更操作，用 **查询（Query）** 处理数据读取：

```go
type CreateOrderCommand struct {
    UserID int
    Items  []OrderItem
}

func (h *OrderHandler) Handle(cmd CreateOrderCommand) (*Order, error) {
    return h.service.CreateOrder(cmd.UserID, cmd.Items)
}
```

### 总结

Go 语言的 DDD 实践强调：

1. **使用 struct 作为领域模型**（Entity、Value Object、Aggregate）
2. **依赖倒置**，通过接口定义领域层，不直接依赖基础设施
3. **使用应用服务（Service）封装业务逻辑**，避免外部直接操作领域对象
4. **事件驱动**，通过 Channel 或 Pub/Sub 进行解耦
5. **结合 CQRS**，实现命令和查询分离，提高可扩展性

通过 DDD 设计模式，可以使得项目业务逻辑更加清晰、模块化，便于扩展和维护。