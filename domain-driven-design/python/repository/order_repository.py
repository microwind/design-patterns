# repository/order_repository.py
from domain.order import Order

class OrderRepository:
    def __init__(self):
        self.orders = {}

    def save(self, order):
        self.orders[order.id] = order

    def find_by_id(self, id):
        return self.orders.get(id)

    def find_all(self):
        return list(self.orders.values())

    def clear(self):
        self.orders.clear()
        print("所有订单已清理")