#include "../src/func.h"

#include <assert.h>
#include <stdio.h>

int main(void)
{
    FixedWindowLimiter limiter;
    limiter_init(&limiter, 3);
    assert(limiter_allow(&limiter) == 1);
    assert(limiter_allow(&limiter) == 1);
    assert(limiter_allow(&limiter) == 1);
    assert(limiter_allow(&limiter) == 0);
    limiter_advance_window(&limiter);
    assert(limiter_allow(&limiter) == 1);
    printf("rate-limiting(c) tests passed\n");
    return 0;
}
