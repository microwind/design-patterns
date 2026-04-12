# configuration-center (Java)

## 模块说明

配置中心模式的 Java 实现。演示配置发布、客户端加载、配置更新后刷新的完整流程。

## 设计模式应用

- **观察者模式（Observer Pattern）**：实际工程中配置变更会推送通知（如 Apollo 长轮询）。本示例简化为客户端主动 refresh。
- **代理模式（Proxy Pattern）**：ConfigClient 代理 ConfigCenter 访问，本地缓存当前配置快照。
- **单例模式（Singleton Pattern）**：ConfigCenter 通常全局唯一。

## 代码结构

```
src/
  ConfigurationCenter.java  — ConfigCenter + ConfigClient + ServiceConfig（内部类）
test/
  Test.java                 — 验证发布/加载/更新/刷新完整流程
```

## 与实际工程对比

| 维度 | 本示例 | Apollo / Nacos |
|---|---|---|
| 存储 | 内存 HashMap | MySQL + 内存缓存 |
| 通知 | 客户端主动 refresh | 长轮询 / 长连接 push |
| 版本 | 手动设置 version | 自动版本号 + 回滚 |
| 灰度 | 无 | 灰度发布 + 命名空间 |

## 测试验证

```bash
cd microservice-architecture/configuration-center/java
javac src/ConfigurationCenter.java test/Test.java
java test.Test
```
