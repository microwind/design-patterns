# microservice-basics (Python)

## 模块说明

微服务基础的 Python 实现。演示从单体到微服务的拆分过程：订单服务通过基类接口调用库存服务，支持从进程内调用（阶段1）无缝切换到 HTTP 远程调用（阶段2）。

## 设计模式应用

- **依赖倒置原则（DIP）**：OrderService 依赖 InventoryClient 基类而非具体实现。Python 用基类 + NotImplementedError 模拟抽象接口。
- **适配器模式（Adapter Pattern）**：HttpInventoryClient 将 urllib HTTP 调用适配为 InventoryClient 接口。
- **策略模式（Strategy Pattern）**：注入 InventoryService（本地）或 HttpInventoryClient（远程），不修改 OrderService。

## 代码结构

```
src/
  __init__.py                — 包初始化文件
  inventory_client.py        — 库存服务契约接口（基类）
  inventory_service.py       — 本地库存服务实现（阶段1）
  http_inventory_client.py   — HTTP 远程库存客户端（阶段2）
  order.py                   — 订单实体（值对象）
  order_service.py           — 订单服务（核心业务服务）
test/
  test.py                    — 阶段1：进程内契约调用测试
  test_http.py               — 阶段2：HTTP 远程调用测试
```

## 与实际工程对比

| 维度 | 本示例 | FastAPI / nameko |
|---|---|---|
| 接口定义 | 基类 + NotImplementedError | ABC / Protocol / Pydantic |
| 远程调用 | urllib 标准库 | httpx / requests / gRPC |
| 服务发现 | 硬编码 base_url | Consul / etcd 动态发现 |
| 序列化 | 纯文本 "OK" | JSON / Pydantic 模型 |

## 测试验证

```bash
cd microservice-architecture/microservice-basics/python
python3 test/test.py
python3 test/test_http.py
```
