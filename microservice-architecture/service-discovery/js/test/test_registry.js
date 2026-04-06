import { RoundRobinDiscoverer, ServiceRegistry } from '../src/registry.js'

function assertEqual(expected, actual, message) {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`)
  }
}

const registry = new ServiceRegistry()
registry.register('inventory-service', { instanceId: 'inventory-a', address: '10.0.0.1:8081' })
registry.register('inventory-service', { instanceId: 'inventory-b', address: '10.0.0.2:8081' })

assertEqual(2, registry.instances('inventory-service').length, 'registry should hold two instances')

const discoverer = new RoundRobinDiscoverer(registry)
assertEqual('inventory-a', discoverer.next('inventory-service').instanceId, 'first instance')
assertEqual('inventory-b', discoverer.next('inventory-service').instanceId, 'second instance')
assertEqual('inventory-a', discoverer.next('inventory-service').instanceId, 'should cycle')

assertEqual(true, registry.deregister('inventory-service', 'inventory-a'), 'deregister should succeed')
assertEqual(1, registry.instances('inventory-service').length, 'one instance should remain')

console.log('service-discovery(js) tests passed')
