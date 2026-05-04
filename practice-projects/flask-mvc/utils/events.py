"""事件驱动支持"""
import threading
import uuid
import logging
from abc import ABC, abstractmethod
from collections import defaultdict
from dataclasses import dataclass, field
from datetime import datetime
from typing import Any, Callable, Dict, List, Type

logger = logging.getLogger(__name__)


@dataclass(frozen=True)
class DomainEvent:
    """领域事件基类"""
    event_id: str = field(default_factory=lambda: str(uuid.uuid4()))
    occurred_at: datetime = field(default_factory=datetime.utcnow)
    
    @property
    def name(self) -> str:
        """事件名"""
        return type(self).__name__


class EventHandler(ABC):
    """事件处理器基类"""
    
    @abstractmethod
    def handle(self, event: DomainEvent) -> None:
        """处理事件"""
        pass


class InMemoryEventBus:
    """内存事件总线"""
    
    def __init__(self):
        self._handlers: Dict[Type[DomainEvent], List[EventHandler]] = defaultdict(list)
        self._lock = threading.Lock()
    
    def subscribe(self, event_type: Type[DomainEvent], handler: EventHandler) -> None:
        """订阅事件"""
        with self._lock:
            self._handlers[event_type].append(handler)
        logger.debug(f"订阅事件 {event_type.__name__}: {handler}")
    
    def publish(self, event: DomainEvent) -> None:
        """发布事件"""
        handlers = list(self._handlers.get(type(event), []))
        if not handlers:
            logger.debug(f"事件 {event.name} 发布，但没有订阅者")
            return
        
        for handler in handlers:
            try:
                handler.handle(event)
            except Exception as e:
                logger.exception(f"事件处理器异常：event={event.name} handler={handler} error={e}")
    
    def unsubscribe(self, event_type: Type[DomainEvent], handler: EventHandler) -> None:
        """取消订阅"""
        with self._lock:
            if handler in self._handlers[event_type]:
                self._handlers[event_type].remove(handler)
                logger.debug(f"取消订阅事件 {event_type.__name__}: {handler}")


# 全局事件总线实例
_event_bus: InMemoryEventBus = None


def get_event_bus() -> InMemoryEventBus:
    """获取事件总线实例"""
    global _event_bus
    if _event_bus is None:
        _event_bus = InMemoryEventBus()
    return _event_bus
