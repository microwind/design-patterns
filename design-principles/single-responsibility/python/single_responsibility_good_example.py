# -*- coding: utf-8 -*-
"""
@author: jarry
"""
"""
这个例子符合单一职责原则。
1. 分别建立三个类，一个负责订单业务处理(OrderProcessor)，一个负责校验订单(OrderValidator)，一个负责保存数据(OrderDao)，各司其职。
2. 由处理订单来负责调用工具类，订单业务逻辑修改不会影响到工具类。
3. 工具类修改也不会影响到订单业务处理。
4. 职责是否足够单一，要根据具体场景而异，不同情形下采用不同的设计，主要的目标是便于理解、扩展和维护。
"""


class OrderProcessor:
    def __init__(self):
        self.orderValidator = OrderValidator()
        self.orderDao = OrderDao()

    # 订单处理逻辑方法
    def processOrder(self, orderId):
        print("oder ID:", orderId)
        # 1. 先验证订单，调用校验类
        if not self.orderValidator.validateId(orderId):
            print("order validate id failed.")
            return False

        if not self.orderValidator.validateTime():
            print("order validate time failed.")
            return False

        # 2. 订单数据其他逻辑处理
        if orderId % 2 == 0:
            print("order data processing.")

        # 3. 再保存订单到数据库，调用数据库类
        print("order save to DB.")
        self.orderDao.saveOrder(orderId)
        # 或则删除订单
        # self.deleteOrder(orderId)

        return True


# 订单校验类，校验订单的合法性和有效性等
class OrderValidator:
    # 校验订单逻辑，最好别放在订单处理类中
    def validateId(self, orderId):
        # doSomething
        if orderId % 2 == 1:
            return False
        return True

    def validateTime(self):
        # doSomething
        return True


# Order数据库访问类，负责处理订单的CRUD操作
class OrderDao:
    # 删除订单
    def deleteOrder(self, orderId):
        # doSomething
        return True

    # 保存订单到数据库
    def saveOrder(self, orderId):
        if orderId % 2 == 0:
            print("data saving.")
        print("data save done.")
        return True


## 测试验证
orderProcessor = OrderProcessor()
orderProcessor.processOrder(1001)
orderProcessor.processOrder(1002)

"""
jarry@jarrys-MBP python % python3 single_responsibility_good_example.py 
oder ID: 1001
order validate id failed.
oder ID: 1002
order data processing.
order save to DB.
data saving.
data save done.
"""
