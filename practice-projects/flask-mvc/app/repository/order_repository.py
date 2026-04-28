from typing import List, Optional
from app.models.order import Order
from app.models import db


class OrderRepository:
    """Order repository for data access operations"""

    def create(self, user_id: int, total_amount: float) -> Order:
        """Create a new order"""
        import time
        order_no = f"ORD{int(time.time() * 1000)}"
        order = Order(user_id=user_id, order_no=order_no, total_amount=total_amount)
        db.session.add(order)
        db.session.commit()
        db.session.refresh(order)
        return order

    def find_by_id(self, order_id: int) -> Optional[Order]:
        """Find order by ID"""
        return Order.query.filter_by(id=order_id).first()

    def find_by_order_no(self, order_no: str) -> Optional[Order]:
        """Find order by order number"""
        return Order.query.filter_by(order_no=order_no).first()

    def find_all(self) -> List[Order]:
        """Get all orders"""
        return Order.query.all()

    def find_by_user_id(self, user_id: int) -> List[Order]:
        """Find orders by user ID"""
        return Order.query.filter_by(user_id=user_id).all()

    def update_status(self, order_id: int, status: str) -> Optional[Order]:
        """Update order status"""
        order = self.find_by_id(order_id)
        if order:
            order.status = status
            db.session.commit()
            db.session.refresh(order)
        return order

    def delete(self, order_id: int) -> bool:
        """Delete order by ID"""
        order = self.find_by_id(order_id)
        if order:
            db.session.delete(order)
            db.session.commit()
            return True
        return False
