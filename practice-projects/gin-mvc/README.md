# Gin MVC + RocketMQ 工程脚手架

> 一个基于 Gin 框架 + RocketMQ 消息队列的 MVC（Model-View-Controller）Go 语言工程脚手架

## 🎯 这是什么？

**Gin MVC** 是一个开箱即用的 MVC 工程脚手架，集成 RocketMQ 事件机制，帮助 Go 开发者快速搭建分层清晰、可扩展的企业应用。

### 核心特点

✅ **清晰 MVC 分层** - Controller、Service、Repository、Model 职责明确  
✅ **Gin Web 框架** - 高性能 HTTP 服务  
✅ **RocketMQ 消息队列** - 事件驱动架构，支持异步解耦  
✅ **业务事件机制** - 订单创建、支付、取消等事件自动发布  
✅ **统一响应格式** - 标准化 API 响应结构  
✅ **全局中间件** - request-id、日志、恢复、跨域支持  
✅ **双数据库支持** - 用户库 + 订单库可独立配置  
✅ **易于对比学习** - 与 `practice-projects/gin-ddd` 功能对齐、架构不同

### 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Go | 1.21+ | 编程语言 |
| Gin | 1.9+ | Web 框架 |
| RocketMQ | 5.3+ | 消息队列 |
| MySQL | 8.0+ | 用户库默认 |
| PostgreSQL | 14+ | 订单库默认 |
| YAML | - | 配置文件格式 |

---

## 📁 工程结构

```text
gin-mvc/
├── cmd/main.go                                       # 应用启动入口
├── internal/
│   ├── controllers/                                  # 【Controller 层】
│   │   ├── home/home_controller.go                   # 首页/健康检查
│   │   ├── user/user_controller.go                   # 用户接口
│   │   └── order/order_controller.go                 # 订单接口
│   │
│   ├── services/                                     # 【Service 层】
│   │   ├── user/user_service.go                      # 用户业务逻辑
│   │   ├── order/order_service.go                    # 订单业务逻辑 + 事件发布
│   │   ├── event/order_handler.go                    # 事件消费处理
│   │   └── notification/mail_service.go              # 邮件服务接口
│   │
│   ├── repository/                                   # 【Repository 层】
│   │   ├── db/                                       # DB 连接与方言适配
│   │   ├── user/                                     # 用户仓储
│   │   ├── order/                                    # 订单仓储
│   │   ├── mq/                                       # RocketMQ 实现
│   │   │   ├── rocketmq_producer.go                 # MQ 生产者
│   │   │   └── rocketmq_consumer.go                 # MQ 消费者
│   │   └── mail/smtp_mail_repository.go             # SMTP 邮件实现
│   │
│   ├── models/                                       # 【Model 层】
│   │   ├── user/user.go                              # 用户模型
│   │   ├── order/order.go                            # 订单模型（状态机）
│   │   └── event/                                    # 事件模型定义
│   │       ├── event.go
│   │       ├── order_event.go
│   │       └── user_event.go
│   │
│   ├── middleware/                                   # 中间件
│   └── config/config.go                              # 配置加载与校验
│
├── pkg/
│   ├── logger/logger.go                              # 日志工具
│   └── response/response.go                          # 统一响应工具
├── config/config.yaml                                # 应用配置（含 RocketMQ/邮件）
├── docs/init_user_mysql.sql                          # 用户库初始化脚本
├── docs/init_order_postgres.sql                      # 订单库初始化脚本
└── README.md                                         # 简版项目文档
```

---

## 🚀 快速开始

### 1. 环境准备

确保已安装：
- Go 1.21+
- MySQL 8.0+ 和 PostgreSQL 14+（可按需仅使用其一）
- RocketMQ 5.3+（可选）
- SMTP 邮箱（可选，推荐 QQ 邮箱）

### 2. 启动 RocketMQ（可选）

```bash
# 启动 NameServer
sh bin/mqnamesrv

# 启动 Broker
sh bin/mqbroker -n localhost:9876
```

### 3. 初始化数据库

```bash
# 用户库（MySQL）
mysql -u root -p < docs/init_user_mysql.sql

# 订单库（PostgreSQL）
psql -U postgres -f docs/init_order_postgres.sql
```

### 4. 配置应用

编辑 `config/config.yaml`：

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
  retry_times: 3
  topics:
    order_event: "order-event-topic"
    user_event: "user-event-topic"

mail:
  enabled: false
  host: "smtp.qq.com"
  port: 465
  username: "your@qq.com"
  password: "your-smtp-auth-code"
  from_email: "your@qq.com"
  from_name: "订单系统"
