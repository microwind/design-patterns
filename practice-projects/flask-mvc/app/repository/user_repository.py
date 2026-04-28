from typing import List, Optional
from app.models.user import User
from app.models import db


class UserRepository:
    """User repository for data access operations"""

    def create(self, name: str, email: str, phone: str = None) -> User:
        """Create a new user"""
        user = User(name=name, email=email, phone=phone)
        db.session.add(user)
        db.session.commit()
        db.session.refresh(user)
        return user

    def find_by_id(self, user_id: int) -> Optional[User]:
        """Find user by ID"""
        return User.query.filter_by(id=user_id).first()

    def find_by_email(self, email: str) -> Optional[User]:
        """Find user by email"""
        return User.query.filter_by(email=email).first()

    def find_all(self) -> List[User]:
        """Get all users"""
        return User.query.all()

    def update_email(self, user_id: int, email: str) -> Optional[User]:
        """Update user email"""
        user = self.find_by_id(user_id)
        if user:
            user.email = email
            db.session.commit()
            db.session.refresh(user)
        return user

    def update_phone(self, user_id: int, phone: str) -> Optional[User]:
        """Update user phone"""
        user = self.find_by_id(user_id)
        if user:
            user.phone = phone
            db.session.commit()
            db.session.refresh(user)
        return user

    def delete(self, user_id: int) -> bool:
        """Delete user by ID"""
        user = self.find_by_id(user_id)
        if user:
            db.session.delete(user)
            db.session.commit()
            return True
        return False
