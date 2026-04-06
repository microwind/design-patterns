# idempotency-pattern (c)

当前目录演示一个最小幂等服务：

- 首次请求创建订单
- 相同幂等键重复请求返回缓存结果
- 相同幂等键但不同参数返回冲突

## 运行方式

```bash
cd microservice-architecture/idempotency-pattern/c
cc test/test.c src/*.c -o test.out
./test.out
```
