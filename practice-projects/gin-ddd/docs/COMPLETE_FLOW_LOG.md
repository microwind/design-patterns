# 完整流程演示：订单创建 → 入库 → 发送MQ → 发送邮件

## 系统流程日志展示

当用户创建订单时，系统会打印以下完整的日志流程：

```
════════════════════════════════════════════════════════════════════════════════
1️⃣  HTTP 请求到达
════════════════════════════════════════════════════════════════════════════════

[INFO] 2026/02/11 17:29:11 logger.go:36: CreateOrder request: user_id=13 total_amount=232.10

✓ OrderHandler 接收到创建订单请求
  - 用户ID: 13
  - 订单金额: 232.10


════════════════════════════════════════════════════════════════════════════════
2️⃣  订单服务处理 - OrderService.CreateOrder()
════════════════════════════════════════════════════════════════════════════════

[OrderService] 开始创建订单: orderNo=ORD1739267351123456789, userId=13, amount=232.10
[OrderService] 持久化订单到数据库...
[OrderService] 订单入库成功: orderId=22

✓ 订单已保存到数据库
  - 订单ID: 22
  - 订单号: ORD1739267351123456789
  - 用户ID: 13
  - 订单金额: 232.10


════════════════════════════════════════════════════════════════════════════════
3️⃣  发送事件到 RocketMQ - OrderService → EventPublisher
════════════════════════════════════════════════════════════════════════════════

[OrderService] 开始发送订单事件到MQ...
[OrderService] 获取用户信息: email=user@example.com, name=张三

✓ 从数据库获取用户信息
  - 用户邮箱: user@example.com
  - 用户名: 张三

[OrderService] 创建订单事件: type=order.created

✓ 事件对象创建
  - 事件类型: order.created
  - 订单ID: 22
  - 订单号: ORD1739267351123456789
  - 用户邮箱: user@example.com
  - 用户名: 张三
  - 订单金额: 232.10

[RocketMQ Producer] 开始发布事件: topic=order-event-topic, eventType=order.created
[RocketMQ Producer] 序列化事件数据...
[RocketMQ Producer] 序列化成功, 消息体大小: 245 bytes

✓ 事件数据序列化为 JSON
  - 消息大小: 245 字节

[RocketMQ Producer] 创建RocketMQ消息...
[RocketMQ Producer] 发送消息到Broker...
[RocketMQ Producer] 消息发送成功: topic=order-event-topic, msgId=7F000001000000000000000000001234

✓ 消息已发送到 RocketMQ Broker
  - Topic: order-event-topic
  - Message ID: 7F000001000000000000000000001234
  - Event Tag: order.created

[OrderService] 订单事件发送到MQ成功


════════════════════════════════════════════════════════════════════════════════
4️⃣  HTTP 响应返回
════════════════════════════════════════════════════════════════════════════════

[INFO] 2026/02/11 17:29:11 logger.go:36: CreateOrder success: order_id=22, user_id=13
2026/02/11 17:29:11 [POST] /api/orders 127.0.0.1 200 14.2765ms

✓ 订单创建成功响应
  - 状态码: 200
  - 响应时间: 14.28ms


════════════════════════════════════════════════════════════════════════════════
5️⃣  RocketMQ 消费者处理 - EventConsumer 接收消息
════════════════════════════════════════════════════════════════════════════════

（大约 100-500ms 后，由于是异步消费）

[INFO] 2026/02/11 17:29:11 logger.go:36: 处理订单事件: Type=order.created, Data=&{BaseEvent:{...} OrderID:22 ...}

✓ Consumer 接收到事件消息
  - 事件类型: order.created
  - 消息队列偏移量: (自动处理)

[Event Handler] 接收到订单创建事件
[Event Handler] 开始发送确认邮件...
[Event Handler] 邮件收件人: email=user@example.com, name=张三
[Event Handler] 订单信息: orderId=22, orderNo=ORD1739267351123456789, amount=232.10

✓ Event Handler 开始处理事件
  - 订单ID: 22
  - 订单号: ORD1739267351123456789
  - 用户邮箱: user@example.com
  - 用户名: 张三

[Event Handler] 调用MailService.SendOrderConfirmationMail()...


════════════════════════════════════════════════════════════════════════════════
6️⃣  邮件服务处理 - MailService 发送邮件
════════════════════════════════════════════════════════════════════════════════

[MailService] 开始发送订单确认邮件
[MailService] 收件人: 张三 <user@example.com>

✓ 邮件服务初始化
  - 收件人名: 张三
  - 收件人邮箱: user@example.com

[MailService] 邮箱格式验证成功
[MailService] 准备邮件模板数据...
[MailService] 模板数据: orderNo=ORD1739267351123456789, amount=232.10, status=PENDING

✓ 模板数据准备完成
  - 订单号: ORD1739267351123456789
  - 金额: 232.10 元
  - 状态: PENDING (待支付)

[MailService] 生成邮件HTML内容...
[MailService] HTML生成成功, 邮件内容大小: 2847 bytes

✓ 邮件 HTML 内容生成
  - 邮件大小: 2847 字节
  - 包含用户信息和订单详情

[MailService] 创建邮件对象...
[MailService] 邮件对象创建成功: From=订单系统 <noreply@company.com>, To=user@example.com, Subject=订单确认 - 订单号: ORD1739267351123456789

✓ 邮件对象创建
  - 发件人: 订单系统 <noreply@company.com>
  - 收件人: user@example.com
  - 主题: 订单确认 - 订单号: ORD1739267351123456789

[MailService] 连接SMTP服务器: smtp.qq.com:587
[MailService] 发送邮件...
[MailService] 邮件发送成功

✓ SMTP 邮件发送完成
  - SMTP 服务器: smtp.qq.com:587
  - 认证方式: PlainAuth
  - 发送状态: 成功

[INFO] 2026/02/11 17:29:11 logger.go:36: 订单确认邮件发送成功 (收件人: user@example.com, 订单号: ORD1739267351123456789)
[Event Handler] 邮件发送完成


════════════════════════════════════════════════════════════════════════════════
✅ 完整流程总结
════════════════════════════════════════════════════════════════════════════════

时间线:
  T0      → HTTP 请求创建订单
  T0+5ms  → 订单入库完成
  T0+10ms → 事件发送到 RocketMQ
  T0+15ms → HTTP 响应返回 (14.28ms)
  T0+200ms → RocketMQ Consumer 接收消息
  T0+220ms → 邮件发送完成 (到达用户邮箱)

关键指标:
  ✓ 订单创建响应时间: 14.28 ms
  ✓ 事件处理时间: 约 200ms (异步)
  ✓ 邮件发送时间: 约 20ms
  ✓ 总流程时间: 约 220ms

消息流向:
  HTTP 请求
    ↓
  OrderHandler.CreateOrder()
    ↓
  OrderService.CreateOrder()
    ↓ (同步)
  数据库 (订单入库)
    ↓ (同步)
  RocketMQ Producer (发送事件)
    ↓
  HTTP 200 OK (14.28ms 返回)
    ↓ (异步，后台处理)
  RocketMQ Broker
    ↓
  RocketMQ Consumer
    ↓
  Event Handler (handleOrderEvent)
    ↓
  MailService.SendOrderConfirmationMail()
    ↓
  SMTP 服务器
    ↓
  用户邮箱 ✓
```

