import os
import sys
import unittest

sys.path.append(os.getcwd())

from src.router import (  # noqa: E402
    Request,
    VersionedRouter,
    product_handler_v1,
    product_handler_v2,
)


class ApiVersioningTest(unittest.TestCase):
    def setUp(self) -> None:
        self.router = VersionedRouter("v1")
        self.router.register("v1", product_handler_v1)
        self.router.register("v2", product_handler_v2)

    def test_default_version_falls_back_to_v1(self) -> None:
        response = self.router.handle(Request("/products/P100", {}))
        self.assertEqual("v1", response.version)
        self.assertIn('"name":"Mechanical Keyboard"', response.body)

    def test_header_can_select_v2(self) -> None:
        response = self.router.handle(Request("/products/P100", {"X-API-Version": "2"}))
        self.assertEqual("v2", response.version)
        self.assertIn('"inventoryStatus":"IN_STOCK"', response.body)

    def test_unsupported_version_returns_bad_request(self) -> None:
        response = self.router.handle(Request("/products/P100", {"X-API-Version": "9"}))
        self.assertEqual(400, response.status_code)


if __name__ == "__main__":
    unittest.main()
