# apps/order/controllers.py
from flask import request
from services import OrderService
from utils.response import api_response

class OrderController:
    @staticmethod
    def get_order(order_id):
        order = OrderService.get_order_by_id(order_id)
        return api_response(data=order.to_dict())

    @staticmethod
    def create_order():
        data = request.get_json()
        order = OrderService.create_order(data)
        return api_response(data=order.to_dict(), status=201)