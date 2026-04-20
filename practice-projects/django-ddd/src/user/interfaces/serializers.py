"""请求/响应序列化。"""
from __future__ import annotations

from rest_framework import serializers


class CreateUserRequest(serializers.Serializer):
    name = serializers.CharField(max_length=50)
    email = serializers.EmailField(max_length=100)
    phone = serializers.CharField(max_length=20, required=False, allow_blank=True, allow_null=True)
    address = serializers.CharField(max_length=255, required=False, allow_blank=True, allow_null=True)


class UpdateEmailRequest(serializers.Serializer):
    email = serializers.EmailField(max_length=100)


class UpdatePhoneRequest(serializers.Serializer):
    phone = serializers.CharField(max_length=20, required=False, allow_blank=True, allow_null=True)
