# distributed-transactions (Java)

## 模块说明

分布式事务 Saga 模式的 Java 实现。演示编排式 Saga：库存预占 → 支付扣款 → 成功/补偿的完整流程。

## 设计模式应用

- **命令模式（Command Pattern）**：reserve/charge/release 是独立的命令动作，由 SagaCoordinator 调度。
- **责任链模式（Chain of Responsibility）**：正向步骤按链式顺序执行，任一环节失败则中断并补偿。
- **状态模式（State Pattern）**：订单状态 PENDING → COMPLETED / CANCELLED。

## 代码结构

```
src/
  SagaPattern.java  — SagaCoordinator + InventoryService + PaymentService + SagaOrder（内部类）
test/
  Test.java         — 验证成功流程和失败补偿流程
```

## 与实际工程对比

| 维度 | 本示例 | Seata / Temporal |
|---|---|---|
| 协调 | 同步方法调用 | 状态机引擎 / 工作流引擎 |
| 持久化 | 内存状态 | Saga 状态持久化到数据库 |
| 超时 | 无 | 步骤级超时 + 自动补偿 |
| 可视化 | 无 | Dashboard 实时监控 |

## 测试验证

```bash
cd microservice-architecture/distributed-transactions/java
javac src/SagaPattern.java test/Test.java
java test.Test
```
