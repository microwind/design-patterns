#ifndef CIRCUIT_BREAKER_C_FUNC_H
#define CIRCUIT_BREAKER_C_FUNC_H

typedef struct {
    int failure_threshold;
    int failures;
    char state[16];
} CircuitBreaker;

void breaker_init(CircuitBreaker *breaker, int failure_threshold);
void breaker_record_failure(CircuitBreaker *breaker);
void breaker_probe(CircuitBreaker *breaker, int success);

#endif
