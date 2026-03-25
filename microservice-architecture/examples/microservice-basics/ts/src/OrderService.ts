import { Order } from './Order.js'
import type { InventoryClient } from './InventoryClient.js'

export class OrderService {
  constructor(private readonly inventoryClient: InventoryClient) {}

  createOrder(orderId: string, sku: string, quantity: number): Order {
    if (this.inventoryClient.reserve(sku, quantity)) {
      return new Order(orderId, sku, quantity, 'CREATED')
    }
    return new Order(orderId, sku, quantity, 'REJECTED')
  }
}
