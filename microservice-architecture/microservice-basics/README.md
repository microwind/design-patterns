# microservice-basics

该示例分两阶段演进：

1. 基础版（进程内契约调用）
2. 进阶版（HTTP 服务通信）

统一业务：`createOrder(orderId, sku, quantity)`
- 库存足够 -> `CREATED`
- 库存不足 -> `REJECTED`

## 阶段说明

- 阶段1：`OrderService -> InventoryClient`（本地实现）
- 阶段2：`OrderService -> HttpInventoryClient -> Inventory HTTP Service`

## 语言实现

- java
- go
- python
- js
- ts
- c
