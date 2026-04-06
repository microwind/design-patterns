# 微服务架构模式（Microservice Architecture Patterns）

> 目标：把“微服务”从一个概念词，补成可以学习、对比、运行和演进的专题目录。

- 为什么很多系统要从单体走向微服务
- 微服务拆分之后，服务如何发现、通信、治理和演进
- 远程调用带来的稳定性问题如何处理
- 如何从最小示例逐步升级到工程级系统

---

## 一、专题范围

本专题当前覆盖 17 个核心子模块：

1. `microservice-basics`
2. `api-gateway`
3. `service-discovery`
4. `configuration-center`
5. `idempotency-pattern`
6. `outbox-pattern`
7. `distributed-transactions`
8. `distributed-tracing`
9. `rate-limiting`
10. `retry-pattern`
11. `circuit-breaker`
12. `feature-flag`
13. `cdc-pattern`
14. `load-balancing`
15. `service-communication`
16. `api-versioning`
17. `resilience-patterns`

这些模块并不是彼此独立的知识点，而是一条典型演进链路：

```text
单体拆分
  -> 服务边界
  -> 服务发现
  -> 配置集中管理
  -> 幂等控制
  -> Outbox 可靠发布
  -> 分布式事务补偿
  -> 链路追踪传播
  -> 限流保护
  -> 重试收敛
  -> 断路保护
  -> 特性开关治理
  -> CDC 数据变更传播
  -> 流量分发
  -> 统一入口
  -> 同步/异步通信
  -> 版本演进
  -> 限流/超时/重试/熔断/降级
```

---

## 二、目录结构

```text
microservice-architecture/
├── README.md
├── docs/
│   ├── architecture-overview.md
│   ├── learning-path.md
│   └── topics/
│       ├── microservice-basics.md
│       ├── api-gateway.md
│       ├── service-discovery.md
│       ├── configuration-center.md
│       ├── idempotency-pattern.md
│       ├── outbox-pattern.md
│       ├── distributed-transactions.md
│       ├── distributed-tracing.md
│       ├── rate-limiting.md
│       ├── retry-pattern.md
│       ├── circuit-breaker.md
│       ├── feature-flag.md
│       ├── cdc-pattern.md
│       ├── load-balancing.md
│       ├── service-communication.md
│       ├── api-versioning.md
│       └── resilience-patterns.md
└── examples/
    ├── STRUCTURE.md
    ├── microservice-basics/
    ├── api-gateway/
    ├── service-discovery/
    ├── configuration-center/
    ├── idempotency-pattern/
    ├── outbox-pattern/
    ├── distributed-transactions/
    ├── distributed-tracing/
    ├── rate-limiting/
    ├── retry-pattern/
    ├── circuit-breaker/
    ├── feature-flag/
    ├── cdc-pattern/
    ├── load-balancing/
    ├── service-communication/
    ├── api-versioning/
    └── resilience-patterns/
```

---

## 三、当前交付状态

### 3.1 文档状态

| 模块 | 概念文档 | 结构说明 | 常见误区 | 演进建议 |
|---|---|---|---|---|
| `microservice-basics` | ✅ | ✅ | ✅ | ✅ |
| `api-gateway` | ✅ | ✅ | ✅ | ✅ |
| `service-discovery` | ✅ | ✅ | ✅ | ✅ |
| `configuration-center` | ✅ | ✅ | ✅ | ✅ |
| `idempotency-pattern` | ✅ | ✅ | ✅ | ✅ |
| `outbox-pattern` | ✅ | ✅ | ✅ | ✅ |
| `distributed-transactions` | ✅ | ✅ | ✅ | ✅ |
| `distributed-tracing` | ✅ | ✅ | ✅ | ✅ |
| `rate-limiting` | ✅ | ✅ | ✅ | ✅ |
| `retry-pattern` | ✅ | ✅ | ✅ | ✅ |
| `circuit-breaker` | ✅ | ✅ | ✅ | ✅ |
| `feature-flag` | ✅ | ✅ | ✅ | ✅ |
| `cdc-pattern` | ✅ | ✅ | ✅ | ✅ |
| `load-balancing` | ✅ | ✅ | ✅ | ✅ |
| `service-communication` | ✅ | ✅ | ✅ | ✅ |
| `api-versioning` | ✅ | ✅ | ✅ | ✅ |
| `resilience-patterns` | ✅ | ✅ | ✅ | ✅ |

