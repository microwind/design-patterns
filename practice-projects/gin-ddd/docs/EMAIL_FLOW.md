# RocketMQ 事件驱动邮件发送完整实现

## 概览

本文档描述了一个完整的事件驱动邮件系统实现，通过 RocketMQ 作为消息队列，当订单创建时自动发送确认邮件给用户。

## 系统架构

```
┌─────────────┐
│  用户请求   │
└──────┬──────┘
       │ POST /api/orders
       ▼
┌─────────────────────┐
│  OrderController    │ (HTTP 请求处理)
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│  OrderService       │ (业务逻辑)
│  - CreateOrder()    │
│  - 生成订单号       │
│  - 保存到数据库     │
│  - 发布事件         │
└──────┬──────────────┘
       │ 发布事件
       ▼
┌─────────────────────┐
│  RocketMQ Broker    │ (消息队列)
│  Topic:             │
│  order-event-topic  │
└──────┬──────────────┘
       │ 存储消息
       ▼
┌─────────────────────┐
│  RocketMQ Consumer  │ (事件消费)
│  - 订阅事件         │
│  - 处理订单事件     │
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│  MailService        │ (邮件服务)
│  - 生成邮件内容     │
│  - SMTP 发送        │
└──────┬──────────────┘
       │ 发送邮件
       ▼
┌─────────────────────┐
│  SMTP 邮件服务器    │ (QQ/163/其他)
└─────────────────────┘
       │
       ▼
┌─────────────────────┐
│  用户邮箱           │ ✓ 收到邮件
└─────────────────────┘
```

## 关键组件

### 1. OrderEvent 事件

**文件**: `internal/domain/event/order_event.go`

```go
type OrderEvent struct {
    BaseEvent
    OrderID     int64   // 订单ID
    OrderNo     string  // 订单号
    UserID      int64   // 用户ID
    UserEmail   string  // 用户邮箱 (新增)
    UserName    string  // 用户名 (新增)
    TotalAmount float64 // 订单金额
    Status      string  // 订单状态
}
```

### 2. MailService 接口

**文件**: `internal/domain/notification/mail_service.go`

```go
type MailService interface {
    SendOrderConfirmationMail(ctx context.Context, userEmail, userName string,
                            orderData map[string]interface{}) error
    Close() error
}
```

### 3. SMTP 邮件服务实现

**文件**: `internal/infrastructure/mail/smtp_mail_service.go`

- 使用 `github.com/jordan-wright/email` 库发送 SMTP 邮件
- 支持配置 SMTP 服务器地址、端口、认证信息
- 邮件发送失败不影响业务流程

### 4. 邮件模板

**文件**: `internal/infrastructure/mail/mail_template.go`

- HTML 邮件模板
- 包含订单号、金额、状态等信息
- 支持扩展为更复杂的模板

## 配置说明

### config/config.yaml

```yaml
mail:
  enabled: true                          # 是否启用邮件服务
  host: "smtp.qq.com"                   # SMTP 服务器地址
  port: 587                             # SMTP 端口
  username: "your-email@qq.com"         # 发件人邮箱
  password: "your-app-password"         # 邮箱密码或应用密码
  from_email: "your-email@qq.com"       # 发件人邮箱
  from_name: "订单系统"                 # 发件人名称
```

### 常见 SMTP 配置

| 邮箱服务商 | SMTP 服务器 | 端口 | 加密方式 |
|-----------|-----------|------|--------|
| QQ 邮箱   | smtp.qq.com | 587 | STARTTLS |
| 163 邮箱  | smtp.163.com | 587 | STARTTLS |
| Gmail     | smtp.gmail.com | 587 | STARTTLS |
| 阿里企业邮箱 | smtp.mxhichina.com | 587 | STARTTLS |

**获取应用密码**:
- QQ 邮箱: 设置 → 账户 → 生成授权码
- 163 邮箱: 设置 → POP3/IMAP/SMTP → 生成授权码

## 完整业务流程

### 1. 用户创建订单

```bash
POST /api/orders
Content-Type: application/json

{
    "user_id": 1,
    "total_amount": 99.99
}
```

### 2. OrderService 处理订单创建

```go
func (s *OrderService) CreateOrder(ctx context.Context, userID int64, totalAmount float64) {
    // 1. 验证参数
    // 2. 生成订单号
    // 3. 创建订单实体
    // 4. 持久化到数据库
    // 5. 获取用户信息（邮箱、名称）
    // 6. 创建并发布订单事件到 RocketMQ
}
```

### 3. RocketMQ 存储消息

- Topic: `order-event-topic`
- 消息内容: OrderEvent JSON

### 4. Consumer 消费事件

```go
func handleOrderEvent(ctx context.Context, evt event.DomainEvent,
                     mailService notification.MailService) error {
    switch evt.EventType() {
    case "order.created":
        orderEvent := evt.(*event.OrderEvent)
        // 调用邮件服务发送邮件
        mailService.SendOrderConfirmationMail(ctx,
            orderEvent.UserEmail,
            orderEvent.UserName,
            orderData)
    }
}
```

### 5. MailService 发送邮件

```go
func (s *SMTPMailService) SendOrderConfirmationMail(ctx context.Context,
    userEmail, userName string, orderData map[string]interface{}) error {
    // 1. 验证邮箱格式
    // 2. 生成邮件模板
    // 3. 通过 SMTP 发送邮件
    // 4. 记录日志，不重试
}
```

## 关键文件清单

### 新创建文件

