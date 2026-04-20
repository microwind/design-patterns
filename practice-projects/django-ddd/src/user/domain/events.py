"""用户相关领域事件。"""
from __future__ import annotations

from dataclasses import dataclass

from shared.domain.events import DomainEvent


@dataclass(frozen=True)
class UserCreatedEvent(DomainEvent):
    user_id: int = 0
    name: str = ""
    email: str = ""


@dataclass(frozen=True)
class UserEmailUpdatedEvent(DomainEvent):
    user_id: int = 0
    old_email: str = ""
    new_email: str = ""
