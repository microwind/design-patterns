from dataclasses import dataclass


@dataclass
class SagaOrder:
    order_id: str
    status: str


class InventoryService:
    def __init__(self, stock: int) -> None:
        self.book_stock = stock

    def reserve(self, sku: str, quantity: int) -> bool:
        if sku != "SKU-BOOK" or quantity <= 0 or self.book_stock < quantity:
            return False
        self.book_stock -= quantity
        return True

    def release(self, sku: str, quantity: int) -> None:
        if sku == "SKU-BOOK" and quantity > 0:
            self.book_stock += quantity


class PaymentService:
    def __init__(self, fail: bool) -> None:
        self.fail = fail

    def charge(self, order_id: str) -> bool:
        return not self.fail


class SagaCoordinator:
    def __init__(self, stock: int, payment_fails: bool) -> None:
        self.inventory = InventoryService(stock)
        self.payment = PaymentService(payment_fails)

    def execute(self, order_id: str, sku: str, quantity: int) -> SagaOrder:
        order = SagaOrder(order_id, "PENDING")
        if not self.inventory.reserve(sku, quantity):
            order.status = "CANCELLED"
            return order
        if not self.payment.charge(order_id):
            self.inventory.release(sku, quantity)
            order.status = "CANCELLED"
            return order
        order.status = "COMPLETED"
        return order
