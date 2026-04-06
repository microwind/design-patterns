import os
import sys
import unittest

sys.path.append(os.getcwd())

from src.cdc import Broker, DataStore  # noqa: E402


class CDCPatternTest(unittest.TestCase):
    def test_cdc_pattern(self) -> None:
        store = DataStore()
        broker = Broker()
        store.create_order("ORD-1001")
        self.assertFalse(store.changes[0].processed)
        store.relay_changes(broker)
        self.assertEqual(["CHG-ORD-1001"], broker.published)
        self.assertTrue(store.changes[0].processed)
        store.relay_changes(broker)
        self.assertEqual(["CHG-ORD-1001"], broker.published)


if __name__ == "__main__":
    unittest.main()
