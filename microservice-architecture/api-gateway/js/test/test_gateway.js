import {
  APIGateway,
  inventoryServiceHandler,
  orderServiceHandler,
  requireUserHeader
} from '../src/gateway.js'

function assertEqual(expected, actual, message) {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`)
  }
}

const gateway = new APIGateway()
gateway.use(requireUserHeader('/api/orders', 'X-User'))
gateway.register('/api/orders', orderServiceHandler())
gateway.register('/api/inventory', inventoryServiceHandler())

const secured = gateway.handle({
  method: 'GET',
  path: '/api/orders/ORD-1001',
  headers: { 'X-User': 'jarry', 'X-Correlation-ID': 'trace-1001' }
})
assertEqual(200, secured.statusCode, 'secured route should succeed')
assertEqual('trace-1001', secured.headers['X-Correlation-ID'], 'trace id should be preserved')
assertEqual('order-service', secured.headers['X-Upstream-Service'], 'order route')

const unauthorized = gateway.handle({
  method: 'GET',
  path: '/api/orders/ORD-1002',
  headers: {}
})
assertEqual(401, unauthorized.statusCode, 'missing user should be rejected')

const inventory = gateway.handle({
  method: 'GET',
  path: '/api/inventory/SKU-BOOK',
  headers: {}
})
assertEqual(200, inventory.statusCode, 'inventory route should succeed')
assertEqual('inventory-service', inventory.headers['X-Upstream-Service'], 'inventory route')

const missing = gateway.handle({
  method: 'GET',
  path: '/api/unknown',
  headers: {}
})
assertEqual(404, missing.statusCode, 'missing route should return not found')

console.log('api-gateway(js) tests passed')
