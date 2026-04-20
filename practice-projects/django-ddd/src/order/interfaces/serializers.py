"""订单请求序列化。"""
from __future__ import annotations

from decimal import Decimal

from rest_framework import serializers


class CreateOrderRequest(serializers.Serializer):
    user_id = serializers.IntegerField(min_value=1)
    total_amount = serializers.DecimalField(
        max_digits=12,
        decimal_places=2,
        min_value=Decimal("0.01"),
    )
    order_no = serializers.CharField(max_length=50, required=False, allow_blank=True)
