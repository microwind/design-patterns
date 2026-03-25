import { Order } from './Order.js'

export class OrderServiceAsync {
  constructor(inventoryClient) {
    this.inventoryClient = inventoryClient
  }

  async createOrder(orderId, sku, quantity) {
    const reserved = await this.inventoryClient.reserve(sku, quantity)
    if (reserved) {
      return new Order(orderId, sku, quantity, 'CREATED')
    }
    return new Order(orderId, sku, quantity, 'REJECTED')
  }
}
