# 【API网关模式详解】C/Java/JS/Go/Python/TS不同语言实现

# 简介

API 网关（API Gateway）是微服务架构中的统一入口，负责将外部请求路由到对应的后端微服务。网关在路由前后可执行横切逻辑：**认证鉴权**（Middleware 拦截未授权请求）、**请求路由**（前缀匹配分发到不同服务）、**关联追踪**（自动注入 Correlation ID）。所有外部流量经由网关进入，内部服务不直接暴露。

# 作用

1. **统一入口**：外部客户端只需访问网关地址，无需知道后端服务的地址和数量。
2. **认证拦截**：Middleware 在请求到达业务服务前检查鉴权信息，未授权直接返回 401。
3. **路由分发**：按 URL 前缀匹配将请求转发到对应的后端服务处理器。
4. **关联追踪**：自动传递或生成 X-Correlation-ID，串联一次请求的全链路日志。

# 实现步骤

1. 定义 Request/Response 模型：Request 包含 method、path、headers；Response 包含 statusCode、body、headers。
2. 注册中间件（use(middleware)）：按顺序执行，返回非 null 表示拦截（如 401）。
3. 注册路由（register(prefix, handler)）：按前缀匹配，最长前缀优先。
4. handle() 流程：中间件链 → 路由匹配 → 执行处理器 → 注入 Correlation ID。

# 流程图

```text
请求到达 Gateway
    │
    ▼
执行 Middleware 链
    │
    ├── 返回 Response ──► 拦截（如 401 Unauthorized）
    │
    └── 返回 null（通过）
         │
         ▼
    前缀匹配路由表（最长匹配）
         │
         ├── 未匹配 ──► 返回 404
         │
         └── 匹配成功
              │
              ▼
         执行 Handler
              │
              ▼
         注入 X-Correlation-ID
              │
              ▼
         返回 Response
```

# 涉及的设计模式

| 设计模式 | 在本模块中的体现 |
|---|---|
| **责任链模式（Chain of Responsibility）** | Middleware 列表按顺序执行，任一中间件可拦截请求或放行给下一个。 |
| **外观模式（Facade Pattern）** | Gateway 是后端多个微服务的统一外观，客户端只与 Gateway 交互。 |
| **策略模式（Strategy Pattern）** | 每个路由对应一个独立的 Handler 策略，Gateway 按匹配结果选择执行。 |

# 与实际开源项目对比

| 对比维度 | 本示例 | 实际工程 |
|---|---|---|
| **实现** | 进程内模拟 | Kong / Envoy / Spring Cloud Gateway / APISIX |
| **路由匹配** | 前缀匹配 | 正则、路径参数、Host Header、方法匹配 |
| **认证** | 简单 Header 检查 | JWT 验证、OAuth2、API Key、mTLS |
| **限流** | 无 | 令牌桶 / 滑动窗口（Rate Limiting） |
| **负载均衡** | 单后端 | 集成服务发现 + 多种均衡策略 |
| **可观测性** | Correlation ID | 分布式追踪、访问日志、Metrics 暴露 |

> **整体思路一致**：中间件链 + 路由分发 + 统一入口是所有 API 网关的核心骨架。

# 代码

## Java 核心实现

```java
// 外观模式 —— 统一入口；责任链模式 —— 中间件链
public class APIGateway {
    private final Map<String, Handler> routes = new HashMap<>();
    private final List<Middleware> middlewares = new ArrayList<>();

    public void use(Middleware middleware) { middlewares.add(middleware); }
    public void register(String prefix, Handler handler) { routes.put(prefix, handler); }

    // 中间件链 → 路由匹配 → 执行处理器 → 注入 Correlation ID
    public Response handle(Request request) { ... }
    private Handler match(String path) { ... } // 最长前缀匹配
}
```

## Go 核心实现

```go
// APIGateway 网关核心 —— 外观模式 + 责任链模式
type APIGateway struct {
    routes      map[string]Handler
    middlewares []Middleware
}
func (g *APIGateway) Use(m Middleware) { ... }
func (g *APIGateway) Register(prefix string, handler Handler) { ... }
func (g *APIGateway) Handle(req Request) Response { ... }
```

## Python 核心实现

```python
class APIGateway:
    """API 网关 —— 外观模式 + 责任链模式"""
    def use(self, middleware: Middleware) -> None: ...
    def register(self, prefix: str, handler: Handler) -> None: ...
    def handle(self, request: Request) -> Response: ...
```

## JavaScript 核心实现

```javascript
export class APIGateway {
  use(middleware) { ... }       // 注册中间件
  register(prefix, handler) { ... } // 注册路由
  handle(request) { ... }      // 中间件链 → 路由 → 处理
}
```

## TypeScript 核心实现

```typescript
export class APIGateway {
  use(middleware: Middleware): void { ... }
  register(prefix: string, handler: Handler): void { ... }
  handle(request: Request): Response { ... }
}
```

## C 核心实现

```c
typedef GatewayResponse (*GatewayHandler)(const GatewayRequest *request);

void gateway_init(APIGateway *gateway);
void gateway_register(APIGateway *gateway, const char *prefix, GatewayHandler handler);
GatewayResponse gateway_handle(const APIGateway *gateway, const GatewayRequest *request);
```

# 测试验证

```bash
# Java
cd microservice-architecture/api-gateway/java
javac src/APIGateway.java test/Test.java && java test.Test

# Go
cd microservice-architecture/api-gateway/go
go test ./...

# Python
cd microservice-architecture/api-gateway/python
python3 -m unittest discover -s test -p "test_*.py"

# JavaScript
cd microservice-architecture/api-gateway/js
node test/test_gateway.js

# TypeScript
cd microservice-architecture/api-gateway/ts
tsc -p . && node dist/test/test_gateway.js

# C
cd microservice-architecture/api-gateway/c
cc test/test.c src/*.c -o test.out && ./test.out
```
