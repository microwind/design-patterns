# idempotency-pattern (python)

当前目录演示一个最小幂等服务：

- 首次请求创建订单
- 相同幂等键重复请求返回缓存结果
- 相同幂等键但不同参数返回冲突

## 运行方式

```bash
cd microservice-architecture/idempotency-pattern/python
python3 -m unittest discover -s test -p "test_*.py"
```
