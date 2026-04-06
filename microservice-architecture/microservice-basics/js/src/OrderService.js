import { Order } from './Order.js'

export class OrderService {
  constructor(inventoryClient) {
    this.inventoryClient = inventoryClient
  }

  createOrder(orderId, sku, quantity) {
    if (this.inventoryClient.reserve(sku, quantity)) {
      return new Order(orderId, sku, quantity, 'CREATED')
    }
    return new Order(orderId, sku, quantity, 'REJECTED')
  }
}
