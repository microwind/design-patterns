import {
  callWithTimeout,
  CircuitBreaker,
  CircuitOpenError,
  OperationTimeoutError,
  retry,
  ScriptedDependency
} from '../src/resilience.js'

function assertEqual(expected, actual, message) {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`)
  }
}

const retryDependency = new ScriptedDependency([
  { error: new Error('temporary failure') },
  { error: new Error('temporary failure') },
  { value: 'OK' }
])
const retryResult = await retry(3, () => retryDependency.call())
assertEqual('OK', retryResult.value, 'retry result')
assertEqual(3, retryResult.attempts, 'retry attempts')

const timeoutDependency = new ScriptedDependency([{ value: 'SLOW_OK', delayMs: 100 }])
try {
  await callWithTimeout(10, () => timeoutDependency.call())
  throw new Error('timeout should have failed')
} catch (error) {
  if (!(error instanceof OperationTimeoutError)) {
    throw error
  }
}

const breaker = new CircuitBreaker(2)
const breakerDependency = new ScriptedDependency([
  { error: new Error('dependency down') },
  { error: new Error('dependency still down') },
  { value: 'RECOVERED' }
])
assertEqual('FALLBACK', await breaker.execute(() => breakerDependency.call(), 'FALLBACK'), 'first fallback')
assertEqual('FALLBACK', await breaker.execute(() => breakerDependency.call(), 'FALLBACK'), 'second fallback')

try {
  await breaker.execute(() => breakerDependency.call(), 'FALLBACK')
  throw new Error('circuit should be open')
} catch (error) {
  if (!(error instanceof CircuitOpenError)) {
    throw error
  }
}

breaker.reset()
assertEqual('RECOVERED', await breaker.execute(() => breakerDependency.call(), 'FALLBACK'), 'recovery')

console.log('resilience-patterns(js) tests passed')
