#include "../src/func.h"

#include <assert.h>
#include <stdio.h>

int main(void)
{
    ScriptedOperation op1;
    int attempts = 0;
    operation_init(&op1, 2);
    assert(retry_run(3, &op1, &attempts) == 1);
    assert(attempts == 3);

    ScriptedOperation op2;
    operation_init(&op2, 5);
    assert(retry_run(3, &op2, &attempts) == 0);
    assert(attempts == 3);

    printf("retry-pattern(c) tests passed\n");
    return 0;
}
