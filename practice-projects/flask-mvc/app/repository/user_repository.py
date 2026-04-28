from typing import List, Optional
from app.models.user import User
from app.models import db


class UserRepository:
    """用户仓储，用于数据访问操作"""

    def create(self, name: str, email: str, phone: str = None) -> User:
        """创建新用户"""
        user = User(name=name, email=email, phone=phone)
        db.session.add(user)
        db.session.commit()
        db.session.refresh(user)
        return user

    def find_by_id(self, user_id: int) -> Optional[User]:
        """根据 ID 查找用户"""
        return User.query.filter_by(id=user_id).first()

    def find_by_email(self, email: str) -> Optional[User]:
        """根据邮箱查找用户"""
        return User.query.filter_by(email=email).first()

    def find_all(self) -> List[User]:
        """获取所有用户"""
        return User.query.all()

    def find_paginated(self, page: int = 1, per_page: int = 10) -> dict:
        """分页获取用户"""
        pagination = User.query.paginate(page=page, per_page=per_page, error_out=False)
        return {
            'items': [user.to_dict() for user in pagination.items],
            'total': pagination.total,
            'pages': pagination.pages,
            'current_page': pagination.page,
            'per_page': per_page,
            'has_next': pagination.has_next,
            'has_prev': pagination.has_prev
        }

    def update_email(self, user_id: int, email: str) -> Optional[User]:
        """更新用户邮箱"""
        user = self.find_by_id(user_id)
        if user:
            user.email = email
            db.session.commit()
            db.session.refresh(user)
        return user

    def update_phone(self, user_id: int, phone: str) -> Optional[User]:
        """更新用户手机号"""
        user = self.find_by_id(user_id)
        if user:
            user.phone = phone
            db.session.commit()
            db.session.refresh(user)
        return user

    def delete(self, user_id: int) -> bool:
        """根据 ID 删除用户"""
        user = self.find_by_id(user_id)
        if user:
            db.session.delete(user)
            db.session.commit()
            return True
        return False
