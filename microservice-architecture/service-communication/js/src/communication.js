export class InventoryService {
  constructor(stock) {
    this.stock = { ...stock }
  }

  reserve(sku, quantity) {
    const available = this.stock[sku] || 0
    if (quantity <= 0 || available < quantity) {
      return false
    }
    this.stock[sku] = available - quantity
    return true
  }
}

export class PaymentService {
  constructor(failOrderIds = []) {
    this.failOrderIds = new Set(failOrderIds)
  }

  charge(orderId) {
    return !this.failOrderIds.has(orderId)
  }
}

export class SynchronousOrderService {
  constructor(inventory, payment) {
    this.inventory = inventory
    this.payment = payment
  }

  placeOrder(orderId, sku, quantity) {
    if (!this.inventory.reserve(sku, quantity)) {
      return { orderId, sku, quantity, status: 'REJECTED' }
    }
    if (!this.payment.charge(orderId)) {
      return { orderId, sku, quantity, status: 'PAYMENT_FAILED' }
    }
    return { orderId, sku, quantity, status: 'CREATED' }
  }
}

export class EventBus {
  constructor() {
    this.subscribers = new Map()
    this.queue = []
  }

  subscribe(eventName, handler) {
    if (!this.subscribers.has(eventName)) {
      this.subscribers.set(eventName, [])
    }
    this.subscribers.get(eventName).push(handler)
  }

  publish(event) {
    this.queue.push(event)
  }

  drain() {
    while (this.queue.length > 0) {
      const event = this.queue.shift()
      for (const handler of this.subscribers.get(event.name) || []) {
        handler(event)
      }
    }
  }
}

export class OrderStore {
  constructor() {
    this.orders = new Map()
  }

  save(order) {
    this.orders.set(order.orderId, order)
  }

  updateStatus(orderId, status) {
    const order = this.orders.get(orderId)
    this.orders.set(orderId, { ...order, status })
  }

  get(orderId) {
    return this.orders.get(orderId)
  }
}

export class AsyncOrderService {
  constructor(bus, store) {
    this.bus = bus
    this.store = store
  }

  placeOrder(orderId, sku, quantity) {
    const order = { orderId, sku, quantity, status: 'PENDING' }
    this.store.save(order)
    this.bus.publish({ name: 'order_placed', orderId, sku, quantity })
    return order
  }
}

export function registerAsyncWorkflow(bus, store, inventory, payment) {
  bus.subscribe('order_placed', (event) => {
    if (!inventory.reserve(event.sku, event.quantity)) {
      store.updateStatus(event.orderId, 'REJECTED')
      return
    }
    if (!payment.charge(event.orderId)) {
      store.updateStatus(event.orderId, 'PAYMENT_FAILED')
      return
    }
    store.updateStatus(event.orderId, 'CREATED')
  })
}
