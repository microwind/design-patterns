import os
import sys
import unittest

sys.path.append(os.getcwd())

from src.communication import (  # noqa: E402
    AsyncOrderService,
    EventBus,
    InventoryService,
    OrderStore,
    PaymentService,
    SynchronousOrderService,
    register_async_workflow,
)


class ServiceCommunicationTest(unittest.TestCase):
    def test_synchronous_workflow(self) -> None:
        inventory = InventoryService({"SKU-BOOK": 5})
        payment = PaymentService()
        service = SynchronousOrderService(inventory, payment)

        order = service.place_order("ORD-1001", "SKU-BOOK", 2)
        self.assertEqual("CREATED", order.status)

    def test_asynchronous_workflow(self) -> None:
        inventory = InventoryService({"SKU-BOOK": 5})
        payment = PaymentService()
        bus = EventBus()
        store = OrderStore()
        register_async_workflow(bus, store, inventory, payment)

        service = AsyncOrderService(bus, store)
        initial = service.place_order("ORD-2001", "SKU-BOOK", 2)
        self.assertEqual("PENDING", initial.status)

        bus.drain()
        self.assertEqual("CREATED", store.get("ORD-2001").status)

    def test_asynchronous_payment_failure(self) -> None:
        inventory = InventoryService({"SKU-BOOK": 5})
        payment = PaymentService(["ORD-2002"])
        bus = EventBus()
        store = OrderStore()
        register_async_workflow(bus, store, inventory, payment)

        service = AsyncOrderService(bus, store)
        service.place_order("ORD-2002", "SKU-BOOK", 1)
        bus.drain()

        self.assertEqual("PAYMENT_FAILED", store.get("ORD-2002").status)


if __name__ == "__main__":
    unittest.main()
