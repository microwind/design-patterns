import os
import sys
import unittest

sys.path.append(os.getcwd())

from src.gateway import (  # noqa: E402
    APIGateway,
    Request,
    inventory_service_handler,
    order_service_handler,
    require_user_header,
)


class ApiGatewayTest(unittest.TestCase):
    def test_gateway_routes_and_preserves_correlation_id(self) -> None:
        gateway = APIGateway()
        gateway.use(require_user_header("/api/orders", "X-User"))
        gateway.register("/api/orders", order_service_handler())
        gateway.register("/api/inventory", inventory_service_handler())

        response = gateway.handle(
            Request(
                method="GET",
                path="/api/orders/ORD-1001",
                headers={"X-User": "jarry", "X-Correlation-ID": "trace-1001"},
            )
        )

        self.assertEqual(200, response.status_code)
        self.assertEqual("trace-1001", response.headers["X-Correlation-ID"])
        self.assertEqual("order-service", response.headers["X-Upstream-Service"])

    def test_gateway_rejects_unauthorized_order_request(self) -> None:
        gateway = APIGateway()
        gateway.use(require_user_header("/api/orders", "X-User"))
        gateway.register("/api/orders", order_service_handler())

        response = gateway.handle(Request(method="GET", path="/api/orders/ORD-1001", headers={}))

        self.assertEqual(401, response.status_code)

    def test_gateway_allows_public_inventory_route(self) -> None:
        gateway = APIGateway()
        gateway.use(require_user_header("/api/orders", "X-User"))
        gateway.register("/api/inventory", inventory_service_handler())

        response = gateway.handle(Request(method="GET", path="/api/inventory/SKU-BOOK", headers={}))

        self.assertEqual(200, response.status_code)
        self.assertEqual("inventory-service", response.headers["X-Upstream-Service"])

    def test_gateway_returns_not_found(self) -> None:
        gateway = APIGateway()
        response = gateway.handle(Request(method="GET", path="/api/unknown", headers={}))
        self.assertEqual(404, response.status_code)


if __name__ == "__main__":
    unittest.main()
