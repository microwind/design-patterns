# retry-pattern (Python)

## 模块说明

重试模式的 Python 实现。使用可调用对象作为重试策略。

## 设计模式应用

- **策略模式**：operation 可调用对象可替换。
- **模板方法模式**：retry 定义循环骨架。

## 代码结构

```
src/
  __init__.py  — 包初始化
  retry.py     — retry 函数 + ScriptedOperation
test/
  test_retry.py — unittest 验证
```

## 与实际工程对比

| 维度 | 本示例 | tenacity |
|---|---|---|
| 退避 | 无 | 指数退避 + 抖动 |
| 装饰器 | 无 | @retry 装饰器 |

## 测试验证

```bash
cd microservice-architecture/retry-pattern/python
python3 -m unittest discover -s test -p "test_*.py"
```
