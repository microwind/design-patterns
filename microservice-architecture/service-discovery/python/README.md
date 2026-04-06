# service-discovery (python)

当前目录提供一个最小服务发现示例，演示：

- 服务注册
- 服务摘除
- 实例查询
- 基于发现结果的轮询选择

## 目录结构

```text
python/
├── README.md
├── src/
│   ├── __init__.py
│   └── registry.py
└── test/
    └── test_registry.py
```

## 运行方式

```bash
cd microservice-architecture/service-discovery/python
python3 -m unittest discover -s test -p "test_*.py"
```
