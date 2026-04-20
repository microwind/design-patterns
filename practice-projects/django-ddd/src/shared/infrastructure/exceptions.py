"""自定义领域异常 + DRF 全局异常处理器。"""
from __future__ import annotations

import logging
from typing import Any

from rest_framework import status
from rest_framework.response import Response
from rest_framework.views import exception_handler as drf_default_handler

logger = logging.getLogger(__name__)


# -----------------------------------------------------------------------------
# 领域/应用层可抛出的业务异常
# -----------------------------------------------------------------------------
class DomainError(Exception):
    """领域规则被违反时抛出（如：状态不允许操作）。"""

    http_status = status.HTTP_400_BAD_REQUEST
    code = 400


class NotFoundError(DomainError):
    """聚合未找到。"""

    http_status = status.HTTP_404_NOT_FOUND
    code = 404


class ValidationError(DomainError):
    """入参校验失败。"""

    http_status = status.HTTP_400_BAD_REQUEST
    code = 400


# -----------------------------------------------------------------------------
# 全局异常处理器（DRF 在 settings 中通过 EXCEPTION_HANDLER 指向这里）
# -----------------------------------------------------------------------------
def global_exception_handler(exc: Exception, context: dict[str, Any]):
    # 优先交给 DRF 默认处理器（处理认证/权限/DRF ValidationError 等）
    response = drf_default_handler(exc, context)

    if isinstance(exc, DomainError):
        return Response(
            {"code": exc.code, "message": str(exc) or exc.__class__.__name__},
            status=exc.http_status,
        )

    if response is not None:
        payload: dict[str, Any] = {"code": response.status_code, "message": "request failed"}
        if isinstance(response.data, (dict, list)):
            payload["data"] = response.data
        elif response.data is not None:
            payload["message"] = str(response.data)
        response.data = payload
        return response

    # 未识别的异常：记录日志并返回 500
    logger.exception("未捕获的异常: %s", exc)
    return Response(
        {"code": 500, "message": "internal server error"},
        status=status.HTTP_500_INTERNAL_SERVER_ERROR,
    )
