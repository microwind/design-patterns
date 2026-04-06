import os
import sys
import unittest

sys.path.append(os.getcwd())

from src.tracing import child_span, gateway_entry  # noqa: E402


class DistributedTracingTest(unittest.TestCase):
    def test_trace_propagation(self) -> None:
        gateway = gateway_entry("TRACE-1001")
        order = child_span(gateway, "order-service", "SPAN-ORDER")
        inventory = child_span(order, "inventory-service", "SPAN-INVENTORY")

        self.assertEqual(gateway.trace_id, order.trace_id)
        self.assertEqual(order.trace_id, inventory.trace_id)
        self.assertEqual(gateway.span_id, order.parent_span_id)
        self.assertEqual(order.span_id, inventory.parent_span_id)


if __name__ == "__main__":
    unittest.main()
