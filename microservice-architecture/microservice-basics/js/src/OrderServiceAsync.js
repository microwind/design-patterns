/**
 * @file OrderServiceAsync.js - 异步订单服务（阶段2 配套）
 *
 * 【设计模式】
 *   - 依赖倒置原则（DIP）：依赖 inventoryClient 接口。
 *   - 适配器模式：将异步远程调用适配为统一的业务接口。
 *
 * 【架构思想】
 *   远程调用本质上是异步的（网络 I/O），因此阶段2 的订单服务需要
 *   使用 async/await 处理异步响应。
 */

import { Order } from './Order.js'

/** 异步订单服务，配合 HttpInventoryClient 使用 */
export class OrderServiceAsync {
  /**
   * @param {object} inventoryClient 异步库存客户端（reserve 返回 Promise）
   */
  constructor(inventoryClient) {
    this.inventoryClient = inventoryClient
  }

  /**
   * 异步创建订单。await 库存服务的远程响应后决定订单状态。
   * @param {string} orderId  订单ID
   * @param {string} sku      商品 SKU 编码
   * @param {number} quantity 订购数量
   * @returns {Promise<Order>}
   */
  async createOrder(orderId, sku, quantity) {
    // await 远程调用结果
    const reserved = await this.inventoryClient.reserve(sku, quantity)
    if (reserved) {
      return new Order(orderId, sku, quantity, 'CREATED')
    }
    return new Order(orderId, sku, quantity, 'REJECTED')
  }
}
