export class InventoryService {
  constructor() {
    this.stock = new Map([
      ['SKU-BOOK', 10],
      ['SKU-PEN', 1],
    ])
  }

  reserve(sku, quantity) {
    const available = this.stock.get(sku) ?? 0
    if (available < quantity) {
      return false
    }
    this.stock.set(sku, available - quantity)
    return true
  }

  available(sku) {
    return this.stock.get(sku) ?? 0
  }
}
