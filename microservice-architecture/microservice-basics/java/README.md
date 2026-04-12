# microservice-basics (Java)

## 模块说明

微服务基础的 Java 实现。演示从单体到微服务的拆分过程：订单服务通过契约接口调用库存服务，支持从进程内调用（阶段1）无缝切换到 HTTP 远程调用（阶段2）。

## 设计模式应用

- **依赖倒置原则（DIP）**：OrderService 依赖 InventoryClient 接口而非具体实现，通过构造函数注入实现 IoC。
- **适配器模式（Adapter Pattern）**：HttpInventoryClient 将 HTTP 远程调用适配为 InventoryClient 接口。
- **策略模式（Strategy Pattern）**：注入 InventoryService（本地）或 HttpInventoryClient（远程），不修改 OrderService。

## 代码结构

```
src/
  InventoryClient.java       — 库存服务契约接口（依赖倒置的核心）
  InventoryService.java      — 本地库存服务实现（阶段1）
  HttpInventoryClient.java   — HTTP 远程库存客户端（阶段2）
  Order.java                 — 订单实体（值对象）
  OrderService.java          — 订单服务（核心业务服务）
test/
  Test.java                  — 阶段1：进程内契约调用测试
  TestHttp.java              — 阶段2：HTTP 远程调用测试
```

## 与实际工程对比

| 维度 | 本示例 | Spring Cloud |
|---|---|---|
| 依赖注入 | 构造函数手动注入 | @Autowired + IoC 容器 |
| 远程调用 | Java 原生 HttpClient | OpenFeign 声明式客户端 |
| 服务发现 | 硬编码 baseUrl | Eureka / Nacos 动态发现 |
| 序列化 | 纯文本 "OK" | JSON / Protobuf |

## 测试验证

```bash
cd microservice-architecture/microservice-basics/java
javac src/*.java test/Test.java && java test.Test
```
