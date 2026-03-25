## 代码结构
```shell
- src
  - inventory_client.go
  - inventory_service.go
  - http_inventory_client.go
  - order.go
  - order_service.go
- test
  - test.go       # 阶段1
  - test_http.go  # 阶段2
```

## 测试验证

```shell
$ cd ./microservice-architecture/examples/microservice-basics/go
$ go run test/test.go
$ go run test/test_http.go
```
