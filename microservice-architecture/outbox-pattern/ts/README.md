# outbox-pattern (TypeScript)

## 模块说明

Outbox 模式的 TypeScript 实现。通过 Order 和 OutboxEvent 类型保证事件结构的编译期安全。

## 设计模式应用

- **观察者模式**：relay 扫描 outbox 并发布到 MemoryBroker。
- **命令模式**：OutboxEvent 类型将事件封装为类型安全的数据对象。

## 代码结构

```
src/
  outbox.ts        — Order 类型 + OutboxEvent 类型 + OutboxService + MemoryBroker
test/
  test_outbox.ts   — 验证完整流程
dist/              — tsc 编译输出
```

## 与实际工程对比

| 维度 | 本示例 | BullMQ (TS) |
|---|---|---|
| 类型安全 | TypeScript type | TypeScript 原生支持 |
| 事务 | 内存数组 | 数据库事务 |
| relay | 同步调用 | Worker 异步处理 |

## 测试验证

```bash
cd microservice-architecture/outbox-pattern/ts
tsc -p .
node dist/test/test_outbox.js
```
