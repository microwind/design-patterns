"""内存事件发布器。

默认实现，便于本地和单机运行。可在配置中切换为 Kafka / RocketMQ 等。
"""
from __future__ import annotations

import logging
import threading
from collections import defaultdict
from typing import DefaultDict

from shared.domain.events import DomainEvent
from shared.domain.publisher import EventHandler, EventPublisher

logger = logging.getLogger(__name__)


class InMemoryEventPublisher(EventPublisher):
    """进程内事件发布器，基于事件类型分发。"""

    def __init__(self) -> None:
        self._handlers: DefaultDict[type[DomainEvent], list[EventHandler]] = defaultdict(list)
        self._lock = threading.Lock()

    def subscribe(self, event_type: type[DomainEvent], handler: EventHandler) -> None:
        with self._lock:
            self._handlers[event_type].append(handler)
        logger.debug("订阅事件 %s: %s", event_type.__name__, handler)

    def publish(self, event: DomainEvent) -> None:
        handlers = list(self._handlers.get(type(event), ()))
        if not handlers:
            logger.debug("事件 %s 发布，但没有订阅者", event.name)
            return
        for handler in handlers:
            try:
                handler(event)
            except Exception:  # noqa: BLE001
                logger.exception("事件处理器异常：event=%s handler=%s", event.name, handler)


# -----------------------------------------------------------------------------
# 单例 + 工厂
# -----------------------------------------------------------------------------
_publisher: EventPublisher | None = None


def configure_publisher(kind: str = "memory") -> EventPublisher:
    """根据配置构建发布器，目前支持 memory。"""
    global _publisher
    if _publisher is not None:
        return _publisher

    if kind != "memory":
        logger.warning("未知事件发布器 %s，退回 memory", kind)

    _publisher = InMemoryEventPublisher()
    return _publisher


def get_publisher() -> EventPublisher:
    """业务代码统一通过这里拿发布器。"""
    if _publisher is None:
        return configure_publisher()
    return _publisher
