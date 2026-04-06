import { MemoryBroker, OutboxService } from '../src/outbox.js'

function assertEqual(expected, actual, message) {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`)
  }
}

const service = new OutboxService()
const broker = new MemoryBroker()

service.createOrder('ORD-1001')
assertEqual(1, service.orders.length, 'order count')
assertEqual('pending', service.outbox[0].status, 'pending status')

service.relayPending(broker)
assertEqual(1, broker.published.length, 'publish once')
assertEqual('published', service.outbox[0].status, 'mark published')

service.relayPending(broker)
assertEqual(1, broker.published.length, 'do not republish')

console.log('outbox-pattern(js) tests passed')
