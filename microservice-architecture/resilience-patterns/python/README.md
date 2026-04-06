# resilience-patterns (python)

当前目录提供一个最小韧性示例，演示：

- Timeout
- Retry
- Circuit Breaker
- Fallback

## 目录结构

```text
python/
├── README.md
├── src/
│   ├── __init__.py
│   └── resilience.py
└── test/
    └── test_resilience.py
```

## 运行方式

```bash
cd microservice-architecture/resilience-patterns/python
python3 -m unittest discover -s test -p "test_*.py"
```
