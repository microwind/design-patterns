# 【服务通信模式详解】C/Java/JS/Go/Python/TS不同语言实现

# 简介

服务通信模式（Service Communication）是微服务架构中服务间交互的基础模式。本示例对比展示两种核心通信方式：**同步通信**（请求-响应，调用方阻塞等待结果）和**异步通信**（事件驱动，通过 EventBus 发布事件、订阅方独立处理）。两种方式各有取舍，实际架构中往往混合使用。

# 作用

1. **同步通信**：调用方直接调用下游服务并等待结果，逻辑清晰、易于调试，适合强一致性场景。
2. **异步通信**：调用方发布事件后立即返回，下游订阅处理，解耦服务依赖，提升吞吐量和容错性。
3. **模式对比**：通过同一业务场景（下单 → 库存扣减 → 支付）分别实现同步/异步，直观展示差异。

# 实现步骤

1. 定义共享模型：Order（订单）、InventoryService（库存）、PaymentService（支付）。
2. **同步方式**：SynchronousOrderService 依次调用 inventory.reserve() → payment.charge()，任一失败立即返回错误状态。
3. **异步方式**：AsyncOrderService 创建 PENDING 订单后发布 order_placed 事件；EventBus 将事件分发给订阅者，订阅者执行库存/支付逻辑并更新订单状态。

# 流程图

```text
【同步通信】                       【异步通信】
placeOrder()                      placeOrder()
    │                                 │
    ▼                                 ▼
inventory.reserve()               保存 PENDING 订单
    │                                 │
    ├── 失败 → REJECTED               ▼
    │                             EventBus.publish(order_placed)
    ▼                                 │
payment.charge()                      ▼ （drain 时）
    │                             订阅者执行 reserve + charge
    ├── 失败 → PAYMENT_FAILED         │
    │                                 ├── 失败 → 更新状态
    ▼                                 │
返回 CREATED                          ▼
                                  更新为 CREATED
```

# 涉及的设计模式

| 设计模式 | 在本模块中的体现 |
|---|---|
| **观察者模式（Observer Pattern）** | EventBus 的 subscribe/publish 机制，订阅者注册回调，事件触发时自动分发。 |
| **中介者模式（Mediator Pattern）** | EventBus 作为中介者，解耦事件生产者与消费者，双方只与 EventBus 交互。 |
| **命令模式（Command Pattern）** | Event 对象封装了业务操作所需的全部信息（orderId、sku、quantity），可被队列存储和延迟执行。 |

# 与实际开源项目对比

| 对比维度 | 本示例 | 实际工程 |
|---|---|---|
| **同步通信** | 直接方法调用 | HTTP REST / gRPC / GraphQL |
| **异步通信** | 内存 EventBus + Queue | Kafka / RabbitMQ / AWS SQS / NATS |
| **序列化** | Java 对象传递 | JSON / Protobuf / Avro |
| **错误处理** | 简单状态码 | 死信队列、补偿事务、Saga 模式 |
| **可观测性** | 无 | 分布式追踪（OpenTelemetry）、链路日志 |
| **事务保证** | 无 | Outbox 模式 + CDC 保证最终一致性 |

> **整体思路一致**：同步调用链 vs 事件驱动异步是所有微服务通信的基本二选一，实际系统中按场景混合使用。

# 测试验证

```bash
# Java
cd microservice-architecture/service-communication/java
javac src/CommunicationModels.java test/Test.java && java test.Test

# Go
cd microservice-architecture/service-communication/go
go test ./...

# Python
cd microservice-architecture/service-communication/python
python3 -m unittest discover -s test -p "test_*.py"

# JavaScript
cd microservice-architecture/service-communication/js
node test/test_communication.js

# TypeScript
cd microservice-architecture/service-communication/ts
tsc -p . && node dist/test/test_communication.js

# C
cd microservice-architecture/service-communication/c
cc test/test.c src/*.c -o test.out && ./test.out
```
