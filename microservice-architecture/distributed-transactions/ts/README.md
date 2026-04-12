# distributed-transactions (TypeScript)

## 模块说明

分布式事务 Saga 模式的 TypeScript 实现。通过 SagaOrder 类型和 private/readonly 修饰符提供类型安全。

## 设计模式应用

- **命令模式**：reserve/charge/release 方法是独立命令。
- **责任链模式**：execute 按序调用，失败则补偿。

## 代码结构

```
src/
  saga.ts        — SagaOrder 类型 + SagaCoordinator + InventoryService + PaymentService
test/
  test_saga.ts   — 验证成功/补偿流程
dist/            — tsc 编译输出
```

## 与实际工程对比

| 维度 | 本示例 | Temporal TS SDK |
|---|---|---|
| 类型安全 | TypeScript type | 原生 TypeScript |
| 协调 | 同步调用 | Workflow + Activity |

## 测试验证

```bash
cd microservice-architecture/distributed-transactions/ts
tsc -p .
node dist/test/test_saga.js
```
