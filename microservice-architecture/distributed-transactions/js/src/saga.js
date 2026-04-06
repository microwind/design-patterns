class InventoryService {
  constructor(stock) {
    this.bookStock = stock
  }

  reserve(sku, quantity) {
    if (sku !== 'SKU-BOOK' || quantity <= 0 || this.bookStock < quantity) {
      return false
    }
    this.bookStock -= quantity
    return true
  }

  release(sku, quantity) {
    if (sku === 'SKU-BOOK' && quantity > 0) {
      this.bookStock += quantity
    }
  }
}

class PaymentService {
  constructor(fail) {
    this.fail = fail
  }

  charge(orderId) {
    return !this.fail
  }
}

export class SagaCoordinator {
  constructor(stock, paymentFails) {
    this.inventory = new InventoryService(stock)
    this.payment = new PaymentService(paymentFails)
  }

  execute(orderId, sku, quantity) {
    const order = { orderId, status: 'PENDING' }
    if (!this.inventory.reserve(sku, quantity)) {
      order.status = 'CANCELLED'
      return order
    }
    if (!this.payment.charge(orderId)) {
      this.inventory.release(sku, quantity)
      order.status = 'CANCELLED'
      return order
    }
    order.status = 'COMPLETED'
    return order
  }
}
