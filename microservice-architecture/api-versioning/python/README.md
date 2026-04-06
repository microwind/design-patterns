# api-versioning (python)

当前目录演示一个最小版本路由器：

- 默认版本回退
- 通过请求头选择版本
- V1 / V2 并存
- 不支持版本的错误响应

## 目录结构

```text
python/
├── README.md
├── src/
│   ├── __init__.py
│   └── router.py
└── test/
    └── test_router.py
```

## 运行方式

```bash
cd microservice-architecture/api-versioning/python
python3 -m unittest discover -s test -p "test_*.py"
```
