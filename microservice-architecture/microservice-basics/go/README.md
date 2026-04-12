# microservice-basics (Go)

## 模块说明

微服务基础的 Go 实现。演示从单体到微服务的拆分过程：订单服务通过 interface 调用库存服务，支持从进程内调用（阶段1）无缝切换到 HTTP 远程调用（阶段2）。

## 设计模式应用

- **依赖倒置原则（DIP）**：OrderService 依赖 InventoryClient interface 而非具体实现。Go 的隐式接口实现使得解耦更加自然。
- **适配器模式（Adapter Pattern）**：HttpInventoryClient 将 HTTP 调用适配为 InventoryClient 接口。
- **策略模式（Strategy Pattern）**：注入 InventoryService（本地）或 HttpInventoryClient（远程），不修改 OrderService。

## 代码结构

```
src/
  inventory_client.go        — 库存服务契约接口（Go interface）
  inventory_service.go       — 本地库存服务实现（阶段1）
  http_inventory_client.go   — HTTP 远程库存客户端（阶段2）
  order.go                   — 订单实体（值对象）
  order_service.go           — 订单服务（核心业务服务）
test/
  test.go                    — 阶段1：进程内契约调用测试
  test_http.go               — 阶段2：HTTP 远程调用测试（httptest）
```

## 与实际工程对比

| 维度 | 本示例 | go-kit / Kratos |
|---|---|---|
| 依赖注入 | 构造函数注入 | wire / fx 依赖注入框架 |
| 远程调用 | net/http 原生客户端 | gRPC-Go / go-resty |
| 服务发现 | 硬编码 baseURL | Consul / etcd 动态发现 |
| 接口定义 | Go interface | Protobuf IDL |

## 测试验证

```bash
cd microservice-architecture/microservice-basics/go
go run test/test.go
go run test/test_http.go
```
