from datetime import datetime
from sqlalchemy import Column, Integer, String, Numeric, DateTime, ForeignKey
from sqlalchemy.orm import relationship
from app.models import db


class Order(db.Model):
    """订单模型，用于订单数据库"""
    __tablename__ = 'orders'
    __bind_key__ = 'order'

    id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(Integer, nullable=False)
    order_no = Column(String(50), nullable=False, unique=True)
    total_amount = Column(Numeric(10, 2), nullable=False)
    status = Column(String(20), default='PENDING')  # PENDING, PAID, SHIPPED, DELIVERED, CANCELLED, REFUNDED
    created_at = Column('created_at', DateTime, default=datetime.utcnow)
    updated_at = Column('updated_at', DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    def to_dict(self):
        """将模型转换为字典"""
        return {
            'id': self.id,
            'user_id': self.user_id,
            'order_no': self.order_no,
            'total_amount': float(self.total_amount) if self.total_amount else 0,
            'status': self.status,
            'created_at': self.created_at.isoformat() if self.created_at else None,
            'updated_at': self.updated_at.isoformat() if self.updated_at else None,
        }

    def __repr__(self):
        return f'<Order {self.id}: {self.order_no}>'
