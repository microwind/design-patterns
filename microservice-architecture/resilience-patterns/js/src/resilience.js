export class OperationTimeoutError extends Error {}
export class CircuitOpenError extends Error {}

export class ScriptedDependency {
  constructor(results) {
    this.results = results
    this.index = 0
  }

  async call() {
    if (this.results.length === 0) {
      throw new Error('no scripted result available')
    }

    const current = this.results[Math.min(this.index, this.results.length - 1)]
    this.index++

    if (current.delayMs && current.delayMs > 0) {
      await new Promise((resolve) => setTimeout(resolve, current.delayMs))
    }
    if (current.error) {
      throw current.error
    }
    return current.value || ''
  }
}

export function callWithTimeout(timeoutMs, operation) {
  const timeout = new Promise((_, reject) => {
    setTimeout(() => reject(new OperationTimeoutError('operation timed out')), timeoutMs)
  })
  return Promise.race([operation(), timeout])
}

export async function retry(maxAttempts, operation) {
  const attemptsLimit = Math.max(1, maxAttempts)
  let lastError = null

  for (let attempt = 1; attempt <= attemptsLimit; attempt++) {
    try {
      return { value: await operation(), attempts: attempt }
    } catch (error) {
      lastError = error
    }
  }

  throw lastError
}

export class CircuitBreaker {
  constructor(failureThreshold) {
    this.failureThreshold = Math.max(1, failureThreshold)
    this.consecutiveFailures = 0
    this.open = false
  }

  async execute(operation, fallback) {
    if (this.open) {
      throw new CircuitOpenError(fallback)
    }

    try {
      const value = await operation()
      this.consecutiveFailures = 0
      return value
    } catch {
      this.consecutiveFailures++
      if (this.consecutiveFailures >= this.failureThreshold) {
        this.open = true
      }
      return fallback
    }
  }

  reset() {
    this.consecutiveFailures = 0
    this.open = false
  }
}
