# configuration-center (C)

## 模块说明

配置中心模式的 C 语言实现。用固定数组存储配置，结构体模拟配置对象。

## 设计模式应用

- **注册表模式**：ConfigCenter 用数组线性查找实现 key-value 存储，put 支持同 key 覆盖更新。
- **代理模式**：ConfigClient 函数代理 ConfigCenter 访问并缓存配置快照到 current 字段。

## 代码结构

```
src/
  func.h                   — 头文件，定义 ServiceConfig / ConfigCenter / ConfigClient 结构体
  configuration_center.c   — 配置中心实现（init / put / get / load / refresh）
test/
  test.c                   — 验证发布/加载/更新/刷新完整流程
```

## 与实际工程对比

| 维度 | 本示例 | Nginx / Envoy |
|---|---|---|
| 存储 | 固定数组 | 配置文件 / xDS API |
| 更新 | 函数调用 | reload 信号 / API 推送 |
| 格式 | C 结构体 | nginx.conf / YAML |

## 测试验证

```bash
cd microservice-architecture/configuration-center/c
cc test/test.c src/*.c -o test.out
./test.out
```
