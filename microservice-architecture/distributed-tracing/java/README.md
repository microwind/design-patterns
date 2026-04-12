# distributed-tracing (Java)

## 模块说明

分布式链路追踪的 Java 实现。演示 traceId 串联调用链、spanId/parentSpanId 构建父子关系。

## 设计模式应用

- **责任链模式（Chain of Responsibility）**：请求沿 gateway → order → inventory 传播，每个节点创建 span。
- **装饰器模式（Decorator Pattern）**：实际工程中追踪逻辑通过 Spring 拦截器/Filter 包裹业务代码。

## 代码结构

```
src/
  TracingPattern.java  — TraceContext 类 + gatewayEntry / childSpan 静态方法
test/
  Test.java            — 验证 traceId 传播和 parentSpanId 关联
```

## 与实际工程对比

| 维度 | 本示例 | OpenTelemetry / Sleuth |
|---|---|---|
| 传播 | 函数参数传递 | HTTP Header（W3C Trace Context） |
| ID 生成 | 硬编码字符串 | 128-bit 随机 ID |
| 埋点 | 手动创建 | 自动埋点（auto-instrumentation） |
| 上报 | 无 | Jaeger / Zipkin / Tempo |

## 测试验证

```bash
cd microservice-architecture/distributed-tracing/java
javac src/TracingPattern.java test/Test.java
java test.Test
```
