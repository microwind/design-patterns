"""统一 API 响应。

所有接口返回：{"code": 0, "message": "success", "data": ...}
和 gin-ddd / nestjs-ddd 对齐。
"""
from __future__ import annotations

from typing import Any

from rest_framework import serializers
from rest_framework.response import Response


def api_response(data: Any = None, message: str = "success", code: int = 0, http_status: int = 200) -> Response:
    body: dict[str, Any] = {"code": code, "message": message}
    if data is not None:
        body["data"] = data
    return Response(body, status=http_status)


def api_error(message: str, code: int = 400, http_status: int = 400, data: Any = None) -> Response:
    body: dict[str, Any] = {"code": code, "message": message}
    if data is not None:
        body["data"] = data
    return Response(body, status=http_status)


# ---------------------------------------------------------------------------
# 供 drf-spectacular 生成 OpenAPI 文档用的通用响应 schema
# ---------------------------------------------------------------------------
class ApiResponseSerializer(serializers.Serializer):
    """`{code, message, data}` 统一响应体的 OpenAPI 描述。"""

    code = serializers.IntegerField(help_text="业务码，0 表示成功")
    message = serializers.CharField(help_text="人读信息")
    data = serializers.JSONField(required=False, help_text="业务载荷，结构随接口而定")
