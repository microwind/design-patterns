import { CircuitBreaker } from '../src/breaker.js'

function assertEqual(expected, actual, message) {
  if (expected !== actual) throw new Error(`${message} expected=${expected} actual=${actual}`)
}

const breaker = new CircuitBreaker(2)
assertEqual('closed', breaker.state, 'initial')
breaker.recordFailure()
breaker.recordFailure()
assertEqual('open', breaker.state, 'open')
breaker.probe(true)
assertEqual('closed', breaker.state, 'close')
breaker.recordFailure()
breaker.recordFailure()
breaker.probe(false)
assertEqual('open', breaker.state, 'reopen')
console.log('circuit-breaker(js) tests passed')
