# outbox-pattern (C)

## 模块说明

Outbox 模式的 C 语言实现。用固定数组模拟 orders 表和 outbox 表。

## 设计模式应用

- **命令模式**：OutboxEvent 结构体将事件封装为数据对象。
- **观察者模式**：outbox_relay_pending 扫描 pending 事件并发布到 MemoryBroker。

## 代码结构

```
src/
  func.h     — 头文件，定义所有结构体和函数声明
  outbox.c   — outbox 服务实现（init / create_order / relay_pending）
test/
  test.c     — 验证创建/relay/重跑安全性
```

## 与实际工程对比

| 维度 | 本示例 | 数据库 + 消息队列 |
|---|---|---|
| 存储 | 固定数组 | MySQL / PostgreSQL outbox 表 |
| relay | 同步函数 | SELECT FOR UPDATE + 定时任务 |
| broker | MemoryBroker | Kafka / RabbitMQ |

## 测试验证

```bash
cd microservice-architecture/outbox-pattern/c
cc test/test.c src/*.c -o test.out
./test.out
```
