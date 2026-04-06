#include "func.h"

void operation_init(ScriptedOperation *operation, int failures_before_success)
{
    operation->failures_before_success = failures_before_success;
    operation->attempts = 0;
}

int operation_call(ScriptedOperation *operation)
{
    operation->attempts++;
    return operation->attempts > operation->failures_before_success;
}

int retry_run(int max_attempts, ScriptedOperation *operation, int *attempts_out)
{
    for (int attempt = 1; attempt <= max_attempts; attempt++) {
        if (operation_call(operation)) {
            *attempts_out = attempt;
            return 1;
        }
    }
    *attempts_out = max_attempts;
    return 0;
}
