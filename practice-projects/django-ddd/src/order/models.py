"""Django 期望的 <app>/models.py，re-export 供 Django 自动发现。"""
from __future__ import annotations

from order.infrastructure.models import OrderModel

__all__ = ["OrderModel"]
