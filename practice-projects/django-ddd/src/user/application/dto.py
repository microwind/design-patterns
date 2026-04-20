"""应用层 DTO。

dto 只用于跨层传输，不带业务行为。
"""
from __future__ import annotations

from dataclasses import asdict, dataclass
from datetime import datetime
from typing import Any


@dataclass
class CreateUserCommand:
    name: str
    email: str
    phone: str | None = None
    address: str | None = None


@dataclass
class UpdateEmailCommand:
    user_id: int
    email: str


@dataclass
class UpdatePhoneCommand:
    user_id: int
    phone: str | None


@dataclass
class UserDTO:
    id: int
    name: str
    email: str
    phone: str | None
    address: str | None
    created_time: datetime
    updated_time: datetime

    def to_dict(self) -> dict[str, Any]:
        d = asdict(self)
        d["created_time"] = self.created_time.isoformat()
        d["updated_time"] = self.updated_time.isoformat()
        return d
