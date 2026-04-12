# configuration-center (Python)

## 模块说明

配置中心模式的 Python 实现。使用 dataclass 定义配置结构，元组键实现环境隔离。

## 设计模式应用

- **观察者模式**：实际工程中 Nacos 通过长连接推送变更。本示例简化为主动 refresh。
- **代理模式**：ConfigClient 代理 ConfigCenter 访问并缓存配置。

## 代码结构

```
src/
  __init__.py                — 包初始化
  configuration_center.py    — ServiceConfig + ConfigCenter + ConfigClient
test/
  test_configuration_center.py — unittest 验证完整流程
```

## 与实际工程对比

| 维度 | 本示例 | nacos-sdk-python |
|---|---|---|
| 存储 | 内存字典 | 远程 Nacos 集群 |
| 通知 | 主动 refresh | 长连接 push |
| 格式 | dataclass | YAML / JSON / Properties |

## 测试验证

```bash
cd microservice-architecture/configuration-center/python
python3 -m unittest discover -s test -p "test_*.py"
```
