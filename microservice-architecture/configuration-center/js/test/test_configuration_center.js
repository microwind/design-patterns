import { ConfigCenter, ConfigClient } from '../src/configuration_center.js'

function assertEqual(expected, actual, message) {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`)
  }
}

const center = new ConfigCenter()
center.put({
  serviceName: 'order-service',
  environment: 'prod',
  version: 1,
  dbHost: 'db.prod.internal',
  timeoutMs: 300,
  featureOrderAudit: false
})

const client = new ConfigClient(center, 'order-service', 'prod')
const loaded = client.load()
assertEqual(1, loaded.version, 'initial version')
assertEqual(300, loaded.timeoutMs, 'initial timeout')

center.put({
  serviceName: 'order-service',
  environment: 'prod',
  version: 2,
  dbHost: 'db.prod.internal',
  timeoutMs: 500,
  featureOrderAudit: true
})

const refreshed = client.refresh()
assertEqual(2, refreshed.version, 'refreshed version')
assertEqual(true, refreshed.featureOrderAudit, 'feature flag')

console.log('configuration-center(js) tests passed')
