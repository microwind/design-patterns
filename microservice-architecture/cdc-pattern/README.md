# 【CDC模式详解】C/Java/JS/Go/Python/TS不同语言实现

# 简介

CDC（Change Data Capture，变更数据捕获）是微服务架构中实现"数据变更自动传播"的核心模式。当业务数据发生写入/更新时，系统自动捕获变更记录，通过 Connector 扫描未处理的变更并发布事件到消息代理（Broker），下游服务订阅事件实现数据同步，避免双写不一致问题。

# 作用

1. **数据同步**：将上游数据库的变更自动传播到下游服务，无需业务代码手动发送消息。
2. **避免双写**：写入数据库后由 CDC 组件负责发布事件，杜绝"写库成功但发消息失败"的不一致。
3. **解耦生产者与消费者**：上游只管写库，下游通过事件流消费，互不依赖。

# 实现步骤

1. 定义变更记录（ChangeRecord）：包含 changeId、aggregateId、changeType、processed 标记。
2. DataStore 写入业务数据时同步追加一条未处理的变更记录。
3. Connector 定期扫描未处理变更，将 changeId 发布到 Broker，并标记为已处理。

# 流程图

```text
业务写入（createOrder）
    │
    ▼
DataStore 追加 ChangeRecord（processed=false）
    │
    ▼
Connector 扫描未处理变更
    │
    ├── 未处理 ──► Broker.publish(changeId)
    │                   │
    │                   ▼
    │              标记 processed=true
    │
    └── 已处理 ──► 跳过
```

# 涉及的设计模式

| 设计模式 | 在本模块中的体现 |
|---|---|
| **观察者模式（Observer Pattern）** | Broker 接收变更事件并分发给下游订阅者，是发布-订阅的核心。 |
| **代理模式（Proxy Pattern）** | Connector 作为中间代理，解耦 DataStore 与 Broker，业务层无需感知事件发布。 |

# 与实际开源项目对比

| 对比维度 | 本示例 | 实际工程 |
|---|---|---|
| **捕获方式** | 应用层追加变更日志 | Debezium（数据库日志解析）、Canal（MySQL Binlog） |
| **传输** | 内存 Broker | Kafka、Pulsar、RabbitMQ |
| **变更格式** | 简单 changeId | Debezium Envelope（before/after/op/ts_ms） |
| **顺序保证** | 单线程顺序 | Kafka 分区内有序 |
| **容错** | 无 | Offset 持久化 + Exactly-Once 语义 |
| **Schema 管理** | 无 | Confluent Schema Registry（Avro/Protobuf） |

> **整体思路一致**：变更捕获 + 事件发布 + 已处理标记是所有 CDC 实现的核心骨架。

# 测试验证

```bash
# Java
cd microservice-architecture/cdc-pattern/java
javac src/CDCPattern.java test/Test.java && java test.Test

# Go
cd microservice-architecture/cdc-pattern/go
go test ./...

# Python
cd microservice-architecture/cdc-pattern/python
python3 -m unittest discover -s test -p "test_*.py"

# JavaScript
cd microservice-architecture/cdc-pattern/js
node test/test_cdc.js

# TypeScript
cd microservice-architecture/cdc-pattern/ts
tsc -p . && node dist/test/test_cdc.js

# C
cd microservice-architecture/cdc-pattern/c
cc test/test.c src/*.c -o test.out && ./test.out
```
