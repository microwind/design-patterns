# service-communication (ts)

当前目录对比两种通信方式：

- 同步调用：订单服务直接调用库存与支付
- 异步事件：订单服务发布事件，由事件总线驱动后续处理

## 运行方式

```bash
cd microservice-architecture/service-communication/ts
tsc -p .
node dist/test/test_communication.js
```
