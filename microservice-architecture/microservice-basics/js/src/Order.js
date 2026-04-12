/**
 * @file Order.js - 订单实体（值对象）
 *
 * 【设计模式】值对象模式：创建后状态不再改变。
 *
 * 【架构思想】
 *   在微服务中，订单实体作为服务间传递的数据载体（DTO）。
 *   status 字段体现业务结果："CREATED"（成功）或 "REJECTED"（库存不足）。
 */

/**
 * Order 订单实体
 */
export class Order {
  /**
   * @param {string} orderId  订单ID
   * @param {string} sku      商品SKU编码
   * @param {number} quantity 订购数量
   * @param {string} status   订单状态：CREATED / REJECTED
   */
  constructor(orderId, sku, quantity, status) {
    this.orderId = orderId
    this.sku = sku
    this.quantity = quantity
    this.status = status
  }
}
