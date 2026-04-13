# 【负载均衡模式详解】C/Java/JS/Go/Python/TS不同语言实现

# 简介

负载均衡（Load Balancing）是微服务架构中将请求分发到多个后端实例的核心模式。本示例实现三种经典算法：**轮询（Round Robin）**、**加权轮询（Weighted Round Robin）** 和 **最少连接（Least Connections）**，展示不同策略下的流量分配逻辑。

# 作用

1. **轮询**：按固定顺序依次分配请求，实现最简单、适合同构后端。
2. **加权轮询**：按权重比例分配请求，高性能实例承担更多流量。
3. **最少连接**：选择当前活跃连接数最少的后端，动态适应实际负载。

# 实现步骤

1. 定义 Backend 模型：包含 backendId、weight（权重）、activeConnections（活跃连接数）。
2. **RoundRobinBalancer**：维护 nextIndex 游标，每次取 `nextIndex % backends.size()` 后递增。
3. **WeightedRoundRobinBalancer**：预展开权重序列（weight=3 的后端重复 3 次），再执行轮询。
4. **LeastConnectionsBalancer**：每次遍历找 activeConnections 最小的后端，调用 acquire/release 维护连接计数。

# 流程图

```text
【轮询】                【加权轮询】              【最少连接】
next()                  next()                    acquire()
  │                       │                         │
  ▼                       ▼                         ▼
index % size            index % 展开序列长度       遍历所有后端
  │                       │                         │
  ▼                       ▼                         ▼
返回 backend            返回 backend               选最小 activeConnections
  │                       │                         │
  ▼                       ▼                         ▼
index++                 index++                    connections++
```

# 涉及的设计模式

| 设计模式 | 在本模块中的体现 |
|---|---|
| **策略模式（Strategy Pattern）** | 三种负载均衡算法是可互换的策略，调用方按需选择不同的 Balancer 实现。 |
| **迭代器模式（Iterator Pattern）** | RoundRobin 和 WeightedRoundRobin 通过 nextIndex 游标循环遍历后端列表。 |

# 与实际开源项目对比

| 对比维度 | 本示例 | 实际工程 |
|---|---|---|
| **部署方式** | 进程内模拟 | Nginx / HAProxy / Envoy / Kubernetes Service |
| **健康检查** | 无 | 主动探活（HTTP/TCP）、被动故障检测 |
| **算法** | 轮询、加权轮询、最少连接 | 还有 IP Hash、一致性哈希、随机、P2C |
| **会话亲和** | 无 | Cookie/Header 粘滞会话 |
| **动态权重** | 静态配置 | 自适应权重（基于响应时间、错误率） |
| **服务发现** | 硬编码后端列表 | Consul / Eureka / DNS 动态更新 |

> **整体思路一致**：后端列表 + 选择算法 + 连接管理是所有负载均衡实现的核心骨架。

# 测试验证

```bash
# Java
cd microservice-architecture/load-balancing/java
javac src/Balancers.java test/Test.java && java test.Test

# Go
cd microservice-architecture/load-balancing/go
go test ./...

# Python
cd microservice-architecture/load-balancing/python
python3 -m unittest discover -s test -p "test_*.py"

# JavaScript
cd microservice-architecture/load-balancing/js
node test/test_balancer.js

# TypeScript
cd microservice-architecture/load-balancing/ts
tsc -p . && node dist/test/test_balancer.js

# C
cd microservice-architecture/load-balancing/c
cc test/test.c src/*.c -o test.out && ./test.out
```
