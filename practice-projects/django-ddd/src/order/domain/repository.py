"""Order 仓储抽象。"""
from __future__ import annotations

from abc import ABC, abstractmethod

from order.domain.order import Order


class OrderRepository(ABC):
    @abstractmethod
    def save(self, order: Order) -> Order:  # pragma: no cover
        raise NotImplementedError

    @abstractmethod
    def find_by_id(self, order_id: int) -> Order | None:  # pragma: no cover
        raise NotImplementedError

    @abstractmethod
    def list_all(self, offset: int = 0, limit: int = 20) -> tuple[list[Order], int]:  # pragma: no cover
        raise NotImplementedError

    @abstractmethod
    def list_by_user(self, user_id: int, offset: int = 0, limit: int = 20) -> tuple[list[Order], int]:  # pragma: no cover
        raise NotImplementedError
