# distributed-transactions (Python)

## 模块说明

分布式事务 Saga 模式的 Python 实现。使用 dataclass 定义订单实体。

## 设计模式应用

- **命令模式**：reserve/charge/release 是独立方法。
- **责任链模式**：execute 按序调用，失败则补偿。

## 代码结构

```
src/
  __init__.py  — 包初始化
  saga.py      — SagaCoordinator + InventoryService + PaymentService + SagaOrder
test/
  test_saga.py — unittest 验证成功/补偿流程
```

## 与实际工程对比

| 维度 | 本示例 | Temporal Python SDK |
|---|---|---|
| 协调 | 同步调用 | async Workflow + Activity |
| 持久化 | 无 | 自动持久化 |

## 测试验证

```bash
cd microservice-architecture/distributed-transactions/python
python3 -m unittest discover -s test -p "test_*.py"
```
