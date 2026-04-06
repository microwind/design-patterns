# service-communication (java)

当前目录对比两种通信方式：

- 同步调用：订单服务直接调用库存与支付
- 异步事件：订单服务发布事件，由事件总线驱动后续处理

## 运行方式

```bash
cd microservice-architecture/service-communication/java
javac src/CommunicationModels.java test/Test.java
java test.Test
```
