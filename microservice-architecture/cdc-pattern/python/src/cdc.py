"""
cdc.py - 变更数据捕获（CDC Pattern）的 Python 实现

【设计模式】
  - 观察者模式（Observer Pattern）：Broker 接收变更事件并分发给下游订阅者。
  - 代理模式（Proxy Pattern）：relay_changes 作为中间代理，解耦 DataStore 与 Broker。

【架构思想】
  业务写入时同步追加变更日志，Connector 扫描未处理变更并发布到 Broker。

【开源对比】
  - Python + Debezium：通过 Kafka Connect 消费变更事件
  - Python + Faust：Kafka Streams 的 Python 实现
  本示例用列表 + 内存 Broker 简化。
"""

from dataclasses import dataclass
from typing import List


@dataclass
class ChangeRecord:
    """变更记录，processed 标记是否已被 Connector 处理。"""
    change_id: str
    aggregate_id: str
    change_type: str
    processed: bool


class DataStore:
    """数据存储（模拟数据库），业务写入时自动追加变更记录。"""

    def __init__(self) -> None:
        self.changes: List[ChangeRecord] = []

    def create_order(self, order_id: str) -> None:
        """创建订单，同时追加一条 order_created 变更记录。"""
        self.changes.append(ChangeRecord(f"CHG-{order_id}", order_id, "order_created", False))

    def relay_changes(self, broker: "Broker") -> None:
        """Connector：扫描未处理变更，发布到 Broker 并标记为已处理。"""
        for change in self.changes:
            if not change.processed:
                broker.published.append(change.change_id)
                change.processed = True


class Broker:
    """消息代理（模拟 Kafka / RabbitMQ）。"""

    def __init__(self) -> None:
        self.published: List[str] = []
