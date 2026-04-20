"""领域事件基类。"""
from __future__ import annotations

import uuid
from dataclasses import dataclass, field
from datetime import datetime


@dataclass(frozen=True)
class DomainEvent:
    """所有领域事件的基类。

    子类通过 dataclass 声明自己的业务载荷字段。
    """

    event_id: str = field(default_factory=lambda: str(uuid.uuid4()))
    occurred_at: datetime = field(default_factory=datetime.utcnow)

    @property
    def name(self) -> str:
        """事件名（默认使用子类类名）。"""
        return type(self).__name__
