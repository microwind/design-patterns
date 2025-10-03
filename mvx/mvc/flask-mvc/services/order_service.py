# apps/order/services.py
from repositories import OrderRepository
from schemas import OrderSchema

class OrderService:
    @staticmethod
    def get_order_by_id(order_id: int):
        return OrderRepository.get_by_id(order_id)

    @staticmethod
    def create_order(order_data: dict):
        # 数据验证
        schema = OrderSchema()
        validated_data = schema.load(order_data)
        return OrderRepository.create(validated_data)