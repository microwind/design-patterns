"""
order_service.py - 订单服务（核心业务服务）

【设计模式】
  - 依赖倒置原则（DIP）：依赖 InventoryClient 接口而非具体实现。
  - 外观模式（Facade）：create_order 对外提供统一入口，隐藏内部流程。
  - 策略模式：通过注入不同的 InventoryClient 实现，切换本地/远程调用。

【架构思想】
  OrderService 只关心业务逻辑，不关心库存服务的具体位置和实现。
  这种解耦使得服务可以独立部署和扩展。

【开源对比】
  - FastAPI / Flask 中通过依赖注入实现类似的解耦
  - nameko 框架提供 RPC 风格的微服务通信
  本示例用构造函数注入模拟 IoC。
"""

from src.order import Order


class OrderService(object):
    """订单服务。通过契约接口调用库存服务完成订单创建。

    Attributes:
        inventory_client: 库存服务客户端（支持本地/远程切换）
    """

    def __init__(self, inventory_client):
        """构造订单服务。

        Args:
            inventory_client: 库存服务客户端（InventoryClient 接口的实现）
        """
        self.inventory_client = inventory_client

    def create_order(self, order_id, sku, quantity):
        """创建订单。先调用库存服务预留库存，根据结果决定订单状态。

        Args:
            order_id: 订单ID
            sku: 商品 SKU 编码
            quantity: 订购数量

        Returns:
            Order: 创建的订单（status 为 "CREATED" 或 "REJECTED"）
        """
        # 通过契约接口调用库存服务（不关心是本地还是远程）
        if self.inventory_client.reserve(sku, quantity):
            return Order(order_id, sku, quantity, 'CREATED')
        return Order(order_id, sku, quantity, 'REJECTED')
