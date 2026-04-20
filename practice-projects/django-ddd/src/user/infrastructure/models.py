"""User 的 Django ORM 模型。

字段与 gin-ddd/nestjs-ddd 的 users 表严格对齐：
  id / name / email / phone / address / created_time / updated_time
"""
from __future__ import annotations

from django.db import models


class UserModel(models.Model):
    name = models.CharField(max_length=50, unique=True, db_index=True)
    email = models.CharField(max_length=100, unique=True, db_index=True)
    phone = models.CharField(max_length=20, null=True, blank=True)
    address = models.CharField(max_length=255, null=True, blank=True)
    created_time = models.DateTimeField(auto_now_add=True)
    updated_time = models.DateTimeField(auto_now=True)

    class Meta:
        app_label = "user"
        db_table = "users"
        # 表结构由 docs/init.mysql.sql 管理，和 gin-ddd / nestjs-ddd 保持一致
        managed = False

    def __str__(self) -> str:  # pragma: no cover
        return f"User(id={self.pk}, name={self.name})"
