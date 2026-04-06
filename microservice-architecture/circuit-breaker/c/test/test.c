#include "../src/func.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>

int main(void)
{
    CircuitBreaker breaker;
    breaker_init(&breaker, 2);
    assert(strcmp(breaker.state, "closed") == 0);
    breaker_record_failure(&breaker);
    breaker_record_failure(&breaker);
    assert(strcmp(breaker.state, "open") == 0);
    breaker_probe(&breaker, 1);
    assert(strcmp(breaker.state, "closed") == 0);
    breaker_record_failure(&breaker);
    breaker_record_failure(&breaker);
    breaker_probe(&breaker, 0);
    assert(strcmp(breaker.state, "open") == 0);
    printf("circuit-breaker(c) tests passed\n");
    return 0;
}
