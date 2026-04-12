"""
order.py - 订单实体（值对象）

【设计模式】
  值对象模式：Order 创建后状态不再改变，所有属性通过构造函数初始化。

【架构思想】
  在微服务中，订单实体通常作为服务间传递的数据载体（DTO）。
  status 字段体现业务结果："CREATED"（成功）或 "REJECTED"（库存不足）。
"""


class Order(object):
    """订单实体。

    Attributes:
        order_id: 订单ID
        sku: 商品SKU编码
        quantity: 订购数量
        status: 订单状态（CREATED / REJECTED）
    """

    def __init__(self, order_id, sku, quantity, status):
        self.order_id = order_id
        self.sku = sku
        self.quantity = quantity
        self.status = status
