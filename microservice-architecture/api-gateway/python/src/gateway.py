"""
gateway.py - API 网关模式（API Gateway Pattern）的 Python 实现

API 网关是微服务架构的统一入口，负责请求路由、中间件处理和跨切面关注点。

【设计模式】
  - 外观模式（Facade Pattern）：网关为客户端提供统一入口，屏蔽后端服务拆分细节。
  - 责任链模式（Chain of Responsibility）：中间件按注册顺序依次执行，
    任一中间件可拦截请求并直接返回响应。
  - 策略模式（Strategy Pattern）：Handler 和 Middleware 作为 Callable，
    不同的处理器和中间件是可插拔的策略。

【架构思想】
  API 网关集中处理认证、限流、日志、链路追踪等跨切面关注点。

【开源对比】
  - FastAPI / Flask：Python Web 框架，可配合中间件实现网关功能
  - Kong / APISIX：生产级 API 网关
  本示例省略了异步 I/O、动态路由等工程细节，聚焦于路由匹配和中间件链。
"""

from dataclasses import dataclass, field
from typing import Callable, Dict, List, Optional


@dataclass
class Request:
    method: str
    path: str
    headers: Dict[str, str]


@dataclass
class Response:
    status_code: int
    body: str
    headers: Dict[str, str] = field(default_factory=dict)


Handler = Callable[[Request], Response]
Middleware = Callable[[Request], Optional[Response]]


class APIGateway:
    def __init__(self) -> None:
        self._routes: Dict[str, Handler] = {}
        self._middlewares: List[Middleware] = []

    def use(self, middleware: Middleware) -> None:
        self._middlewares.append(middleware)

    def register(self, prefix: str, handler: Handler) -> None:
        self._routes[prefix] = handler

    def handle(self, request: Request) -> Response:
        for middleware in self._middlewares:
            response = middleware(request)
            if response is not None:
                return response

        handler = self._match(request.path)
        if handler is None:
            return Response(status_code=404, body="gateway: route not found")

        response = handler(request)
        response.headers.setdefault(
            "X-Correlation-ID",
            request.headers.get("X-Correlation-ID", "gw-generated-correlation-id"),
        )
        return response

    def _match(self, path: str) -> Optional[Handler]:
        matched_prefix = ""
        matched_handler: Optional[Handler] = None
        for prefix, handler in self._routes.items():
            if path.startswith(prefix) and len(prefix) > len(matched_prefix):
                matched_prefix = prefix
                matched_handler = handler
        return matched_handler


def require_user_header(prefix: str, header_name: str) -> Middleware:
    def middleware(request: Request) -> Optional[Response]:
        if not request.path.startswith(prefix):
            return None
        if not request.headers.get(header_name):
            return Response(status_code=401, body="gateway: unauthorized")
        return None

    return middleware


def order_service_handler() -> Handler:
    def handler(request: Request) -> Response:
        return Response(
            status_code=200,
            body=f"order-service handled {request.path}",
            headers={"X-Upstream-Service": "order-service"},
        )

    return handler


def inventory_service_handler() -> Handler:
    def handler(request: Request) -> Response:
        return Response(
            status_code=200,
            body=f"inventory-service handled {request.path}",
            headers={"X-Upstream-Service": "inventory-service"},
        )

    return handler
