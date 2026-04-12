# configuration-center (TypeScript)

## 模块说明

配置中心模式的 TypeScript 实现。通过 ServiceConfig 类型定义和联合类型提供编译期配置结构安全。

## 设计模式应用

- **观察者模式**：实际工程中配置变更会推送通知。本示例简化为主动 refresh。
- **代理模式**：ConfigClient 代理 ConfigCenter 访问，返回 `ServiceConfig | null` 类型安全。

## 代码结构

```
src/
  configuration_center.ts         — ServiceConfig 类型 + ConfigCenter + ConfigClient
test/
  test_configuration_center.ts    — 验证完整流程
dist/                             — tsc 编译输出
```

## 与实际工程对比

| 维度 | 本示例 | Apollo / Nacos |
|---|---|---|
| 类型安全 | ServiceConfig type | 运行时 JSON 解析 |
| 存储 | 内存 Record | 远程集群 |

## 测试验证

```bash
cd microservice-architecture/configuration-center/ts
tsc -p .
node dist/test/test_configuration_center.js
```
