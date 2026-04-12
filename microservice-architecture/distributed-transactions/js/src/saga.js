/**
 * @file saga.js - 分布式事务 Saga 模式的 JavaScript 实现
 *
 * 【设计模式】
 *   - 命令模式：每个步骤和补偿是独立命令。
 *   - 责任链模式：正向步骤按链式顺序执行。
 *   - 状态模式：订单状态 PENDING → COMPLETED / CANCELLED。
 *
 * 【架构思想】
 *   Saga 将跨服务事务拆分为有序步骤 + 补偿动作。
 *
 * 【开源对比】
 *   - Temporal Node SDK：工作流引擎
 *   本示例用同步方法调用模拟编排式 Saga。
 */

/**
 * InventoryService - 库存服务
 * 提供正向操作 reserve 和补偿操作 release。
 */
class InventoryService {
  constructor(stock) {
    this.bookStock = stock
  }

  /** 正向步骤：预占库存 */
  reserve(sku, quantity) {
    if (sku !== 'SKU-BOOK' || quantity <= 0 || this.bookStock < quantity) {
      return false
    }
    this.bookStock -= quantity
    return true
  }

  /** 补偿动作：释放已预占的库存 */
  release(sku, quantity) {
    if (sku === 'SKU-BOOK' && quantity > 0) {
      this.bookStock += quantity
    }
  }
}

/**
 * PaymentService - 支付服务
 */
class PaymentService {
  constructor(fail) {
    this.fail = fail
  }

  /** 正向步骤：扣款 */
  charge(orderId) {
    return !this.fail
  }
}

/**
 * SagaCoordinator - Saga 协调者（编排式）
 * 【设计模式】命令模式 + 责任链：按序执行步骤，失败时逆序补偿。
 */
export class SagaCoordinator {
  constructor(stock, paymentFails) {
    this.inventory = new InventoryService(stock)
    this.payment = new PaymentService(paymentFails)
  }

  /**
   * 执行 Saga 事务：库存预占 → 支付 → 成功/补偿。
   * @returns {{ orderId, status }} 最终订单
   */
  execute(orderId, sku, quantity) {
    const order = { orderId, status: 'PENDING' }

    // 步骤1：库存预占
    if (!this.inventory.reserve(sku, quantity)) {
      order.status = 'CANCELLED'
      return order
    }

    // 步骤2：支付扣款
    if (!this.payment.charge(orderId)) {
      // 支付失败 → 补偿：释放库存
      this.inventory.release(sku, quantity)
      order.status = 'CANCELLED'
      return order
    }

    // 全部成功
    order.status = 'COMPLETED'
    return order
  }
}
