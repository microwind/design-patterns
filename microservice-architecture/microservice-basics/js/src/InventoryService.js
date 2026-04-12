/**
 * @file InventoryService.js - 本地库存服务实现（阶段1）
 *
 * 【设计模式】
 *   - 策略模式（Strategy Pattern）：作为 InventoryClient 的本地实现策略。
 *   - 适配器模式（Adapter Pattern）：将内存 Map 操作适配为统一的 reserve 接口。
 *
 * 【架构思想】
 *   阶段1 的库存服务运行在同一进程内，代表单体架构。
 *   当切换到 HttpInventoryClient 时，OrderService 无需修改。
 *
 * 【开源对比】
 *   实际工程中库存数据存储在数据库，预留需要分布式锁保证并发安全。
 *   本示例用内存 Map 简化，聚焦于服务拆分和契约调用的本质。
 */

/**
 * InventoryService 本地库存服务
 */
export class InventoryService {
  constructor() {
    /** @type {Map<string, number>} 内存库存表：SKU -> 可用数量 */
    this.stock = new Map([
      ['SKU-BOOK', 10],
      ['SKU-PEN', 1],
    ])
  }

  /**
   * 预留库存。检查是否充足，充足则扣减并返回 true。
   * @param {string} sku      商品 SKU 编码
   * @param {number} quantity 预留数量
   * @returns {boolean} true=预留成功，false=库存不足
   */
  reserve(sku, quantity) {
    const available = this.stock.get(sku) ?? 0
    // 库存不足，拒绝预留
    if (available < quantity) {
      return false
    }
    // 扣减库存
    this.stock.set(sku, available - quantity)
    return true
  }

  /**
   * 查询指定 SKU 的可用库存数量
   * @param {string} sku 商品 SKU 编码
   * @returns {number} 可用数量
   */
  available(sku) {
    return this.stock.get(sku) ?? 0
  }
}
