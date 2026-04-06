# distributed-transactions

该示例聚焦微服务中的分布式事务，统一采用 Saga 补偿模型演示：

1. 订单创建
2. 库存预占
3. 支付尝试
4. 成功则完成订单
5. 失败则补偿并取消订单

统一业务语境：

- 订单：`ORD-1001`
- 商品：`SKU-BOOK`
- 状态：`PENDING / COMPLETED / CANCELLED`

## 目标

- 说明跨服务事务为什么不能简单等同于单库事务
- 说明 Saga 的正向步骤与补偿步骤
- 说明补偿成功后系统如何回到业务可接受状态

## 语言实现

- c
- go
- java
- js
- python
- ts
