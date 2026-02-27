# 一款 Go 语言 Gin 框架 MVC 脚手架，适合快速搭建项目

> 一个开箱即用的 MVC（Model-View-Controller）Go 脚手架，基于 Gin + RocketMQ，包含双数据库、统一响应、中间件与事件驱动示例。

## 这是什么

Gin-Framework-MVC 是一个面向 Go 语言的 MVC 工程脚手架，帮你快速搭建分层清晰的 Web 服务。项目内置用户与订单示例、RocketMQ 生产/消费、邮件通知示例、统一响应与中间件，适合作为团队工程模板或教学示例。

功能与 `practice-projects/gin-ddd`对齐，可以对比下`DDD`与`MVC`工程目录结构的差异。

## 为什么要用 MVC？

很多人会在 Go 项目里直接把路由、业务、数据访问写在一起，小项目可以跑得很快，但模块一多就容易失控。MVC 的价值在于把职责拆开：Controller 负责 HTTP 交互，Service 承载业务规则，Repository 专注数据访问，Model 维护核心状态与行为。

总之，是否采用 MVC 和语言无关，重点在于业务复杂度与团队协作成本。对于中小型到中大型业务系统，MVC 能在“足够清晰”和“实现成本”之间取得很好平衡。

源码地址：[https://github.com/microwind/design-patterns/tree/main/practice-projects/gin-mvc](https://github.com/microwind/design-patterns/tree/main/practice-projects/gin-mvc)

项目目录：`gin-mvc/`

## 核心特点

- 清晰 MVC 分层：Controller、Service、Repository、Model
- Gin Web 框架：高性能 HTTP 服务
- 事件驱动：业务事件 + RocketMQ 生产者/消费者
- 双数据库支持：用户库 + 订单库可独立配置（默认 MySQL + PostgreSQL）
- 统一响应格式：Response 封装，错误码按业务域划分
- 全局中间件：请求 ID、日志、恢复、跨域
- 可选邮件通知：订单创建事件驱动 SMTP 邮件发送

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Go | 1.21+ | 语言版本 |
| Gin | 1.9+ | HTTP 框架 |
| RocketMQ | 5.3+ | 事件消息队列 |
| MySQL | 8.0+ | 用户库默认 |
| PostgreSQL | 14+ | 订单库默认 |
| YAML | - | 配置文件格式 |

## 工程结构

### 工程结构图

```mermaid
flowchart LR
    Client[客户端]

    subgraph Web["Web 入口"]
        Router[Gin Router]
        Middleware[Middleware]
    end

    subgraph MVC["MVC 核心"]
        Controller[Controller]
        Service[Service]
        Model[Model]
        View[View JSON/Template ]
    end

    subgraph Repo["数据访问"]
        Repository[Repository]
        UserDB[(MySQL 用户库)]
        OrderDB[(PostgreSQL 订单库)]
    end

    subgraph Event["事件与通知"]
        Producer[RocketMQ Producer]
        Topic[(RocketMQ Topic)]
        Consumer[RocketMQ Consumer]
        Handler[Event Handler]
        Mail[SMTP Mail]
    end

    Client --> Router
    Router --> Middleware
    Middleware --> Controller
    Controller --> Service
    Service --> Model
    Service --> Repository
    Repository --> UserDB
    Repository --> OrderDB
    Controller --> View
    View --> Client

    Service -.发布事件.-> Producer
    Producer --> Topic
    Topic --> Consumer
    Consumer --> Handler
    Handler --> Mail
```

### 工程结构列表

```
gin-mvc/
├── cmd/main.go                                  # 启动入口，装配各层并启动 HTTP + MQ
├── config/config.yaml                            # 应用配置
├── docs/
│   ├── init_user_mysql.sql                       # MySQL 用户库初始化脚本
│   └── init_order_postgres.sql                   # PostgreSQL 订单库初始化脚本
├── internal/
│   ├── controllers/                              # Controller 层（HTTP 处理）
│   │   ├── home/home_controller.go
│   │   ├── order/order_controller.go
│   │   └── user/user_controller.go
│   ├── services/                                 # Service 层（业务编排）
│   │   ├── order/order_service.go                # 订单服务（状态流转 + 发布事件）
│   │   ├── user/user_service.go                  # 用户服务
│   │   ├── event/order_handler.go                # 事件消费处理
│   │   └── notification/mail_service.go          # 邮件服务接口
│   ├── repository/                               # Repository 层（数据访问与外部依赖）
│   │   ├── db/                                   # DB 连接与方言占位符
│   │   ├── order/
│   │   │   ├── order_repository.go
│   │   │   └── order_sql_repository.go
│   │   ├── user/
│   │   │   ├── user_repository.go
│   │   │   └── user_sql_repository.go
│   │   ├── mq/                                   # RocketMQ 实现
│   │   │   ├── rocketmq_producer.go
│   │   │   └── rocketmq_consumer.go
│   │   └── mail/smtp_mail_repository.go          # SMTP 邮件实现
│   ├── models/                                   # Model 层（核心模型与事件）
│   │   ├── order/order.go
│   │   ├── user/user.go
│   │   └── event/
│   │       ├── event.go
│   │       ├── order_event.go
│   │       └── user_event.go
│   ├── middleware/                               # Gin 中间件
│   │   ├── request_id.go
│   │   ├── logging.go
│   │   ├── recovery.go
│   │   ├── cors.go
│   │   └── context.go
│   └── config/config.go                          # 配置加载与校验
├── pkg/
│   ├── logger/logger.go                          # 日志工具
│   └── response/response.go                      # 统一响应
└── web/templates/order.tmpl                      # 示例页面模板
```

## 各层职责说明

| 层级 | 位置 | 职责 | 关键原则 |
|------|------|------|---------|
| Model 层 | `internal/models/` | 核心业务对象、状态机与事件模型 | 聚焦业务语义，不依赖 HTTP/DB 细节 |
| Service 层 | `internal/services/` | 编排业务流程、状态流转、事件发布 | 业务规则集中，避免散落到 Controller |
| Repository 层 | `internal/repository/` | DB 访问、MQ/SMTP 等外部系统集成 | 只做 IO 与持久化，不承载业务规则 |
| Controller 层 | `internal/controllers/` | HTTP 请求解析、参数校验、响应输出 | 轻量薄层，不直接操作数据库 |

## 快速开始

### 1. 环境准备

- Go 1.21+
- MySQL 8.0+ 与 PostgreSQL 14+（或自行选择其一）
- RocketMQ 5.3+（可选）
- SMTP 邮箱（可选，推荐 QQ 邮箱）

### 2. 初始化数据库

默认配置使用双数据库：
- 用户库：MySQL（默认库名 `gin_mvc_user`）
- 订单库：PostgreSQL（默认库名 `gin_mvc_order`）

执行初始化脚本：

```bash
mysql -u root -p < docs/init_user_mysql.sql
psql -U postgres -f docs/init_order_postgres.sql
```

数据库适配注意：
- 当前仓储已同时支持 MySQL 与 PostgreSQL 占位符
- 切换驱动时，确保 `config/config.yaml` 的 `database.user.driver` 与 `database.order.driver` 正确

### 3. 配置应用

编辑 `config/config.yaml`，至少配置数据库与 RocketMQ：

```yaml
server:
  host: "0.0.0.0"
  port: 8080
  mode: "debug"

database:
  user:
    driver: "mysql"
    host: "localhost"
    port: 3306
    username: "root"
    password: "your_password"
    database: "gin_mvc_user"
  order:
    driver: "postgres"
    host: "localhost"
    port: 5432
    username: "postgres"
    password: "your_password"
    database: "gin_mvc_order"

rocketmq:
  enabled: true
  nameserver: "localhost:9876"
  group_name: "gin-mvc-group"
  instance_name: "gin-mvc-instance"
  topics:
    order_event: "order-event-topic"
```

说明：
- `rocketmq.enabled: true` 才会初始化生产者与消费者
- `rocketmq.topics.order_event` 不能为空，否则配置校验会失败
- 若暂不使用 MQ，可设置 `rocketmq.enabled: false`

最小可运行配置（本地联调推荐）：
- `database.user` 和 `database.order` 配置正确可连通
- `rocketmq.enabled: false`（如果暂不使用消息）
- `mail.enabled: false`（如果暂不发邮件）

### 4. 启动 RocketMQ（可选）

```bash
sh bin/mqnamesrv
sh bin/mqbroker -n localhost:9876
```

### 5. 启动应用

```bash
go mod tidy
go run cmd/main.go
```

### 6. 验证接口

```bash
curl http://localhost:8080/health
curl http://localhost:8080/api/users
curl http://localhost:8080/api/orders
```

## API 一览

### 用户接口

- `POST /api/users`
- `GET /api/users`
- `GET /api/users/:id`
- `PUT /api/users/:id/email`
- `PUT /api/users/:id/phone`
- `DELETE /api/users/:id`
- `GET /api/users/:id/orders`

示例：

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"张三","email":"zhangsan@example.com","phone":"13800138000"}'
```

### 订单接口

- `POST /api/orders`
- `GET /api/orders`
- `GET /api/orders/:id`
- `PUT /api/orders/:id/pay`
- `PUT /api/orders/:id/ship`
- `PUT /api/orders/:id/deliver`
- `PUT /api/orders/:id/cancel`
- `PUT /api/orders/:id/refund`

示例：

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"user_id":1,"total_amount":99.99}'
```

