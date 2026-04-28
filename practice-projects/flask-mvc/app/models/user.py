from datetime import datetime
from sqlalchemy import Column, Integer, String, DateTime
from app.models import db


class User(db.Model):
    """用户模型，用于用户数据库"""
    __tablename__ = 'users'
    __bind_key__ = 'user'

    id = Column(Integer, primary_key=True, autoincrement=True)
    name = Column(String(100), nullable=False)
    email = Column(String(100), nullable=False, unique=True)
    phone = Column(String(20))
    created_time = Column('created_time', DateTime, default=datetime.utcnow)
    updated_time = Column('updated_time', DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    def to_dict(self):
        """将模型转换为字典"""
        return {
            'id': self.id,
            'name': self.name,
            'email': self.email,
            'phone': self.phone,
            'created_at': self.created_time.isoformat() if self.created_time else None,
            'updated_at': self.updated_time.isoformat() if self.updated_time else None,
        }

    def __repr__(self):
        return f'<User {self.id}: {self.name}>'
