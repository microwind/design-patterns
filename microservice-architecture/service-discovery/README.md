# 【服务发现模式详解】C/Java/JS/Go/Python/TS不同语言实现

# 简介

服务发现（Service Discovery）是微服务架构中解决"调用方如何找到被调服务"的核心模式。当系统从单体拆分为多个服务后，服务实例的地址不再固定——可能动态扩缩容、重启、迁移。服务发现通过**注册中心**维护服务实例清单，调用方从注册中心获取可用实例地址，而非硬编码。

# 作用

1. **动态寻址**：服务实例上下线后，调用方自动感知，无需修改配置或重新部署。
2. **负载分发**：配合轮询等策略，将请求分散到多个实例，提高系统吞吐。
3. **健康管理**：支持实例注册和摘除，为后续健康检查和故障转移奠定基础。

# 实现步骤

1. 定义服务实例数据结构（ServiceInstance），包含实例ID和地址。
2. 实现注册中心（ServiceRegistry），提供 register / deregister / instances 方法。
3. 实现服务发现客户端（RoundRobinDiscoverer），从注册中心获取实例列表并轮询选择。
4. 测试验证：注册多个实例 → 轮询分发 → 摘除实例 → 验证摘除后不再被选中。

# 架构图

```text
  服务实例A ──register──►┐
  服务实例B ──register──►├── ServiceRegistry（注册中心）
  服务实例C ──register──►┘         │
                                   │ instances()
                                   ▼
                          RoundRobinDiscoverer
                                   │
                              next() 轮询
                                   ▼
                         调用方获取实例地址
```

# 涉及的设计模式

| 设计模式 | 在本模块中的体现 |
|---|---|
| **注册表模式（Registry Pattern）** | ServiceRegistry 作为全局注册表，维护服务名到实例列表的映射。所有实例通过 register/deregister 管理生命周期。 |
| **观察者模式（Observer Pattern）** | 实际工程中注册中心会通知订阅者实例变更（如 Nacos 的 push 通知）。本示例简化为主动查询（pull 模式）。 |
| **策略模式（Strategy Pattern）** | RoundRobinDiscoverer 封装了轮询选择策略。实际工程中还支持随机、加权、一致性哈希等策略。 |

# 与实际开源项目对比

| 对比维度 | 本示例 | 实际工程 |
|---|---|---|
| **存储** | 内存 Map/Dict | Consul（Raft 共识）、Eureka（AP 模式）、Nacos（AP/CP 可切换）、etcd（Raft） |
| **实例发现** | 主动查询（pull） | 支持 push + pull 混合（Nacos）、长轮询（Eureka）、watch（Consul/etcd） |
| **健康检查** | 无 | TTL 心跳、HTTP 健康端点、TCP 探测 |
| **多数据中心** | 不支持 | Consul 原生支持多数据中心联邦 |
| **负载均衡** | 简单轮询 | 加权轮询、最少连接、一致性哈希 |
| **线程安全** | 非线程安全 | 全部线程安全（锁 / CAS / Actor） |

> **整体思路一致**：注册中心 + 实例注册/摘除 + 客户端发现是所有实现的核心骨架。

# 测试验证

```bash
# Java
cd microservice-architecture/service-discovery/java
javac src/ServiceRegistry.java test/Test.java && java test.Test

# Go
cd microservice-architecture/service-discovery/go
go test ./...

# Python
cd microservice-architecture/service-discovery/python
python3 -m unittest discover -s test -p "test_*.py"

# JavaScript
cd microservice-architecture/service-discovery/js
node test/test_registry.js

# TypeScript
cd microservice-architecture/service-discovery/ts
tsc -p . && node dist/test/test_registry.js

# C
cd microservice-architecture/service-discovery/c
cc test/test.c src/*.c -o test.out && ./test.out
```
