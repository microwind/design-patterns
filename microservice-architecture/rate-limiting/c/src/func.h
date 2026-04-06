#ifndef RATE_LIMITING_C_FUNC_H
#define RATE_LIMITING_C_FUNC_H

typedef struct {
    int limit;
    int count;
} FixedWindowLimiter;

void limiter_init(FixedWindowLimiter *limiter, int limit);
int limiter_allow(FixedWindowLimiter *limiter);
void limiter_advance_window(FixedWindowLimiter *limiter);

#endif
