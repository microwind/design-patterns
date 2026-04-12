# distributed-tracing (JavaScript)

## 模块说明

分布式链路追踪的 JavaScript (ESM) 实现。用普通对象表示追踪上下文。

## 设计模式应用

- **责任链模式**：上下文通过对象参数沿调用链传递。

## 代码结构

```
src/
  tracing.js        — gatewayEntry / childSpan 函数
test/
  test_tracing.js   — 验证 traceId 传播和 parentSpanId 关联
```

## 与实际工程对比

| 维度 | 本示例 | OpenTelemetry JS |
|---|---|---|
| 传播 | 对象参数 | Context API + HTTP Header |
| 埋点 | 手动 | auto-instrumentation |

## 测试验证

```bash
cd microservice-architecture/distributed-tracing/js
node test/test_tracing.js
```
