/**
 * @file communication.js - 服务间通信模式（Service Communication Pattern）的 JavaScript 实现
 *
 * 本模块演示微服务架构中两种核心通信方式：同步通信（请求-响应）和异步通信（事件驱动）。
 *
 * 【设计模式】
 *   - 外观模式（Facade Pattern）：SynchronousOrderService 将多个服务调用封装为
 *     统一的 placeOrder 接口，调用方无需了解内部编排逻辑。
 *   - 观察者模式（Observer Pattern）：EventBus 实现发布-订阅机制，
 *     AsyncOrderService 发布事件，下游通过 subscribe 注册处理函数。
 *   - 中介者模式（Mediator Pattern）：EventBus 充当中介者，协调多服务间交互。
 *
 * 【架构思想】
 *   同步通信简单直观但耦合度高；异步通信通过事件总线解耦，支持独立扩展和故障隔离。
 *
 * 【开源对比】
 *   - 同步：axios / node-fetch（HTTP 客户端）、@grpc/grpc-js（gRPC Node.js）
 *   - 异步：bullmq（任务队列）、kafkajs、amqplib（RabbitMQ）
 *   本示例省略了网络传输和序列化，聚焦于同步/异步两种编排模式的对比。
 */

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
