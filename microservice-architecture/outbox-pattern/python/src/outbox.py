from dataclasses import dataclass
from typing import List


@dataclass
class Order:
    order_id: str
    status: str


@dataclass
class OutboxEvent:
    event_id: str
    aggregate_id: str
    event_type: str
    status: str


class OutboxService:
    def __init__(self) -> None:
        self.orders: List[Order] = []
        self.outbox: List[OutboxEvent] = []

    def create_order(self, order_id: str) -> None:
        self.orders.append(Order(order_id, "CREATED"))
        self.outbox.append(OutboxEvent(f"EVT-{order_id}", order_id, "order_created", "pending"))

    def relay_pending(self, broker: "MemoryBroker") -> None:
        for event in self.outbox:
            if event.status == "pending":
                broker.published.append(event.event_id)
                event.status = "published"


class MemoryBroker:
    def __init__(self) -> None:
        self.published: List[str] = []
