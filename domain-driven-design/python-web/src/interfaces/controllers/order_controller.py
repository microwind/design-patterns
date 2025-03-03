# -*- coding: utf-8 -*-
import json
from src.utils.body_parser import parse_body
from src.utils.response import response_json


class OrderController:
    def __init__(self, order_service):
        self.order_service = order_service

    def create_order(self, request, response):
        try:
            body = parse_body(request)
            customer_name = body.get('customer_name')
            amount = body.get('amount')

            # 验证订单金额
            try:
                amount_number = float(amount)
            except (ValueError, TypeError):
                raise ValueError('订单金额无效')

            order = self.order_service.create_order(
                customer_name, amount_number)
            response_json(response, 201, order)  # 使用公共响应方法
        except Exception as error:
            response_json(response, 400, {'error': str(error)})  # 错误响应

    def get_order(self, request, response):
        try:
            # 从路径中获取 ID
            order_id = request.params.get('id')
            if not order_id:
                raise ValueError('订单 ID 不能为空')

            try:
                order_id = int(order_id)
            except ValueError:
                raise ValueError('订单 ID 无效')

            order = self.order_service.get_order(order_id)
            response_json(response, 200, order)
        except Exception as error:
            response_json(response, 404, {'error': str(error)})


    def get_all_orders(self, request, response):
        try:
            # user_id来自参数或者cookie/session
            user_id = 10000001
            orders = self.order_service.get_all_orders(user_id)
            response_json(response, 200, orders)
        except Exception as error:
            response_json(response, 404, {'error': str(error)})

    def update_order(self, request, response):
        try:
            body = parse_body(request)
            # 从路径中获取 ID
            order_id = request.params.get('id')
            if not order_id:
                raise ValueError('订单 ID 不能为空')

            try:
                order_id = int(order_id)
            except ValueError:
                raise ValueError('订单 ID 无效')

            amount = body.get('amount')
            try:
                amount_number = float(amount)
            except (ValueError, TypeError):
                raise ValueError('订单金额无效')

            order = self.order_service.update_order(
                order_id, body.get('customer_name'), amount_number)
            response_json(response, 200, order)
        except Exception as error:
            response_json(response, 400, {'error': str(error)})

    def delete_order(self, request, response):
        try:
            # 从路径中获取 ID
            order_id = request.params.get('id')
            if not order_id:
                raise ValueError('订单 ID 不能为空')

            try:
                order_id = int(order_id)
            except ValueError:
                raise ValueError('订单 ID 无效')

            self.order_service.delete_order(order_id)
            response.send_response(204)
            response.send_header('Content-Length', '0')
            response.end_headers()
            response.end()
        except Exception as error:
            response_json(response, 404, {'error': str(error)})
