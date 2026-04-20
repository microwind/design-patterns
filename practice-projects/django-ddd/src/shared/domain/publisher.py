"""事件发布器抽象。"""
from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Callable

from shared.domain.events import DomainEvent

EventHandler = Callable[[DomainEvent], None]


class EventPublisher(ABC):
    """发布领域事件的抽象接口。

    具体实现由 infrastructure 层提供（内存 / Kafka / RocketMQ ...）。
    """

    @abstractmethod
    def publish(self, event: DomainEvent) -> None:  # pragma: no cover
        raise NotImplementedError

    @abstractmethod
    def subscribe(self, event_type: type[DomainEvent], handler: EventHandler) -> None:  # pragma: no cover
        raise NotImplementedError
