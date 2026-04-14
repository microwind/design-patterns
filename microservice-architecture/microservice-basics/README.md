# 【微服务基础详解】C/Java/JS/Go/Python/TS不同语言实现

# 简介

微服务基础（Microservice Basics）是微服务架构学习的起点。本模块演示了从单体到微服务拆分的核心思路：将一个包含订单和库存的单体应用拆分为两个独立服务，通过**契约接口**解耦，再通过 **HTTP 远程调用**实现跨服务通信。

理解"远程调用不能当成本地函数调用"是后续学习服务发现、熔断、重试等所有治理模式的前提。

# 作用

1. **服务拆分**：将订单逻辑和库存逻辑拆分为独立服务，各自可独立部署和扩展。
2. **契约解耦**：通过接口（InventoryClient）定义服务间契约，调用方不依赖具体实现。
3. **远程调用演进**：从进程内调用（阶段1）演进到 HTTP 调用（阶段2），体现微服务通信本质。

# 实现步骤

1. 定义库存服务的契约接口（InventoryClient），包含 `reserve(sku, quantity)` 方法。
2. 实现本地库存服务（InventoryService），维护内存库存并实现契约接口。
3. 实现订单服务（OrderService），依赖契约接口而非具体实现，调用库存服务完成订单创建。
4. 实现 HTTP 库存客户端（HttpInventoryClient），通过 HTTP 调用远程库存服务。
5. 通过替换注入的客户端实现，从本地调用无缝切换为远程调用。

# 架构图

```text
阶段1（进程内契约调用）：
  OrderService ──► InventoryClient(接口)
                         │
                   InventoryService(本地实现)

阶段2（HTTP 远程调用）：
  OrderService ──► InventoryClient(接口)
                         │
                   HttpInventoryClient ──HTTP──► Inventory HTTP Service
```

# 涉及的设计模式

| 设计模式 | 在本模块中的体现 |
|---|---|
| **依赖倒置原则（DIP）** | OrderService 依赖 InventoryClient 接口而非具体实现，实现了高层模块与低层模块的解耦。 |
| **适配器模式（Adapter Pattern）** | HttpInventoryClient 将 HTTP 远程调用适配为 InventoryClient 接口，调用方无需感知通信细节。 |
| **外观模式（Facade Pattern）** | OrderService 对外提供 createOrder 统一入口，隐藏了库存检查和订单创建的内部流程。 |
| **策略模式（Strategy Pattern）** | 通过构造函数注入不同的 InventoryClient 实现（本地/HTTP），可以在不修改 OrderService 的情况下切换调用策略。 |

# 与实际开源项目对比

| 对比维度 | 本示例 | 实际工程 |
|---|---|---|
| **服务间通信** | 原生 HTTP GET 请求 | Spring Cloud OpenFeign（声明式 HTTP）、gRPC（高性能 RPC）、Dubbo |
| **服务发现** | 硬编码 baseUrl | Consul / Eureka / Nacos 动态发现 |
| **序列化** | 纯文本 "OK" | JSON / Protobuf / Avro |
| **容错处理** | 简单 try-catch 返回 false | Resilience4j 熔断 + 重试 + 超时组合 |
| **负载均衡** | 无 | Ribbon / Envoy 客户端负载均衡 |
| **契约管理** | Java 接口 / Go interface | OpenAPI / Protobuf IDL 契约定义 |

> **整体思路一致**：服务拆分 + 契约接口 + 远程调用是所有微服务框架的核心骨架。本示例省略了服务发现、序列化、容错等工程细节，聚焦于拆分与契约的本质。

# 代码

## 统一业务场景

```
createOrder(orderId, sku, quantity)
  → 库存足够 → Order(status="CREATED")
  → 库存不足 → Order(status="REJECTED")
```

## 代码

### Java 核心实现

```java
// 依赖倒置 —— OrderService 依赖 InventoryClient 接口而非具体实现
public class OrderService {
    private final InventoryClient inventory;
    public Order createOrder(String orderId, String sku, int qty) { ... }
}

public interface InventoryClient {
    boolean checkStock(String sku, int quantity);
}

// HttpInventoryClient 是接口的 HTTP 实现
public class HttpInventoryClient implements InventoryClient { ... }
```

### Go 核心实现

```go
type InventoryClient interface {
    CheckStock(sku string, quantity int) bool
}
type OrderService struct { inventory InventoryClient }
func (s *OrderService) CreateOrder(orderID, sku string, qty int) Order { ... }
```

### Python 核心实现

```python
class OrderService:
    def create_order(self, order_id: str, sku: str, quantity: int) -> Order: ...

class InventoryService:
    def check_stock(self, sku: str, quantity: int) -> bool: ...
```

### JavaScript 核心实现

```javascript
export class OrderService {
  createOrder(orderId, sku, quantity) { ... }
}
export class InventoryService {
  checkStock(sku, quantity) { ... }
}
```

### TypeScript 核心实现

```typescript
export class OrderService {
  createOrder(orderId: string, sku: string, quantity: number): Order { ... }
}
export interface InventoryClient {
  checkStock(sku: string, quantity: number): boolean;
}
```

### C 核心实现

```c
Order create_order(InventoryService *inv, const char *order_id,
    const char *sku, int quantity);
int check_stock(InventoryService *inv, const char *sku, int quantity);
```

## 测试验证

```bash
# Java
cd microservice-architecture/microservice-basics/java
javac src/*.java test/Test.java && java test.Test

# Go
cd microservice-architecture/microservice-basics/go
go run test/test.go

# Python
cd microservice-architecture/microservice-basics/python
python3 test/test.py

# JavaScript
cd microservice-architecture/microservice-basics/js
node test/test.js

# TypeScript
cd microservice-architecture/microservice-basics/ts
tsc -p . && node test/test.js

# C
cd microservice-architecture/microservice-basics/c
cc test/test.c src/inventory_service.c src/order_service.c -o test.out && ./test.out
```
