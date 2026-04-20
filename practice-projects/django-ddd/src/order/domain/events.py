"""订单相关领域事件。"""
from __future__ import annotations

from dataclasses import dataclass
from decimal import Decimal

from shared.domain.events import DomainEvent


@dataclass(frozen=True)
class OrderCreatedEvent(DomainEvent):
    order_id: int = 0
    order_no: str = ""
    user_id: int = 0
    total_amount: Decimal = Decimal("0")


@dataclass(frozen=True)
class OrderPaidEvent(DomainEvent):
    order_id: int = 0
    order_no: str = ""


@dataclass(frozen=True)
class OrderShippedEvent(DomainEvent):
    order_id: int = 0
    order_no: str = ""


@dataclass(frozen=True)
class OrderDeliveredEvent(DomainEvent):
    order_id: int = 0
    order_no: str = ""


@dataclass(frozen=True)
class OrderCancelledEvent(DomainEvent):
    order_id: int = 0
    order_no: str = ""


@dataclass(frozen=True)
class OrderRefundedEvent(DomainEvent):
    order_id: int = 0
    order_no: str = ""
