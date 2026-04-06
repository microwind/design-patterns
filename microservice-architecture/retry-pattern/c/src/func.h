#ifndef RETRY_PATTERN_C_FUNC_H
#define RETRY_PATTERN_C_FUNC_H

typedef struct {
    int failures_before_success;
    int attempts;
} ScriptedOperation;

void operation_init(ScriptedOperation *operation, int failures_before_success);
int operation_call(ScriptedOperation *operation);
int retry_run(int max_attempts, ScriptedOperation *operation, int *attempts_out);

#endif
