import os
import sys
import unittest

sys.path.append(os.getcwd())

from src.configuration_center import ConfigCenter, ConfigClient, ServiceConfig  # noqa: E402


class ConfigurationCenterTest(unittest.TestCase):
    def test_load_and_refresh(self) -> None:
        center = ConfigCenter()
        center.put(ServiceConfig("order-service", "prod", 1, "db.prod.internal", 300, False))

        client = ConfigClient(center, "order-service", "prod")
        loaded = client.load()
        self.assertIsNotNone(loaded)
        self.assertEqual(1, loaded.version)
        self.assertEqual(300, loaded.timeout_ms)

        center.put(ServiceConfig("order-service", "prod", 2, "db.prod.internal", 500, True))
        refreshed = client.refresh()
        self.assertIsNotNone(refreshed)
        self.assertEqual(2, refreshed.version)
        self.assertTrue(refreshed.feature_order_audit)


if __name__ == "__main__":
    unittest.main()
