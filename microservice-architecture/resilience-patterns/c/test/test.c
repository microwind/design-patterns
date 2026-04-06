#include "../src/func.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>

int main(void)
{
    ScriptedResult retry_results[] = {
        {"", 1, 0},
        {"", 1, 0},
        {"OK", 0, 0}
    };
    ScriptedDependency retry_dependency;
    scripted_dependency_init(&retry_dependency, retry_results, 3);
    const char *retry_value = NULL;
    int attempts = 0;
    assert(retry_call(&retry_dependency, 3, &retry_value, &attempts) == 1);
    assert(strcmp(retry_value, "OK") == 0);
    assert(attempts == 3);

    ScriptedResult timeout_results[] = {
        {"SLOW_OK", 0, 100}
    };
    ScriptedDependency timeout_dependency;
    scripted_dependency_init(&timeout_dependency, timeout_results, 1);
    const char *timeout_value = NULL;
    assert(call_with_timeout(&timeout_dependency, 10, &timeout_value) == 0);

    ScriptedResult breaker_results[] = {
        {"", 1, 0},
        {"", 1, 0},
        {"RECOVERED", 0, 0}
    };
    ScriptedDependency breaker_dependency;
    scripted_dependency_init(&breaker_dependency, breaker_results, 3);
    CircuitBreaker breaker;
    circuit_breaker_init(&breaker, 2);
    const char *breaker_value = NULL;
    int circuit_open = 0;

    assert(circuit_breaker_execute(&breaker, &breaker_dependency, "FALLBACK", &breaker_value, &circuit_open) == 0);
    assert(strcmp(breaker_value, "FALLBACK") == 0);
    assert(circuit_breaker_execute(&breaker, &breaker_dependency, "FALLBACK", &breaker_value, &circuit_open) == 0);
    assert(strcmp(breaker_value, "FALLBACK") == 0);
    assert(circuit_breaker_execute(&breaker, &breaker_dependency, "FALLBACK", &breaker_value, &circuit_open) == 0);
    assert(circuit_open == 1);
    assert(strcmp(breaker_value, "FALLBACK") == 0);

    circuit_breaker_reset(&breaker);
    assert(circuit_breaker_execute(&breaker, &breaker_dependency, "FALLBACK", &breaker_value, &circuit_open) == 1);
    assert(strcmp(breaker_value, "RECOVERED") == 0);

    printf("resilience-patterns(c) tests passed\n");
    return 0;
}
