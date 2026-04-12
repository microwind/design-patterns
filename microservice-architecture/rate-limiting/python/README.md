# rate-limiting (Python)

## 模块说明

固定窗口限流器的 Python 实现。

## 设计模式应用

- **策略模式**：固定窗口是一种限流���略。Flask-Limiter / limits 库提供更完善的多策略支持。

## 代码结构

```
src/
  __init__.py   — 包初始化
  limiter.py    — FixedWindowLimiter（allow / advance_window）
test/
  test_limiter.py — unittest 验证
```

## 测试验证

```bash
cd microservice-architecture/rate-limiting/python
python3 -m unittest discover -s test -p "test_*.py"
```