## 配置说明

`config/config.yaml` 主要分区：

- `server`：主机、端口、Gin 模式、超时
- `database.user`：用户库连接
- `database.order`：订单库连接
- `log`：日志级别与输出格式（text/json）
- `rocketmq`：开关、nameserver、group、topic
- `mail`：SMTP 开关与账号配置

## 如何基于脚手架开发新功能

示例：新增“商品管理”模块

步骤 1：新增模型 `internal/models/product/product.go`

```go
package product

import "time"

type Product struct {
	ID        int64
	Name      string
	Price     float64
	Stock     int
	CreatedAt time.Time
	UpdatedAt time.Time
}
```

步骤 2：新增仓储接口 `internal/repository/product/product_repository.go`

```go
package product

import (
	"context"
	"gin-mvc/internal/models/product"
)

type Repository interface {
	Create(ctx context.Context, p *product.Product) error
	Update(ctx context.Context, p *product.Product) error
	FindByID(ctx context.Context, id int64) (*product.Product, error)
	FindAll(ctx context.Context) ([]*product.Product, error)
}
```

步骤 3：新增仓储实现 `internal/repository/product/product_sql_repository.go`

```go
package product

import (
	"context"
	"database/sql"
	"gin-mvc/internal/models/product"
)

type SQLRepository struct {
	db *sql.DB
}

func NewSQLRepository(db *sql.DB) *SQLRepository {
	return &SQLRepository{db: db}
}

func (r *SQLRepository) Create(ctx context.Context, p *product.Product) error {
	_, err := r.db.ExecContext(ctx,
		`INSERT INTO products (name, price, stock, created_at, updated_at) VALUES (?, ?, ?, ?, ?)`,
		p.Name, p.Price, p.Stock, p.CreatedAt, p.UpdatedAt,
	)
	return err
}
```

