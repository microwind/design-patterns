# load-balancing (go)

当前目录演示 3 种常见负载均衡策略：

- 轮询
- 加权轮询
- 最少连接

## 目录结构

```text
go/
├── go.mod
├── src/
│   └── balancer.go
└── test/
    └── balancer_test.go
```

## 运行方式

```bash
cd microservice-architecture/load-balancing/go
go test ./...
```
