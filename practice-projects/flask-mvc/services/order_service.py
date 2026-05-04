from decimal import Decimal
from typing import List, Optional
from repositories.order_repository import OrderRepository
from models.order import Order
from utils.domain_events import OrderCreatedEvent, OrderPaidEvent, OrderShippedEvent, OrderCancelledEvent
from utils.events import get_event_bus


class OrderService:
    """订单服务类"""
    
    def __init__(self, order_repository: OrderRepository = None):
        self.order_repository = order_repository or OrderRepository()
        self.event_bus = get_event_bus()
    
    def get_order_by_id(self, order_id: int) -> Optional[Order]:
        """根据ID获取订单"""
        return self.order_repository.get_by_id(order_id)
    
    def get_order_by_no(self, order_no: str) -> Optional[Order]:
        """根据订单号获取订单"""
        return self.order_repository.get_by_order_no(order_no)
    
    def get_user_orders(self, user_id: int) -> List[Order]:
        """获取用户订单列表"""
        return self.order_repository.get_by_user_id(user_id)
    
    def get_all_orders(self) -> List[Order]:
        """获取所有订单"""
        return self.order_repository.get_all()
    
    def create_order(self, order_data: dict) -> Order:
        """创建订单"""
        # 创建订单实体
        order = Order(
            user_id=order_data['user_id'],
            order_no=order_data['order_no'],
            total_amount=Decimal(str(order_data['total_amount'])),
            status='PENDING'
        )
        
        # 保存订单
        saved_order = self.order_repository.create(order)
        
        # 发布订单创建事件
        event = OrderCreatedEvent(
            order_id=saved_order.id,
            order_no=saved_order.order_no,
            user_id=saved_order.user_id,
            total_amount=saved_order.total_amount
        )
        self.event_bus.publish(event)
        
        return saved_order
    
    def pay_order(self, order_id: int) -> Order:
        """支付订单"""
        order = self.order_repository.get_by_id(order_id)
        if not order:
            raise Exception("订单不存在")
        
        if order.status != 'PENDING':
            raise Exception("订单状态不允许支付")
        
        # 更新订单状态
        order.status = 'PAID'
        updated_order = self.order_repository.update(order)
        
        # 发布订单支付事件
        event = OrderPaidEvent(
            order_id=updated_order.id,
            order_no=updated_order.order_no
        )
        self.event_bus.publish(event)
        
        return updated_order
    
    def ship_order(self, order_id: int) -> Order:
        """发货订单"""
        order = self.order_repository.get_by_id(order_id)
        if not order:
            raise Exception("订单不存在")
        
        if order.status != 'PAID':
            raise Exception("订单状态不允许发货")
        
        # 更新订单状态
        order.status = 'SHIPPED'
        updated_order = self.order_repository.update(order)
        
        # 发布订单发货事件
        event = OrderShippedEvent(
            order_id=updated_order.id,
            order_no=updated_order.order_no
        )
        self.event_bus.publish(event)
        
        return updated_order
    
    def cancel_order(self, order_id: int) -> Order:
        """取消订单"""
        order = self.order_repository.get_by_id(order_id)
        if not order:
            raise Exception("订单不存在")
        
        if order.status not in ['PENDING', 'PAID']:
            raise Exception("订单状态不允许取消")
        
        # 更新订单状态
        order.status = 'CANCELLED'
        updated_order = self.order_repository.update(order)
        
        # 发布订单取消事件
        event = OrderCancelledEvent(
            order_id=updated_order.id,
            order_no=updated_order.order_no
        )
        self.event_bus.publish(event)
        
        return updated_order
    
    def delete_order(self, order_id: int) -> bool:
        """删除订单"""
        return self.order_repository.delete(order_id)
    
    def get_order_count_by_user(self, user_id: int) -> int:
        """获取用户订单数量"""
        return self.order_repository.count_by_user_id(user_id)