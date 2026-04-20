"""订单 HTTP 入口。"""
from __future__ import annotations

from drf_spectacular.utils import extend_schema, extend_schema_view
from rest_framework.request import Request
from rest_framework.views import APIView

from shared.infrastructure.response import ApiResponseSerializer, api_response
from order.application.dto import CreateOrderCommand
from order.application.service import OrderApplicationService
from order.infrastructure.repository import DjangoOrderRepository
from order.interfaces.serializers import CreateOrderRequest


def _service() -> OrderApplicationService:
    return OrderApplicationService(repository=DjangoOrderRepository())


def _parse_pagination(request: Request) -> tuple[int, int]:
    try:
        offset = max(int(request.query_params.get("offset", 0)), 0)
        limit = min(max(int(request.query_params.get("limit", 20)), 1), 100)
    except ValueError:
        offset, limit = 0, 20
    return offset, limit


class OrderListView(APIView):
    @extend_schema(
        operation_id="order_create",
        request=CreateOrderRequest,
        responses=ApiResponseSerializer,
    )
    def post(self, request: Request):
        req = CreateOrderRequest(data=request.data)
        req.is_valid(raise_exception=True)
        data = req.validated_data
        dto = _service().create_order(
            CreateOrderCommand(
                user_id=data["user_id"],
                total_amount=data["total_amount"],
                order_no=data.get("order_no") or None,
            )
        )
        return api_response(dto.to_dict(), message="创建成功", http_status=201)

    @extend_schema(operation_id="order_list", responses=ApiResponseSerializer)
    def get(self, request: Request):
        offset, limit = _parse_pagination(request)
        items, total = _service().list_orders(offset=offset, limit=limit)
        return api_response(
            {
                "total": total,
                "offset": offset,
                "limit": limit,
                "items": [d.to_dict() for d in items],
            }
        )


class OrderDetailView(APIView):
    @extend_schema(operation_id="order_retrieve", responses=ApiResponseSerializer)
    def get(self, _request: Request, order_id: int):
        dto = _service().get_order(order_id)
        return api_response(dto.to_dict())


class _TransitionView(APIView):
    action: str = ""

    @extend_schema(request=None, responses=ApiResponseSerializer)
    def put(self, _request: Request, order_id: int):
        dto = getattr(_service(), self.action)(order_id)
        return api_response(dto.to_dict(), message=f"{self.action} ok")


def _transition_schema(op_id: str):
    return extend_schema_view(
        put=extend_schema(
            operation_id=op_id,
            request=None,
            responses=ApiResponseSerializer,
        )
    )


@_transition_schema("order_pay")
class OrderPayView(_TransitionView):
    action = "pay"


@_transition_schema("order_ship")
class OrderShipView(_TransitionView):
    action = "ship"


@_transition_schema("order_deliver")
class OrderDeliverView(_TransitionView):
    action = "deliver"


@_transition_schema("order_cancel")
class OrderCancelView(_TransitionView):
    action = "cancel"


@_transition_schema("order_refund")
class OrderRefundView(_TransitionView):
    action = "refund"


class UserOrdersView(APIView):
    """GET /api/users/<user_id>/orders —— 属于用户视角但落在订单上下文。"""

    @extend_schema(operation_id="user_orders_list", responses=ApiResponseSerializer)
    def get(self, request: Request, user_id: int):
        offset, limit = _parse_pagination(request)
        items, total = _service().list_user_orders(user_id=user_id, offset=offset, limit=limit)
        return api_response(
            {
                "total": total,
                "offset": offset,
                "limit": limit,
                "items": [d.to_dict() for d in items],
            }
        )
