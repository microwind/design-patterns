# service/order_service.py
from domain.order import Order
from repository.order_repository import OrderRepository


class OrderService:
    def __init__(self):
        self.order_repository = OrderRepository()

    def create_order(self, id, customer_name, amount):
        order = Order(id, customer_name, amount)
        self.order_repository.save(order)
        print(f"订单 ID {id} 创建成功")

    def cancel_order(self, id):
        order = self.order_repository.find_by_id(id)
        if order:
            order.cancel()
        else:
            print(f"未找到 ID {id}")

    def query_order(self, id):
        order = self.order_repository.find_by_id(id)
        if order:
            order.display()
        else:
            print(f"未找到 ID {id}")

    def view_order_history(self):
        orders = self.order_repository.find_all()
        if not orders:
            print("暂无订单历史记录。")
        else:
            for order in orders:
                order.display()
