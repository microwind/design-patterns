export class IdempotencyOrderService {
  constructor() {
    this.store = new Map()
  }

  createOrder(idempotencyKey, orderId, sku, quantity) {
    const fingerprint = `${orderId}|${sku}|${quantity}`
    const existing = this.store.get(idempotencyKey)
    if (existing) {
      if (existing.fingerprint !== fingerprint) {
        return { orderId, sku, quantity, status: 'CONFLICT', replayed: false }
      }
      return { ...existing.response, replayed: true }
    }

    const response = { orderId, sku, quantity, status: 'CREATED', replayed: false }
    this.store.set(idempotencyKey, { fingerprint, response })
    return response
  }
}
