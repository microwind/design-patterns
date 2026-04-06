# load-balancing (ts)

当前目录演示 3 种常见负载均衡策略：

- 轮询
- 加权轮询
- 最少连接

## 运行方式

```bash
cd microservice-architecture/load-balancing/ts
tsc -p .
node dist/test/test_balancer.js
```
