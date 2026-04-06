export class CircuitBreaker {
  constructor(failureThreshold) {
    this.failureThreshold = failureThreshold
    this.failures = 0
    this.state = 'closed'
  }

  recordFailure() {
    if (this.state === 'closed') {
      this.failures++
      if (this.failures >= this.failureThreshold) {
        this.state = 'open'
      }
    }
  }

  probe(success) {
    if (this.state !== 'open') return
    this.state = 'half-open'
    if (success) {
      this.state = 'closed'
      this.failures = 0
    } else {
      this.state = 'open'
    }
  }
}
