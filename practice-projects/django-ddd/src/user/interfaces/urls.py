"""用户上下文 URL（被 project.urls 以 /api/ 前缀挂载）。

只包含用户聚合自身路由；`/users/<id>/orders` 这类跨上下文视图
由组装层 `project/urls.py` 负责装配，避免 user 反向依赖 order。
"""
from __future__ import annotations

from django.urls import path

from user.interfaces.views import (
    UserDetailView,
    UserEmailView,
    UserListView,
    UserPhoneView,
)

urlpatterns = [
    path("users", UserListView.as_view(), name="users"),
    path("users/<int:user_id>", UserDetailView.as_view(), name="user-detail"),
    path("users/<int:user_id>/email", UserEmailView.as_view(), name="user-email"),
    path("users/<int:user_id>/phone", UserPhoneView.as_view(), name="user-phone"),
]
