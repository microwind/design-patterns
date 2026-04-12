# microservice-basics (TypeScript)

## 模块说明

微服务基础的 TypeScript 实现。利用 TypeScript 的类型系统（interface、联合类型、readonly）提供编译期安全保障，演示从进程内调用到 HTTP 远程调用的演进。

## 设计模式应用

- **依赖倒置原则（DIP）**：OrderService 依赖 InventoryClient interface，TypeScript 接口在编译后被擦除但开发时提供类型安全。
- **适配器模式（Adapter Pattern）**：HttpInventoryClient 将 HTTP 调用适配为 AsyncInventoryClient 接口。
- **策略模式（Strategy Pattern）**：注入 InventoryService（同步）或 HttpInventoryClient（异步），分别对应 OrderService 和 OrderServiceAsync。

## 代码结构

```
src/
  InventoryClient.ts       — 同步库存服务契约接口
  InventoryService.ts      — 本地库存服务实现（阶段1）
  HttpInventoryClient.ts   — HTTP 远程库存客户端（阶段2）
  Order.ts                 — 订单实体（联合类型保证 status 取值安全）
  OrderService.ts          — 同步订单服务（阶段1）
  OrderServiceAsync.ts     — 异步订单服务 + AsyncInventoryClient 接口（阶段2）
test/
  test.ts                  — 阶段1 测试
  test_http.ts             — 阶段2 测试
dist/                      — tsc 编译输出目录
```

## 与实际工程对比

| 维度 | 本示例 | NestJS / tRPC |
|---|---|---|
| 类型安全 | interface + 联合类型 | tRPC 端到端类型安全 |
| 依赖注入 | 构造函数注入 | NestJS @Inject + IoC |
| 远程调用 | Node.js 原生 http | axios / tRPC client |
| 契约定义 | TypeScript interface | Protobuf / tRPC router |

## 测试验证

```bash
cd microservice-architecture/microservice-basics/ts
npm install
npx tsc
node test/test.js
node test/test_http.js
```
