# outbox-pattern (c)

当前目录演示一个最小 outbox 流程：

- 创建订单并写入 pending outbox 事件
- relay 发布事件
- 发布后标记为 published

## 运行方式

```bash
cd microservice-architecture/outbox-pattern/c
cc test/test.c src/*.c -o test.out
./test.out
```
