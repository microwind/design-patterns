"""订单应用层 DTO。"""
from __future__ import annotations

from dataclasses import asdict, dataclass
from datetime import datetime
from decimal import Decimal
from typing import Any


@dataclass
class CreateOrderCommand:
    user_id: int
    total_amount: Decimal
    order_no: str | None = None  # 不传则由服务生成


@dataclass
class OrderDTO:
    id: int
    order_no: str
    user_id: int
    total_amount: Decimal
    status: str
    created_at: datetime
    updated_at: datetime

    def to_dict(self) -> dict[str, Any]:
        d = asdict(self)
        d["total_amount"] = str(self.total_amount)
        d["created_at"] = self.created_at.isoformat()
        d["updated_at"] = self.updated_at.isoformat()
        return d
