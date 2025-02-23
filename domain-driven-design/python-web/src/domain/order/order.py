# -*- coding: utf-8 -*-
import logging

# 配置日志
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

class OrderStatus:
    CREATED = 0
    CANCELED = 1


class Order:
    def __init__(self, order_id, customer_name, amount, status=OrderStatus.CREATED):
        self.id = order_id
        self.customer_name = customer_name
        self.amount = amount
        self.status = status

    @staticmethod
    def new_order(order_id, customer_name, amount):
        if amount <= 0:
            logging.warning("订单金额无效")
            return None
        return Order(order_id, customer_name, amount)

    def cancel(self):
        if self.status == OrderStatus.CREATED:
            self.status = OrderStatus.CANCELED
            logging.info(f"订单 ID {self.id} 已取消")
        else:
            logging.warning(f"订单 ID {self.id} 已取消，无法重复取消")

    def update_customer_info(self, new_customer_name):
        self.customer_name = new_customer_name
        logging.info(f"订单 ID {self.id} 的客户名称已更新为: {self.customer_name}")

    def update_amount(self, new_amount):
        if new_amount <= 0:
            logging.warning("更新金额无效")
            return
        self.amount = new_amount
        logging.info(f"订单 ID {self.id} 的金额已更新为: {self.amount}")

    def display(self):
        logging.info(f"订单 ID: {self.id}\n客户名称: {self.customer_name}\n订单金额: {self.amount}\n订单状态: {self.status_to_string()}")

    def status_to_string(self):
        if self.status == OrderStatus.CREATED:
            return '已创建'
        elif self.status == OrderStatus.CANCELED:
            return '已取消'
        else:
            return '未知状态'
