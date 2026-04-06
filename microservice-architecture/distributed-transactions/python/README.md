# distributed-transactions (python)

当前目录演示一个最小 Saga 补偿事务：

- 创建订单
- 预占库存
- 扣款
- 失败时补偿并取消订单

## 运行方式

```bash
cd microservice-architecture/distributed-transactions/python
python3 -m unittest discover -s test -p "test_*.py"
```
