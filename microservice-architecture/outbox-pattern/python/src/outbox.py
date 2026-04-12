"""
outbox.py - Outbox 模式（Outbox Pattern）的 Python 实现

【设计模式】
  - 观察者模式：outbox 事件被 relay 扫描并发布到 broker。
  - 命令模式：OutboxEvent 将事件封装为数据对象，relay 异步发布。

【架构思想】
  解决"写库成功 + 发消息失败"导致的数据与事件不一致问题。

【开源对比】
  - Python + Celery：异步任务队列，可配合 outbox 模式使用
  - Django signals + outbox 表：在 Django 中实现 outbox
  本示例用内存列表模拟。
"""

from dataclasses import dataclass
from typing import List


@dataclass
class Order:
    """订单实体。"""
    order_id: str
    status: str


@dataclass
class OutboxEvent:
    """Outbox 事件记录。

    【设计模式】命令模式：将事件封装为数据对象，relay 异步发布。
    status 控制发布状态：pending → published。
    """
    event_id: str       # 事件唯一ID
    aggregate_id: str   # 聚合根ID
    event_type: str     # 事件类型
    status: str         # 发布状态：pending / published


class OutboxService:
    """Outbox 服务。

    核心流程：
      1. create_order：写入 orders + outbox（同一"事务"）
      2. relay_pending：扫描 pending → 发布 → 标记 published
    """

    def __init__(self) -> None:
        self.orders: List[Order] = []          # 模拟 orders 表
        self.outbox: List[OutboxEvent] = []    # 模拟 outbox 表

    def create_order(self, order_id: str) -> None:
        """创建订单，同时写入 orders 和 outbox（模拟同一事务）。"""
        self.orders.append(Order(order_id, "CREATED"))
        self.outbox.append(OutboxEvent(f"EVT-{order_id}", order_id, "order_created", "pending"))

    def relay_pending(self, broker: "MemoryBroker") -> None:
        """relay 中继：扫描 pending 事件，发布后标记 published。"""
        for event in self.outbox:
            if event.status == "pending":
                # 发布到消息中间件
                broker.published.append(event.event_id)
                # 标记为已发布
                event.status = "published"


class MemoryBroker:
    """内存消息代理（模拟 Kafka / RabbitMQ）。"""

    def __init__(self) -> None:
        self.published: List[str] = []
