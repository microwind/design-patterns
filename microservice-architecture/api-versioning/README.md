# 【API版本控制模式详解】C/Java/JS/Go/Python/TS不同语言实现

# 简介

API 版本控制（API Versioning）是微服务架构中管理 API 演进的核心模式。当 API 需要引入不兼容变更时，通过版本号区分新旧接口，使新旧客户端可以共存。本示例实现两种版本解析策略：**URL 路径版本**（`/v1/products`）和 **Header 版本**（`X-API-Version: v2`），并支持默认版本兜底。

# 作用

1. **向后兼容**：新版 API 上线后，旧版客户端仍可正常访问旧版接口。
2. **灵活路由**：支持 URL 路径和 Header 两种方式指定版本，适应不同客户端需求。
3. **渐进迁移**：允许客户端按自身节奏从旧版迁移到新版，降低升级风险。

# 实现步骤

1. 定义 Request/Response 模型：Request 包含 path 和 headers，Response 包含 statusCode、version、body。
2. VersionedRouter 注册多版本处理器（register(version, handler)），维护版本 → 处理器映射。
3. 版本解析优先级：URL 路径（`/v2/`、`/v1/`）→ Header（`X-API-Version`）→ 默认版本。
4. 版本归一化：统一转小写，自动补 "v" 前缀。

# 流程图

```text
请求到达
  │
  ▼
解析 URL 路径 ──包含 /v2/──► version = "v2"
  │
  不包含
  │
  ▼
解析 URL 路径 ──包含 /v1/──► version = "v1"
  │
  不包含
  │
  ▼
读取 Header X-API-Version
  │
  ├── 有值 ──► version = 归一化(headerValue)
  │
  └── 无值 ──► version = defaultVersion
  │
  ▼
查找 handlers[version]
  │
  ├── 找到 ──► 返回 200 + handler 结果
  │
  └── 未找到 ──► 返回 400 "unsupported api version"
```

# 涉及的设计模式

| 设计模式 | 在本模块中的体现 |
|---|---|
| **策略模式（Strategy Pattern）** | 每个版本的处理器是独立的策略实现，Router 根据版本号选择对应策略执行。 |
| **责任链模式（Chain of Responsibility）** | 版本解析按优先级依次尝试 URL → Header → 默认值，形成解析链。 |

# 与实际开源项目对比

| 对比维度 | 本示例 | 实际工程 |
|---|---|---|
| **版本方式** | URL 路径 + Header | 还有 Query 参数、Content-Type（Accept Header） |
| **路由实现** | 内存 Map | API Gateway（Kong / Envoy）、框架路由（Spring MVC） |
| **版本协商** | 硬匹配 | 语义化版本范围匹配、Content Negotiation |
| **废弃通知** | 无 | Sunset Header / Deprecation Header（RFC 8594） |
| **文档** | 无 | OpenAPI spec 按版本生成（Swagger） |
| **版本生命周期** | 永久存在 | 版本日落策略（Sunset Policy）、迁移指南 |

> **整体思路一致**：版本解析 + 路由分发 + 默认兜底是所有 API 版本控制的核心骨架。

# 代码

## Java 核心实现

```java
// 策略模式 —— 不同版本的处理器可互换
public class VersionedRouter {
    private final String defaultVersion;
    private final Map<String, Supplier<String>> handlers = new HashMap<>();

    public void register(String version, Supplier<String> handler) { ... }

    // 模板方法：解析版本 → 查找处理器 → 执行
    public Response handle(Request request) { ... }

    // 版本解析优先级：URL 路径 → Header → 默认值
    public String resolveVersion(Request request) { ... }
}
```

## Go 核心实现

```go
// VersionedRouter 版本路由器 —— 策略模式 + 模板方法
type VersionedRouter struct {
    defaultVersion string
    handlers       map[string]func() string
}
func (r *VersionedRouter) Register(version string, handler func() string) { ... }
func (r *VersionedRouter) Handle(req Request) Response { ... }
func (r *VersionedRouter) ResolveVersion(req Request) string { ... }
```

## Python 核心实现

```python
class VersionedRouter:
    """版本路由器 —— 策略模式 + 模板方法"""
    def register(self, version: str, handler: Callable) -> None: ...
    def handle(self, request: Request) -> Response: ...
    def resolve_version(self, request: Request) -> str: ...
```

## JavaScript 核心实现

```javascript
export class VersionedRouter {
  register(version, handler) { ... }
  handle(request) { ... }       // 解析版本 → 查找处理器 → 执行
  resolveVersion(request) { ... } // URL → Header → 默认值
}
```

## TypeScript 核心实现

```typescript
export class VersionedRouter {
  register(version: string, handler: () => string): void { ... }
  handle(request: Request): Response { ... }
  resolveVersion(request: Request): string { ... }
}
```

## C 核心实现

```c
typedef struct {
    char path[128];
    char api_version[16]; // Header: X-API-Version
} VersionRequest;

// 版本解析 + 路由分发
void version_router_handle(const VersionRequest *request, VersionResponse *response);
```

# 测试验证

```bash
# Java
cd microservice-architecture/api-versioning/java
javac src/VersionedRouter.java test/Test.java && java test.Test

# Go
cd microservice-architecture/api-versioning/go
go test ./...

# Python
cd microservice-architecture/api-versioning/python
python3 -m unittest discover -s test -p "test_*.py"

# JavaScript
cd microservice-architecture/api-versioning/js
node test/test_router.js

# TypeScript
cd microservice-architecture/api-versioning/ts
tsc -p . && node dist/test/test_router.js

# C
cd microservice-architecture/api-versioning/c
cc test/test.c src/*.c -o test.out && ./test.out
```
