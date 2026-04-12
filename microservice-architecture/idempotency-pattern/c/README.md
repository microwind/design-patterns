# idempotency-pattern (C)

## 模块说明

幂等模式的 C 语言实现。用固定数组存储幂等记录，snprintf 生成指纹，strcmp 匹配键。

## 设计模式应用

- **备忘录模式**：IdempotencyRecord 数组存储首次执行结果。
- **代理模式**：create_order_with_idempotency 函数包裹业务逻辑。

## 代码结构

```
src/
  func.h          — 头文件，定义结构体和函数声明
  idempotency.c   — 幂等服务实现（init / create_order_with_idempotency）
test/
  test.c          — 验证首次/重复/冲突三条路径
```

## 与实际工程对比

| 维度 | 本示例 | Redis C 客户端 |
|---|---|---|
| 存储 | 固定数组 | Redis SETNX |
| 查找 | 线性遍历 | O(1) 哈希查找 |
| 指纹 | snprintf 拼接 | SHA-256 |
| 并发 | 非线程安全 | Redis 原子操作 |

## 测试验证

```bash
cd microservice-architecture/idempotency-pattern/c
cc test/test.c src/*.c -o test.out
./test.out
```
