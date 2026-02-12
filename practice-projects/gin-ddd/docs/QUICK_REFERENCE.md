# 快速参考指南：完整流程日志演示

## 📋 文件概览

### 增强日志的关键文件

| 文件 | 修改内容 | 日志前缀 |
|-----|--------|--------|
| `order_service.go` | CreateOrder 方法 | `[OrderService]` |
| `rocketmq_producer.go` | Publish 方法 | `[RocketMQ Producer]` |
| `cmd/server/main.go` | handleOrderEvent 方法 | `[Event Handler]` |
| `smtp_mail_service.go` | SendOrderConfirmationMail 方法 | `[MailService]` |

## 🚀 快速启动

### 1. 编译应用
```bash
go build ./cmd/server -o server
```

### 2. 启动应用
```bash
./server
```

应用输出示例：
```
========================================
应用程序启动
========================================
配置文件加载成功，服务器模式: debug
用户数据库连接成功: mysql://localhost:3306/frog
订单数据库连接成功: postgres://localhost:5432/seed
邮件服务初始化成功
RocketMQ 生产者初始化成功
启动 RocketMQ 消费者...
服务器启动成功，监听地址: 0.0.0.0:8080
```

### 3. 创建订单
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": 13,
    "total_amount": 232.10
  }'
```

## 📊 完整日志流程

### 阶段 1: HTTP 请求处理 (0ms)
```
[INFO] CreateOrder request: user_id=13 total_amount=232.10
↓
OrderHandler → OrderService.CreateOrder()
```

### 阶段 2: 订单创建和入库 (0-5ms)
```
[OrderService] 开始创建订单: orderNo=ORD..., userId=13, amount=232.10
[OrderService] 持久化订单到数据库...
[OrderService] 订单入库成功: orderId=22
↓
✓ 订单已保存到数据库
```

### 阶段 3: 事件发送到 RocketMQ (5-10ms)
```
[OrderService] 开始发送订单事件到MQ...
[OrderService] 获取用户信息: email=user@example.com, name=张三
[OrderService] 创建订单事件: type=order.created

[RocketMQ Producer] 开始发布事件: topic=order-event-topic, eventType=order.created
[RocketMQ Producer] 序列化事件数据...
[RocketMQ Producer] 序列化成功, 消息体大小: 245 bytes
[RocketMQ Producer] 创建RocketMQ消息...
[RocketMQ Producer] 发送消息到Broker...
[RocketMQ Producer] 消息发送成功: topic=order-event-topic, msgId=7F00...

[OrderService] 订单事件发送到MQ成功
↓
✓ 事件已发送到 MQ Broker
```

### 阶段 4: HTTP 响应返回 (10-15ms)
```
[INFO] CreateOrder success: order_id=22, user_id=13
2026/02/11 17:29:11 [POST] /api/orders 127.0.0.1 200 14.2765ms
↓
✓ 用户立即获得响应 (14.28ms)
```

### 阶段 5: 异步邮件处理 (100-500ms，后台)
```
[INFO] 处理订单事件: Type=order.created, Data=...
↓
[Event Handler] 接收到订单创建事件
[Event Handler] 开始发送确认邮件...
[Event Handler] 邮件收件人: email=user@example.com, name=张三
[Event Handler] 订单信息: orderId=22, orderNo=ORD..., amount=232.10
[Event Handler] 调用MailService.SendOrderConfirmationMail()...

[MailService] 开始发送订单确认邮件
[MailService] 收件人: 张三 <user@example.com>
[MailService] 邮箱格式验证成功
[MailService] 准备邮件模板数据...
[MailService] 模板数据: orderNo=ORD..., amount=232.10, status=PENDING

[MailService] 生成邮件HTML内容...
[MailService] HTML生成成功, 邮件内容大小: 2847 bytes

[MailService] 创建邮件对象...
[MailService] 邮件对象创建成功:
  From=订单系统 <noreply@company.com>,
  To=user@example.com,
  Subject=订单确认 - 订单号: ORD...

[MailService] 连接SMTP服务器: smtp.qq.com:587
[MailService] 发送邮件...
[MailService] 邮件发送成功

