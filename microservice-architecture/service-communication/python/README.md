# service-communication (python)

当前目录对比两种通信方式：

- 同步调用：订单服务直接调用库存与支付
- 异步事件：订单服务发布事件，由事件总线驱动后续处理

## 目录结构

```text
python/
├── README.md
├── src/
│   ├── __init__.py
│   └── communication.py
└── test/
    └── test_communication.py
```

## 运行方式

```bash
cd microservice-architecture/service-communication/python
python3 -m unittest discover -s test -p "test_*.py"
```
