/**
 * @file breaker.c - 断路器模式（Circuit Breaker Pattern）的 C 语言实现
 *
 * 【设计模式】
 *   - 状态模式（State Pattern）：通过 state 字符串字段和 strcmp 条件分支实现状态机。
 *     C 语言没有类和多态，因此用结构体 + 条件分支模拟状态模式的行为切换。
 *   - 代理模式（Proxy Pattern）：断路器函数包裹在真实服务调用之外。
 *
 * 【架构思想】
 *   断路器防止调用方在下游故障时持续重试导致级联雪崩，通过探测机制实现自动恢复。
 *
 * 【开源对比】
 *   - Envoy（C++）：在 Service Mesh sidecar 中内置断路器，支持最大连接数、
 *     最大请求数、最大重试次数等多维度熔断。
 *   - Nginx：通过 upstream 健康检查和 max_fails 参数实现类似功能。
 *   本示例是纯 C 实现的状态机骨架，展示核心逻辑。
 */

#include "func.h"

#include <string.h>

/**
 * 初始化断路器。
 * 设置失败阈值，清零失败计数，初始状态为 "closed"。
 */
void breaker_init(CircuitBreaker *breaker, int failure_threshold)
{
    breaker->failure_threshold = failure_threshold;
    breaker->failures = 0;
    strcpy(breaker->state, "closed");
}

/**
 * 记录一次失败调用。
 * 仅在 closed 状态下生效：累加失败计数，达到阈值时切换到 open。
 */
void breaker_record_failure(CircuitBreaker *breaker)
{
    /* 只有闭合状态下才统计失败 */
    if (strcmp(breaker->state, "closed") == 0) {
        breaker->failures++;
        /* 失败次数达到阈值，打开断路器 */
        if (breaker->failures >= breaker->failure_threshold) {
            strcpy(breaker->state, "open");
        }
    }
}

/**
 * 在 open 状态下进行一次探测调用。
 * 先转为 half-open，再根据 success 参数决定恢复 closed 或回退 open。
 */
void breaker_probe(CircuitBreaker *breaker, int success)
{
    /* 非 open 状态下不需要探测 */
    if (strcmp(breaker->state, "open") != 0) {
        return;
    }
    /* 进入半开状态，允许一次试探调用 */
    strcpy(breaker->state, "half-open");
    if (success) {
        /* 探测成功，恢复正常 */
        strcpy(breaker->state, "closed");
        breaker->failures = 0;
    } else {
        /* 探测失败，重新熔断 */
        strcpy(breaker->state, "open");
    }
}