[INFO] 订单确认邮件发送成功 (收件人: user@example.com, 订单号: ORD...)
[Event Handler] 邮件发送完成
↓
✓ 邮件已发送到用户邮箱
```

## 📈 性能指标

| 指标 | 时间 | 说明 |
|-----|------|------|
| 同步处理 (订单创建到 HTTP 响应) | ~14ms | 用户快速得到响应 |
| 异步处理 (消费事件) | ~100ms | 后台处理，不阻塞用户 |
| 邮件发送 | ~20ms | SMTP 网络延迟 |
| 总端到端时间 | ~120-500ms | 取决于网络状况 |

## 🔍 日志搜索技巧

### 只看订单服务日志
```bash
./server | grep "\[OrderService\]"
```

### 只看 RocketMQ 日志
```bash
./server | grep "\[RocketMQ Producer\]"
```

### 只看邮件服务日志
```bash
./server | grep "\[MailService\]"
```

### 只看事件处理日志
```bash
./server | grep "\[Event Handler\]"
```

### 按时间戳查看
```bash
./server | grep "2026/02/11 17:29"
```

### 输出到文件便于分析
```bash
./server > app.log 2>&1
tail -f app.log | grep "\[OrderService\]"
```

## 🎯 关键日志点

### CreateOrder 流程追踪
```
1. [INFO] CreateOrder request:          ← HTTP 请求到达
2. [OrderService] 开始创建订单         ← 服务开始处理
3. [OrderService] 持久化订单到数据库   ← 数据库操作
4. [OrderService] 订单入库成功         ← 数据库成功
5. [OrderService] 开始发送订单事件到MQ ← 事件发送开始
6. [RocketMQ Producer] 开始发布事件    ← MQ 序列化
7. [RocketMQ Producer] 消息发送成功    ← MQ 发送成功
8. [OrderService] 订单事件发送到MQ成功 ← 完成标记
9. [INFO] CreateOrder success          ← HTTP 响应返回
```

### 邮件流程追踪
```
1. [INFO] 处理订单事件                  ← 消费者接收
2. [Event Handler] 接收到订单创建事件   ← 事件处理开始
3. [Event Handler] 开始发送确认邮件    ← 邮件处理开始
4. [MailService] 开始发送订单确认邮件  ← 邮件服务处理
5. [MailService] 邮箱格式验证成功      ← 验证通过
6. [MailService] 生成邮件HTML内容      ← 模板生成
7. [MailService] HTML生成成功          ← 生成完成
8. [MailService] 创建邮件对象          ← 邮件对象创建
9. [MailService] 连接SMTP服务器        ← SMTP 连接
10. [MailService] 发送邮件             ← 邮件发送
11. [MailService] 邮件发送成功         ← 发送完成
12. [INFO] 订单确认邮件发送成功        ← 确认成功
```

## ⚙️ 配置说明

### RocketMQ 配置 (config/config.yaml)
```yaml
rocketmq:
  enabled: true
  nameserver: "localhost:9876"
  group_name: "gin-ddd-group"
  instance_name: "gin-ddd-instance"
  retry_times: 3
  topics:
    order_event: "order-event-topic"
```

### 邮件配置 (config/config.yaml)
```yaml
mail:
  enabled: true
  host: "smtp.qq.com"
  port: 587
  username: "your-email@qq.com"
  password: "your-app-password"
  from_email: "your-email@qq.com"
  from_name: "订单系统"
```

## 🔧 故障排查

### 问题 1: 没有看到邮件日志
**可能原因**:
- RocketMQ 没启动
- 邮件服务未启用 (`mail.enabled: false`)
- Consumer 没有启动

**解决**:
```bash
# 检查 RocketMQ
jps | grep BrokerStartup

# 检查配置
grep "mail:" config/config.yaml
```

### 问题 2: "订单入库失败"
**可能原因**:
- 数据库连接问题
- 数据库不存在

**解决**:
```bash
# 检查数据库连接
mysql -h localhost -u frog_admin -p
psql -h localhost -U postgres
```

### 问题 3: "邮件发送失败"
**可能原因**:
- SMTP 服务器配置错误
- 用户邮箱无效
- 网络问题

**解决**:
```bash
# 测试 SMTP 连接
telnet smtp.qq.com 587

# 检查日志中的用户邮箱
grep "邮件收件人:" app.log
```

## 📝 完整示例请求和响应

### 请求
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": 13,
    "total_amount": 232.10
  }'
```

### 响应 (立即返回，~14ms)
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

## 📚 相关文档

- 详细流程说明: `docs/COMPLETE_FLOW_LOG.md`
- 邮件系统设计: `docs/EMAIL_FLOW.md`
- API 文档: `docs/API.md`

## 总结

通过详细的日志输出，整个流程变得清晰可见：

✅ **同步部分** (14ms): 用户快速得到响应
✅ **异步部分** (后台): 邮件在后台异步发送
✅ **错误隔离**: 邮件失败不影响订单
✅ **可观测性**: 完整的日志追踪
✅ **性能优化**: 充分利用异步处理

这种设计确保了系统的高可用性和良好的用户体验！