## 流程特点

### 1. 同步部分 (订单创建 - 14.28ms)
- ✓ 验证输入参数
- ✓ 生成订单号
- ✓ 创建订单实体
- ✓ 数据库持久化
- ✓ 发送事件到 MQ
- ✓ 返回 HTTP 响应

### 2. 异步部分 (邮件发送 - 后台处理)
- ✓ 消费者接收事件
- ✓ 验证邮箱
- ✓ 生成邮件内容
- ✓ 连接 SMTP
- ✓ 发送邮件

### 3. 错误处理
- 邮件发送失败不会影响订单创建
- 记录详细错误日志
- 事件发送失败会记录但不会中断

## 关键日志标签

| 标签 | 来源 | 用途 |
|-----|------|------|
| `[OrderService]` | OrderService | 订单服务业务逻辑 |
| `[RocketMQ Producer]` | RocketMQProducer | 消息发送到 MQ |
| `[Event Handler]` | handleOrderEvent | 事件处理 |
| `[MailService]` | SMTPMailService | 邮件服务 |
| `[INFO]` | Logger | 框架和其他组件日志 |

## 如何查看完整日志

### 方式 1: 标准输出
```bash
go run ./cmd/server/main.go
```

所有 `[OrderService]`、`[RocketMQ Producer]`、`[Event Handler]`、`[MailService]` 的日志会打印到控制台。

### 方式 2: 重定向到文件
```bash
go run ./cmd/server/main.go > app.log 2>&1
```

### 方式 3: 使用 tee 同时输出和保存
```bash
go run ./cmd/server/main.go | tee app.log
```

