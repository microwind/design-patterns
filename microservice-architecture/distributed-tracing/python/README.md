# distributed-tracing (Python)

## 模块说明

分布式链路追踪的 Python 实现。使用 dataclass 定义不可变上下文。

## 设计模式应用

- **责任链模式**：上下文通过 TraceContext 参数沿调用链传递。

## 代码结构

```
src/
  __init__.py    — 包初始化
  tracing.py     — TraceContext + gateway_entry / child_span
test/
  test_tracing.py — unittest 验证传播正确性
```

## 与实际工程对比

| 维度 | 本示例 | OpenTelemetry Python |
|---|---|---|
| 传播 | 函数参数 | context + propagator |
| 埋点 | 手动 | auto-instrumentation |

## 测试验证

```bash
cd microservice-architecture/distributed-tracing/python
python3 -m unittest discover -s test -p "test_*.py"
```
