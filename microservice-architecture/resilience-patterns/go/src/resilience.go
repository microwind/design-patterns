package src

import (
	"errors"
	"time"
)

var ErrTimeout = errors.New("operation timed out")
var ErrCircuitOpen = errors.New("circuit breaker is open")

type Result struct {
	Value string
	Err   error
	Delay time.Duration
}

type ScriptedDependency struct {
	results []Result
	index   int
}

func NewScriptedDependency(results []Result) *ScriptedDependency {
	return &ScriptedDependency{results: results}
}

func (d *ScriptedDependency) Call() (string, error) {
	if len(d.results) == 0 {
		return "", errors.New("no scripted result available")
	}

	index := d.index
	if index >= len(d.results) {
		index = len(d.results) - 1
	}
	result := d.results[index]
	d.index++

	if result.Delay > 0 {
		time.Sleep(result.Delay)
	}

	return result.Value, result.Err
}

func CallWithTimeout(timeout time.Duration, operation func() (string, error)) (string, error) {
	type response struct {
		value string
		err   error
	}

	done := make(chan response, 1)
	go func() {
		value, err := operation()
		done <- response{value: value, err: err}
	}()

	select {
	case result := <-done:
		return result.value, result.err
	case <-time.After(timeout):
		return "", ErrTimeout
	}
}

func Retry(maxAttempts int, operation func() (string, error)) (string, int, error) {
	if maxAttempts <= 0 {
		maxAttempts = 1
	}

	var lastErr error
	for attempt := 1; attempt <= maxAttempts; attempt++ {
		value, err := operation()
		if err == nil {
			return value, attempt, nil
		}
		lastErr = err
	}

	return "", maxAttempts, lastErr
}

type CircuitBreaker struct {
	failureThreshold    int
	consecutiveFailures int
	open                bool
}

func NewCircuitBreaker(failureThreshold int) *CircuitBreaker {
	if failureThreshold <= 0 {
		failureThreshold = 1
	}
	return &CircuitBreaker{failureThreshold: failureThreshold}
}

func (c *CircuitBreaker) Execute(operation func() (string, error), fallback string) (string, error) {
	if c.open {
		return fallback, ErrCircuitOpen
	}

	value, err := operation()
	if err != nil {
		c.consecutiveFailures++
		if c.consecutiveFailures >= c.failureThreshold {
			c.open = true
		}
		return fallback, err
	}

	c.consecutiveFailures = 0
	return value, nil
}

func (c *CircuitBreaker) Reset() {
	c.open = false
	c.consecutiveFailures = 0
}
