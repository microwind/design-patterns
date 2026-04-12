/**
 * @file OrderServiceAsync.ts - 异步订单服务（阶段2 配套）
 *
 * 【设计模式】
 *   - 依赖倒置原则（DIP）：依赖 AsyncInventoryClient 接口。
 *   - 适配器模式：将异步远程调用适配为统一的业务接口。
 *
 * 【架构思想】
 *   远程调用本质上是异步的（网络 I/O），因此阶段2 的订单服务需要
 *   使用 async/await 处理异步响应。这体现了"远程调用不能当成本地函数调用"的核心认知。
 *   AsyncInventoryClient 接口的 reserve 返回 Promise<boolean>，
 *   而非同步的 boolean。
 */

import { Order } from './Order.js'

/** 异步库存服务契约接口（用于远程调用场景） */
export interface AsyncInventoryClient {
  reserve(sku: string, quantity: number): Promise<boolean>
}

/** 异步订单服务，配合 HttpInventoryClient 使用 */
export class OrderServiceAsync {
  constructor(private readonly inventoryClient: AsyncInventoryClient) {}

  /**
   * 异步创建订单。await 库存服务的远程响应后决定订单状态。
   */
  async createOrder(orderId: string, sku: string, quantity: number): Promise<Order> {
    // await 远程调用结果
    const reserved = await this.inventoryClient.reserve(sku, quantity)
    if (reserved) {
      return new Order(orderId, sku, quantity, 'CREATED')
    }
    return new Order(orderId, sku, quantity, 'REJECTED')
  }
}
