# -*- coding: utf-8 -*-
from abc import ABC, abstractmethod

# 订单仓储接口（抽象类）
class OrderRepository(ABC):
    @abstractmethod
    def save(self, order):
        """保存订单"""
        pass

    @abstractmethod
    def find_by_id(self, order_id):
        """根据ID查找订单"""
        pass

    @abstractmethod
    def find_all(self, user_id):
        """查找所有订单"""
        pass

    @abstractmethod
    def delete(self, order_id):
        """删除订单"""
        pass

    @abstractmethod
    def find_by_customer_name(self, customer_name):
        """根据客户名称查找订单"""
        pass