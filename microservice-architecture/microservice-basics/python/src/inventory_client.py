"""
inventory_client.py - 库存服务契约接口

【设计模式】
  - 依赖倒置原则（DIP）：OrderService 依赖此接口而非具体实现，
    使得本地调用和远程调用可以通过替换实现类来切换。
  - 策略模式（Strategy Pattern）：不同的实现类代表不同的调用策略。

【架构思想】
  在微服务架构中，服务间通过契约（接口/协议）解耦。
  调用方只依赖契约，不关心被调服务是本地实现还是远程服务。

【开源对比】
  - Python 生态中通常用 ABC（Abstract Base Class）定义服务契约
  - gRPC-Python 通过 .proto 文件自动生成客户端接口
  本示例用基类 + NotImplementedError 模拟抽象接口。
"""


class InventoryClient(object):
    """库存服务契约接口。

    所有库存服务实现（本地/远程）都必须实现此接口。
    OrderService 通过此接口访问库存服务，实现解耦。
    """

    def reserve(self, sku, quantity):
        """预留库存。

        Args:
            sku: 商品 SKU 编码
            quantity: 预留数量

        Returns:
            bool: True=预留成功，False=库存不足

        Raises:
            NotImplementedError: 子类必须实现此方法
        """
        raise NotImplementedError()
