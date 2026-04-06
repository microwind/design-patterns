import { IdempotencyOrderService } from '../src/idempotency.js'

function assertEqual(expected, actual, message) {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`)
  }
}

const service = new IdempotencyOrderService()

const first = service.createOrder('IDEMP-ORDER-1001', 'ORD-1001', 'SKU-BOOK', 1)
assertEqual('CREATED', first.status, 'first request')
assertEqual(false, first.replayed, 'first request should not replay')

const second = service.createOrder('IDEMP-ORDER-1001', 'ORD-1001', 'SKU-BOOK', 1)
assertEqual('CREATED', second.status, 'duplicate request')
assertEqual(true, second.replayed, 'duplicate request should replay')

const conflict = service.createOrder('IDEMP-ORDER-1001', 'ORD-1001', 'SKU-BOOK', 2)
assertEqual('CONFLICT', conflict.status, 'conflicting request')
assertEqual(false, conflict.replayed, 'conflict should not replay')

console.log('idempotency-pattern(js) tests passed')
