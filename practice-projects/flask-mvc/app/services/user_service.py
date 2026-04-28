from typing import List, Optional, Dict
from app.models.user import User
from app.models.event import UserCreatedEvent
from app.repository.user_repository import UserRepository


class UserService:
    """用户服务，处理业务逻辑"""

    def __init__(self, user_repository: UserRepository):
        self.user_repository = user_repository
        self.events = []

    def create_user(self, name: str, email: str, phone: str = None) -> Dict:
        """创建新用户"""
        # 检查邮箱是否已存在
        existing_user = self.user_repository.find_by_email(email)
        if existing_user:
            raise ValueError(f"User with email {email} already exists")

        user = self.user_repository.create(name, email, phone)
        
        # 记录领域事件
        event = UserCreatedEvent(user.id, user.name, user.email)
        self.events.append(event)
        
        return user.to_dict()

    def get_user(self, user_id: int) -> Optional[Dict]:
        """根据 ID 获取用户"""
        user = self.user_repository.find_by_id(user_id)
        return user.to_dict() if user else None

    def get_all_users(self) -> List[Dict]:
        """获取所有用户"""
        users = self.user_repository.find_all()
        return [user.to_dict() for user in users]

    def get_users_paginated(self, page: int = 1, per_page: int = 10) -> Dict:
        """分页获取用户"""
        return self.user_repository.find_paginated(page, per_page)

    def update_user_email(self, user_id: int, email: str) -> Optional[Dict]:
        """更新用户邮箱"""
        # 检查邮箱是否已存在
        existing_user = self.user_repository.find_by_email(email)
        if existing_user and existing_user.id != user_id:
            raise ValueError(f"Email {email} already in use by another user")

        user = self.user_repository.update_email(user_id, email)
        return user.to_dict() if user else None

    def update_user_phone(self, user_id: int, phone: str) -> Optional[Dict]:
        """更新用户手机号"""
        user = self.user_repository.update_phone(user_id, phone)
        return user.to_dict() if user else None

    def delete_user(self, user_id: int) -> bool:
        """根据 ID 删除用户"""
        return self.user_repository.delete(user_id)

    def get_events(self):
        """获取已记录的事件"""
        return self.events

    def clear_events(self):
        """清除已记录的事件"""
        self.events.clear()
