/**
 * @file func.h - 限流模式（Rate Limiting）的 C 语言头文件
 *
 * 【设计模式】策略模式：固定窗口是一种限流策略。
 *
 * 【架构思想】限流保护系统不被过载流量拖垮。
 *   Nginx 的 limit_req 模块使用类似的漏桶/固定窗口算法。
 */

#ifndef RATE_LIMITING_C_FUNC_H
#define RATE_LIMITING_C_FUNC_H

/** FixedWindowLimiter - 固定窗口限流器 */
typedef struct {
    int limit;  /* 窗口内最大允许请求数 */
    int count;  /* 当前窗口已通过请求数 */
} FixedWindowLimiter;

/** 初始化限流器 */
void limiter_init(FixedWindowLimiter *limiter, int limit);
/** 判断是否允许通过，返回 1=放行，0=拒绝 */
int limiter_allow(FixedWindowLimiter *limiter);
/** 推进窗口，重置计数 */
void limiter_advance_window(FixedWindowLimiter *limiter);

#endif