步骤 4：新增业务服务 `internal/services/product/product_service.go`

```go
package product

import (
	"context"
	"time"
	"gin-mvc/internal/models/product"
	productRepo "gin-mvc/internal/repository/product"
)

type Service struct {
	repo productRepo.Repository
}

func New(repo productRepo.Repository) *Service {
	return &Service{repo: repo}
}

func (s *Service) Create(ctx context.Context, name string, price float64, stock int) error {
	p := &product.Product{
		Name:      name,
		Price:     price,
		Stock:     stock,
		CreatedAt: time.Now(),
		UpdatedAt: time.Now(),
	}
	return s.repo.Create(ctx, p)
}
```

步骤 5：新增 Controller 和路由

将 Controller 放到 `internal/controllers/product/`，并在 `cmd/main.go` 中注册路由组 `api.Group("/products")`。

步骤 6：新增数据库表

```sql
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## 事件驱动与 RocketMQ

### 事件类型

订单事件：
- order.created
- order.paid
- order.shipped
- order.delivered
- order.cancelled
- order.refunded

用户事件：
- user.created
- user.deleted

### 消息流转

```
HTTP 请求 -> Controller -> Service -> Model/Repository
            -> 发布 DomainEvent -> RocketMQ Producer
            -> RocketMQ Broker -> Consumer
            -> 事件处理 -> 发送邮件/触发后续流程
