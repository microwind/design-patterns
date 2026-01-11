# Gin DDD + RocketMQ 工程脚手架

> 一个基于 Gin 框架 + RocketMQ 消息队列的 DDD（领域驱动设计）Go 语言工程脚手架

## 🎯 这是什么？

**Gin DDD** 是一个开箱即用的 DDD 工程脚手架，集成 RocketMQ 实现事件驱动架构，帮助 Go 开发者快速搭建符合领域驱动设计原则的企业级应用。

### 核心特点

✅ **严格的 DDD 四层架构** - 领域层、应用层、基础设施层、接口层分离清晰
✅ **Gin Web 框架** - 高性能的 HTTP 框架
✅ **RocketMQ 消息队列** - 事件驱动架构，支持异步解耦
✅ **领域事件机制** - 订单创建、支付、取消等事件自动发布
✅ **统一响应格式** - 标准化的 API 响应结构
✅ **全局中间件** - 日志、恢复、跨域支持
✅ **数据库支持** - MySQL 和 PostgreSQL 双数据库支持
✅ **简洁清晰** - 代码结构清晰，易于理解和扩展

### 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Go | 1.21+ | 编程语言 |
| Gin | 1.9+ | Web 框架 |
| RocketMQ | 5.3+ | 消息队列 |
| MySQL | 8.0+ | 关系型数据库 |
| PostgreSQL | 14+ | 关系型数据库（可选） |

---

## 📁 工程结构

```
gin-ddd/
├── cmd/server/main.go                                 # 应用启动入口
├── internal/
│   ├── domain/                                        # 【领域层】
│   │   ├── model/                                     # 领域模型
│   │   ├── repository/                                # 仓储接口
│   │   └── event/                                     # 🆕 领域事件定义
│   │       ├── domain_event.go                        # 事件接口
│   │       ├── order_event.go                         # 订单事件
│   │       ├── user_event.go                          # 用户事件
│   │       └── event_publisher.go                     # 事件发布器接口
│   │
│   ├── application/                                   # 【应用层】
│   │   ├── dto/                                       # 数据传输对象
│   │   └── service/                                   # 应用服务（集成事件发布）
│   │
│   ├── infrastructure/                                # 【基础设施层】
│   │   ├── config/                                    # 配置管理
│   │   ├── persistence/                               # 持久化实现
│   │   ├── mq/                                        # 🆕 RocketMQ 实现
│   │   │   ├── rocketmq_producer.go                   # RocketMQ 生产者
│   │   │   └── rocketmq_consumer.go                   # RocketMQ 消费者
│   │   ├── middleware/                                # 中间件
│   │   ├── common/                                    # 通用组件
│   │   └── constants/                                 # 常量定义
│   │
│   └── interfaces/                                    # 【接口层】
│       ├── handler/                                   # HTTP 处理器
│       ├── router/                                    # 路由配置
│       └── vo/                                        # 请求/响应对象
│
├── pkg/utils/                                         # 工具类
├── config/config.yaml                                 # 应用配置（含RocketMQ配置）
├── docs/init.sql                                      # 数据库初始化脚本
└── README.md                                          # 项目文档
```

---

## 🚀 快速开始

### 1. 环境准备

确保已安装：
- Go 1.21+
- MySQL 8.0+ 或 PostgreSQL 14+
- RocketMQ 5.3+ （已启动 NameServer 和 Broker）

### 2. 启动 RocketMQ

```bash
# 启动 NameServer
cd rocketmq
sh bin/mqnamesrv

# 启动 Broker
sh bin/mqbroker -n localhost:9876

# 检查集群状态
sh bin/mqadmin clusterList -n localhost:9876
```

### 3. 初始化数据库

```bash
mysql -u root -p < docs/init.sql
```

### 4. 配置应用

编辑 `config/config.yaml`：

