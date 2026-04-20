"""Django 期望在 <app>/models.py 里能找到模型，这里统一 re-export。
业务代码请直接从 user.infrastructure.models 导入。
"""
from __future__ import annotations

from user.infrastructure.models import UserModel

__all__ = ["UserModel"]
