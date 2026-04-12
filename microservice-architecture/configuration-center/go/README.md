# configuration-center (Go)

## 模块说明

配置中心模式的 Go 实现。演示配置发布、客户端加载、配置更新后刷新的完整流程。

## 设计模式应用

- **观察者模式**：实际工程中 etcd 通过 watch 机制推送变更。本示例简化为主动 Refresh。
- **代理模式**：ConfigClient 代理 ConfigCenter 访问并缓存配置。

## 代码结构

```
src/
  configuration_center.go         — ServiceConfig + ConfigCenter + ConfigClient
test/
  configuration_center_test.go    — Go 标准测试
```

## 与实际工程对比

| 维度 | 本示例 | etcd / Consul KV |
|---|---|---|
| 存储 | 内存 map | Raft 共识 + 持久化 |
| 通知 | 主动 Refresh | watch / blocking query |
| 一致性 | 单节点 | CP 强一致 |

## 测试验证

```bash
cd microservice-architecture/configuration-center/go
go test ./...
```
