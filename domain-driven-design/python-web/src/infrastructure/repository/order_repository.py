# -*- coding: utf-8 -*-
import logging

class OrderRepository:
    def __init__(self):
        self.orders = {}

    def save(self, order):
        """保存订单"""
        self.orders[order.id] = order

    def find_by_id(self, order_id):
        """根据ID查找订单"""
        order = self.orders.get(order_id)
        if not order:
            raise ValueError(f"订单 ID {order_id} 不存在")
        return order

    def find_all(self):
        """查找所有订单"""
        return list(self.orders.values())

    def delete(self, order_id):
        """删除订单"""
        if order_id not in self.orders:
            raise ValueError(f"订单 ID {order_id} 不存在，无法删除")
        del self.orders[order_id]

    def find_by_customer_name(self, customer_name):
        """根据客户名称查找订单"""
        result = [order for order in self.orders.values() if order.customer_name == customer_name]
        if not result:
            raise ValueError(f"没有找到客户名称为 {customer_name} 的订单")
        return result
