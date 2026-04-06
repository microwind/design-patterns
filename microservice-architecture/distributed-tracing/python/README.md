# distributed-tracing (python)

当前目录演示一个最小 trace 传播流程：

- gateway 生成 traceId
- order-service 创建子 span
- inventory-service 继续继承 traceId

## 运行方式

```bash
cd microservice-architecture/distributed-tracing/python
python3 -m unittest discover -s test -p "test_*.py"
```
