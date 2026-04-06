import { SagaCoordinator } from '../src/saga.js'

function assertEqual(expected, actual, message) {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`)
  }
}

const success = new SagaCoordinator(10, false)
const completed = success.execute('ORD-1001', 'SKU-BOOK', 2)
assertEqual('COMPLETED', completed.status, 'successful saga')
assertEqual(8, success.inventory.bookStock, 'stock decrease')

const failure = new SagaCoordinator(10, true)
const cancelled = failure.execute('ORD-1002', 'SKU-BOOK', 2)
assertEqual('CANCELLED', cancelled.status, 'cancelled saga')
assertEqual(10, failure.inventory.bookStock, 'compensated stock')

console.log('distributed-transactions(js) tests passed')
