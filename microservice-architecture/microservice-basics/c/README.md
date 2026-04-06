## 代码结构
```shell
- src
  - func.h
  - inventory_service.c
  - order_service.c
  - http_inventory_client.c
- test
  - test.c       # 阶段1
  - test_http.c  # 阶段2
```

## 测试验证

```shell
$ cd ./microservice-architecture/microservice-basics/c
$ gcc test/test.c src/*.c -o test.out
$ ./test.out
$ gcc test/test_http.c src/*.c -o test_http.out -lpthread
$ ./test_http.out
```
