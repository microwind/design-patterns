# service-discovery (C)

## 模块说明

服务发现模式的 C 语言实现。用固定大小数组和索引模拟注册中心，registry_next 实现轮询。

## 设计模式应用

- **注册表模式（Registry Pattern）**：ServiceRegistry 用固定数组存储实例，count 字段跟踪数量。
- **策略模式（Strategy Pattern）**：registry_next 通过 next_index 取模实现轮询，类似 Nginx upstream 的轮询逻辑。

## 代码结构

```
src/
  func.h      — 头文件，定义 ServiceInstance / ServiceRegistry 结构体和函数声明
  registry.c  — 注册中心实现（init / register / deregister / next）
test/
  test.c      — 验证注册/摘除/轮询完整流程
```

## 与实际工程对比

| 维度 | 本示例 | Nginx upstream |
|---|---|---|
| 存储 | 固定数组（MAX_INSTANCES=8） | 动态链表 |
| 负载策略 | 简单轮询 | 轮询 / 加权 / IP哈希 / 最少连接 |
| 健康检查 | 无 | max_fails + fail_timeout |
| 动态更新 | 手动 register/deregister | API / 配置重载 |

## 测试验证

```bash
cd microservice-architecture/service-discovery/c
cc test/test.c src/*.c -o test.out
./test.out
```
