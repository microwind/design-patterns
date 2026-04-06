import { Order } from './Order.js'

export interface AsyncInventoryClient {
  reserve(sku: string, quantity: number): Promise<boolean>
}

export class OrderServiceAsync {
  constructor(private readonly inventoryClient: AsyncInventoryClient) {}

  async createOrder(orderId: string, sku: string, quantity: number): Promise<Order> {
    const reserved = await this.inventoryClient.reserve(sku, quantity)
    if (reserved) {
      return new Order(orderId, sku, quantity, 'CREATED')
    }
    return new Order(orderId, sku, quantity, 'REJECTED')
  }
}
