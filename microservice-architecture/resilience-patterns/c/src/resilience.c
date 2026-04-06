#include "func.h"

#include <stddef.h>

void scripted_dependency_init(ScriptedDependency *dependency, const ScriptedResult *results, int count)
{
    dependency->count = count;
    dependency->index = 0;
    for (int i = 0; i < count; i++) {
        dependency->results[i] = results[i];
    }
}

static int scripted_call(ScriptedDependency *dependency, const char **value_out, int *delay_ms_out)
{
    int index = dependency->index < dependency->count ? dependency->index : dependency->count - 1;
    ScriptedResult result = dependency->results[index];
    dependency->index++;
    *delay_ms_out = result.delay_ms;
    *value_out = result.value;
    return result.fail ? 0 : 1;
}

int call_with_timeout(ScriptedDependency *dependency, int timeout_ms, const char **value_out)
{
    int delay_ms = 0;
    const char *value = NULL;
    int ok = scripted_call(dependency, &value, &delay_ms);
    if (delay_ms > timeout_ms) {
        return 0;
    }
    if (!ok) {
        return 0;
    }
    *value_out = value;
    return 1;
}

int retry_call(ScriptedDependency *dependency, int max_attempts, const char **value_out, int *attempts_out)
{
    for (int attempt = 1; attempt <= max_attempts; attempt++) {
        int delay_ms = 0;
        const char *value = NULL;
        if (scripted_call(dependency, &value, &delay_ms)) {
            *value_out = value;
            *attempts_out = attempt;
            return 1;
        }
    }
    *attempts_out = max_attempts;
    return 0;
}

void circuit_breaker_init(CircuitBreaker *breaker, int failure_threshold)
{
    breaker->failure_threshold = failure_threshold > 0 ? failure_threshold : 1;
    breaker->consecutive_failures = 0;
    breaker->open = 0;
}

int circuit_breaker_execute(CircuitBreaker *breaker, ScriptedDependency *dependency, const char *fallback, const char **value_out, int *circuit_open)
{
    if (breaker->open) {
        *value_out = fallback;
        *circuit_open = 1;
        return 0;
    }

    int delay_ms = 0;
    const char *value = NULL;
    if (scripted_call(dependency, &value, &delay_ms)) {
        breaker->consecutive_failures = 0;
        *value_out = value;
        *circuit_open = 0;
        return 1;
    }

    breaker->consecutive_failures++;
    if (breaker->consecutive_failures >= breaker->failure_threshold) {
        breaker->open = 1;
    }
    *value_out = fallback;
    *circuit_open = 0;
    return 0;
}

void circuit_breaker_reset(CircuitBreaker *breaker)
{
    breaker->consecutive_failures = 0;
    breaker->open = 0;
}
