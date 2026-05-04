from typing import List, Optional
from sqlalchemy.exc import SQLAlchemyError
from utils.extensions import db
from models.user import User


class UserRepository:
    """用户仓储类"""
    
    def create(self, user: User) -> User:
        """创建用户"""
        try:
            db.session.add(user)
            db.session.commit()
            db.session.refresh(user)
            return user
        except SQLAlchemyError as e:
            db.session.rollback()
            raise Exception(f"创建用户失败: {str(e)}")
    
    def get_by_id(self, user_id: int) -> Optional[User]:
        """根据ID获取用户"""
        try:
            return User.query.filter_by(id=user_id).first()
        except SQLAlchemyError as e:
            raise Exception(f"获取用户失败: {str(e)}")
    
    def get_by_email(self, email: str) -> Optional[User]:
        """根据邮箱获取用户"""
        try:
            return User.query.filter_by(email=email).first()
        except SQLAlchemyError as e:
            raise Exception(f"获取用户失败: {str(e)}")
    
    def get_all(self) -> List[User]:
        """获取所有用户"""
        try:
            return User.query.all()
        except SQLAlchemyError as e:
            raise Exception(f"获取用户列表失败: {str(e)}")
    
    def update(self, user: User) -> User:
        """更新用户"""
        try:
            db.session.merge(user)
            db.session.commit()
            db.session.refresh(user)
            return user
        except SQLAlchemyError as e:
            db.session.rollback()
            raise Exception(f"更新用户失败: {str(e)}")
    
    def delete(self, user_id: int) -> bool:
        """删除用户"""
        try:
            user = User.query.filter_by(id=user_id).first()
            if user:
                db.session.delete(user)
                db.session.commit()
                return True
            return False
        except SQLAlchemyError as e:
            db.session.rollback()
            raise Exception(f"删除用户失败: {str(e)}")
    
    def search_by_name(self, name: str) -> List[User]:
        """根据姓名搜索用户"""
        try:
            return User.query.filter(User.name.like(f'%{name}%')).all()
        except SQLAlchemyError as e:
            raise Exception(f"搜索用户失败: {str(e)}")
    
    def count(self) -> int:
        """统计用户总数"""
        try:
            return User.query.count()
        except SQLAlchemyError as e:
            raise Exception(f"统计用户数量失败: {str(e)}")
