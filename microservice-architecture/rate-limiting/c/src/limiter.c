#include "func.h"

void limiter_init(FixedWindowLimiter *limiter, int limit)
{
    limiter->limit = limit;
    limiter->count = 0;
}

int limiter_allow(FixedWindowLimiter *limiter)
{
    if (limiter->count >= limiter->limit) {
        return 0;
    }
    limiter->count++;
    return 1;
}

void limiter_advance_window(FixedWindowLimiter *limiter)
{
    limiter->count = 0;
}
