import os
import sys
import unittest

sys.path.append(os.getcwd())

from src.outbox import MemoryBroker, OutboxService  # noqa: E402


class OutboxPatternTest(unittest.TestCase):
    def test_outbox_pattern(self) -> None:
        service = OutboxService()
        broker = MemoryBroker()

        service.create_order("ORD-1001")
        self.assertEqual(1, len(service.orders))
        self.assertEqual("pending", service.outbox[0].status)

        service.relay_pending(broker)
        self.assertEqual(["EVT-ORD-1001"], broker.published)
        self.assertEqual("published", service.outbox[0].status)

        service.relay_pending(broker)
        self.assertEqual(["EVT-ORD-1001"], broker.published)


if __name__ == "__main__":
    unittest.main()
