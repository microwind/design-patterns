# apps/order/repositories.py
from models import Order

class OrderRepository:
    @staticmethod
    def get_by_id(order_id):
        return Order.query.get_or_404(order_id, description=f"Order {order_id} not found")

    @staticmethod
    def create(order_data):
        order = Order(**order_data)
        db.session.add(order)
        db.session.commit()
        return order