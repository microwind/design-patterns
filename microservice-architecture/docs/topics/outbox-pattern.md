# Outbox Pattern

## 场景问题

在微服务里，最常见的一个一致性陷阱是：

1. 订单已经成功写入数据库
2. 业务准备发布 `order_created` 事件
3. 消息发送失败，或者进程在发送前崩溃

结果就是：

- 订单存在了
- 事件没出去
- 下游库存、支付、通知都感知不到这次变更

## 核心思想

Outbox 模式的目标是把“业务写入”和“待发布事件记录”变成一个原子结果：

- 先在同一个事务里写业务数据和 outbox 记录
- 由独立的 relay 进程扫描未发布事件
- 成功发送后，把 outbox 记录标记为 `published`

这样即使发布过程失败，也可以通过重试补发。

## 结构与职责

```text
Order Service
  - create order
  - append outbox event(order_created)

Outbox Store
  - eventId
  - aggregateId
  - payload
  - status(pending/published)

Relay
  - scan pending events
  - publish to broker
  - mark as published
```

## 最小可运行示例

本专题的最小示例统一表达 3 个动作：

1. 创建订单时，同时写入一条 `pending` outbox 事件
2. relay 扫描 `pending` 事件并发布到内存 broker
3. relay 再跑一次时，不应重复发布已标记为 `published` 的事件

统一业务语境：

- 订单：`ORD-1001`
- 事件：`order_created`
- outbox 状态：`pending / published`

## 进阶演进

真实系统里，outbox 往往进一步演进为：

1. 与数据库事务真正绑定
2. relay 批量拉取、批量确认
3. 配合消费者幂等处理重复事件
4. 配合 CDC 从数据库日志提取 outbox
5. 结合监控指标观察 pending 堆积

## 常见误区

1. 只写业务数据，不写 outbox 记录
2. outbox 写了，但没有可靠扫描与重试机制
3. 发布成功后不更新状态，导致无限重复发送
4. 以为 outbox 能单独解决消费端重复处理问题
5. 把 outbox 当成最终事件存储，而不是中转缓冲

## 推荐和哪些模块一起看

- `service-communication`
- `idempotency-pattern`
- `resilience-patterns`

原因：

- 通信方式决定事件怎么出去
- 幂等保证重复发布或重复消费时副作用可控
- 韧性模式保证 relay 失败时能够恢复和重试