| 文件 | 功能描述 |
|-----|--------|
| `internal/domain/notification/mail_service.go` | 邮件服务接口定义 |
| `internal/infrastructure/mail/smtp_mail_service.go` | SMTP 邮件实现 |
| `internal/infrastructure/mail/mail_template.go` | 邮件 HTML 模板 |
| `internal/infrastructure/mail/smtp_mail_service_test.go` | 邮件服务单元测试 |
| `internal/application/service/order/order_service_test.go` | 订单服务集成测试 |

### 修改的文件

| 文件 | 修改内容 |
|-----|--------|
| `go.mod` | 添加 `github.com/jordan-wright/email` 依赖 |
| `config/config.yaml` | 添加 `mail` 配置部分 |
| `internal/infrastructure/config/config.go` | 添加 `MailConfig` 结构体 |
| `internal/domain/event/order_event.go` | 添加 `UserEmail` 和 `UserName` 字段 |
| `internal/application/service/order/order_service.go` | 注入 UserRepository、在事件中包含用户信息 |
| `cmd/server/main.go` | 初始化 MailService、修改事件处理器 |

## 测试

### 运行单元测试

```bash
# 邮件服务单元测试
go test ./internal/infrastructure/mail -v

# 订单服务集成测试
go test ./internal/application/service/order -v

# 所有测试
go test ./...
```

### 测试覆盖的场景

1. **邮件模板渲染**: 验证模板是否正确渲染用户数据
2. **邮箱验证**: 验证邮箱格式校验
3. **类型转换**: 验证数据类型正确转换
4. **订单事件发布**: 验证订单创建时是否发布事件
5. **事件数据完整性**: 验证事件包含用户邮箱和用户名

## 错误处理

### 邮件发送失败

- **处理策略**: 邮件发送失败不影响订单创建，仅记录日志
- **不重试**: 设计上不自动重试，避免重复发送
- **人工处理**: 可通过日志追踪失败原因，由人工重新发送

### 日志输出

```
[INFO] 订单确认邮件发送成功 (收件人: user@example.com, 订单号: ORD123456)
[ERROR] 发送订单确认邮件失败: connection refused
[INFO] 无效的邮箱地址: invalid-email
```

## 常见问题

### 1. 邮件显示为垃圾邮件

**原因**: SPF/DKIM/DMARC 配置不当

**解决方案**:
- 在 DNS 配置 SPF 记录: `v=spf1 include:mxhichina.com ~all`
- 配置邮件服务商的 DKIM
- 使用企业邮箱而不是个人邮箱

### 2. SMTP 连接超时

**原因**: 防火墙阻止或 SMTP 端口错误

**解决方案**:
- 确认防火墙允许 587 端口出站
- 检查邮件服务商是否支持该端口
- 尝试使用 465（SSL）或 25（SMTP）端口

### 3. 认证失败

**原因**: 用户名或密码错误

**解决方案**:
- 确认使用的是应用密码而不是邮箱密码
- 检查 SMTP 用户名是否为完整邮箱地址
- 重新生成应用授权码

### 4. 邮件格式问题

**原因**: 邮件编码或格式不正确

**解决方案**:
- 确保使用 UTF-8 编码
- 测试 HTML 模板是否有语法错误
- 使用简洁的 HTML，避免复杂样式

## 后续改进建议

### 1. 邮件队列

将邮件发送变为异步，创建专门的邮件队列：

```go
// 创建邮件事件
mailEvent := event.NewMailEvent(userEmail, userName, orderData)
// 发布到邮件队列
eventPublisher.Publish(ctx, "mail-event-topic", mailEvent)
```

### 2. 邮件重试机制

使用死信队列处理失败的邮件：

```go
if err := sendMail(ctx); err != nil {
    // 移到死信队列，由定时任务重试
    dlq.Push(mailEvent)
}
```

### 3. 邮件统计

记录邮件发送统计信息：

```go
type MailStatistics struct {
    TotalSent      int
    SuccessCount   int
    FailureCount   int
    FailureRate    float64
}
```

### 4. 多语言支持

支持多种语言的邮件模板：

```go
func GetOrderConfirmationTemplate(language string) *template.Template {
    switch language {
    case "zh_CN":
        return getChineseTemplate()
    case "en_US":
        return getEnglishTemplate()
    default:
        return getChineseTemplate()
    }
}
```

### 5. 邮件模板管理

支持从数据库加载动态邮件模板：

```go
func (s *SMTPMailService) SendMail(ctx context.Context,
    templateID string, recipientEmail string, data map[string]interface{}) error {
    template := s.loadTemplate(templateID)
    // 渲染模板
    // 发送邮件
}
```

### 6. 其他事件邮件

支持更多事件触发的邮件：

```
- order.paid: 发送支付成功邮件
- order.shipped: 发送发货通知邮件
- order.delivered: 发送收货确认邮件
- order.refunded: 发送退款成功邮件
```

## 性能考虑

### 1. 邮件发送并发

SMTP 连接通常是同步的，可以使用 goroutine 并发发送：

```go
go func() {
    mailService.SendOrderConfirmationMail(ctx, email, name, data)
}()
```

### 2. 批量发送

对于批量用户，可以合并邮件发送请求，减少 SMTP 连接次数。

### 3. 缓存

缓存用户信息和邮件模板，减少数据库查询和模板编译。

## 总结

本实现提供了一个完整的事件驱动邮件系统，具有以下特点：

✓ **事件驱动**: 使用 RocketMQ 作为异步通信机制
✓ **解耦设计**: 订单服务和邮件服务解耦
✓ **容错性强**: 邮件发送失败不影响业务流程
✓ **可扩展**: 支持多种邮件模板和事件类型
✓ **易维护**: 清晰的代码结构和文档
✓ **充分测试**: 单元测试和集成测试覆盖

通过本实现，可以快速搭建企业级的邮件通知系统。
