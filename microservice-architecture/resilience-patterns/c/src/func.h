#ifndef RESILIENCE_PATTERNS_C_FUNC_H
#define RESILIENCE_PATTERNS_C_FUNC_H

typedef struct {
    const char *value;
    int fail;
    int delay_ms;
} ScriptedResult;

typedef struct {
    ScriptedResult results[8];
    int count;
    int index;
} ScriptedDependency;

typedef struct {
    int failure_threshold;
    int consecutive_failures;
    int open;
} CircuitBreaker;

void scripted_dependency_init(ScriptedDependency *dependency, const ScriptedResult *results, int count);
int call_with_timeout(ScriptedDependency *dependency, int timeout_ms, const char **value_out);
int retry_call(ScriptedDependency *dependency, int max_attempts, const char **value_out, int *attempts_out);
void circuit_breaker_init(CircuitBreaker *breaker, int failure_threshold);
int circuit_breaker_execute(CircuitBreaker *breaker, ScriptedDependency *dependency, const char *fallback, const char **value_out, int *circuit_open);
void circuit_breaker_reset(CircuitBreaker *breaker);

#endif