```yaml
server:
  host: "0.0.0.0"
  port: 8080
  mode: "debug"

database:
  driver: "mysql"
  host: "localhost"
  port: 3306
  username: "root"
  password: "your_password"
  database: "gin_ddd"

rocketmq:
  enabled: true
  nameserver: "localhost:9876"         # 你的 NameServer 地址
  group_name: "gin-ddd-group"
  instance_name: "gin-ddd-instance"
  retry_times: 3
  topics:
    order_event: "order-event-topic"
    user_event: "user-event-topic"
```

### 5. 安装依赖并启动

```bash
go mod tidy
go run cmd/server/main.go
```

### 6. 测试接口

```bash
# 健康检查
curl http://localhost:8080/health

# 创建订单（会自动发布订单创建事件到 RocketMQ）
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": 1,
    "items": [
      {
        "product_id": 1,
        "product_name": "商品A",
        "quantity": 2,
        "price": 99.99
      }
    ]
  }'

# 支付订单（会发布订单支付事件）
curl -X PUT http://localhost:8080/api/orders/1/pay
```

---

## 🎨 事件驱动架构

### 领域事件类型

**订单事件**：
- `order.created` - 订单创建
- `order.paid` - 订单支付
- `order.shipped` - 订单发货
- `order.delivered` - 订单送达
- `order.cancelled` - 订单取消
- `order.refunded` - 订单退款

**用户事件**：
- `user.created` - 用户创建
- `user.activated` - 用户激活
- `user.deactivated` - 用户停用
- `user.blocked` - 用户封禁
- `user.deleted` - 用户删除

### 事件流程

```
┌─────────────┐      ┌──────────────┐      ┌─────────────┐
│   用户请求   │ ───> │  应用服务    │ ───> │  领域模型    │
└─────────────┘      └──────────────┘      └─────────────┘
                            │                      │
                            │ 持久化              │ 业务逻辑
                            ▼                      ▼
                     ┌──────────────┐      ┌─────────────┐
                     │   数据库      │      │  事件发布    │
                     └──────────────┘      └─────────────┘
                                                   │
                                                   ▼
                                            ┌─────────────┐
                                            │  RocketMQ   │
                                            └─────────────┘
                                                   │
                                                   ▼
                                            ┌─────────────┐
                                            │  事件消费者  │
                                            └─────────────┘
                                                   │
                                                   ▼
                                     ┌───────────────────────────┐
                                     │ 后续业务处理               │
                                     │ - 库存扣减                │
                                     │ - 发送通知                │
                                     │ - 更新统计                │
                                     └───────────────────────────┘
```

### 代码示例

**发布事件（应用服务层）**：
```go
// 创建订单后发布事件
func (s *OrderService) CreateOrder(ctx context.Context, userID int64, items []orderModel.OrderItem) (*order.OrderDTO, error) {
    // 创建订单实体
    newOrder, err := orderModel.NewOrder(orderNo, userID, items)
    if err != nil {
        return nil, err
    }

    // 持久化订单
    if err := s.orderRepo.Create(ctx, newOrder); err != nil {
        return nil, err
    }

    // 发布订单创建事件
    if s.eventPublisher != nil {
        orderEvent := event.NewOrderCreatedEvent(newOrder.ID, newOrder.OrderNo, newOrder.UserID, newOrder.TotalAmount)
        s.eventPublisher.Publish(ctx, "order-event-topic", orderEvent)
    }

    return order.ToDTO(newOrder), nil
}
```

**消费事件**：
```go
// 处理订单事件
func handleOrderEvent(ctx context.Context, event event.DomainEvent) error {
    switch event.EventType() {
    case "order.created":
        // 触发库存扣减、发送通知等
        log.Println("订单创建事件：可以触发库存扣减、发送通知等")
    case "order.paid":
        // 触发发货流程、更新营销数据等
        log.Println("订单支付事件：可以触发发货流程、更新营销数据等")
    case "order.cancelled":
        // 触发库存回滚、退款流程等
        log.Println("订单取消事件：可以触发库存回滚、退款流程等")
    }
    return nil
}
```

---

## 🔧 配置说明

