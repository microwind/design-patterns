# service-discovery (Go)

## 模块说明

服务发现模式的 Go 实现。演示服务注册、摘除、实例查询和轮询选择的完整流程。

## 设计模式应用

- **注册表模式（Registry Pattern）**：ServiceRegistry 用嵌套 map 维护服务名到实例的映射。
- **策略模式（Strategy Pattern）**：RoundRobinDiscoverer 封装轮询选择策略。Go 的 interface 隐式实现使得策略替换非常自然。

## 代码结构

```
src/
  registry.go        — ServiceRegistry + ServiceInstance + RoundRobinDiscoverer
test/
  registry_test.go   — Go 标准测试，验证注册/摘除/轮询完整流程
```

## 与实际工程对比

| 维度 | 本示例 | Consul / etcd |
|---|---|---|
| 存储 | 内存 map | Raft 共识 + 持久化 |
| 健康检查 | 无 | TTL / HTTP / TCP 探测 |
| 多数据中心 | 不支持 | Consul 原生支持 |
| Watch | 无 | etcd watch / Consul blocking query |

## 测试验证

```bash
cd microservice-architecture/service-discovery/go
go test ./...
```
