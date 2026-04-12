/**
 * @file InventoryService.ts - 本地库存服务实现（阶段1）
 *
 * 【设计模式】
 *   - 策略模式：作为 InventoryClient 的本地实现策略。
 *   - 适配器模式：将内存 Map 操作适配为统一的 reserve 接口。
 *
 * 【架构思想】
 *   阶段1 运行在同一进程内，代表单体架构。
 *   切换到 HttpInventoryClient 时 OrderService 无需修改。
 *
 * 【开源对比】
 *   实际工程中库存数据存储在数据库，预留需要分布式锁保证并发安全。
 */

import type { InventoryClient } from './InventoryClient.js'

/** 本地库存服务，实现 InventoryClient 契约接口 */
export class InventoryService implements InventoryClient {
  /** 内存库存表：SKU -> 可用数量 */
  private stock: Map<string, number>

  constructor() {
    // 初始化测试库存数据
    this.stock = new Map([
      ['SKU-BOOK', 10],
      ['SKU-PEN', 1],
    ])
  }

  /**
   * 预留库存。检查是否充足，充足则扣减并返回 true。
   */
  reserve(sku: string, quantity: number): boolean {
    const available = this.stock.get(sku) ?? 0
    // 库存不足，拒绝预留
    if (available < quantity) {
      return false
    }
    // 扣减库存
    this.stock.set(sku, available - quantity)
    return true
  }

  /** 查询指定 SKU 的可用库存数量 */
  available(sku: string): number {
    return this.stock.get(sku) ?? 0
  }
}
