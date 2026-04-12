# distributed-transactions (JavaScript)

## 模块说明

分布式事务 Saga 模式的 JavaScript (ESM) 实现。

## 设计模式应用

- **命令模式**：reserve/charge/release 是独立方法。
- **责任链模式**：execute 按序调用步骤。

## 代码结构

```
src/
  saga.js        — SagaCoordinator（导出） + InventoryService + PaymentService
test/
  test_saga.js   — 验证成功/补偿流程
```

## 与实际工程对比

| 维度 | 本示例 | Temporal Node SDK |
|---|---|---|
| 协调 | 同步调用 | Workflow + Activity |
| 异步 | 同步方法 | async/await |

## 测试验证

```bash
cd microservice-architecture/distributed-transactions/js
node test/test_saga.js
```
