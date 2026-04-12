# distributed-transactions (Go)

## 模块说明

分布式事务 Saga 模式的 Go 实现。演示编排式 Saga 的正向步骤和补偿动作。

## 设计模式应用

- **命令模式**：reserve/charge/release 是独立命令函数。
- **责任链模式**：Execute 按序调用步骤，失败则补偿。

## 代码结构

```
src/
  saga.go        — SagaCoordinator + InventoryService + PaymentService + SagaOrder
test/
  saga_test.go   — Go 标准测试
```

## 与实际工程对比

| 维度 | 本示例 | Temporal Go SDK |
|---|---|---|
| 协调 | 同步调用 | Workflow + Activity |
| 补偿 | 手动 if-else | defer 补偿回调 |
| 持久化 | 无 | 工作流状态自动持久化 |

## 测试验证

```bash
cd microservice-architecture/distributed-transactions/go
go test ./...
```
