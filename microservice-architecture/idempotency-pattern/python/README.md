# idempotency-pattern (Python)

## 模块说明

幂等模式的 Python 实现。使用 dataclass 定义响应，字典元组键实现幂等存储。

## 设计模式应用

- **备忘录模式**：字典存储首次执行结果。
- **代理模式**：幂等检查包裹在业务逻辑之外。

## 代码结构

```
src/
  __init__.py        — 包初始化
  idempotency.py     — IdempotencyOrderService + OrderResponse
test/
  test_idempotency.py — unittest 验证三条路径
```

## 与实际工程对比

| 维度 | 本示例 | Redis + Python |
|---|---|---|
| 存储 | 内存字典 | Redis SETNX + TTL |
| 指纹 | f-string 拼接 | hashlib.sha256 |
| 中间件 | 无 | Django/FastAPI 中间件 |

## 测试验证

```bash
cd microservice-architecture/idempotency-pattern/python
python3 -m unittest discover -s test -p "test_*.py"
```
