# configuration-center (JavaScript)

## 模块说明

配置中心模式的 JavaScript (ESM) 实现。演示配置发布、加载和刷新流程。

## 设计模式应用

- **观察者模式**：实际工程中配置变更会推送通知。本示例简化为主动 refresh。
- **代理模式**：ConfigClient 代理 ConfigCenter 访问并缓存配置。

## 代码结构

```
src/
  configuration_center.js         — ConfigCenter + ConfigClient
test/
  test_configuration_center.js    — 验证完整流程
```

## 与实际工程对比

| 维度 | 本示例 | node-config / 远程配置中心 |
|---|---|---|
| 存储 | 内存 Map | 远程服务 / 本地文件 |
| 通知 | 主动 refresh | webhook / 文件监听 |

## 测试验证

```bash
cd microservice-architecture/configuration-center/js
node test/test_configuration_center.js
```
