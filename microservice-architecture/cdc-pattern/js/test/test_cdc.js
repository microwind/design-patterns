import { Broker, DataStore } from '../src/cdc.js'

function assertEqual(expected, actual, message) {
  if (expected !== actual) throw new Error(`${message} expected=${expected} actual=${actual}`)
}

const store = new DataStore()
const broker = new Broker()
store.createOrder('ORD-1001')
assertEqual(false, store.changes[0].processed, 'initial')
store.relayChanges(broker)
assertEqual(1, broker.published.length, 'publish once')
assertEqual(true, store.changes[0].processed, 'processed')
store.relayChanges(broker)
assertEqual(1, broker.published.length, 'no duplicate')
console.log('cdc-pattern(js) tests passed')
