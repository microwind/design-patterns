from typing import List, Optional
from repositories.user_repository import UserRepository
from models.user import User
from utils.domain_events import UserCreatedEvent
from utils.events import get_event_bus


class UserService:
    """用户服务类"""
    
    def __init__(self, user_repository: UserRepository = None):
        self.user_repository = user_repository or UserRepository()
        self.event_bus = get_event_bus()
    
    def get_user_by_id(self, user_id: int) -> Optional[User]:
        """根据ID获取用户"""
        return self.user_repository.get_by_id(user_id)
    
    def get_user_by_email(self, email: str) -> Optional[User]:
        """根据邮箱获取用户"""
        return self.user_repository.get_by_email(email)
    
    def get_all_users(self) -> List[User]:
        """获取所有用户"""
        return self.user_repository.get_all()
    
    def create_user(self, user_data: dict) -> User:
        """创建用户"""
        # 检查邮箱是否已存在
        existing_user = self.user_repository.get_by_email(user_data['email'])
        if existing_user:
            raise Exception("邮箱已存在")
        
        # 创建用户实体
        user = User(
            name=user_data['name'],
            email=user_data['email'],
            phone=user_data.get('phone')
        )
        
        # 保存用户
        saved_user = self.user_repository.create(user)
        
        # 发布用户创建事件
        event = UserCreatedEvent(
            user_id=saved_user.id,
            name=saved_user.name,
            email=saved_user.email
        )
        self.event_bus.publish(event)
        
        return saved_user
    
    def update_user(self, user_id: int, user_data: dict) -> User:
        """更新用户"""
        user = self.user_repository.get_by_id(user_id)
        if not user:
            raise Exception("用户不存在")
        
        # 更新字段
        if 'name' in user_data:
            user.name = user_data['name']
        if 'phone' in user_data:
            user.phone = user_data['phone']
        
        # 如果更新邮箱，需要检查唯一性
        if 'email' in user_data and user_data['email'] != user.email:
            existing_user = self.user_repository.get_by_email(user_data['email'])
            if existing_user:
                raise Exception("邮箱已存在")
            user.email = user_data['email']
        
        return self.user_repository.update(user)
    
    def delete_user(self, user_id: int) -> bool:
        """删除用户"""
        return self.user_repository.delete(user_id)
    
    def search_users(self, name: str) -> List[User]:
        """搜索用户"""
        return self.user_repository.search_by_name(name)
    
    def get_user_count(self) -> int:
        """获取用户总数"""
        return self.user_repository.count()
