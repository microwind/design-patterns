# microservice-basics (JavaScript)

## 模块说明

微服务基础的 JavaScript (ESM) 实现。演示从单体到微服务的拆分过程。阶段2 使用 async/await 处理异步 HTTP 调用，体现了 Node.js 异步 I/O 的特性。

## 设计模式应用

- **依赖倒置原则（DIP）**：OrderService 依赖 inventoryClient 的 reserve 方法约定（鸭子类型）。
- **适配器模式（Adapter Pattern）**：HttpInventoryClient 将 Node.js 原生 http 调用适配为 reserve 接口。
- **策略模式（Strategy Pattern）**：注入 InventoryService（同步本地）或 HttpInventoryClient（异步远程），OrderServiceAsync 处理异步场景。

## 代码结构

```
src/
  InventoryService.js      — 本地库存服务实现（阶段1，同步）
  HttpInventoryClient.js   — HTTP 远程库存客户端（阶段2，异步 Promise）
  Order.js                 — 订单实体（值对象）
  OrderService.js          — 同步订单服务（阶段1）
  OrderServiceAsync.js     — 异步订单服务（阶段2，async/await）
test/
  test.js                  — 阶段1：进程内契约调用测试
  test_http.js             — 阶段2：HTTP 远程调用测试
```

## 与实际工程对比

| 维度 | 本示例 | NestJS / Express |
|---|---|---|
| 依赖注入 | 构造函数注入 | @Inject 装饰器 / 中间件 |
| 远程调用 | Node.js 原生 http | axios / node-fetch |
| 异步处理 | Promise + async/await | 同样，但有更完善的错误链 |
| 服务发现 | 硬编码 URL | Consul / etcd |

## 测试验证

```bash
cd microservice-architecture/microservice-basics/js
node test/test.js
node test/test_http.js
```
