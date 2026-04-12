# distributed-tracing (C)

## 模块说明

分布式链路追踪的 C 语言实现。用结构体传递追踪上下文，strcpy 操作字符串字段。

## 设计模式应用

- **责任链模式**：上下文通过 TraceContext 指针沿调用链传递，类似 HTTP Header 传播。

## 代码结构

```
src/
  func.h      — TraceContext 结构体和函数声明
  tracing.c   — gateway_entry / child_span 实现
test/
  test.c      — 验证 traceId 传播和 parentSpanId 关联
```

## 与实际工程对比

| 维度 | 本示例 | Envoy (C++) |
|---|---|---|
| 传播 | 结构体指针 | HTTP Header 自动注入 |
| ID 生成 | 硬编码 | 128-bit 随机 |
| 上报 | 无 | OTLP / Zipkin exporter |

## 测试验证

```bash
cd microservice-architecture/distributed-tracing/c
cc test/test.c src/*.c -o test.out
./test.out
```