### 3.2 示例状态

当前目录采用如下策略：

- `C`：完整可运行参考实现
- `Go`：完整可运行参考实现
- `Python`：完整可运行参考实现
- `TypeScript`：完整可编译、可运行参考实现
- `Java`：完整可编译、可运行参考实现
- `JavaScript`：完整可运行参考实现

这样做的原因是，微服务专题的重点是“系统设计与演进”，优先保证一套完整、可运行、可测试的参考实现，比为每种语言堆一批不一致的半成品更有价值。

---

## 四、每个模块学什么

### 1. `microservice-basics`

学习重点：

- 为什么拆服务
- 订单服务与库存服务如何通过契约解耦
- 为什么“远程调用”不能再当成本地函数调用

入口文档：

- `docs/topics/microservice-basics.md`
- `examples/microservice-basics/README.md`

### 2. `api-gateway`

学习重点：

- 为什么微服务系统通常需要统一入口
- 网关如何做路由、鉴权、头透传和响应统一
- 什么逻辑适合放网关，什么逻辑不适合

入口文档：

- `docs/topics/api-gateway.md`

### 3. `service-discovery`

学习重点：

- 为什么客户端不能写死服务地址
- 注册中心如何维护实例清单
- 服务实例上下线后调用方如何感知

入口文档：

- `docs/topics/service-discovery.md`

### 4. `configuration-center`

学习重点：

- 为什么配置不应该散落在每个服务本地文件里
- 如何把环境差异、开关和阈值集中管理
- 配置变更后客户端如何刷新而不重新发版

入口文档：

- `docs/topics/configuration-center.md`

### 6. `outbox-pattern`

学习重点：

- 为什么“写库成功但发消息失败”会导致数据与事件不一致
- 如何把业务事件先写入 outbox，再由 relay 安全发布
- 为什么 outbox 常和幂等、消息去重、消费端幂等一起出现

入口文档：

- `docs/topics/outbox-pattern.md`

### 7. `distributed-transactions`

学习重点：

- 为什么跨服务场景下不能简单依赖单库事务
- 什么是 Saga、补偿动作和状态推进
- 为什么分布式事务的重点是业务一致性设计，而不是追求“全局锁住”

入口文档：

- `docs/topics/distributed-transactions.md`

### 8. `distributed-tracing`

学习重点：

- 为什么日志里只有 `order-service failed` 还不够定位问题
- traceId / spanId / parentSpanId 如何串起调用链
- 为什么链路追踪的第一步是上下文传播，而不是先上可视化平台

入口文档：

- `docs/topics/distributed-tracing.md`

### 9. `rate-limiting`

学习重点：

- 为什么高峰流量下要优先保护系统而不是无条件接收所有请求
- 固定窗口、令牌桶等限流策略的差异
- 为什么限流不仅是网关能力，也是服务保护能力

入口文档：

- `docs/topics/rate-limiting.md`

### 10. `retry-pattern`

学习重点：

- 为什么失败重试不能“无脑多试几次”
- 如何区分可重试和不可重试错误
- 为什么重试必须和超时、幂等一起设计

入口文档：

- `docs/topics/retry-pattern.md`

### 11. `circuit-breaker`

学习重点：

- 为什么连续失败时必须快速失败而不是继续放大故障
- 断路器的 closed / open / half-open 状态转换
- 为什么断路器和重试策略会相互影响

入口文档：

- `docs/topics/circuit-breaker.md`

### 12. `feature-flag`

学习重点：

- 为什么功能发布不应完全绑定到代码发布
- 开关、灰度、按用户定向启用如何落地
- 为什么开关治理最终会变成平台能力

入口文档：

- `docs/topics/feature-flag.md`

### 13. `cdc-pattern`

学习重点：

- 为什么数据变更传播不一定要侵入业务代码
- CDC 如何从变更日志转成事件流
- CDC 和 outbox 的边界与适用场景差异

