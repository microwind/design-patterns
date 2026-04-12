/**
 * @file OrderService.ts - 订单服务（核心业务服务）
 *
 * 【设计模式】
 *   - 依赖倒置原则（DIP）：依赖 InventoryClient 接口而非具体实现。
 *   - 外观模式（Facade）：createOrder 对外提供统一入口。
 *   - 策略模式：通过注入不同的 InventoryClient 实现切换本地/远程调用。
 *
 * 【架构思想】
 *   OrderService 只关心业务逻辑，不关心库存服务的具体位置和实现。
 *
 * 【开源对比】
 *   - NestJS 通过 @Inject 装饰器实现依赖注入
 *   - tRPC 提供端到端类型安全的远程调用
 *   本示例用构造函数注入模拟 IoC。
 */

import { Order } from './Order.js'
import type { InventoryClient } from './InventoryClient.js'

/** 订单服务 */
export class OrderService {
  /**
   * @param inventoryClient 库存服务客户端（支持本地/远程切换）
   */
  constructor(private readonly inventoryClient: InventoryClient) {}

  /**
   * 创建订单。先调用库存服务预留库存，根据结果决定订单状态。
   * @param orderId  订单ID
   * @param sku      商品 SKU 编码
   * @param quantity 订购数量
   * @returns 创建的订单（status 为 "CREATED" 或 "REJECTED"）
   */
  createOrder(orderId: string, sku: string, quantity: number): Order {
    // 通过契约接口调用库存服务（不关心是本地还是远程）
    if (this.inventoryClient.reserve(sku, quantity)) {
      return new Order(orderId, sku, quantity, 'CREATED')
    }
    return new Order(orderId, sku, quantity, 'REJECTED')
  }
}
