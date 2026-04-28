from typing import List, Optional, Dict
from app.models.user import User
from app.models.event import UserCreatedEvent
from app.repository.user_repository import UserRepository


class UserService:
    """User service for business logic"""

    def __init__(self, user_repository: UserRepository):
        self.user_repository = user_repository
        self.events = []

    def create_user(self, name: str, email: str, phone: str = None) -> Dict:
        """Create a new user"""
        # Check if email already exists
        existing_user = self.user_repository.find_by_email(email)
        if existing_user:
            raise ValueError(f"User with email {email} already exists")

        user = self.user_repository.create(name, email, phone)
        
        # Record domain event
        event = UserCreatedEvent(user.id, user.name, user.email)
        self.events.append(event)
        
        return user.to_dict()

    def get_user(self, user_id: int) -> Optional[Dict]:
        """Get user by ID"""
        user = self.user_repository.find_by_id(user_id)
        return user.to_dict() if user else None

    def get_all_users(self) -> List[Dict]:
        """Get all users"""
        users = self.user_repository.find_all()
        return [user.to_dict() for user in users]

    def update_user_email(self, user_id: int, email: str) -> Optional[Dict]:
        """Update user email"""
        # Check if email already exists
        existing_user = self.user_repository.find_by_email(email)
        if existing_user and existing_user.id != user_id:
            raise ValueError(f"Email {email} already in use by another user")

        user = self.user_repository.update_email(user_id, email)
        return user.to_dict() if user else None

    def update_user_phone(self, user_id: int, phone: str) -> Optional[Dict]:
        """Update user phone"""
        user = self.user_repository.update_phone(user_id, phone)
        return user.to_dict() if user else None

    def delete_user(self, user_id: int) -> bool:
        """Delete user by ID"""
        return self.user_repository.delete(user_id)

    def get_events(self):
        """Get recorded events"""
        return self.events

    def clear_events(self):
        """Clear recorded events"""
        self.events.clear()
