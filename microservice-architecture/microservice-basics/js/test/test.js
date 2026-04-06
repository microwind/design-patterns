import { InventoryService } from '../src/InventoryService.js'
import { OrderService } from '../src/OrderService.js'

function assertEqual(expected, actual, message) {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`)
  }
}

;(function run() {
  const inventory = new InventoryService()
  const service = new OrderService(inventory)

  const success = service.createOrder('ORD-1001', 'SKU-BOOK', 2)
  assertEqual('CREATED', success.status, 'status should be CREATED')
  assertEqual(8, inventory.available('SKU-BOOK'), 'stock should decrease')

  const failed = service.createOrder('ORD-1002', 'SKU-PEN', 2)
  assertEqual('REJECTED', failed.status, 'status should be REJECTED')
  assertEqual(1, inventory.available('SKU-PEN'), 'stock should remain')

  console.log('microservice-basics(js) tests passed')
})()
