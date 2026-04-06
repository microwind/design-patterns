## 代码结构
```shell
- src
  - InventoryClient.ts
  - InventoryService.ts
  - HttpInventoryClient.ts
  - Order.ts
  - OrderService.ts
  - OrderServiceAsync.ts
- test
  - test.ts       # 阶段1
  - test_http.ts  # 阶段2
```

## 测试验证

```shell
$ cd ./microservice-architecture/microservice-basics/ts
$ npm install
$ npx tsc
$ node test/test.js
$ node test/test_http.js
```
