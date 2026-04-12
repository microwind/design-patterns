"""
inventory_service.py - 本地库存服务实现（阶段1）

【设计模式】
  - 策略模式（Strategy Pattern）：作为 InventoryClient 的本地实现策略，
    直接操作内存中的库存数据。
  - 适配器模式（Adapter Pattern）：将内存字典操作适配为统一的 reserve 接口。

【架构思想】
  阶段1 的库存服务运行在同一进程内，代表单体架构的调用方式。
  当演进到阶段2 时，同一个接口由 HttpInventoryClient 实现，
  OrderService 无需修改任何代码即可切换到远程调用。

【开源对比】
  实际工程中库存数据存储在数据库（MySQL/Redis），预留需要分布式锁保证并发安全。
  本示例用内存字典简化，聚焦于服务拆分和契约调用的本质。
"""

from src.inventory_client import InventoryClient


class InventoryService(InventoryClient):
    """本地库存服务，实现 InventoryClient 契约接口。

    使用内存字典维护库存，适用于阶段1（进程内契约调用）。
    """

    def __init__(self):
        # 初始化测试库存数据
        self.stock = {
            'SKU-BOOK': 10,
            'SKU-PEN': 1,
        }

    def reserve(self, sku, quantity):
        """预留库存。检查是否充足，充足则扣减并返回 True。

        Args:
            sku: 商品 SKU 编码
            quantity: 预留数量

        Returns:
            bool: True=预留成功，False=库存不足
        """
        available = self.stock.get(sku, 0)
        # 库存不足，拒绝预留
        if available < quantity:
            return False
        # 扣减库存
        self.stock[sku] = available - quantity
        return True

    def available(self, sku):
        """查询指定 SKU 的可用库存数量。"""
        return self.stock.get(sku, 0)