```

订单创建场景（更具体）：

```text
HTTP 请求
-> OrderController
-> OrderService（入库后发布事件）
-> RocketMQ Producer
-> RocketMQ Topic(order-event-topic)
-> RocketMQ Consumer
-> event handler
-> SMTP MailService（order.created 时发送确认邮件）
```

### 事件发布与消费关键点

- 订单创建、支付、取消流程会尝试发布订单事件（发布失败不会中断主业务）
- 消费端按 Tag 前缀解析消息：`order.*` 映射 `OrderEvent`，`user.*` 映射 `UserEvent`
- 订单邮件通知仅在 `order.created` 且启用邮件时触发

## 邮件发送配置（QQ 邮箱）

在 `config/config.yaml` 中开启邮件配置：

```yaml
mail:
  enabled: true
  host: "smtp.qq.com"
  port: 465
  username: "your@qq.com"
  password: "你的SMTP授权码"
  from_email: "your@qq.com"
  from_name: "订单系统"
```

注意事项：
- 必须使用 SMTP 授权码，不是 QQ 登录密码
- 端口 465 使用 TLS，端口 587 使用 STARTTLS
- 收件人取自用户表中的 `email` 字段

## 常见问题排查

- 开启了 RocketMQ 但没有消息发布
  - 检查 `rocketmq.enabled`、NameServer/Broker 连接、`topics.order_event` 配置
- 订单事件已消费但邮件未发送
  - 检查 `mail.enabled`、SMTP 授权码、用户邮箱字段是否正确
- 消费者没有收到消息
  - 检查 Topic 与 Tag 是否匹配、Consumer Group 是否正常
- 数据库报占位符或驱动错误
  - 检查 `database.*.driver` 与目标数据库是否匹配

## 开发规范

命名建议：
- 模型：名词，如 `Order`、`User`
- 服务：`Service` 或 `XxxService`
- 仓储接口：`Repository`
- 仓储实现：`SQLRepository`
- 控制器：`Controller` 或 `XxxController`

分层原则：
- Controller 只处理 HTTP 参数与响应
- Service 负责业务规则、流程编排与事件发布
- Repository 负责数据访问与外部依赖调用
- Model 负责领域状态和对象行为

## 对比 DDD 版本

和 `practice-projects/gin-ddd` 对比：

- 功能面保持一致：用户/订单/MQ/邮件/双库
- 分层方式改为 MVC：更易理解和教学展示
- 依赖注入在 `cmd/main.go` 统一装配，便于快速定位启动流程

## 常用命令

```bash
go mod tidy
go test ./...
```

## 源码地址

[https://github.com/microwind/design-patterns/tree/main/practice-projects/gin-mvc](https://github.com/microwind/design-patterns/tree/main/practice-projects/gin-mvc)

[https://github.com/microwind/design-patterns/tree/main/practice-projects/gin-ddd](https://github.com/microwind/design-patterns/tree/main/practice-projects/gin-ddd)
