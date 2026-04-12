# outbox-pattern (Go)

## 模块说明

Outbox 模式的 Go 实现。演示业务写入+outbox 同事务、relay 异步发布的完整流程。

## 设计模式应用

- **观察者���式**：relay 扫描 outbox 并发布到 MemoryBroker。
- **命令模式**：OutboxEvent 将事件封装为结构体数据对象。

## 代码结构

```
src/
  outbox.go        — OutboxService + Order + OutboxEvent + MemoryBroker
test/
  outbox_test.go   — Go 标准测试
```

## 与实际工程对比

| 维度 | 本示例 | watermill / Kafka |
|---|---|---|
| 事务 | 内存 slice | 数据库事务 |
| relay | 同步调用 | goroutine + 定时器 |
| broker | MemoryBroker | Kafka / NATS |

## 测试验证

```bash
cd microservice-architecture/outbox-pattern/go
go test ./...
```
