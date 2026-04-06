import {
  AsyncOrderService,
  EventBus,
  InventoryService,
  OrderStore,
  PaymentService,
  registerAsyncWorkflow,
  SynchronousOrderService
} from '../src/communication.js'

function assertEqual(expected, actual, message) {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`)
  }
}

const syncService = new SynchronousOrderService(
  new InventoryService({ 'SKU-BOOK': 5 }),
  new PaymentService()
)
assertEqual('CREATED', syncService.placeOrder('ORD-1001', 'SKU-BOOK', 2).status, 'sync order')

const bus = new EventBus()
const store = new OrderStore()
registerAsyncWorkflow(bus, store, new InventoryService({ 'SKU-BOOK': 5 }), new PaymentService())
const asyncService = new AsyncOrderService(bus, store)
assertEqual('PENDING', asyncService.placeOrder('ORD-2001', 'SKU-BOOK', 2).status, 'async pending')
bus.drain()
assertEqual('CREATED', store.get('ORD-2001').status, 'async created')

const failingBus = new EventBus()
const failingStore = new OrderStore()
registerAsyncWorkflow(
  failingBus,
  failingStore,
  new InventoryService({ 'SKU-BOOK': 5 }),
  new PaymentService(['ORD-2002'])
)
new AsyncOrderService(failingBus, failingStore).placeOrder('ORD-2002', 'SKU-BOOK', 1)
failingBus.drain()
assertEqual('PAYMENT_FAILED', failingStore.get('ORD-2002').status, 'async failure')

console.log('service-communication(js) tests passed')
