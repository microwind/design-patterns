import { FeatureFlagService } from '../src/feature_flag.js'

function assertEqual(expected, actual, message) {
  if (expected !== actual) throw new Error(`${message} expected=${expected} actual=${actual}`)
}

const service = new FeatureFlagService()
service.set('new-checkout', { defaultEnabled: false, allowlist: { 'user-1': true } })
assertEqual(true, service.enabled('new-checkout', 'user-1'), 'allowlist')
assertEqual(false, service.enabled('new-checkout', 'user-2'), 'default disabled')
console.log('feature-flag(js) tests passed')
