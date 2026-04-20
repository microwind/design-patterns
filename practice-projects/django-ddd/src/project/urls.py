"""根 URL 配置。

- /               → 健康欢迎页
- /health         → 健康检查
- /api/users/**   → 用户
- /api/orders/**  → 订单
- /api/docs       → Swagger UI
- /api/schema     → OpenAPI schema
"""
from __future__ import annotations

from django.urls import include, path
from drf_spectacular.views import SpectacularAPIView, SpectacularSwaggerView

from order.interfaces.views import UserOrdersView
from shared.infrastructure.response import api_response


def welcome(_request):
    return api_response(
        {
            "status": "ok",
            "service": "django-ddd",
            "message": "Welcome to Django DDD scaffold! 欢迎来到 Django DDD 工程脚手架！",
        }
    )


def health(_request):
    return api_response({"status": "ok", "service": "django-ddd"})


urlpatterns = [
    path("", welcome, name="welcome"),
    path("health", health, name="health"),

    path("api/", include("user.interfaces.urls")),
    path("api/", include("order.interfaces.urls")),

    # 跨上下文组合视图：url 归属 user，数据来自 order
    path("api/users/<int:user_id>/orders", UserOrdersView.as_view(), name="user-orders"),

    path("api/schema", SpectacularAPIView.as_view(), name="schema"),
    path("api/docs", SpectacularSwaggerView.as_view(url_name="schema"), name="swagger"),
]
