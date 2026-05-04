"""领域事件定义"""
from dataclasses import dataclass
from decimal import Decimal
from utils.events import DomainEvent


@dataclass(frozen=True)
class OrderCreatedEvent(DomainEvent):
    """订单创建事件"""
    order_id: int = 0
    order_no: str = ""
    user_id: int = 0
    total_amount: Decimal = Decimal("0")


@dataclass(frozen=True)
class OrderPaidEvent(DomainEvent):
    """订单支付事件"""
    order_id: int = 0
    order_no: str = ""


@dataclass(frozen=True)
class OrderShippedEvent(DomainEvent):
    """订单发货事件"""
    order_id: int = 0
    order_no: str = ""


@dataclass(frozen=True)
class OrderCancelledEvent(DomainEvent):
    """订单取消事件"""
    order_id: int = 0
    order_no: str = ""


@dataclass(frozen=True)
class UserCreatedEvent(DomainEvent):
    """用户创建事件"""
    user_id: int = 0
    name: str = ""
    email: str = ""
