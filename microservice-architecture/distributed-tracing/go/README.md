# distributed-tracing (Go)

## 模块说明

分布式链路追踪的 Go 实现。演示上下文传播和 span 父子关系。

## 设计模式应用

- **责任链模式**：上下文沿调用链通过 TraceContext 结构体传递。

## 代码结构

```
src/
  tracing.go        — TraceContext + GatewayEntry / ChildSpan
test/
  tracing_test.go   — Go 标准测试
```

## 与实际工程对比

| 维度 | 本示例 | OpenTelemetry Go |
|---|---|---|
| 传播 | 结构体参数 | context.Context + Propagator |
| 埋点 | 手动 | auto-instrumentation |
| 上报 | 无 | OTLP exporter |

## 测试验证

```bash
cd microservice-architecture/distributed-tracing/go
go test ./...
```