```

本地最小可运行建议：
- 不使用 MQ：`rocketmq.enabled: false`
- 不发邮件：`mail.enabled: false`

### 5. 安装依赖并启动

在 `gin-mvc` 项目根目录执行：

```bash
cd practice-projects/gin-mvc
```

1) 检查 Go 环境

```bash
go version
go env GOPROXY
```

2) 拉取并校验依赖

```bash
go mod tidy
go mod download
go mod verify
```

3) 启动服务（前台）

```bash
go run cmd/main.go
```

也可以使用 Makefile：

```bash
make tidy
make run
```

4) 看到类似日志表示启动成功

```text
config loaded
server started addr=0.0.0.0:8080
```

5) 如果你使用了自定义配置文件路径

```bash
CONFIG_PATH=config/config.yaml go run cmd/main.go
```

常见启动问题：
- `read config failed`：检查 `config/config.yaml` 是否存在、路径是否正确
- `init user db failed` 或 `init order db failed`：检查数据库地址、账号密码、库名
- `rocketmq.nameserver is required`：当 `rocketmq.enabled=true` 时必须配置 `nameserver` 和 `topics.order_event`

### 6. 测试接口

```bash
# 健康检查
curl http://localhost:8080/health

# 创建用户
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "张三",
    "email": "zhangsan@example.com",
    "phone": "13800138000"
  }'

# 更新用户手机号
curl -X PUT http://localhost:8080/api/users/1/phone \
  -H "Content-Type: application/json" \
  -d '{
    "new_phone": "13900139000"
  }'

# 创建订单（total_amount 模式）
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": 1,
    "total_amount": 199.99
  }'

# 支付订单
curl -X PUT http://localhost:8080/api/orders/1/pay
```

---

## 🎨 事件驱动架构

### 事件类型

**订单事件**：
- `order.created`
- `order.paid`
- `order.shipped`
- `order.delivered`
- `order.cancelled`
- `order.refunded`

**用户事件**：
- `user.created`
- `user.deleted`

说明：当前服务默认发布 `order.created`、`order.paid`、`order.cancelled`，其余事件类型已预留。

### 事件流程

```text
HTTP Request
-> OrderController
-> OrderService (入库后发布事件)
-> RocketMQ Producer
-> RocketMQ Topic(order-event-topic)
-> RocketMQ Consumer
-> event handler
-> SMTP MailService (仅 order.created 且 mail.enabled=true)
```

### 代码示例

**发布事件（Service 层）**：
```go
func (s *Service) CreateOrder(ctx context.Context, userID int64, totalAmount float64) (*model.Order, error) {
    o, err := model.New(s.generateOrderNo(), userID, totalAmount)
    if err != nil {
        return nil, err
    }
    if err := s.orderRepo.Create(ctx, o); err != nil {
        return nil, err
    }
    email, name := s.userInfo(ctx, o.UserID)
    s.publishOrderEvent(ctx, event.NewOrderCreated(o.ID, o.OrderNo, o.UserID, email, name, o.TotalAmount), s.orderTopic)
    return o, nil
}
```

**消费事件（Event Handler）**：
```go
func HandleOrderEvent(ctx context.Context, evt modelevent.DomainEvent, mailService notification.MailService) error {
    if evt.EventType() != modelevent.OrderCreatedEvent || mailService == nil {
        return nil
    }
    orderEvt, ok := evt.(*modelevent.OrderEvent)
    if !ok {
        return nil
    }
    return mailService.SendOrderConfirmation(ctx, orderEvt.UserEmail, orderEvt.UserName, map[string]interface{}{
        "order_id":     orderEvt.OrderID,
        "order_no":     orderEvt.OrderNo,
        "total_amount": orderEvt.TotalAmount,
        "status":       orderEvt.Status,
    })
}
```

---

## 🔧 常见问题

- 启动时报 `rocketmq.topics.order_event is required`
  - 原因：`rocketmq.enabled=true` 但未配置 `topics.order_event`
- 订单创建成功但没有发布消息
  - 检查 `rocketmq.enabled`、NameServer/Broker 连通性
- 消息消费正常但邮件未发送
  - 检查 `mail.enabled`、SMTP 授权码、用户邮箱是否有效
- SQL 占位符报错
  - 检查 `database.user.driver`、`database.order.driver` 与目标数据库是否匹配

---

## 📚 相关文档

- 详细 MVC 脚手架文档：`Gin-Framework-MVC-Scaffold.md`
- 英文文档：`Gin-Framework-MVC-Scaffold-en.md`
- DDD 对照版本：`../gin-ddd/README.md`
