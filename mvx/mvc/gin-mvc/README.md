## Gin `MVC`目录结构

```bash
gin-order/
├── cmd
│   └── gin-order/           
│       └── main.go              # 应用入口，启动 Gin 引擎
├── internal
│   ├── controllers              # 控制器层（处理 HTTP 请求）
│   │   └── order
│   │       └── order_controller.go  # Order 模块的控制器
│   ├── services                 # 服务层（业务逻辑处理）
│   │   └── order
│   │       └── order_service.go       # Order 模块的服务实现
│   ├── repository               # 数据访问层（与数据库交互）
│   │   └── order
│   │       └── order_repository.go    # Order 模块的数据访问接口及实现
│   ├── models                   # 模型层（数据结构定义）
│   │   └── order
│   │       └── order.go               # Order 模块的数据模型
│   ├── middleware               # 中间件（如鉴权、日志、请求拦截）
│   │   ├── logging.go             # 日志中间件
│   │   └── auth.go                # 鉴权中间件
│   └── config                   # 配置模块（数据库、服务器等配置）
│       └── config.go                # 应用与环境配置
├── pkg                          # 公共工具包（如响应包装工具）
│   └── response.go              # 响应处理工具方法
├── web                          # 前端资源（模板与静态资源）
│   ├── static                   # 静态资源（CSS、JS、图片）
│   └── templates                # 模板文件（HTML模板）
│       └── order.tmpl           # Order 模块的视图模板（如果需要渲染HTML）
├── go.mod                       # Go 模块管理文件
└── go.sum                       # Go 模块依赖版本锁定
```

## 目录结构说明
### cmd
包含项目的入口文件 main.go，用于初始化 Gin 引擎、加载中间件、注册路由，并启动 HTTP 服务。

### internal/controllers
控制器层负责接收并处理 HTTP 请求。
按业务模块（如 order）分目录，每个控制器定义对应模块的 RESTful 接口（例如订单创建、查询等）。

### internal/services
服务层实现具体的业务逻辑。
每个模块的业务处理代码都放在对应的目录下，控制器会调用服务层来执行业务操作。

### internal/repository
数据访问层负责与数据库进行交互，封装数据的增删改查操作。
每个模块对应的仓储实现都在各自的目录下（例如 order_repository.go）。

### internal/models
模型层定义数据库中的数据结构或业务数据对象。
使用结构体定义各模块的数据模型（例如订单结构）。

### internal/middleware
中间件用于对 HTTP 请求进行预处理，例如日志记录、鉴权、错误处理等，统一在这里定义并在 main.go 中注册。

### internal/config
配置模块集中管理应用的配置（如数据库连接、服务器端口、环境变量等），便于全局使用和维护。

### pkg
存放项目公共的工具和辅助函数，如响应格式封装等，供各模块复用。

## 运行项目
假设已正确配置数据库与环境变量，可使用以下命令启动应用：
```bash
# 初始化模块
$ go mod init github.com/yourname/gin-order

# 安装依赖
$ go get -u github.com/gin-gonic/gin
$ go get -u gorm.io/gorm
$ go get -u gorm.io/driver/mysql

# 运行服务
$ go run cmd/server/main.go

# 测试接口
$ curl -X GET http://localhost:8080/api/orders/1
$ curl -X POST http://localhost:8080/api/orders -d '{"order_no":"20240501"}'

# 运行测试
$ go test -v ./...
```

## Gin MVC 架构
借鉴了传统 MVC 模型，主要分为以下层次：
```text
Controller（接口层） → Service（业务逻辑层） → Repository（数据访问层） → Model（数据模型）
```

### 分层代码
- **控制器层（Controller）**

```go
// internal/controller/order/order.go
package order

import (
    "net/http"
    "strconv"
    "github.com/gin-gonic/gin"
    "github.com/gin-order/internal/model"
    "github.com/gin-order/internal/service/order"
    "github.com/gin-order/internal/pkg/response"
)

type OrderController struct {
    service *order.OrderService
}

func NewOrderController(service *order.OrderService) *OrderController {
    return &OrderController{service: service}
}

func (c *OrderController) GetOrder(ctx *gin.Context) {
    idStr := ctx.Param("id")
    id, _ := strconv.ParseUint(idStr, 10, 64)
    
    order, err := c.service.GetOrderByID(uint(id))
    if err != nil {
        response.Error(ctx, http.StatusNotFound, "Order not found")
        return
    }
    
    response.Success(ctx, order)
}

func (c *OrderController) CreateOrder(ctx *gin.Context) {
    var req model.Order
    if err := ctx.ShouldBindJSON(&req); err != nil {
        response.Error(ctx, http.StatusBadRequest, "Invalid request")
        return
    }
    
    if err := c.service.CreateOrder(&req); err != nil {
        response.Error(ctx, http.StatusInternalServerError, "Create failed")
        return
    }
    
    response.Success(ctx, req)
}
```

