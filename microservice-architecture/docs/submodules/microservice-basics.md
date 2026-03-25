# microservice-basics

## 场景问题

单体系统里，订单、库存、支付常写在同一进程里：
- 发布耦合：改库存逻辑会牵连订单模块发布。
- 资源耦合：库存高并发拖慢订单入口。
- 数据耦合：共享表结构导致边界混乱。

## 核心思想

微服务基础模式关注三件事：
1. 以业务能力拆分服务边界（订单服务、库存服务）。
2. 每个服务拥有自己的数据和规则。
3. 服务之间通过明确契约通信（HTTP/RPC/MQ）。

## 结构与职责

```text
[Order Service]
  - 接收下单请求
  - 调用库存契约 reserve(sku, quantity)
  - 维护订单状态
       |
       | HTTP GET /reserve?sku=...&quantity=...
       v
[Inventory Service]
  - 维护库存数据
  - 负责扣减与不足判断
  - 返回 OK / NO_STOCK
```

## 最小可运行示例（阶段1：进程内契约）

- 统一业务：`createOrder(orderId, sku, quantity)`
- 成功路径：库存足够 -> 预占成功 -> 订单状态 `CREATED`
- 失败路径：库存不足 -> 订单状态 `REJECTED`

## 进阶演进示例（阶段2：HTTP 服务通信）

本阶段把库存调用升级成 HTTP 契约：

1. `OrderService` 依赖 `InventoryClient` 抽象不变。
2. 新增 `HttpInventoryClient` 实现，通过 HTTP 请求库存服务。
3. 在 `test_http` 中启动一个轻量库存 HTTP 服务器，验证端到端行为。

### HTTP 契约（演示版）

- 请求：`GET /reserve?sku=<SKU>&quantity=<N>`
- 响应：
  - `200 OK` + `OK`：库存预占成功
  - `409 Conflict` + `NO_STOCK`：库存不足

## 代码清单

- `examples/microservice-basics/{java,go,python,js,ts,c}/src`
  - 基础版：`InventoryService` + `OrderService`
  - 进阶版：`HttpInventoryClient`
- `examples/microservice-basics/{lang}/test`
  - `test.*`：进程内契约测试
  - `test_http.*`：HTTP 通信测试

## 再下一步（阶段3）

1. 引入服务发现（调用地址从固定 URL 升级为注册发现）。
2. 引入重试、超时、熔断与降级。
3. 增加幂等键，处理重复请求。

## 常见误区

1. 只拆代码目录，不拆数据所有权。
2. 服务间直接共享数据库。
3. 契约频繁破坏兼容性。
4. 把“远程调用失败”当成“本地函数失败”。

