from typing import List, Optional
from app.models.order import Order
from app.models import db


class OrderRepository:
    """订单仓储，用于数据访问操作"""

    def create(self, user_id: int, total_amount: float) -> Order:
        """创建新订单"""
        import time
        order_no = f"ORD{int(time.time() * 1000)}"
        order = Order(user_id=user_id, order_no=order_no, total_amount=total_amount)
        db.session.add(order)
        db.session.commit()
        db.session.refresh(order)
        return order

    def find_by_id(self, order_id: int) -> Optional[Order]:
        """根据 ID 查找订单"""
        return Order.query.filter_by(id=order_id).first()

    def find_by_order_no(self, order_no: str) -> Optional[Order]:
        """根据订单号查找订单"""
        return Order.query.filter_by(order_no=order_no).first()

    def find_all(self) -> List[Order]:
        """获取所有订单"""
        return Order.query.all()

    def find_paginated(self, page: int = 1, per_page: int = 10) -> dict:
        """分页获取订单"""
        pagination = Order.query.paginate(page=page, per_page=per_page, error_out=False)
        return {
            'items': [order.to_dict() for order in pagination.items],
            'total': pagination.total,
            'pages': pagination.pages,
            'current_page': pagination.page,
            'per_page': per_page,
            'has_next': pagination.has_next,
            'has_prev': pagination.has_prev
        }

    def find_by_user_id(self, user_id: int) -> List[Order]:
        """根据用户 ID 查找订单"""
        return Order.query.filter_by(user_id=user_id).all()

    def update_status(self, order_id: int, status: str) -> Optional[Order]:
        """更新订单状态"""
        order = self.find_by_id(order_id)
        if order:
            order.status = status
            db.session.commit()
            db.session.refresh(order)
        return order

    def delete(self, order_id: int) -> bool:
        """根据 ID 删除订单"""
        order = self.find_by_id(order_id)
        if order:
            db.session.delete(order)
            db.session.commit()
            return True
        return False
