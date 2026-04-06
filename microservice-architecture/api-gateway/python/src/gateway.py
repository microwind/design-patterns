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
