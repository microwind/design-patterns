# microservice-basics (go)

该示例对应微服务专题的第一步：先把订单和库存拆成两个清晰边界，再从“进程内契约”演进到“HTTP 契约”。

## 代码结构

```text
go/
├── go.mod
├── src/
│   ├── inventory_client.go
│   ├── inventory_service.go
│   ├── http_inventory_client.go
│   ├── order.go
│   └── order_service.go
└── test/
    ├── test.go
    └── test_http.go
```

## 两个阶段

### 阶段 1：进程内契约

- `OrderService` 依赖 `InventoryClient` 抽象
- `InventoryService` 作为本地实现
- 用于先理解“订单 -> 库存预占”的基本协作关系

### 阶段 2：HTTP 契约

- 新增 `HttpInventoryClient`
- 在测试中通过 `httptest` 启动一个轻量库存服务
- 保持 `OrderService` 不变，只替换客户端实现

## 运行方式

```bash
cd microservice-architecture/microservice-basics/go
go run test/test.go
go run test/test_http.go
```

## 这个示例想说明什么

它想表达的不是“怎么写 HTTP 请求”，而是：

- 业务服务应该依赖契约，而不是依赖具体调用方式
- 本地实现和远程实现应该能通过同一抽象切换
- 一旦跨进程，就必须开始考虑失败、超时和治理
