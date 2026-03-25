import type { InventoryClient } from './InventoryClient.js'

export class InventoryService implements InventoryClient {
  private stock: Map<string, number>

  constructor() {
    this.stock = new Map([
      ['SKU-BOOK', 10],
      ['SKU-PEN', 1],
    ])
  }

  reserve(sku: string, quantity: number): boolean {
    const available = this.stock.get(sku) ?? 0
    if (available < quantity) {
      return false
    }
    this.stock.set(sku, available - quantity)
    return true
  }

  available(sku: string): number {
    return this.stock.get(sku) ?? 0
  }
}
