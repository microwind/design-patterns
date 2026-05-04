from typing import List, Optional
from sqlalchemy.exc import SQLAlchemyError
from utils.extensions import db
from models.order import Order


class OrderRepository:
    """订单仓储类"""
    
    def create(self, order: Order) -> Order:
        """创建订单"""
        try:
            db.session.add(order)
            db.session.commit()
            db.session.refresh(order)
            return order
        except SQLAlchemyError as e:
            db.session.rollback()
            raise Exception(f"创建订单失败: {str(e)}")
    
    def get_by_id(self, order_id: int) -> Optional[Order]:
        """根据ID获取订单"""
        try:
            return Order.query.filter_by(id=order_id).first()
        except SQLAlchemyError as e:
            raise Exception(f"获取订单失败: {str(e)}")
    
    def get_by_order_no(self, order_no: str) -> Optional[Order]:
        """根据订单号获取订单"""
        try:
            return Order.query.filter_by(order_no=order_no).first()
        except SQLAlchemyError as e:
            raise Exception(f"获取订单失败: {str(e)}")
    
    def get_by_user_id(self, user_id: int) -> List[Order]:
        """根据用户ID获取订单列表"""
        try:
            return Order.query.filter_by(user_id=user_id).all()
        except SQLAlchemyError as e:
            raise Exception(f"获取用户订单失败: {str(e)}")
    
    def get_all(self) -> List[Order]:
        """获取所有订单"""
        try:
            return Order.query.all()
        except SQLAlchemyError as e:
            raise Exception(f"获取订单列表失败: {str(e)}")
    
    def update(self, order: Order) -> Order:
        """更新订单"""
        try:
            db.session.merge(order)
            db.session.commit()
            db.session.refresh(order)
            return order
        except SQLAlchemyError as e:
            db.session.rollback()
            raise Exception(f"更新订单失败: {str(e)}")
    
    def delete(self, order_id: int) -> bool:
        """删除订单"""
        try:
            order = Order.query.filter_by(id=order_id).first()
            if order:
                db.session.delete(order)
                db.session.commit()
                return True
            return False
        except SQLAlchemyError as e:
            db.session.rollback()
            raise Exception(f"删除订单失败: {str(e)}")
    
    def count_by_user_id(self, user_id: int) -> int:
        """统计用户订单数量"""
        try:
            return Order.query.filter_by(user_id=user_id).count()
        except SQLAlchemyError as e:
            raise Exception(f"统计订单数量失败: {str(e)}")
