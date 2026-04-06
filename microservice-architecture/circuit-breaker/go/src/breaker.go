package src

type CircuitBreaker struct {
	state            string
	failures         int
	failureThreshold int
}

func NewCircuitBreaker(failureThreshold int) *CircuitBreaker {
	return &CircuitBreaker{state: "closed", failureThreshold: failureThreshold}
}

func (b *CircuitBreaker) RecordFailure() {
	if b.state == "closed" {
		b.failures++
		if b.failures >= b.failureThreshold {
			b.state = "open"
		}
	}
}

func (b *CircuitBreaker) Probe(success bool) {
	if b.state != "open" {
		return
	}
	b.state = "half-open"
	if success {
		b.state = "closed"
		b.failures = 0
	} else {
		b.state = "open"
	}
}

func (b *CircuitBreaker) State() string {
	return b.state
}
