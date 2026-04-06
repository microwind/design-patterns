import { productHandlerV1, productHandlerV2, VersionedRouter } from '../src/router.js'

function assertEqual(expected, actual, message) {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`)
  }
}

const router = new VersionedRouter('v1')
router.register('v1', productHandlerV1)
router.register('v2', productHandlerV2)

assertEqual('v1', router.handle({ path: '/products/P100', headers: {} }).version, 'default version')
assertEqual(
  'v2',
  router.handle({ path: '/products/P100', headers: { 'X-API-Version': '2' } }).version,
  'header selected version'
)
assertEqual(
  400,
  router.handle({ path: '/products/P100', headers: { 'X-API-Version': '9' } }).statusCode,
  'unsupported version'
)

console.log('api-versioning(js) tests passed')