### 方式 4: 持续监视日志
```bash
go run ./cmd/server/main.go | grep -E "\[OrderService\]|\[RocketMQ\]|\[Event\]|\[MailService\]"
```

## 测试命令

### 1. 启动应用
```bash
go build ./cmd/server && ./server
```

### 2. 创建订单 (在另一个终端)
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": 13,
    "total_amount": 232.10
  }'
```

### 3. 查看响应
```json
{
  "code": 0,
  "message": "订单创建成功",
  "data": {
    "order_id": 22,
    "order_no": "ORD1739267351123456789",
    "user_id": 13,
    "total_amount": 232.10,
    "status": "PENDING",
    "created_time": "2026-02-11T17:29:11+08:00",
    "updated_time": "2026-02-11T17:29:11+08:00"
  }
}
```

## 完整日志输出示例

```
========================================
应用程序启动
========================================
开始加载配置文件...
配置文件加载成功，服务器模式: debug
开始初始化用户数据库连接...
用户数据库连接成功: mysql://localhost:3306/frog
开始初始化订单数据库连接...
订单数据库连接成功: postgres://localhost:5432/seed
初始化数据仓储...
数据仓储初始化完成
初始化邮件服务...
邮件服务初始化成功
RocketMQ 启用状态: true
开始初始化 RocketMQ 生产者...
RocketMQ 生产者初始化成功
启动 RocketMQ 消费者...
初始化应用服务...
应用服务初始化完成
初始化请求处理器...
请求处理器初始化完成
配置路由...
服务器启动成功，监听地址: 0.0.0.0:8080

------- 创建订单 -------

[INFO] CreateOrder request: user_id=13 total_amount=232.10
[OrderService] 开始创建订单: orderNo=ORD1739267351123456789, userId=13, amount=232.10
[OrderService] 持久化订单到数据库...
[OrderService] 订单入库成功: orderId=22
[OrderService] 开始发送订单事件到MQ...
[OrderService] 获取用户信息: email=user@example.com, name=张三
[OrderService] 创建订单事件: type=order.created
[RocketMQ Producer] 开始发布事件: topic=order-event-topic, eventType=order.created
[RocketMQ Producer] 序列化事件数据...
[RocketMQ Producer] 序列化成功, 消息体大小: 245 bytes
[RocketMQ Producer] 创建RocketMQ消息...
[RocketMQ Producer] 发送消息到Broker...
[RocketMQ Producer] 消息发送成功: topic=order-event-topic, msgId=7F000001000000000000000000001234
[OrderService] 订单事件发送到MQ成功
[INFO] CreateOrder success: order_id=22, user_id=13
2026/02/11 17:29:11 [POST] /api/orders 127.0.0.1 200 14.2765ms

------- 邮件处理 (100-500ms 后) -------

[INFO] 处理订单事件: Type=order.created, Data=&{BaseEvent:{...} ...}
[Event Handler] 接收到订单创建事件
[Event Handler] 开始发送确认邮件...
[Event Handler] 邮件收件人: email=user@example.com, name=张三
[Event Handler] 订单信息: orderId=22, orderNo=ORD1739267351123456789, amount=232.10
[Event Handler] 调用MailService.SendOrderConfirmationMail()...
[MailService] 开始发送订单确认邮件
[MailService] 收件人: 张三 <user@example.com>
[MailService] 邮箱格式验证成功
[MailService] 准备邮件模板数据...
[MailService] 模板数据: orderNo=ORD1739267351123456789, amount=232.10, status=PENDING
[MailService] 生成邮件HTML内容...
[MailService] HTML生成成功, 邮件内容大小: 2847 bytes
[MailService] 创建邮件对象...
[MailService] 邮件对象创建成功: From=订单系统 <noreply@company.com>, To=user@example.com, Subject=订单确认 - 订单号: ORD1739267351123456789
[MailService] 连接SMTP服务器: smtp.qq.com:587
[MailService] 发送邮件...
[MailService] 邮件发送成功
[INFO] 订单确认邮件发送成功 (收件人: user@example.com, 订单号: ORD1739267351123456789)
[Event Handler] 邮件发送完成
```

## 总结

通过这个完整的日志展示，我们可以清晰地看到：

1. **同步流程** (14.28ms): 订单创建、入库、MQ 发送
2. **异步流程** (后台): 消费事件、生成邮件、发送 SMTP
3. **错误处理**: 邮件失败不影响主流程
4. **性能指标**: 用户快速得到响应，邮件后台异步发送

这种设计保证了订单创建的高可用性和邮件发送的可靠性！
