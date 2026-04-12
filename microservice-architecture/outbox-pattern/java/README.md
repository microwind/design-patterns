# outbox-pattern (Java)

## 模块说明

Outbox 模式的 Java 实现。演示业务写入+outbox 同事务、relay 异步发布、标记 published 的完整流程。

## 设计模式应用

- **观察者模式（Observer Pattern）**：outbox 事件被 relay 扫描并发布到 MemoryBroker。实际工程中 broker 是 Kafka/RabbitMQ。
- **命令模式（Command Pattern）**：OutboxEvent 将"需要发布的事件"封装为数据对象，relay 后续异步执行发布。

## 代码结构

```
src/
  OutboxPattern.java  — OutboxService + Order + OutboxEvent + MemoryBroker（内部类）
test/
  Test.java           — 验证创建/relay/重跑安全性
```

## 与实际工程对比

| 维度 | 本示例 | Debezium / Eventuate |
|---|---|---|
| 事务 | 内存列表模拟 | 数据库事务（BEGIN/COMMIT） |
| relay | 同步方法调用 | CDC 监听 / 定时轮询 |
| broker | MemoryBroker | Kafka / RabbitMQ |
| 去重 | status 字段检查 | 消费端幂等 |

## 测试验证

```bash
cd microservice-architecture/outbox-pattern/java
javac src/OutboxPattern.java test/Test.java
java test.Test
```
