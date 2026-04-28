from typing import List, Optional, Dict
from app.models.order import Order
from app.models.event import OrderCreatedEvent, OrderPaidEvent, OrderCancelledEvent
from app.repository.order_repository import OrderRepository


class OrderService:
    """Order service for business logic"""

    def __init__(self, order_repository: OrderRepository):
        self.order_repository = order_repository
        self.events = []

    def create_order(self, user_id: int, total_amount: float) -> Dict:
        """Create a new order"""
        order = self.order_repository.create(user_id, total_amount)
        
        # Record domain event
        event = OrderCreatedEvent(order.id, order.order_no, user_id, float(total_amount))
        self.events.append(event)
        
        return order.to_dict()

    def get_order(self, order_id: int) -> Optional[Dict]:
        """Get order by ID"""
        order = self.order_repository.find_by_id(order_id)
        return order.to_dict() if order else None

    def get_all_orders(self) -> List[Dict]:
        """Get all orders"""
        orders = self.order_repository.find_all()
        return [order.to_dict() for order in orders]

    def get_user_orders(self, user_id: int) -> List[Dict]:
        """Get orders by user ID"""
        orders = self.order_repository.find_by_user_id(user_id)
        return [order.to_dict() for order in orders]

    def pay_order(self, order_id: int) -> Optional[Dict]:
        """Pay order"""
        order = self.order_repository.find_by_id(order_id)
        if not order:
            raise ValueError(f"Order {order_id} not found")
        
        if order.status != 'PENDING':
            raise ValueError(f"Order {order_id} is not in PENDING status")
        
        order = self.order_repository.update_status(order_id, 'PAID')
        
        # Record domain event
        event = OrderPaidEvent(order.id, order.order_no, order.user_id)
        self.events.append(event)
        
        return order.to_dict() if order else None

    def ship_order(self, order_id: int) -> Optional[Dict]:
        """Ship order"""
        order = self.order_repository.find_by_id(order_id)
        if not order:
            raise ValueError(f"Order {order_id} not found")
        
        if order.status != 'PAID':
            raise ValueError(f"Order {order_id} is not in PAID status")
        
        order = self.order_repository.update_status(order_id, 'SHIPPED')
        return order.to_dict() if order else None

    def deliver_order(self, order_id: int) -> Optional[Dict]:
        """Deliver order"""
        order = self.order_repository.find_by_id(order_id)
        if not order:
            raise ValueError(f"Order {order_id} not found")
        
        if order.status != 'SHIPPED':
            raise ValueError(f"Order {order_id} is not in SHIPPED status")
        
        order = self.order_repository.update_status(order_id, 'DELIVERED')
        return order.to_dict() if order else None

    def cancel_order(self, order_id: int) -> Optional[Dict]:
        """Cancel order"""
        order = self.order_repository.find_by_id(order_id)
        if not order:
            raise ValueError(f"Order {order_id} not found")
        
        if order.status not in ['PENDING', 'PAID']:
            raise ValueError(f"Order {order_id} cannot be cancelled in {order.status} status")
        
        order = self.order_repository.update_status(order_id, 'CANCELLED')
        
        # Record domain event
        event = OrderCancelledEvent(order.id, order.order_no, order.user_id)
        self.events.append(event)
        
        return order.to_dict() if order else None

    def refund_order(self, order_id: int) -> Optional[Dict]:
        """Refund order"""
        order = self.order_repository.find_by_id(order_id)
        if not order:
            raise ValueError(f"Order {order_id} not found")
        
        if order.status != 'DELIVERED':
            raise ValueError(f"Order {order_id} is not in DELIVERED status")
        
        order = self.order_repository.update_status(order_id, 'REFUNDED')
        return order.to_dict() if order else None

    def get_events(self):
        """Get recorded events"""
        return self.events

    def clear_events(self):
        """Clear recorded events"""
        self.events.clear()
