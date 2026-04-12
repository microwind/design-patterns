# service-discovery (Python)

## 模块说明

服务发现模式的 Python 实现。使用 dataclass 定义不可变服务实例，演示注册/摘除/轮询的完整流程。

## 设计模式应用

- **注册表模式（Registry Pattern）**：ServiceRegistry 用嵌套字典维护映射。
- **策略模式（Strategy Pattern）**：RoundRobinDiscoverer 封装轮询策略。
- **值对象模式**：ServiceInstance 使用 `@dataclass(frozen=True)` 保证不可变。

## 代码结构

```
src/
  __init__.py       — 包初始化
  registry.py       — ServiceInstance + ServiceRegistry + RoundRobinDiscoverer
test/
  test_registry.py  — unittest 验证完整流程
```

## 与实际工程对比

| 维度 | 本示例 | python-consul / nacos-sdk |
|---|---|---|
| 存储 | 内存字典 | 远程注册中心 |
| 健康检查 | 无 | TTL / HTTP 探测 |
| 异步支持 | 同步 | asyncio / aiohttp |

## 测试验证

```bash
cd microservice-architecture/service-discovery/python
python3 -m unittest discover -s test -p "test_*.py"
```
