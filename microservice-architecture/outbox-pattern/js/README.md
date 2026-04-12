# outbox-pattern (JavaScript)

## 模块说明

Outbox 模式的 JavaScript (ESM) 实现。

## 设计模式应用

- **观察者模式**：relay 扫描 outbox 并发布到 MemoryBroker。
- **命令模式**：outbox 事件对象封装了发布动作。

## 代码结构

```
src/
  outbox.js        — OutboxService + MemoryBroker
test/
  test_outbox.js   — 验证完整流程
```

## 与实际工程对比

| 维度 | 本示例 | BullMQ / Kafka |
|---|---|---|
| 事务 | 内存数组 | 数据库事务 |
| relay | 同步调用 | 定时任务 / CDC |
| broker | MemoryBroker | Kafka / RabbitMQ |

## 测试验证

```bash
cd microservice-architecture/outbox-pattern/js
node test/test_outbox.js
```
