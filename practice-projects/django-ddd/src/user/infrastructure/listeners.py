"""用户上下文的事件监听器。

默认仅打印日志，示意如何在 BC 内部自行订阅。
生产可在这里接入通知服务 / 审计日志 / 下游同步等。
"""
from __future__ import annotations

import logging

from shared.domain.publisher import EventPublisher
from user.domain.events import UserCreatedEvent, UserEmailUpdatedEvent

logger = logging.getLogger("ddd.events.user")


def register_listeners(publisher: EventPublisher) -> None:
    publisher.subscribe(UserCreatedEvent, lambda e: logger.info("用户创建: %s", e))
    publisher.subscribe(UserEmailUpdatedEvent, lambda e: logger.info("用户邮箱更新: %s", e))
