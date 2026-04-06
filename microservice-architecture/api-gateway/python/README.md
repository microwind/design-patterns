# api-gateway (python)

当前目录提供一个最小 API Gateway 示例，演示：

- 路由分发
- 请求头鉴权
- 相关性 ID 透传
- 未知路由统一返回

## 目录结构

```text
python/
├── README.md
├── src/
│   ├── __init__.py
│   └── gateway.py
└── test/
    └── test_gateway.py
```

## 运行方式

```bash
cd microservice-architecture/api-gateway/python
python3 -m unittest discover -s test -p "test_*.py"
```
