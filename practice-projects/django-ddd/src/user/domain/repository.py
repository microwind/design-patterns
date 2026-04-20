"""User 仓储抽象。"""
from __future__ import annotations

from abc import ABC, abstractmethod

from user.domain.user import User


class UserRepository(ABC):
    """抽象仓储：领域层依赖，infrastructure 实现。"""

    @abstractmethod
    def save(self, user: User) -> User:  # pragma: no cover
        raise NotImplementedError

    @abstractmethod
    def find_by_id(self, user_id: int) -> User | None:  # pragma: no cover
        raise NotImplementedError

    @abstractmethod
    def find_by_email(self, email: str) -> User | None:  # pragma: no cover
        raise NotImplementedError

    @abstractmethod
    def list_all(self, offset: int = 0, limit: int = 20) -> tuple[list[User], int]:  # pragma: no cover
        raise NotImplementedError

    @abstractmethod
    def delete(self, user_id: int) -> None:  # pragma: no cover
        raise NotImplementedError
