# service-discovery (go)

当前目录提供一个最小服务发现示例，演示：

- 服务注册
- 服务摘除
- 实例查询
- 基于发现结果的轮询选择

## 目录结构

```text
go/
├── go.mod
├── src/
│   └── registry.go
└── test/
    └── registry_test.go
```

## 运行方式

```bash
cd microservice-architecture/service-discovery/go
go test ./...
```
