"""User 聚合根（纯 Python）。

与 Django ORM 完全解耦：领域逻辑不依赖任何持久化细节。
"""
from __future__ import annotations

from dataclasses import dataclass, field
from datetime import datetime

from shared.infrastructure.exceptions import ValidationError


@dataclass
class User:
    """用户聚合根。"""

    name: str
    email: str
    phone: str | None = None
    address: str | None = None
    id: int | None = None
    created_time: datetime = field(default_factory=datetime.utcnow)
    updated_time: datetime = field(default_factory=datetime.utcnow)

    # ---- 工厂 -----------------------------------------------------------
    @classmethod
    def create(
        cls,
        name: str,
        email: str,
        phone: str | None = None,
        address: str | None = None,
    ) -> "User":
        cls._ensure_name(name)
        cls._ensure_email(email)
        now = datetime.utcnow()
        return cls(
            name=name.strip(),
            email=email.strip().lower(),
            phone=phone.strip() if phone else None,
            address=address.strip() if address else None,
            created_time=now,
            updated_time=now,
        )

    # ---- 行为 -----------------------------------------------------------
    def update_email(self, email: str) -> None:
        self._ensure_email(email)
        self.email = email.strip().lower()
        self.updated_time = datetime.utcnow()

    def update_phone(self, phone: str | None) -> None:
        self.phone = phone.strip() if phone else None
        self.updated_time = datetime.utcnow()

    # ---- 校验 -----------------------------------------------------------
    @staticmethod
    def _ensure_name(name: str) -> None:
        if not name or not name.strip():
            raise ValidationError("用户名不能为空")

    @staticmethod
    def _ensure_email(email: str) -> None:
        if not email or "@" not in email:
            raise ValidationError("邮箱格式非法")
