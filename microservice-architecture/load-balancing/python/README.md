# load-balancing (python)

当前目录演示 3 种常见负载均衡策略：

- 轮询
- 加权轮询
- 最少连接

## 目录结构

```text
python/
├── README.md
├── src/
│   ├── __init__.py
│   └── balancer.py
└── test/
    └── test_balancer.py
```

## 运行方式

```bash
cd microservice-architecture/load-balancing/python
python3 -m unittest discover -s test -p "test_*.py"
```
