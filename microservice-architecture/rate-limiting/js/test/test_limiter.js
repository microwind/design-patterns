import { FixedWindowLimiter } from '../src/limiter.js'

function assertEqual(expected, actual, message) {
  if (expected !== actual) throw new Error(`${message} expected=${expected} actual=${actual}`)
}

const limiter = new FixedWindowLimiter(3)
assertEqual(true, limiter.allow(), 'first')
assertEqual(true, limiter.allow(), 'second')
assertEqual(true, limiter.allow(), 'third')
assertEqual(false, limiter.allow(), 'fourth')
limiter.advanceWindow()
assertEqual(true, limiter.allow(), 'after reset')
console.log('rate-limiting(js) tests passed')
