from typing import List, Optional, Dict
from app.models.order import Order
from app.models.event import OrderCreatedEvent, OrderPaidEvent, OrderCancelledEvent
from app.repository.order_repository import OrderRepository


class OrderService:
    """订单服务，处理业务逻辑"""

    def __init__(self, order_repository: OrderRepository):
        self.order_repository = order_repository
        self.events = []

    def create_order(self, user_id: int, total_amount: float) -> Dict:
        """创建新订单"""
        order = self.order_repository.create(user_id, total_amount)
        
        # 记录领域事件
        event = OrderCreatedEvent(order.id, order.order_no, user_id, float(total_amount))
        self.events.append(event)
        
        return order.to_dict()

    def get_order(self, order_id: int) -> Optional[Dict]:
        """根据 ID 获取订单"""
        order = self.order_repository.find_by_id(order_id)
        return order.to_dict() if order else None

    def get_all_orders(self) -> List[Dict]:
        """获取所有订单"""
        orders = self.order_repository.find_all()
        return [order.to_dict() for order in orders]

    def get_orders_paginated(self, page: int = 1, per_page: int = 10) -> Dict:
        """分页获取订单"""
        return self.order_repository.find_paginated(page, per_page)

    def get_user_orders(self, user_id: int) -> List[Dict]:
        """根据用户 ID 获取订单"""
        orders = self.order_repository.find_by_user_id(user_id)
        return [order.to_dict() for order in orders]

    def pay_order(self, order_id: int) -> Optional[Dict]:
        """支付订单"""
        order = self.order_repository.find_by_id(order_id)
        if not order:
            raise ValueError(f"Order {order_id} not found")
        
        if order.status != 'PENDING':
            raise ValueError(f"Order {order_id} is not in PENDING status")
        
        order = self.order_repository.update_status(order_id, 'PAID')
        
        # 记录领域事件
        event = OrderPaidEvent(order.id, order.order_no, order.user_id)
        self.events.append(event)
        
        return order.to_dict() if order else None

    def ship_order(self, order_id: int) -> Optional[Dict]:
        """发货"""
        order = self.order_repository.find_by_id(order_id)
        if not order:
            raise ValueError(f"Order {order_id} not found")
        
        if order.status != 'PAID':
            raise ValueError(f"Order {order_id} is not in PAID status")
        
        order = self.order_repository.update_status(order_id, 'SHIPPED')
        return order.to_dict() if order else None

    def deliver_order(self, order_id: int) -> Optional[Dict]:
        """送达"""
        order = self.order_repository.find_by_id(order_id)
        if not order:
            raise ValueError(f"Order {order_id} not found")
        
        if order.status != 'SHIPPED':
            raise ValueError(f"Order {order_id} is not in SHIPPED status")
        
        order = self.order_repository.update_status(order_id, 'DELIVERED')
        return order.to_dict() if order else None

    def cancel_order(self, order_id: int) -> Optional[Dict]:
        """取消订单"""
        order = self.order_repository.find_by_id(order_id)
        if not order:
            raise ValueError(f"Order {order_id} not found")
        
        if order.status not in ['PENDING', 'PAID']:
            raise ValueError(f"Order {order_id} cannot be cancelled in {order.status} status")
        
        order = self.order_repository.update_status(order_id, 'CANCELLED')
        
        # 记录领域事件
        event = OrderCancelledEvent(order.id, order.order_no, order.user_id)
        self.events.append(event)
        
        return order.to_dict() if order else None

    def refund_order(self, order_id: int) -> Optional[Dict]:
        """退款"""
        order = self.order_repository.find_by_id(order_id)
        if not order:
            raise ValueError(f"Order {order_id} not found")
        
        if order.status != 'DELIVERED':
            raise ValueError(f"Order {order_id} is not in DELIVERED status")
        
        order = self.order_repository.update_status(order_id, 'REFUNDED')
        return order.to_dict() if order else None

    def get_events(self):
        """获取已记录的事件"""
        return self.events

    def clear_events(self):
        """清除已记录的事件"""
        self.events.clear()
