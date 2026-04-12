"""
saga.py - 分布式事务 Saga 模式的 Python 实现

【设计模式】
  - 命令模式：每个步骤（reserve/charge）和补偿（release）是独立命令。
  - 责任链模式：正向步骤按链式顺序执行，失败则中断并补偿。
  - 状态模式：订单状态 PENDING → COMPLETED / CANCELLED。

【架构思想】
  Saga 将跨服务事务拆分为有序步骤 + 补偿动作，实现最终一致性。

【开源对比】
  - Temporal Python SDK：工作流引擎
  - Python + Celery：通过 Celery chain + 补偿任务实现
  本示例用同步方法调用模拟编排式 Saga。
"""

from dataclasses import dataclass


@dataclass
class SagaOrder:
    """Saga 订单实体。状态：PENDING → COMPLETED / CANCELLED。"""
    order_id: str
    status: str  # PENDING / COMPLETED / CANCELLED


class InventoryService:
    """库存服务。提供正向操作 reserve 和补偿操作 release。"""

    def __init__(self, stock: int) -> None:
        self.book_stock = stock

    def reserve(self, sku: str, quantity: int) -> bool:
        """正向步骤：预占库存。"""
        if sku != "SKU-BOOK" or quantity <= 0 or self.book_stock < quantity:
            return False
        self.book_stock -= quantity
        return True

    def release(self, sku: str, quantity: int) -> None:
        """补偿动作：释放已预占的库存。"""
        if sku == "SKU-BOOK" and quantity > 0:
            self.book_stock += quantity


class PaymentService:
    """支付服务。fail 标志模拟支付失败。"""

    def __init__(self, fail: bool) -> None:
        self.fail = fail

    def charge(self, order_id: str) -> bool:
        """正向步骤：扣款。"""
        return not self.fail


class SagaCoordinator:
    """Saga 协调者（编排式）。

    【设计模式】命令模式 + 责任链：按序执行步骤，失败时逆序补偿。

    执行流程：
      1. 库存预占 → 失败则 CANCELLED
      2. 支付扣款 → 失败则补偿释放库存 → CANCELLED
      3. 全部成功 → COMPLETED
    """

    def __init__(self, stock: int, payment_fails: bool) -> None:
        self.inventory = InventoryService(stock)
        self.payment = PaymentService(payment_fails)

    def execute(self, order_id: str, sku: str, quantity: int) -> SagaOrder:
        """执行 Saga 事务。"""
        order = SagaOrder(order_id, "PENDING")

        # 步骤1：库存预占
        if not self.inventory.reserve(sku, quantity):
            order.status = "CANCELLED"
            return order

        # 步骤2：支付扣款
        if not self.payment.charge(order_id):
            # 支付失败 → 补偿：释放库存
            self.inventory.release(sku, quantity)
            order.status = "CANCELLED"
            return order

        # 全部成功
        order.status = "COMPLETED"
        return order
