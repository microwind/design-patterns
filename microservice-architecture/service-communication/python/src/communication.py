from dataclasses import dataclass
from typing import Callable, Dict, List, Optional


@dataclass
class Order:
    order_id: str
    sku: str
    quantity: int
    status: str


class InventoryService:
    def __init__(self, stock: Dict[str, int]) -> None:
        self._stock = dict(stock)

    def reserve(self, sku: str, quantity: int) -> bool:
        available = self._stock.get(sku, 0)
        if quantity <= 0 or available < quantity:
            return False
        self._stock[sku] = available - quantity
        return True


class PaymentService:
    def __init__(self, fail_order_ids: Optional[List[str]] = None) -> None:
        self._fail_order_ids = set(fail_order_ids or [])

    def charge(self, order_id: str) -> bool:
        return order_id not in self._fail_order_ids


class SynchronousOrderService:
    def __init__(self, inventory: InventoryService, payment: PaymentService) -> None:
        self._inventory = inventory
        self._payment = payment

    def place_order(self, order_id: str, sku: str, quantity: int) -> Order:
        if not self._inventory.reserve(sku, quantity):
            return Order(order_id, sku, quantity, "REJECTED")
        if not self._payment.charge(order_id):
            return Order(order_id, sku, quantity, "PAYMENT_FAILED")
        return Order(order_id, sku, quantity, "CREATED")


@dataclass
class Event:
    name: str
    order_id: str
    sku: str
    quantity: int


class EventBus:
    def __init__(self) -> None:
        self._subscribers: Dict[str, List[Callable[[Event], None]]] = {}
        self._queue: List[Event] = []

    def subscribe(self, event_name: str, handler: Callable[[Event], None]) -> None:
        self._subscribers.setdefault(event_name, []).append(handler)

    def publish(self, event: Event) -> None:
        self._queue.append(event)

    def drain(self) -> None:
        while self._queue:
            event = self._queue.pop(0)
            for handler in self._subscribers.get(event.name, []):
                handler(event)


class OrderStore:
    def __init__(self) -> None:
        self._orders: Dict[str, Order] = {}

    def save(self, order: Order) -> None:
        self._orders[order.order_id] = order

    def update_status(self, order_id: str, status: str) -> None:
        order = self._orders[order_id]
        self._orders[order_id] = Order(order.order_id, order.sku, order.quantity, status)

    def get(self, order_id: str) -> Order:
        return self._orders[order_id]


class AsyncOrderService:
    def __init__(self, bus: EventBus, store: OrderStore) -> None:
        self._bus = bus
        self._store = store

    def place_order(self, order_id: str, sku: str, quantity: int) -> Order:
        order = Order(order_id, sku, quantity, "PENDING")
        self._store.save(order)
        self._bus.publish(Event("order_placed", order_id, sku, quantity))
        return order


def register_async_workflow(
    bus: EventBus,
    store: OrderStore,
    inventory: InventoryService,
    payment: PaymentService,
) -> None:
    def handle(event: Event) -> None:
        if not inventory.reserve(event.sku, event.quantity):
            store.update_status(event.order_id, "REJECTED")
            return
        if not payment.charge(event.order_id):
            store.update_status(event.order_id, "PAYMENT_FAILED")
            return
        store.update_status(event.order_id, "CREATED")

    bus.subscribe("order_placed", handle)
