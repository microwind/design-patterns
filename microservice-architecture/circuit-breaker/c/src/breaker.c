#include "func.h"

#include <string.h>

void breaker_init(CircuitBreaker *breaker, int failure_threshold)
{
    breaker->failure_threshold = failure_threshold;
    breaker->failures = 0;
    strcpy(breaker->state, "closed");
}

void breaker_record_failure(CircuitBreaker *breaker)
{
    if (strcmp(breaker->state, "closed") == 0) {
        breaker->failures++;
        if (breaker->failures >= breaker->failure_threshold) {
            strcpy(breaker->state, "open");
        }
    }
}

void breaker_probe(CircuitBreaker *breaker, int success)
{
    if (strcmp(breaker->state, "open") != 0) {
        return;
    }
    strcpy(breaker->state, "half-open");
    if (success) {
        strcpy(breaker->state, "closed");
        breaker->failures = 0;
    } else {
        strcpy(breaker->state, "open");
    }
}
