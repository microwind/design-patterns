import { childSpan, gatewayEntry } from '../src/tracing.js'

function assertEqual(expected, actual, message) {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`)
  }
}

const gateway = gatewayEntry('TRACE-1001')
const order = childSpan(gateway, 'order-service', 'SPAN-ORDER')
const inventory = childSpan(order, 'inventory-service', 'SPAN-INVENTORY')

assertEqual(gateway.traceId, order.traceId, 'trace should reach order')
assertEqual(order.traceId, inventory.traceId, 'trace should reach inventory')
assertEqual(gateway.spanId, order.parentSpanId, 'order parent')
assertEqual(order.spanId, inventory.parentSpanId, 'inventory parent')

console.log('distributed-tracing(js) tests passed')
