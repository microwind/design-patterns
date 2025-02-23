# -*- coding: utf-8 -*-
import random
import time
from src.domain.order.order import Order


class OrderService:
    def __init__(self, order_repository):
        self.order_repository = order_repository

    async def create_order(self, customer_name, amount):
        """
        创建订单并保存到仓储中
        """
        # 自动生成订单 ID
        order_id = await self.generate_order_id()

        # 创建订单
        new_order = Order.new_order(order_id, customer_name, amount)
        if not new_order:
            raise ValueError('订单创建失败')

        try:
            await self.order_repository.save(new_order)
            return new_order
        except Exception as error:
            raise ValueError(f'订单保存失败: {error}')

    async def generate_order_id(self):
        """
        自动生成订单 ID
        这里可以根据业务需求生成唯一的 ID，如使用时间戳 + 随机数
        """
        timestamp = int(time.time() * 1000)
        random_num = random.randint(0, 999)
        return int(f"{timestamp}{random_num}")

    async def cancel_order(self, order_id):
        """
        取消订单
        """
        try:
            order = await self.order_repository.find_by_id(order_id)
            order.cancel()
            await self.order_repository.save(order)
        except Exception as error:
            raise ValueError(f'订单取消失败: {error}')

    async def get_order(self, order_id):
        """
        查询订单
        """
        try:
            return await self.order_repository.find_by_id(order_id)
        except Exception as error:
            raise ValueError(f'查询订单失败: {error}')

    async def update_order(self, order_id, customer_name, amount):
        """
        更新订单的客户信息和金额
        """
        try:
            order = await self.order_repository.find_by_id(order_id)
            order.update_customer_info(customer_name)
            order.update_amount(amount)
            await self.order_repository.save(order)
            return order
        except Exception as error:
            raise ValueError(f'更新订单失败: {error}')

    async def delete_order(self, order_id):
        """
        删除订单
        """
        try:
            order = await self.order_repository.find_by_id(order_id)
            await self.order_repository.delete(order.id)
        except Exception as error:
            raise ValueError(f'删除订单失败: {error}')