入口文档：

- `docs/topics/cdc-pattern.md`

### 14. `load-balancing`

学习重点：

- 为什么重试、重复提交、消息重复投递会导致业务副作用重复执行
- 如何通过幂等键把“重复请求”折叠为同一结果
- 幂等命中与幂等冲突有什么区别

入口文档：

- `docs/topics/idempotency-pattern.md`

### 6. `load-balancing`

学习重点：

- 轮询、加权轮询、最少连接的差异
- 为什么流量分发策略影响吞吐与稳定性
- 负载均衡与服务发现如何配合

入口文档：

- `docs/topics/load-balancing.md`

### 15. `service-communication`

学习重点：

- 同步调用和异步事件的本质差别
- 什么场景更适合 REST / RPC / MQ
- 如何在一致性与解耦之间做取舍

入口文档：

- `docs/topics/service-communication.md`

### 16. `api-versioning`

学习重点：

- 为什么接口升级不应直接覆盖旧版本
- 如何实现 V1 / V2 并存
- 如何做向后兼容和渐进迁移

入口文档：

- `docs/topics/api-versioning.md`

### 17. `resilience-patterns`

学习重点：

- 为什么分布式系统必须设计重试、超时、熔断和降级
- 为什么“重试”不是越多越好
- 服务抖动时如何避免级联故障

入口文档：

- `docs/topics/resilience-patterns.md`

---

## 五、建议学习顺序

建议按下面的顺序阅读和运行示例：

1. `microservice-basics`
2. `service-discovery`
3. `configuration-center`
4. `idempotency-pattern`
5. `outbox-pattern`
6. `distributed-transactions`
7. `distributed-tracing`
8. `rate-limiting`
9. `retry-pattern`
10. `circuit-breaker`
11. `feature-flag`
12. `cdc-pattern`
13. `load-balancing`
14. `api-gateway`
15. `service-communication`
16. `api-versioning`
17. `resilience-patterns`

如果目标是“从 CRUD 工程师升级到系统设计者”，建议不要跳过前 3 个模块，因为后面的所有治理能力都建立在服务边界与调用链的理解之上。

---

## 六、运行方式

### 6.1 C / Go / Python / TypeScript / Java / JavaScript 参考实现

每个 C / Go / Python / TypeScript / Java / JavaScript 示例目录都是一个独立最小模块，可单独验证。

C 版运行方式：

```bash
cd microservice-architecture/service-discovery/c
cc test/test.c src/*.c -o test.out
./test.out
```

典型运行方式：

```bash
cd microservice-architecture/service-discovery/go
go test ./...
```

其余模块同理：

```bash
cd microservice-architecture/api-gateway/go
go test ./...
```

Python 版运行方式：

```bash
cd microservice-architecture/service-discovery/python
python3 -m unittest discover -s test -p "test_*.py"
```

TypeScript 版运行方式：

```bash
cd microservice-architecture/service-discovery/ts
tsc -p .
node dist/test/test_registry.js
```

Java 版运行方式：

```bash
cd microservice-architecture/service-discovery/java
javac src/ServiceRegistry.java test/Test.java
java test.Test
```

JavaScript 版运行方式：

```bash
cd microservice-architecture/service-discovery/js
node test/test_registry.js
```

---

## 七、专题设计原则

本目录遵循以下原则：

- 示例必须最小，但不能抽象到脱离业务
- 文档必须解释取舍，而不是只解释 API
- 每个模块都要能回答“它解决什么问题”
- 每个模块都要能落到一份可运行代码
- 示例之间尽量复用统一业务语境：订单、库存、支付、网关、注册中心

---

## 八、后续扩展方向

当前专题已经具备“基础拆分 + 服务治理入门”的主线，后续适合继续向 V2 中的分布式系统层扩展：

- 配置中心
- 分布式事务
- Outbox / Inbox
- 幂等设计
- 链路追踪
- 灰度发布
- 可观测性

如果后面继续扩展，建议优先保证：

1. 文档结构先统一
2. C / Go / Python / TypeScript / Java / JavaScript 参考实现先完整
3. 再做多语言移植
