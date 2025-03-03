# -*- coding: utf-8 -*-
import logging
from src.domain.order.order_repository import OrderRepository

# 订单仓储实现OrderRepository抽象类（例如本地字典实现）
class OrderRepositoryImpl(OrderRepository):
    def __init__(self):
        # 本地字典用作替代数据库，存储订单
        self.orders = {}

    def save(self, order):
        """保存订单到本地字典"""
        # 模拟保存订单操作，这里用字典替代数据库存储
        self.orders[order.id] = order

    def find_by_id(self, order_id):
        """根据订单ID查找订单"""
        # 在字典中查找订单，模拟从数据库中查找操作
        order = self.orders.get(order_id)
        if not order:
            raise ValueError(f"订单 ID {order_id} 不存在")
        return order

    def find_all(self, user_id):
        """获取所有订单"""
        # 返回字典中的所有订单，模拟查询所有记录
        return list(self.orders.values())

    def delete(self, order_id):
        """根据订单ID删除订单"""
        # 删除字典中的订单，模拟从数据库中删除记录
        if order_id not in self.orders:
            raise ValueError(f"订单 ID {order_id} 不存在，无法删除")
        del self.orders[order_id]

    def find_by_customer_name(self, customer_name):
        """根据客户名称查找所有订单"""
        # 遍历字典查找客户名称匹配的订单
        result = [order for order in self.orders.values(
        ) if order.customer_name == customer_name]
        if not result:
            raise ValueError(f"没有找到客户名称为 {customer_name} 的订单")
        return result
