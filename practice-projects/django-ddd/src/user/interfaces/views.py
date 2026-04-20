"""用户 HTTP 入口（DRF APIView）。"""
from __future__ import annotations

from drf_spectacular.utils import extend_schema
from rest_framework.request import Request
from rest_framework.views import APIView

from shared.infrastructure.response import ApiResponseSerializer, api_response
from user.application.dto import (
    CreateUserCommand,
    UpdateEmailCommand,
    UpdatePhoneCommand,
)
from user.application.service import UserApplicationService
from user.infrastructure.repository import DjangoUserRepository
from user.interfaces.serializers import (
    CreateUserRequest,
    UpdateEmailRequest,
    UpdatePhoneRequest,
)


def _service() -> UserApplicationService:
    """组装服务实例（简易依赖装配）。"""
    return UserApplicationService(repository=DjangoUserRepository())


class UserListView(APIView):
    @extend_schema(
        operation_id="user_create",
        request=CreateUserRequest,
        responses=ApiResponseSerializer,
    )
    def post(self, request: Request):
        req = CreateUserRequest(data=request.data)
        req.is_valid(raise_exception=True)
        dto = _service().create_user(CreateUserCommand(**req.validated_data))
        return api_response(dto.to_dict(), message="创建成功", http_status=201)

    @extend_schema(operation_id="user_list", responses=ApiResponseSerializer)
    def get(self, request: Request):
        try:
            offset = max(int(request.query_params.get("offset", 0)), 0)
            limit = min(max(int(request.query_params.get("limit", 20)), 1), 100)
        except ValueError:
            offset, limit = 0, 20
        items, total = _service().list_users(offset=offset, limit=limit)
        return api_response(
            {
                "total": total,
                "offset": offset,
                "limit": limit,
                "items": [d.to_dict() for d in items],
            }
        )


class UserDetailView(APIView):
    @extend_schema(operation_id="user_retrieve", responses=ApiResponseSerializer)
    def get(self, _request: Request, user_id: int):
        dto = _service().get_user(user_id)
        return api_response(dto.to_dict())

    @extend_schema(operation_id="user_delete", responses=ApiResponseSerializer)
    def delete(self, _request: Request, user_id: int):
        _service().delete_user(user_id)
        return api_response(message="删除成功")


class UserEmailView(APIView):
    @extend_schema(
        operation_id="user_update_email",
        request=UpdateEmailRequest,
        responses=ApiResponseSerializer,
    )
    def put(self, request: Request, user_id: int):
        req = UpdateEmailRequest(data=request.data)
        req.is_valid(raise_exception=True)
        dto = _service().update_email(
            UpdateEmailCommand(user_id=user_id, email=req.validated_data["email"])
        )
        return api_response(dto.to_dict(), message="邮箱已更新")


class UserPhoneView(APIView):
    @extend_schema(
        operation_id="user_update_phone",
        request=UpdatePhoneRequest,
        responses=ApiResponseSerializer,
    )
    def put(self, request: Request, user_id: int):
        req = UpdatePhoneRequest(data=request.data)
        req.is_valid(raise_exception=True)
        dto = _service().update_phone(
            UpdatePhoneCommand(user_id=user_id, phone=req.validated_data.get("phone"))
        )
        return api_response(dto.to_dict(), message="手机号已更新")
