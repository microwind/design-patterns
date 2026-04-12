/**
 * @file limiter.c - 限流模式（Rate Limiting）的 C 语言实现
 *
 * 【设计模式】策略模式：固定窗口是一种限流策略。
 *
 * 【架构思想】
 *   C 语言中限流通常嵌入在网络框架中。Nginx 的 limit_req 模块
 *   使用漏桶算法实现请求限流。
 *
 * 【开源对比】
 *   - Nginx limit_req：漏桶算法 + burst 突发允许
 *   - Envoy：支持本地限流和全局限流（ratelimit service）
 *   本示例实现最简单的固定窗口。
 */

#include "func.h"

/** 初始化限流器 */
void limiter_init(FixedWindowLimiter *limiter, int limit)
{
    limiter->limit = limit;
    limiter->count = 0;
}

/**
 * 判断是否允许通过。
 * count < limit 时放行并递增，否则拒绝。
 * @return 1=放行，0=拒绝
 */
int limiter_allow(FixedWindowLimiter *limiter)
{
    /* 达到上限，拒绝 */
    if (limiter->count >= limiter->limit) {
        return 0;
    }
    /* 放行并递增 */
    limiter->count++;
    return 1;
}

/** 推进窗口，重置计数 */
void limiter_advance_window(FixedWindowLimiter *limiter)
{
    limiter->count = 0;
}
