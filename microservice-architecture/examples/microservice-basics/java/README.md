## 代码结构
```shell
- src
  - InventoryClient.java
  - InventoryService.java
  - HttpInventoryClient.java
  - Order.java
  - OrderService.java
- test
  - Test.java      # 阶段1
  - TestHttp.java  # 阶段2
```

## 测试验证

```shell
$ cd ./microservice-architecture/examples/microservice-basics/java
$ javac test/Test.java test/TestHttp.java
$ java test/Test
$ java test/TestHttp
```