- **路由配置**
```go
// cmd/server/main.go
package main

import (
    "github.com/gin-gonic/gin"
    "github.com/gin-order/internal/controller/order"
    "github.com/gin-order/internal/pkg/database"
    "github.com/gin-order/internal/repository/order"
    "github.com/gin-order/internal/service/order"
)

func main() {
    // 初始化数据库
    db := database.NewGORM()
    
    // 依赖注入
    orderRepo := order_repo.NewMySQLOrderRepository(db)
    orderService := order_service.NewOrderService(orderRepo)
    orderController := order_controller.NewOrderController(orderService)
    
    // 创建路由
    r := gin.Default()
    
    // 注册中间件
    r.Use(middleware.Logger())
    
    // 路由分组
    apiGroup := r.Group("/api")
    {
        orderGroup := apiGroup.Group("/orders")
        {
            orderGroup.GET("/:id", orderController.GetOrder)
            orderGroup.POST("", orderController.CreateOrder)
        }
    }
    
    // 启动服务
    r.Run(":8080")
}
```

- **服务层（Service）**
```go
// internal/service/order/service.go
package order

import (
    "github.com/gin-order/internal/model"
    "github.com/gin-order/internal/repository/order"
)

type OrderService struct {
    repo order.OrderRepository
}

func NewOrderService(repo order.OrderRepository) *OrderService {
    return &OrderService{repo: repo}
}

func (s *OrderService) GetOrderByID(id uint) (*model.Order, error) {
    return s.repo.FindByID(id)
}

func (s *OrderService) CreateOrder(order *model.Order) error {
    return s.repo.Create(order)
}
```

- **数据访问层（Repository）**
```go
// internal/repository/order/interface.go
package order

import "github.com/gin-order/internal/model"

type OrderRepository interface {
    FindByID(id uint) (*model.Order, error)
    Create(order *model.Order) error
    FindByStatus(status string) ([]model.Order, error)
}
```

```go
// internal/repository/order/mysql.go
package order

import (
    "gorm.io/gorm"
    "github.com/gin-order/internal/model"
)

type MySQLOrderRepository struct {
    db *gorm.DB
}

func NewMySQLOrderRepository(db *gorm.DB) OrderRepository {
    return &MySQLOrderRepository{db: db}
}

func (r *MySQLOrderRepository) FindByID(id uint) (*model.Order, error) {
    var order model.Order
    if err := r.db.First(&order, id).Error; err != nil {
        return nil, err
    }
    return &order, nil
}

func (r *MySQLOrderRepository) Create(order *model.Order) error {
    return r.db.Create(order).Error
}

func (r *MySQLOrderRepository) FindByStatus(status string) ([]model.Order, error) {
    var orders []model.Order
    if err := r.db.Where("status = ?", status).Find(&orders).Error; err != nil {
        return nil, err
    }
    return orders, nil
}
```

- **模型层（Model）**
```go
// internal/model/order.go
package model

import "time"

type Order struct {
    OrderID     uint      `gorm:"primaryKey;column:order_id"`
    OrderNo     string    `gorm:"uniqueIndex;column:order_no"`
    UserID      uint      `gorm:"index;column:user_id"`
    OrderName   string    `gorm:"column:order_name"`
    Amount      float64   `gorm:"type:decimal(10,2);column:amount"`
    Status      string    `gorm:"column:status"`
    CreatedAt   time.Time `gorm:"column:created_at"`
    UpdatedAt   time.Time `gorm:"column:updated_at"`
}

func (Order) TableName() string {
    return "orders"
}
```

## 最佳实践
### 接口隔离原则

Repository 层通过接口定义，支持多种数据库实现

```go
// 可轻松切换为 Mock 实现
type MockOrderRepository struct {}
func (m *MockOrderRepository) FindByID(id uint) (*model.Order, error) {
    return &model.Order{OrderNo: "mock-123"}, nil
}
```

### 统一响应格式

```go
// pkg/response/response.go
func Success(c *gin.Context, data interface{}) {
    c.JSON(http.StatusOK, gin.H{
        "code":    0,
        "message": "success",
        "data":    data,
    })
}
```

### 中间件链

```go
// 全局中间件
r.Use(gin.Logger(), gin.Recovery())

// 路由组中间件
adminGroup := r.Group("/admin", middleware.AuthJWT())
```

### 数据库迁移
使用 GORM AutoMigrate：

```go
db.AutoMigrate(&model.Order{})
```
通过此结构，Gin 可实现经典MVC 分层，同时保持 Go 语言的简洁高效特性。

## 总结
1. **高性能与轻量级**：Gin 框架专注于提供极高的请求处理速度和低内存占用，非常适合构建高并发的 API 服务和微服务架构。
2. **清晰分层**：采用 MVC 模式，将控制器、业务逻辑（Service）、数据访问层（Repository）和数据模型（Model）分层管理，确保代码结构清晰、职责单一。
3. **快速开发**：简洁的路由定义和中间件机制让开发者可以快速构建和扩展应用，同时利用 GORM 等 ORM 框架简化数据库操作。
4. **易于部署与维护**：Go 语言的编译型特性带来了单一二进制文件的部署方式，同时清晰的项目结构有助于提高代码的可维护性和团队协作效率。
