# service-discovery (Java)

## 模块说明

服务发现模式的 Java 实现。演示服务注册、摘除、实例查询和轮询选择的完整流程。

## 设计模式应用

- **注册表模式（Registry Pattern）**：ServiceRegistry 用嵌套 HashMap 维护服务名到实例列表的映射，computeIfAbsent 保证初始化幂等。
- **策略模式（Strategy Pattern）**：RoundRobinDiscoverer 封装轮询选择策略，通过 offsets Map 维护每个服务的轮询偏移量。

## 代码结构

```
src/
  ServiceRegistry.java  — 注册中心 + 服务实例 + 轮询发现客户端（内部类）
test/
  Test.java             — 验证注册/摘除/轮询完整流程
```

## 与实际工程对比

| 维度 | 本示例 | Eureka / Nacos |
|---|---|---|
| 存储 | 内存 HashMap | AP 模式内存 + 集群同步 |
| 健康检查 | 无 | 心跳续约 + 自动剔除 |
| 线程安全 | 否 | ConcurrentHashMap |
| 通知机制 | 主动查询 | push + 长轮询 |

## 测试验证

```bash
cd microservice-architecture/service-discovery/java
javac src/ServiceRegistry.java test/Test.java
java test.Test
```
