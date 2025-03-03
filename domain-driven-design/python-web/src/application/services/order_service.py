# -*- coding: utf-8 -*-
import random
import time
from src.domain.order.order import Order


class OrderService:
    def __init__(self, order_repository):
        self.order_repository = order_repository

    def create_order(self, customer_name, amount):
        """
        创建订单并保存到仓储中
        """
        # 自动生成订单 ID
        order_id = self.generate_order_id()

        # 创建订单
        new_order = Order.new_order(order_id, customer_name, amount)
        if not new_order:
            raise ValueError('订单创建失败')

        try:
            self.order_repository.save(new_order)
            return new_order
        except Exception as error:
            raise ValueError(f'订单保存失败: {error}')

    def generate_order_id(self):
        """
        自动生成订单 ID
        这里可以根据业务需求生成唯一的 ID，如使用时间戳 + 随机数
        """
        timestamp = int(time.time() * 1000)
        random_num = random.randint(0, 999)
        return int(f"{timestamp}{random_num}")

    def cancel_order(self, order_id):
        """
        取消订单
        """
        try:
            order = self.order_repository.find_by_id(order_id)
            order.cancel()
            self.order_repository.save(order)
        except Exception as error:
            raise ValueError(f'订单取消失败: {error}')

    def get_order(self, order_id):
        """
        查询订单
        """
        try:
            return self.order_repository.find_by_id(order_id)
        except Exception as error:
            raise ValueError(f'查询订单失败: {error}')

    def get_all_orders(self, user_id):
        """
        查询全部订单
        """
        try:
            return self.order_repository.find_all(user_id)
        except Exception as error:
            raise ValueError(f'查询订单失败: {error}')

    def update_order(self, order_id, customer_name, amount):
        """
        更新订单的客户信息和金额
        """
        try:
            order = self.order_repository.find_by_id(order_id)
            order.update_customer_info(customer_name)
            order.update_amount(amount)
            self.order_repository.save(order)
            return order
        except Exception as error:
            raise ValueError(f'更新订单失败: {error}')

    def delete_order(self, order_id):
        """
        删除订单
        """
        try:
            order = self.order_repository.find_by_id(order_id)
            self.order_repository.delete(order.id)
        except Exception as error:
            raise ValueError(f'删除订单失败: {error}')
