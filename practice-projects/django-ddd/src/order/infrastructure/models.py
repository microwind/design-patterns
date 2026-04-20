"""Order 的 Django ORM 模型（PostgreSQL：seed.orders 表）。"""
from __future__ import annotations

from django.db import models


class OrderModel(models.Model):
    order_no = models.CharField(max_length=50, unique=True, db_index=True)
    user_id = models.BigIntegerField(db_index=True)
    total_amount = models.DecimalField(max_digits=12, decimal_places=2, default=0)
    status = models.CharField(max_length=20, db_index=True, default="PENDING")
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        app_label = "order"
        db_table = "orders"
        # 表结构由 docs/init.postgres.sql 管理
        managed = False

    def __str__(self) -> str:  # pragma: no cover
        return f"Order(id={self.pk}, no={self.order_no}, status={self.status})"
