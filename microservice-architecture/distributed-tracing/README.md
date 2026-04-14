# 【分布式链路追踪详解】C/Java/JS/Go/Python/TS不同语言实现

# 简介

分布式链路追踪（Distributed Tracing）是微服务架构中解决"一条请求跨多个服务传播时如何看清完整调用链"的核心模式。当请求从网关进入，经过订单服务、库存服务等多个节点时，链路追踪通过 **traceId** 串联整条调用链，通过 **spanId / parentSpanId** 表示父子调用关系。

链路追踪的第一步是**上下文传播**（Context Propagation），即确保 traceId 和 spanId 能沿着调用链正确传递。

# 作用

1. **调用链可视化**：通过 traceId 将分散在多个服务的日志和指标串联成完整调用链。
2. **父子关系建模**：spanId / parentSpanId 构建调用树，清晰展示服务间的调用层级。
3. **故障定位**：快速定位调用链中哪个服务出了问题，而非只看到"某个服务 failed"。

# 实现步骤

1. 定义 TraceContext 数据结构，包含 traceId、spanId、parentSpanId、serviceName。
2. 入口（gateway）生成 traceId 和根 span。
3. 下游服务通过 childSpan 继承 traceId，生成新 spanId，记录 parentSpanId。
4. 测试验证：所有 span 共享同一 traceId，parentSpanId 正确指向上游 span。

# 架构图

```text
  Client
    │
    ▼
  Gateway (traceId=TRACE-1001, spanId=SPAN-GATEWAY, parent="")
    │
    ├──► OrderService (traceId=TRACE-1001, spanId=SPAN-ORDER, parent=SPAN-GATEWAY)
    │
    └──► InventoryService (traceId=TRACE-1001, spanId=SPAN-INVENTORY, parent=SPAN-GATEWAY)
```

# 涉及的设计模式

| 设计模式 | 在本模块中的体现 |
|---|---|
| **责任链模式（Chain of Responsibility）** | 请求沿调用链传播，每个节点创建自己的 span 并将上下文传递给下一个节点。 |
| **装饰器模式（Decorator Pattern）** | 实际工程中追踪逻辑作为装饰器/中间件包裹在业务处理之外，对业务代码透明。 |

# 与实际开源项目对比

| 对比维度 | 本示例 | 实际工程 |
|---|---|---|
| **上下文传播** | 函数参数传递 TraceContext | HTTP Header（W3C Trace Context / B3）、gRPC metadata |
| **ID 生成** | 硬编码字符串 | 128-bit 随机 ID（OpenTelemetry） |
| **采集与上报** | 无 | OpenTelemetry SDK 采集 → Jaeger / Zipkin / Tempo 存储 |
| **可视化** | 无 | Jaeger UI / Grafana Tempo 调用链瀑布图 |
| **采样策略** | 全量 | 概率采样 / 尾部采样 / 自适应采样 |
| **自动埋点** | 手动创建 span | OpenTelemetry auto-instrumentation 自动埋点 |

> **整体思路一致**：traceId 串联 + spanId/parentSpanId 父子关系是所有实现的核心骨架。

# 代码

## Java 核心实现

```java
// 责任链模式 —— 上下文沿调用链传播
public static class TraceContext {
    private final String traceId;     // 全局唯一追踪 ID
    private final String spanId;      // 当前节点 ID
    private final String parentSpanId; // 父节点 ID
    private final String serviceName;
}

// 入口创建 root span
public static TraceContext gatewayEntry(String traceId) { ... }
// 子 span 继承 traceId，设置 parentSpanId
public static TraceContext childSpan(TraceContext parent, String serviceName, String spanId) { ... }
```

## Go 核心实现

```go
type TraceContext struct {
    TraceID      string
    SpanID       string
    ParentSpanID string
    ServiceName  string
}
func GatewayEntry(traceID string) TraceContext { ... }
func ChildSpan(parent TraceContext, serviceName, spanID string) TraceContext { ... }
```

## Python 核心实现

```python
@dataclass
class TraceContext:
    trace_id: str
    span_id: str
    parent_span_id: str
    service_name: str

def gateway_entry(trace_id: str) -> TraceContext: ...
def child_span(parent: TraceContext, service_name: str, span_id: str) -> TraceContext: ...
```

## JavaScript 核心实现

```javascript
export function gatewayEntry(traceId) { ... }
export function childSpan(parent, serviceName, spanId) { ... }
```

## TypeScript 核心实现

```typescript
export function gatewayEntry(traceId: string): TraceContext { ... }
export function childSpan(parent: TraceContext, serviceName: string, spanId: string): TraceContext { ... }
```

## C 核心实现

```c
typedef struct {
    char trace_id[64];
    char span_id[64];
    char parent_span_id[64];
    char service_name[64];
} TraceContext;

TraceContext gateway_entry(const char *trace_id);
TraceContext child_span(const TraceContext *parent, const char *service_name, const char *span_id);
```

# 测试验证

```bash
# Java
cd microservice-architecture/distributed-tracing/java
javac src/TracingPattern.java test/Test.java && java test.Test

# Go
cd microservice-architecture/distributed-tracing/go
go test ./...

# Python
cd microservice-architecture/distributed-tracing/python
python3 -m unittest discover -s test -p "test_*.py"

# JavaScript
cd microservice-architecture/distributed-tracing/js
node test/test_tracing.js

# TypeScript
cd microservice-architecture/distributed-tracing/ts
tsc -p . && node dist/test/test_tracing.js

# C
cd microservice-architecture/distributed-tracing/c
cc test/test.c src/*.c -o test.out && ./test.out
```