### RocketMQ 配置

```yaml
rocketmq:
  enabled: true                          # 是否启用 RocketMQ
  nameserver: "localhost:9876"           # NameServer 地址
  group_name: "gin-ddd-group"            # 消费者组名
  instance_name: "gin-ddd-instance"      # 实例名称
  retry_times: 3                         # 重试次数
  topics:
    order_event: "order-event-topic"     # 订单事件主题
    user_event: "user-event-topic"       # 用户事件主题
```

### 禁用 RocketMQ

如果不需要消息队列功能，可以在配置文件中设置：

```yaml
rocketmq:
  enabled: false  # 禁用后系统仍可正常运行，只是不会发布事件
```

---

## 📝 API 接口

### 用户管理

| 方法 | 路径 | 说明 | 事件 |
|------|------|------|------|
| POST | `/api/users` | 创建用户 | user.created |
| GET | `/api/users` | 获取所有用户 | - |
| GET | `/api/users/:id` | 获取用户详情 | - |
| PUT | `/api/users/:id/email` | 更新邮箱 | - |
| PUT | `/api/users/:id/password` | 更新密码 | - |
| PUT | `/api/users/:id/activate` | 激活用户 | user.activated |
| PUT | `/api/users/:id/deactivate` | 停用用户 | user.deactivated |
| PUT | `/api/users/:id/block` | 封禁用户 | user.blocked |
| DELETE | `/api/users/:id` | 删除用户 | user.deleted |

### 订单管理

| 方法 | 路径 | 说明 | 事件 |
|------|------|------|------|
| POST | `/api/orders` | 创建订单 | order.created |
| GET | `/api/orders` | 获取所有订单 | - |
| GET | `/api/orders/:id` | 获取订单详情 | - |
| GET | `/api/users/:user_id/orders` | 获取用户订单 | - |
| PUT | `/api/orders/:id/pay` | 支付订单 | order.paid |
| PUT | `/api/orders/:id/ship` | 订单发货 | order.shipped |
| PUT | `/api/orders/:id/deliver` | 确认送达 | order.delivered |
| PUT | `/api/orders/:id/cancel` | 取消订单 | order.cancelled |
| PUT | `/api/orders/:id/refund` | 订单退款 | order.refunded |

---

## 🎓 DDD 设计原则

### 1. 领域事件在领域层定义

```go
// domain/event/order_event.go
type OrderEvent struct {
    BaseEvent
    OrderID     int64
    OrderNo     string
    UserID      int64
    TotalAmount float64
    Status      string
}
```

### 2. 事件发布器接口在领域层定义，实现在基础设施层

```go
// domain/event/event_publisher.go
type EventPublisher interface {
    Publish(ctx context.Context, topic string, event DomainEvent) error
    Close() error
}

// infrastructure/mq/rocketmq_producer.go
type RocketMQProducer struct {
    producer rocketmq.Producer
}
```

### 3. 应用服务负责编排和事件发布

```go
// application/service/order/order_service.go
type OrderService struct {
    orderRepo      order.OrderRepository
    eventPublisher event.EventPublisher  // 依赖注入
}
```

### 4. 事件发布不影响主流程

```go
// 发布事件失败不会导致业务失败
if s.eventPublisher != nil {
    if err := s.eventPublisher.Publish(ctx, topic, event); err != nil {
        log.Printf("发布事件失败: %v", err)  // 记录日志，不中断流程
    }
}
```

---

## 📚 参考资料

- [领域驱动设计（DDD）](https://en.wikipedia.org/wiki/Domain-driven_design)
- [Gin Web Framework](https://gin-gonic.com/)
- [Apache RocketMQ](https://rocketmq.apache.org/)
- [Event-Driven Architecture](https://martinfowler.com/articles/201701-event-driven.html)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

---

## 📄 许可证

MIT License

---

**作者**: Jarry
**更新日期**: 2026-01-09

如果这个脚手架对你有帮助，欢迎 ⭐ Star 项目！
