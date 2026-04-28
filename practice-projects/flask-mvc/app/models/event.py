from dataclasses import dataclass
from datetime import datetime
from typing import Any, Dict
import uuid


@dataclass
class DomainEvent:
    """领域事件基类"""
    event_id: str
    aggregate_id: int
    aggregate_type: str
    event_type: str
    occurred_at: datetime
    data: Dict[str, Any]

    def __init__(self, aggregate_id: int, aggregate_type: str, event_type: str, data: Dict[str, Any]):
        self.event_id = str(uuid.uuid4())
        self.aggregate_id = aggregate_id
        self.aggregate_type = aggregate_type
        self.event_type = event_type
        self.occurred_at = datetime.utcnow()
        self.data = data

    def to_dict(self):
        """将事件转换为字典"""
        return {
            'event_id': self.event_id,
            'aggregate_id': self.aggregate_id,
            'aggregate_type': self.aggregate_type,
            'event_type': self.event_type,
            'occurred_at': self.occurred_at.isoformat(),
            'data': self.data,
        }


@dataclass
class OrderCreatedEvent(DomainEvent):
    """订单创建事件"""
    def __init__(self, order_id: int, order_no: str, user_id: int, total_amount: float):
        data = {
            'order_no': order_no,
            'user_id': user_id,
            'total_amount': total_amount,
        }
        super().__init__(order_id, 'Order', 'order.created', data)


@dataclass
class OrderPaidEvent(DomainEvent):
    """订单支付事件"""
    def __init__(self, order_id: int, order_no: str, user_id: int):
        data = {
            'order_no': order_no,
            'user_id': user_id,
        }
        super().__init__(order_id, 'Order', 'order.paid', data)


@dataclass
class OrderCancelledEvent(DomainEvent):
    """订单取消事件"""
    def __init__(self, order_id: int, order_no: str, user_id: int):
        data = {
            'order_no': order_no,
            'user_id': user_id,
        }
        super().__init__(order_id, 'Order', 'order.cancelled', data)


@dataclass
class UserCreatedEvent(DomainEvent):
    """用户创建事件"""
    def __init__(self, user_id: int, name: str, email: str):
        data = {
            'name': name,
            'email': email,
        }
        super().__init__(user_id, 'User', 'user.created', data)
