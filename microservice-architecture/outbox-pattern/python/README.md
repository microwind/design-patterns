# outbox-pattern (Python)

## 模块说明

Outbox 模式的 Python 实现。使用 dataclass 定义事件结构。

## 设计模式应用

- **观察者模式**：relay 扫描 outbox 并发布到 MemoryBroker。
- **命令模式**：OutboxEvent dataclass 将事件封装为数据对象。

## 代码结构

```
src/
  __init__.py    — 包初始化
  outbox.py      — OutboxService + Order + OutboxEvent + MemoryBroker
test/
  test_outbox.py — unittest 验证完整流程
```

## 与实际工程对比

| 维度 | 本示例 | Django + Celery |
|---|---|---|
| 事务 | 内存列表 | Django ORM 事务 |
| relay | 同步调用 | Celery 异步任务 |
| broker | MemoryBroker | RabbitMQ / Redis |

## 测试验证

```bash
cd microservice-architecture/outbox-pattern/python
python3 -m unittest discover -s test -p "test_*.py"
```
