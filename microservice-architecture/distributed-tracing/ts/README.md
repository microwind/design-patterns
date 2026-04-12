# distributed-tracing (TypeScript)

## 模块说明

分布式链路追踪的 TypeScript 实现。通过 TraceContext 类型保证上下文字段的编译期安全。

## 设计模式应用

- **责任链模式**：上下文通过类型安全的 TraceContext 参数传递。

## 代码结构

```
src/
  tracing.ts        — TraceContext 类型 + gatewayEntry / childSpan
test/
  test_tracing.ts   — 验证传播正确性
dist/               — tsc 编译输出
```

## 与实际工程对比

| 维度 | 本示例 | OpenTelemetry TS |
|---|---|---|
| 类型 | TypeScript type | 原生 TS 支持 |
| 传播 | 类型参数 | Context API |

## 测试验证

```bash
cd microservice-architecture/distributed-tracing/ts
tsc -p .
node dist/test/test_tracing.js
```
