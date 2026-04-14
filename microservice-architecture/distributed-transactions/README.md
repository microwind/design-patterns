# 【分布式事务模式详解】C/Java/JS/Go/Python/TS不同语言实现

# 简介

分布式事务（Distributed Transactions）是微服务架构中解决"跨服务操作执行到一半失败时如何恢复"的核心模式。本模块采用 **Saga 补偿模型**：将跨服务事务拆分为一系列有序步骤，每个步骤都有对应的补偿动作。当某个步骤失败时，按逆序执行已完成步骤的补偿，将系统回退到业务可接受状态。

# 作用

1. **跨服务一致性**：在没有全局锁的情况下，通过补偿机制实现最终一致性。
2. **故障恢复**：步骤失败时自动执行补偿（如释放已预占的库存），避免数据不一致。
3. **业务可接受状态**：补偿的目标不是"回滚到初始状态"，而是达到"业务可接受的一致状态"。

# 实现步骤

1. 定义 Saga 的正向步骤序列：库存预占 → 支付扣款。
2. 为每个正向步骤定义补偿动作：释放库存 → 退款。
3. SagaCoordinator 按序执行正向步骤：
   - 全部成功 → 订单状态 COMPLETED
   - 某步骤失败 → 执行已完成步骤的补偿 → 订单状态 CANCELLED

# 流程图

```text
  SagaCoordinator.execute()
      │
      ▼
  ① 库存预占（reserve）
      │
      ├── 失败 ──► 订单 CANCELLED（无需补偿）
      │
      ▼
  ② 支付扣款（charge）
      │
      ├── 失败 ──► 补偿：释放库存（release）──► 订单 CANCELLED
      │
      ▼
  全部成功 ──► 订单 COMPLETED
```

# 涉及的设计模式

| 设计模式 | 在本模块中的体现 |
|---|---|
| **命令模式（Command Pattern）** | 每个 Saga 步骤（reserve/charge）和补偿动作（release）可以看作一个命令对象，由 Coordinator 按序调度执行。 |
| **责任链模式（Chain of Responsibility）** | 正向步骤按链式顺序执行，任一环节失败则中断链并触发补偿。 |
| **状态模式（State Pattern）** | 订单状态从 PENDING → COMPLETED 或 PENDING → CANCELLED，状态转换由 Coordinator 驱动。 |

# 与实际开源项目对比

| 对比维度 | 本示例 | 实际工程 |
|---|---|---|
| **协调方式** | 编排式（Orchestration）：SagaCoordinator 集中协调 | Seata Saga：编排式 + 状态机引擎；Temporal：工作流引擎 |
| **补偿机制** | 同步调用 release | 异步消息 + 重试 + 幂等 |
| **持久化** | 内存状态 | Saga 状态持久化到数据库，支持宕机恢复 |
| **超时处理** | 无 | 步骤超时 + 自动补偿 |
| **并发控制** | 非线程安全 | 分布式锁 / 乐观锁 |
| **可视化** | 无 | Temporal Dashboard / Seata 控制台 |

> **整体思路一致**：正向步骤 + 补偿动作 + 协调者是所有 Saga 实现的核心骨架。

# 代码

## Java 核心实现

```java
// 命令模式 —— reserve/charge 是正向命令，release 是补偿命令
// 责任链模式 —— 正向步骤按链式顺序执行
// 状态模式 —— 订单状态 PENDING → COMPLETED / CANCELLED
public static class SagaCoordinator {
    public SagaOrder execute(String orderId, String sku, int quantity) {
        // Step 1: 库存预留
        // Step 2: 支付扣款
        // 失败时逆序补偿
    }
}
```

## Go 核心实现

```go
type SagaCoordinator struct { ... }
func (c *SagaCoordinator) Execute(orderID, sku string, quantity int) SagaOrder { ... }
```

## Python 核心实现

```python
class SagaCoordinator:
    """Saga 协调者 —— 命令 + 责任链 + 状态模式"""
    def execute(self, order_id: str, sku: str, quantity: int) -> SagaOrder: ...
```

## JavaScript 核心实现

```javascript
export class SagaCoordinator {
  execute(orderId, sku, quantity) { ... }
}
```

## TypeScript 核心实现

```typescript
export class SagaCoordinator {
  execute(orderId: string, sku: string, quantity: number): SagaOrder { ... }
}
```

## C 核心实现

```c
SagaOrder saga_execute(SagaCoordinator *coord, const char *order_id,
    const char *sku, int quantity);
```

# 测试验证

```bash
# Java
cd microservice-architecture/distributed-transactions/java
javac src/SagaPattern.java test/Test.java && java test.Test

# Go
cd microservice-architecture/distributed-transactions/go
go test ./...

# Python
cd microservice-architecture/distributed-transactions/python
python3 -m unittest discover -s test -p "test_*.py"

# JavaScript
cd microservice-architecture/distributed-transactions/js
node test/test_saga.js

# TypeScript
cd microservice-architecture/distributed-transactions/ts
tsc -p . && node dist/test/test_saga.js

# C
cd microservice-architecture/distributed-transactions/c
cc test/test.c src/*.c -o test.out && ./test.out
```
