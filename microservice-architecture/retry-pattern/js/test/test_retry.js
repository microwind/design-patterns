import { retry, ScriptedOperation } from '../src/retry.js'

function assertEqual(expected, actual, message) {
  if (expected !== actual) throw new Error(`${message} expected=${expected} actual=${actual}`)
}

const op1 = new ScriptedOperation(2)
const success = retry(3, () => op1.call())
assertEqual(true, success.ok, 'success')
assertEqual(3, success.attempts, 'attempts')

const op2 = new ScriptedOperation(5)
const failure = retry(3, () => op2.call())
assertEqual(false, failure.ok, 'failure')
assertEqual(3, failure.attempts, 'max attempts')

console.log('retry-pattern(js) tests passed')
