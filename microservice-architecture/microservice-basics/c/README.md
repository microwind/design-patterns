# microservice-basics (C)

## 模块说明

微服务基础的 C 语言实现。用结构体 + 函数指针模拟面向对象的依赖注入，演示从进程内调用（阶段1）到 POSIX socket HTTP 调用（阶段2）的演进。

## 设计模式应用

- **依赖倒置原则（DIP）**：OrderService 通过 inventory 指针和 reserve 函数指针依赖库存服务抽象，不直接调用具体函数。这种模式在 Linux 内核 VFS 中广泛使用。
- **适配器模式（Adapter Pattern）**：reserve_over_http 将 TCP socket + HTTP 协议适配为与本地 reserve 相同的调用签名。
- **策略模式（Strategy Pattern）**：inventory_service_init 绑定本地策略，也可替换为 HTTP 策略。

## 代码结构

```
src/
  func.h                   — 头文件，定义结构体和函数指针接口
  inventory_service.c      — 本地库存服务（函数指针绑定）
  order_service.c          — 订单服务（通过函数指针调用库存）
  http_inventory_client.c  — HTTP 远程库存客户端（POSIX socket）
test/
  test.c                   — 阶段1：进程内契约调用测试
  test_http.c              — 阶段2：HTTP 远程调用测试
```

## 与实际工程对比

| 维度 | 本示例 | Nginx / Envoy |
|---|---|---|
| 接口抽象 | 函数指针 | C++ 虚函数 / C 函数指针表 |
| HTTP 通信 | POSIX socket 手动构造 | libcurl / libevent |
| 服务发现 | 硬编码 host:port | DNS / Consul 动态发现 |
| 并发处理 | 无 | epoll / kqueue 事件循环 |

## 测试验证

```bash
cd microservice-architecture/microservice-basics/c
gcc test/test.c src/inventory_service.c src/order_service.c -o test.out && ./test.out
```
